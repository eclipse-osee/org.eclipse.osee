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
package org.eclipse.osee.ats.world;

import java.util.logging.Level;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

/**
 * @author Donald G. Dunne
 */
public class WorldEditorInput implements IEditorInput {

   IWorldEditorProvider iWorldEditorProvider;

   /**
    * @return the iWorldEditorProvider
    */
   public IWorldEditorProvider getIWorldEditorProvider() {
      return iWorldEditorProvider;
   }

   public WorldEditorInput(IWorldEditorProvider iWorldEditorProvider) {
      this.iWorldEditorProvider = iWorldEditorProvider;
   }

   @Override
   public boolean equals(Object obj) {
      return false;
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.IEditorInput#exists()
    */
   public boolean exists() {
      return false;
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.IEditorInput#getImageDescriptor()
    */
   public ImageDescriptor getImageDescriptor() {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.IEditorInput#getPersistable()
    */
   public IPersistableElement getPersistable() {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.IEditorInput#getToolTipText()
    */
   public String getToolTipText() {
      try {
         return iWorldEditorProvider.getName();
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         return "Exception getting name: " + ex.getLocalizedMessage();
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
    */
   @Override
   public Object getAdapter(Class adapter) {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.IEditorInput#getName()
    */
   @Override
   public String getName() {
      try {
         return iWorldEditorProvider.getName();
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         return "Exception getting name: " + ex.getLocalizedMessage();
      }
   }
}
