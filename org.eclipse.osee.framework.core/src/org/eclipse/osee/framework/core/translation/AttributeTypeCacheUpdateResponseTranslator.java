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
package org.eclipse.osee.framework.core.translation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.data.AttributeTypeCacheUpdateResponse;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.AttributeType;
import org.eclipse.osee.framework.core.model.AttributeTypeFactory;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryServiceProvider;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;

/**
 * @author Roberto E. Escobar
 */
public class AttributeTypeCacheUpdateResponseTranslator implements ITranslator<AttributeTypeCacheUpdateResponse> {

   private enum Fields {
      ROW_COUNT,
      ROW,
      ATTR_TO_ENUM;
   }

   private final IOseeModelFactoryServiceProvider provider;

   public AttributeTypeCacheUpdateResponseTranslator(IOseeModelFactoryServiceProvider provider) {
      this.provider = provider;
   }

   private AttributeTypeFactory getFactory() throws OseeCoreException {
      return provider.getOseeFactoryService().getAttributeTypeFactory();
   }

   @Override
   public AttributeTypeCacheUpdateResponse convert(PropertyStore store) throws OseeCoreException {
      List<AttributeType> rows = new ArrayList<AttributeType>();
      int rowCount = store.getInt(Fields.ROW_COUNT.name());
      AttributeTypeFactory factory = getFactory();
      for (int index = 0; index < rowCount; index++) {
         String[] rowData = store.getArray(createKey(Fields.ROW, index));
         rows.add(createfromArray(factory, rowData));
      }
      Map<Integer, Integer> attrToEnum = new HashMap<Integer, Integer>();
      TranslationUtil.loadMap(attrToEnum, store, Fields.ATTR_TO_ENUM);
      return new AttributeTypeCacheUpdateResponse(rows, attrToEnum);
   }

   @Override
   public PropertyStore convert(AttributeTypeCacheUpdateResponse object) throws OseeCoreException {
      PropertyStore store = new PropertyStore();
      List<AttributeType> rows = object.getAttrTypeRows();
      for (int index = 0; index < rows.size(); index++) {
         AttributeType row = rows.get(index);
         store.put(createKey(Fields.ROW, index), toArray(row));
      }
      store.put(Fields.ROW_COUNT.name(), rows.size());

      TranslationUtil.putMap(store, Fields.ATTR_TO_ENUM, object.getAttrToEnums());
      return store;
   }

   private String createKey(Fields key, int index) {
      return String.format("%s_%s", key.name(), index);
   }

   private String[] toArray(AttributeType type) {
      return new String[] {type.getAttributeProviderId(), type.getBaseAttributeTypeId(), type.getDefaultValue(),
            type.getDescription(), type.getFileTypeExtension(), type.getGuid(), String.valueOf(type.getId()),
            String.valueOf(type.getMaxOccurrences()), String.valueOf(type.getMinOccurrences()),
            type.getModificationType().name(), type.getName(), type.getTaggerId()};
   }

   private AttributeType createfromArray(AttributeTypeFactory factory, String[] data) throws OseeCoreException {
      String attributeProviderId = data[0];
      String baseAttributeTypeId = data[1];
      String defaultValue = data[2];
      String description = data[3];
      String fileTypeExtension = data[4];
      String guid = data[5];
      int uniqueId = Integer.valueOf(data[6]);
      int maxOccurrences = Integer.valueOf(data[7]);
      int minOccurrences = Integer.valueOf(data[8]);
      ModificationType modType = ModificationType.valueOf(data[9]);
      String name = data[10];
      String taggerId = data[11];

      AttributeType type =
            factory.create(guid, name, baseAttributeTypeId, attributeProviderId, fileTypeExtension, defaultValue,
                  minOccurrences, maxOccurrences, description, taggerId);
      type.setId(uniqueId);
      type.setModificationType(modType);
      return type;
   }
}
