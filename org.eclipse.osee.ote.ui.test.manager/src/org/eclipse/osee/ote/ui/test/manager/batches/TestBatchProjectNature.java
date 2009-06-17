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
package org.eclipse.osee.ote.ui.test.manager.batches;

import java.util.logging.Level;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.ui.test.manager.TestManagerPlugin;
import org.eclipse.osee.ote.ui.test.manager.batches.navigate.TestBatchRegistry;
import org.eclipse.osee.ote.ui.test.manager.batches.navigate.TestBatchSetupViewItems;
import org.eclipse.osee.ote.ui.test.manager.batches.util.ResourceFinder;
import org.eclipse.swt.widgets.Display;

/**
 * @author Roberto E. Escobar
 */
public class TestBatchProjectNature implements IProjectNature {
   public static final String NATURE_ID = "org.eclipse.osee.ote.ui.test.manager.TestBatchProjectNature";
   private IProject project;
   private ResourceFinder resourceFinder;
   private IResourceChangeListener resourceChangeListener;
   private IResourceChangeListener projectClosedListener;
   private IResourceChangeListener projectDeletedListener;
   private FindResourceWorker worker;

   public TestBatchProjectNature() {
      this.resourceFinder = new ResourceFinder();
      this.resourceChangeListener = new TestBatchResourceChangeListener();
      this.projectClosedListener = new TestBatchProjectClosedListener();
      this.projectDeletedListener = new TestBatchProjectDeletedListener();
      this.worker = new FindResourceWorker(this);
   }

   /* (non-Javadoc)
    * @see org.eclipse.core.resources.IProjectNature#configure()
    */
   @Override
   public void configure() throws CoreException {
      IWorkspace workspace = ResourcesPlugin.getWorkspace();
      workspace.addResourceChangeListener(resourceChangeListener, IResourceChangeEvent.POST_CHANGE);
      workspace.addResourceChangeListener(projectClosedListener, IResourceChangeEvent.PRE_CLOSE);
      workspace.addResourceChangeListener(projectDeletedListener, IResourceChangeEvent.PRE_DELETE);
      ResourcesPlugin.getWorkspace().getRoot().getWorkspace().run(worker, new NullProgressMonitor());
   }

   /* (non-Javadoc)
    * @see org.eclipse.core.resources.IProjectNature#deconfigure()
    */
   @Override
   public void deconfigure() throws CoreException {
      TestBatchRegistry registry = TestBatchSetupViewItems.getInstance().getRegistry();
      for (String key : resourceFinder.getIds()) {
         registry.deregisterTestBatch(key);
      }
      TestBatchDecorator.performLabelDecoratorUpdate(project);
      IWorkspace workspace = ResourcesPlugin.getWorkspace();
      workspace.removeResourceChangeListener(resourceChangeListener);
      workspace.removeResourceChangeListener(projectClosedListener);
      workspace.removeResourceChangeListener(projectDeletedListener);
   }

   /* (non-Javadoc)
    * @see org.eclipse.core.resources.IProjectNature#getProject()
    */
   @Override
   public IProject getProject() {
      return this.project;
   }

   /* (non-Javadoc)
    * @see org.eclipse.core.resources.IProjectNature#setProject(org.eclipse.core.resources.IProject)
    */
   @Override
   public void setProject(IProject project) {
      this.project = project;
   }

   public ResourceFinder getResourceFinder() {
      return resourceFinder;
   }

   public FindResourceWorker getWorker() {
      return worker;
   }

   public static void initializeProjectSet() {
      Display.getDefault().asyncExec(new Runnable() {
         public void run() {
            IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
            for (IProject project : projects) {
               try {
                  if (project != null && project.isOpen() == true) {
                     IProjectNature nature = project.getNature(TestBatchProjectNature.NATURE_ID);
                     if (nature != null) {
                        nature.configure();
                     }
                  }
               } catch (CoreException ex) {
                  OseeLog.log(TestManagerPlugin.class, Level.SEVERE, "Error initializing project nature", ex);
               }
            }
         }
      });
   }

   private final class TestBatchProjectDeletedListener implements IResourceChangeListener {

      /* (non-Javadoc)
       * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
       */
      @Override
      public void resourceChanged(IResourceChangeEvent event) {
         try {
            IResource resource = event.getResource();
            if (resource != null) {
               IProject project = resource.getProject();
               if (project != null) {
                  final IProjectNature nature = project.getNature(NATURE_ID);
                  if (nature != null) {
                     Display.getDefault().asyncExec(new Runnable() {
                        public void run() {
                           TestBatchProjectNature batchProjectNature = (TestBatchProjectNature) nature;
                           TestBatchRegistry registry = TestBatchSetupViewItems.getInstance().getRegistry();
                           for (String key : batchProjectNature.getResourceFinder().getIds()) {
                              registry.deregisterTestBatch(key);
                           }
                        }
                     });
                  }
               }
            }
         } catch (CoreException ex) {
            OseeLog.log(TestManagerPlugin.class, Level.SEVERE, "Error during resource change event", ex);
         }
      }

   }

   private final class TestBatchProjectClosedListener implements IResourceChangeListener {
      /* (non-Javadoc)
       * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
       */
      @Override
      public void resourceChanged(IResourceChangeEvent event) {
         try {
            IResource resource = event.getResource();
            if (resource != null) {
               IProject project = resource.getProject();
               if (project != null) {
                  final IProjectNature nature = project.getNature(NATURE_ID);
                  if (nature != null) {
                     Display.getDefault().asyncExec(new Runnable() {
                        public void run() {
                           TestBatchProjectNature tbpNature = (TestBatchProjectNature) nature;
                           TestBatchRegistry registry = TestBatchSetupViewItems.getInstance().getRegistry();
                           for (String key : tbpNature.getResourceFinder().getIds()) {
                              registry.deregisterTestBatch(key);
                           }
                        }
                     });
                  }
               }
            }
         } catch (CoreException ex) {
            OseeLog.log(TestManagerPlugin.class, Level.SEVERE, "Error during resource change event", ex);
         }
      }
   }

   private final class TestBatchResourceChangeListener implements IResourceChangeListener {
      /* (non-Javadoc)
       * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
       */
      @Override
      public void resourceChanged(IResourceChangeEvent event) {
         try {
            IResourceDelta delta = event.getDelta();
            IResourceDelta[] children = delta.getAffectedChildren();
            for (IResourceDelta childDelta : children) {
               IResource resource = childDelta.getResource();
               if (resource != null) {
                  IProject project = resource.getProject();
                  if (project != null) {
                     if (project.isOpen() != false && project.isNatureEnabled(NATURE_ID)) {
                        final IProjectNature nature = project.getNature(NATURE_ID);
                        if (nature != null) {
                           handleProjectFilesChanging((TestBatchProjectNature) nature);
                        }
                     }
                  }
               }
            }
         } catch (CoreException ex) {
            OseeLog.log(TestManagerPlugin.class, Level.SEVERE, "Error during resource change event", ex);
         }
      }

      private void handleProjectFilesChanging(final TestBatchProjectNature nature) {
         Display.getDefault().asyncExec(new Runnable() {
            public void run() {
               try {
                  nature.getWorker().run(new NullProgressMonitor());
               } catch (CoreException ex) {
                  OseeLog.log(TestManagerPlugin.class, Level.SEVERE, ex);
               }
            }
         });
      }
   };

   private final class FindResourceWorker implements IWorkspaceRunnable {

      private TestBatchProjectNature nature;

      public FindResourceWorker(TestBatchProjectNature nature) {
         this.nature = nature;
      }

      @Override
      public void run(IProgressMonitor monitor) throws CoreException {
         ResourceFinder finder = nature.getResourceFinder();
         finder.findBatchAndProjectFiles(nature.getProject());
         TestBatchRegistry registry = TestBatchSetupViewItems.getInstance().getRegistry();
         for (String key : finder.getIds()) {
            Pair<IFile, IFile> fileSet = finder.getFileSet(key);
            registry.registerTestBatch(key, fileSet.getKey().getLocationURI(), fileSet.getValue().getLocationURI());
         }
         Display.getDefault().asyncExec(new Runnable() {
            public void run() {
               TestBatchDecorator.performLabelDecoratorUpdate(nature.getProject());
            }
         });
      }
   }
}
