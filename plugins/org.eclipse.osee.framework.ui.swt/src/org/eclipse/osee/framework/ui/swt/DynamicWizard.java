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
package org.eclipse.osee.framework.ui.swt;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.osee.framework.ui.swt.internal.FrameworkUiImage;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public abstract class DynamicWizard implements IWizard {
   /**
    * Image registry key of the default image for wizard pages (value
    * <code>"org.eclipse.jface.wizard.Wizard.pageImage"</code>).
    */
   public static final String DEFAULT_IMAGE = "org.eclipse.jface.wizard.Wizard.pageImage"; //$NON-NLS-1$
   /*
    * Register the default page image
    */

   static {
      JFaceResources.getImageRegistry().put(DEFAULT_IMAGE, ImageManager.getImage(FrameworkUiImage.PAGE));//$NON-NLS-1$
   }

   /**
    * The wizard container this wizard belongs to; <code>null</code> if none.
    */
   private IWizardContainer container = null;

   /**
    * This wizard's list of pages (element type: <code>IWizardPage</code>).
    */
   private final Map<String, IWizardPage> pages = new HashMap<>();

   /**
    * Indicates whether this wizard needs a progress monitor.
    */
   private boolean needsProgressMonitor = false;

   /**
    * Indicates whether this wizard needs previous and next buttons even if the wizard has only one page.
    */
   private boolean forcePreviousAndNextButtons = false;

   /**
    * Indicates whether this wizard supports help.
    */
   private boolean isHelpAvailable = false;

   /**
    * The default page image for pages without one of their one; <code>null</code> if none.
    */
   private Image defaultImage = null;

   /**
    * The default page image descriptor, used for creating a default page image if required; <code>null</code> if none.
    */
   private ImageDescriptor defaultImageDescriptor = JFaceResources.getImageRegistry().getDescriptor(DEFAULT_IMAGE);

   /**
    * The color of the wizard title bar; <code>null</code> if none.
    */
   private RGB titleBarColor = null;

   /**
    * The window title string for this wizard; <code>null</code> if none.
    */
   private String windowTitle = null;

   /**
    * The dialog settings for this wizard; <code>null</code> if none.
    */
   private IDialogSettings dialogSettings = null;

   private IWizardPage startingPage = null;

   /**
    * Creates a new empty wizard.
    */
   protected DynamicWizard() {
      super();
   }

   /**
    * Adds a new page to this wizard. The page is inserted at the end of the page list.
    *
    * @param page the new page
    */
   public void addPage(IWizardPage page) {
      if (!pages.containsKey(page.getName())) {
         pages.put(page.getName(), page);
         page.setWizard(this);
      }
   }

   /**
    * The <code>Wizard</code> implementation of this <code>IWizard</code> method does nothing. Subclasses should extend
    * if extra pages need to be added before the wizard opens. New pages should be added by calling <code>addPage</code>
    * .
    */
   @Override
   public void addPages() {
      // do nothing
   }

   @Override
   public boolean canFinish() {
      Set<String> keys = pages.keySet();
      for (String key : keys) {
         if (!pages.get(key).isPageComplete()) {
            return false;
         }
      }
      return true;
   }

   /**
    * The <code>Wizard</code> implementation of this <code>IWizard</code> method creates all the pages controls using
    * <code>IDialogPage.createControl</code>. Subclasses should reimplement this method if they want to delay creating
    * one or more of the pages lazily. The framework ensures that the contents of a page will be created before
    * attempting to show it.
    */
   @Override
   public void createPageControls(Composite pageContainer) {
      // the default behavior is to create all the pages controls
      Set<String> keys = pages.keySet();
      for (String key : keys) {
         IWizardPage page = pages.get(key);
         page.createControl(pageContainer);
         // page is responsible for ensuring the created control is
         // accessable
         // via getControl.
         Assert.isNotNull(page.getControl());
      }
   }

   /**
    * The <code>Wizard</code> implementation of this <code>IWizard</code> method disposes all the pages controls using
    * <code>DialogPage.dispose</code>. Subclasses should extend this method if the wizard instance maintains addition
    * SWT resource that need to be disposed.
    */
   @Override
   public void dispose() {
      // notify pages
      Set<String> keys = pages.keySet();
      for (String key : keys) {
         pages.get(key).dispose();
      }
      // dispose of image
      if (defaultImage != null) {
         JFaceResources.getResources().destroyImage(defaultImageDescriptor);
         defaultImage = null;
      }
   }

   @Override
   public IWizardContainer getContainer() {
      return container;
   }

   @Override
   public Image getDefaultPageImage() {
      if (defaultImage == null) {
         defaultImage = JFaceResources.getResources().createImageWithDefault(defaultImageDescriptor);
      }
      return defaultImage;
   }

   @Override
   public IDialogSettings getDialogSettings() {
      return dialogSettings;
   }

   @Override
   public IWizardPage getPage(String name) {
      return pages.get(name);
   }

   @Override
   public IWizardPage getNextPage(IWizardPage page) {
      return page.getNextPage();
   }

   @Override
   public int getPageCount() {
      return pages.size();
   }

   @Override
   public IWizardPage[] getPages() {
      Collection<IWizardPage> collectionOfPages = pages.values();
      return collectionOfPages.toArray(new IWizardPage[collectionOfPages.size()]);
   }

   public boolean containsPage(IWizardPage page) {
      Set<String> pageNames = pages.keySet();
      return pageNames.contains(page.getName());
   }

   @Override
   public IWizardPage getPreviousPage(IWizardPage page) {
      return page.getPreviousPage();
   }

   /**
    * Returns the wizard's shell if the wizard is visible. Otherwise <code>null</code> is returned.
    */
   public Shell getShell() {
      if (container == null) {
         return null;
      }
      return container.getShell();
   }

   @Override
   public IWizardPage getStartingPage() {
      return this.startingPage;
   }

   @Override
   public RGB getTitleBarColor() {
      return titleBarColor;
   }

   @Override
   public String getWindowTitle() {
      return windowTitle;
   }

   @Override
   public boolean isHelpAvailable() {
      return isHelpAvailable;
   }

   @Override
   public boolean needsPreviousAndNextButtons() {
      return forcePreviousAndNextButtons || pages.size() > 1;
   }

   @Override
   public boolean needsProgressMonitor() {
      return needsProgressMonitor;
   }

   /**
    * The <code>Wizard</code> implementation of this <code>IWizard</code> method does nothing and returns
    * <code>true</code>. Subclasses should reimplement this method if they need to perform any special cancel processing
    * for their wizard.
    */
   @Override
   public boolean performCancel() {
      return true;
   }

   /**
    * Subclasses must implement this <code>IWizard</code> method to perform any special finish processing for their
    * wizard.
    */
   @Override
   public abstract boolean performFinish();

   @Override
   public void setContainer(IWizardContainer wizardContainer) {
      container = wizardContainer;
   }

   /**
    * Sets the default page image descriptor for this wizard.
    * <p>
    * This image descriptor will be used to generate an image for a page with no image of its own; the image will be
    * computed once and cached.
    * </p>
    *
    * @param imageDescriptor the default page image descriptor
    */
   public void setDefaultPageImageDescriptor(ImageDescriptor imageDescriptor) {
      defaultImageDescriptor = imageDescriptor;
   }

   /**
    * Sets the dialog settings for this wizard.
    * <p>
    * The dialog settings is used to record state between wizard invocations (for example, radio button selection, last
    * import directory, etc.)
    * </p>
    *
    * @param settings the dialog settings, or <code>null</code> if none
    * @see #getDialogSettings
    */
   public void setDialogSettings(IDialogSettings settings) {
      dialogSettings = settings;
   }

   /**
    * Controls whether the wizard needs Previous and Next buttons even if it currently contains only one page.
    * <p>
    * This flag should be set on wizards where the first wizard page adds follow-on wizard pages based on user input.
    * </p>
    *
    * @param b <code>true</code> to always show Next and Previous buttons, and <code>false</code> to suppress Next and
    * Previous buttons for single page wizards
    */
   public void setForcePreviousAndNextButtons(boolean b) {
      forcePreviousAndNextButtons = b;
   }

   /**
    * Sets whether help is available for this wizard.
    * <p>
    * The result of this method is typically used by the container to show or hide the Help button.
    * </p>
    *
    * @param b <code>true</code> if help is available, and <code>false</code> if this wizard is helpless
    * @see #isHelpAvailable
    */
   public void setHelpAvailable(boolean b) {
      isHelpAvailable = b;
   }

   /**
    * Sets whether this wizard needs a progress monitor.
    *
    * @param b <code>true</code> if a progress monitor is required, and <code>false</code> if none is needed
    * @see #needsProgressMonitor()
    */
   public void setNeedsProgressMonitor(boolean b) {
      needsProgressMonitor = b;
   }

   /**
    * Sets the title bar color for this wizard.
    *
    * @param color the title bar color
    */
   public void setTitleBarColor(RGB color) {
      titleBarColor = color;
   }

   /**
    * Sets the window title for the container that hosts this page to the given string.
    *
    * @param newTitle the window title for the container
    */
   public void setWindowTitle(String newTitle) {
      windowTitle = newTitle;
      if (container != null) {
         container.updateWindowTitle();
      }
   }

   public void setStartingPage(IWizardPage startingPage) {
      this.startingPage = startingPage;
   }
}
