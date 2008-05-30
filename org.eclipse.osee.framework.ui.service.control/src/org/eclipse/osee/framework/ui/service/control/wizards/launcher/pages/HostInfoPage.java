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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.ui.service.control.ControlPlugin;
import org.eclipse.osee.framework.ui.service.control.wizards.launcher.ServiceLaunchDataPersist;
import org.eclipse.osee.framework.ui.service.control.wizards.launcher.ServiceLaunchingInformation;
import org.eclipse.osee.framework.ui.swt.DynamicWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

public class HostInfoPage extends DynamicWizardPage {
   private Combo hostName;
   private Text userName;
   private ServiceLaunchingInformation serviceInfo;
   private List<String> hosts;

   public HostInfoPage(String pageName, String previous, String next, ServiceLaunchingInformation serviceInfo) {
      super(pageName, previous, next);
      this.serviceInfo = serviceInfo;
      this.hosts = new ArrayList<String>();
      setTitle("Host Information");
      setDescription("Please select a remote host to connect to and input a valid username.");
      setPageComplete(false);
   }

   public void createControl(Composite parent) {
      Composite composite = new Composite(parent, SWT.NULL);
      GridLayout gridLayout = new GridLayout();
      gridLayout.numColumns = 1;
      composite.setLayout(gridLayout);
      GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
      gridData.horizontalSpan = 1;
      gridData.grabExcessHorizontalSpace = true;
      composite.setLayoutData(gridData);
      createHostInfoGroup(composite);
      createUserInfoGroup(composite);
      setControl(composite);
   }

   private void createHostInfoGroup(Composite parent) {
      Group hostinfo = new Group(parent, SWT.NONE);
      hostinfo.setText("Host: ");

      GridLayout gridLayout = new GridLayout();
      gridLayout.numColumns = 1;
      hostinfo.setLayout(gridLayout);

      GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
      gridData.horizontalSpan = 1;
      gridData.grabExcessHorizontalSpace = true;
      hostinfo.setLayoutData(gridData);

      hostName = new Combo(hostinfo, SWT.SINGLE | SWT.BORDER);
      hostName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      serviceInfo.setAvailableHosts(serviceInfo.getServiceItem().getHosts());
      hosts = serviceInfo.getAvailableHosts();
      for (String name : hosts) {
         if (name != null && !name.equals("")) {
            hostName.add(name);
         }
      }

      hostName.addModifyListener(new ModifyListener() {
         public void modifyText(ModifyEvent e) {
            tryToGoToNext();
         }
      });
      hostName.addSelectionListener(new SelectionListener() {

         public void widgetSelected(SelectionEvent e) {
            tryToGoToNext();
         }

         public void widgetDefaultSelected(SelectionEvent e) {
         }

      });
   }

   private void createUserInfoGroup(Composite parent) {
      Group userinfo = new Group(parent, SWT.NONE);
      userinfo.setText("User Name: ");

      GridLayout gridLayout = new GridLayout();
      gridLayout.numColumns = 2;
      userinfo.setLayout(gridLayout);

      GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
      gridData.horizontalSpan = 1;
      userinfo.setLayoutData(gridData);

      userName = new Text(userinfo, SWT.SINGLE | SWT.BORDER);
      userName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      String name;
      try {
         name = SkynetAuthentication.getUser().getName();
      } catch (Exception ex) {
         name = System.getProperty("user.name");
      }
      userName.setText(name);
      userName.addModifyListener(new ModifyListener() {
         public void modifyText(ModifyEvent e) {
            tryToGoToNext();
         }
      });
   }

   private void tryToGoToNext() {
      if (Strings.isValid(userName.getText()) && Strings.isValid(this.hostName.getText())) {
         setPageComplete(true);
      }
   }

   @Override
   public void setVisible(boolean visible) {
      serviceInfo.setAvailableHosts(serviceInfo.getServiceItem().getHosts());
      hosts = serviceInfo.getAvailableHosts();
      if (hostName != null && !hostName.isDisposed()) {
         hostName.removeAll();
         for (String name : hosts) {
            if (name != null && !name.equals("")) {
               hostName.add(name);
            }
         }
         hostName.select(0);
      }

      super.setVisible(visible);
   }

   public Combo getHostNameCombo() {
      return hostName;
   }

   @Override
   public boolean onNextPressed() {
      serviceInfo.setUser(userName.getText());
      serviceInfo.setSelectedHost(hostName.getText());
      serviceInfo.setUnzipLocation(serviceInfo.getServiceItem().getUnzipLocation() + "/" + serviceInfo.getServiceItem().getPlugin());

      ServiceLaunchDataPersist data = ServiceLaunchDataPersist.getInstance();
      data.saveHostName(hostName.getText());
      data.saveLastServiceLaunched(serviceInfo.getServiceItem().getName());

      try {
         serviceInfo.connectToRemoteHost();
      } catch (Exception ex) {
         StringWriter error = new StringWriter();
         ex.printStackTrace(new PrintWriter(error));
         MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
               "Unable to connect to the remote host", ControlPlugin.getStackMessages(ex));
         return false;
      }
      return true;
   }

}
