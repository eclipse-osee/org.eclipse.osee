/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.ui.plugin.workspace.internal;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.osee.framework.ui.plugin.workspace.SafeWorkspaceAccess;
import org.eclipse.osee.framework.ui.plugin.workspace.WrapResourceChangeListener;

/**
 * @author Andrew M. Finkbeiner
 */
public class SafeWorkspaceAccessImpl implements SafeWorkspaceAccess {

   @Override
   public IWorkspace getWorkspace() {
      return ResourcesPlugin.getWorkspace();
   }

   @Override
   public void addResourceChangeListener(WrapResourceChangeListener workspaceListener) {
      ResourcesPlugin.getWorkspace().addResourceChangeListener(new ResourceChangeListenerImpl(workspaceListener));
   }

}
