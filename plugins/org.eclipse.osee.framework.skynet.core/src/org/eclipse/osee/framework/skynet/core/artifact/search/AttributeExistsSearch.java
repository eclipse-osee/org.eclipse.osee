/*******************************************************************************
 * Copyright (c) 2014 Boeing.
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
public class AttributeExistsSearch implements ISearchPrimitive {
   private final IAttributeType attributeType;

   public AttributeExistsSearch(IAttributeType attributeType) {
      Conditions.checkNotNull(attributeType, "attributeType");
      this.attributeType = attributeType;
   }

   @Override
   public String toString() {
      return "Attribute type: \"" + attributeType + "\"";
   }

   @Override
   public String getStorageString() {
      return attributeType.getGuid().toString();
   }

   public static AttributeExistsSearch getPrimitive(String storageString) {
      IAttributeType type = TokenFactory.createAttributeType(Long.valueOf(storageString), "SearchAttrType");
      return new AttributeExistsSearch(type);
   }

   @Override
   public void addToQuery(QueryBuilderArtifact builder) {
      builder.andExists(attributeType);
   }

}
