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

import java.util.Collection;
import org.eclipse.core.runtime.CoreException;

/**
 * @author Robert A. Fisher
 * @author Andrew M. Finkbeiner
 */
public class WorkspaceStarterNature extends JarCollectionNature {
   public static final String NATURE_ID = "org.eclipse.osee.framework.ui.workspacebundleloader.WorkspaceStarterNature";
   static final String BUNDLE_PATH_ATTRIBUTE = "WorkspaceBundlePath";

   public WorkspaceStarterNature() {
      super(BUNDLE_PATH_ATTRIBUTE);
   }

   public static Collection<WorkspaceStarterNature> getWorkspaceProjects() throws CoreException {
      return getWorkspaceProjects(NATURE_ID, WorkspaceStarterNature.class);
   }
}
