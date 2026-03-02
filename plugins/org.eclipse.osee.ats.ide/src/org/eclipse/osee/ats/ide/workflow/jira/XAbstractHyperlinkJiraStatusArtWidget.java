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
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.XAbstractHyperlinkLabelValueSelWidget;

/**
 * @author Donald G. Dunne
 */
public abstract class XAbstractHyperlinkJiraStatusArtWidget extends XAbstractHyperlinkLabelValueSelWidget {

   protected IAtsTeamWorkflow teamWf;

   public XAbstractHyperlinkJiraStatusArtWidget(WidgetId widgetId) {
      super(widgetId, "JIRA Status");
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
   public void setArtifact(Artifact artifact) {
      super.setArtifact(artifact);
      if (artifact instanceof IAtsTeamWorkflow) {
         this.teamWf = (IAtsTeamWorkflow) artifact;
      }
   }

}
