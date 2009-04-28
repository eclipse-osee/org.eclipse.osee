/*
 * Created on Apr 28, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.plugin.workspace.internal;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.osee.framework.ui.plugin.workspace.WrapResourceChangeListener;

/**
 * @author b1528444
 *
 */
public class ResourceChangeListenerImpl implements IResourceChangeListener {

   private WrapResourceChangeListener listener;
   
   public ResourceChangeListenerImpl(WrapResourceChangeListener listener){
      this.listener = listener;
   }

   /* (non-Javadoc)
    * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
    */
   @Override
   public void resourceChanged(IResourceChangeEvent event) {
      listener.resourceChanged(event);
   }
   
}
