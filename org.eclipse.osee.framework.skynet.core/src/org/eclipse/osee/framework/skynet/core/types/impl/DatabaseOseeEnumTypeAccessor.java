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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.ConnectionHandlerStatement;
import org.eclipse.osee.framework.database.core.SequenceManager;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.AbstractOseeType;
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
   private static final String DELETE_ENUM_TYPE_DEF = "delete from osee_enum_type_def where enum_type_id = ?";

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
               entry.clearDirty();
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
            oseeEnumType.clearDirty();
         }
      }
   }

   @Override
   public void store(OseeTypeCache cache, Collection<OseeEnumType> oseeEnumTypes) throws OseeCoreException {
      Set<OseeEnumType> dirtyEntries = new HashSet<OseeEnumType>();
      List<Object[]> insertData = new ArrayList<Object[]>();
      List<Object[]> updateData = new ArrayList<Object[]>();
      List<Object[]> deleteData = new ArrayList<Object[]>();
      for (OseeEnumType oseeEnumType : oseeEnumTypes) {
         if (oseeEnumType.isDataDirty()) {
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
         if (oseeEnumType.areEntriesDirty()) {
            if (!oseeEnumType.getModificationType().isDeleted()) {
               dirtyEntries.add(oseeEnumType);
            }
         }
      }
      ConnectionHandler.runBatchUpdate(INSERT_ENUM_TYPE, insertData);
      ConnectionHandler.runBatchUpdate(UPDATE_ENUM_TYPE, updateData);
      ConnectionHandler.runBatchUpdate(DELETE_ENUM_TYPE, deleteData);

      storeOseeEnumEntries(dirtyEntries);
      for (OseeEnumType oseeEnumType : oseeEnumTypes) {
         oseeEnumType.clearDirty();
      }

      insertData.clear();
      updateData.clear();
      deleteData.clear();

      for (OseeEnumType oseeEnumType : oseeEnumTypes) {
         for (OseeEnumEntry entry : oseeEnumType.values()) {
            entry.clearDirty();
         }
      }
   }

   private void storeOseeEnumEntries(Collection<OseeEnumType> oseeEnumTypes) throws OseeCoreException {
      List<Object[]> insertData = new ArrayList<Object[]>();
      List<Object[]> deleteData = new ArrayList<Object[]>();
      for (OseeEnumType type : oseeEnumTypes) {
         if (type.getTypeId() != AbstractOseeType.UNPERSISTTED_VALUE) {
            deleteData.add(toDeleteValues(type));
         }
         for (OseeEnumEntry entry : type.values()) {
            switch (entry.getModificationType()) {
               case NEW:
                  entry.setTypeId(type.getTypeId());
                  insertData.add(toInsertValues(entry));
                  break;
               case MODIFIED:
                  insertData.add(toInsertValues(entry));
                  break;
               default:
                  break;
            }
         }
      }
      ConnectionHandler.runBatchUpdate(DELETE_ENUM_TYPE_DEF, deleteData);
      ConnectionHandler.runBatchUpdate(INSERT_ENUM_TYPE_DEF, insertData);
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
}
