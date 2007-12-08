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
package org.eclipse.osee.framework.ui.skynet.search.page.actions;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.osee.framework.jdk.core.type.TreeObject;
import org.eclipse.osee.framework.ui.skynet.search.page.ArtifactSearchComposite;
import org.eclipse.osee.framework.ui.skynet.search.page.data.ArtifactTypeNode;
import org.eclipse.osee.framework.ui.skynet.search.page.data.AttributeTypeNode;
import org.eclipse.osee.framework.ui.skynet.search.page.data.RelationTypeNode;

public class TreeNodeCheckStateListener implements ICheckStateListener {

   private ArtifactSearchComposite parentWindow;

   public TreeNodeCheckStateListener(ArtifactSearchComposite parentWindow) {
      this.parentWindow = parentWindow;
      this.parentWindow.getTreeWidget().addCheckBoxStateListener(this);
   }

   public void checkStateChanged(CheckStateChangedEvent event) {
      TreeObject treeObject = (TreeObject) event.getElement();
      treeObject.setChecked(event.getChecked());
      if (treeObject instanceof RelationTypeNode) {
         parentWindow.getTreeWidget().getInputManager().inputChanged();
         //         GetArtifactTypesFromRelationJob job =
         //            new GetArtifactTypesFromRelationJob("Populate ArtifactTypes From Relation Link Type", 
         //                  parentWindow, (RelationTypeNode) treeObject, event.getChecked());
         //         GetArtifactTypesFromRelationJob.scheduleJob(job);

      } else if (treeObject instanceof ArtifactTypeNode) {
         ArtifactTypeNode artifactTypeNode = (ArtifactTypeNode) treeObject;
         if (artifactTypeNode.hasChildren()) {
            TreeObject[] children = artifactTypeNode.getChildren();
            for (TreeObject child : children) {
               if (child instanceof AttributeTypeNode) {
                  parentWindow.getTreeWidget().getTreeViewer().setChecked(child, event.getChecked());
               }
            }
         }
         parentWindow.getTreeWidget().refresh();
      }
   }
}
