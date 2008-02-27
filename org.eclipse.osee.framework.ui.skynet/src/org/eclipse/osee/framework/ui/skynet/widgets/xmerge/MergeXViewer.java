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
package org.eclipse.osee.framework.ui.skynet.widgets.xmerge;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.conflict.Conflict;
import org.eclipse.osee.framework.skynet.core.event.BranchEvent;
import org.eclipse.osee.framework.skynet.core.event.SkynetEventManager;
import org.eclipse.osee.framework.ui.plugin.event.Event;
import org.eclipse.osee.framework.ui.plugin.event.IEventReceiver;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.util.SkynetGuiDebug;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.IXViewerFactory;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 */
public class MergeXViewer extends XViewer implements IEventReceiver {

   private static String NAMESPACE = "osee.skynet.gui.MergeXViewer";
   private final XMergeViewer xCommitViewer;
   private Conflict[] conflicts;

   /**
    * @param parent
    * @param style
    */
   public MergeXViewer(Composite parent, int style, XMergeViewer xViewer) {
      this(parent, style, NAMESPACE, new MergeXViewerFactory(), xViewer);
      SkynetEventManager.getInstance().register(BranchEvent.class, this);
   }

   public MergeXViewer(Composite parent, int style, String nameSpace, IXViewerFactory xViewerFactory, XMergeViewer xRoleViewer) {
      super(parent, style, nameSpace, xViewerFactory);
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

   @Override
   public boolean isColumnMultiEditable(TreeColumn treeColumn, Collection<TreeItem> treeItems) {
      MergeColumn aCol = MergeColumn.getAtsXColumn((XViewerColumn) treeColumn.getData());
      XViewerColumn xCol = getCustomize().getCurrentCustData().getColumnData().getXColumn(aCol.getName());
      if (!xCol.isShow() || !aCol.isMultiColumnEditable()) return false;
      return true;
   }

   @Override
   public boolean isColumnMultiEditEnabled() {
      return true;
   }

   public void updateMenuActions() {
      MenuManager mm = getMenuManager();

      updateEditMenuActions();

      mm.insertBefore(MENU_GROUP_PRE, new Separator());
   }

   public void setWorkingBranch(Conflict[] conflicts) throws SQLException {
      this.conflicts = conflicts;
      setInput(conflicts);
   }

   /**
    * Release resources
    */
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

   /*
    * (non-Javadoc)
    * 
    * @see osee.ats.viewer.XViewer#handleAltLeftClick(org.eclipse.swt.widgets.TreeColumn,
    *      org.eclipse.swt.widgets.TreeItem)
    */
   @Override
   public boolean handleAltLeftClick(TreeColumn treeColumn, TreeItem treeItem) {
      try {
         XViewerColumn xCol = (XViewerColumn) treeColumn.getData();
         MergeColumn aCol = MergeColumn.getAtsXColumn(xCol);
         Branch userRole = (Branch) treeItem.getData();
         boolean modified = false;
         AWorkbench.popup("ERROR", "Not handled");

         if (modified) {
            xCommitViewer.notifyXModifiedListeners();
            update(userRole, null);
            return true;
         }
      } catch (Exception ex) {
         OSEELog.logException(SkynetGuiDebug.class, ex, true);
      }
      return false;
   }

   /**
    * @return the xUserRoleViewer
    */
   public XMergeViewer getXUserRoleViewer() {
      return xCommitViewer;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.plugin.event.IEventReceiver#onEvent(org.eclipse.osee.framework.ui.plugin.event.Event)
    */
   public void onEvent(Event event) {
      xCommitViewer.refresh();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.plugin.event.IEventReceiver#runOnEventInDisplayThread()
    */
   public boolean runOnEventInDisplayThread() {
      return true;
   }

/**
 * @return the transactionArtifactChanges
 */
public Conflict[] getTransactionArtifactChanges() {
	return conflicts;
}

}
