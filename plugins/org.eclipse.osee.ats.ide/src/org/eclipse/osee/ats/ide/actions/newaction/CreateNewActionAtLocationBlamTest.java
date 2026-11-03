/*********************************************************************
 * Copyright (c) 2026 Boeing
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

package org.eclipse.osee.ats.ide.actions.newaction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.agile.IAgileBacklog;
import org.eclipse.osee.ats.api.agile.IAgileItem;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.AtsApiIde;
import org.eclipse.osee.ats.ide.workflow.goal.GoalMemberProvider;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.BlamEditor;

/**
 * @author Donald G. Dunne
 */
public class CreateNewActionAtLocationBlamTest extends CreateNewActionBlamTest {

   private AtsApiIde atsApi;
   private Artifact sawBacklogArt;
   private IAgileBacklog sawBacklog2;
   private Collection<IAgileItem> items;

   public CreateNewActionAtLocationBlamTest() {
      super(new CreateNewActionAtLocationBlam());
   }

   @Override
   protected void setupDemoTest(AbstractBlam blam2, XResultData rd) {
      CreateNewActionAtLocationBlam blam = (CreateNewActionAtLocationBlam) getBlam();
      blam.setOpenUponCreation(false);

      atsApi = AtsApiService.get();
      sawBacklogArt =
         (Artifact) atsApi.getQueryService().getArtifact(DemoArtifactToken.SAW_Backlog, CoreBranches.COMMON);
      sawBacklogArt.reloadAttributesAndRelations();
      sawBacklog2 = atsApi.getAgileService().getAgileBacklog(sawBacklogArt);
      items = atsApi.getAgileService().getItems(sawBacklog2);
      rd.assertEquals(18, items.size());

      GoalMemberProvider provider = new GoalMemberProvider(sawBacklog2);
      blam.setMemberProvider(provider);
      IAtsTeamWorkflow sawCodeCommittedWf = getSawCodeCommittedWf();
      blam.setDropTarget((Artifact) sawCodeCommittedWf.getStoreObject());
      List<IAtsActionableItem> ais = new ArrayList<>();
      ais.addAll(sawCodeCommittedWf.getActionableItems());
      blam.setAias(ais);
   }

   private IAtsTeamWorkflow getSawCodeCommittedWf() {
      return atsApi.getWorkItemService().getTeamWf(DemoArtifactToken.SAW_Commited_Code_TeamWf);
   }

   @Override
   protected void runDemoTest(BlamEditor editor, XResultData rd) throws Exception {
      super.runDemoTest(editor, rd);

      sawBacklogArt.reloadAttributesAndRelations();
      sawBacklog2 = atsApi.getAgileService().getAgileBacklog(sawBacklogArt);
      items = atsApi.getAgileService().getItems(sawBacklog2);
      rd.assertEquals(19, items.size());

      cleanupSimpleTest(getClass().getSimpleName(), CoreBranches.COMMON);
   }

}
