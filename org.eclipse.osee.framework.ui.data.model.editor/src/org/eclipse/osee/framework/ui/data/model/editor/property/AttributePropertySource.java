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
import org.eclipse.osee.framework.ui.data.model.editor.model.AttributeDataType;
import org.eclipse.osee.framework.ui.plugin.views.property.IntegerPropertyDescriptor;
import org.eclipse.osee.framework.ui.plugin.views.property.ModelPropertySource;
import org.eclipse.osee.framework.ui.plugin.views.property.PropertyId;
import org.eclipse.osee.framework.ui.plugin.views.property.StringPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

/**
 * @author Roberto E. Escobar
 */
public class AttributePropertySource extends ModelPropertySource {

   protected final PropertyId idDefaultValue;
   protected final PropertyId idValidityXml;
   protected final PropertyId idToolTipText;
   protected final PropertyId idFileTypeExtension;
   protected final PropertyId idTaggerId;

   protected final PropertyId idMinOccurrence;
   protected final PropertyId idMaxOccurrence;

   protected final PropertyId idBaseAttributeClass;
   protected final PropertyId idProviderAttributeClass;

   public AttributePropertySource(String categoryName, Object dataType) {
      super(dataType);
      idDefaultValue = new PropertyId(categoryName, "Default Value");
      idValidityXml = new PropertyId(categoryName, "Validity Xml");
      idToolTipText = new PropertyId(categoryName, "ToolTip");
      idFileTypeExtension = new PropertyId(categoryName, "File Extension");
      idTaggerId = new PropertyId(categoryName, "Tagger Id");
      idMinOccurrence = new PropertyId(categoryName, "Min Occurrence");
      idMaxOccurrence = new PropertyId(categoryName, "Max Occurrence");
      idBaseAttributeClass = new PropertyId(categoryName, "Base Attribute Class");
      idProviderAttributeClass = new PropertyId(categoryName, "Base Provider Attribute Class");
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.plugin.views.property.AbstractPropertySource#addPropertyDescriptors(java.util.List)
    */
   @Override
   protected void addPropertyDescriptors(List<IPropertyDescriptor> list) {
      list.add(new StringPropertyDescriptor(idDefaultValue));
      list.add(new StringPropertyDescriptor(idValidityXml));
      list.add(new StringPropertyDescriptor(idToolTipText));
      list.add(new StringPropertyDescriptor(idFileTypeExtension));
      list.add(new StringPropertyDescriptor(idTaggerId));

      list.add(new IntegerPropertyDescriptor(idMinOccurrence));
      list.add(new IntegerPropertyDescriptor(idMaxOccurrence));

      list.add(new AttributeBaseClassPropertyDescriptor(idBaseAttributeClass));
      list.add(new AttributeProviderPropertyDescriptor(idProviderAttributeClass));
   }

   protected AttributeDataType getDataTypeElement() {
      return (AttributeDataType) getModel();
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.views.properties.IPropertySource2#isPropertyResettable(java.lang.Object)
    */
   @Override
   public boolean isPropertyResettable(Object id) {
      return id == idDefaultValue || id == idValidityXml || id == idToolTipText || id == idFileTypeExtension || id == idTaggerId || id == idMinOccurrence || id == idMaxOccurrence || id == idBaseAttributeClass || id == idProviderAttributeClass;
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.views.properties.IPropertySource2#isPropertySet(java.lang.Object)
    */
   @Override
   public boolean isPropertySet(Object id) {
      if (id == idDefaultValue) return getDataTypeElement().getDefaultValue() != null;
      if (id == idValidityXml) return getDataTypeElement().getValidityXml() != null;
      if (id == idToolTipText) return getDataTypeElement().getToolTipText() != null;
      if (id == idFileTypeExtension) return getDataTypeElement().getFileTypeExtension() != null;
      if (id == idTaggerId) return getDataTypeElement().getTaggerId() != null;
      if (id == idMinOccurrence) return getDataTypeElement().getMinOccurrence() != -1;
      if (id == idMaxOccurrence) return getDataTypeElement().getMaxOccurrence() != -1;
      if (id == idBaseAttributeClass) return getDataTypeElement().getBaseAttributeClass() != null;
      if (id == idProviderAttributeClass) return getDataTypeElement().getProviderAttributeClass() != null;
      return false;
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
    */
   @Override
   public Object getPropertyValue(Object id) {
      if (id == idDefaultValue) return StringPropertyDescriptor.fromModel(getDataTypeElement().getDefaultValue());
      if (id == idValidityXml) return StringPropertyDescriptor.fromModel(getDataTypeElement().getValidityXml());
      if (id == idToolTipText) return StringPropertyDescriptor.fromModel(getDataTypeElement().getToolTipText());
      if (id == idFileTypeExtension) return StringPropertyDescriptor.fromModel(getDataTypeElement().getFileTypeExtension());
      if (id == idTaggerId) return StringPropertyDescriptor.fromModel(getDataTypeElement().getTaggerId());
      if (id == idMinOccurrence) return IntegerPropertyDescriptor.fromModel(getDataTypeElement().getMinOccurrence());
      if (id == idMaxOccurrence) return IntegerPropertyDescriptor.fromModel(getDataTypeElement().getMaxOccurrence());
      if (id == idBaseAttributeClass) return AttributeBaseClassPropertyDescriptor.fromModel(getDataTypeElement().getBaseAttributeClass());
      if (id == idProviderAttributeClass) return AttributeProviderPropertyDescriptor.fromModel(getDataTypeElement().getProviderAttributeClass());
      return false;
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.views.properties.IPropertySource#resetPropertyValue(java.lang.Object)
    */
   @Override
   public void resetPropertyValue(Object id) {
      if (id == idDefaultValue) getDataTypeElement().setDefaultValue(null);
      if (id == idValidityXml) getDataTypeElement().setValidityXml(null);
      if (id == idToolTipText) getDataTypeElement().setToolTipText(null);
      if (id == idFileTypeExtension) getDataTypeElement().setFileTypeExtension(null);
      if (id == idTaggerId) getDataTypeElement().setTaggerId(null);
      if (id == idMinOccurrence) getDataTypeElement().setMinOccurrence(-1);
      if (id == idMaxOccurrence) getDataTypeElement().setMaxOccurrence(-1);
      if (id == idBaseAttributeClass) getDataTypeElement().setBaseAttributeClass(null);
      if (id == idProviderAttributeClass) getDataTypeElement().setProviderAttributeClass(null);
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
    */
   @Override
   public void setPropertyValue(Object id, Object value) {
      if (id == idDefaultValue) getDataTypeElement().setDefaultValue(StringPropertyDescriptor.toModel(value));
      if (id == idValidityXml) getDataTypeElement().setValidityXml(StringPropertyDescriptor.toModel(value));
      if (id == idToolTipText) getDataTypeElement().setToolTipText(StringPropertyDescriptor.toModel(value));
      if (id == idFileTypeExtension) getDataTypeElement().setFileTypeExtension(StringPropertyDescriptor.toModel(value));
      if (id == idTaggerId) getDataTypeElement().setTaggerId(StringPropertyDescriptor.toModel(value));
      if (id == idMinOccurrence) getDataTypeElement().setMinOccurrence(IntegerPropertyDescriptor.toModel(value));
      if (id == idMaxOccurrence) getDataTypeElement().setMaxOccurrence(IntegerPropertyDescriptor.toModel(value));
      if (id == idBaseAttributeClass) getDataTypeElement().setBaseAttributeClass(
            AttributeBaseClassPropertyDescriptor.toModel(value));
      if (id == idProviderAttributeClass) getDataTypeElement().setProviderAttributeClass(
            AttributeProviderPropertyDescriptor.toModel(value));
   }

}
