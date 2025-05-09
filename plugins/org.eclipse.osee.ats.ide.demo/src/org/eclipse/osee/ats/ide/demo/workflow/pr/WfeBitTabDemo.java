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

package org.eclipse.osee.ats.ide.demo.workflow.pr;

import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.config.JaxTeamWorkflow;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.cr.bit.model.BuildImpactDatas;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.editor.tab.bit.WfeBitTab;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.ui.forms.IManagedForm;

public class WfeBitTabDemo extends WfeBitTab {

   public static List<ArtifactTypeToken> validBidArtTypes = Arrays.asList(AtsArtifactTypes.DemoReqTeamWorkflow,
      AtsArtifactTypes.DemoCodeTeamWorkflow, AtsArtifactTypes.DemoTestTeamWorkflow);

   public WfeBitTabDemo(WorkflowEditor editor, IAtsTeamWorkflow teamWf) {
      super(editor, teamWf);
      setXBitXViewerFactoryAms(new XBitXViewerFactoryDemo());
   }

   @Override
   protected void createFormContent(IManagedForm managedForm) {
      super.createFormContent(managedForm);
      messageLabel.addMouseListener(new MouseAdapter() {

         @Override
         public void mouseUp(MouseEvent e) {
            if (e.button == 3) {
               handleDebugInfo();
            }
         }

      });
   }

   protected void handleDebugInfo() {
      BuildImpactDatas debugBids = BuildImpactDataSampleDemo.get();
      debugBids.setBidArtType(getBuildImpactDataType());
      debugBids.setTeamWf(teamWf.getArtifactToken());
      bids = atsApi.getServerEndpoints().getActionEndpoint().updateBids(teamWf.getArtifactToken(), debugBids);
      if (bids.getResults().isErrors()) {
         XResultDataUI.report(bids.getResults(), "Error creating BIDs");
      } else {
         ((Artifact) teamWf).reloadAttributesAndRelations();
         refresh();
      }
   }

   @Override
   public ArtifactTypeToken getBuildImpactDataType() {
      return AtsArtifactTypes.BuildImpactData;
   }

   @Override
   public void creatingSibling(IAtsTeamWorkflow teamWf, JaxTeamWorkflow jTeamWf, IAtsActionableItem ai) {
      String propRes =
         atsApi.getAttributeResolver().getSoleAttributeValueAsString(teamWf, AtsAttributeTypes.ProposedResolution, "");
      if (Strings.isValid(propRes)) {
         jTeamWf.addAttribute(AtsAttributeTypes.Description, propRes);
      }
   }

   @Override
   public boolean isValidBidWorkflow(Artifact art) {
      return validBidArtTypes.contains(art.getArtifactType());
   }

}
