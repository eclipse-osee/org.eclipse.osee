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
package org.eclipse.osee.framework.ui.skynet.commandHandlers.branch;

import java.util.List;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osee.framework.access.AccessControlManager;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.ui.plugin.util.CommandHandler;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.Handlers;
import org.eclipse.osee.framework.ui.skynet.widgets.xBranch.BranchXViewerFactory;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Jeff C. Phillips
 * @author Paul K. Waldfogel
 */
public class RenameBranchHandler extends CommandHandler {

   private int findColumnIndex(TreeViewer treeViewer, String columnName) {
      if (treeViewer != null && Widgets.isAccessible(treeViewer.getTree())) {
         TreeColumn[] columns = treeViewer.getTree().getColumns();
         for (int index = 0; index < columns.length; index++) {
            TreeColumn column = columns[index];
            if (columnName.equalsIgnoreCase(column.getText())) {
               return index;
            }
         }
      }
      return 0;
   }

   @Override
   public Object executeWithException(ExecutionEvent event, IStructuredSelection selection) {
      ISelectionProvider selectionProvider = getSelectionProvider();
      if (selectionProvider instanceof TreeViewer) {
         final TreeViewer treeViewer = (TreeViewer) selectionProvider;
         Tree tree = treeViewer.getTree();

         final IOseeBranch selectedBranch = Handlers.getBranchesFromStructuredSelection(selection).iterator().next();
         TreeItem[] myTreeItemsSelected = tree.getSelection();

         if (myTreeItemsSelected.length != 1) {
            return null;
         }
         TreeEditor myTreeEditor = new TreeEditor(tree);
         myTreeEditor.horizontalAlignment = SWT.LEFT;
         myTreeEditor.grabHorizontal = true;
         myTreeEditor.minimumWidth = 50;
         myTreeEditor.setColumn(findColumnIndex(treeViewer, BranchXViewerFactory.branchName.getName()));

         final TreeItem myTreeItem = myTreeItemsSelected[0];

         Control oldEditor = myTreeEditor.getEditor();
         if (oldEditor != null) {
            oldEditor.dispose();
         }
         final Text textBeingRenamed = new Text(tree, SWT.BORDER);
         textBeingRenamed.setText(selectedBranch.getName());

         textBeingRenamed.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
               BranchManager.setName(selectedBranch, textBeingRenamed.getText());
               treeViewer.refresh();
               textBeingRenamed.dispose();
            }

            @Override
            public void focusGained(FocusEvent e) {
               // do nothing
            }
         });
         textBeingRenamed.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
               if (e.character == SWT.CR) {
                  BranchManager.setName(selectedBranch, textBeingRenamed.getText());
                  treeViewer.refresh();
                  textBeingRenamed.dispose();
               } else if (e.keyCode == SWT.ESC) {
                  textBeingRenamed.dispose();
               }
            }
         });
         textBeingRenamed.selectAll();
         textBeingRenamed.setFocus();
         myTreeEditor.setEditor(textBeingRenamed, myTreeItem);
      }
      return null;
   }

   @Override
   public boolean isEnabledWithException(IStructuredSelection structuredSelection)  {
      List<IOseeBranch> branches = Handlers.getBranchesFromStructuredSelection(structuredSelection);
      if (branches.size() != 1) {
         return false;
      }
      IOseeBranch branch = branches.get(0);
      return AccessControlManager.isOseeAdmin() || BranchManager.getType(
         branch).isWorkingBranch() || BranchManager.getBaseTransaction(branch).getAuthor().equals(
            UserManager.getUser());
   }

}
