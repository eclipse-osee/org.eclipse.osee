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
package org.eclipse.osee.connection.service;

import java.io.File;
import java.io.Serializable;
import java.net.URI;
import java.rmi.server.ExportException;
import java.util.HashSet;

import org.eclipse.osee.framework.jdk.core.util.EnhancedProperties;

/**
 * @author Ken J. Aguilar
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

   @Override
   public Object getService() {
      return service;
   }

   @Override
   public void stop() {
   }

   @Override
   public String getConnectorType() {
      return TYPE;
   }

   @Override
   public void setProperty(String key, Serializable value) {
      properties.setProperty(key, value);
      for (IServicePropertyChangeListener listener : propertyChangeListeners) {
         listener.propertyChanged(this, key, value);
      }

   }

   @Override
   public Serializable getProperty(String property, Serializable defaultValue) {
      return properties.getProperty(property, defaultValue);
   }

   @Override
   public Object export(Object callback) throws ExportException {
      return callback;
   }

   @Override
   public void unexport(Object callback) throws Exception {
   }
   

   @Override
   public Object findExport(Object callback) {
		return callback;
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
   public URI upload(File file) throws Exception {
      return file.toURI();
   }

    @Override
    public boolean ping() {
	return true;
    }

}
