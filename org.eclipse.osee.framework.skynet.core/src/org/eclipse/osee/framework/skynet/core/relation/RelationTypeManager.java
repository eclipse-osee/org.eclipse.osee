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
import java.util.logging.Level;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.exception.OseeTypeDoesNotExist;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.ConnectionHandlerStatement;
import org.eclipse.osee.framework.database.core.SequenceManager;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.OseeTypeCache;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * @author Ryan D. Brooks
 * @author Andrew M. Finkbeiner
 */
public class RelationTypeManager {
   private static final String SELECT_LINK_TYPES = "SELECT * FROM osee_relation_link_type";
   private static final String INSERT_RELATION_LINK_TYPE =
         "INSERT INTO osee_relation_link_type (rel_link_type_id, type_name, a_name, b_name, a_art_type_id, b_art_type_id, multiplicity, user_ordered, default_order_type_guid) VALUES (?,?,?,?,?,?,?,?,?)";

   private static final RelationTypeManager instance = new RelationTypeManager();
   private final OseeTypeCache oseeTypeCache;

   private RelationTypeManager() {
   }

   public static List<RelationType> getValidTypes(ArtifactType artifactType, Branch branch) throws OseeCoreException {
      Collection<RelationType> relationTypes = instance.idToTypeMap.values();
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
    * @throws OseeDataStoreException
    * @throws OseeTypeDoesNotExist
    */
   public static List<RelationType> getValidTypes(Branch branch) throws OseeDataStoreException, OseeTypeDoesNotExist {
      return getAllTypes();
   }

   /**
    * @return all Relation types
    * @throws OseeDataStoreException
    * @throws OseeTypeDoesNotExist
    */
   public static List<RelationType> getAllTypes() throws OseeDataStoreException, OseeTypeDoesNotExist {
      ensurePopulated();
      return new ArrayList<RelationType>(instance.idToTypeMap.values());
   }

   public static RelationType getType(int relationTypeId) throws OseeTypeDoesNotExist, OseeDataStoreException {
      ensurePopulated();
      RelationType relationType = instance.idToTypeMap.get(relationTypeId);
      if (relationType == null) {
         throw new OseeTypeDoesNotExist("The relation with type id[" + relationTypeId + "] does not exist");
      }
      return relationType;
   }

   public static RelationType getType(String typeName) throws OseeTypeDoesNotExist, OseeDataStoreException {
      ensurePopulated();
      RelationType relationType = instance.nameToTypeMap.get(typeName);
      if (relationType == null) {
         throw new OseeTypeDoesNotExist("The relation type [" + typeName + "] does not exist");
      }
      return relationType;
   }

   public static boolean typeExists(String name) throws OseeDataStoreException, OseeTypeDoesNotExist {
      ensurePopulated();
      return instance.nameToTypeMap.get(name) != null;
   }

   private void cache(RelationType relationType) {
      nameToTypeMap.put(relationType.getTypeName(), relationType);
      idToTypeMap.put(relationType.getRelationTypeId(), relationType);
   }

   public void refreshCache() throws OseeDataStoreException, OseeTypeDoesNotExist {
      nameToTypeMap.clear();
      idToTypeMap.clear();
      populateCache();
   }

   private static synchronized void ensurePopulated() throws OseeDataStoreException, OseeTypeDoesNotExist {
      if (instance.idToTypeMap.isEmpty()) {
         instance.populateCache();
      }
   }

   private void populateCache() throws OseeDataStoreException, OseeTypeDoesNotExist {
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();

      try {
         chStmt.runPreparedQuery(SELECT_LINK_TYPES);

         while (chStmt.next()) {
            try {
               String relationTypeName = chStmt.getString("type_name");
               int relationTypeId = chStmt.getInt("rel_link_type_id");
               ArtifactType artifactTypeSideA = ArtifactTypeManager.getType(chStmt.getInt("a_art_type_id"));
               ArtifactType artifactTypeSideB = ArtifactTypeManager.getType(chStmt.getInt("b_art_type_id"));
               RelationTypeMultiplicity multiplicity =
                     RelationTypeMultiplicity.getRelationMultiplicity(chStmt.getInt("multiplicity"));
               if (multiplicity == null) {
                  throw new OseeCoreException(String.format("Multiplicity was null for [%s][%s]", relationTypeName,
                        relationTypeId));
               }
               RelationType relationType =
                     new RelationType(chStmt.getInt("rel_link_type_id"), relationTypeName, chStmt.getString("a_name"),
                           chStmt.getString("b_name"), artifactTypeSideA, artifactTypeSideB, multiplicity,
                           chStmt.getString("user_ordered"), chStmt.getString("default_order_type_guid"));
               cache(relationType);
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      } finally {
         chStmt.close();
      }
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
   public static RelationType createRelationType(String relationTypeName, String sideAName, String sideBName, String artifactTypeSideA, String artifactTypeSideB, String multiplicity, String ordered, String orderTypeGuid) throws OseeCoreException {
      if (typeExists(relationTypeName)) {
         return getType(relationTypeName);
      }
      if (!Strings.isValid(relationTypeName)) {
         throw new IllegalArgumentException("The relationName can not be null or empty");
      }
      if (!Strings.isValid(sideAName)) {
         throw new IllegalArgumentException("The sideAName can not be null or empty");
      }
      if (!Strings.isValid(sideBName)) {
         throw new IllegalArgumentException("The sideBName can not be null or empty");
      }
      if (!Strings.isValid(artifactTypeSideA)) {
         throw new IllegalArgumentException("The artifactTypeSideA can not be null or empty");
      }

      if (!Strings.isValid(artifactTypeSideB)) {
         throw new IllegalArgumentException("The artifactTypeSideB can not be null or empty");
      }

      RelationTypeMultiplicity multiplicityEnum = RelationTypeMultiplicity.getFromString(multiplicity);
      if (multiplicityEnum == null) {
         throw new IllegalArgumentException("The multiplicity can not be null or empty");
      }
      ArtifactType artTypeIdA = ArtifactTypeManager.getType(artifactTypeSideA);
      ArtifactType artTypeIdB = ArtifactTypeManager.getType(artifactTypeSideB);

      int relationTypeId = SequenceManager.getNextRelationTypeId();

      ConnectionHandler.runPreparedUpdate(INSERT_RELATION_LINK_TYPE, relationTypeId, relationTypeName, sideAName,
            sideBName, artTypeIdA.getArtTypeId(), artTypeIdB.getArtTypeId(), multiplicityEnum.getValue(), ordered,
            orderTypeGuid);

      RelationType relationType =
            new RelationType(relationTypeId, relationTypeName, sideAName, sideBName, artTypeIdA, artTypeIdB,
                  multiplicityEnum, ordered, orderTypeGuid);
      instance.cache(relationType);
      return relationType;
   }
}
