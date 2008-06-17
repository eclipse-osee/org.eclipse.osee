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

import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.skynet.core.exception.OseeDataStoreException;

/**
 * @author Roberto E. Escobar
 */
public class DefaultAttributeDataProvider extends AbstractAttributeDataProvider implements ICharacterAttributeDataProvider {

   /**
    * @param attribute
    */
   public DefaultAttributeDataProvider(Attribute<?> attribute) {
      super(attribute);
   }

   private String value;

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.AbstractAttributeDataProvider#getDisplayableString()
    */
   @Override
   public String getDisplayableString() {
      return value;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.AbstractAttributeDataProvider#getValueAsString()
    */
   @Override
   public String getValueAsString() {
      return this.value;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.AbstractAttributeDataProvider#setDisplayableString(java.lang.String)
    */
   @Override
   public void setDisplayableString(String toDisplay) {
      throw new UnsupportedOperationException();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.AbstractAttributeDataProvider#setValue(java.lang.String)
    */
   @Override
   public boolean setValue(String value) {
      boolean response = false;

      if (this.value == value || (this.value != null && this.value.equals(value))) {
         response = false;
      } else {
         this.value = value;
         response = true;
      }
      return response;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.providers.IDataAccessObject#getData()
    */
   @Override
   public Object[] getData() throws OseeDataStoreException {
      return new Object[] {this.value, ""};
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.providers.IDataAccessObject#loadData(java.lang.Object[])
    */
   @Override
   public void loadData(Object... objects) throws OseeDataStoreException {
      if (objects != null && objects.length > 0) {
         this.value = (String) objects[0];
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.providers.IDataAccessObject#persist()
    */
   @Override
   public void persist() throws OseeDataStoreException {
      // Do Nothing
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.providers.IAttributeDataProvider#purge()
    */
   @Override
   public void purge() throws OseeDataStoreException {
      try {
         ArtifactPersistenceManager.purgeAttribute(getAttribute(), getAttribute().getAttrId());
      } catch (Exception ex) {
         throw new OseeDataStoreException(ex);
      }
   }
}
