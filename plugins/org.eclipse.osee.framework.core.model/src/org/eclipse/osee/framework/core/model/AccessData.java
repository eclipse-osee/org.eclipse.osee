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
package org.eclipse.osee.framework.core.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.model.access.ArtifactTypeFilter;
import org.eclipse.osee.framework.core.model.access.AttributeTypeFilter;
import org.eclipse.osee.framework.core.model.access.IAcceptFilter;
import org.eclipse.osee.framework.jdk.core.type.DoubleKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.Pair;

/**
 * @author Jeff C. Phillips
 * @author Roberto E. Escobar
 */
public class AccessData {

   private final DoubleKeyHashMap<IBasicArtifact<?>, Object, PermissionEnum> artifactPermissions;

   public AccessData() {
      this.artifactPermissions = new DoubleKeyHashMap<IBasicArtifact<?>, Object, PermissionEnum>();
   }

   public void add(Object object, PermissionEnum permission) {
      artifactPermissions.put(null, object, permission);
   }

   public void add(IBasicArtifact<?> artifact, Object object, PermissionEnum permission) {
      artifactPermissions.put(artifact, object, permission);
   }

   public void merge(AccessData accessData) {
      //Merge all of the common permissions
      for (IBasicArtifact<?> artifact : artifactPermissions.getKeySetOne()) {
         for (Object object : artifactPermissions.getSubHash(artifact).keySet()) {
            if (accessData.artifactPermissions.containsKey(artifact, object)) {
               PermissionEnum mainPermission = artifactPermissions.get(artifact, object);
               PermissionEnum subPermission = accessData.artifactPermissions.get(artifact, object);
               PermissionEnum mergePermission;

               //Deny overrides all permissions
               if (mainPermission == PermissionEnum.DENY || subPermission == PermissionEnum.DENY) {
                  mainPermission = PermissionEnum.DENY;
                  subPermission = PermissionEnum.DENY;
               }
               //Always take the weaker permission
               if (mainPermission.matches(subPermission)) {
                  mergePermission = subPermission;
               } else {
                  mergePermission = mainPermission;
               }
               artifactPermissions.put(artifact, object, mergePermission);
               accessData.artifactPermissions.remove(artifact, object);
            }
         }
      }
      //Add the uncommon permissions to the main accessData
      for (IBasicArtifact<?> artifact : accessData.artifactPermissions.getKeySetOne()) {
         for (Object object : accessData.artifactPermissions.getSubHash(artifact).keySet()) {
            artifactPermissions.put(artifact, object, accessData.artifactPermissions.get(artifact, object));
         }
      }
   }

   public boolean matchesAll(PermissionEnum permissionEnum) {
      boolean matches = false;
      for (PermissionEnum objectPermission : artifactPermissions.allValues()) {
         if (objectPermission.matches(permissionEnum)) {
            matches = true;
         } else {
            matches = false;
            break;
         }
      }
      return matches;
   }

   public Collection<IArtifactType> getArtifactTypeMatches(IBasicArtifact<?> artifact, IArtifactType type, PermissionEnum permissionEnum) {
      IAcceptFilter<IArtifactType> filter = new ArtifactTypeFilter(permissionEnum, type);
      return filter(artifact, filter);
   }

   public Collection<IAttributeType> getAttributeTypeMatches(IBasicArtifact<?> artifact, IAttributeType attributeType, PermissionEnum permissionEnum) {
      List<Pair<IBasicArtifact<?>, IAttributeType>> pairList = new ArrayList<Pair<IBasicArtifact<?>, IAttributeType>>();
      pairList.add(new Pair<IBasicArtifact<?>, IAttributeType>(artifact, attributeType));
      IAcceptFilter<IAttributeType> filter = new AttributeTypeFilter(permissionEnum, pairList);
      return filter(artifact, filter);
   }

   public Collection<IRelationType> getRelationTypeMatches(IBasicArtifact<?> artifact, PermissionEnum permissionEnum) {
      return Collections.emptyList();
   }

   private <T> Collection<T> filter(IBasicArtifact<?> artifact, IAcceptFilter<T> filter) {
      Collection<T> filtered = new ArrayList<T>();
      for (Object object : artifactPermissions.getSubHash(artifact).keySet()) {
         PermissionEnum permission = artifactPermissions.get(artifact, object);

         T toCheck = filter.getObject(object);
         if (toCheck != null) {
            boolean shouldAccept = filter.accept(toCheck, artifact, permission);
            if (shouldAccept) {
               filtered.add(toCheck);
            }
         }
      }
      return filtered;
   }
}
