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
package org.eclipse.osee.framework.ui.data.model.editor.property;

import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.attribute.OseeEnumType;
import org.eclipse.osee.framework.skynet.core.attribute.OseeEnumTypeManager;
import org.eclipse.osee.framework.ui.data.model.editor.ODMEditorActivator;
import org.eclipse.osee.framework.ui.data.model.editor.model.AttributeDataType;
import org.eclipse.osee.framework.ui.plugin.views.property.ModelPropertySource;
import org.eclipse.osee.framework.ui.plugin.views.property.PropertyId;
import org.eclipse.osee.framework.ui.plugin.views.property.StringPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

/**
 * @author Roberto E. Escobar
 */
public class EnumPropertySource extends ModelPropertySource {
   protected final PropertyId idEnumTypeList;

   public EnumPropertySource(String categoryName, Object dataType) {
      super(dataType);
      idEnumTypeList = new PropertyId(categoryName, "Enum Type");
   }

   @Override
   protected void addPropertyDescriptors(List<IPropertyDescriptor> list) {
      list.add(new StringPropertyDescriptor(idEnumTypeList));
   }

   protected AttributeDataType getDataTypeElement() {
      return (AttributeDataType) getModel();
   }

   @Override
   public boolean isPropertyResettable(Object id) {
      return id == idEnumTypeList;
   }

   @Override
   public boolean isPropertySet(Object id) {
      if (id == idEnumTypeList) {
         return false;
      }
      return false;
   }

   @Override
   public Object getPropertyValue(Object id) {
      if (id == idEnumTypeList) {
         int enumTypeId = getDataTypeElement().getEnumTypeId();
         OseeEnumType enumType = null;
         String descriptor = "";
         try {
            enumType = OseeEnumTypeManager.getType(enumTypeId);
            if (enumType != null) {
               descriptor = enumType.valuesAsOrderedStringSet().toString();
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(ODMEditorActivator.class, Level.SEVERE, ex);
         }
         return StringPropertyDescriptor.fromModel(descriptor);
      }
      return 0;
   }

   @Override
   public void resetPropertyValue(Object id) {
      // Here
      System.out.println("resetPropertyValue");
   }

   @Override
   public void setPropertyValue(Object id, Object value) {
      // Here
      System.out.println("setPropertyValue");
   }
}
