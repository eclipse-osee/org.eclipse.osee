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
package org.eclipse.osee.framework.ui.service.control.view;

import org.eclipse.osee.framework.jdk.core.util.StringFormat;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.ui.service.control.ControlPlugin;
import org.eclipse.osee.framework.ui.service.control.actions.HideLookupsAction;
import org.eclipse.osee.framework.ui.service.control.actions.KillServiceAction;
import org.eclipse.osee.framework.ui.service.control.actions.OpenLaunchWizard;
import org.eclipse.osee.framework.ui.service.control.actions.RefreshDataStore;
import org.eclipse.osee.framework.ui.service.control.menu.MenuBuilder;
import org.eclipse.osee.framework.ui.service.control.widgets.ManagerMain;
import org.eclipse.osee.framework.ui.skynet.ats.IActionable;
import org.eclipse.osee.framework.ui.skynet.ats.OseeAts;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Roberto E. Escobar
 */
public class ServiceManagerView extends ViewPart implements IActionable {

   public static final String VIEW_ID = "org.eclipse.osee.framework.ui.service.control.view.ServiceManagerView";
   private ManagerMain managerMain;

   public void createPartControl(Composite parent) {
      Composite composite = new Composite(parent, SWT.NONE);
      composite.setLayout(new GridLayout());
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      managerMain = new ManagerMain(composite, SWT.NONE);

      createServicesViewerPopUp();
      createLookupViewerPopUp();

      this.setContentDescription("Jini Groups { " + StringFormat.commaSeparate(ConfigUtil.getConfigFactory().getOseeConfig().getJiniServiceGroups()) + " }");

      OseeAts.addBugToViewToolbar(this, this, ControlPlugin.getInstance(), VIEW_ID, "Service Manager");
   }

   private void createServicesViewerPopUp() {
      MenuBuilder menuBuilder = new MenuBuilder(this);
      menuBuilder.addAction(new OpenLaunchWizard(managerMain));
      menuBuilder.addAction(new RefreshDataStore(managerMain));
      menuBuilder.addAction(new KillServiceAction(managerMain));
      menuBuilder.contributeToActionBars();
      menuBuilder.createPopUpMenu(managerMain.getServicesViewer().getViewer());
   }

   private void createLookupViewerPopUp() {
      MenuBuilder menuBuilder = new MenuBuilder(this);
      menuBuilder.addAction(new HideLookupsAction(managerMain));
      menuBuilder.addAction(new RefreshDataStore(managerMain));
      menuBuilder.createPopUpMenu(managerMain.getLookupViewer().getViewer());
   }

   public String getActionDescription() {
      return "";
   }

   @Override
   public void dispose() {
      super.dispose();
      if (managerMain != null) {
         managerMain.dispose();
      }

   }

   @Override
   public void setFocus() {
      managerMain.setFocus();
   }
}