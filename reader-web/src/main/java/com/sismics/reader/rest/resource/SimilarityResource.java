package com.sismics.reader.rest.resource;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

@Path("/similarity")
public class SimilarityResource extends BaseResource {
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response calculateSimilarity(
            @QueryParam("article1") String article1,
            @QueryParam("article2") String article2) throws JSONException {
        try {
            System.out.println("Received article1: '" + article1 + "'");
            System.out.println("Received article2: '" + article2 + "'");
            if (article1 == null || article2 == null || article1.trim().isEmpty() || article2.trim().isEmpty()) {
                JSONObject errorResponse = new JSONObject();
                errorResponse.put("status", "error");
                errorResponse.put("message", "Both articles must be provided and non-empty");
                return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse.toString()).build();
            }

            // Set up paths
            String currentDir = System.getProperty("user.dir");
            String basePath = "/home/aaditya/project-1-team-8/llm/"; // Adjust if needed for portability
            String pythonExecutable = "/usr/bin/python3"; // Use system Python
            String scriptPath = "ner.py";
            String fullScriptPath = basePath + scriptPath;

            System.out.println("Current Dir: " + currentDir);
            System.out.println("Base Path: " + basePath);
            System.out.println("Python Executable: " + pythonExecutable);
            System.out.println("Full Script Path: " + fullScriptPath);

            // Verify paths exist
            java.io.File pyFile = new java.io.File(pythonExecutable);
            java.io.File scriptFile = new java.io.File(fullScriptPath);
            if (!pyFile.exists()) {
                throw new IOException("Python executable not found at: " + pythonExecutable);
            }
            if (!scriptFile.exists()) {
                throw new IOException("Script not found at: " + fullScriptPath);
            }

            // Create ProcessBuilder
            ProcessBuilder processBuilder = new ProcessBuilder(pythonExecutable, fullScriptPath);
            processBuilder.redirectErrorStream(true);
            processBuilder.directory(new java.io.File(currentDir));

            // Start the process
            Process process = processBuilder.start();

            // Send articles to Python script via stdin
            try (OutputStreamWriter writer = new OutputStreamWriter(process.getOutputStream())) {
                JSONObject inputJson = new JSONObject();
                inputJson.put("article1", article1);
                inputJson.put("article2", article2);
                writer.write(inputJson.toString());
                writer.flush();
            }

            // Read the output
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }

            // Wait for process to complete
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                JSONObject errorResponse = new JSONObject();
                errorResponse.put("status", "error");
                errorResponse.put("message", "Python script failed with exit code: " + exitCode + ", Output: " + output.toString());
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorResponse.toString()).build();
            }

            // Parse the output
            JSONObject jsonResponse = new JSONObject();
            try {
                JSONObject pythonOutput = new JSONObject(output.toString().trim());
                double similarityScore = pythonOutput.getDouble("similarity_score");
                jsonResponse.put("status", "success");
                jsonResponse.put("similarity_score", similarityScore);
            } catch (JSONException e) {
                jsonResponse.put("status", "error");
                jsonResponse.put("message", "Failed to parse Python output: " + output.toString());
            }

            return Response.ok().entity(jsonResponse.toString()).build();

        } catch (Exception e) {
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Exception: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorResponse.toString()).build();
        }
    }
}