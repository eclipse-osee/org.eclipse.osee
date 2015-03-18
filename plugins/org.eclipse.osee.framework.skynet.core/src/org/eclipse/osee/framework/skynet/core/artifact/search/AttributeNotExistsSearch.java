/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.artifact.search;

import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * @author John Misinco
 */
public class AttributeNotExistsSearch implements ISearchPrimitive {
   private static final String TOKEN = ";";
   private final IAttributeType attributeType;

   public AttributeNotExistsSearch(IAttributeType attributeType) {
      Conditions.checkNotNull(attributeType, "attributeType");
      this.attributeType = attributeType;
   }

   @Override
   public String toString() {
      return "Attribute Not Exists type: \"" + attributeType + "\"";
   }

   @Override
   public String getStorageString() {
      return "DNE" + TOKEN + attributeType.getGuid().toString();
   }

   public static AttributeNotExistsSearch getPrimitive(String storageString) {
      String[] values = storageString.split(TOKEN);
      if (values.length < 2) {
         throw new IllegalStateException("Value for " + InRelationSearch.class.getSimpleName() + " not parsable");
      }
      IAttributeType type = TokenFactory.createAttributeType(Long.valueOf(values[1]), "SearchAttrType");
      return new AttributeNotExistsSearch(type);
   }

   @Override
   public void addToQuery(QueryBuilderArtifact builder) {
      builder.andNotExists(attributeType);
   }

}
