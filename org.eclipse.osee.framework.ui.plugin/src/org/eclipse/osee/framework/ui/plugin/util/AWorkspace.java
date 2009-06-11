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
package org.eclipse.osee.framework.ui.plugin.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Pattern;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.ui.IPackagesViewPart;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.utility.OseeData;
import org.eclipse.osee.framework.ui.plugin.OseePluginUiActivator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.views.navigator.IResourceNavigator;
import org.eclipse.ui.views.navigator.ResourceNavigator;

/**
 * @author Donald G. Dunne
 */
public class AWorkspace {
   private static HashMap<String, File> fileFindMap = new HashMap<String, File>();

   private static boolean initializedWorkspaceSearch = false;
   private static HashMap<String, List<File>> fileSearch = new HashMap<String, List<File>>();
   private static IWindowListener windowListener;
   private static IResourceChangeListener changeListener;

   public static void init(final Object objectToNotify) {
      Job initJob = new WorkspaceSearchInit(objectToNotify);
      initJob.setPriority(Job.LONG);
      initJob.schedule();
   }

   public static void reinit() {
      Job initJob = new Job("Initializing Workspace Search") {
         protected IStatus run(IProgressMonitor monitor) {
            initializedWorkspaceSearch = false;
            File savefile = OseeData.getFile("serializedFileFinder");
            savefile.delete();

            File mapfile = OseeData.getFile("fileFindMap");
            mapfile.delete();

            fileSearch.clear();
            fileFindMap.clear();
            initWorkspaceSearch();
            return Status.OK_STATUS;
         }
      };
      initJob.setPriority(Job.LONG);
      initJob.schedule();
   }

   @SuppressWarnings("unchecked")
   public static void initWorkspaceSearch() {
      if (!initializedWorkspaceSearch) {
         initializedWorkspaceSearch = true;
         File savefile = OseeData.getFile("serializedFileFinder");
         File mapfile = OseeData.getFile("fileFindMap");
         if (savefile.exists() && mapfile.exists()) {
            ObjectInputStream ois;
            try {
               OseeLog.log(OseePluginUiActivator.class, Level.INFO, "starting init");
               long time = System.currentTimeMillis();
               ois = new ObjectInputStream(new FileInputStream(savefile));
               fileSearch = (HashMap<String, List<File>>) ois.readObject();
               OseeLog.log(OseePluginUiActivator.class, Level.INFO,
                     "ending init " + (System.currentTimeMillis() - time) / 1000 + " secs");

               OseeLog.log(OseePluginUiActivator.class, Level.INFO, "starting init");
               time = System.currentTimeMillis();
               ois = new ObjectInputStream(new FileInputStream(mapfile));
               fileFindMap = (HashMap<String, File>) ois.readObject();
               OseeLog.log(OseePluginUiActivator.class, Level.INFO,
                     "ending init " + (System.currentTimeMillis() - time) / 1000 + " secs");

            } catch (FileNotFoundException ex) {
               ex.printStackTrace();
            } catch (IOException ex) {
               ex.printStackTrace();
            } catch (ClassNotFoundException ex) {
               ex.printStackTrace();
            }
         } else {
            OseeLog.log(OseePluginUiActivator.class, Level.INFO, "starting init");
            long time = System.currentTimeMillis();
            List files = Lib.recursivelyListFiles(new File(getWorkspacePath()), Pattern.compile(".*"));
            for (Object obj : files) {
               File file = (File) obj;
               addFile(file);
            }
            OseeLog.log(OseePluginUiActivator.class, Level.INFO,
                  "ending init " + (System.currentTimeMillis() - time) / 1000 + " secs");
         }

         if (changeListener == null) {
            changeListener = new IResourceChangeListener() {

               public void resourceChanged(IResourceChangeEvent event) {
                  if (IResourceChangeEvent.POST_CHANGE == event.getType()) {
                     recDeltaInfo(event.getDelta());
                  }
               }

               private void recDeltaInfo(IResourceDelta delta) {
                  File file = delta.getResource().getLocation().toFile();
                  if ((delta.getKind() & IResourceDelta.ADDED) > 0) {
                     addFile(file);
                  } else if ((delta.getKind() & IResourceDelta.REMOVED) > 0) {
                     removeFile(file);
                  }
                  IResourceDelta[] deltas =
                        delta.getAffectedChildren(IResourceDelta.ADDED | IResourceDelta.REMOVED | IResourceDelta.CHANGED);
                  for (IResourceDelta d : deltas) {
                     recDeltaInfo(d);
                  }
               }
            };
            OseeData.getProject().getWorkspace().addResourceChangeListener(changeListener);
         }
         if (windowListener == null) {
            windowListener = new IWindowListener() {

               public void windowActivated(IWorkbenchWindow window) {
               }

               public void windowClosed(IWorkbenchWindow window) {
                  OseeLog.log(OseePluginUiActivator.class, Level.INFO, "closed window");
                  OseeLog.log(OseePluginUiActivator.class, Level.INFO, "saving...");

                  try {
                     ObjectOutputStream oos =
                           new ObjectOutputStream(new FileOutputStream(OseeData.getFile("serializedFileFinder")));
                     oos.writeObject(fileSearch);

                     oos = new ObjectOutputStream(new FileOutputStream(OseeData.getFile("fileFindMap")));
                     oos.writeObject(fileFindMap);
                  } catch (FileNotFoundException ex) {
                     ex.printStackTrace();
                  } catch (IOException ex) {
                     ex.printStackTrace();
                  }

               }

               public void windowDeactivated(IWorkbenchWindow window) {
               }

               public void windowOpened(IWorkbenchWindow window) {
               }

            };
            PlatformUI.getWorkbench().addWindowListener(windowListener);
         }

      }
   }

   /**
    * @param file
    */
   protected static void removeFile(File file) {
      List<File> files = fileSearch.get(file.getName());
      Iterator<File> it = files.iterator();
      while (it.hasNext()) {
         File f = it.next();
         if (f.equals(file)) {
            it.remove();
         }
      }
      if (files.size() == 0) {
         fileSearch.remove(file.getName());
      }
   }

   /**
    * @param file
    */
   protected static void addFile(File file) {
      if (file.isFile()) {
         List<File> fileSearchResults = fileSearch.get(file.getName());
         if (fileSearchResults == null) {
            fileSearchResults = new ArrayList<File>();
            fileSearch.put(file.getName(), fileSearchResults);
         }
         if (!fileSearchResults.contains(file)) {
            fileSearchResults.add(file);
         }
      }
   }

   public static File getProjectFile(File fileFile) {
      // If file is an artifact, go up one level to create tree
      File parentFile = fileFile.getParentFile();
      while (!parentFile.getParentFile().getName().equals("workspace") && !parentFile.getParentFile().getName().equals(
            "runtime-workbench-workspace")) {
         parentFile = parentFile.getParentFile();
      }
      return parentFile;
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
      if (iFile.exists()) return iFile;
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

   public static IProject fileToIProject(File file) {
      String p = file.getAbsolutePath();
      p = p.replace('\\', '/');
      System.err.println("p *" + p + "*");
      // Run through projects to see if any contain this file
      IProject projs[] = getProjects();
      for (int i = 0; i < projs.length; i++) {
         IProject proj = projs[i];
         String projLoc = proj.getLocation().toString();
         System.err.println("proj *" + projLoc + "*");
         if (p.equals(projLoc)) {
            return proj;
         }
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
      if (file == null) return false;
      IViewPart p =
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(
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
         if (sel != null) return sel;
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
      }
      return null;
   }

   /**
    * @return IResource arraylist
    */
   public static ArrayList<IResource> getSelectedResources() {
      ArrayList<IResource> l = new ArrayList<IResource>();
      StructuredSelection sel = getSelection();
      Iterator<?> i = sel.iterator();
      while (i.hasNext()) {
         IResource resource = (IResource) i.next();
         l.add(resource);
      }
      return l;
   }

   // public static File findWorkspaceFile(String fileName, String filePathHint) throws IOException
   // {
   // File file = fileFindMap.get(fileName);
   // if(file == null || !file.exists()){
   // File workspace = new File(AWorkspace.getWorkspacePath());
   // List files = Lib.recursivelyListFiles(workspace, Pattern.compile(fileName));
   // if(files.size() == 0){
   // throw new IOException("we didn't find the file in the workspace");
   // } else if (files.size() > 1){
   //            
   // DialogSelectionHelper selection = new DialogSelectionHelper(files.toArray());
   //            
   // Display.getDefault().syncExec(selection);
   //            
   // if(selection.getSelectionIndex() == -1){
   // String message = "we found multiple matches";
   // for(int i = 0; i < files.size(); i++){
   // message += "\n" + files.get(i);
   // }
   // throw new IOException(message);
   // } else {
   // if(selection.isSaveSelection()){
   // fileFindMap.put(fileName, (File)files.get(selection.getSelectionIndex()));
   // }
   // return (File)files.get(selection.getSelectionIndex());
   // }
   // } else {
   // file = (File)files.get(0);
   // fileFindMap.put(fileName, file);
   // }
   // }
   // return file;
   // }
   /**
    * Return workspace file give workspace relative path and file. eg ".metadata/.log"
    */
   public static File getWorkspaceFile(String wsPathFileName) throws IOException {
      return new File(getWorkspacePath() + "\\" + wsPathFileName);
   }

   public static File findWorkspaceFileNew(final String fileName) throws IOException {
      File file = fileFindMap.get(fileName);
      if (file != null) {
         return file;
      }
      List<File> files = fileSearch.get(fileName);
      if (files != null) {
         if (files.size() == 1) {
            return files.get(0);
         } else if (files.size() > 0) {
            DialogSelectionHelper selection = new DialogSelectionHelper(files.toArray());

            Display.getDefault().syncExec(selection);

            if (selection.getSelectionIndex() == -1) {
               String message = "we found multiple matches";
               for (int i = 0; i < files.size(); i++) {
                  message += "\n" + files.get(i);
               }
               throw new IOException(message);
            } else {
               if (selection.isSaveSelection()) {
                  fileFindMap.put(fileName, (File) files.get(selection.getSelectionIndex()));
               }
               return (File) files.get(selection.getSelectionIndex());
            }
         }
      }
      // Display.getDefault().asyncExec(new Runnable() {
      // public void run() {
      // if
      // (MessageDialog.openQuestion(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
      // "Open Error", "Can't find \"" + fileName
      // + "\" in workspace. Would you like to re-initialize the search structure, it will be
      // performed in the background and could take several minutes?")) {
      // reinit();
      // }
      // }
      // });
      return null;
   }

   public static File findWorkspaceFile(String fileName, String filePathHint) throws IOException {
      File file = fileFindMap.get(fileName);
      if (file == null || !file.exists()) {
         List<File> files = null;
         if (filePathHint != null) {
            files = Lib.recursivelyListFiles(new File(filePathHint), Pattern.compile(fileName));
         }
         if (files == null || files.size() == 0) {
            files = Lib.recursivelyListFiles(new File(AWorkspace.getWorkspacePath()), Pattern.compile(fileName));
         }
         if (files.size() == 0) {
            throw new IOException("we didn't find the file [" + fileName + "] in the workspace");
         } else if (files.size() > 1) {

            DialogSelectionHelper selection = new DialogSelectionHelper(files.toArray());

            Display.getDefault().syncExec(selection);

            if (selection.getSelectionIndex() == -1) {
               String message = "we found multiple matches";
               for (int i = 0; i < files.size(); i++) {
                  message += "\n" + files.get(i);
               }
               throw new IOException(message);
            } else {
               if (selection.isSaveSelection()) {
                  fileFindMap.put(fileName, (File) files.get(selection.getSelectionIndex()));
               }
               return (File) files.get(selection.getSelectionIndex());
            }
         } else {
            file = (File) files.get(0);
            fileFindMap.put(fileName, file);
         }
      }
      return file;
   }

   public static IResource findWorkspaceFile(String fileName) throws IOException {
      IContainer ws = ResourcesPlugin.getWorkspace().getRoot();
      List<IResource> resources = new ArrayList<IResource>();
      recursiveFileFind(fileName, ws, resources);
      for (IResource resource : resources) {
         System.out.println("found a file " + resource.getName());
      }
      if (resources.size() > 0) {
         return resources.get(0);
      }
      return null;
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
         }
      }
   }

   public static List<IResource> findWorkspaceFileMatch(String regex) throws IOException {
      IContainer ws = ResourcesPlugin.getWorkspace().getRoot();
      List<IResource> resources = new ArrayList<IResource>();
      recursiveFileFindMatch(regex, ws, resources);
      return resources;
   }

   public static void recursiveFileFindMatch(String regex, IResource resource, List<IResource> matches) {
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
         }
      }
   }

}