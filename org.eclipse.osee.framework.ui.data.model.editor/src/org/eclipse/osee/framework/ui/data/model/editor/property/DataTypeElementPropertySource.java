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
import org.eclipse.osee.framework.ui.data.model.editor.model.DataType;
import org.eclipse.osee.framework.ui.plugin.views.property.ModelPropertySource;
import org.eclipse.osee.framework.ui.plugin.views.property.PropertyId;
import org.eclipse.osee.framework.ui.plugin.views.property.ReadOnlyPropertyDescriptor;
import org.eclipse.osee.framework.ui.plugin.views.property.StringPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

/**
 * @author Roberto E. Escobar
 */
public class DataTypeElementPropertySource extends ModelPropertySource {

   protected final PropertyId idName;
   protected final PropertyId idNamespace;
   protected final PropertyId idUniqueId;

   public DataTypeElementPropertySource(String categoryName, Object dataType) {
      super(dataType);
      idUniqueId = new PropertyId(categoryName, "UniqueId");
      idName = new PropertyId(categoryName, "Name");
      idNamespace = new PropertyId(categoryName, "Namespace");
   }

   protected void addPropertyDescriptors(List<IPropertyDescriptor> list) {
      list.add(new ReadOnlyPropertyDescriptor(idUniqueId));
      list.add(new StringPropertyDescriptor(idName));
      list.add(new StringPropertyDescriptor(idNamespace));
   }

   protected DataType getDataTypeElement() {
      return (DataType) getModel();
   }

   public Object getPropertyValue(Object id) {
      if (id == idUniqueId) return ReadOnlyPropertyDescriptor.fromModel(getDataTypeElement().getUniqueId());
      if (id == idName) return StringPropertyDescriptor.fromModel(getDataTypeElement().getName());
      if (id == idNamespace) return StringPropertyDescriptor.fromModel(getDataTypeElement().getNamespace());
      return null;
   }

   public boolean isPropertyResettable(Object id) {
      return id == idName || id == idNamespace;
   }

   public boolean isPropertySet(Object id) {
      if (id == idUniqueId) return true;
      if (id == idName) return getDataTypeElement().getName() != null;
      if (id == idNamespace) return getDataTypeElement().getNamespace() != null;
      return false;
   }

   public void resetPropertyValue(Object id) {
      if (id == idUniqueId) return;
      if (id == idName) getDataTypeElement().setName(null);
      if (id == idNamespace) getDataTypeElement().setNamespace(null);
   }

   public void setPropertyValue(Object id, Object value) {
      if (id == idUniqueId) return;
      if (id == idName) getDataTypeElement().setName(StringPropertyDescriptor.toModel(value));
      if (id == idNamespace) getDataTypeElement().setNamespace(StringPropertyDescriptor.toModel(value));
   }

}