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
package org.eclipse.osee.framework.ui.skynet.search.page.widget;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.jdk.core.type.InputManager;
import org.eclipse.osee.framework.jdk.core.type.TreeObject;
import org.eclipse.osee.framework.jdk.core.type.TreeParent;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.search.page.data.ArtifactTypeNode;
import org.eclipse.osee.framework.ui.skynet.search.page.data.AttributeTypeNode;
import org.eclipse.osee.framework.ui.skynet.search.page.data.RelationTypeNode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class ArtifactTreeSearchWidget extends Composite implements IViewer {

   private CheckboxTreeViewer treeViewer;
   private InputManager<ArtifactTypeNode> inputManager;

   private static final Image ARTIFACT_IMAGE = SkynetGuiPlugin.getInstance().getImage("laser_16_16.gif");
   private static final Image RELATION_IMAGE = SkynetGuiPlugin.getInstance().getImage("relate.gif");

   public ArtifactTreeSearchWidget(Composite parent, int style) {
      super(parent, style);
      createControl();
   }

   private void createControl() {
      this.setLayout(new GridLayout());
      GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
      gd.widthHint = 300;
      this.setLayoutData(gd);
      createTreeArea(this);
   }

   private void createTreeArea(Composite parent) {
      inputManager = new InputManager<ArtifactTypeNode>();
      treeViewer =
            new CheckboxTreeViewer(parent,
                  SWT.MULTI | SWT.CHECK | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
      treeViewer.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      treeViewer.setContentProvider(new TreeContentProvider());
      treeViewer.setLabelProvider(new TreeLableProvider());
      treeViewer.setInput(inputManager.getInputList());
      treeViewer.getTree().setBackground(Display.getDefault().getSystemColor(SWT.COLOR_GRAY));
   }

   public CheckboxTreeViewer getTreeViewer() {
      return treeViewer;
   }

   public void addSelectionListener(ISelectionChangedListener listener) {
      treeViewer.addSelectionChangedListener(listener);
   }

   synchronized public void setTreeInput(List<ArtifactTypeNode> inputList) {
      treeViewer.setInput(inputList);
   }

   public void addCheckBoxStateListener(ICheckStateListener listener) {
      treeViewer.addCheckStateListener(listener);
   }

   public void dispose() {
      this.treeViewer.getTree().dispose();
   }

   public void refresh() {
      this.treeViewer.refresh();
   };

   public InputManager<ArtifactTypeNode> getInputManager() {
      return inputManager;
   }

   private class TreeLableProvider extends LabelProvider {

      public Image getImage(Object obj) {
         Image toReturn = null;
         if (obj instanceof ArtifactTypeNode) {
            toReturn = ARTIFACT_IMAGE;
         } else if (obj instanceof AttributeTypeNode) {
            // toReturn = ATTRIBUTE_IMAGE;
         } else if (obj instanceof RelationTypeNode) {
            toReturn = RELATION_IMAGE;
         }
         return toReturn;
      }

      public String getText(Object obj) {
         return obj.toString();
      }
   };

   private class TreeContentProvider implements ITreeContentProvider {

      public void dispose() {

      }

      public Object[] getChildren(Object parentElement) {
         if (parentElement != null && parentElement instanceof TreeParent) {
            TreeParent parent = (TreeParent) parentElement;
            if (parent.hasChildren()) {
               return parent.getChildren();
            }
         }
         return new Object[0];
      }

      @SuppressWarnings("unchecked")
      public Object[] getElements(Object inputElement) {
         if (inputElement != null && inputElement instanceof ArrayList) {
            ArrayList elementArray = (ArrayList) inputElement;
            return elementArray.toArray();
         }
         return new Object[0];
      }

      public Object getParent(Object element) {
         if (element != null && element instanceof TreeObject) {
            TreeObject child = (TreeObject) element;
            return child.getParent();
         }
         return new Object();
      }

      public boolean hasChildren(Object element) {
         if (element instanceof TreeParent) {
            TreeParent parent = (TreeParent) element;
            return parent.hasChildren();
         }
         return false;
      }

      public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

      }
   }
}
