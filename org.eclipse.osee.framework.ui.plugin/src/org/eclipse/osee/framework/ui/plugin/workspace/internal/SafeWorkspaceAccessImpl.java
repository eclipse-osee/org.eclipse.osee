/*
 * Created on Apr 28, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.plugin.workspace.internal;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.osee.framework.ui.plugin.workspace.SafeWorkspaceAccess;
import org.eclipse.osee.framework.ui.plugin.workspace.WrapResourceChangeListener;

/**
 * @author b1528444
 *
 */
public class SafeWorkspaceAccessImpl implements SafeWorkspaceAccess {

   @Override
   public IWorkspace getWorkspace() {
      return ResourcesPlugin.getWorkspace();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.plugin.workspace.SafeWorkspaceAccess#addResourceChangeListener(org.eclipse.osee.framework.ui.plugin.workspace.WrapResourceChangeListener)
    */
   @Override
   public void addResourceChangeListener(WrapResourceChangeListener workspaceListener) {
      ResourcesPlugin.getWorkspace().addResourceChangeListener(new ResourceChangeListenerImpl(workspaceListener));
   }

}
