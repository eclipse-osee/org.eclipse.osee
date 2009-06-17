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
package org.eclipse.osee.ote.ui.markers;

import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.ui.plugin.OseeUiActivator;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class MarkerPlugin extends OseeUiActivator {

   private FileWatchList filesToWatch;
   // The plug-in ID
   public static final String PLUGIN_ID = "org.eclipse.osee.ote.ui.markers";

   // The shared instance
   private static MarkerPlugin plugin;

   /**
    * The constructor
    */
   public MarkerPlugin() {
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
    */
   public void start(BundleContext context) throws Exception {
      super.start(context);
      plugin = this;
      filesToWatch = new FileWatchList();
      ResourcesPlugin.getWorkspace().addResourceChangeListener(new IResourceChangeListener() {

         public void resourceChanged(final IResourceChangeEvent event) {
            IResourceDelta delta = event.getDelta();
            try {
               delta.accept(new IResourceDeltaVisitor() {
                  public boolean visit(IResourceDelta delta) throws CoreException {
                     IPath path = delta.getFullPath();
                     String extension = path.getFileExtension();
                     if (extension != null) {
                        if ("tmo".equals(extension)) {
                           IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
                           // file.refreshLocal(depth, monitor);
                           if (file != null) {
                              switch (delta.getKind()) {
                                 case IResourceDelta.ADDED:
                                    // OseeLog.log(Activator.class, Level.INFO, String.format("ADDED %s updating
                                    // markers - delta kind: %d", file.getName(), delta.getKind()));
                                    // addMarkers(file);
                                    break;
                                 case IResourceDelta.CHANGED:
                                    // OseeLog.log(Activator.class, Level.INFO, String.format("CHANGED %s updating
                                    // markers - delta kind: %d", file.getName(), delta.getKind()));
                                    // updateMarkers(file);
                                    break;
                                 case IResourceDelta.REMOVED:
                                    // OseeLog.log(Activator.class, Level.INFO, String.format("removing %s markers -
                                    // delta kind: %d", file.getName(), delta.getKind()));
                                    removeMarkers(file);
                                    break;
                              }
                           }
                        }
                     }
                     if (delta.getAffectedChildren().length > 0) return true;
                     return false;
                  }
               });
            } catch (CoreException ex) {
               OseeLog.log(MarkerPlugin.class, Level.SEVERE, ex);
            }
         }

      }, IResourceChangeEvent.POST_CHANGE);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
    */
   public void stop(BundleContext context) throws Exception {
      plugin = null;
      super.stop(context);
   }

   /**
    * Returns the shared instance
    * 
    * @return the shared instance
    */
   public static MarkerPlugin getDefault() {
      return plugin;
   }

   public void addMarkers(IFile file) {
      removeMarkers(file);
      Jobs.runInJob("OTE Marker Processing", new ProcessOutfileSax(this, file), MarkerPlugin.class, MarkerPlugin.PLUGIN_ID, false);
   }

   public void removeMarkers(IFile file) {
      List<IMarker> markers = filesToWatch.get(file);
      if (markers != null) {
         for (IMarker marker : markers) {
            try {
               marker.delete();
            } catch (CoreException ex) {
            }
         }
      }
   }

   synchronized void updateMarkerInfo(IFile file, List<IMarker> markers) {
      List<IMarker> oldMarkers = filesToWatch.get(file);
      if(oldMarkers != null){
         oldMarkers.addAll(markers);
      } else {
         filesToWatch.put(file, markers);
      }
   }
}
