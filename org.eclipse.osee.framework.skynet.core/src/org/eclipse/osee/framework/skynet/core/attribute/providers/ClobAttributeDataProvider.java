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

import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeResourceProcessor;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeStateManager;
import org.eclipse.osee.framework.skynet.core.attribute.utils.BinaryContentUtils;

/**
 * @author Roberto E. Escobar
 */
public class ClobAttributeDataProvider extends AbstractAttributeDataProvider implements ICharacterAttributeDataProvider {
   private static final int MAX_VARCHAR_LENGTH = 4000;
   private String rawStringValue;

   private DataStore dataStore;

   /**
    * @param attributeStateManager
    */
   public ClobAttributeDataProvider(AttributeStateManager attributeStateManager) {
      super(attributeStateManager);
      this.dataStore = new DataStore(new AttributeResourceProcessor(attributeStateManager));
      this.rawStringValue = "";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.providers.IAttributeDataProvider#getDisplayableString()
    */
   @Override
   public String getDisplayableString() {
      return getValueAsString();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.providers.IAttributeDataProvider#setDisplayableString(java.lang.String)
    */
   @Override
   public void setDisplayableString(String toDisplay) {
      throw new UnsupportedOperationException();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.providers.ICharacterAttributeDataProvider#getValueAsString()
    */
   @Override
   public String getValueAsString() {
      String fromStorage = null;
      byte[] data = null;
      try {
         data = dataStore.getContent();
         if (data != null) {
            fromStorage = new String(data, "UTF-8");
         }
      } catch (Exception ex) {
         SkynetActivator.getLogger().log(Level.SEVERE, ex.toString(), ex);
      }
      String toReturn = fromStorage != null ? fromStorage : rawStringValue;
      return toReturn != null ? toReturn : "";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.providers.ICharacterAttributeDataProvider#setValue(java.lang.String)
    */
   @Override
   public void setValue(String value) {
      if (this.rawStringValue == value) {
         return;
      }
      if (this.rawStringValue != null && this.rawStringValue.equals(value)) {
         return;
      }
      try {
         storeValue(value);
         getAttributeStateManager().setDirty();
      } catch (UnsupportedEncodingException ex) {
         SkynetActivator.getLogger().log(Level.SEVERE, ex.toString(), ex);
      }
   }

   private String getExtension() {
      String toReturn = getAttributeStateManager().getAttributeManager().getAttributeType().getFileTypeExtension();
      if (Strings.isValid(toReturn) != true) {
         toReturn = "txt";
      }
      return toReturn;
   }

   private void storeValue(String value) throws UnsupportedEncodingException {
      if (value != null && value.length() > MAX_VARCHAR_LENGTH) {
         String extension = getExtension();
         String contentType = BinaryContentUtils.getContentType(extension);
         dataStore.setContent(value.getBytes("UTF-8"), extension, contentType, "UTF-8");
      } else {
         this.rawStringValue = value;
         dataStore.clear();
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.providers.IDataAccessObject#getData()
    */
   @Override
   public Object[] getData() throws Exception {
      return new Object[] {rawStringValue, dataStore.getLocator()};
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.providers.IDataAccessObject#loadData(java.lang.Object[])
    */
   @Override
   public void loadData(Object... objects) throws Exception {
      if (objects != null && objects.length > 2) {
         storeValue((String) objects[0]);
         dataStore.setLocator((String) objects[1]);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.providers.IDataAccessObject#persist()
    */
   @Override
   public void persist() throws Exception {
      dataStore.persist();
   }
}
