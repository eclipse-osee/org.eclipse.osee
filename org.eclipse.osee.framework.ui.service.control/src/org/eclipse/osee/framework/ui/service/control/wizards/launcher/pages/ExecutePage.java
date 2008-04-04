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
import org.eclipse.osee.framework.ui.service.control.ControlPlugin;
import org.eclipse.osee.framework.ui.service.control.wizards.launcher.ServiceLaunchingInformation;
import org.eclipse.osee.framework.ui.service.control.wizards.launcher.data.JiniGroupSelector;
import org.eclipse.osee.framework.ui.service.control.wizards.launcher.data.ServiceItem;
import org.eclipse.osee.framework.ui.swt.DynamicWizardPage;
import org.eclipse.osee.framework.ui.swt.FormattedText;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

public class ExecutePage extends DynamicWizardPage {

   private static final String TEMPORARY_JINI_GROUP = "<USE COMBO BOX FROM ABOVE>";
   private enum LabelEnum {
      Service, User, Host;
   }

   private Map<LabelEnum, Text> dataMap;
   private FormattedText cmdText;
   private FormattedText updateText;
   private ServiceLaunchingInformation serviceInfo;
   private JiniGroupSelector jiniGroupSelector;

   public ExecutePage(String pageName, String previous, String next, ServiceLaunchingInformation serviceInfo) {
      super(pageName, previous, next);
      this.serviceInfo = serviceInfo;
      this.dataMap = new HashMap<LabelEnum, Text>();
      setTitle("Launch Service");
      setDescription("Through ssh the remote host is accessed and a list of commands are run that launch the selected service.");
      setPageComplete(true);
   }

   public void createControl(Composite parent) {
      Group composite = new Group(parent, SWT.NULL);
      composite.setLayout(new GridLayout());
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      composite.setText("Upload Info");

      createLabelArea(composite);
      if (false != serviceInfo.getServiceItem().isJiniGroupRequired()) {
         jiniGroupSelector = new JiniGroupSelector();
         jiniGroupSelector.createJiniGroupWidget(composite);
      }
      createExecuteInfoGroup(composite);
      createButtonArea(composite);

      setControl(composite);
   }

   private void createLabelArea(Composite parent) {
      Composite composite = new Composite(parent, SWT.NONE);
      composite.setLayout(new GridLayout(2, false));
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      dataMap.clear();
      for (LabelEnum labelEnum : LabelEnum.values()) {
         new Label(composite, SWT.NONE).setText(labelEnum.toString() + ":");

         Text updateable = new Text(composite, SWT.SINGLE);
         updateable.setEditable(false);
         updateable.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
         dataMap.put(labelEnum, updateable);
      }
   }

   private void createExecuteInfoGroup(Composite parent) {
      SashForm sashForm = new SashForm(parent, SWT.NONE);
      sashForm.setLayout(new GridLayout());
      sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      sashForm.setOrientation(SWT.VERTICAL);
      sashForm.SASH_WIDTH = 3;

      Group group = new Group(sashForm, SWT.NONE);
      group.setLayout(new GridLayout());
      group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
      group.setText("Enter Commands To Execute:");

      cmdText = new FormattedText(group, SWT.NONE, 50, 100, true);
      cmdText.getStyledText().setBackground(PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_WHITE));
      cmdText.getStyledText().addModifyListener(new ModifyListener() {

         public void modifyText(ModifyEvent e) {
            String cmds = cmdText.getStyledText().getText();
            String[] cmdList = cmds.split("\r\n");
            for (int i = 0; i < cmdList.length; i++) {
               cmdList[i] = cmdList[i].trim();
            }
            serviceInfo.setExecCmds(cmdList);

         }

      });

      updateText = new FormattedText(sashForm, SWT.BORDER, 200, 300, false);
      sashForm.setWeights(new int[] {3, 7});
   }

   public void createButtonArea(Composite parent) {
      Composite buttonComposite = new Composite(parent, SWT.NONE);
      buttonComposite.setLayout(new GridLayout(2, true));
      buttonComposite.setLayoutData(new GridData(SWT.END, SWT.END, false, false));

      Button clearText = new Button(buttonComposite, SWT.PUSH);
      clearText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
      clearText.setText("Clear");
      clearText.setToolTipText("Clear the execution status window");
      clearText.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            updateText.clearTextArea();
         }

      });

      final Button execute = new Button(buttonComposite, SWT.PUSH);
      execute.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
      execute.setText("Execute");
      execute.setToolTipText("Executes commands speficied int the command window on the remote host");
      execute.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
               public void run() {
                  execute.setEnabled(false);
                  try {
                     String[] execCommands = serviceInfo.getExecCmds();
                     if (null != jiniGroupSelector) {
                        serviceInfo.getServiceItem().setJiniGroup(jiniGroupSelector.getJiniGroupVmArg());

                        for (int index = 0; index < execCommands.length; index++) {
                           String temp = execCommands[index];
                           if (temp.contains(TEMPORARY_JINI_GROUP)) {
                              execCommands[index] =
                                    temp.replace(TEMPORARY_JINI_GROUP, serviceInfo.getServiceItem().getJiniGroup());
                           }
                        }
                     }
                     String output = serviceInfo.getSSHConnection().executeCommandList(execCommands);
                     updateText.addText(output);
                  } catch (Exception ex) {
                     updateText.addText("\n" + ControlPlugin.getStackMessages(ex) + "\n", SWT.NORMAL, SWT.COLOR_RED);
                  }
                  execute.setEnabled(true);
               }
            });
         }
      });
   }

   @Override
   public void setVisible(boolean visible) {
      this.cmdText.clearTextArea();
      this.dataMap.get(LabelEnum.Service).setText(serviceInfo.getServiceItem().getName());
      this.dataMap.get(LabelEnum.User).setText(serviceInfo.getUser());
      this.dataMap.get(LabelEnum.Host).setText(serviceInfo.getSelectedHost());

      if (null != jiniGroupSelector) {
         serviceInfo.getServiceItem().setJiniGroup(TEMPORARY_JINI_GROUP);
      }

      String execute =
            "cd " + serviceInfo.getUnzipLocation() + "\n" + serviceInfo.getServiceItem().getRemoteExecution().replaceAll(
                  ServiceItem.EXEC_SEPARATOR, " ");

      this.cmdText.addText(execute);

      super.setVisible(visible);
   }

}
