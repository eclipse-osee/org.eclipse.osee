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
package org.eclipse.osee.framework.ui.skynet.results;

import java.util.logging.Level;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

/**
 * @author Donald G. Dunne
 */
public class ResultsEditorInput implements IEditorInput {

   IResultsEditorProvider iresultsEditorProvider;

   /**
    * @return the iWorldEditorProvider
    */
   public IResultsEditorProvider getIWorldEditorProvider() {
      return iresultsEditorProvider;
   }

   public ResultsEditorInput(IResultsEditorProvider iresultsEditorProvider) {
      this.iresultsEditorProvider = iresultsEditorProvider;
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
      return "";
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
         return iresultsEditorProvider.getEditorName();
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
         return "Exception getting name: " + ex.getLocalizedMessage();
      }
   }
}
