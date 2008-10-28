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
package org.eclipse.osee.framework.ui.skynet.widgets.xcommit;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 */
public class CommitXViewer extends XViewer {

   private final XCommitViewer xCommitViewer;
   private Branch workingBranch;

   public CommitXViewer(Composite parent, int style, XCommitViewer xRoleViewer) {
      super(parent, style, new CommitXViewerFactory());
      this.xCommitViewer = xRoleViewer;
   }

   @Override
   protected void createSupportWidgets(Composite parent) {
      super.createSupportWidgets(parent);
      createMenuActions();
   }

   Action openMergeViewAction;

   public void createMenuActions() {
      MenuManager mm = getMenuManager();
      mm.createContextMenu(getControl());
      mm.addMenuListener(new IMenuListener() {
         public void menuAboutToShow(IMenuManager manager) {
            updateMenuActions();
         }
      });

      openMergeViewAction = new Action("Open Merge View", Action.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            AWorkbench.popup("ERROR", "Not implemented yet");
         }
      };
   }

   public void updateEditMenuActions() {
      MenuManager mm = getMenuManager();

      // EDIT MENU BLOCK
      mm.insertBefore(MENU_GROUP_PRE, openMergeViewAction);
      openMergeViewAction.setEnabled(getSelectedBranches().size() == 1 && getSelectedBranches().iterator().next().isBaselineBranch());

   }

   public void updateMenuActions() {
      MenuManager mm = getMenuManager();

      updateEditMenuActions();

      mm.insertBefore(MENU_GROUP_PRE, new Separator());
   }

   public void setWorkingBranch(Branch workingBranch) throws OseeCoreException {
      this.workingBranch = workingBranch;
      Set<Branch> branches = new HashSet<Branch>();
      branches.add(workingBranch.getParentBranch());
      setInput(branches.toArray());
      expandAll();
   }

   /**
    * Release resources
    */
   @Override
   public void dispose() {
      getLabelProvider().dispose();
   }

   public ArrayList<Branch> getSelectedBranches() {
      ArrayList<Branch> arts = new ArrayList<Branch>();
      TreeItem items[] = getTree().getSelection();
      if (items.length > 0) for (TreeItem item : items)
         arts.add((Branch) item.getData());
      return arts;
   }

   /**
    * @return the xUserRoleViewer
    */
   public XCommitViewer getXUserRoleViewer() {
      return xCommitViewer;
   }

   /**
    * @return the workingBranch
    */
   public Branch getWorkingBranch() {
      return workingBranch;
   }

}
