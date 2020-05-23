/*********************************************************************
 * Copyright (c) 2014 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.rest.internal.cpa;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.osee.ats.api.cpa.IAtsCpaService;

/**
 * @author Donald G Dunne
 */
public class CpaServiceRegistry {

   private final Map<String, IAtsCpaService> idToCpaService = new ConcurrentHashMap<>();

   public void addCpaService(IAtsCpaService cpaService) {
      String id = cpaService.getId();
      idToCpaService.put(id, cpaService);
   }

   public void removeCpaService(IAtsCpaService cpaService) {
      String id = cpaService.getId();
      idToCpaService.remove(id);
   }

   public Iterable<IAtsCpaService> getServices() {
      return idToCpaService.values();
   }

   public IAtsCpaService getServiceById(String pcrSystem) {
      return idToCpaService.get(pcrSystem);
   }

}
