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
package org.eclipse.osee.ats.core.client;

import org.eclipse.osee.ats.api.team.IAtsTeamDefinitionService;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionService;
import org.eclipse.osee.ats.api.workflow.IAtsWorkItemService;
import org.eclipse.osee.ats.core.client.config.AtsTeamDefinitionServiceImpl;
import org.eclipse.osee.ats.core.client.team.TeamWorkflowProviders;
import org.eclipse.osee.ats.core.client.workdef.WorkDefinitionFactory;
import org.eclipse.osee.ats.core.workdef.AtsWorkDefinitionService;
import org.eclipse.osee.ats.core.workflow.AtsWorkItemService;

/**
 * @author Donald G. Dunne
 */
public class AtsClient {

   private static IAtsTeamDefinitionService teamDefService;
   private static WorkDefinitionFactory workDefFactory;
   private static IAtsWorkDefinitionService workDefService;

   public static IAtsTeamDefinitionService getTeamDefService() {
      if (teamDefService == null) {
         teamDefService = new AtsTeamDefinitionServiceImpl();
      }
      return teamDefService;
   }

   public static IAtsWorkDefinitionService getWorkDefService() {
      if (workDefService == null) {
         workDefService = AtsWorkDefinitionService.getService();
      }
      return workDefService;
   }

   public static WorkDefinitionFactory getWorkDefFactory() {
      if (workDefFactory == null) {
         workDefFactory =
            new WorkDefinitionFactory(getTeamDefService(), getWorkItemService(), getWorkDefService(),
               TeamWorkflowProviders.getAtsTeamWorkflowProviders());
      }
      return workDefFactory;
   }

   public static IAtsWorkItemService getWorkItemService() {
      return AtsWorkItemService.get();
   }
}
