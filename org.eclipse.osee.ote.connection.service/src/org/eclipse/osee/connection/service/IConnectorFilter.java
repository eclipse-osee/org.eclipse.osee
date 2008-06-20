/*
 * Created on May 12, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.connection.service;

/**
 * @author b1529404
 */
public interface IConnectorFilter {
   public boolean accept(IServiceConnector connector);
}
