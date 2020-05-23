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

package org.eclipse.osee.framework.ui.plugin.views.property;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource2;

public abstract class AbstractPropertySource implements IPropertySource2 {
   public static final String READ_ONLY = " (Read-Only)";

   private IPropertyDescriptor[] descriptors;

   public AbstractPropertySource() {
      // do nothing
   }

   protected abstract void addPropertyDescriptors(List<IPropertyDescriptor> list);

   @Override
   public Object getEditableValue() {
      return this;
   }

   @Override
   public IPropertyDescriptor[] getPropertyDescriptors() {
      if (descriptors == null) {
         ArrayList<IPropertyDescriptor> list = new ArrayList<>();
         addPropertyDescriptors(list);
         descriptors = new IPropertyDescriptor[list.size()];
         list.toArray(descriptors);
         return descriptors;
      }
      return descriptors;
   }

}