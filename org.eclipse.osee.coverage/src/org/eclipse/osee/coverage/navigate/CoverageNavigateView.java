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
package org.eclipse.osee.coverage.navigate;

import java.util.logging.Level;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IRegistryEventListener;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.coverage.util.CoverageUtil;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.ats.IActionable;
import org.eclipse.osee.framework.ui.skynet.ats.OseeAts;
import org.eclipse.osee.framework.ui.skynet.widgets.XBranchSelectWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Donald G. Dunne
 */
public class CoverageNavigateView extends ViewPart implements IActionable {

   public static final String VIEW_ID = "org.eclipse.osee.coverage.navigate.CoverageNavigateView";
   private XNavigateComposite xNavComp;
   private XBranchSelectWidget xBranchSelectWidget;

   public CoverageNavigateView() {
   }

   @Override
   public void setFocus() {
   }

   @Override
   public void createPartControl(Composite parent) {

      Composite comp = new Composite(parent, SWT.None);
      comp.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));

      xBranchSelectWidget = new XBranchSelectWidget("");
      xBranchSelectWidget.setDisplayLabel(false);
      if (CoverageUtil.getBranch() != null) {
         xBranchSelectWidget.setSelection(CoverageUtil.getBranch());
      }
      xBranchSelectWidget.createWidgets(comp, 1);
      xBranchSelectWidget.addListener(new Listener() {
         @Override
         public void handleEvent(Event event) {
            try {
               Branch selectedBranch = xBranchSelectWidget.getData();
               if (selectedBranch != null) {
                  CoverageUtil.setBranch(selectedBranch);
               }
            } catch (Exception ex) {
               OseeLog.log(getClass(), Level.SEVERE, ex);
            }
         }

      });
      CoverageUtil.addBranchChangeListener(new Listener() {
         @Override
         public void handleEvent(Event event) {
            xBranchSelectWidget.setSelection(CoverageUtil.getBranch());
         }
      });
      xNavComp = new XNavigateComposite(new CoverageNavigateViewItems(), comp, SWT.NONE);

      createActions();
      xNavComp.refresh();

      Label label = new Label(xNavComp, SWT.None);
      String str = getWhoAmI();
      if (CoverageUtil.isAdmin()) {
         str += " - Admin";
      }
      if (!str.equals("")) {
         if (CoverageUtil.isAdmin()) {
            label.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
         } else {
            label.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLUE));
         }
      }
      label.setText(str);
      label.setToolTipText(str);
      GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER | GridData.VERTICAL_ALIGN_CENTER);
      gridData.heightHint = 15;
      label.setLayoutData(gridData);

      addExtensionPointListenerBecauseOfWorkspaceLoading();
   }

   private String getWhoAmI() {
      try {
         String userName = UserManager.getUser().getName();
         return String.format("%s - %s:%s", userName, ClientSessionManager.getDataStoreName(),
               ClientSessionManager.getDataStoreLoginName());
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         return "Exception: " + ex.getLocalizedMessage();
      }
   }

   private void addExtensionPointListenerBecauseOfWorkspaceLoading() {
      IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
      extensionRegistry.addListener(new IRegistryEventListener() {
         @Override
         public void added(IExtension[] extensions) {
            refresh();
         }

         @Override
         public void added(IExtensionPoint[] extensionPoints) {
            refresh();
         }

         @Override
         public void removed(IExtension[] extensions) {
            refresh();
         }

         @Override
         public void removed(IExtensionPoint[] extensionPoints) {
            refresh();
         }
      }, "org.eclipse.osee.coverage.CoverageNavigateItem");
   }

   protected void createActions() {
      Action refreshAction = new Action("Refresh") {

         @Override
         public void run() {
            xNavComp.refresh();
         }
      };
      refreshAction.setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.REFRESH));
      refreshAction.setToolTipText("Refresh");
      getViewSite().getActionBars().getMenuManager().add(refreshAction);

      OseeAts.addBugToViewToolbar(this, this, Activator.getInstance(), VIEW_ID, "Coverage Navigator");

   }

   public String getActionDescription() {
      IStructuredSelection sel = (IStructuredSelection) xNavComp.getFilteredTree().getViewer().getSelection();
      if (sel.iterator().hasNext()) {
         return String.format("Currently Selected - %s", ((XNavigateItem) sel.iterator().next()).getName());
      }
      return "";
   }

   public void refresh() {
      Display.getDefault().asyncExec(new Runnable() {
         public void run() {
            xNavComp.refresh();
         }
      });
   }

}