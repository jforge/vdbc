# vdbc
Automatically exported from http://code.google.com/p/vdbc

Project site of the original author is located on http://bitlama.github.io/vdbc/


VDBC is yet another JDBC database manager web-application using the Vaadin framework (https://vaadin.com/).

# Changes in this fork

- Changed JRE requirement to Java 1.8+
- Added simple custom jndi authorization mechanism to enable or disable access to connection profiles.
- Added simple jndi names to build jdbc connection profiles (instead of providing a datasource directly).

# Remarks

Master integrates the changes of the mitobit vdbc fork (https://github.com/eRfO/vdbc), but there are currently some issues concerning the aceeditor integration.

The branch "before-mitobit-ace" contains the original version forked from code google plus the above mentioned changes.

The simplified auth+jdbc jndi handling is used to restrict access to vdbc without JEE container/servlet standard approaches and should be replaced with them and with more a sophisticated authorization mechanism (OAuth2++) later.

