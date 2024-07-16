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

package org.eclipse.osee.ats.ide.actions;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryCheckDialog;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class RevertDuplicateAtsTransitionByIdAction extends Action {

   public RevertDuplicateAtsTransitionByIdAction() {
      super("Revert Duplicate ATS Transition by ID");
      setToolTipText(getText());
   }

   @Override
   public void run() {
      final String title = getText();
      EntryCheckDialog dialog = new EntryCheckDialog(title, "Enter ATS Ids", "Persist");
      if (dialog.open() == Window.OK) {

         final boolean persist = dialog.isChecked();
         AbstractOperation operation =
            new org.eclipse.osee.framework.core.operation.AbstractOperation(title, Activator.PLUGIN_ID) {

               @Override
               protected void doWork(IProgressMonitor monitor) throws Exception {
                  List<String> atsIds = new LinkedList<>();
                  List<ArtifactId> artIds = new LinkedList<>();
                  for (String id : dialog.getEntry().split(",")) {
                     id = id.replaceAll(" ", "");
                     if (Strings.isNumeric(id)) {
                        artIds.add(ArtifactId.valueOf(id));
                     } else {
                        atsIds.add(id);
                     }
                  }
                  XResultData results = new XResultData();
                  SkynetTransaction trans =
                     TransactionManager.createTransaction(AtsApiService.get().getAtsBranch(), getName());

                  boolean changed = false;
                  for (Artifact art : ArtifactQuery.getArtifactListFrom(artIds, AtsApiService.get().getAtsBranch())) {
                     results.logf("\n\nReverting transition for %s\n\n", art.toStringWithId());
                     if (revertTransition(art, results, persist, trans)) {
                        changed = true;
                     } else {
                        results.log("Nothing to change.");
                     }
                  }

                  if (!atsIds.isEmpty()) {
                     for (Artifact art : ArtifactQuery.getArtifactListFromAttributeValues(AtsAttributeTypes.AtsId,
                        atsIds, AtsApiService.get().getAtsBranch(), 50)) {
                        results.logf("\n\nReverting transition for %s\n\n", art.toStringWithId());
                        if (revertTransition(art, results, persist, trans)) {
                           changed = true;
                        } else {
                           results.log("Nothing to change");
                        }
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

   protected static boolean revertTransition(Artifact art, XResultData results, boolean persist, SkynetTransaction persistTransaction) {
      List<Attribute<Object>> attributes = null;
      for (AttributeTypeId attrType : Arrays.asList(AtsAttributeTypes.CompletedDate, AtsAttributeTypes.CancelledDate)) {
         attributes = art.getAttributes(attrType);
         if (attributes.size() > 1) {
            break;
         }
      }
      if (attributes == null || attributes.isEmpty()) {
         results.errorf("Unable to find duplicate completed/cancelled dates for artifact %s", art.toStringWithId());
         return false;
      }
      TransactionId earlyTrans, lateTrans, trans1 = null, trans2 = null;
      for (Attribute<Object> attr : attributes) {
         TransactionId transId = TransactionManager.getTransaction(art.getBranch(), attr);
         if (trans1 == null) {
            trans1 = transId;
         } else {
            trans2 = transId;
         }
      }
      if (trans1 == null || trans2 == null) {
         return false;
      }
      earlyTrans = trans1.getId() < trans2.getId() ? trans1 : trans2;
      lateTrans = trans2.getId() < trans1.getId() ? trans1 : trans2;
      Conditions.assertNotNull(earlyTrans, "Can not find early transaction");
      Conditions.assertNotNull(lateTrans, "Can not find late transaction");

      return TransactionManager.revertArtifactFromTransaction(art, lateTrans, results, persist, persistTransaction);

   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(AtsImage.TASK);
   }

}
