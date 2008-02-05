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
package org.eclipse.osee.framework.ui.admin.autoRun;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.autoRun.IAutoRunTask;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.util.SkynetGuiDebug;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.IXViewerFactory;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 */
public class AutoRunXViewer extends XViewer {

   private static String NAMESPACE = "osee.ats.UserRoleXViewer";
   private final XAutoRunViewer xUserRoleViewer;
   private Set<IAutoRunTask> runList = new HashSet<IAutoRunTask>();

   /**
    * @param parent
    * @param style
    */
   public AutoRunXViewer(Composite parent, int style, XAutoRunViewer xViewer) {
      this(parent, style, NAMESPACE, new AutoRunXViewerFactory(), xViewer);
   }

   public AutoRunXViewer(Composite parent, int style, String nameSpace, IXViewerFactory xViewerFactory, XAutoRunViewer xRoleViewer) {
      super(parent, style, nameSpace, xViewerFactory);
      this.xUserRoleViewer = xRoleViewer;
   }

   public boolean isRun(IAutoRunTask autoRunTask) {
      return runList.contains(autoRunTask);
   }

   public void setRun(IAutoRunTask autoRunTask, boolean run) {
      if (run)
         runList.add(autoRunTask);
      else
         runList.remove(autoRunTask);
   }

   public Collection<IAutoRunTask> getRunList() {
      return runList;
   }

   public void toggleRun(IAutoRunTask autoRunTask) {
      setRun(autoRunTask, !isRun(autoRunTask));
   }

   @Override
   protected void createSupportWidgets(Composite parent) {
      super.createSupportWidgets(parent);
      parent.addDisposeListener(new DisposeListener() {
         public void widgetDisposed(DisposeEvent e) {
            ((AutoRunContentProvider) getContentProvider()).clear();
         }
      });
      createMenuActions();
   }

   public void createMenuActions() {
      MenuManager mm = getMenuManager();
      mm.createContextMenu(getControl());
      mm.addMenuListener(new IMenuListener() {
         public void menuAboutToShow(IMenuManager manager) {
            updateMenuActions();
         }
      });
   }

   @Override
   public boolean isColumnMultiEditable(TreeColumn treeColumn, Collection<TreeItem> treeItems) {
      AutoRunColumn aCol = AutoRunColumn.getAtsXColumn((XViewerColumn) treeColumn.getData());
      XViewerColumn xCol = getCustomize().getCurrentCustData().getColumnData().getXColumn(aCol.getName());
      if (!xCol.isShow() || !aCol.isMultiColumnEditable()) return false;
      return true;
   }

   @Override
   public boolean isColumnMultiEditEnabled() {
      return true;
   }

   public void updateEditMenuActions() {
      // MenuManager mm = getMenuManager();

      // EDIT MENU BLOCK
   }

   public void updateMenuActions() {
      MenuManager mm = getMenuManager();

      updateEditMenuActions();

      mm.insertBefore(MENU_GROUP_PRE, new Separator());
   }

   public void set(Collection<? extends IAutoRunTask> userRoles) {
      ((AutoRunContentProvider) getContentProvider()).set(userRoles);
   }

   public void clear() {
      ((AutoRunContentProvider) getContentProvider()).clear();
   }

   /**
    * Release resources
    */
   public void dispose() {
      // Dispose of the table objects is done through separate dispose listener off tree
      // Tell the label provider to release its ressources
      getLabelProvider().dispose();
   }

   public ArrayList<IAutoRunTask> getSelectedAutoRunItems() {
      ArrayList<IAutoRunTask> arts = new ArrayList<IAutoRunTask>();
      TreeItem items[] = getTree().getSelection();
      if (items.length > 0) for (TreeItem item : items)
         arts.add((IAutoRunTask) item.getData());
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
         // System.out.println("Column " + treeColumn.getText() + " item " +
         // treeItem);
         XViewerColumn xCol = (XViewerColumn) treeColumn.getData();
         AutoRunColumn aCol = AutoRunColumn.getAtsXColumn(xCol);
         IAutoRunTask userRole = (IAutoRunTask) treeItem.getData();
         boolean modified = false;
         if (aCol == AutoRunColumn.Name_Col) {
            AWorkbench.popup("ERROR", "Not implemented yet");
         } else
            throw new IllegalStateException("Unhandled user role column");

      } catch (Exception ex) {
         OSEELog.logException(SkynetGuiDebug.class, ex, true);
      }
      return false;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewer#handleLeftClick(org.eclipse.swt.widgets.TreeColumn, org.eclipse.swt.widgets.TreeItem)
    */
   @Override
   public boolean handleLeftClick(TreeColumn treeColumn, TreeItem treeItem) {
      if (treeColumn.getText().equals(AutoRunColumn.Run_Col.getName())) {
         toggleRun((IAutoRunTask) treeItem.getData());
         refresh();
      }
      return true;
   }

   /**
    * @return the xUserRoleViewer
    */
   public XAutoRunViewer getXUserRoleViewer() {
      return xUserRoleViewer;
   }

}
