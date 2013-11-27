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
package org.eclipse.osee.coverage.demo.examples.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.coverage.editor.CoverageEditor;
import org.eclipse.osee.coverage.editor.CoverageEditorInput;
import org.eclipse.osee.coverage.model.CoveragePackage;
import org.eclipse.osee.coverage.model.WorkProductAction;
import org.eclipse.osee.coverage.store.CoverageRelationTypes;
import org.eclipse.osee.coverage.store.OseeCoveragePackageStore;
import org.eclipse.osee.coverage.util.CoverageUtil;
import org.eclipse.osee.coverage.util.dialog.CoveragePackageArtifactListDialog;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.cm.IOseeCmService;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

/**
 * @author Donald G. Dunne
 */
public class CreateWorkProductNavigateItemAction extends XNavigateItemAction {

   public CreateWorkProductNavigateItemAction() {
      super(null, "");
   }

   public CreateWorkProductNavigateItemAction(XNavigateItem parent) {
      super(parent, "Create Work Product Action");
   }

   private IOseeCmService getCmService() throws OseeCoreException {
      Bundle bundle = FrameworkUtil.getBundle(getClass());
      Conditions.checkNotNull(bundle, "bundle", "Unable to get IOseeCmService reference");
      BundleContext context = bundle.getBundleContext();
      Conditions.checkNotNull(context, "bundleContext", "Unable to get IOseeCmService reference");
      ServiceReference<IOseeCmService> reference = context.getServiceReference(IOseeCmService.class);
      Conditions.checkNotNull(context, "serviceReference", "Unable to get IOseeCmService reference");
      IOseeCmService service = context.getService(reference);
      Conditions.checkNotNull(context, "service", "Unable to get IOseeCmService reference");
      return service;
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws Exception {
      IOseeCmService cmService = getCmService();
      if (!CoverageUtil.getBranchFromUser(false)) {
         return;
      }
      IOseeBranch branch = CoverageUtil.getBranch();
      CoveragePackageArtifactListDialog dialog =
         new CoveragePackageArtifactListDialog("Open Coverage Package", "Select Coverage Package");
      dialog.setInput(OseeCoveragePackageStore.getCoveragePackageArtifacts(branch));
      if (dialog.open() == 0) {
         Artifact coveragePackageArtifact = (Artifact) dialog.getResult()[0];
         OseeCoveragePackageStore store = new OseeCoveragePackageStore(coveragePackageArtifact);
         CoveragePackage coveragePackage = store.getCoveragePackage();
         List<WorkProductAction> workProductActions = new ArrayList<WorkProductAction>();

         SkynetTransaction transaction = TransactionManager.createTransaction(branch, getName());
         Artifact actionArt =
            cmService.createPcr("Reqts PCR 1001", "Do requirements for PCR 1001", "Improvement", "1", null,
               Arrays.asList("SAW Requirements"));
         actionArt.persist(transaction);
         Artifact reqTeamArt = actionArt.getRelatedArtifact(CoverageRelationTypes.ActionToWorkflow_WorkFlow);
         reqTeamArt.persist(transaction);

         workProductActions.add(new WorkProductAction(reqTeamArt));

         actionArt =
            cmService.createPcr("Code PCR 1001", "Do code for PCR 33201", "Improvement", "1", null,
               Arrays.asList("SAW Code"));
         actionArt.persist(transaction);
         Artifact codeTeamArt = actionArt.getRelatedArtifact(CoverageRelationTypes.ActionToWorkflow_WorkFlow);
         codeTeamArt.persist(transaction);

         workProductActions.add(new WorkProductAction(codeTeamArt));

         actionArt =
            cmService.createPcr("Test PCR 1001", "Do test for PCR 33201", "Improvement", "1", null,
               Arrays.asList("SAW Test"));
         actionArt.persist(transaction);
         Artifact testTeamArt = actionArt.getRelatedArtifact(CoverageRelationTypes.ActionToWorkflow_WorkFlow);
         testTeamArt.persist(transaction);

         workProductActions.add(new WorkProductAction(testTeamArt));

         coveragePackageArtifact.persist(transaction);

         transaction.execute();

         coveragePackage.getWorkProductTaskProvider().addWorkProductAction(workProductActions);
         CoverageEditor.open(new CoverageEditorInput(coveragePackageArtifact.getName(), coveragePackageArtifact, null,
            false));
      }

   }
}
