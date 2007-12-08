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
package org.eclipse.osee.framework.ui.service.control.managers;

import org.eclipse.osee.framework.jdk.core.type.InputManager;
import org.eclipse.osee.framework.jdk.core.type.TreeParent;
import org.eclipse.osee.framework.jini.discovery.EclipseJiniClassloader;
import org.eclipse.osee.framework.jini.discovery.IServiceLookupListener;
import org.eclipse.osee.framework.jini.discovery.ServiceDataStore;
import net.jini.core.lookup.ServiceItem;

/**
 * @author Roberto E. Escobar
 */
public class ServicesManager implements IServiceLookupListener {

   private static ServicesManager instance = null;
   private ServiceTreeBuilder servicesTreeBuilder;

   private ServicesManager() {
      super();
      servicesTreeBuilder = new ServiceTreeBuilder();
      ServiceDataStore.getEclipseInstance(EclipseJiniClassloader.getInstance()).addListener(this);
   }

   public static ServicesManager getInstance() {
      if (instance == null) {
         instance = new ServicesManager();
      }
      return instance;
   }

   public void serviceAdded(ServiceItem serviceItem) {
      servicesTreeBuilder.serviceAdded(serviceItem);
   }

   public void serviceChanged(ServiceItem serviceItem) {
      servicesTreeBuilder.serviceChanged(serviceItem);
   }

   public void serviceRemoved(ServiceItem serviceItem) {
      servicesTreeBuilder.serviceRemoved(serviceItem);
   }

   public void clear() {
      servicesTreeBuilder.clear();
   }

   public InputManager<TreeParent> getInputManager() {
      return servicesTreeBuilder.getInputManager();
   }

   public void dispose() {
      ServiceDataStore.getEclipseInstance(EclipseJiniClassloader.getInstance()).removeListener(this);
      servicesTreeBuilder.clear();
      servicesTreeBuilder.getInputManager().dispose();
   }
}
