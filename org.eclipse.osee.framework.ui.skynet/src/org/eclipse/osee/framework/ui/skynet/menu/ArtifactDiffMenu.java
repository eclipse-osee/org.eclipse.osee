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

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.WordArtifact;
import org.eclipse.osee.framework.skynet.core.revision.TransactionData;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;
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
public class ArtifactDiffMenu {

   public enum DiffTypes {
      CONFLICT, PARENT
   }

   private static final RendererManager rendererManager = RendererManager.getInstance();
   private static final TransactionIdManager transactionIdManager = TransactionIdManager.getInstance();

   private static final String DIFF_ARTIFACT = "DIFF_ARTIFACT";
   private static boolean validSelection;
   private static Object firstSelection;
   private static Object secondSelection;

   public static void createDiffMenuItem(Menu parentMenu, final Viewer viewer, String subMenuText, final DiffTypes diffType) {
      final MenuItem diffMenuItem = new MenuItem(parentMenu, SWT.CASCADE);
      diffMenuItem.setText(subMenuText);

      final Menu submenu = new Menu(diffMenuItem);
      diffMenuItem.setMenu(submenu);

      final MenuItem stdDiffMenuItem = new MenuItem(submenu, SWT.PUSH);
      stdDiffMenuItem.setText("Standard Diff");

      stdDiffMenuItem.addSelectionListener(new SelectionAdapter() {

         public void widgetSelected(SelectionEvent ev) {
            try {
               processSelectedArtifacts(null, viewer, diffType);
            } catch (Exception ex) {
            }
         }
      });

      final MenuItem diffArtifactMenuItem = new MenuItem(submenu, SWT.PUSH);
      diffArtifactMenuItem.setText("Diff Artifact");

      diffArtifactMenuItem.addSelectionListener(new SelectionAdapter() {

         public void widgetSelected(SelectionEvent ev) {
            try {
               processSelectedArtifacts(DIFF_ARTIFACT, viewer, diffType);
            } catch (Exception ex) {
            }
         }
      });

      parentMenu.addMenuListener(new MenuListener() {

         public void menuHidden(MenuEvent e) {
         }

         public void menuShown(MenuEvent e) {
            validSelection = false;

            IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();

            if (!selection.isEmpty()) {
               if (selection.size() == 2) {
                  validSelection = validateTransactionData(viewer, selection);
               }
            }
            diffMenuItem.setEnabled(validSelection);
         }
      });
   }

   private static boolean validateTransactionData(Viewer viewer, IStructuredSelection selection) {
      boolean valid = false;
      Object[] selections = selection.toArray();

      if (selections[1] instanceof TransactionData && selections[0] instanceof TransactionData) {
         Artifact selectedArtifact = (Artifact) viewer.getInput();
         valid = (selectedArtifact instanceof WordArtifact);
      }
      return valid;
   }

   private static void processSelectedArtifacts(String option, Viewer viewer, DiffTypes type) throws Exception {
      IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
      WordArtifact firstArtifact = null;
      WordArtifact secondArtifact = null;

      if (selection.size() == 2) {
         Object[] selections = selection.toArray();
         firstSelection = selections[1];
         secondSelection = selections[0];

         if (firstSelection instanceof TransactionData && secondSelection instanceof TransactionData) {
            TransactionData firstTransactionData = (TransactionData) firstSelection;
            TransactionData secondTransactionData = (TransactionData) secondSelection;

            firstArtifact =
                  (WordArtifact) ArtifactPersistenceManager.getInstance().getArtifactFromId(
                        firstTransactionData.getAssociatedArtId(), firstTransactionData.getTransactionId());
            secondArtifact =
                  (WordArtifact) ArtifactPersistenceManager.getInstance().getArtifactFromId(
                        secondTransactionData.getAssociatedArtId(), secondTransactionData.getTransactionId());
         }
      }
      rendererManager.compareInJob(firstArtifact, secondArtifact, option);
   }
}
