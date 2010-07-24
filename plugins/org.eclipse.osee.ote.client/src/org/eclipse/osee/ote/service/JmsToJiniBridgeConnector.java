/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.service;

import java.io.File;
import java.io.Serializable;
import java.net.URI;
import java.net.UnknownHostException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.ExportException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import net.jini.export.Exporter;
import net.jini.jeri.BasicILFactory;
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.tcp.TcpServerEndpoint;
import org.eclipse.osee.connection.service.IServiceConnector;
import org.eclipse.osee.connection.service.IServicePropertyChangeListener;
import org.eclipse.osee.framework.jdk.core.util.EnhancedProperties;
import org.eclipse.osee.framework.jdk.core.util.Network;
import org.eclipse.osee.framework.plugin.core.util.ExportClassLoader;
import org.eclipse.osee.ote.core.environment.interfaces.IHostTestEnvironment;

/**
 * @author Andrew M. Finkbeiner
 */
public class JmsToJiniBridgeConnector implements IServiceConnector {
   public static final String TYPE = "jmstojini";
   private static final class ExportInfo {
      private final Exporter exporter;
      private final Object exportedObject;

      private ExportInfo(Exporter exporter, Object exportedObject) {
         this.exportedObject = exportedObject;
         this.exporter = exporter;
      }
   }

   private EnhancedProperties properties;
   private final HashMap<Object, ExportInfo> exports = new HashMap<Object, ExportInfo>();
   private ExportClassLoader exportClassLoader;
   private Object service;
   private List<IServicePropertyChangeListener> propertyChangeListeners = new CopyOnWriteArrayList<IServicePropertyChangeListener>();
   private String uniqueServerId;

  

   public JmsToJiniBridgeConnector(ExportClassLoader exportClassLoader, Object service, String id) {
      this.uniqueServerId = id;
      this.exportClassLoader = exportClassLoader;
      this.service = service;
      if (service instanceof IHostTestEnvironment) {
         try {
            this.properties = ((IHostTestEnvironment) service).getProperties();
         } catch (RemoteException ex) {
            this.properties = new EnhancedProperties();
            ex.printStackTrace();
         }
      } else {
         this.properties = new EnhancedProperties();
      }
   }

   @Override
   public Object export(Object callback) throws ExportException {
      try {
         Exporter exporter = createExporter();
         Object exportedObject = exporter.export((Remote) callback);
         exports.put(callback, new ExportInfo(exporter, exportedObject));
         return exportedObject;
      } catch (UnknownHostException e) {
         throw new ExportException("failed to export", e);
      }
   }

   @Override
   public void unexport(Object callback) throws Exception {
      ExportInfo info = exports.remove(callback);
      if (info != null) {
         info.exporter.unexport(false);
      }
   }

   @Override
   public Object findExport(Object callback) {
      ExportInfo info = exports.get(callback);
      if (info != null) {
         return info.exportedObject;
      }
      return null;
   }

   @Override
   public void stop() throws Exception {
      for (ExportInfo info : exports.values()) {
         info.exporter.unexport(false);
      }
      exports.clear();
   }

   private Exporter createExporter() throws UnknownHostException {
      return new BasicJeriExporter(TcpServerEndpoint.getInstance(Network.getValidIP().getHostAddress(), 0), new BasicILFactory(null, null, exportClassLoader), false, false);
   }

   @Override
   public String getConnectorType() {
      return TYPE;
   }

   @Override
   public Serializable getProperty(String property, Serializable defaultValue) {
      return properties.getProperty(property, defaultValue);
   }

   @Override
   public Object getService() {
      return service;
   }

   @Override
   public boolean ping() {
      if(getService() != null && getService() instanceof IHostTestEnvironment){
         try{
            ((IHostTestEnvironment)getService()).getProperties();
            return true;
         } catch (Throwable th){
            return false;
         }
      }
      return false;
   }

   @Override
   public void addPropertyChangeListener(IServicePropertyChangeListener listener) {
      propertyChangeListeners.add(listener);
   }

   @Override
   public void removePropertyChangeListener(IServicePropertyChangeListener listener) {
      propertyChangeListeners.remove(listener);
   }

   @Override
   public void setProperty(String key, Serializable value) {
      properties.setProperty(key, value);
      for (IServicePropertyChangeListener listener : propertyChangeListeners) {
         listener.propertyChanged(this, key, value);
      }
   }

   @Override
   public URI upload(File file) throws Exception {
      return null;
   }

   public String getUniqueServerId() {
      return uniqueServerId;
   }

   @Override
   public EnhancedProperties getProperties() {
      return properties;
   }

   @Override
   public void init(Object service) throws UnknownHostException, ExportException {
      
   }
   
}
