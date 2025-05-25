/**
 * Application modules.
 */
var r = {
  main: {
    mobile: false // True if mobile context
  },
  user: {},
  subscription: {},
  category: {},
  feed: {},
  article: {},
  search: {},
  settings: {},
  about: {},
  wizard: {},
  theme: {},
  shortcuts: {},
  util: {},
  report: {},
  curated: {},
  util: {},
  filter: {},
  issues: {}
};

/**
 * Application entry point.
 */
$(document).ready(function() {
  r.main.mobile = $('#subscriptions-show-button').is(':visible');
  
  // Displaying login if necessary
  r.util.init();
  r.user.init();
  r.user.boot();
  r.user.switchToRegister();
  r.user.switchToLogin();
  
});

$(document).on("click", "#register-submit", function () {
  console.log("Button Clicked!");
  r.user.registerFormHandler();
  console.log("registration done");
});
/**
 * Modules initialization.
 */
r.main.initModules = function() {
  // Load modules together
  r.subscription.init();
  r.feed.init();
  r.category.init();
  r.article.init();
  r.search.init();
  r.settings.init();
  r.about.init();
  r.wizard.init();
  r.theme.init();
  r.shortcuts.init();
  r.issues.init();
  r.report.init();
  
  r.filter.init();
  console.log("HERE");
  console.log(r.user.hasBaseFunction('ADMIN'));
  console.log(r.user.userInfo.first_connection);
  // First page
  if (r.user.hasBaseFunction('ADMIN') && r.user.userInfo.first_connection) {
    
    window.location.hash = '#/wizard/';
  } else if (window.location.hash.length == 0) {
    window.location.hash = '#/feed/unread';
  }
};

/**
 * Reset current page context to show a new view.
 */
r.main.reset = function() {
  $('#toolbar > *').addClass('hidden');
  
  r.feed.reset();
  r.settings.reset();
  r.about.reset();
  r.issues.reset();
  r.wizard.reset();
  r.report.reset();
};