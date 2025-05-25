/**
 * Initialize the report reporting module.
 */
r.report = {};
/**
 * Reset report related context.
 */
r.report.reset = function () {
  $("#report-container").hide();
};

/**
 * Initializing report module.
 */
r.report.init = function () {
  $.History.bind("/report/", function (state, target) {
    console.log("here at report.js");
    // Reset page context
    r.main.reset();

    // Show report container
    $("#report-container").show();

    $("#toolbar > .about").removeClass("hidden");

    // Load report data from API
    r.report.loadData();
  });
};

/**
 * Load report data from API.
 */
r.report.loadData = function () {
  console.log("here at loadData");

  r.util.ajax({
    url: r.util.url.report,
    type: "GET",
    done: function (data) {
      console.log("the data: ", data);
      // Update the report container with the received data
      var converter = new showdown.Converter(),
      html = converter.makeHtml(data.report);
      
      $("#show-report-data").html(html);
      // // Show the report container if it's hidden
      // $('#report-container').show();
    },
    fail: function (jqXHR, textStatus, errorThrown) {
      console.log("Error details:", textStatus, errorThrown);
      console.log("Response:", jqXHR.responseText);
      console.log("Status:", jqXHR.status);
      alert($.t("report.error"));
    },
  });
};
