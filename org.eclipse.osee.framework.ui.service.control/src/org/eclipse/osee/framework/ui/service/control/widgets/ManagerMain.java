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
package org.eclipse.osee.framework.ui.service.control.widgets;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.jdk.core.type.InputManager;
import org.eclipse.osee.framework.jdk.core.type.TreeParent;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.plugin.core.util.ExportClassLoader;
import org.eclipse.osee.framework.ui.service.control.actions.InspectLookUpServerAction;
import org.eclipse.osee.framework.ui.service.control.actions.LookupUpdates;
import org.eclipse.osee.framework.ui.service.control.actions.NodeDoubleClicked;
import org.eclipse.osee.framework.ui.service.control.actions.NodeSelected;
import org.eclipse.osee.framework.ui.service.control.actions.OpenLaunchWizard;
import org.eclipse.osee.framework.ui.service.control.actions.ServiceUpdates;
import org.eclipse.osee.framework.ui.service.control.actions.UpdateLookupViewerToolTip;
import org.eclipse.osee.framework.ui.service.control.actions.UpdateToolTip;
import org.eclipse.osee.framework.ui.service.control.data.ServiceNode;
import org.eclipse.osee.framework.ui.service.control.managers.ConnectionManager;
import org.eclipse.osee.framework.ui.service.control.managers.ContributionManager;
import org.eclipse.osee.framework.ui.service.control.managers.ServicesManager;
import org.eclipse.osee.framework.ui.service.control.managers.interfaces.IConnectionListener;
import org.eclipse.osee.framework.ui.service.control.renderer.IRenderer;
import org.eclipse.osee.framework.ui.service.control.renderer.IServiceRenderer;
import org.eclipse.osee.framework.ui.swt.FormattedText;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

/**
 * @author Roberto E. Escobar
 */
public class ManagerMain extends Composite implements IConnectionListener, IServiceManager<TreeParent> {

   private ServicesViewer servicesViewer;
   private LookupViewer lookupViewer;
   private FormattedText textArea;
   private StackedViewer stackedViewer;
   private ServicesManager servicesManager;
   private LookupUpdates lookupUpdater;
   private SashForm serviceAreaSash;
   private SashForm mainSashForm;
   private SashForm serviceAndDetailsSash;
   private ConnectionManager connectionManager;
   private ContributionManager contributionManager;
   private Logger logger = ConfigUtil.getConfigFactory().getLogger(ManagerMain.class);

   public ManagerMain(Composite parent, int style) {
      super(parent, style);
      create();
      initializeBackend();
      registerActions();
   }

   private void create() {
      this.setLayout(new GridLayout());
      this.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      mainSashForm = new SashForm(this, SWT.NONE);
      mainSashForm.setLayout(new GridLayout());
      mainSashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      mainSashForm.setOrientation(SWT.HORIZONTAL);
      mainSashForm.SASH_WIDTH = 3;

      createServiceAndDetailAreaSash(mainSashForm);
      createConnectionArea(mainSashForm);

      mainSashForm.setWeights(new int[] {4, 5});
   }

   private void createServiceAndDetailAreaSash(Composite parent) {
      serviceAndDetailsSash = new SashForm(parent, SWT.NONE);
      serviceAndDetailsSash.setLayout(new GridLayout());
      serviceAndDetailsSash.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      serviceAndDetailsSash.setOrientation(SWT.VERTICAL);
      serviceAndDetailsSash.SASH_WIDTH = 1;

      createServiceAreaSash(serviceAndDetailsSash);

      textArea = new FormattedText(serviceAndDetailsSash, SWT.BORDER);
      textArea.getStyledText().setToolTipText("Displays Service Information");
      textArea.setTextAreaBackground(SWT.COLOR_WHITE);

      serviceAndDetailsSash.setWeights(new int[] {4, 5});
   }

   private void createServiceAreaSash(Composite parent) {
      serviceAreaSash = new SashForm(parent, SWT.NONE);
      serviceAreaSash.setLayout(new GridLayout());
      serviceAreaSash.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      serviceAreaSash.setOrientation(SWT.HORIZONTAL);
      serviceAreaSash.SASH_WIDTH = 1;

      createLookupViewerArea(serviceAreaSash);
      createServicesArea(serviceAreaSash);

      serviceAreaSash.setWeights(new int[] {4, 7});
   }

   private void createServicesArea(Composite parent) {
      Group composite = new Group(parent, SWT.NONE);
      composite.setLayout(new GridLayout());
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      composite.setText("Services");
      servicesViewer = new ServicesViewer(composite, SWT.NONE);
   }

   private void createLookupViewerArea(Composite parent) {
      Group composite = new Group(parent, SWT.NONE);
      composite.setLayout(new GridLayout());
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      composite.setText("Available Lookup Servers");
      lookupViewer = new LookupViewer(composite, SWT.NONE);
   }

   private void createConnectionArea(Composite parent) {
      stackedViewer = new StackedViewer(parent, SWT.BORDER);
   }

   public ServicesViewer getServicesViewer() {
      return servicesViewer;
   }

   public FormattedText getQuickViewer() {
      return textArea;
   }

   public ServicesManager getServicesManager() {
      return servicesManager;
   }

   public InputManager<TreeParent> getInputManager() {
      return servicesManager.getInputManager();
   }

   public ConnectionManager getConnectionManager() {
      return connectionManager;
   }

   public LookupViewer getLookupViewer() {
      return lookupViewer;
   }

   public LookupUpdates getLookupUpdater() {
      return lookupUpdater;
   }

   private void registerActions() {
      new OpenLaunchWizard(this);
      new ServiceUpdates(this);
      new NodeSelected(this);
      new NodeDoubleClicked(this);
      new UpdateToolTip(this);
      new InspectLookUpServerAction(this);
      new UpdateLookupViewerToolTip(this);
      lookupUpdater = new LookupUpdates(this);
   }

   public void dispose() {
      lookupUpdater.dispose();
      servicesViewer.dispose();
      stackedViewer.dispose();
      lookupViewer.dispose();
      textArea.dispose();
      connectionManager.removeConnectionListener(this);
      servicesManager.dispose();
      super.dispose();
   }

   private void initializeBackend() {
      connectionManager = ConnectionManager.getInstance();
      contributionManager = ContributionManager.getInstance();

      registerServiceRenderers();
      registerServiceIcons();

      servicesManager = ServicesManager.getInstance();
      servicesViewer.setInput(servicesManager.getInputManager().getInputList());

      connectionManager.addConnectionListener(this);
   }

   private void registerServiceRenderers() {
      Thread.currentThread().setContextClassLoader(ExportClassLoader.getInstance());

      Map<String, String> interfaceToRenderer = contributionManager.getInterfaceToRendererMap();
      for (String interfaceName : interfaceToRenderer.keySet()) {
         String serviceRenderer = interfaceToRenderer.get(interfaceName);

         try {
            Class<?> interfaceClass = contributionManager.loadClass(interfaceName);
            Class<?> rendererClass = contributionManager.loadClass(serviceRenderer);
            try {
               Object renderer = rendererClass.newInstance();
               connectionManager.registerForConnection(interfaceClass, (IServiceRenderer) renderer);
               stackedViewer.addControl(interfaceClass.getCanonicalName(), (IRenderer) renderer);
            } catch (InstantiationException ex) {
               logger.log(Level.WARNING, "registerServiceRenderers: Instantiation Error.\n", ex);
            } catch (IllegalAccessException ex) {
               logger.log(Level.WARNING, "registerServiceRenderers: IllegalAccess Error.\n", ex);
            }
         } catch (ClassNotFoundException ex) {
            logger.log(Level.WARNING, "registerServiceRenderers: ClassNotFound Error.\n", ex);
         }
      }
   }

   private void registerServiceIcons() {
      Map<String, String> interfaceToIcon = contributionManager.getInterfaceToIconMap();
      for (String interfaceName : interfaceToIcon.keySet()) {

         String iconPath = interfaceToIcon.get(interfaceName);
         ImageDescriptor imageDescriptor = contributionManager.getImageDescriptor(iconPath);
         if (imageDescriptor != null) {
            try {
               Class<?> interfaceClass = contributionManager.loadClass(interfaceName);
               servicesViewer.registerImage(interfaceClass, imageDescriptor);
            } catch (ClassNotFoundException ex) {
               logger.log(Level.WARNING, "Error in registerServiceIcons.\n", ex);
            }
         }
      }
   }

   public void onConnectionChanged(ServiceNode serviceNode, boolean connected) {
      if (connected) {
         serviceAreaSash.setOrientation(SWT.VERTICAL);
         Class<?> key = connectionManager.getConnectionType();
         IServiceRenderer renderer = connectionManager.getRenderer();
         renderer.setService(serviceNode.getServiceItem());
         stackedViewer.displayArea(key.getCanonicalName());
         stackedViewer.setVisible(true);
         serviceAndDetailsSash.setWeights(new int[] {7, 3});
         renderer.refresh();
      } else {
         serviceAreaSash.setOrientation(SWT.HORIZONTAL);
         stackedViewer.displayArea(StackedViewer.DEFAULT_CONTROL);
         stackedViewer.setVisible(false);
         serviceAndDetailsSash.setWeights(new int[] {4, 5});
      }
      this.mainSashForm.layout();
      this.serviceAndDetailsSash.layout();
      getServicesViewer().refresh();
   }
}
