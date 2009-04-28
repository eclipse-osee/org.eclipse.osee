/*
 * Created on Apr 28, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.plugin.workspace;

import org.eclipse.core.resources.IWorkspace;


/**
 * @author b1528444
 *
 */
public interface SafeWorkspaceAccess {
   IWorkspace getWorkspace();

   /**
    * @param workspaceListener
    */
   void addResourceChangeListener(WrapResourceChangeListener workspaceListener);
}
