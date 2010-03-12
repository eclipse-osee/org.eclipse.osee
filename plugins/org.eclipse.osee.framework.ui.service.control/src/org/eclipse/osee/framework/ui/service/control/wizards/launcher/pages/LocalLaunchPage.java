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
import java.util.Set;
import org.eclipse.osee.framework.ui.service.control.wizards.launcher.ServiceLaunchWizard;
import org.eclipse.osee.framework.ui.service.control.wizards.launcher.ServiceLaunchingInformation;
import org.eclipse.osee.framework.ui.service.control.wizards.launcher.widgets.EclipseApplicationLaunchWidget;
import org.eclipse.osee.framework.ui.service.control.wizards.launcher.widgets.ILaunchWidget;
import org.eclipse.osee.framework.ui.service.control.wizards.launcher.widgets.StandAloneApplicationLaunchWidget;
import org.eclipse.osee.framework.ui.swt.DynamicWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

/**
 * @author Roberto E. Escobar
 */
public class LocalLaunchPage extends DynamicWizardPage {

   private ServiceLaunchingInformation serviceInfo;

   private Button eclipseApplicationButton;
   private Button standAloneButton;
   private StackLayout stackLayout;
   private Composite stackedComposite;
   private Map<Button, ILaunchWidget> widgets;

   public LocalLaunchPage(String pageName, String previous, String next, ServiceLaunchingInformation serviceInfo) {
      super(pageName, previous, next);
      this.serviceInfo = serviceInfo;
      setTitle("Local Launch");
      setDescription("Select a local launch method to execute service.");
      setPageComplete(true);
   }

   public void createControl(Composite parent) {
      Composite composite = new Composite(parent, SWT.NONE);
      composite.setLayout(new GridLayout());
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      createLaunchTypeButtonArea(composite);

      stackedComposite = new Composite(composite, SWT.NONE);
      stackedComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      stackLayout = new StackLayout();
      stackedComposite.setLayout(stackLayout);

      ServiceLaunchWizard wizard = (ServiceLaunchWizard) this.getWizard();

      widgets = new HashMap<Button, ILaunchWidget>();
      widgets.put(eclipseApplicationButton, new EclipseApplicationLaunchWidget(serviceInfo));
      widgets.put(standAloneButton, new StandAloneApplicationLaunchWidget(serviceInfo, wizard));

      Set<Button> keys = widgets.keySet();
      for (Button key : keys) {
         ILaunchWidget widget = widgets.get(key);
         widget.create(stackedComposite);
      }
      determineDefaultSelection();
      registerListeners();

      setPageComplete(true);
      setControl(composite);
   }

   private void determineDefaultSelection() {
      Button key = null;
      if (serviceInfo.getServiceItem().isStandAloneAllowed()) {
         standAloneButton.setEnabled(true);
         standAloneButton.setSelection(true);
         key = standAloneButton;
      }

      if (serviceInfo.getServiceItem().isEclipseAppAllowed()) {
         eclipseApplicationButton.setEnabled(true);
         eclipseApplicationButton.setSelection(true);
         key = eclipseApplicationButton;
      }

      if (standAloneButton.getSelection() && eclipseApplicationButton.getSelection()) {
         standAloneButton.setSelection(false);
      }

      if (key != null) {
         stackLayout.topControl = widgets.get(key).getControl();
         stackedComposite.layout();
      }
   }

   @Override
   public void setVisible(boolean visible) {
      for (Button button : widgets.keySet()) {
         widgets.get(button).refresh();
         button.setEnabled(false);
         button.setSelection(false);
      }
      determineDefaultSelection();
      super.setVisible(visible);
   }

   private void createLaunchTypeButtonArea(Composite parent) {
      Group group = new Group(parent, SWT.NONE);
      group.setLayout(new GridLayout());
      group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
      group.setText("Select how to launch the application locally");

      eclipseApplicationButton = new Button(group, SWT.RADIO);
      eclipseApplicationButton.setEnabled(false);
      eclipseApplicationButton.setText("As an Eclipse Application");
      eclipseApplicationButton.setToolTipText("This option launches the selected service\n" + "as an Eclipse application. The executable will be selected from \n" + "the latest plugin folder under the eclipse installation location.\n" + "The Eclipse framework will be used to execute the application.");

      standAloneButton = new Button(group, SWT.RADIO);
      standAloneButton.setEnabled(false);
      standAloneButton.setText("As a Standalone Application");
      standAloneButton.setToolTipText("This option launches the selected service\n" + "as a standalone application under the specified directory.");
   }

   private void registerListeners() {
      eclipseApplicationButton.addSelectionListener(new SelectionAdapter() {
         public void widgetSelected(SelectionEvent e) {
            stackLayout.topControl = widgets.get(eclipseApplicationButton).getControl();
            stackedComposite.layout();

         }
      });

      standAloneButton.addSelectionListener(new SelectionAdapter() {
         public void widgetSelected(SelectionEvent e) {
            stackLayout.topControl = widgets.get(standAloneButton).getControl();
            stackedComposite.layout();
         }
      });
   }

   @Override
   public void dispose() {
      if (widgets != null) {
         super.dispose();
         Set<Button> keys = widgets.keySet();
         for (Button key : keys) {
            ILaunchWidget widget = widgets.get(key);
            widget.dispose();
         }
      }
   }

   @Override
   public boolean isPageComplete() {
      return true;
   }

}
