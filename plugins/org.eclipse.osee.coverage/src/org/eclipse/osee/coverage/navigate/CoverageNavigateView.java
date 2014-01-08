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
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.osee.coverage.help.ui.CoverageHelpContext;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.coverage.util.CoverageUtil;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.plugin.util.HelpUtil;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite;
import org.eclipse.osee.framework.ui.skynet.OseeStatusContributionItemFactory;
import org.eclipse.osee.framework.ui.skynet.util.DbConnectionExceptionComposite;
import org.eclipse.osee.framework.ui.skynet.widgets.GenericViewPart;
import org.eclipse.osee.framework.ui.skynet.widgets.XBranchSelectWidget;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

/**
 * @author Donald G. Dunne
 */
public class CoverageNavigateView extends GenericViewPart {

   public static final String VIEW_ID = "org.eclipse.osee.coverage.navigate.CoverageNavigateView";
   private XNavigateComposite xNavComp;
   private XBranchSelectWidget xBranchSelectWidget;

   private Composite comp;

   @Override
   public void createPartControl(Composite parent) {
      if (DbConnectionExceptionComposite.dbConnectionIsOk(parent)) {

         comp = new Composite(parent, SWT.None);
         comp.setLayout(ALayout.getZeroMarginLayout());
         comp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

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
                  IOseeBranch selectedBranch = xBranchSelectWidget.getData();
                  if (selectedBranch != null) {
                     CoverageUtil.setNavigatorSelectedBranch(selectedBranch);
                  }
               } catch (Exception ex) {
                  OseeLog.log(getClass(), Level.SEVERE, ex);
               }
            }

         });
         CoverageUtil.addBranchChangeListener(new Listener() {
            @Override
            public void handleEvent(Event event) {
               if (Widgets.isAccessible(xBranchSelectWidget.getControl())) {
                  xBranchSelectWidget.setSelection(CoverageUtil.getBranch());
               }
            }
         });
         xNavComp = new XNavigateComposite(new CoverageNavigateViewItems(), comp, SWT.NONE);
         GridData gridData = new GridData(GridData.FILL_BOTH);
         gridData.widthHint = 100;
         xNavComp.setLayoutData(gridData);

         createActions();

         Label label = new Label(xNavComp, SWT.None);
         String str = getWhoAmI();
         if (CoverageUtil.isAdmin()) {
            str += " - Admin";
         }
         if (!str.equals("")) {
            if (CoverageUtil.isAdmin()) {
               label.setForeground(Displays.getSystemColor(SWT.COLOR_RED));
            } else {
               label.setForeground(Displays.getSystemColor(SWT.COLOR_BLUE));
            }
         }
         label.setText(str);
         label.setToolTipText(str);
         gridData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER | GridData.VERTICAL_ALIGN_CENTER);
         gridData.heightHint = 15;
         label.setLayoutData(gridData);

         HelpUtil.setHelp(xNavComp, CoverageHelpContext.NAVIGATOR);

         xNavComp.refresh();
         xNavComp.getFilteredTree().getFilterControl().setFocus();

         parent.getParent().layout(true);
         parent.layout(true);

         OseeStatusContributionItemFactory.addTo(this, false);
         setFocusWidget(comp);
      }
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

   protected void createActions() {
      Action refreshAction = new Action("Refresh") {

         @Override
         public void run() {
            xNavComp.refresh();
         }
      };
      refreshAction.setImageDescriptor(ImageManager.getImageDescriptor(PluginUiImage.REFRESH));
      refreshAction.setToolTipText("Refresh");

      IToolBarManager toolbarManager = getViewSite().getActionBars().getToolBarManager();
      toolbarManager.add(refreshAction);
   }

   public void refresh() {
      Displays.ensureInDisplayThread(new Runnable() {

         @Override
         public void run() {
            xNavComp.refresh();
         }
      });
   }

}
