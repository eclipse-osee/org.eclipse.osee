/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.workdef.internal;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionProvider;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionProviderService;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.core.workdef.internal.workdefs.WorkDefGoal;
import org.eclipse.osee.ats.core.workdef.internal.workdefs.WorkDefReviewDecision;
import org.eclipse.osee.ats.core.workdef.internal.workdefs.WorkDefReviewPeerToPeer;
import org.eclipse.osee.ats.core.workdef.internal.workdefs.WorkDefSprint;
import org.eclipse.osee.ats.core.workdef.internal.workdefs.WorkDefTaskAtsConfig2Example;
import org.eclipse.osee.ats.core.workdef.internal.workdefs.WorkDefTaskDefault;
import org.eclipse.osee.ats.core.workdef.internal.workdefs.WorkDefTeamAtsConfig2Example;
import org.eclipse.osee.ats.core.workdef.internal.workdefs.WorkDefTeamDefault;
import org.eclipse.osee.ats.core.workdef.internal.workdefs.WorkDefTeamDemoCode;
import org.eclipse.osee.ats.core.workdef.internal.workdefs.WorkDefTeamDemoReq;
import org.eclipse.osee.ats.core.workdef.internal.workdefs.WorkDefTeamDemoSwDesign;
import org.eclipse.osee.ats.core.workdef.internal.workdefs.WorkDefTeamDemoTest;
import org.eclipse.osee.ats.core.workdef.internal.workdefs.WorkDefTeamSimple;

/**
 * Service to retrieve all work definitions that have been registered. Should not be used by applications, only by the
 * WorkDefinitionService
 *
 * @author Donald G. Dunne
 */
public class AtsWorkDefinitionProviderService implements IAtsWorkDefinitionProviderService {

   public Map<Long, IAtsWorkDefinition> idToWorkDef = new HashMap<>();

   /**
    * Add default ATS Workflow Definitions. Others will be provided via OSGI through addWorkDefinitionProvider
    */
   public AtsWorkDefinitionProviderService() {
      // @formatter:off
      for (IAtsWorkDefinition workDef : Arrays.asList(
         new WorkDefGoal().build(),
         new WorkDefReviewDecision().build(),
         new WorkDefReviewPeerToPeer().build(),
         new WorkDefSprint().build(),
         new WorkDefTaskAtsConfig2Example().build(),
         new WorkDefTaskDefault().build(),
         new WorkDefTeamAtsConfig2Example().build(),
         new WorkDefTeamDefault().build(),
         new WorkDefTeamDemoCode().build(),
         new WorkDefTeamDemoReq().build(),
         new WorkDefTeamDemoSwDesign().build(),
         new WorkDefTeamDemoTest().build(),
         new WorkDefTeamSimple().build())) {
         idToWorkDef.put(workDef.getId(), workDef);
      }
   }

   @Override
   public void addWorkDefinitionProvider(IAtsWorkDefinitionProvider workDefProvider) {
      for (IAtsWorkDefinition workDef : workDefProvider.getWorkDefinitions()) {
         idToWorkDef.put(workDef.getId(), workDef);
      }
   }

   @Override
   public IAtsWorkDefinition getWorkDefinition(Long id) {
      return idToWorkDef.get(id);
   }

   @Override
   public Collection<IAtsWorkDefinition> getAll() {
      return idToWorkDef.values();
   }

   @Override
   public void addWorkDefinition(WorkDefinition workDef) {
      idToWorkDef.put(workDef.getId(), workDef);
   }

}
