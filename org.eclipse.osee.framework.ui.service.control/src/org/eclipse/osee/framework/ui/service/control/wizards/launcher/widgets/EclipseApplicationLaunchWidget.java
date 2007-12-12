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
import java.net.URL;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.ui.service.control.ControlPlugin;
import org.eclipse.osee.framework.ui.service.control.jobs.EclipseApplicationLaunchJob;
import org.eclipse.osee.framework.ui.service.control.jobs.TextDisplayHelper;
import org.eclipse.osee.framework.ui.service.control.wizards.launcher.ServiceLaunchingInformation;
import org.eclipse.osee.framework.ui.service.control.wizards.launcher.data.JiniGroupSelector;
import org.eclipse.osee.framework.ui.service.control.wizards.launcher.data.ServiceItem;
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
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.osgi.framework.Bundle;

/**
 * @author Roberto E. Escobar
 */
public class EclipseApplicationLaunchWidget implements ILaunchWidget {

   private Control control;
   private ServiceLaunchingInformation serviceInfo;
   private FormattedText serviceInfoText;
   private FormattedText executionResultText;
   private StyledText javaCompilerText;
   private File latestPlugin;
   private File localLocation;
   private String javaCompiler;
   private Shell shell;
   private JiniGroupSelector groupSelector;
   private TextDisplayHelper displayHelper;

   public EclipseApplicationLaunchWidget(ServiceLaunchingInformation serviceInfo) {
      this.serviceInfo = serviceInfo;
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

      createControlsArea(infoComposite);
      createJavaCompilerArea(infoComposite);

      createExecutionArea(composite);

      int[] weights = null;
      if (false != serviceInfo.getServiceItem().isJiniGroupRequired()) {
         groupSelector = new JiniGroupSelector();
         groupSelector.createJiniGroupWidget(infoComposite);
         weights = new int[] {4, 6};
      } else {
         weights = new int[] {4, 6};
      }

      composite.setWeights(weights);
      control = composite;
      displayHelper = new TextDisplayHelper(executionResultText);
   }

   private void createControlsArea(Composite parent) {
      Group group = new Group(parent, SWT.NONE);
      group.setLayout(new GridLayout());
      group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      group.setText("Service Details");

      serviceInfoText = new FormattedText(group, SWT.NONE, 100, 400, false);
      refresh();
   }

   private void createExecutionArea(Composite parent) {
      Group group = new Group(parent, SWT.NONE);
      group.setLayout(new GridLayout());
      group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      group.setText("Execution Log");

      executionResultText = new FormattedText(group, SWT.NONE, 200, 400);
      executionResultText.setTextAreaBackground(SWT.COLOR_WHITE);
      executionResultText.getStyledText().setEditable(false);

      Composite composite = new Composite(group, SWT.NONE);
      composite.setLayout(new GridLayout(2, true));
      composite.setLayoutData(new GridData(SWT.END, SWT.END, true, false));

      Button clear = new Button(composite, SWT.NONE);
      clear.setLayoutData(new GridData(SWT.END, SWT.END, false, false));
      clear.setText("Clear");
      clear.setToolTipText("Clear the execution status window");
      clear.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            executionResultText.clearTextArea();
         }

      });

      Button execute = new Button(composite, SWT.NONE);
      execute.setLayoutData(new GridData(SWT.END, SWT.END, false, false));
      execute.setText("Execute");
      execute.setToolTipText("Executes service as an Eclipse Application");
      execute.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            getEclipseInformation();
            javaCompiler = javaCompilerText.getText();
            if (null != groupSelector) {
               serviceInfo.getServiceItem().setJiniGroup(groupSelector.getJiniGroupVmArg());
            }
            Job job =
                  new EclipseApplicationLaunchJob("Eclipse Application Launch", javaCompiler, latestPlugin,
                        serviceInfo, displayHelper);
            job.setUser(true);
            job.setPriority(Job.LONG);
            job.schedule();
         }
      });
   }

   private void createJavaCompilerArea(Composite parent) {
      Group group = new Group(parent, SWT.NONE);
      group.setLayout(new GridLayout(2, false));
      group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
      group.setText("Select a java runtime location");

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

   private void getEclipseInformation() {
      latestPlugin = null;
      try {
         Bundle bundle = Platform.getBundle(serviceInfo.getServiceItem().getPlugin());
         URL url = bundle.getEntry("/");
         URL resolvedURL = FileLocator.resolve(url);
         latestPlugin = new File(resolvedURL.getFile());
      } catch (Exception ex) {
         if (executionResultText != null && !executionResultText.isDisposed()) {
            executionResultText.addText(ControlPlugin.getStackMessages(ex) + "\n\n", SWT.NORMAL, SWT.COLOR_RED);
         }
      }

      localLocation = new File(Platform.getInstallLocation().getURL().getFile());
   }

   public Control getControl() {
      return control;
   }

   public void dispose() {
      if (control != null && !control.isDisposed()) {
         control.dispose();
      }
      if (groupSelector != null) {
         groupSelector.dispose();
      }
      displayHelper.disposeProcessHandling();
   }

   public void refresh() {
      getEclipseInformation();
      serviceInfoText.clearTextArea();

      serviceInfoText.addText("\tService Name:\t", SWT.BOLD, SWT.COLOR_DARK_BLUE, true);
      serviceInfoText.addText(serviceInfo.getServiceItem().getName() + "\n");
      serviceInfoText.addText("\tPlugin:\t", SWT.BOLD, SWT.COLOR_DARK_BLUE, true);
      serviceInfoText.addText((latestPlugin != null ? latestPlugin.toString() : "<Plugin Not Available>") + "\n");
      serviceInfoText.addText("\tEclipse Install Location:\t", SWT.BOLD, SWT.COLOR_DARK_BLUE, true);
      serviceInfoText.addText(localLocation.toString() + "\n");
      serviceInfoText.addText("\tExecution String:\t", SWT.BOLD, SWT.COLOR_DARK_BLUE, true);

      String execString = serviceInfo.getServiceItem().getLocalExecution().replaceAll(ServiceItem.EXEC_SEPARATOR, " ");
      execString = execString.replace("java", "<JAVA_COMPILER>");

      serviceInfoText.addText(execString + "\n");
   }
}
