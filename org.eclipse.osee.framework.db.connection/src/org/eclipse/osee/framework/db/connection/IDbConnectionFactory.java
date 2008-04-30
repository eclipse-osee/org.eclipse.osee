/*
 * Created on Apr 23, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.db.connection;

/**
 * @author Andrew M. Finkbeiner
 */
public interface IDbConnectionFactory {
   IConnection get(String driver);
}
