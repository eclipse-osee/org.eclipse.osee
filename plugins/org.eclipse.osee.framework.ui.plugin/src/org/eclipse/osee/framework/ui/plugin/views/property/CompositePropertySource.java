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
import org.eclipse.ui.views.properties.IPropertySource;

public class CompositePropertySource extends AbstractPropertySource {

   private List<AbstractPropertySource> sources;

   public CompositePropertySource() {
      // do nothing
   }

   public CompositePropertySource(IPropertySource... sources) {
      if (sources != null) {
         for (IPropertySource source : sources) {
            add(source);
         }
      }
   }

   public void add(IPropertySource propertySource) {
      if (propertySource instanceof AbstractPropertySource) {
         if (sources == null) {
            sources = new ArrayList<>();
         }
         sources.add((AbstractPropertySource) propertySource);
      }
   }

   @Override
   protected void addPropertyDescriptors(List<IPropertyDescriptor> list) {
      if (sources != null) {
         for (AbstractPropertySource propertySource : sources) {
            propertySource.addPropertyDescriptors(list);
         }
      }
   }

   @Override
   public Object getPropertyValue(Object id) {
      if (sources != null) {
         for (AbstractPropertySource propertySource : sources) {
            Object result = propertySource.getPropertyValue(id);
            if (result != null) {
               return result;
            }
         }
      }
      return null;
   }

   @Override
   public boolean isPropertyResettable(Object id) {
      if (sources != null) {
         for (AbstractPropertySource propertySource : sources) {
            if (propertySource.isPropertyResettable(id)) {
               return true;
            }
         }
      }
      return false;
   }

   @Override
   public boolean isPropertySet(Object id) {
      if (sources != null) {
         for (AbstractPropertySource propertySource : sources) {
            if (propertySource.isPropertySet(id)) {
               return true;
            }
         }
      }
      return false;
   }

   @Override
   public void resetPropertyValue(Object id) {
      if (sources != null) {
         for (AbstractPropertySource propertySource : sources) {
            propertySource.resetPropertyValue(id);
         }
      }
   }

   @Override
   public void setPropertyValue(Object id, Object value) {
      if (sources != null) {
         for (AbstractPropertySource propertySource : sources) {
            propertySource.setPropertyValue(id, value);
         }
      }
   }

}