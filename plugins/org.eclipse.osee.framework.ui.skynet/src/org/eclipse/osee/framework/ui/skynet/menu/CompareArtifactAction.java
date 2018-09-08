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
package org.eclipse.osee.framework.ui.skynet.menu;

import java.util.logging.Level;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.model.TransactionDelta;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.change.ArtifactDelta;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.render.IRenderer;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;

/**
 * @author Jeff C. Phillips
 */
public final class CompareArtifactAction extends Action {

   private final ISelectionProvider selectionProvider;

   public CompareArtifactAction(String text, final ISelectionProvider selectionProvider) {
      super(text, IAction.AS_PUSH_BUTTON);
      this.selectionProvider = selectionProvider;
      this.selectionProvider.addSelectionChangedListener(new ISelectionChangedListener() {

         @Override
         public void selectionChanged(SelectionChangedEvent event) {
            IStructuredSelection selection = (IStructuredSelection) selectionProvider.getSelection();
            boolean isValidSelection = selection.size() == 2;
            if (isValidSelection) {
               for (Object object : selection.toArray()) {
                  isValidSelection &= isComparable(object);
               }
            }
            setEnabled(isValidSelection);
         }
      });
   }

   @Override
   public void run() {
      try {
         processSelectedArtifacts();
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   private boolean isComparable(Object object) {
      boolean isValidSelection = false;
      if (object instanceof Change) {
         Change change = (Change) object;
         try {
            Artifact toCheck = change.getChangeArtifact();
            if (toCheck != null) {
               IRenderer renderer = RendererManager.getBestRenderer(PresentationType.DIFF, toCheck);
               isValidSelection = renderer.supportsCompare();
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
      return isValidSelection;
   }

   private void processSelectedArtifacts() throws Exception {
      IStructuredSelection selection = (IStructuredSelection) selectionProvider.getSelection();
      if (selection.size() == 2) {
         Object[] selections = selection.toArray();
         Object selectionA = selections[0];
         Object selectionB = selections[1];
         ArtifactDelta artifactDelta = asArtifactDelta(selectionA, selectionB);
         if (artifactDelta != null) {
            RendererManager.diffInJob(artifactDelta, "Diff_For");
         }
      }
   }

   private ArtifactDelta asArtifactDelta(Object selectionA, Object selectionB) {
      ArtifactDelta toReturn = null;
      if (selectionA instanceof Change && selectionB instanceof Change) {
         Change changeA = (Change) selectionA;
         Change changeB = (Change) selectionB;

         Conditions.checkExpressionFailOnTrue(changeA.getArtId().notEqual(changeB.getArtId()),
            "Change art ids don't match [%s:%s]", changeA.getArtId(), changeB.getArtId());

         ArtifactId artId = changeA.getArtId();
         TransactionDelta txDelta = asTxDelta(changeA, changeB);

         Artifact startArtifact =
            ArtifactQuery.getHistoricalArtifactFromId(artId, txDelta.getStartTx(), DeletionFlag.INCLUDE_DELETED);
         Artifact endArtifact =
            ArtifactQuery.getHistoricalArtifactFromId(artId, txDelta.getEndTx(), DeletionFlag.INCLUDE_DELETED);
         toReturn = new ArtifactDelta(txDelta, startArtifact, endArtifact);
      }
      return toReturn;
   }

   private TransactionDelta asTxDelta(Change first, Change second) {
      TransactionToken startTx = first.getChangeArtifact().getTransaction();
      TransactionToken endTx = second.getChangeArtifact().getTransaction();
      if (endTx.isOlderThan(startTx)) {
         TransactionToken tempTx = startTx;
         startTx = endTx;
         endTx = tempTx;
      }
      return new TransactionDelta(startTx, endTx);
   }
}
