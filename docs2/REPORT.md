# Report

## Task 1 User Registration

In this section, no design pattern was implemented. The primary change involved removing the security layer from the API that was previously accessible only to the admin for user creation.

Change 1: The security layer was removed, allowing users to register directly.
Change 2: A registration form was introduced on the homepage. A "Register" button was added, which redirects users to this form. Through this form, users can now register in the same manner as the admin previously did when creating accounts.

## Task 2 Filtering

This Section involed allowing users to filter articles by sources / categories or both. It also incorporates keyword search across document title and description. Module Pattern was used.The files modified are:

1. `index.html` - added a button to check for filter requests
2. `r.main.js` - Initialised r.filter.init
3. `r.filter.js` - r.filter.js implements the filtering logic for the application. 
                  -Rendering the filter panel with category, subscription, and keyword fields.
                  -Fetching category and subscription data from the server to populate dropdowns.
                  -Applying filters by constructing the appropriate feed URL and triggering a reload.
                  -Resetting filters back to the default.

## Task 3 Bug Reporting

- Server-Side Flow

1. User Interaction: User interacts with the web interface, triggering actions like creating, updating, or deleting bug reports.
2. REST API Request: The client-side JavaScript makes an HTTP request to one of the endpoints in `BugResource`.
3. Command Creation: Inside the `BugResource` class, a specific command object is created based on the action:
   - `CreateBugCommand` for creating a new bug report
   - `UpdateBugCommand` for updating an existing bug report
   - `DeleteBugCommand` for deleting a bug report
   - `GetBugReportsCommand` for retrieving bug reports
4. Command Execution: The command is passed to the `CommandInvoker`, which executes it.
5. Business Logic: The command encapsulates all the business logic needed to perform the action, including validation and data persistence.
6. Response Handling: The result of the command execution is used to create a response that's sent back to the client.

- Client-Side Flow

1. User Interaction: User performs an action in the UI (e.g., submitting a form, clicking a button).
2. Command Creation: Based on the action, a specific client-side command object is created.
3. Command Execution: The client-side command invoker executes the command, which makes the appropriate AJAX request to the server.
4. Response Handling: The command's promise is resolved/rejected, and callbacks are executed to update the UI accordingly.

### Benefits of This Implementation

1. Separation of Concerns: The Command pattern separates the request for an operation from its execution, making the code more modular and easier to maintain.
2. Encapsulation: Each command encapsulates all the data and logic needed to perform a specific operation, making the code more self-contained.
3. Flexibility: New commands can be added without modifying existing code, following the Open/Closed Principle.
4. Testability: Commands can be tested in isolation, making unit testing easier.
5. Extensibility: The pattern allows for easy addition of features like undo/redo functionality, logging, or transaction support.

## Task 4 Nested Categories

Have modified the existing code to include nested categories. The previous code allowed for only one level of categories. The new code allows for upto 5 levels of categories. The **composite design pattern** has been used to implement this. Each node of the subscription tree is a category. It can have children which are subscriptions or other categories. This allows us to reuse the same code.

The files modified are:

1. `CategoryDao.java` - modified to allow for setting a parent category
2. `CategoryResource.java` - modified the `category` endpoint to return nested categories
3. `SubscriptionResource.java` - modified the `subscription` endpoint to return the nested subscription tree
4. `r.subscription.js` - modified the UI to allow nested categories and limiting the depth to 5
5. `messages.en.js` - added the display strings for categories

## Task 5 Custom Feeds

### Task 5A Simulating RSS Feeds

here we should extend the code such that it also supports newsapi format feeds along with rsstype. And also it should convert the feeds to rssformat as well.
Here is the clear implementation steps in a very abstract way. For clear details please refer the following functions

1. FeedService.java - parseFeedOrPage - which is extended to include newsapi adapter.
2. ContentExtractionService.java - new file is created to support to choose the type of adapter.
3. ContentExtractorAdapter.java - new interface is created for the adapters to follow same pattern.
4. NewsApiAdapter.java - new class is created and implements the the adapter for newsapi urls and endpoints.
5. RssReader.java - added setFeed and setArticleList added to convert to rss type.

Here we should use Adapter and strategy pattern - As we are adapter pattern to convert the json format input and parsing the input and converting it rssfeed articles.
We should also use strategy pattern as we should implement different code or extraction mechanism to subscibe feeds. And also really the end user don't know any of these details. However when it sees the newsapi url it uses newsapi adapter , but when rss or html is uses different extraction mechanism.

### Task 5B User-Created Feeds

This is divided into two parts

i - user should be able to create their own feed.

ii - other user should be able to subscribe this feed.

for the second part again we use same thing as the above , I created CuratedFeedExtractorAdapter and implemted the ContentExtractorAdapter and implemented the code to get the articles in the feed.(Essentially reading from the database)

For the first part first We changed the index.html page to add the rectangle box on each diaplyed article of the feed.And also added the create curate feed button on the toolbar which open up the dialog box to allow user to enter the feed name and allow to create or update. It allows only one article to be added into the feed each time.

Then this is processed by the r.feed.js file which takes the name and userarticleid as the json payload and send a post request to the backend rest resource using the api defined in the r.util.js.

We created a resource in the rest folder named CuratedFeedResource.java which provides the endpoint to perform the operation to carry out on the fetched data from the T_USERARTICLE table.

We also modified the UserArticleDao.java , UserArticledto.java , UserArticleMapper.java , UserArticle.java to include the new column which stores the which curated feeds the corresponding article is added.

And also there is already a function known as synchroniseallfeeds in feedservice.java which synchronises all the feeds.

Overall We use _ADAPTER , STRATEGY_ patterns to include the 5a and 5b to the existing system.

## Task 6 LLM-Features

### Task 6A Daily Report

We used the approach of hierarchical summarisation, wherein we summarised the sub-categories and subscriptions under each category. They were concatenated to form the final summary of the category. We used **builder** pattern to generate the summary. This is because the summary consists of multiple sub-sections and the order of these sections is important. The builder pattern allows us to build the summary in a step-by-step manner.

We also used the **composite** pattern to recursively traverse the category tree and generate the summary. The python code has a `generate_summary_category()` method which generates the summary for a category. It calls itself recursively for each child category. This function uses the builder functions to generate the summary.

We have used the **strategy** pattern to allow for using different LLM models. The interface `SummaryStrategy` has a method `generate_summary()` which is implemented by the different LLM models. The `OpenAISummaryStrategy` implements the `generate_summary()` method using the OpenAI API.

We have added a new endpoint `/report` which generates the daily report. This endpoint starts the daily report generation process the first time it is called. It then returns the report when hit later.

The files modified are:

1. `ReportResource.java` - added the `/report` endpoint to run the python code
2. `messages.en.js` - added the display strings related to daily report generation
3. `r.util.js` - added the url for the `/report` endpoint
4. `r.report.js` - added the UI to display the daily report
5. `r.main.js` - include the `r.report.js` file
6. `index.html` - added the div container for displaying the daily report

### Task 6B Duplicate Detection

In this section, the **Strategy Pattern** was implemented. A Python-based solution was developed that applies the strategy pattern, and this implementation is invoked via the web API resources. The code runs as a separate process, which is system-dependent due to variations in Python environments across systems. However, since this code is intended for the server machine, this dependency is acceptable.

A button was added to the frontend that triggers this functionality.

The Strategy Pattern was chosen because most functions in this context are consistent across different LLMs, while the choice of the model itself can vary. By implementing the strategy pattern, we gain the flexibility to switch between different LLM strategies seamlessly, allowing dynamic adaptation to changes in model preferences or API configurations.

---

# Contributions:

5a and 5b - Sathvika

4 and 6a - Amey

1 and 6b - Aaditya

2 - Alex

3 - Yeshu
