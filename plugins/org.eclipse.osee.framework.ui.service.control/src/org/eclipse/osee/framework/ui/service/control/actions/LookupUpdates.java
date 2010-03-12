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
package org.eclipse.osee.framework.ui.service.control.actions;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.jini.discovery.EclipseJiniClassloader;
import org.eclipse.osee.framework.jini.discovery.IRegistrarListener;
import org.eclipse.osee.framework.ui.service.control.managers.ReggieCache;
import org.eclipse.osee.framework.ui.service.control.renderer.IRenderer;
import org.eclipse.osee.framework.ui.service.control.renderer.ReggieItemHandler;
import org.eclipse.osee.framework.ui.service.control.widgets.LookupViewer;
import org.eclipse.osee.framework.ui.service.control.widgets.ManagerMain;
import net.jini.core.lookup.ServiceID;
import net.jini.core.lookup.ServiceRegistrar;

/**
 * @author Roberto E. Escobar
 */
public class LookupUpdates implements IRegistrarListener {

   private LookupViewer viewer;
   private ReggieCache reggieCache;
   private Map<ServiceID, IRenderer> handlerMap;
   private Map<ServiceID, ServiceRegistrar> filteredReggieMap;
   private Collection<ServiceRegistrar> unfilteredList;

   public LookupUpdates(ManagerMain mainWindow) {
      super();
      this.viewer = mainWindow.getLookupViewer();
      this.handlerMap = new HashMap<ServiceID, IRenderer>();
      this.reggieCache = ReggieCache.getEclipseInstance(EclipseJiniClassloader.getInstance());
      this.filteredReggieMap = new HashMap<ServiceID, ServiceRegistrar>();
      this.unfilteredList = reggieCache.getServiceRegistrars().values();
      this.viewer.setRendererMap(handlerMap);
      this.viewer.setInput(filteredReggieMap.values());
      reggieCache.addListener(this);
   }

   public void clear() {
      handlerMap.clear();
      filteredReggieMap.clear();
   }

   public void filterLookupServers(boolean displayAll) {
      this.viewer.setInput(displayAll ? unfilteredList : filteredReggieMap.values());
      this.viewer.refresh();
   }

   public void reggieAdded(List<ServiceRegistrar> serviceRegistrars) {
      // System.out.println("Reggie Added: ");
      Iterator<ServiceRegistrar> iterator = serviceRegistrars.iterator();
      while (iterator.hasNext()) {
         ServiceRegistrar reggie = iterator.next();
         ServiceID serviceID = reggie.getServiceID();
         if (!handlerMap.containsKey(serviceID)) {
            handlerMap.put(serviceID, new ReggieItemHandler(reggie));

            if (ReggieItemHandler.isAllowed(reggie)) {
               filteredReggieMap.put(serviceID, reggie);
            }
         }
      }
      viewer.refresh();
   }

   @SuppressWarnings("unchecked")
   public void reggieRemoved(List<ServiceRegistrar> serviceRegistrars) {
      // System.out.println("Reggie Removed: ");
      Set<ServiceID> cachedReggies = handlerMap.keySet();
      Set<ServiceID> availableReggies = new HashSet<ServiceID>();
      Iterator<ServiceRegistrar> iterator = serviceRegistrars.iterator();
      while (iterator.hasNext()) {
         ServiceRegistrar reggie = iterator.next();
         availableReggies.add(reggie.getServiceID());
      }

      List discardedIds =
            org.eclipse.osee.framework.jdk.core.util.Collections.setComplement(cachedReggies, availableReggies);
      Iterator iterator2 = discardedIds.iterator();
      ServiceID toProcess = null;
      while (iterator2.hasNext()) {
         toProcess = (ServiceID) iterator2.next();
         handlerMap.remove(toProcess);

         if (filteredReggieMap.containsKey(toProcess)) {
            filteredReggieMap.remove(toProcess);
         }
      }
      viewer.refresh();
   }

   public void reggieChanged(List<ServiceRegistrar> serviceRegistrars) {
      // System.out.println("Reggie Changed: ");
      Iterator<ServiceRegistrar> iterator = serviceRegistrars.iterator();
      while (iterator.hasNext()) {
         ServiceRegistrar reggie = iterator.next();
         ServiceID serviceID = reggie.getServiceID();
         if (handlerMap.containsKey(serviceID)) {
            handlerMap.put(serviceID, new ReggieItemHandler(reggie));
         }
         if (ReggieItemHandler.isAllowed(reggie)) {
            filteredReggieMap.put(serviceID, reggie);
         }
      }
      viewer.refresh();
   }

   public void dispose() {
      reggieCache.removeListener(this);
      viewer.dispose();
      handlerMap.clear();
      filteredReggieMap.clear();
   }
}
