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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.AttributeType;
import org.eclipse.osee.framework.core.model.AttributeTypeFactory;

/**
 * @author Roberto E. Escobar
 */
public class AttributeTypeCacheUpdateResponse {

   private final List<AttributeType> rows;
   private final Map<Integer, Integer> attrToEnum;

   public AttributeTypeCacheUpdateResponse(List<AttributeType> rows, Map<Integer, Integer> attrToEnum) {
      this.rows = rows;
      this.attrToEnum = attrToEnum;
   }

   public List<AttributeType> getAttrTypeRows() {
      return rows;
   }

   public Map<Integer, Integer> getAttrToEnums() {
      return attrToEnum;
   }

   public static AttributeTypeCacheUpdateResponse fromCache(AttributeTypeFactory factory, Collection<AttributeType> types) throws OseeCoreException {
      List<AttributeType> rows = new ArrayList<AttributeType>();
      Map<Integer, Integer> attrToEnum = new HashMap<Integer, Integer>();
      for (AttributeType item : types) {
         AttributeType type =
               factory.create(item.getGuid(), item.getName(), item.getBaseAttributeTypeId(),
                     item.getAttributeProviderId(), item.getFileTypeExtension(), item.getDefaultValue(),
                     item.getMinOccurrences(), item.getMaxOccurrences(), item.getDescription(), item.getTaggerId());
         type.setId(item.getId());
         type.setModificationType(item.getModificationType());
         rows.add(type);

         if (item.getOseeEnumType() != null) {
            attrToEnum.put(item.getId(), item.getOseeEnumTypeId());
         }
      }
      return new AttributeTypeCacheUpdateResponse(rows, attrToEnum);
   }
}
