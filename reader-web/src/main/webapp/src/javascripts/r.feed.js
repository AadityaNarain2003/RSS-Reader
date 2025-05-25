/**
 * r.feed.js
 * 
 * This module handles the feed functionality (loading, scrolling, modes, etc.)
 * and also includes the curated feed features (creating, adding articles, viewing curated feeds).
 */

/**
 * Feed context.
 */
r.feed.context = {
  unread: true, // Unread state
  subscriptionId: null, // Subscription ID if relevant
  categoryId: null, // Category ID if relevant
  url: null, // API URL
  loading: false, // True if XHR in progress
  limit: function() { // Articles number to fetch each page
    return r.feed.cache.container.hasClass('list') ? 15 : 10;
  },
  lastItem: null, // Last article
  bumper: null, // Bumper element
  fullyLoaded: false, // True if all articles are loaded
  activeXhr: null // Active XHR retrieving a feed
};

/**
 * jQuery cache.
 */
r.feed.cache = {
  container: null,
  toolbar: null
};

/**
 * Reset feed context.
 */
r.feed.reset = function() {
  console.log('Resetting feed context.');
  r.feed.cache.container.hide();
  $('#subscriptions').find('li.active').removeClass('active');
};

/**
 * Initialize feed module.
 */
r.feed.init = function() {
  console.log('Initializing feed module.');
  r.feed.cache.container = $('#feed-container');
  r.feed.cache.toolbar = $('#toolbar');
  // Bind history for curated feed viewing
  $.History.bind('/feed/curated', function() {
    r.feed.loadCuratedFeeds();
  });

  // Or you could bind a click handler directly if needed:
  $('#curated-feeds-button').on('click', function() {
    r.feed.loadCuratedFeeds();
  });

  // Bind history changes for /feed/*
  $.History.bind('/feed/', function(state, target) {
    console.log('History change for /feed/ target:', target);
    r.main.reset();
    
    // Showing articles container
    r.feed.cache.container.show();
    r.feed.cache.toolbar.find('> .feed').removeClass('hidden');
    r.feed.context.subscriptionId = null;
    r.feed.context.categoryId = null;
    
    if (target === 'all') {
      r.feed.context.url = r.util.url.all;
      r.feed.context.unread = false;
      $('#all-feed-button').addClass('active');
      r.feed.cache.toolbar.find('> .all').removeClass('hidden');
    } else if (target === 'unread') {
      r.feed.context.url = r.util.url.all;
      r.feed.context.unread = true;
      $('#unread-feed-button').addClass('active');
      r.feed.cache.toolbar.find('> .unread').removeClass('hidden');
    } else if (target === 'starred') {
      r.feed.context.url = r.util.url.starred;
      r.feed.context.unread = false;
      $('#starred-feed-button').addClass('active');
      r.feed.cache.toolbar.find('> .starred').removeClass('hidden');
    } else if (target.substring(0, 13) === 'subscription/') {
      r.feed.context.url = '../api/' + target;
      r.feed.context.subscriptionId = target.substring(13);
      r.feed.cache.toolbar.find('> .subscription').removeClass('hidden');
    } else if (target.substring(0, 9) === 'category/') {
      r.feed.context.url = '../api/' + target;
      r.feed.context.categoryId = target.substring(9);
      r.feed.cache.toolbar.find('> .category').removeClass('hidden');
    } else if (target.substring(0, 7) === 'search/') {
      r.feed.context.unread = false;
      r.feed.context.url = r.util.url.search.replace('{query}', target.substring(7));
      r.feed.cache.toolbar.find('> .search').removeClass('hidden');
    }
    
    r.feed.cache.container.focus();
    console.log('Feed context before loading articles:', r.feed.context);
    r.feed.load(false);
    r.subscription.update();
  });
  
  // Scroll listeners for desktop and smartphone
  $(window).scroll(r.feed.scroll);
  r.feed.cache.container.scroll(r.feed.scroll);
  
  // Toolbar actions
  r.feed.cache.toolbar.find('.refresh-button').click(function() {
    console.log('Refresh button clicked.');
    r.feed.load(false);
    r.subscription.update();
  });
  
  r.feed.cache.toolbar.find('.all-button').click(function() {
    r.feed.context.unread = !r.feed.context.unread;
    console.log('Toggled unread state:', r.feed.context.unread);
    $(this).html(r.feed.context.unread ? $.t('toolbar.showall') : $.t('toolbar.shownew'));
    r.feed.load(false);
  });
  
  r.feed.cache.toolbar.find('.all-read-button').click(function() {
    console.log('All-read button clicked.');
    r.feed.markAllRead();
  });
  
  r.feed.cache.toolbar.find('.list-button').click(function() {
    console.log('List mode button clicked.');
    r.user.setDisplayTitle(true);
    r.feed.updateMode(true);
  });
  
  r.feed.cache.toolbar.find('.full-button').click(function() {
    console.log('Full mode button clicked.');
    r.user.setDisplayTitle(false);
    r.feed.updateMode(true);
  });
  
  r.feed.cache.toolbar.find('.narrow-article').click(function() {
    console.log('Narrow article button clicked.');
    r.user.setNarrowArticle(!r.user.isNarrowArticle());
    r.feed.updateMode(true);
  });
  
  r.feed.cache.container.on('click', '.markread', function() {
    console.log('Mark read clicked.');
    r.article.read($(this).prevAll('.feed-item:not(.read)'), true);
  });
  
  r.feed.updateMode(false);

  // Bind history for curated feed viewing (if viewing a curated feed, the URL would match)
  $.History.bind('/feed/curated/(.*)', function(id) {
    console.log('History change for curated feed with id:', id);
    r.feed.loadCuratedFeed(id);
  });
  
  /* --- Curated Feed Creation & Article Association --- */
  
  // When a user clicks the "create curated feed" button
  $('.create-curated-button').click(function() {
    console.log('Create curated feed button (desktop) clicked.');
    var selectedCheckboxes = $('.article-select-checkbox:checked');
    if (selectedCheckboxes.length === 0) {
      console.warn('No articles selected for curated feed.');
      $().toastmessage('showWarningToast', $.t('curatedfeed.noarticles'));
      return;
    }
    var currentArticleId = selectedCheckboxes.first().closest('.feed-item').data('article-id');
    console.log('Setting article id on dialog:', currentArticleId);
    $('#create-curated-feed-dialog').data('article-id', currentArticleId);
    $('body').append('<div id="dialog-backdrop"></div>');
    $('#create-curated-feed-dialog').removeClass('hidden');
    $('#curated-feed-name').focus();
  });

  $('.Similarity-score-button').click(function() {
    console.log('Similarity score button (desktop) clicked.');
    var selectedCheckboxes = $('.article-select-checkbox:checked');
    
    if (selectedCheckboxes.length !== 2) {
        console.warn('Can only match 2 articles');
        var warningMessage = typeof $.t === 'function' ? $.t('Can only match 2 articles') : 'Can only match 2 articles';
        $().toastmessage('showWarningToast', warningMessage);
        return;
    }

    var $firstFeedItem = selectedCheckboxes.eq(0).closest('.feed-item');
    var firstArticleTitle = $firstFeedItem.find('.feed-item-title').text().trim();
    var firstArticleDescription = $firstFeedItem.find('.feed-item-description').text().trim();
    var article1 = firstArticleTitle + " " + firstArticleDescription;

    var $secondFeedItem = selectedCheckboxes.eq(1).closest('.feed-item');
    var secondArticleTitle = $secondFeedItem.find('.feed-item-title').text().trim();
    var secondArticleDescription = $secondFeedItem.find('.feed-item-description').text().trim();
    var article2 = secondArticleTitle + " " + secondArticleDescription;

    console.log('Article 1:', article1);
    console.log('Article 2:', article2);

    // Construct URL with query parameters
    var url = r.util.url.similarity_score + '?article1=' + encodeURIComponent(article1) + '&article2=' + encodeURIComponent(article2);

    $.ajax({
        url: url,
        method: 'POST',
        dataType: 'json',
        success: function(response) {
            console.log('API Response:', response);
            if (response.status === 'success') {
                $().toastmessage('showSuccessToast', 'Similarity Score: ' + response.similarity_score);
            } else {
                $().toastmessage('showErrorToast', 'Error: ' + response.message);
            }
        },
        error: function(xhr, status, error) {
            console.error('API Error:', status, error);
            $().toastmessage('showErrorToast', 'Failed to calculate similarity: ' + (xhr.responseJSON?.message || error));
        }
    });
});


  // Cancel button for the curated feed dialog
  $('#create-curated-feed-dialog .cancel-button').click(function() {
    console.log('Curated feed dialog cancel clicked.');
    $('#dialog-backdrop').remove();
    $('#create-curated-feed-dialog').addClass('hidden');
  });
  
  // Submit the curated feed creation form (which now sends feedName and articleId)
  $('#create-curated-feed-dialog form').submit(function(e) {
    e.preventDefault();
    console.log('Curated feed dialog form submitted.');
    
    var feedName = $('#curated-feed-name').val().trim();
    if (!feedName) {
      console.error('No curated feed name entered.');
      $().toastmessage('showErrorToast', 'Please enter a feed name.');
      return;
    }
    
    var articleId = $('#create-curated-feed-dialog').data('article-id');
    console.log('Retrieved articleId:', articleId);
    if (!articleId) {
      console.error('No article ID found for curated feed creation.');
      $().toastmessage('showErrorToast', 'No article selected for curated feed.');
      return;
    }
    
    console.log('Submitting curated feed with name:', feedName, 'for article:', articleId);
    r.util.ajax({
      url: r.util.url.curatedfeed_create,  // should be set to '../api/curated-feed/create'
      method: 'POST',
      contentType: 'application/json',
      data: JSON.stringify({ feedName: feedName, articleId: articleId }),
      done: function(data) {
        console.log('Curated feed created successfully:', data);
        $().toastmessage('showSuccessToast', 'Curated feed created successfully.');
        $('#dialog-backdrop').remove();
        $('#create-curated-feed-dialog').addClass('hidden');
        $('#curated-feed-name').val('');
        $('.article-select-checkbox').prop('checked', false);
        $('.create-curated-button').hide();
      },
      fail: function(error) {
        console.error('Error creating curated feed:', error);
        $().toastmessage('showErrorToast', 'Error creating curated feed.');
      }
    });
  });
  
  // When an article checkbox is toggled, show/hide the create button accordingly
  $('body').on('change', '.article-select-checkbox', function() {
    if ($('.article-select-checkbox:checked').length > 0) {
      console.log('Some articles selected, showing create button.');
      $('.create-curated-button').show();
    } else {
      console.log('No articles selected, hiding create button.');
      $('.create-curated-button').hide();
    }
  });
};

/**
 * Feed scroll listener.
 */
r.feed.scroll = function() {
  var scroll = r.main.mobile ? $(window).scrollTop() : r.feed.cache.container.scrollTop();
  var height = r.main.mobile ? $(window).height() / 2 : r.feed.cache.container.height() / 2;
  console.log('Feed scroll triggered. Scroll:', scroll, 'Half-height:', height);
  var feedItemList = r.feed.cache.container.find('.feed-item');
  var selected = null;
  var selectedTopAbs = 0;
  feedItemList.removeClass('selected').filter(function() {
    return r.article.top($(this), scroll) < height;
  }).each(function() {
    var topAbs = Math.abs($(this).data('top'));
    if (selected == null || selectedTopAbs > topAbs) {
      selected = $(this);
      selectedTopAbs = topAbs;
    }
  });
  if (selected != null) {
    selected.addClass('selected');
    var itemsToRead = feedItemList.slice(0, selected.index('.feed-item') + 1);
    if (r.feed.cache.container.hasClass('list')) {
      itemsToRead = itemsToRead.filter('.unfolded');
    }
    itemsToRead = itemsToRead.not('.read, .forceunread');
    itemsToRead.each(function() {
      r.article.read($(this), true);
    });
  }
  r.feed.triggerPaging();
  r.feed.optimize();
};

/**
 * Load articles according to the feed context.
 */
r.feed.load = function(next) {
  console.log("Loading feed", r.feed.context.url);
  if (r.feed.context.loading) {
    if (next) {
      console.log('Already loading articles, skipping next load.');
      return;
    } else if (r.feed.activeXhr) {
      console.log('Aborting active XHR.');
      r.feed.activeXhr.abort();
    }
  }
  if (!next) {
    r.feed.cache.container.html(r.util.buildLoader());
    r.feed.context.unread 
      ? r.feed.cache.toolbar.find('.all-button').html($.t('toolbar.showall')) 
      : r.feed.cache.toolbar.find('.all-button').html($.t('toolbar.shownew'));
    r.feed.context.fullyLoaded = false;
    r.feed.context.lastItem = null;
  }
  if (r.feed.context.fullyLoaded) {
    console.log('All articles fully loaded. Skipping load.');
    return;
  }
  r.feed.context.loading = true;
  if (r.feed.context.bumper != null) {
    r.feed.context.bumper.find('.loader').show();
  }
  var data = {
    unread: r.feed.context.unread,
    limit: r.feed.context.limit()
  };
  if (r.feed.context.lastItem) {
    data.after_article = r.feed.context.lastItem.attr('data-article-id');
  }
  if (next && r.feed.context.url.substring(0, 11) == r.util.url.search.substring(0, 11)) {
    data.offset = r.feed.cache.container.find('.feed-item').length;
  }
  console.log('Feed load payload:', data);
  r.feed.activeXhr = r.util.ajax({
    url: r.feed.context.url,
    type: 'GET',
    data: data,
    done: function(data) {
      var nbArticles = $(data.articles).length;
      console.log('Number of articles loaded:', nbArticles);
      if (!next) {
        r.feed.cache.container.html('');
        if (nbArticles === 0) {
          r.feed.cache.container.append(r.feed.buildEmpty());
        }
        var bumper = r.feed.buildBumper(data);
        r.feed.context.bumper = bumper;
        r.feed.cache.container.append(bumper);
      }
      r.feed.context.fullyLoaded = nbArticles === 0;
      $(data.articles).each(function(i, article) {
        article.subscription.title = r.util.escape(article.subscription.title);
        article.creator = r.util.escape(article.creator);
        var item = r.article.build(article);
        r.feed.context.bumper.before(item);
        if (i === nbArticles - 1) {
          r.feed.context.lastItem = item;
        }
      });

      // NEW: Apply keyword filter if one is set and not empty
      if (r.filter.currentFilters && r.filter.currentFilters.keyword && r.filter.currentFilters.keyword.trim() !== '') {
        console.log("Applying keyword filter:", r.filter.currentFilters.keyword);
        r.filter.filterArticlesByKeyword(r.filter.currentFilters.keyword);
      } else {
        // If keyword filter is blank, ensure all articles are visible.
        $('.feed-item').show();
      }
      if (r.feed.context.unread && nbArticles === r.feed.context.limit()) {
        r.feed.context.lastItem.after('<div class="markread"><a>' + $.t('feed.markread') + '</a></div>');
      }
      if (!next) {
        r.feed.scrollTop(0);
        r.feed.cache.container.trigger('focus').redraw();
      }
      console.log('Feed load completed.');
      r.feed.context.loading = false;
      r.feed.activeXhr = null;
      r.feed.context.bumper.find('.loader').hide();
      r.feed.context.bumper.find('.retry').hide();
      r.feed.triggerPaging();
    },
    fail: function(jqXHR, textStatus) {
      console.error('Feed load failed with status:', textStatus);
      r.feed.context.loading = false;
      r.feed.activeXhr = null;
      r.feed.cache.container.find('.loader').hide();
      if (textStatus !== 'abort') {
        r.feed.context.bumper.find('.retry').show();
        $().toastmessage('showErrorToast', $.t('error.feed'));
      }
    }
  });
};




/**
 * Load and display curated feeds.
 * This function calls the curated feed list endpoint,
 * then renders each article (that has curated feed info) into the feed container.
 */
r.feed.loadCuratedFeeds = function() {
  console.log('Loading curated feeds for current user.');
  
  // Hide other content containers and show feed container
  $('#feed-container, #settings-container, #about-container, #wizard-container').hide();
  $('#feed-container').show();
  
  // Optionally clear the container before loading new items
  r.feed.cache.container.empty();
  
  // Call the backend endpoint to fetch curated articles
  r.util.ajax({
      url: r.util.url.curatedfeed_list,  // should be set to '../api/curated-feed/list'
      type: 'GET',
      done: function(response) {
          console.log('Curated feed data received:', response);
          // Check if there are any articles returned
          if (response.articles && response.articles.length > 0) {
              // Iterate through each article and build the feed item
              $.each(response.articles, function(i, article) {
                  // You can modify this as needed to include additional details
                  var feedItem = r.article.build(article);
                  // Append each feed item to the feed container
                  r.feed.cache.container.append(feedItem);
              });
          } else {
              // If no curated articles found, display a friendly message
              r.feed.cache.container.html('<div class="empty">No curated feeds found.</div>');
          }
      },
      fail: function(error) {
          console.error('Error loading curated feeds:', error);
          r.feed.cache.container.html('<div class="empty">Error loading curated feeds.</div>');
      }
  });
};


/**
 * Trigger loading next articles if necessary.
 */
r.feed.triggerPaging = function() {
  if (r.feed.context.lastItem != null && r.feed.context.lastItem.visible(true) ||
      r.feed.context.bumper != null && r.feed.context.bumper.visible(true)) {
    console.log('Triggering paging: Last item or bumper visible, loading next articles.');
    r.feed.load(true);
  } else {
    console.log('Paging not triggered.');
  }
};

/**
 * Build empty placeholder according to feed context.
 */
r.feed.buildEmpty = function() {
  var message = $.t('feed.noarticle');
  if (r.feed.context.unread) {
    message = $.t('feed.nonewarticle');
  }
  console.log('Building empty placeholder with message:', message);
  var empty = $('<div class="empty">' + message + '<br /><img src="images/rssman.png" /></div>');
  $(empty).click(function() {
    $(this).addClass('bounce').one('webkitAnimationEnd mozAnimationEnd MSAnimationEnd oanimationend animationend', function() {
      $(this).removeClass('bounce');
    });
  });
  return empty;
};

/**
 * Build feed bumper according to feed context.
 */
r.feed.buildBumper = function(data) {
  var html = $.t('feed.nomorearticles');
  if (r.feed.context.unread && r.feed.context.url.substring(0, 11) != r.util.url.search.substring(0, 11)) {
    if (data.subscription) {
      html = $.t('feed.subscriptionnomoreunreadarticles', { subscription: data.subscription.title });
    } else {
      html = $.t('feed.nomoreunreadarticles');
    }
    html += '<br /><a href="#" class="showall">' + $.t('feed.showall') + '</a>';
    if (r.user.isDisplayTitle()) {
      html += '<br /><a href="#" class="markallread">' + $.t('feed.markallread') + '</a>';
    }
  }
  var bumper = $('<div class="bumper"><img class="loader" src="images/ajax-loader.gif" />' +
      '<br /><a href="#" class="retry" style="display: none;" data-i18n="feed.retry">Retry</a><br />' + html + '</div>');
  bumper.find('.showall').click(function() {
    r.feed.context.unread = false;
    r.feed.load(false);
    return false;
  });
  bumper.find('.retry').click(function() {
    r.feed.triggerPaging();
    $(this).hide();
    return false;
  });
  bumper.find('.markallread').click(function() {
    r.feed.markAllRead();
    return false;
  });
  bumper.css('height', ($(window).height() - 200) + 'px');
  console.log('Built bumper with height:', bumper.css('height'));
  return bumper;
};

/**
 * Optimize articles list by destructing article's DOM outside of the viewport.
 */
r.feed.optimize = $.debounce(350, function() {
  console.log('Optimizing feed articles...');
  if (r.feed.cache.container.hasClass('list')) {
    console.log('Optimization skipped (list mode).');
    return;
  }
  r.feed.cache.container.find('.feed-item:largelyoutside')
    .addClass('destroyed')
    .height(function() {
      return $(this).height();
    })
    .html('');
  r.feed.cache.container.find('.feed-item.destroyed:not(:largelyoutside)')
    .removeClass('destroyed')
    .height('auto')
    .replaceWith(function() {
      return r.article.build($(this).data('article'), $(this).attr('class'));
    });
});

/**
 * Scroll to top.
 */
r.feed.scrollTop = function(top, animate) {
  console.log('Scrolling to top:', top, 'Animate:', animate);
  if (animate) {
    $('body, html').animate({ scrollTop: top }, 200);
    r.feed.cache.container.animate({ scrollTop: top }, 200);
  } else {
    $('body, html').scrollTop(top);
    r.feed.cache.container.scrollTop(top);
  }
};

/**
 * Update feed mode according to user preference.
 */
r.feed.updateMode = function(reload) {
  console.log('Updating feed mode. Reload:', reload);
  var list = r.user.isDisplayTitle();
  var narrow = r.user.isNarrowArticle();
  var narrowBtn = r.feed.cache.toolbar.find('.narrow-article');
  
  if (list) {
    r.feed.cache.container.addClass('list');
    console.log('Set feed mode to list.');
  } else {
    r.feed.cache.container.removeClass('list');
    console.log('Set feed mode to full.');
  }
  
  if (narrow) {
    r.feed.cache.container.addClass('narrow');
    narrowBtn.find('img:first').removeClass('hidden');
    narrowBtn.find('img:last').addClass('hidden');
    console.log('Enabled narrow mode.');
  } else {
    r.feed.cache.container.removeClass('narrow');
    narrowBtn.find('img:first').addClass('hidden');
    narrowBtn.find('img:last').removeClass('hidden');
    console.log('Disabled narrow mode.');
  }
  if (reload) {
    console.log('Reloading feed after mode update.');
    r.feed.load(false);
  }
};

/**
 * Mark all as read.
 */
r.feed.markAllRead = function() {
  console.log('Marking all articles as read.');
  r.util.ajax({
    url: r.feed.context.url + '/read',
    type: 'POST',
    always: function() {
      console.log('Mark all as read completed.');
      r.feed.load(false);
      r.subscription.update();
    }
  });
};

/**
 * Load a curated feed.
 */
r.feed.loadCuratedFeed = function(id) {
  console.log('Loading curated feed with id:', id);
  $('#feed-container, #settings-container, #about-container, #wizard-container, #curated-feed-container').hide();
  $('#feed-container').show();
  $('.button').hide();
  $('.refresh-button, .settings-button, .narrow-article').show();
  r.util.ajax({
    url: r.util.url.curatedfeed_list,
    type: 'GET',
    done: function(data) {
      console.log('Curated feed data received:', data);
      $('#feed-container').empty();
      if (data.articles.length === 0) {
        $('#feed-container').append('<div class="empty" data-i18n="feed.empty">No articles</div>');
      } else {
        $.each(data.articles, function(i, article) {
          r.article.addArticle(article);
        });
      }
      $('#feed-container').append('<div class="bumper"><div class="loader">' + r.util.buildLoader() + '</div><div class="retry" style="display: none;" data-i18n="feed.retry">Retry</div></div>');
      console.log('Curated feed loaded successfully.');
    },
    fail: function(error) {
      console.error('Error loading curated feed:', error);
    }
  });
};
