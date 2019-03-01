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
package org.eclipse.osee.ats.ide.actions;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.ide.AtsImage;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.CheckBoxDialog;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class RevertDuplicateAtsTransitionsAction extends Action {

   public RevertDuplicateAtsTransitionsAction() {
      this("Revert Duplicate ATS Transitions");
   }

   public RevertDuplicateAtsTransitionsAction(String name) {
      super(name);
      setToolTipText(getText());
   }

   @Override
   public void run() {
      final String title = getText();
      CheckBoxDialog dialog = new CheckBoxDialog(title, "Fix all duplicate transitions?", "Persist");
      if (dialog.open() == 0) {

         final boolean persist = dialog.isChecked();
         AbstractOperation operation =
            new org.eclipse.osee.framework.core.operation.AbstractOperation(title, Activator.PLUGIN_ID) {

               @Override
               protected void doWork(IProgressMonitor monitor) throws Exception {
                  List<ArtifactId> artIds = getArtIdsWithDuplicateTransitions();
                  XResultData results = new XResultData();
                  SkynetTransaction trans =
                     TransactionManager.createTransaction(AtsClientService.get().getAtsBranch(), getName());

                  boolean changed = false;
                  for (Artifact art : ArtifactQuery.getArtifactListFrom(artIds,
                     AtsClientService.get().getAtsBranch())) {
                     results.logf("\n\nReverting transition for %s\n\n", art.toStringWithId());
                     if (RevertDuplicateAtsTransitionByIdAction.revertTransition(art, results, persist, trans)) {
                        changed = true;
                     } else {
                        results.log("Nothing to change.");
                     }
                  }
                  if (persist && !results.isErrors() && changed) {
                     trans.execute();
                  }
                  if (!changed) {
                     results.error("Nothing changed");
                  }
                  XResultDataUI.report(results, getName());
                  if (results.isErrors()) {
                     AWorkbench.popup(
                        "Errors found, search Error in results.  Restart before re-running if you persisted");
                  }
               }
            };
         Operations.executeAsJob(operation, true);
      }
   }

   protected List<ArtifactId> getArtIdsWithDuplicateTransitions() {
      List<ArtifactId> artIds = new LinkedList<>();
      for (AttributeTypeId attrType : Arrays.asList(AtsAttributeTypes.CompletedDate, AtsAttributeTypes.CancelledDate)) {
         for (IAtsWorkItem workItem : AtsClientService.get().getQueryService().getWorkItemsFromQuery(
            DUPLICATE_TRANSITION_QUERY, AtsClientService.get().getAtsBranch().getId(), attrType.getId())) {
            artIds.add(ArtifactId.valueOf(workItem.getId()));
         }
      }
      return artIds;
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(AtsImage.TASK);
   }

   private static String DUPLICATE_TRANSITION_QUERY = //
      "select art_id, transaction_id " + //
         "from " + //
         "    (SELECT" + //
         "        art.art_id, attr.attr_id, attr.value, attr.gamma_id, txs.tx_current, " + //
         "      txs.transaction_id, count(distinct attr.attr_id) over (partition by art.art_id) attr_id_cnt" + //
         "     FROM" + //
         "        osee_artifact art, osee_attribute attr, osee_txs txs" + //
         "     WHERE" + //
         "       txs.branch_id = ? and txs.gamma_id = attr.gamma_id and art.art_id = " + //
         "     attr.art_id and attr.attr_type_id = ? and " + //
         "       txs.tx_current = 1) t1 " + //
         "where t1.attr_id_cnt > 1";

}
