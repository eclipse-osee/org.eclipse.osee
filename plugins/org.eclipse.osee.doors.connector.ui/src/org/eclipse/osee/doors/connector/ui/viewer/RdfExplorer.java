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
import org.eclipse.osee.doors.connector.core.IDoorsArtifactParser;
import org.eclipse.osee.doors.connector.core.LoginDialog;
import org.eclipse.osee.doors.connector.core.QueryCapabilities;
import org.eclipse.osee.doors.connector.core.Requirement;
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
   private RdfExplorerDragAndDrop dragAndDropWorker;
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

      dragAndDropWorker = new RdfExplorerDragAndDrop(treeViewer.getTree());
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

   private ArrayList<Requirement> getSelectedItems(IStructuredSelection selection) {
      ArrayList<Requirement> reqs = new ArrayList<>();
      Iterator<?> i = selection.iterator();
      while (i.hasNext()) {
         Object obj = i.next();
         if (obj instanceof RdfExplorerItem) {
            DoorsArtifact dwaItem = ((RdfExplorerItem) obj).getDwaItem();
            if (dwaItem instanceof Requirement) {
               reqs.add((Requirement) dwaItem);
            }
         }
      }
      return reqs;
   }

   private void expandAll(IStructuredSelection selection) {
      Iterator<?> iter = selection.iterator();
      while (iter.hasNext()) {
         treeViewer.expandToLevel(iter.next(), AbstractTreeViewer.ALL_LEVELS);
      }
   }

   public void expandItem(IStructuredSelection selection) {
      RdfExplorerItem item = (RdfExplorerItem) selection.getFirstElement();
      DoorsArtifact provider = item.getDwaItem();
      if (provider.getChildren().size() < 1) {
         IDoorsArtifactParser reader = provider.getReader();
         try {
            reader.parse(provider);
            for (DoorsArtifact dwaItem : provider.getChildren()) {
               if (dwaItem instanceof QueryCapabilities) {
                  // root level, contains requirements
                  QueryCapabilities qc = (QueryCapabilities) dwaItem;
                  for (DoorsArtifact reqt : qc.getRequirements()) {
                     item.addItem(RdfExplorerFactory.getExplorerItem(reqt.getName(), item.getTreeViewer(), item,
                        item.getRdfExplorer(), reqt));
                  }
               } else {
                  item.addItem(RdfExplorerFactory.getExplorerItem(dwaItem.getName(), item.getTreeViewer(), item,
                     item.getRdfExplorer(), dwaItem));
               }
            }
            if (provider instanceof Requirement) {
               List<Requirement> selected = getSelectedItems(selection);
               if (selected.size() > 0) {
                  dragAndDropWorker.clearRequirements();
                  for (Requirement req : selected) {
                     // set up drag and drop for selected requirements only
                     // make sure they are all parsed
                     IDoorsArtifactParser reqReader = req.getReader();
                     reqReader.parse(req);
                     dragAndDropWorker.addRequirement(req);
                  }
               }
            }
            reload();
         } catch (Exception e) {
            e.printStackTrace();
         }
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

            Cookie[] cookies = service1.getHttpClient().getState().getCookies();
            for (Cookie cookie : cookies) {
               DoorsModel.setJSessionID(cookie.getValue());
            }
         }
      }
      DoorsArtifact da = DoorsModel.getDoorsArtifact();
      for (DoorsArtifact item : da.getChildren()) {
         rootItem.addItem(RdfExplorerFactory.getExplorerItem(item.getName(), treeViewer, rootItem, this, item));
      }
   }

}
