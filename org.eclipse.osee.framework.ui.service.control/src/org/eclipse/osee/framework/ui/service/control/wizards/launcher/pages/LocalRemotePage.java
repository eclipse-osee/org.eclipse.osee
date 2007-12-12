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
package org.eclipse.osee.framework.ui.service.control.wizards.launcher.pages;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.osee.framework.ui.service.control.wizards.launcher.ServiceLaunchWizard;
import org.eclipse.osee.framework.ui.service.control.wizards.launcher.ServiceLaunchingInformation;
import org.eclipse.osee.framework.ui.service.control.wizards.launcher.data.ServiceLaunchConfig;
import org.eclipse.osee.framework.ui.swt.DynamicWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

public class LocalRemotePage extends DynamicWizardPage {

   private ServiceLaunchingInformation serviceInfo;
   private Button local;
   private Button remote;
   private ServiceLaunchWizard wizard;
   private Composite composite;

   public LocalRemotePage(String pageName, String previous, String next, ServiceLaunchingInformation serviceInfo, ServiceLaunchWizard wizard) {
      super(pageName, previous, next);
      this.serviceInfo = serviceInfo;
      setTitle("Local/Remote Launch");
      setDescription("Please select local or remote for the launching of the selected service.");
      setPageComplete(true);
      this.wizard = wizard;
   }

   public void createControl(Composite parent) {
      composite = new Composite(parent, SWT.NONE);
      composite.setLayout(new GridLayout());
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      createHostInfoGroup(composite);
      setControl(composite);
   }

   private void createHostInfoGroup(Composite parent) {
      Group group = new Group(parent, SWT.NONE);
      group.setLayout(new GridLayout());
      group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      group.setText("Launch Location");

      local = new Button(group, SWT.RADIO);
      local.setText("Local");
      local.setSelection(true);
      local.setEnabled(false);

      remote = new Button(group, SWT.RADIO);
      remote.setText("Remote");
      remote.setEnabled(false);

      if (ServiceLaunchConfig.getInstance().getLocalServiceItems().size() > 0) {
         local.setEnabled(true);
      } else {
         remote.setSelection(true);
      }

      if (ServiceLaunchConfig.getInstance().getRemoteServiceItems().size() > 0) {
         remote.setEnabled(true);
      } else {
         local.setSelection(true);
      }

   }

   @Override
   public void setVisible(boolean visible) {
      super.setVisible(visible);
   }

   @Override
   public boolean canFlipToNextPage() {
      return true;
   }

   @Override
   public boolean onNextPressed() {
      ServicePage servicePage = ((ServicePage) this.getNextPage());
      servicePage.setIsLocal(local.getSelection());
      serviceInfo.setIsLocal(local.getSelection());

      if (local.getSelection()) {
         String unzipLocationTitle = "Execute Local Service";
         servicePage.setNextPage(unzipLocationTitle);
         wizard.addPage(new LocalLaunchPage(unzipLocationTitle, servicePage.getName(), "", serviceInfo));
      } else {
         String hostInformationTitle = "Host Information";
         String uploadServiceTitle = "Upload service";
         String executeServiceTitle = "Execute Remote Service";

         servicePage.setNextPage(hostInformationTitle);
         IWizardPage[] pagesToAdd =
               new IWizardPage[] {
                     new HostInfoPage(hostInformationTitle, servicePage.getName(), uploadServiceTitle, serviceInfo),
                     new UploadPage(uploadServiceTitle, hostInformationTitle, executeServiceTitle, serviceInfo),
                     new ExecutePage(executeServiceTitle, uploadServiceTitle, "", serviceInfo)};
         for (IWizardPage page : pagesToAdd) {
            wizard.addPage(page);
         }
      }
      return true;
   }
}
