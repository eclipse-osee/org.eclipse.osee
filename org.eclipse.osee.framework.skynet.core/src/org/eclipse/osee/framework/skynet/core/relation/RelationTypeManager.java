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
package org.eclipse.osee.framework.skynet.core.relation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.cache.AbstractOseeCache;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeTypeDoesNotExist;
import org.eclipse.osee.framework.core.model.ArtifactType;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.RelationType;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * @author Ryan D. Brooks
 * @author Andrew M. Finkbeiner
 */
public class RelationTypeManager {

   private RelationTypeManager() {
   }

   public static AbstractOseeCache<RelationType> getCache() {
      return Activator.getInstance().getOseeCacheService().getRelationTypeCache();
   }

   public static List<RelationType> getValidTypes(ArtifactType artifactType, Branch branch) throws OseeCoreException {
      Collection<RelationType> relationTypes = getAllTypes();
      List<RelationType> validRelationTypes = new ArrayList<RelationType>();
      for (RelationType relationType : relationTypes) {
         int sideAMax = getRelationSideMax(relationType, artifactType, RelationSide.SIDE_A);
         int sideBMax = getRelationSideMax(relationType, artifactType, RelationSide.SIDE_B);
         boolean onSideA = sideBMax > 0;
         boolean onSideB = sideAMax > 0;
         if (onSideA || onSideB) {
            validRelationTypes.add(relationType);
         }
      }
      return validRelationTypes;
   }

   public static int getRelationSideMax(RelationType relationType, ArtifactType artifactType, RelationSide relationSide) throws OseeCoreException {
      int toReturn = 0;
      if (relationType.isArtifactTypeAllowed(relationSide, artifactType)) {
         toReturn = relationType.getMultiplicity().getLimit(relationSide);
      }
      return toReturn;
   }

   /**
    * @param branch
    * @return all the relation types that are valid for the given branch
    * @throws OseeCoreException
    */
   public static Collection<RelationType> getValidTypes(Branch branch) throws OseeCoreException {
      return getAllTypes();
   }

   /**
    * @return all Relation types
    * @throws OseeCoreException
    */
   public static Collection<RelationType> getAllTypes() throws OseeCoreException {
      return getCache().getAll();
   }

   public static RelationType getType(int relationTypeId) throws OseeCoreException {
      RelationType relationType = getCache().getById(relationTypeId);
      if (relationType == null) {
         throw new OseeTypeDoesNotExist("The relation with type id[" + relationTypeId + "] does not exist");
      }
      return relationType;
   }

   public static RelationType getByGuid(String guid) throws OseeCoreException {
      RelationType relationType = getCache().getByGuid(guid);
      if (relationType == null) {
         throw new OseeTypeDoesNotExist("The relation with type guid [" + guid + "] does not exist");
      }
      return relationType;
   }

   public static RelationType getType(IRelationType relationType) throws OseeCoreException {
      return getCache().get(relationType);
   }

   public static RelationType getType(String typeName) throws OseeCoreException {
      RelationType relationType = getCache().getUniqueByName(typeName);
      if (relationType == null) {
         throw new OseeTypeDoesNotExist("The relation type [" + typeName + "] does not exist");
      }
      return relationType;
   }

   public static boolean typeExists(String name) throws OseeCoreException {
      return !getCache().getByName(name).isEmpty();
   }

   public static void persist() throws OseeCoreException {
      getCache().storeAllModified();
   }
}
