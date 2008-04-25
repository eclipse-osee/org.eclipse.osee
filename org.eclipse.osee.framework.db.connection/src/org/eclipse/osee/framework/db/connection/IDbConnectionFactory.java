/*
 * Created on Apr 23, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.db.connection;

/**
 * @author b1528444
 */
public interface IDbConnectionFactory {
   IConnection get(String driver);
}
