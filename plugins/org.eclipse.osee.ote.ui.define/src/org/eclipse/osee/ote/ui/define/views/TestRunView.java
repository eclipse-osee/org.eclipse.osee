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
package org.eclipse.osee.ote.ui.define.views;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.osee.framework.help.ui.OseeHelpContext;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.plugin.util.HelpUtil;
import org.eclipse.osee.framework.ui.skynet.widgets.GenericViewPart;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.ote.ui.define.OteDefineImage;
import org.eclipse.osee.ote.ui.define.internal.Activator;
import org.eclipse.osee.ote.ui.define.viewers.IDataChangedListener;
import org.eclipse.osee.ote.ui.define.viewers.TestRunXViewer;
import org.eclipse.osee.ote.ui.define.viewers.data.ArtifactItem;
import org.eclipse.osee.ote.ui.define.viewers.data.BranchItem;
import org.eclipse.osee.ote.ui.define.viewers.data.ScriptItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;

/**
 * @author Roberto E. Escobar
 */
public class TestRunView extends GenericViewPart implements IDataChangedListener {
   public static final String VIEW_ID = TestRunView.class.getName();
   private static final String BEGIN_MESSAGE =
      "To begin, drag and drop a test run artifact from artifact explorer or from an artifact search result window. Alternatively, you can drag and drop a test outfile onto this view.";

   private Action expandAction, collapseAction, refreshAction;
   private static TestRunXViewer viewer = null;
   @SuppressWarnings("unused")
   private IHandlerService handlerService;
   private ShowOnlyLatestRuns latestRunFilter;
   private boolean isFilterOn;

   //   private static final String FLAT_KEY = "flat";

   @Override
   public void createPartControl(Composite parent) {

      parent.setLayout(new GridLayout());

      PlatformUI.getWorkbench().getService(IHandlerService.class);
      handlerService = getSite().getService(IHandlerService.class);

      viewer = new TestRunXViewer(parent);
      Tree tree = viewer.getTree();
      GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
      gridData.minimumHeight = 350;
      tree.setLayoutData(gridData);
      tree.setHeaderVisible(true);
      tree.setLinesVisible(true);
      tree.setFont(parent.getFont());

      parent.layout();
      createActions();
      createMenus();
      createToolbar();

      latestRunFilter = new ShowOnlyLatestRuns();
      isFilterOn = false;
      viewer.registerListener(this);
      viewer.getTree().addKeyListener(new KeyAdapter() {

         @Override
         public void keyPressed(KeyEvent event) {
            if (event.stateMask == SWT.CTRL && (event.keyCode == 'A' || event.keyCode == 'a')) {
               Object object = event.getSource();
               if (object instanceof Tree) {
                  Tree tree = (Tree) object;
                  tree.selectAll();
               }
            }
         }
      });
      onDataChanged();

      HelpUtil.setHelp(parent, OseeHelpContext.TEST_RUN_VIEW);
      HelpUtil.setHelp(viewer.getTree(), OseeHelpContext.TEST_RUN_VIEW);

      setFocusWidget(viewer.getControl());
   }

   protected void createActions() {
      expandAction = new Action("Expand All") {

         @Override
         public void run() {
            viewer.getTree().setRedraw(false);
            viewer.expandAll();
            viewer.getTree().setRedraw(true);
         }
      };
      expandAction.setImageDescriptor(ImageManager.getImageDescriptor(OteDefineImage.EXPAND_STATE));
      expandAction.setToolTipText("Expand All");

      collapseAction = new Action("Collapse All") {

         @Override
         public void run() {
            viewer.getTree().setRedraw(false);
            viewer.collapseAll();
            viewer.getTree().setRedraw(true);
         }
      };
      collapseAction.setImageDescriptor(ImageManager.getImageDescriptor(OteDefineImage.COLLAPSE_STATE));
      collapseAction.setToolTipText("Collapse All");

      refreshAction = new Action("Refresh") {

         @Override
         public void run() {
            Displays.ensureInDisplayThread(new Runnable() {
               @Override
               public void run() {
                  viewer.refresh();
               };
            });
         }
      };
      refreshAction.setToolTipText("Refresh Table");
      refreshAction.setImageDescriptor(ImageManager.getImageDescriptor(PluginUiImage.REFRESH));

   }

   private void createMenus() {
      MenuManager menuManager = new MenuManager();
      getSite().registerContextMenu(VIEW_ID, menuManager, viewer);
      menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
      menuManager.setParent(getViewSite().getActionBars().getMenuManager());
      fillMenu(menuManager);
      getSite().setSelectionProvider(viewer);
   }

   private void fillMenu(IMenuManager menuManager) {
      menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
   }

   private void createToolbar() {
      IToolBarManager toolbarManager = getViewSite().getActionBars().getToolBarManager();
      toolbarManager.add(refreshAction);
      toolbarManager.add(expandAction);
      toolbarManager.add(collapseAction);
   }

   public void setDescription(final String message) {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            setContentDescription(message);
         }
      });
   }

   @Override
   public void onDataChanged() {
      String message = BEGIN_MESSAGE;
      Object object = viewer.getInput();
      if (object != null && object instanceof Collection<?>) {
         if (!((Collection<?>) object).isEmpty()) {
            message = "";
         }
      }
      setDescription(message);
   }

   public static StructuredViewer getViewer() {
      return viewer;
   }

   public void presentGroupedByBranch() {
      BranchItem.setGroupModeEnabled(true);
      viewer.refresh();
   }

   public void presentGroupedByScript() {
      BranchItem.setGroupModeEnabled(false);
      viewer.refresh();
   }

   public void presentWithShortNames() {
      ScriptItem.setFullDescriptionModeEnabled(false);
      ArtifactItem.setFullDescriptionModeEnabled(false);
      viewer.refresh();
   }

   public void presentWithLongNames() {
      ScriptItem.setFullDescriptionModeEnabled(true);
      ArtifactItem.setFullDescriptionModeEnabled(true);
      viewer.refresh();
   }

   public void toggleFilter() {
      latestRunFilter.clear();
      if (isFilterOn != true) {
         isFilterOn = true;
         viewer.addFilter(latestRunFilter);
      } else {
         isFilterOn = false;
         viewer.removeFilter(latestRunFilter);
      }
      viewer.refresh();
   }

   private final class ShowOnlyLatestRuns extends ViewerFilter {
      private final Map<String, ArtifactItem> latestMap = new HashMap<>();

      @Override
      public Object[] filter(Viewer viewer, Object parent, Object[] elements) {
         return super.filter(viewer, parent, elements);
      }

      @Override
      public boolean select(Viewer viewer, Object parentElement, Object element) {
         boolean toReturn = false;
         if (element instanceof ArtifactItem) {
            ArtifactItem item1 = (ArtifactItem) element;
            String name = item1.getOperator().getDescriptiveName();

            ArtifactItem item2 = latestMap.get(name);
            if (item2 == null) {
               latestMap.put(name, item1);
               toReturn = true;
            } else {
               try {
                  Date date1 = item1.getOperator().getEndDate();
                  Date date2 = item2.getOperator().getEndDate();
                  if (date1.after(date2) || date1.equals(date2)) {
                     latestMap.put(name, item1);
                     toReturn = true;
                  }
               } catch (Exception ex) {
                  OseeLog.log(Activator.class, Level.WARNING, "Error comparing run dates.", ex);
               }
            }
         } else {
            toReturn = true;
         }
         return toReturn;
      }

      public void clear() {
         latestMap.clear();
      }
   }
}
