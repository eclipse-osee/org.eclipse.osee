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
package org.eclipse.osee.framework.types.bridge.wizards;

import java.util.logging.Level;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.osee.framework.jdk.core.type.MutableBoolean;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.types.bridge.internal.Activator;

/**
 * @author Roberto E. Escobar
 */
public class OseeTypesViewerFilter extends ViewerFilter {

   private boolean processIFile(Object resource) {
      boolean toReturn = false;
      if (resource instanceof IFile) {
         IFile aFile = (IFile) resource;
         String currentExtension = aFile.getFileExtension();
         if (currentExtension.equalsIgnoreCase("osee")) {
            toReturn = true;
         }
      }
      return toReturn;
   }

   @Override
   public boolean select(Viewer viewer, Object parentElement, Object element) {
      if (element instanceof IProject) {
         if (((IProject) element).isOpen()) {
            return true;
         }
      } else if (element instanceof IContainer) {
         IContainer container = (IContainer) element;
         String name = container.getName();
         if (!name.startsWith(".") && !name.equals("osee")) {
            final MutableBoolean mutable = new MutableBoolean(false);
            try {
               container.accept(new IResourceVisitor() {

                  @Override
                  public boolean visit(IResource resource) throws CoreException {
                     mutable.setValue(processIFile(resource));
                     return mutable.getValue();
                  }
               }, IResource.DEPTH_INFINITE, true);
            } catch (CoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
            return mutable.getValue();
         }
      } else {
         return processIFile(element);
      }
      return false;
   }
}
