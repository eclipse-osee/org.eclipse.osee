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
package org.eclipse.osee.ats.rest.internal.workitem.operations;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.IAtsDatabaseConversion;
import org.eclipse.osee.ats.api.workdef.AtsWorkDefinitionTokens;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Collections;

/**
 * See description below
 *
 * @author Donald G Dunne
 */
public class FixWorkDefinitionToAttributeConversion implements IAtsDatabaseConversion {

   @Override
   public void run(XResultData rd, boolean reportOnly, AtsApi atsApi) {
      List<ArtifactId> artIdList = new LinkedList<>();
      artIdList.addAll(atsApi.getQueryService().createQuery(WorkItemType.WorkItem).andAttr(
         AtsAttributeTypes.WorkflowDefinition, AtsWorkDefinitionTokens.WorkDef_Team_Default.getName()).getItemIds());
      List<Collection<ArtifactId>> subDivide = Collections.subDivide(artIdList, 500);
      int size = subDivide.size();
      int count = 1;
      for (Collection<ArtifactId> artIds : subDivide) {
         String msg = String.format("WorkItem Set: Processing %s / %s\n", count++, size);
         System.err.println(msg);
         rd.logf(msg);
         List<Long> ids = new LinkedList<>();
         for (ArtifactId art : artIds) {
            ids.add(art.getId());
         }
         Collection<ArtifactToken> artSet = atsApi.getQueryService().getArtifacts(ids);
         for (ArtifactToken art : artSet) {
            IAtsTeamWorkflow teamWf = atsApi.getWorkItemService().getTeamWf(art);
            IAtsWorkDefinition workDefinition = atsApi.getWorkDefinitionService().getWorkDefinition(teamWf);
            IAtsWorkDefinition computeWorkDefinition =
               atsApi.getWorkDefinitionService().computeWorkDefinition(teamWf, false);
            if (!workDefinition.equals(computeWorkDefinition)) {
               String log = String.format("Work Def [%s] doesn't match computed [%s] for %s", workDefinition,
                  computeWorkDefinition, teamWf.toStringWithId());
               System.err.println(log);
               rd.error(log);
            }
         }
      }
   }

   @Override
   public String getDescription() {
      StringBuffer data = new StringBuffer();
      data.append("Fix/Validate Work Definition to attributes conversion....\n");
      return data.toString();
   }

   @Override
   public String getName() {
      return "Fix/Validate Work Definition To Attributes";
   }
}