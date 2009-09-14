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
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.ConnectionHandlerStatement;
import org.eclipse.osee.framework.database.core.SequenceManager;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.attribute.OseeEnumEntry;
import org.eclipse.osee.framework.skynet.core.attribute.OseeEnumType;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.types.IOseeTypeDataAccessor;
import org.eclipse.osee.framework.skynet.core.types.IOseeTypeFactory;
import org.eclipse.osee.framework.skynet.core.types.OseeEnumTypeCache;
import org.eclipse.osee.framework.skynet.core.types.OseeTypeCache;

/**
 * @author Roberto E. Escobar
 */
public class DatabaseOseeEnumTypeAccessor implements IOseeTypeDataAccessor<OseeEnumType> {

   private static final String SELECT_OSEE_ENUM_TYPES =
         "select oet.enum_type_name, oet.enum_type_guid, oetd.* from osee_enum_type oet, osee_enum_type_def oetd where oet.enum_type_id = oetd.enum_type_id order by oetd.enum_type_id, oetd.ordinal";
   private static final String INSERT_ENUM_TYPE =
         "insert into osee_enum_type (enum_type_id, enum_type_guid, enum_type_name) values (?,?,?)";
   private static final String UPDATE_ENUM_TYPE = "update osee_enum_type set enum_type_name=? where enum_type_id=?";
   private static final String DELETE_ENUM_TYPE = "delete from osee_enum_type oet where enum_type_id = ?";

   private static final String INSERT_ENUM_TYPE_DEF =
         "insert into osee_enum_type_def (enum_type_id, enum_entry_guid, name, ordinal) values (?,?,?,?)";
   private static final String UPDATE_ENUM_TYPE_DEF =
         "update osee_enum_type_def set name=?, ordinal=? where enum_entry_guid=?";
   private static final String DELETE_ENUM_TYPE_DEF = "delete from osee_enum_type_def where enum_entry_guid = ?";

   @Override
   public void load(OseeTypeCache cache, IOseeTypeFactory factory) throws OseeCoreException {
      OseeEnumTypeCache cacheData = cache.getEnumTypeCache();
      HashCollection<OseeEnumType, OseeEnumEntry> types = new HashCollection<OseeEnumType, OseeEnumEntry>();
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      try {
         chStmt.runPreparedQuery(SELECT_OSEE_ENUM_TYPES);
         OseeEnumType oseeEnumType = null;
         int lastEnumTypeId = -1;
         while (chStmt.next()) {
            try {
               int currentEnumTypeId = chStmt.getInt("enum_type_id");
               String currentEnumTypeGuid = chStmt.getString("enum_type_guid");
               if (lastEnumTypeId != currentEnumTypeId) {
                  oseeEnumType =
                        factory.createEnumType(cacheData, currentEnumTypeGuid, chStmt.getString("enum_type_name"));
                  oseeEnumType.setTypeId(currentEnumTypeId);
                  oseeEnumType.setModificationType(ModificationType.MODIFIED);
                  cache.getEnumTypeCache().cacheType(oseeEnumType);
                  lastEnumTypeId = currentEnumTypeId;
               }
               OseeEnumEntry entry =
                     factory.createEnumEntry(cacheData, chStmt.getString("enum_entry_guid"), chStmt.getString("name"),
                           chStmt.getInt("ordinal"));
               entry.setModificationType(ModificationType.MODIFIED);
               types.put(oseeEnumType, entry);
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      } finally {
         chStmt.close();
      }
      for (OseeEnumType oseeEnumType : types.keySet()) {
         Collection<OseeEnumEntry> oseeEnumEntries = types.getValues(oseeEnumType);
         if (oseeEnumEntries != null) {
            oseeEnumType.setEntries(oseeEnumEntries);
         }
      }
   }

   @Override
   public void store(OseeTypeCache cache, Collection<OseeEnumType> oseeEnumTypes) throws OseeCoreException {
      List<Object[]> insertData = new ArrayList<Object[]>();
      List<Object[]> updateData = new ArrayList<Object[]>();
      List<Object[]> deleteData = new ArrayList<Object[]>();
      for (OseeEnumType oseeEnumType : oseeEnumTypes) {
         switch (oseeEnumType.getModificationType()) {
            case NEW:
               oseeEnumType.setTypeId(SequenceManager.getNextOseeEnumTypeId());
               insertData.add(toInsertValues(oseeEnumType));
               break;
            case MODIFIED:
               updateData.add(toUpdateValues(oseeEnumType));
               break;
            case DELETED:
               deleteData.add(toDeleteValues(oseeEnumType));
               break;
            default:
               break;
         }
      }
      ConnectionHandler.runBatchUpdate(INSERT_ENUM_TYPE, insertData);
      ConnectionHandler.runBatchUpdate(UPDATE_ENUM_TYPE, updateData);
      ConnectionHandler.runBatchUpdate(DELETE_ENUM_TYPE, deleteData);

      for (OseeEnumType oseeEnumType : oseeEnumTypes) {
         oseeEnumType.persist();
      }

      insertData.clear();
      updateData.clear();
      deleteData.clear();
      for (OseeEnumType oseeEnumType : oseeEnumTypes) {
         for (OseeEnumEntry entry : oseeEnumType.values()) {
            switch (entry.getModificationType()) {
               case NEW:
                  entry.setTypeId(oseeEnumType.getTypeId());
                  insertData.add(toInsertValues(entry));
                  break;
               case MODIFIED:
                  updateData.add(toUpdateValues(entry));
                  break;
               case DELETED:
                  deleteData.add(toDeleteValues(entry));
                  break;
               default:
                  break;
            }
         }
      }
      ConnectionHandler.runBatchUpdate(INSERT_ENUM_TYPE_DEF, insertData);
      ConnectionHandler.runBatchUpdate(UPDATE_ENUM_TYPE_DEF, updateData);
      ConnectionHandler.runBatchUpdate(DELETE_ENUM_TYPE_DEF, deleteData);

      for (OseeEnumType oseeEnumType : oseeEnumTypes) {
         for (OseeEnumEntry entry : oseeEnumType.values()) {
            entry.persist();
         }
      }
   }

   private Object[] toInsertValues(OseeEnumType type) throws OseeDataStoreException {
      return new Object[] {type.getTypeId(), type.getGuid(), type.getName()};
   }

   private Object[] toUpdateValues(OseeEnumType type) throws OseeDataStoreException {
      return new Object[] {type.getName(), type.getTypeId()};
   }

   private Object[] toDeleteValues(OseeEnumType type) throws OseeDataStoreException {
      return new Object[] {type.getTypeId()};
   }

   private Object[] toInsertValues(OseeEnumEntry type) throws OseeDataStoreException {
      return new Object[] {type.getTypeId(), type.getGuid(), type.getName(), type.ordinal()};
   }

   private Object[] toUpdateValues(OseeEnumEntry type) throws OseeDataStoreException {
      return new Object[] {type.getName(), type.ordinal(), type.getGuid()};
   }

   private Object[] toDeleteValues(OseeEnumEntry type) throws OseeDataStoreException {
      return new Object[] {type.getGuid()};
   }
}
