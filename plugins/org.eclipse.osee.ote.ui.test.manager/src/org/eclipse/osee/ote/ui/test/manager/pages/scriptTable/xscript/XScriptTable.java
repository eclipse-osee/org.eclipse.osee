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
package org.eclipse.osee.ote.ui.test.manager.pages.scriptTable.xscript;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.ote.ui.test.manager.pages.scriptTable.ScriptTask;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 */
public class XScriptTable extends XViewer {

   /**
    * @param parent
    * @param style
    * @param namespace
    * @param viewerFactory
    */
   public XScriptTable(Composite parent, int style) {
      super(parent, style, new XScriptTableFactory(), false, false);
   }

   @Override
   public void handleDoubleClick(TreeColumn col, TreeItem item) {
      XViewerColumn xcol = (XViewerColumn) col.getData();
      ScriptTask task = (ScriptTask) item.getData();
      if (XScriptTableFactory.RESULT.equals(xcol) || XScriptTableFactory.OUPUT_FILE.equals(xcol)) {
         task.getScriptModel().getOutputModel().openEditor();
      } else if (XScriptTableFactory.TEST_LOCATION.equals(xcol)) {
         task.getScriptModel().openPackageExplorer();
      } else if (XScriptTableFactory.TEST.equals(xcol)) {// != k) {
         task.getScriptModel().openEditor();
      }
   }

   @Override
   public boolean handleLeftClick(TreeColumn treeColumn, TreeItem treeItem) {
      XViewerColumn aCol = (XViewerColumn) treeColumn.getData();
      if (XScriptTableFactory.RUN.equals(aCol)) {
         ScriptTask task = (ScriptTask) treeItem.getData();
         task.setRun(!task.isRun());
         refresh(task);
      }
      return super.handleLeftClick(treeColumn, treeItem);
   }
   
   @Override
   public boolean handleLeftClickInIconArea(TreeColumn treeColumn, TreeItem treeItem) {
      XViewerColumn aCol = (XViewerColumn) treeColumn.getData();
      if (XScriptTableFactory.RUN.equals(aCol)) {
         ScriptTask task = (ScriptTask) treeItem.getData();
         task.setRun(!task.isRun());
         refresh(task);
      }
      return super.handleLeftClickInIconArea(treeColumn, treeItem);
   }

   public List<ScriptTask> getVisibleSortedScriptTasksToRun() {
      Object[] objs = getSortedChildren(getInput());
      List<ScriptTask> runs = new ArrayList<ScriptTask>();
      for (Object obj : objs) {
         if (((ScriptTask) obj).isRun()) {
            runs.add((ScriptTask) obj);
         }
      }
      return runs;
   }

   public List<ScriptTask> getVisibibleSortedScriptTasks() {
      Object[] objs = getSortedChildren(getInput());
      List<ScriptTask> runs = new ArrayList<ScriptTask>();
      for (Object obj : objs) {
         runs.add((ScriptTask) obj);
      }
      return runs;
   }

}
