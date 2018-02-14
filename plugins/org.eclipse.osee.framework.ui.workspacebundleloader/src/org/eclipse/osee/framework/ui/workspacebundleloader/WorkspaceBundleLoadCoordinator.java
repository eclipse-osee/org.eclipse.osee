/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.workspacebundleloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.core.data.OseeData;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.workspacebundleloader.internal.Activator;
import org.eclipse.osee.framework.ui.workspacebundleloader.internal.ManagedFolderArea;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.XMLMemento;
import org.eclipse.ui.views.IViewDescriptor;
import org.eclipse.ui.views.IViewRegistry;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.wiring.FrameworkWiring;

public class WorkspaceBundleLoadCoordinator {

   private static final String TAG_VIEW = "view";
   private static final String TAG_PERSPECTIVE = "perspective";
   private static final String TAG_OTE_PRECOMPILED = "OTEPrecompiled";
   private static final String OTE_MEMENTO = "OTEMemento";

   private final ManagedFolderArea managedFolderArea;
   private final Set<String> bundlesToCheck;
   private final BundleCollection managedArea = new BundleCollection();
   private final FrameworkWiring wiring;

   public WorkspaceBundleLoadCoordinator(File temporaryBundleLocationFolder) {
      bundlesToCheck = new HashSet<>();
      this.managedFolderArea = new ManagedFolderArea(temporaryBundleLocationFolder);
      managedFolderArea.initialize();
      this.wiring = getFrameworkWiring();

      Thread th = new Thread(new Runnable() {
         @Override
         public void run() {
            int lastSize = 0;
            boolean extraWait = false;
            while (true) {
               try {
                  if (extraWait) {
                     Thread.sleep(15000);
                     extraWait = false;
                  } else {
                     Thread.sleep(5000);
                  }
               } catch (InterruptedException e) {
                  // do nothing
               }
               if (lastSize == bundlesToCheck.size()) {
                  if (lastSize != 0) {
                     if (bundlesToCheck.size() > 0) {
                        lastSize = 0;
                        Operations.executeAsJob(new RefreshWorkspaceBundles(), false);
                        try {
                           Thread.sleep(1000 * 60); //give time to load so we don't get called twice
                        } catch (InterruptedException e) {
                           // do nothing
                        }
                     }
                  }
               } else {
                  if (bundlesToCheck.size() - lastSize > 5) {
                     extraWait = true;//big import allow for extra time for file imports
                  }
                  lastSize = bundlesToCheck.size();
               }
            }
         }
      });
      th.setName("OTE BundleLoad Check");
      th.setDaemon(true);
      th.start();
   }

   private FrameworkWiring getFrameworkWiring() {
      FrameworkWiring frameworkWiring = null;
      Bundle bundle = FrameworkUtil.getBundle(getClass());
      for (Bundle findit : bundle.getBundleContext().getBundles()) {
         frameworkWiring = findit.adapt(FrameworkWiring.class);
         if (frameworkWiring != null) {
            break;
         }
      }
      return frameworkWiring;
   }

   private class RefreshWorkspaceBundles extends AbstractOperation {

      public RefreshWorkspaceBundles() {
         super("Update Precompiled", Activator.BUNDLE_ID);
      }

      @Override
      protected void doWork(IProgressMonitor monitor) throws Exception {
         updateBundles(monitor);
         installLatestBundles(monitor);
      }

   }

   public void saveAndCloseViews() {
      PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
         @Override
         public void run() {
            saveAndCloseManagedViews(determineManagedViews(), true);
         }
      });
   }

   private void closeUpdatedViews(final List<BundleInfoLite> uninstallList) {
      PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
         @Override
         public void run() {
            saveAndCloseManagedViews(determineManagedViews(uninstallList), true);
         }
      });
   }

   public synchronized void uninstallBundles() {
      if (managedArea.getInstalledBundles().size() > 0) {
         saveAndCloseViews();
         for (BundleInfoLite info : managedArea.getInstalledBundles()) {
            try {
               info.uninstall();
            } catch (BundleException e) {
               OseeLog.log(WorkspaceBundleLoadCoordinator.class, Level.WARNING, e);
            }
         }
         if (wiring != null) {
            wiring.refreshBundles(null);
         }
         IWorkbench workbench = PlatformUI.getWorkbench();
         if (workbench != null && workbench.getActiveWorkbenchWindow() != null) {
            IViewRegistry registry = workbench.getViewRegistry();
            forceViewRegistryReload(workbench, registry);
         }
         waitForViewsToBeRegistered(null);
      }
   }

   private void saveAndCloseManagedViews(Set<String> managedViewIds, boolean save) {

      IWorkbench workbench = PlatformUI.getWorkbench();
      if (managedArea.getInstalledBundles().size() > 0 && workbench != null) {
         IWorkbenchPage page = null;
         if (PlatformUI.getWorkbench().getActiveWorkbenchWindow() == null) {
            IWorkbenchWindow[] windows = workbench.getWorkbenchWindows();
            for (IWorkbenchWindow win : windows) {
               page = win.getActivePage();
               if (page != null) {
                  break;
               }
            }
         } else {
            page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
         }
         if (page == null) {
            return;
         }
         IPerspectiveDescriptor originalPerspective = page.getPerspective();
         XMLMemento memento = XMLMemento.createWriteRoot(TAG_OTE_PRECOMPILED);
         //find the view in other perspectives
         IPerspectiveDescriptor[] pd = page.getOpenPerspectives();
         for (int i = 0; i < pd.length; i++) {
            try {
               page.setPerspective(pd[i]);
            } catch (Exception ex) {
               // Ignore, this can get an NPE in Eclipse, see bug 4454
            }
            IMemento perspectiveMemento = null;
            try {
               perspectiveMemento = memento.createChild(TAG_PERSPECTIVE);
               perspectiveMemento.putString("id", pd[i].getId());
            } catch (Exception ex) {
               //Ignore, the perspective id is invalid xml
            }
            IViewReference[] activeReferences = page.getViewReferences();
            for (IViewReference viewReference : activeReferences) {
               int index = viewReference.getId().indexOf(":");
               String id = null;
               if (index > 0) {
                  id = viewReference.getId().substring(0, index);
               } else {
                  id = viewReference.getId();
               }
               if (managedViewIds.contains(id)) {
                  if (perspectiveMemento != null) {
                     try {
                        IMemento viewMemento = perspectiveMemento.createChild(TAG_VIEW);
                        viewMemento.putString("id", id);
                        String secondaryId = viewReference.getSecondaryId();
                        if (secondaryId != null) {
                           viewMemento.putString("secondId", secondaryId);
                        }
                        IWorkbenchPart part = viewReference.getPart(false);
                        if (part instanceof IViewPart) {
                           IViewPart viewPart = (IViewPart) part;
                           viewPart.saveState(viewMemento);
                        }
                     } catch (Exception ex) {
                        //Ignore, we failed during view save
                     }
                  }
                  try {
                     page.hideView(viewReference);
                  } catch (Throwable th) {
                     // do nothing
                  }
               }
            }
         }
         if (save) {
            saveMementoToFile(memento);
         }
         page.setPerspective(originalPerspective);
      }
   }

   private Set<String> determineManagedViews() {
      return determineManagedViews(null);
   }

   private Set<String> determineManagedViews(List<BundleInfoLite> uninstallList) {
      Set<String> managedViewIds = new HashSet<>();
      IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
      IExtensionPoint extensionPoint = extensionRegistry.getExtensionPoint("org.eclipse.ui.views");
      IExtension[] extensions = extensionPoint.getExtensions();
      for (IExtension ex : extensions) {
         String name = ex.getContributor().getName();
         if (managedArea.getByBundleName(name) != null) {
            IConfigurationElement[] elements = ex.getConfigurationElements();
            for (IConfigurationElement el : elements) {
               if (el.getName().equals(TAG_VIEW)) {
                  String id = el.getAttribute("id");
                  if (id != null) {
                     if (uninstallList != null) {
                        for (BundleInfoLite infoLite : uninstallList) {
                           if (name.equals(infoLite.getSymbolicName())) {
                              managedViewIds.add(id);
                              break;
                           }
                        }
                     } else {
                        managedViewIds.add(id);
                     }
                  }
               }
            }
         }
      }
      return managedViewIds;
   }

   private boolean saveMementoToFile(XMLMemento memento) {
      File stateFile = OseeData.getFile(OTE_MEMENTO);
      if (stateFile == null) {
         return false;
      }
      try {
         FileOutputStream stream = new FileOutputStream(stateFile);
         OutputStreamWriter writer = new OutputStreamWriter(stream, "utf-8"); //$NON-NLS-1$
         memento.save(writer);
         writer.close();
      } catch (IOException e) {
         stateFile.delete();
         return false;
      }
      return true;
   }

   @SuppressWarnings("resource")
   public static void copyFile(File source, File destination) throws IOException {
      final FileChannel in = new FileInputStream(source).getChannel();
      try {
         final FileChannel out;
         if (destination.isDirectory()) {
            out = new FileOutputStream(new File(destination, source.getName())).getChannel();
         } else {
            if (destination.exists()) {
               destination.delete(); // to work around some file permission problems
            }
            out = new FileOutputStream(destination).getChannel();
         }
         try {
            long position = 0;
            long size = in.size();
            while (position < size) {
               position += in.transferTo(position, size, out);
            }
         } finally {
            Lib.close(out);
         }
      } finally {
         Lib.close(in);
      }
   }

   private List<BundleInfoLite> determineDeltasBetweenBundlesToLoad() {
      List<BundleInfoLite> bundlesToOperateOn = new ArrayList<>();
      for (String urlString : bundlesToCheck) {
         try {
            URL newURL;
            try {
               newURL = new URL(urlString);
            } catch (MalformedURLException ex) {
               newURL = new File(urlString).toURI().toURL();
            }

            BundleInfoLite bundleInfo = new BundleInfoLite(newURL);
            List<BundleInfoLite> bundleList = managedArea.getByBundleName(bundleInfo.getSymbolicName());
            if (bundleList == null) {
               bundlesToOperateOn.add(bundleInfo);
            } else {
               boolean newBundle = true;
               if (bundleList.size() > 0) {
                  byte[] digest1 = bundleInfo.getMd5Digest();
                  for (BundleInfoLite bundle : bundleList) {
                     byte[] digest2 = bundle.getMd5Digest();
                     if (Arrays.equals(digest1, digest2)) {
                        newBundle = false;
                        new File(bundle.getSystemLocation().getFile()).setLastModified(System.currentTimeMillis());
                     }
                  }
               }
               if (newBundle) {
                  bundlesToOperateOn.add(bundleInfo);
               }
            }
         } catch (MalformedURLException e) {
            OseeLog.log(WorkspaceBundleLoadCoordinator.class, Level.WARNING, e);
         } catch (IOException e) {
            OseeLog.log(WorkspaceBundleLoadCoordinator.class, Level.WARNING, e);
         }
      }
      bundlesToCheck.clear();
      return bundlesToOperateOn;
   }

   public synchronized void addBundleToCheck(String urlString) {
      this.bundlesToCheck.add(urlString);
   }

   public synchronized void updateBundles(IProgressMonitor monitor) {
      List<BundleInfoLite> deltas = determineDeltasBetweenBundlesToLoad();
      monitor.worked(Operations.calculateWork(Operations.TASK_WORK_RESOLUTION, 0.15));
      List<BundleInfoLite> bundlesToAdd = managedFolderArea.copyDeltasToManagedFolder(deltas);
      monitor.worked(Operations.calculateWork(Operations.TASK_WORK_RESOLUTION, 0.30));
      for (BundleInfoLite bundle : bundlesToAdd) {
         managedArea.add(bundle);
      }
      monitor.worked(Operations.calculateWork(Operations.TASK_WORK_RESOLUTION, 0.05));
   }

   public synchronized void installLatestBundles(final IProgressMonitor subMonitor) {
      final List<BundleInfoLite> bundles = managedArea.getLatestBundles();
      Collection<Bundle> bundlesToRefresh = new ArrayList<>();
      List<BundleInfoLite> uninstallListAll = new ArrayList<>();
      for (BundleInfoLite info : bundles) {
         if (!info.isInstalled()) {
            List<BundleInfoLite> uninstallList = managedArea.getByBundleName(info.getSymbolicName());
            if (uninstallList.size() > 1) {
               for (BundleInfoLite toUninstall : uninstallList) {
                  if (toUninstall.isInstalled()) {
                     uninstallListAll.add(toUninstall);
                  }
               }
            }
         }
      }

      closeUpdatedViews(uninstallListAll);
      for (BundleInfoLite toUninstall : uninstallListAll) {
         try {
            Bundle bundle = toUninstall.uninstall();
            bundlesToRefresh.add(bundle);
         } catch (BundleException e) {
            OseeLog.log(WorkspaceBundleLoadCoordinator.class, Level.WARNING, e);
         }
      }

      if (wiring != null && bundlesToRefresh.size() > 0) {
         final Object waitForLoad = new Object();
         wiring.refreshBundles(bundlesToRefresh, new FrameworkListener() {
            @Override
            public void frameworkEvent(FrameworkEvent event) {
               if (FrameworkEvent.PACKAGES_REFRESHED == event.getType()) {
                  startBundles(bundles, subMonitor);
                  waitForViewsToBeRegistered(subMonitor);
                  PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
                     @Override
                     public void run() {
                        restoreStateFromMemento(subMonitor);
                     }
                  });
                  synchronized (waitForLoad) {
                     waitForLoad.notifyAll();
                  }
               }
            }
         });
         synchronized (waitForLoad) {
            try {
               waitForLoad.wait(20000);
            } catch (InterruptedException e) {
               // do nothing
            }
         }
      } else {
         startBundles(bundles, subMonitor);
         waitForViewsToBeRegistered(subMonitor);
         PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
            @Override
            public void run() {
               restoreStateFromMemento(subMonitor);
            }
         });
      }
   }

   private boolean waitForViewsToBeRegistered(IProgressMonitor monitor) {
      if (monitor != null) {
         monitor.setTaskName("Waiting for views to register.");
      }
      for (int i = 0; i < 10; i++) {
         if (monitor != null) {
            monitor.worked(1);
         }
         CheckViewsRegistered check = new CheckViewsRegistered();
         PlatformUI.getWorkbench().getDisplay().syncExec(check);
         if (check.isLoaded()) {
            return true;
         } else {
            try {
               Thread.sleep(5000);
            } catch (InterruptedException e) {
               e.printStackTrace();
            }
         }
      }
      return false;
   }

   private class CheckViewsRegistered implements Runnable {

      private volatile boolean isLoaded = false;

      @Override
      public void run() {
         IWorkbench workbench = PlatformUI.getWorkbench();
         if (managedArea.getInstalledBundles().size() > 0 && workbench != null && workbench.getActiveWorkbenchWindow() != null) {
            IViewRegistry registry = workbench.getViewRegistry();
            forceViewRegistryReload(workbench, registry);
            Set<String> managedViews = determineManagedViews();
            for (String viewId : managedViews) {
               try {
                  IViewDescriptor desc = registry.find(viewId);
                  if (desc == null) {
                     return;
                  }
               } catch (Exception ex) {
                  return;
               }
            }
            isLoaded = true;
         } else { //no workspace bundles to load, so don't wait
            isLoaded = true;
         }

      }

      public boolean isLoaded() {
         return isLoaded;
      }
   }

   @SuppressWarnings({"rawtypes"})
   private void forceViewRegistryReload(IWorkbench workbench, IViewRegistry registry) {
      try {
         Field field1 = registry.getClass().getDeclaredField("descriptors");
         Field field2 = registry.getClass().getDeclaredField("stickyDescriptors");
         Field field3 = registry.getClass().getDeclaredField("categories");

         field1.setAccessible(true);
         field2.setAccessible(true);
         field3.setAccessible(true);

         ((Map) field1.get(registry)).clear();
         ((List) field2.get(registry)).clear();
         ((Map) field3.get(registry)).clear();

         field1.setAccessible(false);
         field2.setAccessible(false);
         field3.setAccessible(false);

         Method[] methods = registry.getClass().getDeclaredMethods();
         Method method = null;
         for (Method m : methods) {
            if (m.getName().equals("postConstruct")) {
               method = m;
               break;
            }
         }
         if (method != null) {
            boolean access = method.isAccessible();
            method.setAccessible(true);
            try {
               method.invoke(registry);
            } finally {
               method.setAccessible(access);
            }
         }
      } catch (Throwable th) {
         OseeLog.log(this.getClass(), Level.SEVERE, th);
      }
   }

   private void restoreStateFromMemento(IProgressMonitor restore) {
      File mementoFile = OseeData.getFile(OTE_MEMENTO);
      if (mementoFile.exists()) {
         try {
            IWorkbench workbench = PlatformUI.getWorkbench();
            if (managedArea.getInstalledBundles().size() > 0 && workbench != null && workbench.getActiveWorkbenchWindow() != null) {
               IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
               IPerspectiveDescriptor originalPerspective = page.getPerspective();
               IPerspectiveDescriptor[] pds = page.getOpenPerspectives();

               XMLMemento memento = XMLMemento.createReadRoot(new FileReader(mementoFile));
               IMemento[] perspectives = memento.getChildren(TAG_PERSPECTIVE);
               if (perspectives != null) {
                  for (IMemento perspective : perspectives) {
                     IMemento[] views = perspective.getChildren(TAG_VIEW);
                     if (views != null && views.length > 0) {
                        String perspectiveId = perspective.getString("id");
                        for (IPerspectiveDescriptor pd : pds) {
                           if (pd.getId().equals(perspectiveId)) {
                              page.setPerspective(pd);
                              for (IMemento view : views) {
                                 String viewId = view.getString("id");
                                 String secondId = view.getString("secondId");
                                 if (viewId != null) {
                                    //show view
                                    try {
                                       page.showView(viewId, secondId, IWorkbenchPage.VIEW_ACTIVATE);
                                    } catch (PartInitException ex) {
                                       System.err.println("COULD NOT FIND " + viewId + ", with ID # = " + secondId);
                                       ex.printStackTrace();
                                    }
                                 }
                              }
                              break;
                           }
                        }
                     }
                  }
               }

               page.setPerspective(originalPerspective);
            }

         } catch (WorkbenchException e) {
            e.printStackTrace();
         } catch (FileNotFoundException e) {
            e.printStackTrace();
         }
      }
   }

   private void startBundles(Collection<BundleInfoLite> bundles, IProgressMonitor subMonitor) {
      BundleContext context = FrameworkUtil.getBundle(this.getClass()).getBundleContext();
      subMonitor.setTaskName("Installing Bundles");
      double workPercentage = 0.50 / (bundles.size() * 2);
      int workAmount = Operations.calculateWork(Operations.TASK_WORK_RESOLUTION, workPercentage);
      for (BundleInfoLite info : bundles) {
         if (!info.isInstalled()) {
            try {
               info.install(context);
            } catch (BundleException e) {
               // do nothing
            } catch (IOException e) {
               // do nothing
            }
         }
         subMonitor.worked(workAmount * 2);
      }
      for (BundleInfoLite info : bundles) {
         if (!info.isStarted()) {
            try {
               info.start(context);
            } catch (BundleException e) {
               // do nothing
            }
         }
         subMonitor.worked(workAmount);
      }
   }

}
