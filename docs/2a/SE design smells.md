# SE design smells

## **Unnecessary Abstraction and deficient encapsulation**

1. from designite :  project-1-team-8,com.sismics.reader.constant,Constants,Unnecessary Abstraction
2. project-1-team-8,com.sismics.reader.constant,Constants,Deficient Encapsulation

Constant class is an example of unnecessary abstraction. To refactor it, we should prefer Enum over Constant classes.

1. Constants.java: class constant are public that mean it is also a deficient encapsulation, can be made private and use enums.
this was identified from sonarqube with this issue of the public contructor: Add a private constructor to hide the implicit public one. 


## **Broken Hierarchy**

This smell arises when a supertype and its subtype conceptually do not share an "IS-A" relationship resulting in broken substitutability.

the starredresource,articleresource,categoryresource,userresource,subscription resource, resource helper all extend base resource but they are never overriding the methods in the base resource. They are just using them which suggest that it should not be the inheritance.the relationship should be has-a not is-a(also supported by designate and class diagram.)

<!-- ## **Unnecessary Hierarchy**

This smell arises when the whole inheritance hierarchy is unnecessary, indicating that inheritance has been applied needlessly for the particular design context.
From Basedao, all other files with dao are extended but they do not use any Basedao methods overriding.Instead they override the queryparams method which is imported. -->


## **Broken Modularisation**
starredarticleimportedevent class should be merged with starredreader class as the class is never used anywhere except in the starredreader and it does not have any specific functionality as it is only used in it and it has only getter setter methods.

## **Broken Modularization**

1. AtomArticleUrlGuesserStrategy.java, AtomArticleCommentUrlGuesserStrategy.java, AtomUrlGuesserStrategy.java, can be merged since it is being used by only rssReader.java- to support this when we look at class diagram all of them have methods by same name and very same functionality and without article, they do not need comments guesser.(Unutilized abstraction given by the designite but we think it is broken modularisation as these classes should not be seperated.)

## **Insufficient Modularization**

1. RssReader.java: too many if else condition which could increase code complexity, testing, readability . It is backed by the smell found using sonarqube (Long Method,long class,god class) "Brain Method" was detected. Refactor it to reduce at least one of the following metrics: LOC from 68 to 64, Complexity from 57 to 14, Nesting Level from 3 to 2, Number of Variables from 9 to 6.

## **Insufficient Modularisation**
Observed in the feed service.java class with the methods extending more than 20 lines and has functions which are un related.

## **Imperative Abstraction && Unutilized Abstraction**
The interface "starredarticleimportedlistener" is used by starredreader to use it's method.But it is never extended or implemented by anyother class.

 

