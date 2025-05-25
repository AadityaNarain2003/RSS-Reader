# Assumptions
- when adding the methods into the class:Removed the logger functions as it is not related to the functionality
- in the dao objects and dto objects removed the setter and getter methods as they do not clearly show the functionality and makes the diagram clumsy
- not considering the threadlocal context and dialectutil as they are something like implementation details 
- and also not considering the javax.persistence and java.util and ord package 
removing com.google.common.collect.Sets
- imports like the below shown are not included in the diagram
  - import org.mindrot.jbcrypt.BCrypt;
  - import org.codehaus.jackson.JsonNode;
  - import org.codehaus.jackson.map.ObjectMapper;
  - import org.codehaus.jackson.node.ArrayNode;
  - import org.codehaus.jackson.node.ObjectNode;
  - import org.slf4j.Logger;
  - import org.slf4j.LoggerFactory;
  - import java.io.InputStream;
  - import java.text.MessageFormat;
  - import java.util.Date;
- All the variables and methods in the some classes like article,feed etc are not shown when they are used by other classes to increase the abstraction
- some classes which have relationship with this class but only helps in the implementation not from the design perspective are omitted for a abstract view.

