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
package org.eclipse.osee.framework.ui.service.control.dialogs;

import net.jini.core.lookup.ServiceRegistrar;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.jdk.core.type.InputManager;
import org.eclipse.osee.framework.jdk.core.type.TreeParent;
import org.eclipse.osee.framework.ui.service.control.actions.KillServiceAction;
import org.eclipse.osee.framework.ui.service.control.actions.NodeSelected;
import org.eclipse.osee.framework.ui.service.control.actions.ServiceUpdates;
import org.eclipse.osee.framework.ui.service.control.managers.ServiceTreeBuilder;
import org.eclipse.osee.framework.ui.service.control.renderer.ReggieItemHandler;
import org.eclipse.osee.framework.ui.service.control.widgets.IServiceManager;
import org.eclipse.osee.framework.ui.service.control.widgets.LookupViewer;
import org.eclipse.osee.framework.ui.service.control.widgets.ServicesViewer;
import org.eclipse.osee.framework.ui.swt.FormattedText;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Roberto E. Escobar
 */
public class InspectReggieDialog extends MessageDialog implements IServiceManager<TreeParent> {

   private Button okButton;
   private Button cancelButton;
   private boolean selectionOk;

   private ServiceRegistrar reggie;
   private ServicesViewer servicesViewer;

   private FormattedText quickViewer;
   private ServiceTreeBuilder serviceTreeBuilder;
   private ReggieItemHandler reggieParser;

   public InspectReggieDialog(Shell parentShell, ServiceRegistrar reggie, ReggieItemHandler reggieParser, String dialogTitle, Image dialogTitleImage, String dialogMessage, int dialogImageType, String[] dialogButtonLabels, int defaultIndex) {
      super(parentShell, dialogTitle, dialogTitleImage, dialogMessage, dialogImageType, dialogButtonLabels,
            defaultIndex);
      this.reggie = reggie;
      this.reggieParser = reggieParser;
      this.serviceTreeBuilder = new ServiceTreeBuilder();
      this.selectionOk = false;
   }

   @Override
   protected Control createCustomArea(Composite parent) {
      super.createCustomArea(parent);

      SashForm composite = new SashForm(parent, SWT.NONE);
      composite.setLayout(new GridLayout());
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      composite.setOrientation(SWT.VERTICAL);
      composite.SASH_WIDTH = 3;

      reggieParser.renderInComposite(new FormattedText(composite, SWT.NONE));

      SashForm sashForm = new SashForm(composite, SWT.NONE);
      sashForm.setLayout(new GridLayout());
      sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      sashForm.setOrientation(SWT.HORIZONTAL);
      sashForm.SASH_WIDTH = 3;

      servicesViewer = new ServicesViewer(sashForm, SWT.NONE);

      quickViewer = new FormattedText(sashForm, SWT.BORDER);
      quickViewer.getStyledText().setToolTipText("Displays Service Information");
      quickViewer.setTextAreaBackground(SWT.COLOR_WHITE);

      composite.setWeights(new int[] {3, 7});
      sashForm.setWeights(new int[] {4, 6});

      attachListeners();
      initializeBackend();
      return parent;
   }

   private void attachListeners() {
      new ServiceUpdates(this);
      new NodeSelected(this);
      createPopup();
   }

   private void createPopup() {
      MenuManager menuManager = new MenuManager("#PopupMenu");
      menuManager.setRemoveAllWhenShown(true);
      menuManager.addMenuListener(new IMenuListener() {
         public void menuAboutToShow(IMenuManager manager) {
            manager.add(new KillServiceAction(InspectReggieDialog.this));
         }
      });

      Control control = servicesViewer.getViewer().getControl();
      Menu menu = menuManager.createContextMenu(control);
      control.setMenu(menu);
   }

   private void initializeBackend() {
      servicesViewer.setInput(serviceTreeBuilder.getInputManager().getInputList());
      populateServices();
   }

   private void populateServices() {
      Job job = new PopulateInspectReggieDialog("Searching for services on ", serviceTreeBuilder, reggie);
      PopulateInspectReggieDialog.scheduleJob(job);
   }

   @Override
   protected Control createButtonBar(Composite parent) {
      Control c = super.createButtonBar(parent);
      okButton = getButton(0);
      cancelButton = getButton(1);

      okButton.addSelectionListener(new SelectionAdapter() {

         public void widgetSelected(SelectionEvent e) {
            selectionOk = true;
         }
      });

      cancelButton.addSelectionListener(new SelectionAdapter() {

         public void widgetSelected(SelectionEvent e) {
            selectionOk = false;
         }
      });
      return c;
   }

   public boolean isValid() {
      return selectionOk;
   }

   public FormattedText getQuickViewer() {
      return quickViewer;
   }

   public ServicesViewer getServicesViewer() {
      return servicesViewer;
   }

   public InputManager<TreeParent> getInputManager() {
      return serviceTreeBuilder.getInputManager();
   }

   public LookupViewer getLookupViewer() {
      return null;
   }

}
