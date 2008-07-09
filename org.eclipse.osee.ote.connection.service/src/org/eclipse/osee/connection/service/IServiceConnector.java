/*
 * Created on May 1, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.connection.service;

import java.io.Serializable;

/**
 * Provides a communication pipe to a service.
 * 
 * @author Ken J. Aguilar
 */
public interface IServiceConnector {

   String getConnectorType();

   /**
    * gets the service provided by this connector
    * 
    * @return
    */
   Object getService();

   void setProperty(String key, Serializable value);

   Serializable getProperty(String property, Serializable defaultValue);

   void stop() throws Exception;

   /**
    * makes the callback accessible by this connector's service
    * 
    * @param callback
    * @return
    * @throws Exception
    */
   Object export(Object callback) throws Exception;

   void unexport(Object callback) throws Exception;

   void addPropertyChangeListener(IServicePropertyChangeListener listener);

   void removePropertyChangeListener(IServicePropertyChangeListener listener);
}
