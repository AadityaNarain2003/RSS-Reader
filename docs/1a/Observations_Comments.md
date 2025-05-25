# Observations and Comments
When we are making class diagram we observed:
- there are wide hierarchies in the classes like BaseResource to starredResource, UserResource, ArticleResource, CategoryResource, SubscritionResource
- even with the dao objects also.BaseDao is extended by UserDao, Article Dao, JobDao, FeedSubscriptionDao, JobEventDao, UserArticleDao.
- Some classes like RssReader, FeedService etc has long methods and also responsibilities to the classes, because of this the fanin and fanout for these classes are high.
- We observe a interface starredarticleimportedlistner which has single method is never implemented but used in starredreader class.
- GuidFixer class which has method to complete the incomplete Guid, but there is a another method related to gid in the Rssreader which shows less cohesion between classes
- All the classes which are overriding queryparams method are not extending the queryparams class which is a error as queryparams is the user defined class

- FeedChooserStrategy does nothing valuable
