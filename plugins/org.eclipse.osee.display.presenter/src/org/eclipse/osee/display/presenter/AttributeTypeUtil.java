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
package org.eclipse.osee.display.presenter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Roberto E. Escobar
 */
public class AttributeTypeUtil {

   public static List<IAttributeType> getEmptyTypes(ArtifactReadable artifact) throws OseeCoreException {
      List<IAttributeType> items = new ArrayList<IAttributeType>();
      for (IAttributeType type : artifact.getExistingAttributeTypes()) {
         if (!CoreAttributeTypes.Name.equals(type) && artifact.getAttributeCount(type) == 0) {
            items.add(type);
         }
      }
      Collections.sort(items);
      return items;
   }

   public static List<IAttributeType> getTypesWithData(ArtifactReadable artifact) throws OseeCoreException {
      List<IAttributeType> items = new ArrayList<IAttributeType>();

      Collection<? extends IAttributeType> typesInExistence = artifact.getExistingAttributeTypes();

      IAttributeType nameType = null;
      IAttributeType annotations = null;
      IAttributeType relationOrder = null;

      for (IAttributeType type : typesInExistence) {
         if (CoreAttributeTypes.Name.equals(type)) {
            nameType = type;
         } else if (CoreAttributeTypes.Annotation.equals(type)) {
            annotations = type;
         } else if (CoreAttributeTypes.RelationOrder.equals(type)) {
            relationOrder = type;
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
         // Skip relation order items
         //         items.add(relationOrder);
      }
      return items;
   }
}