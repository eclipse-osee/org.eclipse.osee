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
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.core.data.OseeCodeVersion;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.PluginUtil;
import org.eclipse.osee.orcs.rest.client.OseeClient;
import org.eclipse.swt.program.Program;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

/**
 * @author Ryan D. Brooks
 */
public abstract class OseeUiActivator extends AbstractUIPlugin {
   private PluginUtil helper;
   private final String pluginId;
   private static Boolean supported;
   private static Boolean applicationServerAlive;

   protected OseeUiActivator(String pluginId) {
      super();
      this.pluginId = pluginId;
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
    * @return Return plugin file reference
    */
   public File getPluginFile(String path) throws IOException {
      return helper.getPluginFile(path);
   }

   public InputStream getInputStream(String resource) throws IOException {
      return helper.getInputStream(resource);
   }

   public List<URL> getInputStreams(String directory, String pattern, boolean recurse) {
      return helper.getInputStreams(directory, pattern, recurse);
   }

   /**
    * This method is called upon plug-in activation
    */
   @Override
   public void start(BundleContext context) throws Exception {
      super.start(context);
      helper = new PluginUtil(pluginId);
   }

   /**
    * This method is called when the plug-in is stopped
    */
   @Override
   public void stop(BundleContext context) throws Exception {
      super.stop(context);
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
      Result toReturn = Result.FalseResult;
      if (!isVersionSupported()) {
         toReturn =
            new Result(
               "This OSEE client version [%s] is not supported by the current application server(s).\n\nDatabase capability disabled.",
               OseeCodeVersion.getVersion());
      } else {
         StringBuffer message = new StringBuffer();
         message.append("\nCLIENT VERSION: ");
         message.append(OseeCodeVersion.getVersion());
         message.append("\n\n");
         message.append(OseeLog.getStatusReport());
         boolean isStatusOk = OseeLog.isStatusOk();
         toReturn = new Result(isStatusOk, message.toString());
      }
      return toReturn;
   }

   public static boolean isApplicationServerAlive() {
      if (applicationServerAlive == null) {
         OseeClient client = getOseeClient();
         if (client != null) {
            applicationServerAlive = client.isApplicationServerAlive();
         } else {
            applicationServerAlive = false;
         }
      }
      return applicationServerAlive;
   }

   public static boolean isVersionSupported() {
      if (supported == null) {
         OseeClient client = getOseeClient();
         if (client != null) {
            supported = client.isClientVersionSupportedByApplicationServer();
         } else {
            supported = false;
         }
      }
      return supported;
   }

   private static <T> T getService(Class<T> clazz) throws OseeCoreException {
      Bundle bundle = FrameworkUtil.getBundle(OseeUiActivator.class);
      Conditions.checkNotNull(bundle, "bundle");
      BundleContext context = bundle.getBundleContext();
      Conditions.checkNotNull(context, "bundleContext");
      ServiceReference<T> reference = context.getServiceReference(clazz);
      Conditions.checkNotNull(reference, "serviceReference");
      T service = context.getService(reference);
      Conditions.checkNotNull(service, "service");
      return service;
   }

   public static OseeClient getOseeClient() throws OseeCoreException {
      return getService(OseeClient.class);
   }

   public ImageDescriptor getImageDescriptorForProgram(String extenstion) {
      ImageDescriptor imageDescriptor = getImageRegistry().getDescriptor(extenstion);

      if (imageDescriptor == null && extenstion != null) {
         Program program = Program.findProgram(extenstion);
         if (program == null || program.getImageData() == null) {
            // provide no image (i.e. leave null)
         } else {
            imageDescriptor = ImageDescriptor.createFromImageData(program.getImageData());
            getImageRegistry().put(extenstion, imageDescriptor);
         }
      }
      return imageDescriptor;
   }
}