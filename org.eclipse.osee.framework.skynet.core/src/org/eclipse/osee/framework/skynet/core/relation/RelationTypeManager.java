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
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeTypeDoesNotExist;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.OseeTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.OseeTypeCache.OseeTypeCacheData;

/**
 * @author Ryan D. Brooks
 * @author Andrew M. Finkbeiner
 */
public class RelationTypeManager {

   private RelationTypeManager() {
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
      ArtifactType allowedType = relationType.getArtifactType(relationSide);
      if (artifactType.inheritsFrom(allowedType)) {
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
      return OseeTypeManager.getCache().getRelationTypeData().getAllTypes();
   }

   public static RelationType getType(int relationTypeId) throws OseeCoreException {
      RelationType relationType = OseeTypeManager.getCache().getRelationTypeData().getTypeById(relationTypeId);
      if (relationType == null) {
         throw new OseeTypeDoesNotExist("The relation with type id[" + relationTypeId + "] does not exist");
      }
      return relationType;
   }

   public static RelationType getType(String typeName) throws OseeCoreException {
      RelationType relationType = OseeTypeManager.getCache().getRelationTypeData().getTypeByName(typeName);
      if (relationType == null) {
         throw new OseeTypeDoesNotExist("The relation type [" + typeName + "] does not exist");
      }
      return relationType;
   }

   public static boolean typeExists(String name) throws OseeCoreException {
      return OseeTypeManager.getCache().getRelationTypeData().getTypeByName(name) != null;
   }

   public static RelationType createRelationType(String guid, String typeName, String sideAName, String sideBName, ArtifactType artifactTypeSideA, ArtifactType artifactTypeSideB, RelationTypeMultiplicity multiplicity, boolean isUserOrdered, String defaultOrderTypeGuid) throws OseeCoreException {
      OseeTypeCacheData<RelationType> dataCache = OseeTypeManager.getCache().getRelationTypeData();
      RelationType relationType = dataCache.getTypeByGuid(guid);
      if (relationType == null) {
         relationType =
               OseeTypeManager.getTypeFactory().createRelationType(guid, typeName, sideAName, sideBName,
                     artifactTypeSideA, artifactTypeSideB, multiplicity, isUserOrdered, defaultOrderTypeGuid);
      } else {
         // UPDATE VALUES HERE
      }
      dataCache.cacheType(relationType);
      dataCache.storeAllModified();
      return relationType;
   }
}
