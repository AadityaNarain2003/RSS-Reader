// package com.sismics.reader.rest.resource;

// import com.sismics.reader.core.dao.jpa.BugReportDao;
// import com.sismics.reader.core.model.jpa.BugReport;
// import com.sismics.rest.exception.ClientException;
// import org.codehaus.jettison.json.JSONException;
// import org.codehaus.jettison.json.JSONObject;
// import org.codehaus.jettison.json.JSONArray;

// import javax.ws.rs.*;
// import javax.ws.rs.core.MediaType;
// import javax.ws.rs.core.Response;
// import java.util.Date;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
// import java.util.UUID;

// /**
//  * Bug report REST resources.
//  * 
//  * @author Yeshu
//  */
// @Path("/bug")
// public class BugResource extends BaseResource {
//     /**
//      * Creates a bug report.
//      * 
//      * @param description  Description of the bug
//      * @param username     Username
//      * @param bugStatus    Status of the bug
//      * @param deleteStatus Delete status of the bug
//      * @return Response
//      * @throws JSONException
//      */
//     @POST
//     @Produces(MediaType.APPLICATION_JSON)
//     public Response create(
//             @FormParam("description") String description,
//             @FormParam("username") String username,
//             @FormParam("bugStatus") String bugStatus,
//             @FormParam("deleteStatus") String deleteStatus) throws JSONException {

//         if (description == null || description.isEmpty()) {
//             throw new ClientException("ValidationError", "Description is required");
//         }

//         if (username == null || username.isEmpty()) {
//             username = "anonymous";
//         }

//         try {
//             // Create the bug report
//             BugReport bugReport = new BugReport();

//             String randomId = UUID.randomUUID().toString();
//             bugReport.setId(randomId);
//             bugReport.setUsername(username);
//             bugReport.setDescription(description);
//             bugReport.setDate(new Date());
//             bugReport.setBugStatus(bugStatus != null ? bugStatus : "ongoing");
//             bugReport.setDeleteStatus(deleteStatus != null ? deleteStatus : "false");

//             BugReportDao bugReportDao = new BugReportDao();
//             bugReportDao.create(bugReport);

//             // Return the created bug report
//             JSONObject response = new JSONObject();
//             JSONObject bugObject = new JSONObject();
//             bugObject.put("id", bugReport.getId());
//             bugObject.put("description", bugReport.getDescription());
//             bugObject.put("date", bugReport.getDate().getTime());
//             bugObject.put("username", bugReport.getUsername());
//             bugObject.put("bugStatus", bugReport.getBugStatus());
//             bugObject.put("deleteStatus", bugReport.getDeleteStatus());

//             response.put("bug", bugObject);
//             response.put("status", "ok");

//             return Response.ok().entity(response).build();

//         } catch (Exception e) {
//             System.err.println("Error creating bug report: " + e.getMessage());
//             e.printStackTrace();

//             JSONObject errorResponse = new JSONObject();
//             errorResponse.put("status", "error");
//             errorResponse.put("message", "Failed to create bug report: " + e.getMessage());
//             return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
//                     .entity(errorResponse).build();
//         }
//     }

//     /**
//      * Returns all bug reports grouped by username.
//      * 
//      * @return Response
//      * @throws JSONException
//      */
//     @GET
//     @Produces(MediaType.APPLICATION_JSON)
//     public Response getAll(
//         @FormParam("username") String userUsername) throws JSONException {

//         System.out.println("here at get function: " + userUsername);
//         try {
//             BugReportDao bugReportDao = new BugReportDao();
//             List<BugReport> allBugs;
//             if(userUsername.toLowerCase() == "admin"){
//                 allBugs = bugReportDao.findAll();
//             }else{
//                 allBugs = bugReportDao.findByUsername(userUsername);
//             }

//             if (allBugs == null || allBugs.isEmpty()) {
//                 return Response.ok().entity(new JSONObject().put("message", "No bug reports found")).build();
//             }

//             // Group bugs by username
//             Map<String, JSONArray> bugsByUsername = new HashMap<>();

//             for (BugReport bug : allBugs) {
//                 String username = bug.getUsername();
//                 if (!bugsByUsername.containsKey(username)) {
//                     bugsByUsername.put(username, new JSONArray());
//                 }

//                 JSONObject bugObject = new JSONObject();
//                 bugObject.put("id", bug.getId());
//                 bugObject.put("description", bug.getDescription());
//                 bugObject.put("date", bug.getDate().getTime());
//                 bugObject.put("bugStatus", bug.getBugStatus());
//                 bugObject.put("deleteStatus", bug.getDeleteStatus());

//                 bugsByUsername.get(username).put(bugObject);
//             }

//             // Build the response
//             JSONObject response = new JSONObject();
//             response.put("status", "ok");

//             JSONObject usersObject = new JSONObject();
//             for (Map.Entry<String, JSONArray> entry : bugsByUsername.entrySet()) {
//                 usersObject.put(entry.getKey(), new JSONObject().put("bugs", entry.getValue()));
//             }

//             response.put("users", usersObject);

//             return Response.ok().entity(response).build();

//         } catch (Exception e) {
//             System.err.println("Error getting bug reports: " + e.getMessage());
//             e.printStackTrace();

//             JSONObject errorResponse = new JSONObject();
//             errorResponse.put("status", "error");
//             errorResponse.put("message", "Failed to get bug reports: " + e.getMessage());
//             return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
//                     .entity(errorResponse).build();
//         }
//     }

//     @PUT
//     @Produces(MediaType.APPLICATION_JSON)
//     public Response update(
//             @FormParam("id") String id,
//             @FormParam("description") String description,
//             @FormParam("username") String username,
//             @FormParam("bugStatus") String bugStatus,
//             @FormParam("deleteStatus") String deleteStatus) throws JSONException {
    
//         // Validate ID
//         if (id == null || id.isEmpty()) {
//             throw new ClientException("ValidationError", "ID is required");
//         }
    
//         try {
//             // Check if the bug report exists
//             BugReportDao bugReportDao = new BugReportDao();
//             BugReport existingBug = bugReportDao.findById(id);
    
//             if (existingBug == null) {
//                 JSONObject errorResponse = new JSONObject();
//                 errorResponse.put("status", "error");
//                 errorResponse.put("message", "Bug report not found with ID: " + id);
//                 return Response.status(Response.Status.NOT_FOUND).entity(errorResponse).build();
//             }
    
//             // If description is provided but empty, reject the update
//             if (description != null && description.isEmpty()) {
//                 throw new ClientException("ValidationError", "Description cannot be empty");
//             }
    
//             // Set default username to anonymous if null or empty (consistent with POST)
//             if (username != null && username.isEmpty()) {
//                 username = "anonymous";
//             }
    
//             // Create a map of fields to update
//             Map<String, Object> updateData = new HashMap<>();
    
//             if (description != null) {
//                 updateData.put("description", description);
//             }
    
//             if (username != null) {
//                 updateData.put("username", username);
//             }
    
//             if (bugStatus != null) {
//                 updateData.put("bugStatus", bugStatus);
//             }
    
//             if (deleteStatus != null) {
//                 updateData.put("deleteStatus", deleteStatus);
//             }
    
//             // Update the bug report
//             BugReport updatedBug = bugReportDao.updateById(id, updateData);
    
//             // Return the updated bug report
//             JSONObject response = new JSONObject();
//             JSONObject bugObject = new JSONObject();
//             bugObject.put("id", updatedBug.getId());
//             bugObject.put("description", updatedBug.getDescription());
//             bugObject.put("date", updatedBug.getDate().getTime());
//             bugObject.put("username", updatedBug.getUsername());
//             bugObject.put("bugStatus", updatedBug.getBugStatus());
//             bugObject.put("deleteStatus", updatedBug.getDeleteStatus());
    
//             response.put("bug", bugObject);
//             response.put("status", "ok");
    
//             return Response.ok().entity(response).build();
    
//         } catch (ClientException ce) {
//             // Handle client validation errors specifically
//             JSONObject errorResponse = new JSONObject();
//             errorResponse.put("status", "error");
//             errorResponse.put("message", ce.getMessage());
//             return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build();
//         } catch (Exception e) {
//             System.err.println("Error updating bug report: " + e.getMessage());
//             e.printStackTrace();
    
//             JSONObject errorResponse = new JSONObject();
//             errorResponse.put("status", "error");
//             errorResponse.put("message", "Failed to update bug report: " + e.getMessage());
//             return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
//                     .entity(errorResponse).build();
//         }
//     }

//     /**
//      * Deletes a bug report.
//      * 
//      * @param id ID of the bug report to delete
//      * @return Response
//      * @throws JSONException
//      */
//     @DELETE
//     @Path("/{id}")
//     @Produces(MediaType.APPLICATION_JSON)
//     public Response delete(@PathParam("id") String id) throws JSONException {
//         try {
//             // Check if the bug report exists
//             BugReportDao bugReportDao = new BugReportDao();
//             BugReport existingBug = bugReportDao.findById(id);

//             if (existingBug == null) {
//                 JSONObject errorResponse = new JSONObject();
//                 errorResponse.put("status", "error");
//                 errorResponse.put("message", "Bug report not found with ID: " + id);
//                 return Response.status(Response.Status.NOT_FOUND).entity(errorResponse).build();
//             }

//             Map<String, Object> updateData = new HashMap<>();
//             updateData.put("deleteStatus", "true");

//             // Update the bug report to mark it as deleted
//             bugReportDao.updateById(id, updateData);

//             // Return success response
//             JSONObject response = new JSONObject();
//             response.put("status", "ok");
//             response.put("message", "Bug report deleted successfully");

//             return Response.ok().entity(response).build();

//         } catch (Exception e) {
//             System.err.println("Error deleting bug report: " + e.getMessage());
//             e.printStackTrace();

//             JSONObject errorResponse = new JSONObject();
//             errorResponse.put("status", "error");
//             errorResponse.put("message", "Failed to delete bug report: " + e.getMessage());
//             return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
//                     .entity(errorResponse).build();
//         }
//     }
// }


package com.sismics.reader.rest.resource;

import com.sismics.reader.rest.resource.command.*;
import com.sismics.reader.core.model.jpa.BugReport;
import com.sismics.rest.exception.ClientException;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.codehaus.jettison.json.JSONArray;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Bug report REST resources.
 * 
 * @author Yeshu
 */
@Path("/bug")
public class BugResource extends BaseResource {
    private final CommandInvoker invoker = new CommandInvoker();
    
    /**
     * Creates a bug report.
     * 
     * @param description  Description of the bug
     * @param username     Username
     * @param bugStatus    Status of the bug
     * @param deleteStatus Delete status of the bug
     * @return Response
     * @throws JSONException
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(
            @FormParam("description") String description,
            @FormParam("username") String username,
            @FormParam("bugStatus") String bugStatus,
            @FormParam("deleteStatus") String deleteStatus) throws JSONException {

        try {
            // Create and execute the command
            CreateBugCommand command = new CreateBugCommand(description, username, bugStatus, deleteStatus);
            BugReport bugReport = (BugReport) invoker.executeCommand(command);

            // Return the created bug report
            JSONObject response = new JSONObject();
            JSONObject bugObject = new JSONObject();
            bugObject.put("id", bugReport.getId());
            bugObject.put("description", bugReport.getDescription());
            bugObject.put("date", bugReport.getDate().getTime());
            bugObject.put("username", bugReport.getUsername());
            bugObject.put("bugStatus", bugReport.getBugStatus());
            bugObject.put("deleteStatus", bugReport.getDeleteStatus());

            response.put("bug", bugObject);
            response.put("status", "ok");

            return Response.ok().entity(response).build();

        } catch (ClientException ce) {
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("status", "error");
            errorResponse.put("message", ce.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build();
        } catch (Exception e) {
            System.err.println("Error creating bug report: " + e.getMessage());
            e.printStackTrace();

            JSONObject errorResponse = new JSONObject();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Failed to create bug report: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorResponse).build();
        }
    }

    /**
     * Returns all bug reports grouped by username.
     * 
     * @return Response
     * @throws JSONException
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll(
        @QueryParam("username") String userUsername) throws JSONException {
        // System.out.println("here at get function: " + userUsername);
        try {
            boolean isAdmin = userUsername != null && userUsername.toLowerCase().equals("admin");
            // System.out.println("is Admin value:  " + isAdmin);

            GetBugReportsCommand command = new GetBugReportsCommand(userUsername, isAdmin);

            List<BugReport> allBugs = (List<BugReport>) invoker.executeCommand(command);
            // System.out.println("All bugs: " + allBugs);

            if (allBugs == null || allBugs.isEmpty()) {
                return Response.ok().entity(new JSONObject().put("message", "No bug reports found")).build();
            }

            // Group bugs by username
            Map<String, JSONArray> bugsByUsername = new HashMap<>();

            for (BugReport bug : allBugs) {
                String username = bug.getUsername();
                if (!bugsByUsername.containsKey(username)) {
                    bugsByUsername.put(username, new JSONArray());
                }

                JSONObject bugObject = new JSONObject();
                bugObject.put("id", bug.getId());
                bugObject.put("description", bug.getDescription());
                bugObject.put("date", bug.getDate().getTime());
                bugObject.put("bugStatus", bug.getBugStatus());
                bugObject.put("deleteStatus", bug.getDeleteStatus());

                bugsByUsername.get(username).put(bugObject);
            }

            // Build the response
            JSONObject response = new JSONObject();
            response.put("status", "ok");

            JSONObject usersObject = new JSONObject();
            for (Map.Entry<String, JSONArray> entry : bugsByUsername.entrySet()) {
                usersObject.put(entry.getKey(), new JSONObject().put("bugs", entry.getValue()));
            }

            response.put("users", usersObject);

            return Response.ok().entity(response).build();

        } catch (Exception e) {
            System.err.println("Error getting bug reports: " + e.getMessage());
            e.printStackTrace();

            JSONObject errorResponse = new JSONObject();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Failed to get bug reports: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorResponse).build();
        }
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(
            @FormParam("id") String id,
            @FormParam("description") String description,
            @FormParam("username") String username,
            @FormParam("bugStatus") String bugStatus,
            @FormParam("deleteStatus") String deleteStatus) throws JSONException {
    
        try {
            // Create and execute the command
            UpdateBugCommand command = new UpdateBugCommand(id, description, username, bugStatus, deleteStatus);
            BugReport updatedBug = (BugReport) invoker.executeCommand(command);
    
            // Return the updated bug report
            JSONObject response = new JSONObject();
            JSONObject bugObject = new JSONObject();
            bugObject.put("id", updatedBug.getId());
            bugObject.put("description", updatedBug.getDescription());
            bugObject.put("date", updatedBug.getDate().getTime());
            bugObject.put("username", updatedBug.getUsername());
            bugObject.put("bugStatus", updatedBug.getBugStatus());
            bugObject.put("deleteStatus", updatedBug.getDeleteStatus());
    
            response.put("bug", bugObject);
            response.put("status", "ok");
    
            return Response.ok().entity(response).build();
    
        } catch (ClientException ce) {
            // Handle client validation errors specifically
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("status", "error");
            errorResponse.put("message", ce.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build();
        } catch (Exception e) {
            System.err.println("Error updating bug report: " + e.getMessage());
            e.printStackTrace();
    
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Failed to update bug report: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorResponse).build();
        }
    }

    /**
     * Deletes a bug report.
     * 
     * @param id ID of the bug report to delete
     * @return Response
     * @throws JSONException
     */
    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@PathParam("id") String id) throws JSONException {
        try {
            // Create and execute the command
            DeleteBugCommand command = new DeleteBugCommand(id);
            Boolean result = (Boolean) invoker.executeCommand(command);

            // Return success response
            JSONObject response = new JSONObject();
            response.put("status", "ok");
            response.put("message", "Bug report deleted successfully");

            return Response.ok().entity(response).build();

        } catch (ClientException ce) {
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("status", "error");
            errorResponse.put("message", ce.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build();
        } catch (Exception e) {
            System.err.println("Error deleting bug report: " + e.getMessage());
            e.printStackTrace();

            JSONObject errorResponse = new JSONObject();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Failed to delete bug report: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorResponse).build();
        }
    }
}