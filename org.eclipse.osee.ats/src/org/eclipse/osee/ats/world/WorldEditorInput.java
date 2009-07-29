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
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

/**
 * @author Donald G. Dunne
 */
public class WorldEditorInput implements IEditorInput {

   IWorldEditorProvider iWorldEditorProvider;

   @Override
   public int hashCode() {
      return iWorldEditorProvider.hashCode();
   }

   public IWorldEditorProvider getIWorldEditorProvider() {
      return iWorldEditorProvider;
   }

   public WorldEditorInput(IWorldEditorProvider iWorldEditorProvider) {
      this.iWorldEditorProvider = iWorldEditorProvider;
   }

   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof WorldEditorInput))
         return false;
      WorldEditorInput castObj = (WorldEditorInput) obj;
      return castObj.iWorldEditorProvider.equals(this.iWorldEditorProvider);
   }

   public boolean exists() {
      return false;
   }

   public ImageDescriptor getImageDescriptor() {
      return null;
   }

   public IPersistableElement getPersistable() {
      return null;
   }

   public String getToolTipText() {
      try {
         return iWorldEditorProvider.getName();
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         return "Exception getting name: " + ex.getLocalizedMessage();
      }
   }

   @SuppressWarnings("unchecked")
   @Override
   public Object getAdapter(Class adapter) {
      return null;
   }

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
