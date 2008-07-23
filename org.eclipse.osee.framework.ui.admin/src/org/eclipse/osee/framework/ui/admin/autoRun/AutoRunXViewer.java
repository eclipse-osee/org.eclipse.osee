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
import org.eclipse.osee.framework.ui.skynet.autoRun.IAutoRunTask;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 */
public class AutoRunXViewer extends XViewer {

   private Set<IAutoRunTask> runList = new HashSet<IAutoRunTask>();
   private final XAutoRunViewer xAutoRunViewer;

   /**
    * @param parent
    * @param style
    */
   public AutoRunXViewer(Composite parent, int style, XAutoRunViewer xAutoRunViewer) {
      super(parent, style, new AutoRunXViewerFactory());
      this.xAutoRunViewer = xAutoRunViewer;
   }

   public void selectAll() {
      runList.clear();
      for (TreeItem treeItem : xAutoRunViewer.getXViewer().getTree().getItems()) {
         runList.add((IAutoRunTask) treeItem.getData());
      }
      xAutoRunViewer.refresh();
   }

   public void delSelectAll() {
      runList.clear();
      xAutoRunViewer.refresh();
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

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewer#handleLeftClick(org.eclipse.swt.widgets.TreeColumn, org.eclipse.swt.widgets.TreeItem)
    */
   @Override
   public boolean handleLeftClick(TreeColumn treeColumn, TreeItem treeItem) {
      if (treeColumn.getText().equals(AutoRunXViewerFactory.Run_Col.getName())) {
         toggleRun((IAutoRunTask) treeItem.getData());
         refresh();
      }
      return true;
   }

   /**
    * @return the xUserRoleViewer
    */
   public XAutoRunViewer getXAutoRunViewer() {
      return xAutoRunViewer;
   }

}
