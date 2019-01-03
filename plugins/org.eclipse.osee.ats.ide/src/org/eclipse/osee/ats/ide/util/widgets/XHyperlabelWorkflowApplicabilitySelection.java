/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.util.widgets;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.util.ServiceUtil;
import org.eclipse.osee.ats.ide.world.WorldEditor;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.widgets.IArtifactWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlinkLabelCmdValueSelection;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.ViewApplicabilityFilterTreeDialog;
import org.eclipse.osee.orcs.rest.model.ApplicabilityEndpoint;

/**
 * @author Donald G. Dunne
 */
public class XHyperlabelWorkflowApplicabilitySelection extends XHyperlinkLabelCmdValueSelection implements IArtifactWidget {

   public static final String WIDGET_ID = XHyperlabelWorkflowApplicabilitySelection.class.getSimpleName();
   public List<ApplicabilityToken> selectedAppls = new LinkedList<>();
   private IAtsWorkItem workItem;

   public XHyperlabelWorkflowApplicabilitySelection() {
      super("Referenced Applicabilities", true, WorldEditor.TITLE_MAX_LENGTH);
   }

   public List<ApplicabilityToken> getSelectedApplicabilities() {
      return selectedAppls;
   }

   @Override
   public Object getData() {
      return getSelectedApplicabilities();
   }

   @Override
   public String getCurrentValue() {
      return Artifacts.commaArts(selectedAppls);
   }

   @Override
   public boolean handleClear() {
      selectedAppls.clear();
      notifyXModifiedListeners();
      return true;
   }

   @Override
   public boolean handleSelection() {
      try {
         IAtsTeamWorkflow teamWorkflow = workItem.getParentTeamWorkflow();
         if (teamWorkflow == null) {
            AWorkbench.popup("No valid parent Team Workflow found");
         }

         BranchId branch = AtsClientService.get().getBranchService().getConfiguredBranchForWorkflow(teamWorkflow);
         ApplicabilityEndpoint applEndpoint = ServiceUtil.getOseeClient().getApplicabilityEndpoint(branch);
         Iterable<ApplicabilityToken> applicabilityTokens = applEndpoint.getApplicabilityTokens();

         ViewApplicabilityFilterTreeDialog dialog =
            new ViewApplicabilityFilterTreeDialog("Select View Applicability", "Select View Applicability");
         dialog.setInput(applicabilityTokens);
         dialog.setMultiSelect(true);
         int result = dialog.open();
         if (result == Window.OK) {
            selectedAppls = Collections.castAll(dialog.getSelected());
            HashMap<ArtifactId, List<ApplicabilityId>> artToApplMap = new HashMap<>();
            artToApplMap.put(ArtifactId.valueOf(workItem.getStoreObject().getId()), Collections.castAll(selectedAppls));
            AtsClientService.get().getOseeClient().getApplicabilityEndpoint(
               AtsClientService.get().getAtsBranch()).setApplicabilityReference(artToApplMap);
            return true;
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   @Override
   public boolean isEmpty() {
      return selectedAppls.isEmpty();
   }

   @Override
   public Artifact getArtifact() {
      return AtsClientService.get().getQueryServiceClient().getArtifact(workItem);
   }

   @Override
   public void saveToArtifact() {
      HashMap<ArtifactId, List<ApplicabilityId>> artToApplMap = new HashMap<>();
      List<ApplicabilityToken> selectedApplicabilities = getSelectedApplicabilities();
      if (!selectedApplicabilities.isEmpty()) {
         artToApplMap.put(workItem.getStoreObject(), Collections.castAll(selectedApplicabilities));
         AtsClientService.get().getOseeClient().getApplicabilityEndpoint(
            AtsClientService.get().getAtsBranch()).setApplicabilityReference(artToApplMap);
      }
   }

   @Override
   public void revert() {
      this.selectedAppls = getStoredApplicabilities();
   }

   private List<ApplicabilityToken> getStoredApplicabilities() {
      return AtsClientService.get().getOseeClient().getApplicabilityEndpoint(
         AtsClientService.get().getAtsBranch()).getApplicabilityReferenceTokens(workItem.getStoreObject());
   }

   @Override
   public Result isDirty() {
      if (!Collections.isEqual(getStoredApplicabilities(), getSelectedApplicabilities())) {
         return Result.TrueResult;
      }
      return Result.FalseResult;
   }

   @Override
   public void setArtifact(Artifact artifact) {
      if (artifact instanceof IAtsWorkItem) {
         this.workItem = (IAtsWorkItem) artifact;
         selectedAppls = getStoredApplicabilities();
      }
   }

}
