/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.connection.jini;

import java.io.Serializable;
import java.net.UnknownHostException;
import java.rmi.Remote;
import java.rmi.server.ExportException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import net.jini.core.entry.Entry;
import net.jini.export.Exporter;
import net.jini.jeri.BasicILFactory;
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.tcp.TcpServerEndpoint;

import org.eclipse.osee.connection.service.IServiceConnector;
import org.eclipse.osee.connection.service.IServicePropertyChangeListener;
import org.eclipse.osee.framework.jdk.core.util.EnhancedProperties;
import org.eclipse.osee.framework.jdk.core.util.Network;

/**
 * @author Ken J. Aguilar
 */
public abstract class JiniConnector implements IServiceConnector {
   protected final static String LINK_PROPERTY = "JINI_CONNECTOR_LINK";
   private final HashMap<Object, ExportInfo> exports = new HashMap<>();
   private final EnhancedProperties properties;
   private final HashSet<IServicePropertyChangeListener> propertyChangeListeners =
      new HashSet<IServicePropertyChangeListener>();
   private boolean connected = false;

   private static final class ExportInfo {
      private final Exporter exporter;
      private final Object exportedObject;

      private ExportInfo(Exporter exporter, Object exportedObject) {
         this.exportedObject = exportedObject;
         this.exporter = exporter;
      }
   }

   protected JiniConnector() {
      this(new EnhancedProperties());
   }

   protected JiniConnector(EnhancedProperties properties) {
      this.properties = properties;
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
      return new BasicJeriExporter(TcpServerEndpoint.getInstance(Network.getValidIP().getHostAddress(), 0),
         new BasicILFactory(null, null, Activator.getDefault().getExportClassLoader()), false, false);
   }

   protected Entry[] createEntries() {
      LinkedList<Entry> entries = new LinkedList<>();
      return entries.toArray(new Entry[entries.size()]);
   }

   @Override
   public Serializable getProperty(String property, Serializable defaultValue) {
      return properties.getProperty(property, defaultValue);
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
   public EnhancedProperties getProperties() {
      return properties;
   }

   public void entriesChanged(Entry[] entries) {
      EnhancedProperties newProps = new EnhancedProperties();
      for (String key : properties.differences(newProps)) {
         for (IServicePropertyChangeListener listener : propertyChangeListeners) {
            listener.propertyChanged(this, key, properties.getProperty(key));
         }
      }
   }
   
   public void setConnected(boolean connected){
      this.connected = connected;
   }
   
   public boolean isConnected() {
      return this.connected;
   }
}
