/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.client.query;

import org.eclipse.osee.ats.api.query.IAtsQueryService;

public class AtsQueryService {

   private static AtsQueryService instance;
   private static IAtsQueryService service;

   public void start() {
      AtsQueryService.instance = this;
   }

   public static IAtsQueryService getService() {
      if (instance == null) {
         throw new IllegalStateException("Ats Query Service has not been activated");
      }
      return service;
   }

   public void setQueryService(IAtsQueryService service) {
      AtsQueryService.service = service;
   }
}
