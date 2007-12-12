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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.access.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.revision.ArtifactChange;
import org.eclipse.osee.framework.skynet.core.revision.TransactionData;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.swt.ITreeNode;
import org.eclipse.osee.framework.ui.swt.TreeViewerUtility;
import org.eclipse.search.ui.text.Match;
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
public class ArtifactPreviewMenu {

   private static final AccessControlManager accessManager = AccessControlManager.getInstance();
   private static final RendererManager rendererManager = RendererManager.getInstance();
   private static final SkynetAuthentication skynetAuth = SkynetAuthentication.getInstance();
   private static final TransactionIdManager transactionIdManager = TransactionIdManager.getInstance();

   private static final String PREVIEW_WITH_RECURSE = "PREVIEW_WITH_RECURSE";
   private static final String PREVIEW_ARTIFACT = "PREVIEW_ARTIFACT";

   public static void createPreviewMenuItem(Menu parentMenu, final Viewer viewer) {
      final MenuItem previewMenuItem = new MenuItem(parentMenu, SWT.CASCADE);
      previewMenuItem.setText("&Preview");

      final Menu submenu = new Menu(previewMenuItem);
      previewMenuItem.setMenu(submenu);

      final MenuItem previewArtifact = new MenuItem(submenu, SWT.PUSH);
      previewArtifact.setText("Preview Artifact");

      previewArtifact.addSelectionListener(new SelectionAdapter() {

         public void widgetSelected(SelectionEvent ev) {
            rendererManager.previewInJob(getSelectedArtifacts(viewer), PREVIEW_ARTIFACT);
         }
      });

      final MenuItem previewWithChildRecursionItem = new MenuItem(submenu, SWT.PUSH);
      previewWithChildRecursionItem.setText("Preview with child recursion");

      previewWithChildRecursionItem.addSelectionListener(new SelectionAdapter() {

         public void widgetSelected(SelectionEvent ev) {
            rendererManager.previewInJob(getSelectedArtifacts(viewer), PREVIEW_WITH_RECURSE);
         }
      });

      parentMenu.addMenuListener(new MenuListener() {

         public void menuHidden(MenuEvent e) {
         }

         public void menuShown(MenuEvent e) {
            IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
            Iterator<?> iterator = selection.iterator();
            boolean permitted = true;
            Artifact artifact = null;

            try {
               while (iterator.hasNext()) {
                  Object object = iterator.next();

                  if (object instanceof Artifact) {
                     artifact = (Artifact) object;
                  } else if (object instanceof Match) {
                     artifact = (Artifact) ((Match) object).getElement();
                  } else if (object instanceof ITreeNode && ((ITreeNode) object).getBackingData() instanceof ArtifactChange) {
                     artifact = ((ArtifactChange) ((ITreeNode) object).getBackingData()).getArtifact();
                  } else if (object instanceof TransactionData) {
                     TransactionData firstTransactionData = (TransactionData) object;

                     TransactionId firstTransactionId =
                           transactionIdManager.getNonEditableTransactionId(firstTransactionData.getTransactionNumber());
                     artifact =
                           ArtifactPersistenceManager.getInstance().getArtifactFromId(
                                 firstTransactionData.getAssociatedArtId(), firstTransactionId);
                  }

                  if (artifact != null) {
                     permitted &=
                           accessManager.checkObjectPermission(skynetAuth.getAuthenticatedUser(), artifact,
                                 PermissionEnum.READ);
                  }

                  previewMenuItem.setEnabled(permitted);
               }
            } catch (Exception ex) {
               OSEELog.logException(ArtifactPreviewMenu.class, ex, true);
               previewMenuItem.setEnabled(false);
            }
         }

      });
   }

   private static List<Artifact> getSelectedArtifacts(Viewer viewer) {
      LinkedList<Artifact> selectedItems = new LinkedList<Artifact>();
      IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();

      if (!selection.isEmpty()) {
         // preOrder can only done for TreeViewers
         if (viewer instanceof TreeViewer) {
            Object firstElement = selection.getFirstElement();

            try {
               // This to support the changeReportView
               if (firstElement instanceof ITreeNode && ((ITreeNode) firstElement).getBackingData() instanceof ArtifactChange) {
                  ArtifactChange artifactChange = (ArtifactChange) ((ITreeNode) firstElement).getBackingData();
                  selectedItems.add(artifactChange.getArtifact());
               }
               // Resource History
               else if (firstElement instanceof TransactionData) {
                  TransactionData firstTransactionData = (TransactionData) firstElement;

                  TransactionId firstTransactionId =
                        transactionIdManager.getNonEditableTransactionId(firstTransactionData.getTransactionNumber());
                  selectedItems.add(ArtifactPersistenceManager.getInstance().getArtifactFromId(
                        firstTransactionData.getAssociatedArtId(), firstTransactionId));
               } else {
                  TreeViewerUtility.getPreorderSelection((TreeViewer) viewer, selectedItems);
               }
            } catch (Exception ex) {
               OSEELog.logException(ArtifactPreviewMenu.class, ex, true);
            }
         } else {
            for (Object object : selection.toArray()) {
               if (object instanceof Artifact) {
                  selectedItems.add((Artifact) object);
               } else if (object instanceof Match) {
                  selectedItems.add((Artifact) ((Match) object).getElement());
               }
            }
         }
      }
      return selectedItems;
   }
}
