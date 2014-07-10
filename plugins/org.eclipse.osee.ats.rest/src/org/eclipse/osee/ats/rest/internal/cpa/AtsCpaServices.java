/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.cpa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.ats.api.cpa.IAtsCpaService;
import org.eclipse.osee.ats.api.cpa.IAtsCpaServiceProvider;

/**
 * @author Donald G Dunne
 */
public class AtsCpaServices {

   static List<IAtsCpaServiceProvider> serviceProviders = new ArrayList<IAtsCpaServiceProvider>();
   static Map<String, IAtsCpaService> nameToService = new HashMap<String, IAtsCpaService>(5);

   public void addCpaServiceProvider(IAtsCpaServiceProvider serviceProvider) {
      serviceProviders.add(serviceProvider);
   }

   public static List<IAtsCpaService> getServices() {
      List<IAtsCpaService> services = new ArrayList<IAtsCpaService>();
      for (IAtsCpaServiceProvider provider : serviceProviders) {
         for (IAtsCpaService service : provider.getCpaServices()) {
            services.add(service);
         }
      }
      return services;
   }

   public static IAtsCpaService getService(String pcrSystem) {
      IAtsCpaService service = nameToService.get(pcrSystem);
      if (service == null) {
         for (IAtsCpaService ser : getServices()) {
            if (ser.getId().equals(pcrSystem)) {
               service = ser;
               nameToService.put(pcrSystem, service);
               break;
            }
         }
      }
      return service;
   }

}
