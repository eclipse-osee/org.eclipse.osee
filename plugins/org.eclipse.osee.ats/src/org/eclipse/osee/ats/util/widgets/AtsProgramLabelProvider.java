/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.util.widgets;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.osee.ats.core.client.config.IAtsProgramClient;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public class AtsProgramLabelProvider implements ILabelProvider {

   @Override
   public Image getImage(Object element) {
      return null;
   }

   @Override
   public String getText(Object element) {
      if (element instanceof IAtsProgramClient) {
         IAtsProgramClient program = (IAtsProgramClient) element;
         boolean active;
         try {
            active = program.isActive();
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            return "Exception: " + ex.getLocalizedMessage();
         }
         return ((IAtsProgramClient) element).getName() + (active ? "" : " (InActive)");
      }
      return "Unknown";
   }

   @Override
   public void addListener(ILabelProviderListener listener) {
      // do nothing
   }

   @Override
   public void dispose() {
      // do nothing
   }

   @Override
   public boolean isLabelProperty(Object element, String property) {
      return false;
   }

   @Override
   public void removeListener(ILabelProviderListener listener) {
      // do nothing
   }

}
