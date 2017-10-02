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

package org.eclipse.osee.framework.ui.plugin.xnavigate;

import static org.eclipse.osee.framework.core.data.CoreActivityTypes.XNAVIGATEITEM;
import java.util.List;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osee.activity.api.ActivityLog;
import org.eclipse.osee.activity.api.ActivityLogEndpoint;
import org.eclipse.osee.framework.core.util.OsgiUtil;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.internal.UiPluginConstants;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.osee.orcs.rest.client.OseeClient;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.dialogs.FilteredTree;

/**
 * @author Donald G. Dunne
 */
public class XNavigateComposite extends Composite {

   public static enum TableLoadOption {
      None,
      // Wait for table to be loaded before returning; for test only
      ForcePend,
      //
      ClearLastSearchItem,
      // Don't perform UI check, just search
      NoUI,
      // Don't create fresh copy of search item; for test only
      DontCopySearchItem
   };

   private final ToolTipDisplayListener tableListener = new ToolTipDisplayListener();
   protected final XNavigateViewItems navigateViewItems;
   protected Browser browser;
   protected FilteredTreePlus filteredTree;
   private final String filterText;

   public XNavigateComposite(XNavigateViewItems navigateViewItems, Composite parent, int style) {
      this(navigateViewItems, parent, style, null);
   }

   public XNavigateComposite(XNavigateViewItems navigateViewItems, Composite parent, int style, String filterText) {
      super(parent, style);
      this.navigateViewItems = navigateViewItems;
      this.filterText = filterText;

      setLayout(new GridLayout());
      createControl();
   }

   private void createControl() {
      filteredTree = new FilteredTreePlus(this, SWT.SINGLE | SWT.BORDER, new XNavigateViewFilter(filterText), true);
      if (Strings.isValid(filterText)) {
         filteredTree.setFilterTextPlus(filterText);
      }
      filteredTree.getViewer().setContentProvider(new XNavigateContentProvider());
      filteredTree.getViewer().setLabelProvider(new XNavigateLabelProvider());
      filteredTree.getViewer().getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      // Disable native tree tooltip
      filteredTree.getViewer().getTree().setToolTipText("");
      filteredTree.getViewer().getTree().addListener(SWT.Dispose, tableListener);
      filteredTree.getViewer().getTree().addListener(SWT.KeyDown, tableListener);
      filteredTree.getViewer().getTree().addListener(SWT.MouseMove, tableListener);
      filteredTree.getViewer().getTree().addListener(SWT.MouseHover, tableListener);
      filteredTree.getViewer().addDoubleClickListener(new IDoubleClickListener() {
         @Override
         public void doubleClick(DoubleClickEvent event) {
            try {
               handleDoubleClick();
            } catch (OseeCoreException ex) {
               OseeLog.log(UiPluginConstants.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      });
      filteredTree.getViewer().getTree().addKeyListener(new KeyAdapter() {

         @Override
         public void keyReleased(KeyEvent e) {
            if (e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
               try {
                  handleDoubleClick();
               } catch (OseeCoreException ex) {
                  OseeLog.log(UiPluginConstants.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
         }
      });

      MenuManager menuManager = new MenuManager();
      menuManager.setRemoveAllWhenShown(true);
      menuManager.addMenuListener(new IMenuListener() {
         @Override
         public void menuAboutToShow(IMenuManager manager) {
            MenuManager menuManager = (MenuManager) manager;
            menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
         }
      });

      addMenu(filteredTree.getViewer());
   }

   private void addMenu(TreeViewer treeViewer) {
      final Menu menu = new Menu(treeViewer.getTree().getShell(), SWT.POP_UP);
      treeViewer.getTree().setMenu(menu);
      menu.addListener(SWT.Show, new Listener() {
         @Override
         public void handleEvent(Event event) {
            MenuItem[] menuItems = menu.getItems();
            for (int i = 0; i < menuItems.length; i++) {
               menuItems[i].dispose();
            }
            TreeItem[] treeItems = treeViewer.getTree().getSelection();
            final TreeItem selectedTreeItem = treeItems[0];
            if (selectedTreeItem.getData() instanceof XNavigateItem) {
               XNavigateItem navItem = (XNavigateItem) selectedTreeItem.getData();
               for (IXNavigateMenuItem menuItem : navItem.getMenuItems()) {
                  menuItem.addMenuItems(menu, selectedTreeItem);
               }
            }
         }

      });

   }

   protected void disposeTooltip() {
      tableListener.disposeTooltip();
   }

   protected void handleDoubleClick() {
      IStructuredSelection sel = (IStructuredSelection) filteredTree.getViewer().getSelection();
      if (!sel.iterator().hasNext()) {
         return;
      }
      XNavigateItem item = (XNavigateItem) sel.iterator().next();
      handleDoubleClick(item);
   }

   protected void handleDoubleClick(XNavigateItem item, TableLoadOption... tableLoadOptions) {
      disposeTooltip();

      if (item.getChildren().size() > 0) {
         filteredTree.getViewer().setExpandedState(item, true);
      } else {
         try {
            long uuid = Lib.generateUuid();
            ActivityLogEndpoint activityEp = OsgiUtil.getService(getClass(), OseeClient.class).getActivityLogEndpoint();
            activityEp.createEntry(XNAVIGATEITEM, uuid, ActivityLog.INITIAL_STATUS, item.getName());
            item.run(tableLoadOptions);
            activityEp.createEntry(XNAVIGATEITEM, uuid, ActivityLog.COMPLETE_STATUS, item.getName());
         } catch (Exception ex) {
            OseeLog.log(UiPluginConstants.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
   }

   public void refresh() {
      final List<XNavigateItem> items = navigateViewItems.getSearchNavigateItems();
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            filteredTree.getViewer().setInput(items);
         }
      });
   }

   /**
    * @return the listViewer
    */
   public FilteredTree getFilteredTree() {
      return filteredTree;
   }

   /**
    * @return the items
    */
   @SuppressWarnings("unchecked")
   public List<XNavigateItem> getInput() {
      Object input = filteredTree.getViewer().getInput();
      return (List<XNavigateItem>) input;
   }

   private class ToolTipDisplayListener implements Listener {

      private final LabelListener labelListener = new LabelListener();
      private Shell tip;
      private Label label;

      protected void disposeTooltip() {
         if (Widgets.isAccessible(tip)) {
            tip.dispose();
         }
         tip = null;
         label = null;
      }

      @Override
      public void handleEvent(Event event) {
         switch (event.type) {
            case SWT.Dispose:
            case SWT.KeyDown:
            case SWT.MouseMove: {
               if (tip == null) {
                  break;
               }
               disposeTooltip();
               break;
            }
            case SWT.MouseHover: {
               Tree tree = filteredTree.getViewer().getTree();
               TreeItem item = tree.getItem(new Point(event.x, event.y));

               if (item != null && item.getData() instanceof XNavigateItem) {
                  XNavigateItem navItem = (XNavigateItem) item.getData();
                  String description = navItem.getDescription();

                  if (Strings.isValid(description)) {
                     disposeTooltip();

                     tip = new Shell(Displays.getActiveShell(), SWT.ON_TOP | SWT.NO_FOCUS | SWT.TOOL);
                     tip.setBackground(Displays.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
                     FillLayout layout = new FillLayout();
                     layout.marginWidth = 2;
                     tip.setLayout(layout);

                     label = new Label(tip, SWT.NONE);
                     label.setForeground(Displays.getSystemColor(SWT.COLOR_INFO_FOREGROUND));
                     label.setBackground(Displays.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
                     label.setData("_TABLEITEM", item);
                     label.setText(String.format("%s\n\n", item.getText(), description));
                     label.addListener(SWT.MouseExit, labelListener);
                     label.addListener(SWT.MouseDown, labelListener);

                     Point size = tip.computeSize(SWT.DEFAULT, SWT.DEFAULT);
                     Rectangle rect = item.getBounds(0);
                     Point pt = tree.toDisplay(rect.x, rect.y);

                     tip.setBounds(pt.x, pt.y + 15, size.x, size.y);
                     tip.setVisible(true);
                  }
               }
            }
         }
      }
   };

   private class LabelListener implements Listener {
      @Override
      public void handleEvent(Event event) {
         Label label = (Label) event.widget;
         Shell shell = label.getShell();
         switch (event.type) {
            case SWT.MouseDown:
               Event e = new Event();
               e.item = (TableItem) label.getData("_TABLEITEM");
               // Assuming table is single select, set the selection as if
               // the mouse down event went through to the table
               //               filteredTree.getViewer().getTree().setSelection(new TableItem[] {(TableItem) e.item});
               filteredTree.getViewer().getTree().notifyListeners(SWT.Selection, e);
               shell.dispose();
               filteredTree.getViewer().getTree().setFocus();
               break;
            case SWT.MouseExit:
               shell.dispose();
               break;
         }
      }
   };

}
