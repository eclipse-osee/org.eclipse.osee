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
package org.eclipse.osee.ote.runtimemanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.osee.framework.plugin.core.server.ResourceFinder;

/** 
 * Finds resources that 
 * @author Robert A. Fisher
 *
 */
public class UserLibResourceFinder extends ResourceFinder {

   public UserLibResourceFinder() {
   }

   @Override
   public byte[] find(String path) throws IOException {
      try {
         for (OteUserLibsNature userLibsNature : OteUserLibsNature.getWorkspaceProjects()) {
            IProject project = userLibsNature.getProject();
            
            IProjectDescription description;
            try {
               description = project.getDescription();
               if (description.hasNature("org.eclipse.jdt.core.javanature")) {
                  
                  IJavaProject javaProject = JavaCore.create(project);
                  
                  // Projects don't have to be stored in the workspace, so make sure to use the project.getLocation as the starting point
                  IPath candidate = project.getLocation().removeLastSegments(1).append(javaProject.getOutputLocation().makeAbsolute()).append(path);
                  if (candidate.toFile().exists()) {
                     File file = candidate.toFile();
                     
                     try {
                        return getBytes(new FileInputStream(file), file.length());
                     } catch (FileNotFoundException e) {
                     }
                  }
               }
            } catch (CoreException ex) {
               ex.printStackTrace();
            }
         }
      } catch (CoreException ex) {
      }
      return null;
   }

   @Override
   public void dispose() {
   }
}
