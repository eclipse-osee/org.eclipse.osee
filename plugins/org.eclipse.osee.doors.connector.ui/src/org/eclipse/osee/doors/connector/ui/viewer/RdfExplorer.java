/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.doors.connector.ui.viewer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import org.apache.commons.httpclient.Cookie;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.doors.connector.core.DoorsArtifact;
import org.eclipse.osee.doors.connector.core.DoorsModel;
import org.eclipse.osee.doors.connector.core.DoorsOSLCConnector;
import org.eclipse.osee.doors.connector.core.LoginDialog;
import org.eclipse.osee.doors.connector.core.oauth.DWAOAuthService;
import org.eclipse.osee.doors.connector.ui.oauth.extension.DoorsOSLCDWAProviderInfoExtn;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.skynet.listener.IRebuildMenuListener;
import org.eclipse.osee.framework.ui.skynet.widgets.GenericViewPart;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.PlatformUI;

/**
 * @author Donald G. Dunne
 */
public class RdfExplorer extends GenericViewPart implements IRebuildMenuListener {
   public static final String VIEW_ID = "org.eclipse.osee.doors.connector.ui.viewer.RdfExplorer";
   private RdfTreeViewer treeViewer;
   private Composite parentComp;
   private RdfExplorerItem rootItem;

   @Override
   public void createPartControl(Composite parent) {

      GridData gridData = new GridData();
      gridData.verticalAlignment = GridData.FILL;
      gridData.horizontalAlignment = GridData.FILL;
      gridData.grabExcessVerticalSpace = true;
      gridData.grabExcessHorizontalSpace = true;

      GridLayout gridLayout = new GridLayout(1, false);
      gridData.heightHint = 1000;
      gridData.widthHint = 1000;

      parentComp = parent;

      parentComp.setLayout(gridLayout);
      parentComp.setLayoutData(gridData);

      treeViewer = new RdfTreeViewer(this, parentComp);
      treeViewer.setContentProvider(new RdfContentProvider());
      treeViewer.setLabelProvider(new RdfLabelProvider());
      treeViewer.setUseHashlookup(true);
      treeViewer.getControl().setLayoutData(gridData);

      getSite().setSelectionProvider(treeViewer);
      parentComp.layout();
      createActions();
      getViewSite().getActionBars().updateActionBars();
      rebuildMenu();
      setFocusWidget(parentComp);
   }

   @Override
   public void rebuildMenu() {
      Menu popupMenu = new Menu(treeViewer.getTree().getParent());
      OpenOnShowListener openListener = new OpenOnShowListener();
      popupMenu.addMenuListener(openListener);

      new MenuItem(popupMenu, SWT.SEPARATOR);

      MenuItem item = new MenuItem(popupMenu, SWT.PUSH);
      item.setText("&Remove from Group");
      item.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            try {
               //               handleRemoveFromGroup();
            } catch (Exception ex) {
               OseeLog.log(RdfExplorer.class, Level.SEVERE, ex);
            }
         }
      });

      item = new MenuItem(popupMenu, SWT.PUSH);
      item.setText("&Delete Group");
      item.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            try {
               //               handleDeleteGroup();
            } catch (Exception ex) {
               OseeLog.log(RdfExplorer.class, Level.SEVERE, ex);
            }
         }
      });

      item = new MenuItem(popupMenu, SWT.PUSH);
      item.setText("&New Group");
      item.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            //            handleNewGroup();
         }
      });

      new MenuItem(popupMenu, SWT.SEPARATOR);

      item = new MenuItem(popupMenu, SWT.PUSH);
      item.setText("&Select All\tCtrl+A");
      item.addListener(SWT.Selection, new Listener() {
         @Override
         public void handleEvent(org.eclipse.swt.widgets.Event event) {
            treeViewer.getTree().selectAll();
         }
      });

      item = new MenuItem(popupMenu, SWT.PUSH);
      item.setText("Expand All\tCtrl+X");
      item.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            expandAll((IStructuredSelection) treeViewer.getSelection());
         }
      });

      treeViewer.getTree().setMenu(popupMenu);
   }

   private class OpenOnShowListener implements MenuListener {
      private final List<MenuItem> items = new LinkedList<>();

      public void add(MenuItem item) {
         items.add(item);
      }

      @Override
      public void menuShown(MenuEvent e) {
         for (MenuItem item : items) {
            item.setEnabled(!treeViewer.getSelection().isEmpty());
         }
      }

      @Override
      public void menuHidden(MenuEvent e) {
         // nothing
      }
   }

   protected void createActions() {
      Action refreshAction = new Action("Refresh", IAction.AS_PUSH_BUTTON) {

         @Override
         public void run() {
            reload();
         }
      };
      refreshAction.setImageDescriptor(ImageManager.getImageDescriptor(PluginUiImage.REFRESH));
      refreshAction.setToolTipText("Refresh");

      IToolBarManager toolbarManager = getViewSite().getActionBars().getToolBarManager();
      toolbarManager.add(refreshAction);

   }

   private ArrayList<RdfExplorerItem> getSelectedItems() {
      ArrayList<RdfExplorerItem> arts = new ArrayList<>();
      Iterator<?> i = ((IStructuredSelection) treeViewer.getSelection()).iterator();
      while (i.hasNext()) {
         Object obj = i.next();
         if (obj instanceof RdfExplorerItem) {
            arts.add((RdfExplorerItem) obj);
         }
      }
      return arts;
   }

   private void expandAll(IStructuredSelection selection) {
      Iterator<?> iter = selection.iterator();
      while (iter.hasNext()) {
         treeViewer.expandToLevel(iter.next(), AbstractTreeViewer.ALL_LEVELS);
      }
   }

   public void reload() {
      List<RdfExplorerItem> items = new LinkedList<>();
      if (rootItem == null) {
         rootItem = new RdfExplorerItem("Top Level", treeViewer, null, this, null);
         buildDoorsItems(rootItem);
      }
      //note: top level item has parent and related dwaItem set to null
      items.add(rootItem);
      if (treeViewer != null) {
         treeViewer.setInput(items);
      }
   }

   private void buildDoorsItems(RdfExplorerItem rootItem) {
      DoorsArtifact doorsArtifact1 = DoorsModel.getDoorsArtifact();

      if (doorsArtifact1 == null) {
         LoginDialog dialog = new LoginDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
         if (dialog.open() == Window.OK) {
            DoorsOSLCDWAProviderInfoExtn config = new DoorsOSLCDWAProviderInfoExtn();
            DoorsOSLCConnector connector = new DoorsOSLCConnector();
            DWAOAuthService service1 = new DWAOAuthService(config, "Open System Engineering Environment", "BoeingOSEE");
            DoorsArtifact doorsArtifact = connector.getAuthentication(service1, dialog.getName(), dialog.getPassword());
            DoorsModel.setDoorsArtifact(doorsArtifact);
            for (DoorsArtifact item : doorsArtifact.getChildren()) {
               rootItem.addItem(RdfExplorerFactory.getExplorerItem(item.getName(), treeViewer, rootItem, this, item));
            }

            Cookie[] cookies = service1.getHttpClient().getState().getCookies();
            for (Cookie cookie : cookies) {
               DoorsModel.setJSessionID(cookie.getValue());
            }
         }
      }
   }

}
