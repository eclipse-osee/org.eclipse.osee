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

import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.ui.service.control.ControlPlugin;
import org.eclipse.osee.framework.ui.service.control.jobs.TextDisplayHelper;
import org.eclipse.osee.framework.ui.service.control.jobs.UploadRemoteFileJob;
import org.eclipse.osee.framework.ui.service.control.wizards.launcher.ServiceLaunchingInformation;
import org.eclipse.osee.framework.ui.swt.DynamicWizardPage;
import org.eclipse.osee.framework.ui.swt.FormattedText;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

public class UploadPage extends DynamicWizardPage {

   public enum LabelEnum {
      Service, User, Host, Host_Upload_Location;

      public String toString() {
         return this.name().replaceAll("_", " ");
      }
   }

   private Map<LabelEnum, Text> dataMap;

   private ServiceLaunchingInformation serviceInfo;
   private FormattedText cmdText;
   private ProgressBar progress;

   private static final Image HELP_IMAGE = ControlPlugin.getInstance().getImage("help.gif");

   public UploadPage(String pageName, String previous, String next, ServiceLaunchingInformation serviceInfo) {
      super(pageName, previous, next);
      this.serviceInfo = serviceInfo;
      this.dataMap = new HashMap<LabelEnum, Text>();
      setTitle("Upload Service Selection");
      setDescription("An scp client will upload the files required by the service to the host machine at the given location.");
      setPageComplete(true);
   }

   public void createControl(Composite parent) {
      Group composite = new Group(parent, SWT.NULL);
      composite.setLayout(new GridLayout());
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      composite.setText("Upload Info");

      createLabelArea(composite);

      cmdText = new FormattedText(composite, SWT.NONE, 200, 300);
      cmdText.getStyledText().setEditable(false);

      createUploadBarArea(composite);

      setControl(composite);
   }

   private void createLabelArea(Composite parent) {
      Composite composite = new Composite(parent, SWT.NONE);
      composite.setLayout(new GridLayout(2, false));
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      dataMap.clear();
      for (LabelEnum labelEnum : LabelEnum.values()) {
         new Label(composite, SWT.NONE).setText(labelEnum.toString() + ":");

         Text updateable;
         if (labelEnum.equals(LabelEnum.Host_Upload_Location)) {
            updateable = new Text(composite, SWT.SINGLE | SWT.BORDER);
            updateable.setEditable(true);
         } else {
            updateable = new Text(composite, SWT.SINGLE);
            updateable.setEditable(false);
         }
         updateable.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
         dataMap.put(labelEnum, updateable);
      }
   }

   public void createUploadBarArea(Composite parent) {
      Composite composite = new Composite(parent, SWT.NONE);
      composite.setLayout(new GridLayout(2, false));
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      progress = new ProgressBar(composite, SWT.HORIZONTAL);
      progress.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
      progress.setEnabled(true);
      progress.setMinimum(0);
      progress.setMaximum(20);
      progress.setSelection(0);

      Composite buttonComposite = new Composite(composite, SWT.NONE);
      buttonComposite.setLayout(new GridLayout(3, true));
      buttonComposite.setLayoutData(new GridData(SWT.END, SWT.END, false, false));

      Button clearText = new Button(buttonComposite, SWT.PUSH);
      clearText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
      clearText.setText("Clear");
      clearText.setToolTipText("Clear the execution status window");
      clearText.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            progress.setSelection(0);
            cmdText.clearTextArea();
         }

      });

      Button upload = new Button(buttonComposite, SWT.NONE);
      upload.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
      upload.setText("Upload");
      upload.setToolTipText("Uploads files to the remote host");
      upload.addSelectionListener(new SelectionAdapter() {

         public void widgetSelected(SelectionEvent e) {
            PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
               public void run() {
                  Job job =
                        new UploadRemoteFileJob("Uploading Files", serviceInfo, new TextDisplayHelper(cmdText),
                              progress, dataMap);
                  job.setUser(true);
                  job.setPriority(Job.LONG);
                  job.schedule();
               }
            });
         }
      });

      final Label help = new Label(buttonComposite, SWT.NONE);
      help.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
      help.setImage(HELP_IMAGE);
      help.setToolTipText("Double-Click to open help dialog.");
      help.addMouseListener(new MouseListener() {

         public void mouseDoubleClick(MouseEvent e) {
            MessageDialog.openInformation(
                  PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                  "Upload Help",
                  "Upload Hints: \n\n" + "1. If the service binds to a static port, ensure that the \n" + "service to be launched is not running on the target machine.\n\n" + "2. If the remote directory used to upload files to exists previously, \n" + "make sure that the user login has write/execute permissions for that folder.\n");
         }

         public void mouseDown(MouseEvent e) {
         }

         public void mouseUp(MouseEvent e) {
         }

      });
   }

   @Override
   public void setVisible(boolean visible) {
      this.dataMap.get(LabelEnum.Service).setText(serviceInfo.getServiceItem().getName());
      this.dataMap.get(LabelEnum.User).setText(serviceInfo.getUser());
      this.dataMap.get(LabelEnum.Host).setText(serviceInfo.getSelectedHost());

      String unzipLocation = serviceInfo.getUnzipLocation();
      if (unzipLocation == null || unzipLocation.equals("")) {
         serviceInfo.setUnzipLocation(serviceInfo.getServiceItem().getUnzipLocation() + "/" + serviceInfo.getServiceItem().getPlugin());
      }
      this.dataMap.get(LabelEnum.Host_Upload_Location).setText(serviceInfo.getUnzipLocation());
      super.setVisible(visible);
   }

   @Override
   public boolean onNextPressed() {
      serviceInfo.setUnzipLocation(dataMap.get(LabelEnum.Host_Upload_Location).getText());
      return super.onNextPressed();
   }

}
