/*
 * Created on Apr 28, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.plugin.workspace;

import java.util.Hashtable;
import org.eclipse.osee.framework.ui.plugin.OseePluginUiActivator;
import org.eclipse.osee.framework.ui.plugin.workspace.internal.SafeWorkspaceAccessImpl;
import org.eclipse.ui.IStartup;
import org.osgi.framework.BundleContext;

/**
 * @author b1528444
 *
 */
public class EarlyStartup implements IStartup {

   /* (non-Javadoc)
    * @see org.eclipse.ui.IStartup#earlyStartup()
    */
   @Override
   public void earlyStartup() {
      registerWorkspaceAccessService();
   }
   
   private void registerWorkspaceAccessService(){
      BundleContext context = OseePluginUiActivator.getInstance().getContext();
      context.registerService(SafeWorkspaceAccess.class.getName(), new SafeWorkspaceAccessImpl(), new Hashtable());
   }

}
