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

package org.eclipse.osee.framework.ui.plugin.views.property;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;

public class CompositePropertySource extends AbstractPropertySource {

   private List<AbstractPropertySource> sources;

   public CompositePropertySource() {
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
            sources = new ArrayList<AbstractPropertySource>();
         }
         sources.add((AbstractPropertySource) propertySource);
      }
   }

   protected void addPropertyDescriptors(List<IPropertyDescriptor> list) {
      if (sources != null) {
         for (AbstractPropertySource propertySource : sources) {
            propertySource.addPropertyDescriptors(list);
         }
      }
   }

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

   public void resetPropertyValue(Object id) {
      if (sources != null) {
         for (AbstractPropertySource propertySource : sources) {
            propertySource.resetPropertyValue(id);
         }
      }
   }

   public void setPropertyValue(Object id, Object value) {
      if (sources != null) {
         for (AbstractPropertySource propertySource : sources) {
            propertySource.setPropertyValue(id, value);
         }
      }
   }

}