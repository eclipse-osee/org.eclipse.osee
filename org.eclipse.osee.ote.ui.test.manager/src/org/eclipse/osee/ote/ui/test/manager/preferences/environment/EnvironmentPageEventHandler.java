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
package org.eclipse.osee.ote.ui.test.manager.preferences.environment;

import java.util.ArrayList;
import java.util.Iterator;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.jdk.core.type.TreeObject;
import org.eclipse.osee.framework.jdk.core.type.TreeParent;
import org.eclipse.osee.ote.ui.test.manager.util.EnvVariableDetailsDialogHelper;
import org.eclipse.osee.ote.ui.test.manager.util.EnvVariableDialogHelper;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;


/**
 * @author Roberto E. Escobar
 */
public class EnvironmentPageEventHandler {

   private EnvironmentPreferenceNode currentSelection;
   private EnvironmentPageDataViewer environmentPageDataViewer;
   private ArrayList<EnvironmentPreferenceNode> treeInputList;
   private CheckboxTreeViewer treeViewer;

   public EnvironmentPageEventHandler(Composite parent, CheckboxTreeViewer treeViewer,
         ArrayList<EnvironmentPreferenceNode> treeInputList) {
      this.treeViewer = treeViewer;
      this.treeInputList = treeInputList;
      this.environmentPageDataViewer = new EnvironmentPageDataViewer(parent);
   }

   public void editEnvVariable(EnvironmentPreferenceNode node) {
      EnvVariableDetailsDialogHelper selection = new EnvVariableDetailsDialogHelper(node.getEnvName(), node.getValue());
      Display.getDefault().syncExec(selection);
      if (selection.getResult() != Window.CANCEL) {
         node.setValue(selection.getSelection());
         environmentPageDataViewer.update();
      }
   }

   public void handleAddEnvironmentVariableEvent() {
      EnvVariableDialogHelper selection = new EnvVariableDialogHelper();
      Display.getDefault().syncExec(selection);
      if (selection.getResult() != Window.CANCEL) {
         this.addEnvironmentVariable(selection.getSelection());
         treeViewer.refresh();
      }
   }

   public void handleCheckStateChangeEvent(CheckStateChangedEvent event) {
      Object obj = event.getElement();
      if (obj != null) {
         EnvironmentPreferenceNode tempSelection = null;

         if (obj instanceof EnvironmentPreferenceNode) {
            tempSelection = (EnvironmentPreferenceNode) obj;
            tempSelection.setChecked(event.getChecked());
            currentSelection = tempSelection;
            environmentPageDataViewer.setNodeToDisplay(currentSelection);
         }
      }
   }

   public void handleEditVariableEvent() {
      ISelection sel = this.treeViewer.getSelection();
      if (!sel.isEmpty()) {
         TreeObject selectedItem = (TreeObject) ((StructuredSelection) sel).getFirstElement();
         if (selectedItem instanceof EnvironmentPreferenceNode) {
            editEnvVariable((EnvironmentPreferenceNode) selectedItem);
         }
      }
   }

   public void handleRemoveSelectedViewEvent() {
      StructuredSelection sel = (StructuredSelection) treeViewer.getSelection();
      if (!sel.isEmpty()) {
         Iterator<?> it = sel.iterator();
         while (it.hasNext()) {
            TreeObject leaf = (TreeObject) it.next();
            if (leaf instanceof TreeParent) {
               treeInputList.remove(leaf);
               environmentPageDataViewer.setNodeToDisplay(null);
            }
            else {
               leaf.getParent().removeChild(leaf);
               environmentPageDataViewer.update();
            }
         }
         treeViewer.refresh();
      }
   }

   public void handleTreeSelectionEvent(SelectionChangedEvent event) {
      ISelection sel = event.getSelection();
      if (!sel.isEmpty()) {
         TreeObject selectedItem = (TreeObject) ((StructuredSelection) sel).getFirstElement();

         EnvironmentPreferenceNode tempSelection = null;

         if (selectedItem instanceof EnvironmentPreferenceNode) {
            tempSelection = (EnvironmentPreferenceNode) selectedItem;
            currentSelection = tempSelection;
            environmentPageDataViewer.setNodeToDisplay(currentSelection);
         }
         treeViewer.refresh();
      }
   }

   private void addEnvironmentVariable(String name) {
      EnvironmentPreferenceNode node = new EnvironmentPreferenceNode(name);
      treeInputList.add(node);
   }
}
