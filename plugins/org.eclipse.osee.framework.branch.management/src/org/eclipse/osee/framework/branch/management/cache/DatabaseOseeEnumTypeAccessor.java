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
package org.eclipse.osee.framework.branch.management.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.framework.branch.management.internal.Activator;
import org.eclipse.osee.framework.core.enums.StorageState;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.model.AbstractOseeType;
import org.eclipse.osee.framework.core.model.OseeEnumEntry;
import org.eclipse.osee.framework.core.model.cache.IOseeCache;
import org.eclipse.osee.framework.core.model.type.OseeEnumType;
import org.eclipse.osee.framework.core.model.type.OseeEnumTypeFactory;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryServiceProvider;
import org.eclipse.osee.framework.database.IOseeDatabaseServiceProvider;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 */
public class DatabaseOseeEnumTypeAccessor extends AbstractDatabaseAccessor<OseeEnumType> {

   private static final String SELECT_OSEE_ENUM_TYPES = "select * from osee_enum_type oet";
   private static final String SELECT_OSEE_ENUM_ENTRIES =
         "select * from osee_enum_type_def order by enum_type_id, ordinal";

   private static final String INSERT_ENUM_TYPE =
         "insert into osee_enum_type (enum_type_id, enum_type_guid, enum_type_name) values (?,?,?)";
   private static final String UPDATE_ENUM_TYPE = "update osee_enum_type set enum_type_name=? where enum_type_id=?";
   private static final String DELETE_ENUM_TYPE = "delete from osee_enum_type oet where enum_type_id = ?";

   private static final String INSERT_ENUM_TYPE_DEF =
         "insert into osee_enum_type_def (enum_type_id, enum_entry_guid, name, ordinal) values (?,?,?,?)";
   private static final String DELETE_ENUM_TYPE_DEF = "delete from osee_enum_type_def where enum_type_id = ?";

   public DatabaseOseeEnumTypeAccessor(IOseeDatabaseServiceProvider databaseProvider, IOseeModelFactoryServiceProvider factoryProvider) {
      super(databaseProvider, factoryProvider);
   }

   private void loadEnumEntries(OseeEnumTypeFactory factory, HashCollection<Integer, OseeEnumEntry> entryTypes) throws OseeCoreException {
      IOseeStatement chStmt = getDatabaseService().getStatement();
      try {
         chStmt.runPreparedQuery(SELECT_OSEE_ENUM_ENTRIES);
         while (chStmt.next()) {
            try {
               Integer enumTypeId = chStmt.getInt("enum_type_id");
               String enumEntryGuid = chStmt.getString("enum_entry_guid");
               String enumEntryName = chStmt.getString("name");
               int ordinal = chStmt.getInt("ordinal");

               OseeEnumEntry entry = factory.createEnumEntry(enumEntryGuid, enumEntryName, ordinal);
               entry.setStorageState(StorageState.LOADED);
               entry.clearDirty();
               entryTypes.put(enumTypeId, entry);
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      } finally {
         chStmt.close();
      }
   }

   @Override
   public void load(IOseeCache<OseeEnumType> cache) throws OseeCoreException {
      OseeEnumTypeFactory factory = getFactoryService().getOseeEnumTypeFactory();

      HashCollection<Integer, OseeEnumEntry> entryTypes = new HashCollection<Integer, OseeEnumEntry>();

      loadEnumEntries(factory, entryTypes);

      IOseeStatement chStmt = getDatabaseService().getStatement();
      try {
         chStmt.runPreparedQuery(SELECT_OSEE_ENUM_TYPES);
         while (chStmt.next()) {
            try {
               Integer enumTypeId = chStmt.getInt("enum_type_id");
               String enumTypeGuid = chStmt.getString("enum_type_guid");
               String enumTypeName = chStmt.getString("enum_type_name");

               OseeEnumType oseeEnumType =
                     factory.createOrUpdate(cache, enumTypeId, StorageState.LOADED, enumTypeGuid, enumTypeName);

               List<OseeEnumEntry> oseeEnumEntries = (List<OseeEnumEntry>) entryTypes.getValues(enumTypeId);
               if (oseeEnumEntries != null) {
                  oseeEnumType.setEntries(oseeEnumEntries);
                  oseeEnumType.clearDirty();
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
   public void store(Collection<OseeEnumType> oseeEnumTypes) throws OseeCoreException {
      Set<OseeEnumType> dirtyEntries = new HashSet<OseeEnumType>();
      List<Object[]> insertData = new ArrayList<Object[]>();
      List<Object[]> updateData = new ArrayList<Object[]>();
      List<Object[]> deleteData = new ArrayList<Object[]>();
      for (OseeEnumType oseeEnumType : oseeEnumTypes) {
         if (isDataDirty(oseeEnumType)) {
            switch (oseeEnumType.getStorageState()) {
               case CREATED:
                  oseeEnumType.setId(getSequence().getNextOseeEnumTypeId());
                  insertData.add(toInsertValues(oseeEnumType));
                  break;
               case MODIFIED:
                  updateData.add(toUpdateValues(oseeEnumType));
                  break;
               case PURGED:
                  deleteData.add(toDeleteValues(oseeEnumType));
                  break;
               default:
                  break;
            }
         }
         if (oseeEnumType.isFieldDirty(OseeEnumType.OSEE_ENUM_TYPE_ENTRIES_FIELD)) {
            dirtyEntries.add(oseeEnumType);
         }
      }
      getDatabaseService().runBatchUpdate(INSERT_ENUM_TYPE, insertData);
      getDatabaseService().runBatchUpdate(UPDATE_ENUM_TYPE, updateData);
      getDatabaseService().runBatchUpdate(DELETE_ENUM_TYPE, deleteData);

      storeOseeEnumEntries(dirtyEntries);
      for (OseeEnumType oseeEnumType : oseeEnumTypes) {
         oseeEnumType.clearDirty();
         for (OseeEnumEntry entry : oseeEnumType.values()) {
            entry.clearDirty();
         }
      }
   }

   private boolean isDataDirty(OseeEnumType type) throws OseeCoreException {
      return type.areFieldsDirty(AbstractOseeType.NAME_FIELD_KEY, AbstractOseeType.UNIQUE_ID_FIELD_KEY);
   }

   private void storeOseeEnumEntries(Collection<OseeEnumType> oseeEnumTypes) throws OseeCoreException {
      List<Object[]> insertData = new ArrayList<Object[]>();
      List<Object[]> deleteData = new ArrayList<Object[]>();

      for (OseeEnumType type : oseeEnumTypes) {
         // Delete all type entries that have been inserted into the DB before
         if (type.isIdValid()) {
            deleteData.add(toDeleteValues(type));
         }

         //Re-add only entries that are valid
         for (OseeEnumEntry entry : type.values()) {
            if (StorageState.PURGED != entry.getStorageState()) {
               if (!entry.isIdValid()) {
                  entry.setId(type.getId());
               }
               insertData.add(toInsertValues(entry));
            }
         }
      }

      getDatabaseService().runBatchUpdate(DELETE_ENUM_TYPE_DEF, deleteData);
      getDatabaseService().runBatchUpdate(INSERT_ENUM_TYPE_DEF, insertData);
   }

   private Object[] toInsertValues(OseeEnumType type) throws OseeDataStoreException {
      return new Object[] {type.getId(), type.getGuid(), type.getName()};
   }

   private Object[] toUpdateValues(OseeEnumType type) throws OseeDataStoreException {
      return new Object[] {type.getName(), type.getId()};
   }

   private Object[] toDeleteValues(OseeEnumType type) throws OseeDataStoreException {
      return new Object[] {type.getId()};
   }

   private Object[] toInsertValues(OseeEnumEntry type) throws OseeDataStoreException {
      return new Object[] {type.getId(), type.getGuid(), type.getName(), type.ordinal()};
   }
}
