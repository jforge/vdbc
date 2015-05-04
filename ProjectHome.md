### Overview ###
This is yet another JDBC database manager. It should be deployable to any modern servlet container. Known to work in Apache Tomcat 7, Glassfish 3.1 and JBoss 7.

### General Features ###
  * Connect to a database via JDBC
  * Connect to JNDI datasources
  * Run SQL queries
  * Browse database objects
  * View database metadata information

### Recent Changes ###
  * Minor UI fixes

### Requirements ###
  * Servlet container (Servlet API 3.0+)
  * JRE 1.7+

### Downloads ###
  * Latest version (2014-11-15): [WAR](http://dl.bintray.com/ivnpav/generic/141115/vdbc.war) or [Apache Tomcat package](http://dl.bintray.com/ivnpav/generic/141115/vdbc-141115-apache-tomcat.zip)
  * Deprecated version (2013-08-04): [WAR](https://code.google.com/p/vdbc/downloads/detail?name=vdbc-130804.war&can=2&q=) or [Apache Tomcat package](https://code.google.com/p/vdbc/downloads/detail?name=vdbc-130804-apache-tomcat.zip&can=2&q=)

### FAQ ###

Q: _What is the current status of the project?_

A: It is work-in-progress. Stable builds will be regularly published.


Q: _Will you keep developing the project?_

A: Hopefully, yes. Anyway, you know what happens to the most of open-source projects: there's always risk of being abandoned.


Q: _How to run it?_

A: Download featured tomcat-vdbc bundle, unzip it and run startup.sh or startup.bat depending on what operating system you use. Also make sure you have set your environment properly:
  * variable `JAVA_HOME` must point to a JDK root directory if you have JDK installed
  * variable `JRE_HOME` must point to a JRE root directory if you have JRE installed
Then open http://localhost:8080/vdbc/ in your browser.


Q: _What database management systems are supported?_

A: A DBMS is supported if it provides JDBC-compliant driver. H2 driver is already packaged. Add your driver jars to $TOMCAT\_DIR/lib (in case of using featured tomcat package) or wherever your container expect these jars to be.


Q: _Where are the connection profiles stored?_

A: Connection profiles stored in the vdbc-settings.xml file in your home directory (~/.config/vdbc/vdbc-settings.xml). You are encouraged to edit connection profiles using bult-in settings manager.


Q: _How to disable settings editor?_

A: To disable settings editor you should provide a value for system property named "vdbc.settings.editor-enabled". E.g., for Tomcat you can add the following text somewhere in the beginning of $TOMCAT\_DIR/bin/catalina.sh:
```
CATALINA_OPTS=-Dvdbc.settings.editor-enabled=false
```


Q: _Have anybody ever asked you these questions?_

A: Actually, no, but it's nice to have a FAQ section.

### Screenshots ###
<table>
<blockquote><tbody>
<blockquote><tr>
<blockquote><td>
Selecting profile<br />
<a href='http://vdbc.googlecode.com/git/wiki/img/login.png'>
</blockquote></blockquote><img src='http://vdbc.googlecode.com/git/wiki/img/thumbs/login.png' />
</a>
<blockquote></td>
<td>
Configuring profiles<br />
<a href='http://vdbc.googlecode.com/git/wiki/img/profiles.png'>
</blockquote><img src='http://vdbc.googlecode.com/git/wiki/img/thumbs/profiles.png' />
</a>
<blockquote></td>
</blockquote><blockquote></tr>
<tr>
<blockquote><td>
Query<br />
<a href='http://vdbc.googlecode.com/git/wiki/img/query.png'>
</blockquote></blockquote><img src='http://vdbc.googlecode.com/git/wiki/img/thumbs/query.png' />
</a>
<blockquote></td>
<td>
Database Metadata view<br />
<a href='http://vdbc.googlecode.com/git/wiki/img/metadata.png'>
</blockquote><img src='http://vdbc.googlecode.com/git/wiki/img/thumbs/metadata.png' />
</a>
<blockquote></td>
</blockquote><blockquote></tr>
<tr>
<blockquote><td>
Table data<br />
<a href='http://vdbc.googlecode.com/git/wiki/img/table-data.png'>
</blockquote></blockquote><img src='http://vdbc.googlecode.com/git/wiki/img/thumbs/table-data.png' />
</a>
<blockquote></td>
<td>
Table structure<br />
<a href='http://vdbc.googlecode.com/git/wiki/img/table-structure.png'>
</blockquote><img src='http://vdbc.googlecode.com/git/wiki/img/thumbs/table-structure.png' />
</a>
<blockquote></td>
</blockquote><blockquote></tr>
</blockquote></tbody>
</table>