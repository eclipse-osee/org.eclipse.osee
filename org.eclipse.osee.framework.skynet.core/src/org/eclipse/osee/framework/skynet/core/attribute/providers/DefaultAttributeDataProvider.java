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
package org.eclipse.osee.framework.skynet.core.attribute.providers;

import org.eclipse.osee.framework.skynet.core.attribute.AttributeStateManager;

/**
 * @author Roberto E. Escobar
 */
public class DefaultAttributeDataProvider extends AbstractAttributeDataProvider implements ICharacterAttributeDataProvider {

   private String value;

   public DefaultAttributeDataProvider(AttributeStateManager attributeStateManager) {
      super(attributeStateManager);
      this.value = null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.IAttributeDataProvider#getDisplayableString()
    */
   @Override
   public String getDisplayableString() {
      return value;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.IAttributeDataProvider#getValueAsString()
    */
   @Override
   public String getValueAsString() {
      return this.value;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.IAttributeDataProvider#setDisplayableString(java.lang.String)
    */
   @Override
   public void setDisplayableString(String toDisplay) {
      throw new UnsupportedOperationException();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.IAttributeDataProvider#setValue(java.lang.String)
    */
   @Override
   public void setValue(String value) {
      if (this.value == value) {
         return;
      }
      if (this.value != null && this.value.equals(value)) {
         return;
      }
      this.value = value;
      getAttributeStateManager().setDirty();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.providers.IDataAccessObject#getData()
    */
   @Override
   public Object[] getData() throws Exception {
      return new Object[] {this.value, ""};
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.providers.IDataAccessObject#loadData(java.lang.Object[])
    */
   @Override
   public void loadData(Object... objects) throws Exception {
      if (objects != null && objects.length > 0) {
         this.value = (String) objects[0];
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.providers.IDataAccessObject#persist()
    */
   @Override
   public void persist() throws Exception {
      // Do Nothing
   }
}
