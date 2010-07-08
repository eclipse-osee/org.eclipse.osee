/*
 * Created on Jul 7, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.core.model.access;

import java.util.Collection;
import java.util.HashSet;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.model.IBasicArtifact;

/**
 * @author Jeff C. Phillips
 * @author Roberto E. Escobar
 */
public final class ArtifactTypeFilter implements IAcceptFilter<IArtifactType> {
   private final PermissionEnum toMatch;
   private final Collection<IArtifactType> itemsToCheck;

   public ArtifactTypeFilter(PermissionEnum toMatch, IArtifactType... itemsToCheck) {
      this.toMatch = toMatch;
      this.itemsToCheck = new HashSet<IArtifactType>();
      if (itemsToCheck != null) {
         for (IArtifactType type : itemsToCheck) {
            this.itemsToCheck.add(type);
         }
      }
   }

   @Override
   public boolean accept(IArtifactType item, IBasicArtifact<?> artifact, PermissionEnum permission) {
      boolean result = false;
      if (itemsToCheck != null && itemsToCheck.contains(item)) {
         result = permission.matches(toMatch);
      }
      return result;
   }

   @Override
   public IArtifactType getObject(Object object) {
      IArtifactType toReturn = null;
      if (object instanceof IArtifactType) {
         toReturn = (IArtifactType) object;
      }
      return toReturn;
   }
}
