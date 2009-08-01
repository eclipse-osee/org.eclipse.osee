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
import org.eclipse.osee.framework.core.exception.OseeCoreException;
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
      return "";
   }

   @Override
   public Object getAdapter(Class adapter) {
      return null;
   }

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
