/*
 * Created on Jul 7, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.core.model.access;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.model.IBasicArtifact;
import org.eclipse.osee.framework.jdk.core.type.Pair;

public class AttributeTypeFilter implements IAcceptFilter<IAttributeType> {
   private final PermissionEnum toMatch;
   private final Map<IBasicArtifact<?>, IAttributeType> itemsToCheck;

   public AttributeTypeFilter(PermissionEnum toMatch, Collection<Pair<IBasicArtifact<?>, IAttributeType>> items) {
      this.toMatch = toMatch;
      this.itemsToCheck = new HashMap<IBasicArtifact<?>, IAttributeType>();

      for (Pair<IBasicArtifact<?>, IAttributeType> pair : items) {
         itemsToCheck.put(pair.getFirst(), pair.getSecond());
      }
   }

   @Override
   public IAttributeType getObject(Object object) {
      IAttributeType toReturn = null;
      if (object instanceof IAttributeType) {
         toReturn = (IAttributeType) object;
      }
      return toReturn;
   }

   @Override
   public boolean accept(IAttributeType item, IBasicArtifact<?> artifact, PermissionEnum permission) {
      boolean result = false;

      if (itemsToCheck.containsKey(artifact)) {
         result = permission.matches(toMatch);
      }
      return result;
   }

}
