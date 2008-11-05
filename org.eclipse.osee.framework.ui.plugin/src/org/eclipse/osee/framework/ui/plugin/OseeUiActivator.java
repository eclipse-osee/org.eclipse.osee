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
package org.eclipse.osee.framework.ui.plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.CharBuffer;
import java.util.List;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.ActivatorHelper;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * @author Ryan D. Brooks
 */
public abstract class OseeUiActivator extends AbstractUIPlugin {
   private static final String imagePath = "images/";
   private ImageRegistry imageRegistry;
   private OseeUiActivator parentPlugin;
   private ActivatorHelper helper;

   /**
    * The constructor.
    */
   protected OseeUiActivator() {
      super();
   }

   public Plugin getOseePlugin(String pluginId) {
      return helper.getOseePlugin(pluginId);
   }

   /**
    * returns a File to from the default persistent storage area provided for the bundle by the Framework (.ie.)
    * myworkspace/.metadata/.plugins/org.eclipse.pde.core/myPlugin/...
    */
   public File getPluginStoreFile(String path) {
      return helper.getPluginStoreFile(path);
   }

   /**
    * finds a resource in the plugin bundle and writes it out to the default persistent storage area as a regualar file
    * 
    * @param path
    * @return Return plugin file reference
    * @throws IOException
    */
   public File getPluginFile(String path) throws IOException {
      return helper.getPluginFile(path);
   }

   public InputStream getInputStream(String resource) throws IOException {
      return helper.getInputStream(resource);
   }

   public List<URL> getInputStreams(String directory, String pattern, boolean recurse) throws IOException {
      return helper.getInputStreams(directory, pattern, recurse);
   }

   /**
    * This method is called upon plug-in activation
    */
   @Override
   public void start(BundleContext context) throws Exception {
      super.start(context);

      parentPlugin = OseePluginUiActivator.getInstance();
      /*
       * parentPlugin will be the CorePlugin except in the case of CorePlugin itself when
       * parentPlugin will be null
       */
      if (parentPlugin == this) parentPlugin = null;

      helper = new ActivatorHelper(context, this);

   }

   /**
    * This method is called when the plug-in is stopped
    */
   @Override
   public void stop(BundleContext context) throws Exception {
      super.stop(context);
      if (imageRegistry != null) {
         imageRegistry.dispose();
      }
   }

   private Image getImageFromRegistry(String imageKey) {
      /*
       * initialize here rather than the constructor since some O.S.E.E. plug-ins don't use the image
       * registry and because some plug-ins seem to load before it is safe to construct an image
       * registry without getting a NullPointerException
       */
      if (imageRegistry == null) {
         imageRegistry = new ImageRegistry();
      }
      return imageRegistry.get(imageKey);
   }

   public void addImageToRegistry(String imageKey, ImageDescriptor descriptor) {
      Image previousImage = getImageFromRegistry(imageKey);
      if (previousImage == null) {
         imageRegistry.put(imageKey, descriptor);
      } else {
         throw new IllegalArgumentException("The image registry already contains an image mapped to " + imageKey);
      }
   }

   public void addImageToRegistry(String imageKey, Image image) {
      Image previousImage = getImageFromRegistry(imageKey);
      if (previousImage == null) {
         imageRegistry.put(imageKey, image);
      } else {
         throw new IllegalArgumentException("The image registry already contains an image mapped to " + imageKey);
      }
   }

   public Image getImageForProgram(String extenstion) {
      Image image = getImageFromRegistry(extenstion);

      if (image == null && extenstion != null) {
         Program program = Program.findProgram(extenstion);
         if (program == null || program.getImageData() == null) {
            // provide no image (i.e. leave null)
         } else {
            ImageDescriptor imageDescriptor = ImageDescriptor.createFromImageData(program.getImageData());
            imageRegistry.put(extenstion, imageDescriptor);
            image = imageRegistry.get(extenstion);
         }
      }
      return image;
   }

   /**
    * Returns the Image for the icon with the given path under images/
    * 
    * @return the Image object
    */
   public Image getImage(String imageName) throws IllegalArgumentException {
      Image image = getImageFromRegistry(imageName);
      if (image == null) { // if image is not already cached
         ImageDescriptor descriptor = getImageDescriptor(imageName);

         // if image not found in this plug-in, then look in parent plug-in (if parent exists)
         if (descriptor == null && parentPlugin != null) {
            Image ret = parentPlugin.getImage(imageName);
            if (ret != null) {
               return ret;
            }
         }
         if (descriptor == null) {
            throw new IllegalArgumentException(String.format("The image %s does not exist", imageName));
         }

         image = descriptor.createImage(false);
         if (image != null) { // cache image only if successfully returned
            imageRegistry.put(imageName, image);
         }
      }
      return image;
   }

   /**
    * Returns the ImageDiscriptor from images/ with the given icon name
    * 
    * @return the Image object
    */
   public ImageDescriptor getImageDescriptor(String name) {
      return imageDescriptorFromPlugin(getBundle().getSymbolicName(), imagePath + name);
   }

   public InputStream getInputStreamForImage(String imageName) throws IOException {
      return getInputStream(imagePath + imageName);
   }

   public Object getBundleHeaderValue(String name) {
      return getBundle().getHeaders().get(name);
   }

   public void log(String message) {
      getLog().log(new Status(0, toString(), 0, message, null));
   }

   public void log(String message, Exception ex) {
      getLog().log(new Status(0, toString(), 0, message, ex));
   }

   public CharBuffer getCharBuffer(String resource) {
      try {
         return Lib.inputStreamToCharBuffer(getInputStream(resource));
      } catch (IOException ex) {
         ex.printStackTrace();
      }
      return null;
   }

   public void setHelp(Control control, String name) {
      PlatformUI.getWorkbench().getHelpSystem().setHelp(control, getPluginName() + "." + name);
   }

   public void setHelp(IAction action, String name) {
      PlatformUI.getWorkbench().getHelpSystem().setHelp(action, getPluginName() + "." + name);
   }

   public void setHelp(Menu menu, String name) {
      PlatformUI.getWorkbench().getHelpSystem().setHelp(menu, getPluginName() + "." + name);
   }

   public void setHelp(MenuItem menuItem, String name) {
      PlatformUI.getWorkbench().getHelpSystem().setHelp(menuItem, getPluginName() + "." + name);
   }

   public void displayHelp(String name) {
      PlatformUI.getWorkbench().getHelpSystem().displayHelp(getPluginName() + "." + name);
   }

   protected String getPluginName() {
      throw new UnsupportedOperationException();
   }

   public static File getBasePluginInstallDirectory() {
      return new File(Platform.getInstallLocation().getURL().getFile() + File.separator + "plugins");
   }

   /**
    * Returns the workspace instance.
    * 
    * @return the workspace instance
    */
   public static IWorkspace getWorkspace() {
      return ResourcesPlugin.getWorkspace();
   }

   public static IPath getWorkspaceFile(IPath path) {
      return getWorkspaceRoot().getLocation().append(path);
   }

   public static IPath getWorkspaceFile(String path) {
      return getWorkspaceRoot().getLocation().append(path);
   }

   /**
    * Returns Returns the root resource of this workspace
    * 
    * @return the workspace root
    */
   public static IWorkspaceRoot getWorkspaceRoot() {
      return ResourcesPlugin.getWorkspace().getRoot();
   }

   public static String getStackMessages(Exception ex) {
      Throwable exloop = ex;
      String exceptionString = "";
      while (exloop != null) {
         exceptionString += exloop.getClass().getName() + ":\n\t" + exloop.getMessage() + "\n";
         exloop = exloop.getCause();
      }
      return exceptionString;
   }

   /**
    * Checks that OSEE is connected to all necessary application services
    * 
    * @return Result.isFalse if not connected with getText() of problem
    */
   public static Result areOSEEServicesAvailable() {
      Result toReturn = Result.TrueResult;
      if (!OseeLog.isStatusOk()) {
         toReturn = new Result(OseeLog.getStatusReport());
      }
      return toReturn;
   }
}