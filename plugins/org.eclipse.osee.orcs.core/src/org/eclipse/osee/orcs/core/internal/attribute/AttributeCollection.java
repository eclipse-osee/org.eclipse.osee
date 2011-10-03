/*
 * Created on Oct 3, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.orcs.core.internal.attribute;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.util.Collections;

public class AttributeCollection {

   private final HashCollection<IAttributeType, Attribute<?>> attributes =
      new HashCollection<IAttributeType, Attribute<?>>(false, LinkedList.class, 12);

   public void add(IAttributeType type, Attribute<?> attribute) {
      attributes.put(type, attribute);
   }

   public Collection<IAttributeType> keySet() {
      return attributes.keySet();
   }

   private List<Attribute<?>> getAttributesByModificationType(Set<ModificationType> allowedModTypes) throws OseeCoreException {
      return filterByModificationType(attributes.getValues(), allowedModTypes);
   }

   private List<Attribute<?>> getAttributesByModificationType(IAttributeType attributeType, Set<ModificationType> allowedModTypes) throws OseeCoreException {
      return filterByModificationType(attributes.getValues(attributeType), allowedModTypes);
   }

   private static List<Attribute<?>> filterByModificationType(Collection<Attribute<?>> attributes, Set<ModificationType> allowedModTypes) {
      List<Attribute<?>> filteredList = new ArrayList<Attribute<?>>();
      if (allowedModTypes != null && !allowedModTypes.isEmpty() && attributes != null && !attributes.isEmpty()) {
         for (Attribute<?> attribute : attributes) {
            if (allowedModTypes.contains(attribute.getModificationType())) {
               filteredList.add(attribute);
            }
         }
      }
      return filteredList;
   }

   public <T> List<Attribute<T>> getCurrentAttributesFor(IAttributeType type) throws OseeCoreException {
      return Collections.castAll(getAttributesByModificationType(type, ModificationType.getCurrentModTypes()));
   }

}
