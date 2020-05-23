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

package org.eclipse.osee.framework.ui.skynet.results;

import java.util.logging.Level;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.core.data.Adaptable;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

/**
 * @author Donald G. Dunne
 */
public class ResultsEditorInput implements IEditorInput, Adaptable {

   IResultsEditorProvider iResultsEditorProvider;

   public IResultsEditorProvider getIWorldEditorProvider() {
      return iResultsEditorProvider;
   }

   public ResultsEditorInput(IResultsEditorProvider iresultsEditorProvider) {
      this.iResultsEditorProvider = iresultsEditorProvider;
   }

   @Override
   public boolean exists() {
      return false;
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return null;
   }

   @Override
   public IPersistableElement getPersistable() {
      return null;
   }

   @Override
   public String getToolTipText() {
      return "";
   }

   @Override
   public String getName() {
      try {
         return iResultsEditorProvider.getEditorName();
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         return "Exception getting name: " + ex.getLocalizedMessage();
      }
   }

   @Override
   public int hashCode() {
      return super.hashCode();
   }
}
