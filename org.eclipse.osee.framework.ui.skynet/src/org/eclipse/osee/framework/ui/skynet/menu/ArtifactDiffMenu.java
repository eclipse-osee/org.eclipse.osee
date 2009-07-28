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
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
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
public class ArtifactDiffMenu {

   public enum DiffTypes {
      CONFLICT, PARENT
   }

   private static final String DIFF_ARTIFACT = "DIFF_ARTIFACT";
   private static boolean validSelection;
   private static Object firstSelection;
   private static Object secondSelection;

   public static void createDiffMenuItem(Menu parentMenu, final Viewer viewer, String subMenuText, final DiffTypes diffType) {
      final MenuItem diffMenuItem = new MenuItem(parentMenu, SWT.CASCADE);
      diffMenuItem.setText(subMenuText);
      diffMenuItem.addSelectionListener(new SelectionAdapter() {

         @Override
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

      if (selections[1] instanceof Change && selections[0] instanceof Change) {
         try {
            valid =
                  (RendererManager.getBestFileRenderer(PresentationType.DIFF,
                        ((Change) selections[0]).getArtifact()).supportsCompare());
         } catch (OseeCoreException ex) {
         }
      }
      return valid;
   }

   private static void processSelectedArtifacts(String option, Viewer viewer, DiffTypes type) throws Exception {
      IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
      Artifact newerArtifact = null;
      Artifact baselineArtifact = null;

      if (selection.size() == 2) {
         Object[] selections = selection.toArray();
         firstSelection = selections[0];
         secondSelection = selections[1];

         if (firstSelection instanceof Change && secondSelection instanceof Change) {

            Change firstChange = (Change) firstSelection;
            Change secondChange = (Change)secondSelection;
            TransactionId firstTransactionId = firstChange.getFromTransactionId();
            TransactionId secondTransactionId = secondChange.getFromTransactionId();

            if (firstTransactionId.getTransactionNumber() < secondTransactionId.getTransactionNumber()) {
               firstTransactionId = secondChange.getFromTransactionId();
               secondTransactionId = firstChange.getFromTransactionId();
            }
            newerArtifact =
                  ArtifactQuery.getHistoricalArtifactFromId(firstChange.getArtId(),
                        firstTransactionId, true);
            baselineArtifact =
                  ArtifactQuery.getHistoricalArtifactFromId(firstChange.getArtId(),
                        secondTransactionId, true);
         }
      }
      RendererManager.diffInJob(baselineArtifact, newerArtifact);
   }
}
