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

import static org.eclipse.osee.framework.skynet.core.artifact.DeletionFlag.INCLUDE_DELETED;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.TransactionDelta;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.change.ArtifactDelta;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.ui.skynet.render.FileSystemRenderer;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

/**
 * @author Jeff C. Phillips
 */
public final class ArtifactDiffMenu {

   private ArtifactDiffMenu() {
   }

   public static void createDiffMenuItem(Menu parentMenu, Viewer viewer, String subMenuText) {
      MenuItem diffMenuItem = new MenuItem(parentMenu, SWT.CASCADE);
      diffMenuItem.setText(subMenuText);
      diffMenuItem.addSelectionListener(new SelectionListener(viewer));
      parentMenu.addMenuListener(new CompareMenuListener(viewer, diffMenuItem));
   }

   private static final class CompareMenuListener implements MenuListener {
      private final Viewer viewer;
      private final MenuItem diffMenuItem;

      public CompareMenuListener(Viewer viewer, MenuItem diffMenuItem) {
         super();
         this.diffMenuItem = diffMenuItem;
         this.viewer = viewer;
      }

      @Override
      public void menuHidden(MenuEvent e) {
      }

      @Override
      public void menuShown(MenuEvent e) {
         boolean isValidSelection = false;

         IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
         if (selection.size() == 2) {
            isValidSelection = true;
            for (Object object : selection.toArray()) {
               isValidSelection &= isComparable(object);
            }
         }
         diffMenuItem.setEnabled(isValidSelection);
      }

      private boolean isComparable(Object object) {
         boolean isValidSelection = false;
         if (object instanceof Change) {
            Change change = (Change) object;
            try {
               Artifact toCheck = change.getChangeArtifact();
               if (toCheck != null) {
                  FileSystemRenderer renderer = RendererManager.getBestFileRenderer(PresentationType.DIFF, toCheck);
                  isValidSelection = renderer.supportsCompare();
               }
            } catch (OseeCoreException ex) {
            }
         }
         return isValidSelection;
      }
   }

   private static final class SelectionListener extends SelectionAdapter {
      private final Viewer viewer;

      public SelectionListener(Viewer viewer) {
         super();
         this.viewer = viewer;
      }

      @Override
      public void widgetSelected(SelectionEvent ev) {
         try {
            processSelectedArtifacts();
         } catch (Exception ex) {
         }
      }

      private void processSelectedArtifacts() throws Exception {
         IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
         if (selection.size() == 2) {
            Object[] selections = selection.toArray();
            Object selectionA = selections[0];
            Object selectionB = selections[1];
            ArtifactDelta artifactDelta = asArtifactDelta(selectionA, selectionB);
            if (artifactDelta != null) {
               RendererManager.diffInJob(artifactDelta);
            }
         }
      }

      private ArtifactDelta asArtifactDelta(Object selectionA, Object selectionB) throws OseeCoreException {
         ArtifactDelta toReturn = null;
         if (selectionA instanceof Change && selectionB instanceof Change) {
            Change changeA = (Change) selectionA;
            Change changeB = (Change) selectionB;

            Conditions.checkExpressionFailOnTrue(changeA.getArtId() != changeB.getArtId(),
               "Change art ids don't match [%s:%s]", changeA.getArtId(), changeB.getArtId());

            int artId = changeA.getArtId();
            TransactionDelta txDelta = asTxDelta(changeA, changeB);

            Artifact startArtifact =
               ArtifactQuery.getHistoricalArtifactFromId(artId, txDelta.getStartTx(), INCLUDE_DELETED);
            Artifact endArtifact =
               ArtifactQuery.getHistoricalArtifactFromId(artId, txDelta.getEndTx(), INCLUDE_DELETED);
            toReturn = new ArtifactDelta(txDelta, startArtifact, endArtifact);
         }
         return toReturn;
      }

      private TransactionDelta asTxDelta(Change first, Change second) throws OseeCoreException {
         TransactionRecord startTx = first.getChangeArtifact().getTransactionRecord();
         TransactionRecord endTx = second.getChangeArtifact().getTransactionRecord();
         if (startTx.getId() > endTx.getId()) {
            startTx = second.getChangeArtifact().getTransactionRecord();
            endTx = first.getChangeArtifact().getTransactionRecord();
         }
         return new TransactionDelta(startTx, endTx);
      }

   }
}
