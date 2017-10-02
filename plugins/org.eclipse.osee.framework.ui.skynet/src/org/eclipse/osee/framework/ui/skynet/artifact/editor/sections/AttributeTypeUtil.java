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
package org.eclipse.osee.framework.ui.skynet.artifact.editor.sections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;

/**
 * @author Roberto E. Escobar
 */
public class AttributeTypeUtil {

   public static List<AttributeTypeToken> getEmptyTypes(Artifact artifact)  {
      List<AttributeTypeToken> items = new ArrayList<>();
      for (AttributeTypeToken type : artifact.getAttributeTypes()) {
         if (CoreAttributeTypes.Name.notEqual(type) && artifact.getAttributes(type).isEmpty()) {
            items.add(type);
         }
      }
      Collections.sort(items);
      return items;
   }

   private static Set<AttributeType> toTypes(List<Attribute<?>> attributes) {
      Set<AttributeType> types = new HashSet<>();
      for (Attribute<?> attribute : attributes) {
         types.add(attribute.getAttributeType());
      }
      return types;
   }

   public static List<AttributeTypeToken> getTypesWithData(Artifact artifact)  {
      List<AttributeTypeToken> items = new ArrayList<>();

      List<Attribute<?>> attributeInstances = artifact.getAttributes(artifact.isDeleted());
      Set<AttributeType> typesInExistence = toTypes(attributeInstances);

      AttributeType nameType = null;
      AttributeType annotations = null;
      AttributeType relationOrder = null;
      AttributeType dslEditableAttribute = null;

      for (AttributeType type : typesInExistence) {
         if (CoreAttributeTypes.Name.equals(type)) {
            nameType = type;
         } else if (CoreAttributeTypes.Annotation.equals(type)) {
            annotations = type;
         } else if (CoreAttributeTypes.RelationOrder.equals(type)) {
            relationOrder = type;
         } else if (type.hasMediaType() && type.getMediaType().endsWith("dsl")) {
            dslEditableAttribute = type;
         } else {
            items.add(type);
         }
      }
      Collections.sort(items);
      if (nameType != null) {
         items.add(0, nameType);
      }
      if (annotations != null) {
         items.add(annotations);
      }
      if (relationOrder != null) {
         items.add(relationOrder);
      }
      if (dslEditableAttribute != null) {
         items.add(dslEditableAttribute);
      }
      return items;
   }
}