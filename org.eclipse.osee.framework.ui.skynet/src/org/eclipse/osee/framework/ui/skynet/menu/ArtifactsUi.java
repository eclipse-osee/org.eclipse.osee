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

import java.util.LinkedList;
import java.util.List;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.revision.ArtifactChange;
import org.eclipse.osee.framework.skynet.core.revision.TransactionData;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.swt.ITreeNode;
import org.eclipse.osee.framework.ui.swt.TreeViewerUtility;
import org.eclipse.search.ui.text.Match;

/**
 * Provides convenience methods for displaying artifacts
 * 
 * @author Jeff C. Phillips
 */
public class ArtifactsUi {
   public static List<Artifact> getSelectedArtifacts(Viewer viewer) {
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

                  selectedItems.add(ArtifactPersistenceManager.getInstance().getArtifactFromId(
                        firstTransactionData.getAssociatedArtId(), firstTransactionData.getTransactionId()));
               } else {
                  TreeViewerUtility.getPreorderSelection((TreeViewer) viewer, selectedItems);
               }
            } catch (Exception ex) {
               OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
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
