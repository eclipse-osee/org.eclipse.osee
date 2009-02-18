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
import org.eclipse.osee.framework.ui.data.model.editor.model.ArtifactDataType;
import org.eclipse.osee.framework.ui.plugin.views.property.ImagePropertyDescriptor;
import org.eclipse.osee.framework.ui.plugin.views.property.PropertyId;
import org.eclipse.osee.framework.ui.plugin.views.property.StringPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactPropertySource extends DataTypeElementPropertySource {
   protected final PropertyId idImage;

   public ArtifactPropertySource(String categoryName, Object dataType) {
      super(categoryName, (ArtifactDataType) dataType);
      idImage = new PropertyId(categoryName, "Image");
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.plugin.views.property.AbstractPropertySource#addPropertyDescriptors(java.util.List)
    */
   @Override
   protected void addPropertyDescriptors(List<IPropertyDescriptor> list) {
      super.addPropertyDescriptors(list);
      list.add(new StringPropertyDescriptor(idImage));
   }

   protected ArtifactDataType getDataTypeElement() {
      return (ArtifactDataType) getModel();
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.views.properties.IPropertySource2#isPropertyResettable(java.lang.Object)
    */
   @Override
   public boolean isPropertyResettable(Object id) {
      return super.isPropertyResettable(id) || id == idImage;
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.views.properties.IPropertySource2#isPropertySet(java.lang.Object)
    */
   @Override
   public boolean isPropertySet(Object id) {
      if (id == idImage) return getDataTypeElement().getImage() != null;
      return super.isPropertySet(id);
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
    */
   @Override
   public Object getPropertyValue(Object id) {
      if (id == idImage) return ImagePropertyDescriptor.fromModel(getDataTypeElement().getImage());
      return super.getPropertyValue(id);
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.views.properties.IPropertySource#resetPropertyValue(java.lang.Object)
    */
   @Override
   public void resetPropertyValue(Object id) {
      if (id == idImage) getDataTypeElement().setImage(null);
      super.resetPropertyValue(id);
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
    */
   @Override
   public void setPropertyValue(Object id, Object value) {
      if (id == idImage) getDataTypeElement().setImage(ImagePropertyDescriptor.toModel(value));
      super.setPropertyValue(id, value);
   }

}
