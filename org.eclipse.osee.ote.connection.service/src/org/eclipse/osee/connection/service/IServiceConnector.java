/*
 * Created on May 1, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.connection.service;

import java.io.File;
import java.io.Serializable;
import java.net.URI;
import java.rmi.server.ExportException;

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
   Object export(Object callback) throws ExportException;

   /**
	 * finds the matching exported representation of supplied object
	 * 
	 * @param callback
	 * @return
	 * @throws Exception
	 */
   Object findExport(Object callback);
   
   void unexport(Object callback) throws Exception;

   void addPropertyChangeListener(IServicePropertyChangeListener listener);

   void removePropertyChangeListener(IServicePropertyChangeListener listener);

   /**
    * uploads a file to a service and creates an {@link URI} that the service can access.
    * 
    * @param file
    * @return
    * @throws Exception
    */
   URI upload(File file) throws Exception;

   boolean ping();
}
