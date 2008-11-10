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
package org.eclipse.osee.framework.ui.service.control.wizards.launcher;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.osee.framework.jdk.core.util.io.IZipEntryCompleteCallback;
import org.eclipse.osee.framework.ui.service.control.wizards.launcher.pages.LocalRemotePage;
import org.eclipse.osee.framework.ui.service.control.wizards.launcher.pages.ServicePage;
import org.eclipse.osee.framework.ui.swt.DynamicWizard;

public class ServiceLaunchWizard extends DynamicWizard implements IZipEntryCompleteCallback {

   private ServiceLaunchingInformation serviceInfo;

   public ServiceLaunchWizard() {
      serviceInfo = new ServiceLaunchingInformation();
   }

   @Override
   public boolean canFinish() {
      return serviceInfo.canFinish();
   }

   @Override
   public boolean performCancel() {
      return super.performCancel();
   }

   @Override
   public boolean performFinish() {
      if (!canFinish()) return false;
      boolean returnVal = true;
      ServiceLaunchDataPersist data = ServiceLaunchDataPersist.getInstance();
      data.saveHostName(serviceInfo.getSelectedHost());
      data.saveLastServiceLaunched(serviceInfo.getServiceItem().getName());
      return returnVal;
   }

   @Override
   public void addPages() {
      super.addPages();
      this.setWindowTitle("Service Launching");

      String servicePageTitle = "Service Page";
      String localRemoteTitle = "Location Selection";

      IWizardPage startingPage = new LocalRemotePage(localRemoteTitle, null, servicePageTitle, serviceInfo, this);
      this.addPage(startingPage);
      this.addPage(new ServicePage(servicePageTitle, localRemoteTitle, null, serviceInfo));
      this.setStartingPage(startingPage);
   }

   public void setValue(int i) {
   }

   public void setMinimum(int i) {
   }

   public void setMaximum(int i) {
   }
}
