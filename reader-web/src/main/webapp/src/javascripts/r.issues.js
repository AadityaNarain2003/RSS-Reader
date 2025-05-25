
/**
 * Initialize the issues reporting module.
 */
// r.issues = {};

// /**
//  * Reset issues related context.
//  */
// r.issues.reset = function() {
//   $('#issues-container').hide();
// };

// /**
//  * Initializing issues module.
//  */
// r.issues.init = function() {
//   $.History.bind('/issues/', function(state, target) {
//     // Reset page context
//     r.main.reset();
    
//     // Show issues container
//     $('#issues-container').show();
    
//     // Configure toolbar
//     $('#toolbar > .about').removeClass('hidden');
    
//     // Initialize form
//     r.issues.initForm();

    
//     if (r.user.hasBaseFunction('ADMIN')) {
//       r.issues.loadBugReports(false);
//     } else {
//       r.issues.loadBugReports(false);
//     }

//   });


//   // Bug reports infinite scrolling
//   $('#issues-container').scroll(function() {
//     if ($('#issues-table tr.bug-item:last').visible(true)) {
//       r.issues.loadBugReports(true);
//     }
//   });
  
//   // Reload bug reports on refresh button click
//   $('#bugs-refresh-button').click(function() {
//     r.issues.loadBugReports(false);
//   });

  

// };

// r.issues.initForm = function() {
//   var form = $('#issues-account-edit-form');
//   var descriptionInput = form.find(".issues-description-input");
  
//   // Initialize values
//   descriptionInput.val("");
  
//   // Submit button click handler
//   form.find('.issues-submit-button').click(function() {

//     // Get the bug description
//     var description = descriptionInput.val();
    
//     if (!description || description.trim() === '') {

//       $().toastmessage('showWarningToast', $.t('issues.submit.empty'));
//       return;
//     }

//     var username = r.user.userInfo.username;
//     var bugStatus = form.find('.bug-status-select').val() || 'ongoing';
//     var deleteStatus = form.find('.delete-status-select').val() || 'false';
    
//     // Submit the bug report using direct API endpoint
    
//     $.ajax({
//       url: r.util.url.bug_report_list,
//       type: 'POST',
//       data: {
//         description: description,
//         username: username,
//         bugStatus: bugStatus,
//         deleteStatus: deleteStatus
//       },
//       success: function(data) {
//         $().toastmessage('showSuccessToast', 'Bug report submitted successfully');
        
//         // Reset form
//         form[0].reset();
//       },
//       error: function(xhr, status, error) {
//         console.error("Bug report submission failed:", xhr.status, error);
//         $().toastmessage('showErrorToast', 'Failed to submit bug report');
//       }
//     });


//   });
// };


// /**
//  * Update a bug report.
//  */
// r.issues.updateBugReport = function(id, data, callback) {
//   r.util.ajax({
//     url: r.util.url.bug_report_update,
//     type: 'PUT',
//     data: $.extend({ id: id }, data),
//     done: function(response) {
//       $().toastmessage('showSuccessToast', $.t('issues.update.success'));
//       if (typeof callback === 'function') {
//         callback(response);
//       }
//     }
//   });
// };

// /**
//  * Load bug reports.
//  */
// r.issues.bugsLoading = false;
// r.issues.loadBugReports = function(isAdmin) {
//   // Stop if already loading something
//   if (r.issues.bugsLoading) {
//     return;
//   }

//   // Calling API
//   r.issues.bugsLoading = true;
//   r.util.ajax({
//     url: r.util.url.bug_report_list,
//     type: 'GET',
//     done: function(data) {
//       // Check if the response is valid
//       if (data.status !== "ok" || !data.users) {
//         $('#issues-table').html('<tr><td colspan="5" class="no-data">' + $.t('issues.table.error') + '</td></tr>');
//         r.issues.bugsLoading = false;
//         return;
//       }
      
//       // Building table rows
//       var html = '';
      
//       // Add table header
//       html += '<tr class="bug-header">'
//         + '<th class="date">' + $.t('issues.table.date') + '</th>'
//         + '<th class="description">' + $.t('issues.table.description') + '</th>'
//         if (r.user.userInfo.username === "admin") {
//           html += '<th class="user">' + $.t('issues.table.user') + '</th>'
//           + '<th class="status">' + $.t('issues.table.status') + '</th>'
//         }
//         html += '<th class="actions">' + $.t('issues.table.actions') + '</th>'
//       '</tr>';
              
//       // Process each user's bugs
//       var bugCount = 0;
//       $.each(data.users, function(username, userData) {
//         if (!userData.bugs || userData.bugs.length === 0) {
//           return; // Skip users with no bugs
//         }
        
//         // Add each bug report as a row
//         $.each(userData.bugs, function(index, bug) {
//           bugCount++;
          
//           // Format date
//           var date = new Date(bug.date);
//           var formattedDate = date.toLocaleString();
          
//           html += '<tr class="bug-item ' + (bug.deleteStatus === "true" ? 'deleted-row' : '') + '" data-bug-id="' + bug.id + '">'
//               + '<td class="date">' + formattedDate + '</td>'
//               + '<td class="description"><span class="editable-text">' + bug.description + '</span>'
//               + '<input type="text" class="edit-input" style="display:none;" value="' + bug.description + '"></td>';
//               if(r.user.userInfo.username === "admin"){
//                 html += '<td class="user">' + username + '</td>'
//                 + '<td class="status">' + r.issues.buildStatusDropdown(bug.bugStatus) + '</td>';
//               }
//               html += '<td class="actions">' + r.issues.buildActionButtons(bug.deleteStatus) + '</td>'
//               + '</tr>';
//         });
//       });
      
//       // If no bugs were found, display message
//       if (bugCount === 0) {
//         html += '<tr><td colspan="5" class="no-data">' + $.t('issues.table.nodata') + '</td></tr>';
//       }
      
//       // Update the table
//       $('#issues-table').html(html);
      
//       // Add event handlers for editable fields
//       r.issues.bindEditableFields();
      
//       // Add event handlers for status dropdown
//       r.issues.bindStatusDropdowns();
      
//       // Add event handlers for action buttons
//       r.issues.bindActionButtons();
      
//       // Add table styles
//       r.issues.addTableStyles();
//     },
//     always: function() {
//       r.issues.bugsLoading = false;
//     }
//   });
// };


/**
 * Initialize the issues reporting module.
 */
r.issues = {};

/**
 * Command objects for client-side commands
 */
r.issues.commands = {
    /**
     * Command to create a bug report
     */
    createBugReport: function(description, username, bugStatus, deleteStatus) {
        return {
            execute: function() {
                return $.ajax({
                    url: r.util.url.bug_report_list,
                    type: 'POST',
                    data: {
                        description: description,
                        username: username,
                        bugStatus: bugStatus,
                        deleteStatus: deleteStatus
                    }
                });
            }
        };
    },
    
    /**
     * Command to get bug reports
     */
    getBugReports: function(username) {
        return {
            execute: function() {
                return $.ajax({
                    url: r.util.url.bug_report_list,
                    type: 'GET',
                    data: {
                        username: username
                    }
                });
            }
        };
    },
    
    /**
     * Command to update a bug report
     */
    updateBugReport: function(id, data) {
        return {
            execute: function() {
                return $.ajax({
                    url: r.util.url.bug_report_list,
                    type: 'PUT',
                    data: $.extend({ id: id }, data)
                });
            }
        };
    },
    
    /**
     * Command to delete a bug report
     */
    deleteBugReport: function(id) {
        return {
            execute: function() {
                return $.ajax({
                    url: r.util.url.bug_report_list + '/' + id,
                    type: 'DELETE'
                });
            }
        };
    }
};

/**
 * Command invoker for client-side commands
 */
r.issues.commandInvoker = {
    executeCommand: function(command) {
        return command.execute();
    }
};

/**
 * Reset issues related context.
 */
r.issues.reset = function() {
  $('#issues-container').hide();
};

/**
 * Initializing issues module.
 */
r.issues.init = function() {
  $.History.bind('/issues/', function(state, target) {
    // Reset page context
    r.main.reset();
    
    // Show issues container
    $('#issues-container').show();
    
    // Configure toolbar
    $('#toolbar > .about').removeClass('hidden');
    
    // Initialize form
    r.issues.initForm();

    // Load bug reports
    if (r.user.hasBaseFunction('ADMIN')) {
      r.issues.loadBugReports(true);
    } else {
      r.issues.loadBugReports(false);
    }
  });

  // Bug reports infinite scrolling
  $('#issues-container').scroll(function() {
    if ($('#issues-table tr.bug-item:last').visible(true)) {
      r.issues.loadBugReports(false);
    }
  });
  
  // Reload bug reports on refresh button click
  $('#bugs-refresh-button').click(function() {
    r.issues.loadBugReports(false);
  });
};

r.issues.initForm = function() {
  var form = $('#issues-account-edit-form');
  var descriptionInput = form.find(".issues-description-input");
  
  // Initialize values
  descriptionInput.val("");
  
  // Submit button click handler
  form.find('.issues-submit-button').click(function() {
    // Get the bug description
    var description = descriptionInput.val();
    
    if (!description || description.trim() === '') {
      $().toastmessage('showWarningToast', $.t('issues.submit.empty'));
      return;
    }

    var username = r.user.userInfo.username;
    var bugStatus = form.find('.bug-status-select').val() || 'ongoing';
    var deleteStatus = form.find('.delete-status-select').val() || 'false';
    
    // Create and execute the command
    var command = r.issues.commands.createBugReport(description, username, bugStatus, deleteStatus);
    r.issues.commandInvoker.executeCommand(command)
      .done(function(data) {
        $().toastmessage('showSuccessToast', 'Bug report submitted successfully');
        
        // Reset form
        form[0].reset();
        
        // Reload bug reports
        r.issues.loadBugReports(false);
      })
      .fail(function(xhr, status, error) {
        console.error("Bug report submission failed:", xhr.status, error);
        $().toastmessage('showErrorToast', 'Failed to submit bug report');
      });
  });
};

/**
 * Update a bug report.
 */
r.issues.updateBugReport = function(id, data, callback) {
  var command = r.issues.commands.updateBugReport(id, data);
  r.issues.commandInvoker.executeCommand(command)
    .done(function(response) {
      $().toastmessage('showSuccessToast', $.t('issues.update.success'));
      if (typeof callback === 'function') {
        callback(response);
      }
    })
    .fail(function(xhr) {
      $().toastmessage('showErrorToast', $.t('issues.update.error'));
    });
};

/**
 * Load bug reports.
 */
r.issues.bugsLoading = false;
r.issues.loadBugReports = function(isAdmin) {
  // Stop if already loading something
  if (r.issues.bugsLoading) {
    return;
  }

  // Set loading flag
  r.issues.bugsLoading = true;
  
  // Get the username
  var username = r.user.userInfo.username;
  if (isAdmin) {
    username = "admin";
  }
  
  // Create and execute the command
  var command = r.issues.commands.getBugReports(username);
  r.issues.commandInvoker.executeCommand(command)
    .done(function(data) {
      // Check if the response is valid
      if (data.status !== "ok" || !data.users) {
        $('#issues-table').html('<tr><td colspan="5" class="no-data">' + $.t('issues.table.error') + '</td></tr>');
        r.issues.bugsLoading = false;
        return;
      }
      
      // Building table rows
      var html = '';
      
      // Add table header
      html += '<tr class="bug-header">'
        + '<th class="date">' + $.t('issues.table.date') + '</th>'
        + '<th class="description">' + $.t('issues.table.description') + '</th>';
        if (r.user.userInfo.username === "admin") {
          html += '<th class="user">' + $.t('issues.table.user') + '</th>'
          + '<th class="status">' + $.t('issues.table.status') + '</th>';
        }
        html += '<th class="actions">' + $.t('issues.table.actions') + '</th>'
      + '</tr>';
              
      // Process each user's bugs
      var bugCount = 0;
      $.each(data.users, function(username, userData) {
        if (!userData.bugs || userData.bugs.length === 0) {
          return; // Skip users with no bugs
        }
        
        // Add each bug report as a row
        $.each(userData.bugs, function(index, bug) {
          bugCount++;
          
          // Format date
          var date = new Date(bug.date);
          var formattedDate = date.toLocaleString();
          
          html += '<tr class="bug-item ' + (bug.deleteStatus === "true" ? 'deleted-row' : '') + '" data-bug-id="' + bug.id + '">'
              + '<td class="date">' + formattedDate + '</td>'
              + '<td class="description"><span class="editable-text">' + bug.description + '</span>'
              + '<input type="text" class="edit-input" style="display:none;" value="' + bug.description + '"></td>';
              if(r.user.userInfo.username === "admin"){
                html += '<td class="user">' + username + '</td>'
                + '<td class="status">' + r.issues.buildStatusDropdown(bug.bugStatus) + '</td>';
              }
              html += '<td class="actions">' + r.issues.buildActionButtons(bug.deleteStatus) + '</td>'
              + '</tr>';
        });
      });
      
      // If no bugs were found, display message
      if (bugCount === 0) {
        html += '<tr><td colspan="5" class="no-data">' + $.t('issues.table.nodata') + '</td></tr>';
      }
      
      // Update the table
      $('#issues-table').html(html);
      
      // Add event handlers for editable fields
      r.issues.bindEditableFields();
      
      // Add event handlers for status dropdown
      r.issues.bindStatusDropdowns();
      
      // Add event handlers for action buttons
      r.issues.bindActionButtons();
      
      // Add table styles
      r.issues.addTableStyles();
    })
    .fail(function(xhr, status, error) {
      console.error("Failed to load bug reports:", xhr.status, error);
      $('#issues-table').html('<tr><td colspan="5" class="no-data">' + $.t('issues.table.error') + '</td></tr>');
    })
    .always(function() {
      r.issues.bugsLoading = false;
    });
};

// Keep the rest of the code unchanged

/**
 * Build status dropdown HTML.
 */
r.issues.buildStatusDropdown = function(currentStatus) {
  var statuses = ['open', 'ongoing', 'resolved', 'closed'];
  var html = '<select class="status-dropdown">';
  
  $.each(statuses, function(i, status) {
    html += '<option value="' + status + '"' + (status === currentStatus ? ' selected' : '') + '>' + status + '</option>';
  });
  
  html += '</select>';
  return html;
};

/**
 * Build action buttons HTML.
 */
r.issues.buildActionButtons = function(deleteStatus) {
  var isDeleted = deleteStatus === "true";
  
  if (isDeleted) {
    return '<button class="delete-button restore-button"><i class="fa fa-trash"></i> ' + $.t('issues.table.restore') + '</button>';
  } else {
    return '<button class="delete-button"><i class="fa fa-trash"></i> ' + $.t('issues.table.delete') + '</button>';
  }
};


/**
 * Build status dropdown HTML.
 */
r.issues.buildStatusDropdown = function(currentStatus) {
  var statuses = ['open', 'ongoing', 'resolved', 'closed'];
  var html = '<select class="status-dropdown">';
  
  $.each(statuses, function(i, status) {
    html += '<option value="' + status + '"' + (status === currentStatus ? ' selected' : '') + '>' + status + '</option>';
  });
  
  html += '</select>';
  return html;
};

/**
 * Build action buttons HTML.
 */
r.issues.buildActionButtons = function(deleteStatus) {
  var isDeleted = deleteStatus === "true";
  
  if (isDeleted) {
    return '<button class="delete-button restore-button"><i class="fa fa-trash"></i> ' + $.t('issues.table.restore') + '</button>';
  } else {
    return '<button class="delete-button"><i class="fa fa-trash"></i> ' + $.t('issues.table.delete') + '</button>';
  }
};

/**
 * Update a bug report.
 */
r.issues.updateBugReport = function(id, data, callback) {
  $.ajax({
    url: r.util.url.bug_report_list,
    type: 'PUT',
    data: {
      id: id,
      description: data.description,
      username: data.username,
      bugStatus: data.bugStatus,
      deleteStatus: data.deleteStatus
    },
    success: function(response) {
      console.log("the response: ", response);
      $().toastmessage('showSuccessToast', $.t('issues.update.success'));
      if (typeof callback === 'function') {
        callback(response);
      }
    },
    error: function(xhr) {
      $().toastmessage('showErrorToast', $.t('issues.update.error'));
    }
  });
};

/**
 * Bind event handlers for editable fields.
 */
r.issues.bindEditableFields = function() {
  $('.editable-text').off('click').on('click', function() {
    $(this).hide();
    $(this).siblings('.edit-input').show().focus();
  });
  
  $('.edit-input').off('blur').on('blur', function() {
    var newValue = $(this).val();
    var originalValue = $(this).siblings('.editable-text').text();
    var bugId = $(this).closest('tr').data('bug-id');
    
    if (newValue !== originalValue) {
      r.issues.updateBugReport(bugId, { description: newValue });
      $(this).siblings('.editable-text').text(newValue);
    }
    
    $(this).hide();
    $(this).siblings('.editable-text').show();
  });
};

/**
 * Bind event handlers for status dropdowns.
 */
r.issues.bindStatusDropdowns = function() {
  $('.status-dropdown').off('change').on('change', function() {
    var newStatus = $(this).val();
    var bugId = $(this).closest('tr').data('bug-id');
    r.issues.updateBugReport(bugId, { bugStatus: newStatus });
  });
};

/**
 * Bind event handlers for action buttons.
 */
r.issues.bindActionButtons = function() {
  $('.delete-button').off('click').on('click', function() {
    var row = $(this).closest('tr');
    var bugId = row.data('bug-id');
    var isDeleted = $(this).hasClass('restore-button');
    var newDeleteStatus = isDeleted ? "false" : "true";
    
    r.issues.updateBugReport(bugId, { deleteStatus: newDeleteStatus }, function() {
      if (newDeleteStatus === "true") {
        $(this).html('<i class="fa fa-trash"></i> ' + $.t('issues.table.restore'));
        $(this).addClass('restore-button');
        row.addClass('deleted-row');
      } else {
        $(this).html('<i class="fa fa-trash"></i> ' + $.t('issues.table.delete'));
        $(this).removeClass('restore-button');
        row.removeClass('deleted-row');
      }
    }.bind(this));
  });
};

/**
 * Add table styles.
 */
r.issues.addTableStyles = function() {
  if (!$('#bug-reports-styles').length) {
    $('<style id="bug-reports-styles">')
      .text(`
        #issues-table {
          width: 100%;
          border-collapse: collapse;
          margin-top: 20px;
        }
        #issues-table th, #issues-table td {
          border: 1px solid #ddd;
          padding: 12px; /* Increased padding for more height */
          text-align: left;
        }
        #issues-table th {
          background-color: #f2f2f2;
          font-weight: bold;
        }
        #issues-table tr:nth-child(even) {
          background-color: #f9f9f9;
        }
        #issues-table tr:hover {
          background-color: #f1f1f1;
        }
        .deleted-row {
          background-color: #ffeeee !important;
          text-decoration: line-through;
          color: #999;
        }
        .deleted-row:hover {
          background-color: #ffdddd !important;
        }
        .delete-button {
          background: none;
          border: none;
          color: #ff4d4d;
          cursor: pointer;
          padding: 5px 10px;
          border-radius: 4px;
        }
        .delete-button:hover {
          background-color: #ffeeee;
        }
        .restore-button {
          color: #4CAF50;
        }
        .restore-button:hover {
          background-color: #eeffee;
        }
        .status-dropdown {
          padding: 5px;
          border-radius: 4px;
          border: 1px solid #ddd;
          width: 100%;
        }
        .editable-text {
          cursor: pointer;
          display: block;
          padding: 5px;
          border-radius: 4px;
        }
        .editable-text:hover {
          background-color: #f0f0f0;
        }
        .edit-input {
          width: 100%;
          padding: 5px;
          border: 1px solid #ddd;
          border-radius: 4px;
        }
        .loading, .no-data {
          text-align: center;
          padding: 20px;
          color: #666;
        }
        #bugs-refresh-button {
          float: right;
          cursor: pointer;
        }
        #bugs-refresh-button img {
          width: 16px;
          height: 16px;
        }
      `)
      .appendTo('head');
  }
};