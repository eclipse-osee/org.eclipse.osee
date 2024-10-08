/*********************************************************************
 * Copyright (c) 2018 Boeing
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

package org.eclipse.osee.ats.core.workflow.util;

import java.util.Collection;
import java.util.Map;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IValidatingOperation;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.AXml;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * Duplicate Workflow including all fields and, states.
 *
 * @author Donald G. Dunne
 */
public abstract class AbstractDuplicateWorkflowOperation implements IValidatingOperation {

   protected final Collection<IAtsTeamWorkflow> teamWfs;
   protected final String title;
   protected final AtsApi atsApi;
   protected Map<IAtsTeamWorkflow, IAtsTeamWorkflow> oldToNewMap;
   protected final AtsUser asUser;

   public AbstractDuplicateWorkflowOperation(Collection<IAtsTeamWorkflow> teamWfs, String title, AtsUser asUser, AtsApi atsApi) {
      this.teamWfs = teamWfs;
      this.title = title;
      this.asUser = asUser;
      this.atsApi = atsApi;
   }

   @Override
   public XResultData validate() {
      XResultData results = new XResultData();
      if (teamWfs.isEmpty()) {
         results.error("Team Workflows can not be empty.");
      }
      if (asUser == null) {
         results.error("AsUser can not be empty.");
      }
      return results;
   }

   /**
    * Return "Copy of"-title if title isn't specified
    */
   protected String getTitle(IAtsWorkItem workItem) {
      if (workItem.isTask()) {
         return workItem.getName();
      }
      if (teamWfs.size() == 1 && Strings.isValid(title)) {
         return AXml.textToXml(title);
      } else {
         if (workItem.isTeamWorkflow()) {
            return AXml.textToXml("Copy of " + workItem.getName());
         } else {
            return workItem.getName();
         }
      }
   }

   public Map<IAtsTeamWorkflow, IAtsTeamWorkflow> getResults() {
      return oldToNewMap;
   }

}
