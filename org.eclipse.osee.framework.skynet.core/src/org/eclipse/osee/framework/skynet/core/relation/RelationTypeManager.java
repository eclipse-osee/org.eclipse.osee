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
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeTypeDoesNotExist;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.OseeTypeManager;

/**
 * @author Ryan D. Brooks
 * @author Andrew M. Finkbeiner
 */
public class RelationTypeManager {

   private RelationTypeManager() {
   }

   private static synchronized void ensurePopulated() throws OseeCoreException {
      OseeTypeManager.getCache().ensureRelationTypePopulated();
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
      if (artifactType.isOfType(allowedType)) {
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
      ensurePopulated();
      return OseeTypeManager.getCache().getAllRelationTypes();
   }

   public static RelationType getType(int relationTypeId) throws OseeCoreException {
      ensurePopulated();
      RelationType relationType = OseeTypeManager.getCache().getRelationTypeById(relationTypeId);
      if (relationType == null) {
         throw new OseeTypeDoesNotExist("The relation with type id[" + relationTypeId + "] does not exist");
      }
      return relationType;
   }

   public static RelationType getType(String typeName) throws OseeCoreException {
      ensurePopulated();
      RelationType relationType = OseeTypeManager.getCache().getRelationTypeByName(typeName);
      if (relationType == null) {
         throw new OseeTypeDoesNotExist("The relation type [" + typeName + "] does not exist");
      }
      return relationType;
   }

   public static boolean typeExists(String name) throws OseeCoreException {
      ensurePopulated();
      return OseeTypeManager.getCache().getRelationTypeByName(name) != null;
   }

   /**
    * Persist a new relation link type. If the <code>relationTypeName</code> is already in the database, then nothing is
    * done.
    * 
    * @param relationTypeName The type name of the relation link to define.
    * @param sideAName The name for the 'a' side of the relation.
    * @param sideBName The name for the 'b' side of the relation.
    * @param abPhrasing The phrasing appropriate from the 'a' side to the 'b' side.
    * @param baPhrasing The phrasing appropriate from the 'b' side to the 'a' side.
    * @param shortName An abbreviated name to display for the link type.
    * @throws OseeCoreException
    */
   public static RelationType createRelationType(String guid, String relationTypeName, String sideAName, String sideBName, String artifactTypeSideA, String artifactTypeSideB, String multiplicity, boolean isUserOrdered, String orderTypeGuid) throws OseeCoreException {
      RelationType relationType;
      if (!typeExists(relationTypeName)) {
         relationType = null;
         //               OseeTypeManager.getTypeFactory().createRelationType(guid, relationTypeName, sideAName, sideBName,
         //                     artifactTypeSideA, artifactTypeSideB, multiplicity, isUserOrdered, orderTypeGuid);
         OseeTypeManager.getDataTypeAccessor().storeRelationType(relationType);
         OseeTypeManager.getCache().cacheRelationType(relationType);
      } else {
         // TODO: Check if anything valuable is different and update it
         relationType = getType(relationTypeName);
      }
      return relationType;
   }
}
