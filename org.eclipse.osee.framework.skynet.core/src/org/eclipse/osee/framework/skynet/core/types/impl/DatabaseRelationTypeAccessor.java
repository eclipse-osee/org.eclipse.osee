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
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.relation.RelationType;
import org.eclipse.osee.framework.skynet.core.types.IOseeTypeDataAccessor;
import org.eclipse.osee.framework.skynet.core.types.IOseeTypeFactory;
import org.eclipse.osee.framework.skynet.core.types.OseeTypeCache;

/**
 * @author Roberto E. Escobar
 */
public class DatabaseRelationTypeAccessor implements IOseeTypeDataAccessor<RelationType> {
   private static final String SELECT_LINK_TYPES = "SELECT * FROM osee_relation_link_type";
   private static final String INSERT_RELATION_TYPE =
         "INSERT INTO osee_relation_link_type (rel_link_type_id, rel_link_type_guid, type_name, a_name, b_name, a_art_type_id, b_art_type_id, multiplicity, user_ordered, default_order_type_guid) VALUES (?,?,?,?,?,?,?,?,?,?)";
   private static final String UPDATE_RELATION_TYPE =
         "update osee_relation_link_type SET type_name=?, a_name=?, b_name=?, a_art_type_id=?, b_art_type_id=?, multiplicity=?, default_order_type_guid=? where rel_link_type_id = ?";

   private static final String USER_ORDERED = "Yes";
   private static final String NOT_USER_ORDERED = "No";

   @Override
   public void load(OseeTypeCache cache, IOseeTypeFactory factory) throws OseeCoreException {
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
               if (multiplicity != null) {
                  boolean isUserOrdered = USER_ORDERED.equalsIgnoreCase(chStmt.getString("user_ordered"));
                  RelationType relationType =
                        factory.createRelationType(chStmt.getString("rel_link_type_guid"), relationTypeName,
                              chStmt.getString("a_name"), chStmt.getString("b_name"), artifactTypeSideA,
                              artifactTypeSideB, multiplicity, isUserOrdered,
                              chStmt.getString("default_order_type_guid"));
                  relationType.setTypeId(chStmt.getInt("rel_link_type_id"));
                  relationType.setModificationType(ModificationType.MODIFIED);
                  cache.getRelationTypeCache().cacheType(relationType);
               } else {
                  OseeLog.log(Activator.class, Level.SEVERE, String.format("Multiplicity was null for [%s][%s]",
                        relationTypeName, relationTypeId));
               }
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      } finally {
         chStmt.close();
      }
   }

   @Override
   public void store(OseeTypeCache cache, Collection<RelationType> relationTypes) throws OseeCoreException {
      List<Object[]> insertData = new ArrayList<Object[]>();
      List<Object[]> updateData = new ArrayList<Object[]>();
      for (RelationType type : relationTypes) {
         switch (type.getModificationType()) {
            case NEW:
               type.setTypeId(SequenceManager.getNextRelationTypeId());
               insertData.add(toInsertValues(type));
               break;
            case MODIFIED:
               updateData.add(toUpdateValues(type));
               break;
            default:
               break;
         }
      }
      ConnectionHandler.runBatchUpdate(INSERT_RELATION_TYPE, insertData);
      ConnectionHandler.runBatchUpdate(UPDATE_RELATION_TYPE, updateData);

      for (RelationType type : relationTypes) {
         type.persist();
      }
   }

   private Object[] toInsertValues(RelationType type) throws OseeDataStoreException {
      return new Object[] {type.getTypeId(), type.getGuid(), type.getName(), type.getSideAName(), type.getSideBName(),
            type.getArtifactTypeSideA().getTypeId(), type.getArtifactTypeSideB().getTypeId(),
            type.getMultiplicity().getValue(), type.isOrdered() ? USER_ORDERED : NOT_USER_ORDERED,
            type.getDefaultOrderTypeGuid()};
   }

   private Object[] toUpdateValues(RelationType type) throws OseeDataStoreException {
      return new Object[] {type.getName(), type.getSideAName(), type.getSideBName(),
            type.getArtifactTypeSideA().getTypeId(), type.getArtifactTypeSideB().getTypeId(),
            type.getMultiplicity().getValue(), type.getDefaultOrderTypeGuid(), type.getTypeId()};
   }
}
