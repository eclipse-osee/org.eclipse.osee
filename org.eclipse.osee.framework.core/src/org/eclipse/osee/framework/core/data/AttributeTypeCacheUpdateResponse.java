/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.data;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.cache.IOseeCache;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.AttributeType;

/**
 * @author Roberto E. Escobar
 */
public class AttributeTypeCacheUpdateResponse {

   private final List<AttributeTypeRow> rows;

   public AttributeTypeCacheUpdateResponse(List<AttributeTypeRow> rows) {
      this.rows = rows;
   }

   public List<AttributeTypeRow> getAttrTypeRows() {
      return rows;
   }

   public static final class AttributeTypeRow {
      private final int id;
      private final String name;
      private final String guid;
      private final ModificationType modType;
      private final int max;
      private final int min;
      private final String defaultValue;
      private final String description;
      private final String fileType;
      private final String provider;
      private final String baseType;
      private final String taggerId;
      private final int enumId;

      public AttributeTypeRow(int id, String name, String guid, ModificationType modType, int max, int min, String defaultValue, String description, String fileType, String provider, String baseType, String taggerId, int enumId) {
         super();
         this.id = id;
         this.name = name;
         this.guid = guid;
         this.modType = modType;
         this.max = max;
         this.min = min;
         this.defaultValue = defaultValue;
         this.description = description;
         this.fileType = fileType;
         this.provider = provider;
         this.baseType = baseType;
         this.taggerId = taggerId;
         this.enumId = enumId;
      }

      public int getId() {
         return id;
      }

      public String getName() {
         return name;
      }

      public String getGuid() {
         return guid;
      }

      public ModificationType getModType() {
         return modType;
      }

      public int getMax() {
         return max;
      }

      public int getMin() {
         return min;
      }

      public String getDefaultValue() {
         return defaultValue;
      }

      public String getDescription() {
         return description;
      }

      public String getFileType() {
         return fileType;
      }

      public String getProvider() {
         return provider;
      }

      public String getBaseType() {
         return baseType;
      }

      public String getTaggerId() {
         return taggerId;
      }

      public int getEnumId() {
         return enumId;
      }

      public String[] toArray() {
         return new String[] {String.valueOf(getId()), getGuid(), getName(), getModType().name()};
      }

      public static AttributeTypeRow fromArray(String[] data) {
         int id = Integer.valueOf(data[0]);
         String guid = data[1];
         String name = data[2];
         ModificationType modType = ModificationType.valueOf(data[3]);
         return new AttributeTypeRow(id, guid, name, modType);
      }
   }

   public static AttributeTypeCacheUpdateResponse fromCache(IOseeCache<AttributeType> cache) throws OseeCoreException {
      List<AttributeTypeRow> rows = new ArrayList<AttributeTypeRow>();
      for (AttributeType item : cache.getAll()) {
         item.getAttributeProviderId();
         item.getBaseAttributeTypeId();
         item.getDefaultValue();
         item.getDescription();
         item.getFileTypeExtension();
         item.getMaxOccurrences();
         item.getMinOccurrences();
         item.getTaggerId();
         item.getOseeEnumTypeId();

         rows.add(new AttributeTypeRow(item.getId(), item.getGuid(), item.getName(), item.getModificationType()));
      }
      return new AttributeTypeCacheUpdateResponse(rows);
   }
}
