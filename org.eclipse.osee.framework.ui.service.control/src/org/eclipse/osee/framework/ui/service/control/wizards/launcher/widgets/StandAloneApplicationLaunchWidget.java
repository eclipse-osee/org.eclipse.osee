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

package org.eclipse.osee.framework.ui.service.control.wizards.launcher.widgets;

import java.io.File;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.ui.service.control.ControlPlugin;
import org.eclipse.osee.framework.ui.service.control.jobs.StandAloneApplicationLaunchJob;
import org.eclipse.osee.framework.ui.service.control.jobs.TextDisplayHelper;
import org.eclipse.osee.framework.ui.service.control.wizards.launcher.ServiceLaunchWizard;
import org.eclipse.osee.framework.ui.service.control.wizards.launcher.ServiceLaunchingInformation;
import org.eclipse.osee.framework.ui.service.control.wizards.launcher.data.JiniGroupSelector;
import org.eclipse.osee.framework.ui.swt.FormattedText;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * @author Roberto E. Escobar
 */
public class StandAloneApplicationLaunchWidget implements ILaunchWidget {

   private Control control;
   private StyledText scriptOutputText;
   private StyledText javaCompilerText;
   private ServiceLaunchingInformation serviceInfo;
   // private ServiceLaunchWizard wizard;
   private Shell shell;
   private FormattedText cmdText;
   private String localUnzipLocation;
   private String javaCompiler;
   private ProgressBar progress;
   private JiniGroupSelector groupSelector;
   private TextDisplayHelper displayHelper;

   public StandAloneApplicationLaunchWidget(ServiceLaunchingInformation serviceInfo, ServiceLaunchWizard wizard) {
      this.serviceInfo = serviceInfo;
      // this.wizard = wizard;
      this.localUnzipLocation = "";
      this.javaCompiler = "";
   }

   public void create(Composite parent) {
      shell = parent.getShell();
      SashForm composite = new SashForm(parent, SWT.NONE);
      composite.setLayout(new GridLayout());
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      composite.SASH_WIDTH = 3;
      composite.setOrientation(SWT.VERTICAL);

      Composite infoComposite = new Composite(composite, SWT.NONE);
      infoComposite.setLayout(new GridLayout());
      infoComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      createStandaloneComponents(infoComposite);
      createJavaCompilerArea(infoComposite);

      createExecutionArea(composite);

      int[] weights = null;
      if (false != serviceInfo.getServiceItem().isJiniGroupRequired()) {
         groupSelector = new JiniGroupSelector();
         groupSelector.createJiniGroupWidget(infoComposite);
         weights = new int[] {4, 6};
      } else {
         weights = new int[] {3, 7};
      }
      composite.setWeights(weights);

      control = composite;
      displayHelper = new TextDisplayHelper(cmdText);
   }

   public Control getControl() {
      return control;
   }

   private void createExecutionArea(Composite parent) {
      Group group = new Group(parent, SWT.NONE);
      group.setLayout(new GridLayout());
      group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      group.setText("Execution Log");

      cmdText = new FormattedText(group, SWT.NONE, 200, 400);
      cmdText.setTextAreaBackground(SWT.COLOR_WHITE);
      cmdText.getStyledText().setEditable(false);

      Composite composite = new Composite(group, SWT.NONE);
      composite.setLayout(new GridLayout(2, false));
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      progress = new ProgressBar(composite, SWT.HORIZONTAL);
      progress.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
      progress.setEnabled(true);
      progress.setMinimum(0);
      progress.setMaximum(20);
      progress.setSelection(0);

      Composite buttonComposite = new Composite(composite, SWT.NONE);
      buttonComposite.setLayout(new GridLayout(2, true));
      buttonComposite.setLayoutData(new GridData(SWT.END, SWT.END, false, false));

      Button clear = new Button(buttonComposite, SWT.NONE);
      clear.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
      clear.setText("Clear");
      clear.setToolTipText("Clear the execution status window");
      clear.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            cmdText.clearTextArea();
            progress.setSelection(0);
         }
      });

      Button execute = new Button(buttonComposite, SWT.NONE);
      execute.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
      execute.setText("Execute");
      execute.setToolTipText("Execute the application");
      execute.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
               public void run() {
                  File localLocation =
                        new File(
                              localUnzipLocation + File.separator + serviceInfo.getServiceItem().getPlugin() + File.separator);
                  javaCompiler = javaCompilerText.getText();
                  if (null != groupSelector) {
                     serviceInfo.getServiceItem().setJiniGroup(groupSelector.getJiniGroupVmArg());
                  }
                  Job job =
                        new StandAloneApplicationLaunchJob("Launch Stand Alone Application", javaCompiler,
                              localLocation, serviceInfo, displayHelper, progress);
                  job.setUser(true);
                  job.setPriority(Job.LONG);
                  job.schedule();
               }
            });
         }
      });
   }

   private void createStandaloneComponents(Composite parent) {
      Group group = new Group(parent, SWT.NONE);
      group.setLayout(new GridLayout(2, false));
      group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
      group.setText("Select a location in the local file system to launch the Application");

      final String homedir = System.getProperty("user.home") + File.separator + "oseeservices";
      localUnzipLocation = homedir;

      scriptOutputText = new StyledText(group, SWT.BORDER);
      scriptOutputText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
      scriptOutputText.setText(homedir);

      scriptOutputText.addModifyListener(new ModifyListener() {
         public void modifyText(ModifyEvent e) {
            localUnzipLocation = scriptOutputText.getText();
         }
      });

      Button fileDialog = new Button(group, SWT.NONE);
      fileDialog.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
      fileDialog.setImage(ControlPlugin.getInstance().getImage("file.gif"));
      fileDialog.addSelectionListener(new SelectionAdapter() {
         public void widgetSelected(SelectionEvent e) {
            DirectoryDialog dialog = new DirectoryDialog(shell, SWT.OPEN);
            String defaultDir = scriptOutputText.getText();
            File dir = new File(defaultDir);
            if (dir.isFile() || dir.isDirectory())
               dialog.setFilterPath(defaultDir);
            else {
               dialog.setFilterPath(homedir);
            }

            String result = dialog.open();
            if (result != null && !result.equals("")) {
               scriptOutputText.setText(result);
               localUnzipLocation = result;
            }
         }
      });
   }

   private void createJavaCompilerArea(Composite parent) {
      Group group = new Group(parent, SWT.NONE);
      group.setLayout(new GridLayout(2, false));
      group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
      group.setText("Select a java compiler location");

      final String javaHome = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
      javaCompiler = javaHome;

      javaCompilerText = new StyledText(group, SWT.BORDER);
      javaCompilerText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
      javaCompilerText.setText(javaHome);

      javaCompilerText.addModifyListener(new ModifyListener() {
         public void modifyText(ModifyEvent e) {
            javaCompiler = javaCompilerText.getText();
         }
      });

      Button fileDialog = new Button(group, SWT.NONE);
      fileDialog.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
      fileDialog.setImage(ControlPlugin.getInstance().getImage("file.gif"));
      fileDialog.addSelectionListener(new SelectionAdapter() {

         public void widgetSelected(SelectionEvent e) {
            FileDialog dialog = new FileDialog(shell, SWT.OPEN);

            String defaultDir = javaCompilerText.getText();
            File dir = new File(defaultDir);
            if (dir.isFile() || dir.isDirectory())
               dialog.setFilterPath(defaultDir);
            else {
               dialog.setFilterPath(javaHome);
            }

            dialog.setFilterExtensions(new String[] {"java"});

            dialog.setFilterNames(new String[] {"java"});

            String result = dialog.open();
            if (result != null && !result.equals("")) {
               javaCompilerText.setText(result);
               javaCompiler = result;
            }
         }
      });
   }

   public void dispose() {
      if (scriptOutputText != null && !scriptOutputText.isDisposed()) {
         scriptOutputText.dispose();
      }
      if (control != null && !control.isDisposed()) {
         control.dispose();
      }
      if (groupSelector != null) {
         groupSelector.dispose();
      }
      displayHelper.disposeProcessHandling();
   }

   public void refresh() {
   }
}
