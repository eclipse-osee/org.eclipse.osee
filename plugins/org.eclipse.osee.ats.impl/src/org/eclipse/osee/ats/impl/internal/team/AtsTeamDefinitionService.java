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
package org.eclipse.osee.ats.impl.internal.team;

import org.eclipse.osee.ats.api.team.IAtsTeamDefinitionService;

public class AtsTeamDefinitionService {

   public static AtsTeamDefinitionService instance;
   private static IAtsTeamDefinitionService service;

   public void start() {
      AtsTeamDefinitionService.instance = this;
   }

   public static IAtsTeamDefinitionService getService() {
      if (instance == null) {
         throw new IllegalStateException("Ats Team Definition Service has not been activated");
      }
      return service;
   }

   public void setTeamDefinitionService(IAtsTeamDefinitionService service) {
      AtsTeamDefinitionService.service = service;
   }
}
