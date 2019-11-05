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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.util.ServiceUtil;
import org.eclipse.osee.ats.ide.world.WorldEditor;
import org.eclipse.osee.framework.core.data.ApplicabilityData;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.widgets.IArtifactWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlinkLabelCmdValueSelection;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlinkLabelValueSelection;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.ViewApplicabilityFilterTreeDialog;
import org.eclipse.osee.orcs.rest.model.ApplicabilityEndpoint;

/**
 * @author Donald G. Dunne
 */
public class XHyperlabelWorkflowApplicabilitySelection extends XHyperlinkLabelCmdValueSelection implements IArtifactWidget {

   public static final String WIDGET_ID = XHyperlabelWorkflowApplicabilitySelection.class.getSimpleName();
   public List<ApplicabilityToken> selectedAppls;
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
      if (selectedAppls == null) {
         selectedAppls = getSelectedApplicabilities();
      }
      return Collections.toString(",", selectedAppls);
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
      if (isDirty().isTrue()) {
         List<ApplicabilityData> appDatas = new LinkedList<>();
         ApplicabilityData data = new ApplicabilityData();
         data.setArtifact(ArtifactId.valueOf(workItem.getStoreObject().getId()));
         data.setApplIds(Collections.castAll(selectedAppls));
         appDatas.add(data);
         AtsClientService.get().getOseeClient().getApplicabilityEndpoint(
            AtsClientService.get().getAtsBranch()).setApplicabilityReference(appDatas);
      }
   }

   @Override
   public void revert() {
      this.selectedAppls = getStoredApplicabilities();
   }

   private List<ApplicabilityToken> getStoredApplicabilities() {
      List<ApplicabilityToken> tokens = new ArrayList<ApplicabilityToken>();
      try {
         IOseeBranch branch = AtsClientService.get().getAtsBranch();
         ApplicabilityEndpoint applicEndpoint = AtsClientService.get().getOseeClient().getApplicabilityEndpoint(branch);
         List<ApplicabilityToken> applicTokens =
            applicEndpoint.getApplicabilityReferenceTokens(workItem.getArtifactId());
         for (ApplicabilityToken tok : applicTokens) {
            if (tok.isValid()) {
               tokens.add(tok);
            }
         }
      } catch (Exception ex) {
         OseeLog.log(XHyperlinkLabelValueSelection.class, Level.SEVERE,
            String.format("Exception getting applicability for workItem %s", workItem.toStringWithId()), ex);
      }
      return tokens;
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
