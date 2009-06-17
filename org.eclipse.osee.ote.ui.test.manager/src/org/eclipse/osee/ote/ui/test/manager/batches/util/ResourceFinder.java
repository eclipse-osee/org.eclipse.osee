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
package org.eclipse.osee.ote.ui.test.manager.batches.util;

import java.util.Collection;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.ui.test.manager.TestManagerPlugin;

/**
 * @author Roberto E. Escobar
 */
public class ResourceFinder {
   private static final String PROJECT_SET_EXTENSION = "psf";
   private static final String TEST_BATCH_SET_EXTENSION = "xml";

   private HashCollection<String, IFile> resourceMap;

   public ResourceFinder() {
      this.resourceMap = new HashCollection<String, IFile>();
   }

   public Set<String> getIds() {
      return resourceMap.keySet();
   }

   public Pair<IFile, IFile> getFileSet(String key) {
      IFile project = null;
      IFile testBatch = null;

      Collection<IFile> items = this.resourceMap.getValues(key);
      for (IFile file : items) {
         if (file.getFileExtension().equals(PROJECT_SET_EXTENSION)) {
            project = file;
         } else {
            testBatch = file;
         }
      }
      return new Pair<IFile, IFile>(project, testBatch);
   }

   public void findBatchAndProjectFiles(IProject project) {
      final HashCollection<String, IFile> localMap = new HashCollection<String, IFile>();

      try {
         project.accept(new IResourceVisitor() {

            @Override
            public boolean visit(IResource resource) throws CoreException {
               if (resource instanceof IFile) {
                  IFile file = (IFile) resource;
                  if (isValid(file) != false) {
                     String key = file.getName();
                     key = key.replace(file.getFileExtension(), "");
                     if (key.endsWith(".")) {
                        key = key.substring(0, key.length() - 1);
                     }
                     localMap.put(key, file);
                  }
               }
               return true;
            }
         });

      } catch (CoreException ex) {
         OseeLog.log(TestManagerPlugin.class, Level.SEVERE, ex);
      }
      // Remove incompletes
      this.resourceMap.clear();
      for (String key : localMap.keySet()) {
         Collection<IFile> values = localMap.getValues(key);
         if (values != null && values.size() == 2) {
            this.resourceMap.put(key, values);
         }
      }
   }

   private boolean isValid(IFile file) {
      boolean result = false;
      if (file != null && file.isAccessible() != false) {
         String extension = file.getFileExtension();
         result =
               extension.equalsIgnoreCase(PROJECT_SET_EXTENSION) || extension.equalsIgnoreCase(TEST_BATCH_SET_EXTENSION);
      }
      return result;
   }
}
