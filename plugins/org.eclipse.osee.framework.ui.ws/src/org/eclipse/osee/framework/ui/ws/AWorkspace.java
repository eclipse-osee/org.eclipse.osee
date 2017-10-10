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
package org.eclipse.osee.framework.ui.ws;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.ui.IPackagesViewPart;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.views.navigator.IResourceNavigator;
import org.eclipse.ui.views.navigator.ResourceNavigator;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Donald G. Dunne
 */
@SuppressWarnings("deprecation")
public final class AWorkspace {

   private AWorkspace() {
   }

   public static String getWorkspacePath() {
      return ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString();
   }

   public static File iFileToFile(IFile iFile) {
      return new File(iFile.getLocation().toString());
   }

   public static IFile fileToIFile(File file) {
      String p = file.getAbsolutePath();
      IPath path = new Path(p);
      IFile iFile = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
      if (iFile.exists()) {
         return iFile;
      }
      p = p.replace('\\', '/');
      // System.err.println("p *" + p + "*");
      // Run through projects to see if any contain this file
      IProject projs[] = getProjects();
      for (int i = 0; i < projs.length; i++) {
         IProject proj = projs[i];
         String projLoc = proj.getLocation().toString();
         // System.err.println("proj *" + projLoc + "*");
         if (p.equals(projLoc)) {
            return null;
         } else if (p.startsWith(projLoc)) {
            // System.out.println("found it");
            p = p.replaceFirst(projLoc, "");
            p = "/" + proj.getName() + p;
            // System.err.println("new pLoc*" + p + "*");
            path = new Path(p);
            iFile = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
            if (iFile.exists()) {
               return iFile;
            } else {
               // System.err.println("iFile DOESN'T exist *" +
               // iFile.getRawLocation()+ "*");
            }
         }
         // System.out.println("proj");
      }
      return null;
   }

   public static IFile getIFile(String filename) {
      return fileToIFile(new File(filename));
   }

   public static IProject[] getProjects() {
      IWorkspace workspace = ResourcesPlugin.getWorkspace();
      return workspace.getRoot().getProjects();
   }

   public static boolean showInResourceNavigator(IFile file) {
      if (file == null) {
         return false;
      }
      IWorkbenchPage page = AWorkbench.getActivePage();
      try {
         IViewPart viewPart =
            page.showView("org.eclipse.ui.views.ResourceNavigator", null, IWorkbenchPage.VIEW_ACTIVATE);

         if (viewPart != null && viewPart instanceof ResourceNavigator) {
            ResourceNavigator resourceNavigator = (ResourceNavigator) viewPart;
            StructuredSelection ss = new StructuredSelection(file);
            resourceNavigator.selectReveal(ss);
            return true;
         }
      } catch (PartInitException ex) {
         ex.printStackTrace();
      }

      return false;
   }

   public static boolean openEditor(String filename) {
      IFile iFile = AWorkspace.getIFile(filename);
      return openEditor(iFile);
   }

   public static boolean openEditor(IFile iFile) {
      IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
      try {
         IDE.openEditor(page, iFile, true);
      } catch (PartInitException e) {
         e.printStackTrace();
         return false;
      }
      return true;
   }

   public static boolean showInPackageExplorer(IFile file) {
      if (file == null) {
         return false;
      }
      IViewPart p = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(
         "org.eclipse.jdt.ui.PackageExplorer");
      if (p != null && p instanceof IPackagesViewPart) {
         StructuredSelection ss = new StructuredSelection(file);
         IPackagesViewPart rn = (IPackagesViewPart) p;
         rn.selectAndReveal(ss);
      }
      return true;
   }

   public static void refreshResource(IResource resource) {
      IResource parentResource = resource.getParent();
      try {
         parentResource.refreshLocal(IResource.DEPTH_INFINITE, null);
      } catch (org.eclipse.core.runtime.CoreException ex) {
         MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
            "Refresh Navigator", "Can't refresh \"" + resource.getName() + "\"\n\nYou must refresh Manually");
         return;
      }

   }

   public static StructuredSelection getSelection() {
      IViewReference[] parts = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getViewReferences();
      for (int i = 0; i < parts.length; i++) {
         StructuredSelection sel = getSelection(parts[i].getPart(false));
         if (sel != null) {
            return sel;
         }
      }
      return null;
   }

   public static StructuredSelection getSelection(IWorkbenchPart targetPart) {
      if (targetPart instanceof IResourceNavigator) {
         IResourceNavigator navigator = (IResourceNavigator) targetPart;
         return (StructuredSelection) navigator.getViewer().getSelection();
      } else if (targetPart instanceof IPackagesViewPart) {
         IPackagesViewPart navigator = (IPackagesViewPart) targetPart;
         return (StructuredSelection) navigator.getTreeViewer().getSelection();
      } else if (targetPart instanceof CommonNavigator) {
         CommonNavigator navigator = (CommonNavigator) targetPart;
         return (StructuredSelection) navigator.getCommonViewer().getSelection();
      }
      return null;
   }

   public static IResource findWorkspaceFile(String fileName) {
      IContainer ws = ResourcesPlugin.getWorkspace().getRoot();
      List<IResource> resources = new ArrayList<>();
      recursiveFileFind(fileName, ws, resources);
      return !resources.isEmpty() ? resources.iterator().next() : null;
   }

   public static String getOseeInfResource(String path, Class<?> clazz) throws Exception {
      Bundle bundle = FrameworkUtil.getBundle(clazz);
      URL url = bundle.getEntry("OSEE-INF/" + path);
      return Lib.inputStreamToString(url.openStream());
   }

   public static void recursiveFileFind(String fileName, IResource resource, List<IResource> matches) {
      if (resource.getName().equalsIgnoreCase(fileName)) {
         matches.add(resource);
      }
      if (resource instanceof IContainer) {
         try {
            for (IResource res : ((IContainer) resource).members()) {
               recursiveFileFind(fileName, res, matches);
            }
         } catch (CoreException ex) {
            // do nothing
         }
      }
   }

   public static List<IResource> findWorkspaceFileMatch(String regex) {
      IContainer ws = ResourcesPlugin.getWorkspace().getRoot();
      List<IResource> resources = new ArrayList<>();
      recursiveFileFindMatch(regex, ws, resources);
      return resources;
   }

   private static void recursiveFileFindMatch(String regex, IResource resource, List<IResource> matches) {
      if (IResource.FILE == resource.getType() && resource.getName().length() > 0) {
         if (resource.getName().matches(regex)) {
            matches.add(resource);
         }
      }
      if (resource instanceof IContainer) {
         try {
            for (IResource res : ((IContainer) resource).members()) {
               recursiveFileFindMatch(regex, res, matches);
            }
         } catch (CoreException ex) {
            // do nothing
         }
      }
   }

}