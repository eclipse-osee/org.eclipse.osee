/*
 * Created on May 2, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.connection.service;

import java.util.Properties;

/**
 * @author b1529404
 */
public class LocalConnector implements IServiceConnector {
   public static final String TYPE = "local";
   private final Object service;
   private final Properties properties;

   /**
    * @param service
    * @param properties
    */
   public LocalConnector(Object service, Properties properties) {
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
    * @see org.eclipse.osee.connection.service.IServiceConnector#getProperty(java.lang.String, java.lang.String)
    */
   @Override
   public String getProperty(String property, String defaultValue) {
      return properties.getProperty(property, defaultValue);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.connection.service.IServiceConnector#export(java.lang.Object)
    */
   @Override
   public Object export(Object callback) throws Exception {
      return callback;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.connection.service.IServiceConnector#unexport(java.lang.Object)
    */
   @Override
   public void unexport(Object callback) throws Exception {
   }

}
