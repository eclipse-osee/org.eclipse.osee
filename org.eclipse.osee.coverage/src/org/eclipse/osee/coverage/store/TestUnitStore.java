/*
 * Created on Jan 20, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.store;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.osee.coverage.model.CoverageItem;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.IOseeStatement;

/**
 * @author Donald G. Dunne
 */
public class TestUnitStore {

   Map<Integer, String> testIdToNameMap = new HashMap<Integer, String>();
   public static String TEST_UNIT_QUERY =
         "select name, cvgn.name_id from osee_cvg_testunit_names cvgn, osee_cvg_testunits cvgu where cvgn.name_id = cvgu.name_id and cvgu.item_guid = ?";
   public static String TEST_UNIT_ID_QUERY = "select name, name_id from osee_cvg_testunit_names where name_id = ?";
   public static String ITEM_TO_ID_QUERY = "select name_id from osee_cvg_testunits where cvgu.item_guid = ?";
   public static String DELETE_TEST_UNIT_ID = "delete from osee_cvg_testunits where item_guid = ? and name_id = ?";
   public static String INSERT_TEST_UNIT_ID = "insert into osee_cvg_testunits (name_id, item_guid) values (?,?)";
   public static String INSERT_TEST_UNIT_NAME = "insert into osee_cvg_testunit_names (name_id, name) values (?,?)";
   public static String DELETE_ALL_TEST_NAMES = "delete from osee_cvg_testunit_names";

   public static void setTestUnits(CoverageItem coverageItem, Collection<String> testUnitNames) throws OseeCoreException {
      List<Integer> toDelete = new ArrayList<Integer>();
      // Remove old values
      for (Entry<Integer, String> entry : getTestUnitMap(coverageItem).entrySet()) {
         if (!testUnitNames.contains(entry.getValue())) {
            toDelete.add(entry.getKey());
         }
      }
      if (toDelete.size() > 0) {
         removeTestUnitsFromDb(coverageItem.getGuid(), toDelete);
      }

      // Add new values
      Map<Integer, String> currentTestUnits = getTestUnitMap(coverageItem);
      for (String testUnitName : testUnitNames) {
         if (!currentTestUnits.values().contains(testUnitName)) {
            addTestUnitToDb(coverageItem.getGuid(), testUnitName);
         }
      }
   }

   public static void removeTestUnits(CoverageItem coverageItem) throws OseeCoreException {
      removeTestUnits(coverageItem, null);
   }

   public static void removeTestUnits(CoverageItem coverageItem, Collection<String> testUnitNames) throws OseeCoreException {
      List<Integer> toDelete = new ArrayList<Integer>();
      // Remove old values
      for (Entry<Integer, String> entry : getTestUnitMap(coverageItem).entrySet()) {
         if (testUnitNames == null || testUnitNames.contains(entry.getValue())) {
            toDelete.add(entry.getKey());
         }
      }
      if (toDelete.size() > 0) {
         removeTestUnitsFromDb(coverageItem.getGuid(), toDelete);
      }
   }

   public static Map<Integer, String> getTestUnitMap(CoverageItem coverageItem) throws OseeCoreException {
      IOseeStatement chStmt = ConnectionHandler.getStatement();
      Map<Integer, String> names = new HashMap<Integer, String>();
      try {
         chStmt.runPreparedQuery(TEST_UNIT_QUERY, coverageItem.getGuid());
         while (chStmt.next()) {
            names.put(chStmt.getInt("name_id"), chStmt.getString("name"));
         }
      } finally {
         chStmt.close();
      }
      return names;
   }

   public static Collection<String> getTestUnitNames(CoverageItem coverageItem) throws OseeCoreException {
      return getTestUnitMap(coverageItem).values();
   }

   private static Map<Integer, String> addTestUnitToDb(String item_guid, String name) throws OseeCoreException {
      int name_id = getNameId(name, true);
      IOseeStatement chStmt = ConnectionHandler.getStatement();
      Map<Integer, String> names = new HashMap<Integer, String>();
      try {
         chStmt.runCallableStatement(INSERT_TEST_UNIT_ID, name_id, item_guid);
      } finally {
         chStmt.close();
      }
      return names;
   }

   private static int addTestUnitNameToDb(String name) throws OseeCoreException {
      IOseeStatement chStmt = ConnectionHandler.getStatement();
      int nextId = getNextNameId();
      try {
         chStmt.runCallableStatement(INSERT_TEST_UNIT_NAME, nextId, name);
      } finally {
         chStmt.close();
      }
      return nextId;
   }

   public static void clearTestUnitNames() throws OseeCoreException {
      IOseeStatement chStmt = ConnectionHandler.getStatement();
      try {
         chStmt.runCallableStatement(DELETE_ALL_TEST_NAMES);
      } finally {
         chStmt.close();
      }
   }

   private static void removeTestUnitsFromDb(String item_guid, List<Integer> toDelete) throws OseeCoreException {
      IOseeStatement chStmt = ConnectionHandler.getStatement();
      try {
         for (Integer nameId : toDelete) {
            chStmt.runCallableStatement(DELETE_TEST_UNIT_ID, item_guid, nameId);
         }
      } finally {
         chStmt.close();
      }
   }

   private static int getNextNameId() throws OseeCoreException {
      return getNameCount() + 1;
   }

   public static int getNameCount() throws OseeCoreException {
      IOseeStatement chStmt = ConnectionHandler.getStatement();
      try {
         chStmt.runPreparedQuery("SELECT count(name) from osee_cvg_testunit_names");
         chStmt.next();
         return chStmt.getInt(1);
      } finally {
         chStmt.close();
      }
   }

   public static Integer getNameId(String name, boolean add) throws OseeCoreException {
      IOseeStatement chStmt = ConnectionHandler.getStatement();
      try {
         chStmt.runPreparedQuery("SELECT name_id from osee_cvg_testunit_names where name = ?", name);
         if (chStmt.next()) {
            return chStmt.getInt(1);
         }
         if (add) {
            return addTestUnitNameToDb(name);
         } else {
            return null;
         }
      } finally {
         chStmt.close();
      }
   }
}
