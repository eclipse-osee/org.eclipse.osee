/*******************************************************************************
 * Copyright (c) 2025 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.util.widgets;

import java.util.Arrays;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.agile.ApplicabilityColumnUI;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.AtsApiIde;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.XAbstractHyperlinkLabelValueSelWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.osee.orcs.rest.model.ApplicabilityEndpoint;
import org.osgi.service.component.annotations.Component;

/**
 * Widget to allow a single applicability token to be selected from the applicabilities configured on the product line
 * branch for this team and stored as the workflow's single applicability token. This is different than other PLE where
 * applicability tokens come from the same branch the artifact is on.
 *
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XHyperlinkApplicabilityArtWidget extends XAbstractHyperlinkLabelValueSelWidget {

   public static WidgetId ID = WidgetIdAts.XHyperlinkApplicabilityArtWidget;
   private IAtsTeamWorkflow teamWf;
   private final AtsApiIde atsApi;
   private String value = Widgets.NOT_SET;
   private ApplicabilityToken selected;

   public XHyperlinkApplicabilityArtWidget() {
      this("Applicability");
   }

   public XHyperlinkApplicabilityArtWidget(String label) {
      super(ID, label);
      atsApi = AtsApiService.get();
   }

   @Override
   public String getCurrentValue() {
      return value;
   }

   @Override
   public boolean handleSelection() {
      boolean changed = ApplicabilityColumnUI.promptChangeApplicability(
         Arrays.asList((AbstractWorkflowArtifact) teamWf.getStoreObject()));
      if (changed) {
         refresh();
      }
      return changed;
   }

   @Override
   public void refresh() {
      ApplicabilityEndpoint applicEp = atsApi.getOseeClient().getApplicabilityEndpoint(CoreBranches.COMMON);
      selected = applicEp.getApplicabilityToken(teamWf.getArtifactId());
      if (selected == null) {
         value = Widgets.NOT_SET;
      } else {
         value = selected.toString();
      }
      super.refresh();
   }

   public void refreshSuper() {
      super.refresh();
   }

   @Override
   public void setArtifact(Artifact artifact) {
      super.setArtifact(artifact);
      if (artifact instanceof IAtsTeamWorkflow) {
         teamWf = (IAtsTeamWorkflow) artifact;
      }
   }

}
