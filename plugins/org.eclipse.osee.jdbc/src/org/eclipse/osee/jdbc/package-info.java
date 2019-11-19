/**
 * The optional package import of the package for each JDBC driver's main driver class allows
 * JdbcConnectionFactoryManager to use DriverManager.getConnection to class load the driver without requiring all the
 * drivers to be presence in any one install. The default choice of which JDBC driver is used is controlled by the JSON
 * key jdbc.client.driver in the server-side OSGI JSON file referenced by the system property cm.config.uri
 */
package org.eclipse.osee.jdbc;