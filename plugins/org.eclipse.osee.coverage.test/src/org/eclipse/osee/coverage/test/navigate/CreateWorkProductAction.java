/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.coverage.test.navigate;

import java.util.Arrays;
import org.eclipse.osee.coverage.editor.CoverageEditor;
import org.eclipse.osee.coverage.editor.CoverageEditorInput;
import org.eclipse.osee.coverage.store.CoverageRelationTypes;
import org.eclipse.osee.coverage.store.OseeCoveragePackageStore;
import org.eclipse.osee.coverage.util.CoverageUtil;
import org.eclipse.osee.coverage.util.dialog.CoveragePackageArtifactListDialog;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.cm.IOseeCmService;

/**
 * @author Donald G. Dunne
 */
public class CreateWorkProductAction extends XNavigateItemAction {

   public CreateWorkProductAction() {
      super(null, "");
   }

   public CreateWorkProductAction(XNavigateItem parent) {
      super(parent, "Create Work Product Action");
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws Exception {

      IOseeCmService cmService = SkynetGuiPlugin.getInstance().getOseeCmService();
      if (cmService == null) {
         AWorkbench.popup("Can not acquire CM Service");
         return;
      }

      if (!CoverageUtil.getBranchFromUser(false)) {
         return;
      }
      Branch branch = CoverageUtil.getBranch();
      CoveragePackageArtifactListDialog dialog =
         new CoveragePackageArtifactListDialog("Open Coverage Package", "Select Coverage Package");
      dialog.setInput(OseeCoveragePackageStore.getCoveragePackageArtifacts(branch));
      if (dialog.open() == 0) {
         Artifact coveragePackageArtifact = (Artifact) dialog.getResult()[0];
         OseeCoveragePackageStore store = new OseeCoveragePackageStore(coveragePackageArtifact);

         SkynetTransaction transaction = new SkynetTransaction(branch, getName());
         Artifact actionArt =
            cmService.createPcr("Reqts PCR 1001", "Do requirements for PCR 1001", "Improvement", "1", null,
               Arrays.asList("SAW Requirements"));
         actionArt.persist(transaction);
         Artifact reqTeamArt = actionArt.getRelatedArtifact(CoverageRelationTypes.ActionToWorkflow_WorkFlow);
         reqTeamArt.persist(transaction);

         coveragePackageArtifact.addRelation(CoreRelationTypes.SupportingInfo_SupportingInfo, reqTeamArt);
         coveragePackageArtifact.persist(transaction);

         actionArt =
            cmService.createPcr("Code PCR 1001", "Do code for PCR 33201", "Improvement", "1", null,
               Arrays.asList("SAW Code"));
         actionArt.persist(transaction);
         Artifact codeTeamArt = actionArt.getRelatedArtifact(CoverageRelationTypes.ActionToWorkflow_WorkFlow);
         codeTeamArt.persist(transaction);

         coveragePackageArtifact.addRelation(CoreRelationTypes.SupportingInfo_SupportingInfo, codeTeamArt);
         coveragePackageArtifact.persist(transaction);

         actionArt =
            cmService.createPcr("Test PCR 1001", "Do test for PCR 33201", "Improvement", "1", null,
               Arrays.asList("SAW Test"));
         actionArt.persist(transaction);
         Artifact testTeamArt = actionArt.getRelatedArtifact(CoverageRelationTypes.ActionToWorkflow_WorkFlow);
         testTeamArt.persist(transaction);

         coveragePackageArtifact.addRelation(CoreRelationTypes.SupportingInfo_SupportingInfo, testTeamArt);
         coveragePackageArtifact.persist(transaction);

         transaction.execute();

         CoverageEditor.open(new CoverageEditorInput(coveragePackageArtifact.getName(), coveragePackageArtifact, null,
            false));
      }

   }
}
