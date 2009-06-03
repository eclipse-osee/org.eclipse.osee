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
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
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

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.plugin.views.property.AbstractPropertySource#addPropertyDescriptors(java.util.List)
    */
   @Override
   protected void addPropertyDescriptors(List<IPropertyDescriptor> list) {
      list.add(new StringPropertyDescriptor(idEnumTypeList));
   }

   protected AttributeDataType getDataTypeElement() {
      return (AttributeDataType) getModel();
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.views.properties.IPropertySource2#isPropertyResettable(java.lang.Object)
    */
   @Override
   public boolean isPropertyResettable(Object id) {
      return id == idEnumTypeList;
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.views.properties.IPropertySource2#isPropertySet(java.lang.Object)
    */
   @Override
   public boolean isPropertySet(Object id) {
      if (id == idEnumTypeList) return false;
      return false;
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
    */
   @Override
   public Object getPropertyValue(Object id) {
      if (id == idEnumTypeList) {
         int enumTypeId = getDataTypeElement().getEnumTypeId();
         OseeEnumType enumType = null;
         try {
            enumType = OseeEnumTypeManager.getType(enumTypeId);
         } catch (OseeCoreException ex) {
            OseeLog.log(ODMEditorActivator.class, Level.SEVERE, ex);
         }
         return StringPropertyDescriptor.fromModel(enumType != null ? enumType.valuesAsOrderedStringSet().toString() : Strings.emptyString());
      }
      return 0;
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.views.properties.IPropertySource#resetPropertyValue(java.lang.Object)
    */
   @Override
   public void resetPropertyValue(Object id) {
      // Here
      System.out.println("resetPropertyValue");
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
    */
   @Override
   public void setPropertyValue(Object id, Object value) {
      // Here
      System.out.println("setPropertyValue");
   }
}
