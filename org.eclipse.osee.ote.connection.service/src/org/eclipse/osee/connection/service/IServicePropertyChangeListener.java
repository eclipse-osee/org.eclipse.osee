/*
 * Created on Jul 1, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.connection.service;

import java.io.Serializable;

/**
 * @author b1529404
 */
public interface IServicePropertyChangeListener {
   void propertyChanged(IServiceConnector connector, String key, Serializable value);
}
