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
package org.eclipse.osee.framework.ui.skynet.queryLog;

import java.util.Iterator;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.osee.framework.db.connection.core.query.QueryLog;
import org.eclipse.osee.framework.db.connection.core.query.QueryRecord;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.ui.plugin.util.SelectionCountChangeListener;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.ats.IActionable;
import org.eclipse.osee.framework.ui.skynet.ats.OseeAts;
import org.eclipse.osee.framework.ui.skynet.util.DbConnectionExceptionComposite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Robert A. Fisher
 */
public class QueryLogView extends ViewPart implements IActionable {
   public static final String VIEW_ID = "org.eclipse.osee.framework.ui.skynet.QueryLogView";
   public static final String ITEM = "Item";
   public static final String TIME = "Time";
   public static final String DURATION = "Run ms";

   private XViewer viewer;
   private Clipboard clipboard;

   @Override
   public void createPartControl(Composite parent) {

      if (!DbConnectionExceptionComposite.dbConnectionIsOk(parent)) return;

      clipboard = new Clipboard(null);

      parent.setLayout(new GridLayout(1, false));
      parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      viewer =
            new XViewer(parent, SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION, new QueryLogXViewerFactory(), false, false);
      viewer.setContentProvider(new QueryLogContentProvider());
      viewer.setLabelProvider(new QueryLogLabelProvider(viewer));
      viewer.setInput(QueryLog.getInstance());
      viewer.addSelectionChangedListener(new SelectionCountChangeListener(getViewSite()));

      Tree tree = viewer.getTree();
      GridData gridData = new GridData(GridData.FILL_BOTH | GridData.GRAB_VERTICAL | GridData.GRAB_HORIZONTAL);
      tree.setLayoutData(gridData);
      tree.setHeaderVisible(true);
      tree.setLinesVisible(true);
      tree.addKeyListener(new KeySelectedListener());

      createActions();
      setHelpContexts();
   }

   private void createActions() {

      Action refreshAction = new Action("Refresh") {

         @Override
         public void run() {
            viewer.refresh();
         }
      };
      refreshAction.setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.REFRESH));
      refreshAction.setToolTipText("Refresh");

      Action clearLogAction = new Action("Delete Log") {

         @Override
         public void run() {
            ((QueryLog) viewer.getInput()).clear();
            viewer.refresh();
         }
      };
      clearLogAction.setImageDescriptor(SkynetGuiPlugin.getInstance().getImageDescriptor("delete_edit.gif"));
      clearLogAction.setToolTipText("Delete Log");

      IToolBarManager toolbarManager = getViewSite().getActionBars().getToolBarManager();
      toolbarManager.add(refreshAction);
      toolbarManager.add(clearLogAction);
      viewer.addCustomizeToViewToolbar(this);
      OseeAts.addBugToViewToolbar(this, this, SkynetGuiPlugin.getInstance(), VIEW_ID, "Query Log");
   }

   private void setHelpContexts() {
      SkynetGuiPlugin.getInstance().setHelp(viewer.getControl(), "query_log_table");
   }

   @Override
   public void setFocus() {
      if (viewer != null) viewer.getControl().setFocus();
   }

   public String getActionDescription() {
      return "";
   }

   private void performCopy() {
      IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
      if (selection.isEmpty()) {
         return;
      }
      Object selected = selection.getFirstElement();

      String text = null;

      if (selected instanceof QueryRecord) {
         text = ((QueryRecord) selected).getSql();
      } else if (selected instanceof String) {
         text = ((String) selected).replaceAll(".*:", "");
      } else if (selected instanceof Exception) {
         text = Lib.exceptionToString((Exception) selected);
      } else {
         text = selected.toString();
      }
      clipboard.setContents(new Object[] {text}, new Transfer[] {TextTransfer.getInstance()});
   }

   private class KeySelectedListener implements KeyListener {
      public void keyPressed(KeyEvent e) {
      }

      public void keyReleased(KeyEvent e) {
         if (e.keyCode == 'a' && e.stateMask == SWT.CONTROL) {
            viewer.getTree().selectAll();
         } else if (e.keyCode == 'x' && e.stateMask == SWT.CONTROL) {
            expandAll((IStructuredSelection) viewer.getSelection());
         } else if (e.keyCode == 'c' && e.stateMask == SWT.CONTROL) {
            performCopy();
         }
      }
   }

   private void expandAll(IStructuredSelection selection) {
      Iterator<?> iter = selection.iterator();
      while (iter.hasNext()) {
         viewer.expandToLevel(iter.next(), TreeViewer.ALL_LEVELS);
      }
   }
}
