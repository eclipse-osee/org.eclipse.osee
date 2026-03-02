/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.ats.ide.util.widgets;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.ServiceUtil;
import org.eclipse.osee.ats.ide.world.WorldEditor;
import org.eclipse.osee.framework.core.data.ApplicabilityData;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.widgets.XAbstractHyperlinkLabelCmdValueSelWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XAbstractHyperlinkLabelValueSelWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.ViewApplicabilityTokenFilterTreeDialog;
import org.eclipse.osee.orcs.rest.model.ApplicabilityEndpoint;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XHyperlabelWorkflowApplicSelArtWidget extends XAbstractHyperlinkLabelCmdValueSelWidget {

   public static WidgetId ID = WidgetIdAts.XHyperlabelWorkflowApplicSelArtWidget;

   public List<ApplicabilityToken> selectedAppls;
   private IAtsWorkItem workItem;

   public XHyperlabelWorkflowApplicSelArtWidget() {
      super(ID, "Referenced Applicabilities", true, WorldEditor.TITLE_MAX_LENGTH);
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

         if (teamWorkflow != null) {
            BranchId branch = AtsApiService.get().getBranchService().getConfiguredBranchForWorkflow(teamWorkflow);
            ApplicabilityEndpoint applEndpoint = ServiceUtil.getOseeClient().getApplicabilityEndpoint(branch);
            Iterable<ApplicabilityToken> applicabilityTokens = applEndpoint.getApplicabilityTokens();

            ViewApplicabilityTokenFilterTreeDialog dialog =
               new ViewApplicabilityTokenFilterTreeDialog("Select View Applicability", "Select View Applicability");
            dialog.setInput(applicabilityTokens);
            dialog.setMultiSelect(true);
            int result = dialog.open();
            if (result == Window.OK) {
               selectedAppls = Collections.castAll(dialog.getSelected());
               return true;
            }
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

   public void saveToArtifact() {
      List<ApplicabilityData> appDatas = new LinkedList<>();
      ApplicabilityData data = new ApplicabilityData();
      data.setArtifact(ArtifactId.create(workItem.getStoreObject()));
      data.setApplIds(Collections.castAll(selectedAppls));
      appDatas.add(data);
      AtsApiService.get().getOseeClient().getApplicabilityEndpoint(
         AtsApiService.get().getAtsBranch()).setApplicabilityReference(appDatas);
   }

   private List<ApplicabilityToken> getStoredApplicabilities() {
      List<ApplicabilityToken> tokens = new ArrayList<ApplicabilityToken>();
      try {
         BranchToken branch = AtsApiService.get().getAtsBranch();
         ApplicabilityEndpoint applicEndpoint = AtsApiService.get().getOseeClient().getApplicabilityEndpoint(branch);
         List<ApplicabilityToken> applicTokens =
            applicEndpoint.getApplicabilityReferenceTokens(workItem.getArtifactId());
         for (ApplicabilityToken tok : applicTokens) {
            if (tok.isValid()) {
               tokens.add(tok);
            }
         }
      } catch (Exception ex) {
         OseeLog.log(XAbstractHyperlinkLabelValueSelWidget.class, Level.SEVERE,
            String.format("Exception getting applicability for workItem %s", workItem.toStringWithId()), ex);
      }
      return tokens;
   }

   @Override
   public void setArtifact(Artifact artifact) {
      super.setArtifact(artifact);
      if (artifact instanceof IAtsWorkItem) {
         this.workItem = (IAtsWorkItem) artifact;
         selectedAppls = getStoredApplicabilities();
      }
   }

}
