/*********************************************************************
 * Copyright (c) 2022 Boeing
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
package org.eclipse.osee.ats.ide.workflow.priority;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.osee.ats.api.team.Priorities;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.swt.graphics.Image;

public class PriorityLabelProvider implements ILabelProvider {

   @Override
   public Image getImage(Object arg0) {
      Priorities type = (Priorities) arg0;
      return PriorityColumnUI.getImage(type);
   }

   @Override
   public String getText(Object arg0) {
      Priorities type = (Priorities) arg0;
      if (Strings.isValid(type.getDescription())) {
         return String.format("%s - %s", type.name(), type.getDescription());
      }
      return type.name();
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
