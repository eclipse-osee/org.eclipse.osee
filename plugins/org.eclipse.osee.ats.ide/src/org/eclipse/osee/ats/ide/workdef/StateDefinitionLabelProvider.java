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

package org.eclipse.osee.ats.ide.workdef;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public class StateDefinitionLabelProvider implements ILabelProvider {

   @Override
   public Image getImage(Object arg0) {
      return null;
   }

   @Override
   public String getText(Object arg0) {
      return ((StateDefinition) arg0).getName() + getCompletedAppend((StateDefinition) arg0);
   }

   private String getCompletedAppend(StateDefinition page) {
      if (page.isCompleted() && !page.getName().startsWith("Complete")) {
         if (!page.getName().equals("Completed")) {
            return " (Completed)";
         }
      }
      return "";
   }

   @Override
   public void addListener(ILabelProviderListener arg0) {
      // do nothing
   }

   @Override
   public void dispose() {
      // do nothing
   }

   @Override
   public boolean isLabelProperty(Object arg0, String arg1) {
      return false;
   }

   @Override
   public void removeListener(ILabelProviderListener arg0) {
      // do nothing
   }
}