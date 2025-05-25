// package com.sismics.reader.core.service;

// import com.sismics.reader.core.dao.file.html.RssExtractor;
// import com.sismics.reader.core.dao.file.newsapi.ContentExtractorAdapter;
// import com.sismics.reader.core.dao.file.rss.RssReader;
// import com.sismics.reader.core.model.jpa.Feed;
// import com.sismics.reader.core.util.http.ReaderHttpClient;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

// import java.io.InputStream;
// import java.net.URL;

// /**
//  * Modified methods for FeedService to integrate content extraction.
//  * 
//  * Note: Only showing the modified methods, not the entire class.
//  */
// public class FeedService {
//     /**
//      * Logger.
//      */
//     private static final Logger log = LoggerFactory.getLogger(FeedService.class);
    
//     /**
//      * Content extraction service.
//      */
//     private ContentExtractionService contentExtractionService = new ContentExtractionService();
    
//     /**
//      * Parse a page containing a RSS or Atom feed, or HTML linking to a feed,
//      * or use content extraction if no feed is available.
//      *
//      * @param url       Url to parse
//      * @param parsePage If true, try to parse the resource as an HTML page linking to a feed
//      * @return Reader
//      */
//     private RssReader parseFeedOrPage(String url, boolean parsePage) throws Exception {
//         try {
//             final RssReader reader = new RssReader();
//             new ReaderHttpClient() {
//                 @Override
//                 public Void process(InputStream is) throws Exception {
//                     reader.readRssFeed(is);
//                     return null;
//                 }
//             }.open(new URL(url));
//             reader.getFeed().setRssUrl(url);
//             return reader;
//         } catch (Exception eRss) {
//             try {
//                 // Try to extract feed from HTML page
//                 if (parsePage) {
//                     try {
//                         final RssExtractor extractor = new RssExtractor(url);
//                         new ReaderHttpClient() {
//                             @Override
//                             public Void process(InputStream is) throws Exception {
//                                 extractor.readPage(is);
//                                 return null;
//                             }
//                         }.open(new URL(url));
//                         List<String> feedList = extractor.getFeedList();
//                         if (feedList != null && !feedList.isEmpty()) {
//                             String feed = new FeedChooserStrategy().guess(feedList);
//                             return parseFeedOrPage(feed, false);
//                         }
//                     } catch (Exception ePage) {
//                         log.warn("Error parsing HTML page at URL {}: {}", url, ePage.getMessage());
//                     }
//                 }
                
//                 // No feed found, try content extraction with adapters
//                 log.info("No RSS feed found, trying content extraction for URL: {}", url);
//                 ContentExtractorAdapter.ExtractedContent extractedContent = contentExtractionService.extract(url);
                
//                 // Convert extracted content to RssReader format
//                 RssReader reader = new RssReader();
//                 reader.setFeed(extractedContent.getFeed());
//                 reader.setArticleList(extractedContent.getArticleList());
                
//                 return reader;
//             } catch (Exception eExtract) {
//                 // If content extraction also fails, throw the original RSS exception
//                 log.error("Content extraction failed for URL: {}", url, eExtract);
//                 throw eRss;
//             }
//         }
//     }
// }