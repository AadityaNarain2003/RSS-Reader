

// r.filter.js

r.filter = {};

/**
 * Render the filter panel dynamically.
 */
r.filter.render = function() {
    console.log('Rendering filter panel');


        // Create the filter panel markup once
    var panel = $(
        '<div id="filter-panel" style="display:none; position:absolute; top:60px; right:20px; z-index:9999; background:#fff; border:1px solid #ccc; padding:15px; box-shadow:0 2px 10px rgba(0,0,0,0.2); border-radius:4px; width:300px;">' +
            '<h3>Filter Feeds</h3>' +
            '<div class="filter-section">' +
                '<label for="filter-category-input">Category:</label>' +
                '<select id="filter-category-input" class="filter-select">' +
                    '<option value="">All Categories</option>' +
                '</select>' +
            '</div>' +
            '<div class="filter-section">' +
                '<label for="filter-subscription-input">Source:</label>' +
                '<select id="filter-subscription-input" class="filter-select">' +
                    '<option value="">All Sources</option>' +
                '</select>' +
            '</div>' +
            '<div class="filter-section">' +
                '<label for="filter-keyword-input">Keyword:</label>' +
                '<input type="text" id="filter-keyword-input" placeholder="Search in titles...">' +
            '</div>' +
            '<div class="filter-actions" style="margin-top:10px;">' +
                '<button id="apply-filter-button" class="important">Apply Filter</button> ' +
                '<button id="reset-filter-button">Reset</button>' +
            '</div>' +
            '<div class="close-button" style="position:absolute; top:5px; right:10px; cursor:pointer;">×</div>' +
        '</div>'
    );

    // Append panel once to the body
    $('body').append(panel);

    // Bind the close, apply, and reset button events
    $('#filter-panel .close-button').off('click').on('click', function() {
        $('#filter-panel').slideUp("fast");
    });
    $('#apply-filter-button').off('click').on('click', function() {
        r.filter.applyFilter();
    });
    $('#reset-filter-button').off('click').on('click', function() {
        r.filter.resetFilter();
    });

    // Inject styles for the panel if not already present
    if ($('#filter-panel-style').length === 0) {
        $('<style id="filter-panel-style">')
            .prop('type', 'text/css')
            .html('\
                #filter-panel .filter-section { margin-bottom: 15px; }\
                #filter-panel label { display: block; margin-bottom: 5px; font-weight: bold; }\
                #filter-panel select, #filter-panel input { width: 100%; padding: 5px; box-sizing: border-box; }\
                #filter-panel .filter-actions button { padding: 5px 10px; }\
            ')
            .appendTo('head');
    }
    

};

/**
 * Initialize the filter module.
 * Renders the panel if needed and binds the toggle event to the filter button.
 */
r.filter.init = function() {
    console.log('Initializing filter module');


    // Render the filter panel only once if it doesn't exist
    if ($('#filter-panel').length === 0) {
        r.filter.render();
    }

    // Use delegated event binding so that clicks on any .filter-button anywhere in the document are caught
    $(document).off('click', '.filter-button').on('click', '.filter-button', function(e) {
        e.preventDefault();
        console.log('Filter button clicked');
        if ($('#filter-panel').is(':visible')) {
            $('#filter-panel').slideUp("fast");
        } else {
            $('#filter-panel').slideDown("fast", function() {
                r.filter.loadCategories();
                r.filter.loadSubscriptions();
            });
        }
    });

};

/**
 * Load categories into the filter dropdown.
 */
r.filter.loadCategories = function() {
    console.log('Loading categories for filter');
    r.util.ajax({
        url: r.util.url.category_list,
        type: 'GET',
        done: function(data) {
            var categorySelect = $('#filter-category-input');
            // Remove all options except the first
            categorySelect.find('option:not(:first-child)').remove();

            // The response structure is { categories: { id: 'root', name: null, categories: [...] } }
            var rootCategory = data.categories; // Access the root category object directly

            // Helper function to recursively add categories to the select dropdown
            function addCategoriesRecursive(categories, level) {
                var prefix = Array(level * 2 + 1).join('-'); // Create indentation prefix
                if (level > 0) prefix += ' '; // Add space after prefix for non-root levels

                $.each(categories, function(i, category) {
                    if (category.id && category.name) { // Ensure category has ID and Name
                         categorySelect.append('<option value="' + category.id + '">' + prefix + r.util.escape(category.name) + '</option>');
                    }
                    // Recursively add subcategories
                    if (category.categories && category.categories.length > 0) {
                        addCategoriesRecursive(category.categories, level + 1);
                    }
                });
            }

            if (rootCategory && rootCategory.categories) {
                 // Start recursion from the root's subcategories (level 0)
                 addCategoriesRecursive(rootCategory.categories, 0);
            } else {
                console.warn('No categories received or root category structure is invalid.');
            }
        }
    });
};

/**
 * Load subscriptions into the filter dropdown.
 */
r.filter.loadSubscriptions = function() {
    console.log('Loading subscriptions for filter');
    r.util.ajax({
        url: r.util.url.subscription_list,
        type: 'GET',
        done: function(data) {
            var subscriptionSelect = $('#filter-subscription-input');
            subscriptionSelect.find('option:not(:first-child)').remove();
            
            // Recursively process categories for subscriptions
            function processCategory(category) {
                if (category.subscriptions) {
                    $.each(category.subscriptions, function(i, subscription) {
                        subscriptionSelect.append('<option value="' + subscription.id + '">' + r.util.escape(subscription.title) + '</option>');
                    });
                }
                if (category.categories) {
                    $.each(category.categories, function(i, subCategory) {
                        processCategory(subCategory);
                    });
                }
            }
            
            if (data.categories) {
                $.each(data.categories, function(i, rootCategory) {
                    processCategory(rootCategory);
                });
            } else {
                console.warn('No subscriptions received.');
            }
        }
    });
};

/**
 * Apply the selected filters.
 */
r.filter.applyFilter = function() {
    var categoryId = $('#filter-category-input').val();
    var subscriptionId = $('#filter-subscription-input').val();
    var keyword = $('#filter-keyword-input').val().trim();
    
    console.log('Applying filter - Category:', categoryId, 'Subscription:', subscriptionId, 'Keyword:', keyword);
    
    // Save filter state
    r.filter.currentFilters = {
        categoryId: categoryId,
        subscriptionId: subscriptionId,
        keyword: keyword
    };
    
    // Determine which feed URL to use based on selections
    var baseUrl;
    var params = {};
    if (subscriptionId) {
        baseUrl = '../api/subscription/' + subscriptionId;
        params.unread = r.feed.context.unread;
    } else if (categoryId) {
        baseUrl = '../api/category/' + categoryId;
        params.unread = r.feed.context.unread;
    } else {
        baseUrl = r.util.url.all;
        params.unread = r.feed.context.unread;
    }
    
    // Build the query string
    var queryParams = [];
    for (var key in params) {
        if (params[key] !== undefined && params[key] !== null && params[key] !== '') {
            queryParams.push(key + '=' + encodeURIComponent(params[key]));
        }
    }
    var url = baseUrl + (queryParams.length > 0 ? '?' + queryParams.join('&') : '');
    
    // Update feed context and reload the feed
    r.feed.context.url = url;
    r.feed.load(false);
    
    // Hide the filter panel and mark the filter button active
    $('#filter-panel').hide();
    $('.filter-button').addClass('active');
};

/**
 * Reset all filters.
 */
r.filter.resetFilter = function() {
    console.log('Resetting all filters');
    
    $('#filter-category-input').val('');
    $('#filter-subscription-input').val('');
    $('#filter-keyword-input').val('');
    
    r.filter.currentFilters = null;
    
    // Reset feed URL based on current view
    if (r.feed.context.subscriptionId) {
        r.feed.context.url = '../api/subscription/' + r.feed.context.subscriptionId;
    } else if (r.feed.context.categoryId) {
        r.feed.context.url = '../api/category/' + r.feed.context.categoryId;
    } else {
        r.feed.context.url = r.util.url.all;
    }
    
    r.feed.load(false);
    $('#filter-panel').hide();
    $('.filter-button').removeClass('active');
};

/**
 * Client-side filtering of articles based on keyword.
 */
r.filter.filterArticlesByKeyword = function(keyword) {
    if (!keyword || keyword.trim() === '') {
        // If the keyword is empty, show all articles.
        $('.feed-item').show();
        return;
    }
    keyword = keyword.toLowerCase();
    $('.feed-item').each(function() {
        // For debugging, print each article's text content.
        var title = $(this).find('.feed-item-title').text().toLowerCase();
        var description = $(this).find('.feed-item-description').text().toLowerCase();
        var subscription = $(this).find('.feed-item-subscription').text().toLowerCase();
        console.log('Filtering article:', {
            title: title,
            description: description,
            subscription: subscription
        });
        if (title.indexOf(keyword) >= 0 ||
            description.indexOf(keyword) >= 0 ||
            subscription.indexOf(keyword) >= 0) {
            $(this).show();
        } else {
            $(this).hide();
        }
    });
};

/**
 * Check if any filters are currently active.
 */
r.filter.isFilterActive = function() {
    return r.filter.currentFilters !== undefined && r.filter.currentFilters !== null;
};