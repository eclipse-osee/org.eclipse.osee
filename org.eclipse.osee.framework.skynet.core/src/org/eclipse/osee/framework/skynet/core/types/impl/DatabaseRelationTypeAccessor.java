/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.types.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.ConnectionHandlerStatement;
import org.eclipse.osee.framework.database.core.SequenceManager;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.relation.RelationType;
import org.eclipse.osee.framework.skynet.core.types.AbstractOseeCache;
import org.eclipse.osee.framework.skynet.core.types.ArtifactTypeCache;
import org.eclipse.osee.framework.skynet.core.types.IOseeDataAccessor;
import org.eclipse.osee.framework.skynet.core.types.IOseeTypeFactory;

/**
 * @author Roberto E. Escobar
 */
public class DatabaseRelationTypeAccessor implements IOseeDataAccessor<RelationType> {
   private static final String SELECT_LINK_TYPES = "SELECT * FROM osee_relation_link_type";
   private static final String INSERT_RELATION_TYPE =
         "INSERT INTO osee_relation_link_type (rel_link_type_id, rel_link_type_guid, type_name, a_name, b_name, a_art_type_id, b_art_type_id, multiplicity, default_order_type_guid) VALUES (?,?,?,?,?,?,?,?,?)";
   private static final String UPDATE_RELATION_TYPE =
         "update osee_relation_link_type SET type_name=?, a_name=?, b_name=?, a_art_type_id=?, b_art_type_id=?, multiplicity=?, default_order_type_guid=? where rel_link_type_id = ?";

   private final ArtifactTypeCache artifactCache;

   public DatabaseRelationTypeAccessor(ArtifactTypeCache artifactCache) {
      this.artifactCache = artifactCache;
   }

   @Override
   public void load(AbstractOseeCache<RelationType> cache, IOseeTypeFactory factory) throws OseeCoreException {
      artifactCache.ensurePopulated();
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();

      try {
         chStmt.runPreparedQuery(SELECT_LINK_TYPES);

         while (chStmt.next()) {
            String name = chStmt.getString("type_name");
            int typeId = chStmt.getInt("rel_link_type_id");
            int aArtTypeId = chStmt.getInt("a_art_type_id");
            int bArtTypeId = chStmt.getInt("b_art_type_id");
            int multiplicityValue = chStmt.getInt("multiplicity");
            try {
               ArtifactType artifactTypeSideA = artifactCache.getById(aArtTypeId);
               ArtifactType artifactTypeSideB = artifactCache.getById(bArtTypeId);
               RelationTypeMultiplicity multiplicity =
                     RelationTypeMultiplicity.getRelationMultiplicity(multiplicityValue);
               String sideAName = chStmt.getString("a_name");
               String sideBName = chStmt.getString("b_name");
               String defaultOrderTypeGuid = chStmt.getString("default_order_type_guid");

               RelationType relationType = cache.getById(typeId);
               if (relationType == null) {
                  relationType =
                        factory.createRelationType(cache, chStmt.getString("rel_link_type_guid"), name, sideAName,
                              sideBName, artifactTypeSideA, artifactTypeSideB, multiplicity, defaultOrderTypeGuid);
                  relationType.setId(typeId);
                  relationType.setModificationType(ModificationType.MODIFIED);
                  cache.cache(relationType);
               } else {
                  relationType.setFields(name, sideAName, sideBName, artifactTypeSideA, artifactTypeSideB,
                        multiplicity, defaultOrderTypeGuid);
               }
               relationType.clearDirty();
            } catch (OseeCoreException ex) {
               String message =
                     String.format(
                           "Error loading relation type - name:[%s] id:[%s] aArtTypeId:[%s] bArtTypeid:[%s] multiplicity:[%s]",
                           name, typeId, aArtTypeId, bArtTypeId, multiplicityValue);
               OseeLog.log(Activator.class, Level.SEVERE, message, ex);
            }
         }
      } finally {
         chStmt.close();
      }
   }

   @Override
   public void store(AbstractOseeCache<RelationType> cache, Collection<RelationType> relationTypes) throws OseeCoreException {
      List<Object[]> insertData = new ArrayList<Object[]>();
      List<Object[]> updateData = new ArrayList<Object[]>();
      for (RelationType type : relationTypes) {
         if (type.isDirty()) {
            switch (type.getModificationType()) {
               case NEW:
                  type.setId(SequenceManager.getNextRelationTypeId());
                  insertData.add(toInsertValues(type));
                  break;
               case MODIFIED:
                  updateData.add(toUpdateValues(type));
                  break;
               default:
                  break;
            }
         }
      }
      ConnectionHandler.runBatchUpdate(INSERT_RELATION_TYPE, insertData);
      ConnectionHandler.runBatchUpdate(UPDATE_RELATION_TYPE, updateData);

      for (RelationType type : relationTypes) {
         type.clearDirty();
      }
   }

   private Object[] toInsertValues(RelationType type) throws OseeDataStoreException {
      return new Object[] {type.getId(), type.getGuid(), type.getName(), type.getSideAName(), type.getSideBName(),
            type.getArtifactTypeSideA().getId(), type.getArtifactTypeSideB().getId(),
            type.getMultiplicity().getValue(), type.getDefaultOrderTypeGuid()};
   }

   private Object[] toUpdateValues(RelationType type) throws OseeDataStoreException {
      return new Object[] {type.getName(), type.getSideAName(), type.getSideBName(),
            type.getArtifactTypeSideA().getId(), type.getArtifactTypeSideB().getId(),
            type.getMultiplicity().getValue(), type.getDefaultOrderTypeGuid(), type.getId()};
   }
}
