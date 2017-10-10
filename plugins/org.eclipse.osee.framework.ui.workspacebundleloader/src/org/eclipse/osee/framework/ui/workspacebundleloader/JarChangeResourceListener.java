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
package org.eclipse.osee.framework.ui.workspacebundleloader;

import java.net.MalformedURLException;
import java.net.URL;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

/**
 * Handler for IResourceChangeEvent.POST_CHANGE and IResourceChangeEvent.PRE_CLOSE events for projects with a given
 * nature ID that extends JarCollectionNature. POST_CHANGE events against the jars in projects with the nature, and
 * close events on the projects with the nature are detected and offered to an IJarChangeListener.
 *
 * @author Robert A. Fisher
 */
public class JarChangeResourceListener<T extends JarCollectionNature> implements IResourceChangeListener {
   private final String natureId;
   private final IJarChangeListener<T> listener;

   public JarChangeResourceListener(String natureId, IJarChangeListener<T> listener) {
      if (natureId == null) {
         throw new IllegalArgumentException("natureId must not be null");
      }
      if (listener == null) {
         throw new IllegalArgumentException("listener must not be null");
      }
      this.natureId = natureId;
      this.listener = listener;
   }

   @SuppressWarnings("unchecked")
   @Override
   public void resourceChanged(IResourceChangeEvent event) {
      try {
         if (event.getType() == IResourceChangeEvent.POST_CHANGE) {
            handleChangeEvent(event);
         } else if (event.getType() == IResourceChangeEvent.PRE_CLOSE) {
            IResource resource = event.getResource();
            if (resource != null && resource instanceof IProject) {
               IProject project = (IProject) resource;

               IProjectNature nature = project.getNature(natureId);
               if (nature != null) {
                  listener.handleNatureClosed((T) nature);
               }
            }
         }
      } catch (Exception ex) {
         // do nothing
      }
   }

   private void handleChangeEvent(IResourceChangeEvent event) throws CoreException, MalformedURLException {
      IResourceDelta rootDelta = event.getDelta();
      if (rootDelta != null) {
         boolean triggered = false;
         for (IResourceDelta child : rootDelta.getAffectedChildren()) {
            IResource resource = child.getResource();
            if (resource != null && resource instanceof IProject) {
               IProject project = (IProject) resource;

               IProjectNature nature = project.getNature(natureId);
               if (nature != null) {
                  JarCollectionNature starterNature = (JarCollectionNature) nature;
                  IPath[] paths = starterNature.getProjectRelativeBundlePaths();
                  for (IPath path : paths) {
                     IResourceDelta pluginDelta = child.findMember(path);
                     if (pluginDelta != null && isModifyingChange(pluginDelta)) {
                        handlePluginChanges(project.getLocation().removeLastSegments(1),
                           pluginDelta.getAffectedChildren());
                        triggered = true;
                     }
                  }
               }
            }
         }

         if (triggered) {
            listener.handlePostChange();
         }
      }
   }

   private boolean isModifyingChange(IResourceDelta pluginDelta) {
      boolean synch = (pluginDelta.getFlags() & IResourceDelta.SYNC) != 0;
      //		boolean content = (pluginDelta.getFlags() & IResourceDelta.CONTENT) != 0;
      //		boolean REPLACED = (pluginDelta.getFlags() & IResourceDelta.REPLACED) != 0;
      //		boolean MARKERS = (pluginDelta.getFlags() & IResourceDelta.MARKERS) != 0;
      //		boolean TYPE = (pluginDelta.getFlags() & IResourceDelta.TYPE) != 0;
      //		boolean MOVED_FROM = (pluginDelta.getFlags() & IResourceDelta.MOVED_FROM) != 0;
      //		boolean MOVED_TO = (pluginDelta.getFlags() & IResourceDelta.MOVED_TO) != 0;
      //		boolean OPEN = (pluginDelta.getFlags() & IResourceDelta.OPEN) != 0;
      //		boolean ENCODING = (pluginDelta.getFlags() & IResourceDelta.ENCODING) != 0;
      //		boolean DESCRIPTION = (pluginDelta.getFlags() & IResourceDelta.DESCRIPTION) != 0;

      //		boolean ADDED = (pluginDelta.getKind() & IResourceDelta.ADDED) != 0;
      //		boolean CHANGED = (pluginDelta.getKind() & IResourceDelta.CHANGED) != 0;
      //		boolean ADDED_PHANTOM = (pluginDelta.getKind() & IResourceDelta.ADDED_PHANTOM) != 0;
      //		boolean REMOVED_PHANTOM = (pluginDelta.getKind() & IResourceDelta.REMOVED_PHANTOM) != 0;
      return !synch;
   }

   protected void handlePluginChanges(IPath workspacePath, IResourceDelta[] affectedChildren) throws MalformedURLException {
      for (IResourceDelta affectedPluginDelta : affectedChildren) {
         URL url = workspacePath.append(affectedPluginDelta.getFullPath()).toFile().toURI().toURL();
         if (affectedPluginDelta.getFullPath().getFileExtension().equals("jar")) {
            try {
               switch (affectedPluginDelta.getKind()) {
                  case IResourceDelta.ADDED:
                     listener.handleBundleAdded(url);
                     break;
                  case IResourceDelta.CHANGED:
                     listener.handleBundleChanged(url);
                     break;
                  case IResourceDelta.REMOVED:
                     listener.handleBundleRemoved(url);
                     break;

                  default:
                     System.err.println(
                        "Do not expect change kind of " + generateKindString(affectedPluginDelta.getKind()));
               }
            } catch (RuntimeException ex) {
               ex.printStackTrace();
               throw ex;
            }
         }
      }
   }

   private String generateKindString(int kind) {
      switch (kind) {
         case IResourceDelta.ADDED:
            return "Added";
         case IResourceDelta.CHANGED:
            return "Changed";
         case IResourceDelta.REMOVED:
            return "Removed";
         default:
            return "Unexpected Kind: " + kind;
      }
   }

   protected String generateEventString(int type) {
      switch (type) {
         case IResourceChangeEvent.POST_BUILD:
            return "Post Build";
         case IResourceChangeEvent.POST_CHANGE:
            return "Post Change";
         case IResourceChangeEvent.PRE_BUILD:
            return "Pre Build";
         case IResourceChangeEvent.PRE_CLOSE:
            return "Pre Close";
         case IResourceChangeEvent.PRE_DELETE:
            return "Pre Delete";
         case IResourceChangeEvent.PRE_REFRESH:
            return "Pre Refresh";
         default:
            return "Unknown Code: " + type;
      }
   }
}
