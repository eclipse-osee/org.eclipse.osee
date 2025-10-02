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
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.agile.ApplicabilityColumnUI;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.ArtifactWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlinkLabelValueSelection;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.osee.orcs.rest.model.ApplicabilityEndpoint;

/**
 * Widget to allow a single applicability token to be selected from the applicabilities configured on the product line
 * branch for this team and stored as the workflow's single applicability token. This is different than other PLE where
 * applicability tokens come from the same branch the artifact is on.
 *
 * @author Donald G. Dunne
 */
public class XHyperlinkApplicabilityDam extends XHyperlinkLabelValueSelection implements ArtifactWidget {

   private IAtsTeamWorkflow teamWf;
   private final AtsApi atsApi;
   private String value = Widgets.NOT_SET;
   private ApplicabilityToken selected;

   public XHyperlinkApplicabilityDam() {
      this("Applicability");
   }

   public XHyperlinkApplicabilityDam(String label) {
      super(label);
      atsApi = AtsApiService.get();
   }

   @Override
   public String getCurrentValue() {
      return value;
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
      ApplicabilityEndpoint applicEp =
         AtsApiService.get().getOseeClient().getApplicabilityEndpoint(CoreBranches.COMMON);
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
      if (artifact instanceof IAtsTeamWorkflow) {
         teamWf = (IAtsTeamWorkflow) artifact;
      }
   }

}
