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
import org.eclipse.osee.framework.ui.data.model.editor.model.RelationDataType;
import org.eclipse.osee.framework.ui.plugin.views.property.BooleanPropertyDescriptor;
import org.eclipse.osee.framework.ui.plugin.views.property.ModelPropertySource;
import org.eclipse.osee.framework.ui.plugin.views.property.PropertyId;
import org.eclipse.osee.framework.ui.plugin.views.property.StringPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

/**
 * @author Roberto E. Escobar
 */
public class RelationPropertySource extends ModelPropertySource {

   protected final PropertyId idSideAName;
   protected final PropertyId idSideBName;
   protected final PropertyId idAToBPhrase;
   protected final PropertyId idBToAPhrase;
   protected final PropertyId idShortName;
   protected final PropertyId idOrdered;

   public RelationPropertySource(String categoryName, Object dataType) {
      super((RelationDataType) dataType);
      idSideAName = new PropertyId(categoryName, "Side A Name");
      idSideBName = new PropertyId(categoryName, "Side B Name");
      idAToBPhrase = new PropertyId(categoryName, "A to B Phrase");
      idBToAPhrase = new PropertyId(categoryName, "B to A Phrase");
      idShortName = new PropertyId(categoryName, "Short Name");
      idOrdered = new PropertyId(categoryName, "Is Ordered");
   }

   @Override
   protected void addPropertyDescriptors(List<IPropertyDescriptor> list) {
      list.add(new StringPropertyDescriptor(idSideAName));
      list.add(new StringPropertyDescriptor(idSideBName));
      list.add(new StringPropertyDescriptor(idAToBPhrase));
      list.add(new StringPropertyDescriptor(idBToAPhrase));
      list.add(new StringPropertyDescriptor(idShortName));
      list.add(new BooleanPropertyDescriptor(idOrdered));
   }

   protected RelationDataType getDataTypeElement() {
      return (RelationDataType) getModel();
   }

   @Override
   public boolean isPropertyResettable(Object id) {
      return id == idSideAName || id == idSideBName || id == idAToBPhrase || id == idBToAPhrase || id == idShortName || id == idOrdered;
   }

   @Override
   public boolean isPropertySet(Object id) {
      if (id == idSideAName) return getDataTypeElement().getSideAName() != null;
      if (id == idSideBName) return getDataTypeElement().getSideBName() != null;
      if (id == idAToBPhrase) return getDataTypeElement().getAToBPhrase() != null;
      if (id == idBToAPhrase) return getDataTypeElement().getBToAPhrase() != null;
      if (id == idShortName) return getDataTypeElement().getShortName() != null;
      if (id == idOrdered) {
         return true;
      }
      return false;
   }

   @Override
   public Object getPropertyValue(Object id) {
      if (id == idSideAName) return StringPropertyDescriptor.fromModel(getDataTypeElement().getSideAName());
      if (id == idSideBName) return StringPropertyDescriptor.fromModel(getDataTypeElement().getSideBName());
      if (id == idAToBPhrase) return StringPropertyDescriptor.fromModel(getDataTypeElement().getAToBPhrase());
      if (id == idBToAPhrase) return StringPropertyDescriptor.fromModel(getDataTypeElement().getBToAPhrase());
      if (id == idShortName) return StringPropertyDescriptor.fromModel(getDataTypeElement().getShortName());
      if (id == idOrdered) return BooleanPropertyDescriptor.fromModel(getDataTypeElement().getOrdered());
      return false;
   }

   @Override
   public void resetPropertyValue(Object id) {
      if (id == idSideAName) getDataTypeElement().setSideAName(null);
      if (id == idSideBName) getDataTypeElement().setSideBName(null);
      if (id == idAToBPhrase) getDataTypeElement().setAToBPhrase(null);
      if (id == idBToAPhrase) getDataTypeElement().setBToAPhrase(null);
      if (id == idShortName) getDataTypeElement().setShortName(null);
      if (id == idOrdered) getDataTypeElement().setOrdered(true);
   }

   @Override
   public void setPropertyValue(Object id, Object value) {
      if (id == idSideAName) getDataTypeElement().setSideAName(StringPropertyDescriptor.toModel(value));
      if (id == idSideBName) getDataTypeElement().setSideBName(StringPropertyDescriptor.toModel(value));
      if (id == idAToBPhrase) getDataTypeElement().setAToBPhrase(StringPropertyDescriptor.toModel(value));
      if (id == idBToAPhrase) getDataTypeElement().setBToAPhrase(StringPropertyDescriptor.toModel(value));
      if (id == idShortName) getDataTypeElement().setShortName(StringPropertyDescriptor.toModel(value));
      if (id == idOrdered) getDataTypeElement().setOrdered(BooleanPropertyDescriptor.toModel(value));
   }

}
