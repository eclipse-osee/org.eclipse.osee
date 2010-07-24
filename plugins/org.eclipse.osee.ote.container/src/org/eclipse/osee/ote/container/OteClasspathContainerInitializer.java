/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.container;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ClasspathContainerInitializer;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

public class OteClasspathContainerInitializer extends
ClasspathContainerInitializer {

   public OteClasspathContainerInitializer() {
   }

   @Override
   public void initialize(IPath containerPath, IJavaProject project) throws CoreException {

      OteClasspathContainer oteClasspathContainer = new OteClasspathContainer(containerPath, project);
      JavaCore.setClasspathContainer(containerPath, new IJavaProject[]{project}, new IClasspathContainer[] {oteClasspathContainer}, null);
   }

}
