/*********************************************************************
 * Copyright (c) 2024 Boeing
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
package org.eclipse.osee.ats.ide.workflow.jira;

import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.ArtifactWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlinkLabelValueSelection;

/**
 * @author Donald G. Dunne
 */
public abstract class XHyperlinkJiraStatusSelection extends XHyperlinkLabelValueSelection implements ArtifactWidget {

   protected IAtsTeamWorkflow teamWf;

   public XHyperlinkJiraStatusSelection() {
      super("JIRA Status");
   }

   @Override
   public String getCurrentValue() {
      if (teamWf != null) {
         if (AtsApiService.get().getAttributeResolver().hasAttribute(teamWf, AtsAttributeTypes.JiraStoryId)) {
            String status = AtsApiService.get().getJiraService().getStatus(teamWf);
            if (Strings.isValid(status)) {
               return status;
            }
         }
         return "Not Linked";
      }
      return "";
   }

   @Override
   public Artifact getArtifact() {
      return (Artifact) teamWf.getStoreObject();
   }

   @Override
   public void saveToArtifact() {
      // do nothing
   }

   @Override
   public void revert() {
      // do nothing
   }

   @Override
   public Result isDirty() {
      return Result.FalseResult;
   }

   @Override
   public void setArtifact(Artifact artifact) {
      if (artifact instanceof IAtsTeamWorkflow) {
         this.teamWf = (IAtsTeamWorkflow) artifact;
      }
   }

}
