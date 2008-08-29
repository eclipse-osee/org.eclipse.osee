/*
 * Created on May 2, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.connection.service;

import java.io.File;
import java.io.Serializable;
import java.net.URI;
import java.rmi.server.ExportException;
import java.util.HashSet;

import org.eclipse.osee.framework.jdk.core.util.EnhancedProperties;

/**
 * @author b1529404
 */
public class LocalConnector implements IServiceConnector {
   public static final String TYPE = "local";
   private final Object service;
   private final EnhancedProperties properties;

   private final HashSet<IServicePropertyChangeListener> propertyChangeListeners =
         new HashSet<IServicePropertyChangeListener>();

   /**
    * @param service
    * @param properties
    */
   public LocalConnector(Object service, EnhancedProperties properties) {
      this.service = service;
      this.properties = properties;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.connection.service.IServiceConnector#getService()
    */
   @Override
   public Object getService() {
      return service;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.connection.service.IServiceConnector#stop()
    */
   @Override
   public void stop() {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.connection.service.IServiceConnector#getType()
    */
   @Override
   public String getConnectorType() {
      return TYPE;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.connection.service.IServiceConnector#setProperty(java.lang.String, java.lang.String)
    */
   @Override
   public void setProperty(String key, Serializable value) {
      properties.setProperty(key, value);
      for (IServicePropertyChangeListener listener : propertyChangeListeners) {
         listener.propertyChanged(this, key, value);
      }

   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.connection.service.IServiceConnector#getProperty(java.lang.String, java.lang.String)
    */
   @Override
   public Serializable getProperty(String property, Serializable defaultValue) {
      return properties.getProperty(property, defaultValue);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.connection.service.IServiceConnector#export(java.lang.Object)
    */
   @Override
   public Object export(Object callback) throws ExportException {
      return callback;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.connection.service.IServiceConnector#unexport(java.lang.Object)
    */
   @Override
   public void unexport(Object callback) throws Exception {
   }
   

   @Override
   public Object findExport(Object callback) {
		return callback;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.osee.connection.service.IServiceConnector#
	 * addPropertyChangeListener
	 * (org.eclipse.osee.connection.service.IServicePropertyChangeListener)
	 */
   @Override
   public void addPropertyChangeListener(IServicePropertyChangeListener listener) {
      propertyChangeListeners.add(listener);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.connection.service.IServiceConnector#removePropertyChangeListener(org.eclipse.osee.connection.service.IServicePropertyChangeListener)
    */
   @Override
   public void removePropertyChangeListener(IServicePropertyChangeListener listener) {
      propertyChangeListeners.remove(listener);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.connection.service.IServiceConnector#upload(java.io.File)
    */
   @Override
   public URI upload(File file) throws Exception {
      return file.toURI();
   }

    @Override
    public boolean ping() {
	return true;
    }

}
