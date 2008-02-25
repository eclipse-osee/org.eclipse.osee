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
package org.eclipse.osee.framework.ui.skynet.render;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;

/**
 * @author Ryan D. Brooks
 */
public class EditChangeListener implements IResourceChangeListener {
   private IResourceDeltaVisitor visitor;

   public EditChangeListener(IFolder workingFolder) {
      this.visitor = new EditingFolderVisitor(workingFolder);
   }

   /*
    * (non-Javadoc)
    * The  workspace is locked during all resource change event notification
    * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
    */
   public void resourceChanged(IResourceChangeEvent event) {
      if (event.getType() == IResourceChangeEvent.POST_CHANGE) {
         try {
            event.getDelta().accept(visitor);
         } catch (CoreException ex) {
            OSEELog.logException(SkynetGuiPlugin.class, ex, true);
         }
      } else {
         OSEELog.logException(SkynetGuiPlugin.class, null, true);
      }
   }
}
