/*
 * Created on Jun 28, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.core.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.enums.PermissionEnum;

public class AccessData {

   private final Map<Object, PermissionEnum> permissions;

   public AccessData() {
      this.permissions = new HashMap<Object, PermissionEnum>();
   }

   public void add(Object object, PermissionEnum permission) {
      permissions.put(object, permission);
   }

   public void merge(AccessData accessData) {
   }

   public boolean matches(PermissionEnum permissionEnum) {
      return false;
   }

   //   public boolean matches(IBasicArtifact<?> artifact, IAttributeType type, PermissionEnum permissionEnum) {
   //      return false;
   //   }

   public Collection<IArtifactType> getArtifactType(IBasicArtifact<?> artifact, PermissionEnum permissionEnum) {
      IArtifactType type = artifact.getArtifactType();
      AcceptFilter<IArtifactType> filter = new ArtifactTypeFilter(permissionEnum, type);
      return filter(filter);
   }

   public Collection<IAttributeType> getAttributeTypeMatches(IBasicArtifact<?> artifact, PermissionEnum permissionEnum) {

      return Collections.emptyList();
   }

   public Collection<IRelationType> getRelationTypeMatches(IBasicArtifact<?> artifact, PermissionEnum permissionEnum) {
      return Collections.emptyList();
   }

   private <T> Collection<T> filter(AcceptFilter<T> filter) {
      Collection<T> filtered = new ArrayList<T>();
      for (Entry<Object, PermissionEnum> entry : permissions.entrySet()) {
         Object object = entry.getKey();
         PermissionEnum permission = entry.getValue();

         T toCheck = (T) object;

         boolean shouldAccept = filter.accept(toCheck, permission);
         if (shouldAccept) {
            filtered.add(toCheck);
         }
      }
      return filtered;
   }

   private static interface AcceptFilter<T> {

      boolean accept(T item, PermissionEnum permission);
   }

   private static final class ArtifactTypeFilter implements AcceptFilter<IArtifactType> {
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
      public boolean accept(IArtifactType item, PermissionEnum permission) {
         boolean result = false;
         if (itemsToCheck != null && itemsToCheck.contains(item)) {
            result = toMatch.matches(permission);
         }
         return result;
      }
   }
}
