/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.coverage.store;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.IOseeStatement;

/**
 * @author Donald G. Dunne
 */
public class TestUnitStore {
   private static String TEST_UNIT_NAME_QUERY = "select name from osee_cvg_testunits where name_id = ?";
   private static String TEST_UNIT_ID_QUERY = "select name_id from osee_cvg_testunits where name = ?";
   private static String INSERT_TEST_UNIT_NAME = "insert into osee_cvg_testunits (name_id, name) values (?,?)";
   private static String DELETE_ALL_TEST_NAMES = "delete from osee_cvg_testunits";
   private static String TEST_UNIT_COUNT_QUERY = "SELECT count(name) from osee_cvg_testunits";
   private static TestUnitStore instance = new TestUnitStore();
   final Map<String, Integer> nameToId = new HashMap<String, Integer>(1000);
   final Map<Integer, String> idToName = new HashMap<Integer, String>(1000);

   private TestUnitStore() {
      // private constructor
   }

   private int addTestUnitNameToDb(String name) throws OseeCoreException {
      IOseeStatement chStmt = ConnectionHandler.getStatement();
      int nextId = getNextTestUnitNameId();
      try {
         chStmt.runCallableStatement(INSERT_TEST_UNIT_NAME, nextId, name);
      } finally {
         chStmt.close();
      }
      return nextId;
   }

   public static void clearStore() throws OseeCoreException {
      instance.nameToId.clear();
      instance.idToName.clear();
      IOseeStatement chStmt = ConnectionHandler.getStatement();
      try {
         chStmt.runCallableStatement(DELETE_ALL_TEST_NAMES);
      } finally {
         chStmt.close();
      }
   }

   private int getNextTestUnitNameId() throws OseeCoreException {
      return getTestUnitCount() + 1;
   }

   public static int getTestUnitCount() throws OseeCoreException {
      IOseeStatement chStmt = ConnectionHandler.getStatement();
      try {
         chStmt.runPreparedQuery(TEST_UNIT_COUNT_QUERY);
         chStmt.next();
         return chStmt.getInt(1);
      } finally {
         chStmt.close();
      }
   }

   private void cacheName(String name, Integer idInt) {
      nameToId.put(name, idInt);
      idToName.put(idInt, name);
   }

   public static Integer getTestUnitId(String name, boolean add) throws OseeCoreException {
      if (instance.nameToId.containsKey(name)) {
         return instance.nameToId.get(name);
      }
      IOseeStatement chStmt = ConnectionHandler.getStatement();
      try {
         chStmt.runPreparedQuery(TEST_UNIT_ID_QUERY, name);
         if (chStmt.next()) {
            Integer nameId = chStmt.getInt(1);
            instance.cacheName(name, nameId);
            return nameId;
         }
         if (add) {
            int nameId = instance.addTestUnitNameToDb(name);
            instance.cacheName(name, nameId);
            return nameId;
         } else {
            return null;
         }
      } finally {
         chStmt.close();
      }
   }

   public static String getTestUnitName(Integer nameId) throws OseeCoreException {
      if (instance.idToName.containsKey(nameId)) {
         return instance.idToName.get(nameId);
      }
      IOseeStatement chStmt = ConnectionHandler.getStatement();
      try {
         chStmt.runPreparedQuery(TEST_UNIT_NAME_QUERY, nameId);
         if (chStmt.next()) {
            String name = chStmt.getString(1);
            instance.cacheName(name, nameId);
            return name;
         }
      } finally {
         chStmt.close();
      }
      return null;
   }

}
