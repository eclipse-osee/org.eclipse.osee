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
import org.eclipse.osee.coverage.model.CoverageUnit;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.jdk.core.util.Collections;

/**
 * @author Donald G. Dunne
 */
public class TestUnitStore {

   Map<Integer, String> testIdToNameMap = new HashMap<Integer, String>();
   public static String TEST_UNIT_QUERY =
         "select name, cvgn.name_id from osee_cvg_testunit_names cvgn, osee_cvg_test_units cvgu where cvgn.name_id = cvgu.name_id and cvgu.item_guid = ?";
   public static String TEST_UNIT_ID_QUERY = "select name, name_id from osee_cvg_testunit_names where name_id = ?";
   public static String ITEM_TO_ID_QUERY = "select name_id from osee_cvg_test_units where cvgu.item_guid = ?";
   public static String DELETE_TEST_UNIT_ID = "delete from osee_cvg_test_units where item_guid = ? and name_id in (?)";
   public static String INSERT_TEST_UNIT_ID = "insert into osee_cvg_test_units set (name_id, guid) values (?,?)";

   public static void setTestUnits(CoverageUnit coverageUnit, Collection<String> testUnitNames) throws OseeCoreException {
      List<Integer> toDelete = new ArrayList<Integer>();
      // Remove old values
      for (Entry<Integer, String> entry : getTestUnits(coverageUnit).entrySet()) {
         if (!testUnitNames.contains(entry.getValue())) {
            toDelete.add(entry.getKey());
         }
      }
      if (toDelete.size() > 0) {
         removeTestUnitsFromDb(coverageUnit.getGuid(), toDelete);
      }

      // Add new values
      Map<Integer, String> currentTestUnits = getTestUnits(coverageUnit);
      for (String testUnitName : testUnitNames) {
         if (!currentTestUnits.values().contains(testUnitName)) {
            addTestUnitToDb(coverageUnit.getGuid(), testUnitName);
         }
      }

   }

   public static Map<Integer, String> getTestUnits(CoverageUnit coverageUnit) throws OseeCoreException {
      IOseeStatement chStmt = ConnectionHandler.getStatement();
      Map<Integer, String> names = new HashMap<Integer, String>();
      try {
         chStmt.runPreparedQuery(TEST_UNIT_QUERY, coverageUnit.getGuid());
         while (chStmt.next()) {
            names.put(chStmt.getInt("name_id"), chStmt.getString("name"));
         }
      } finally {
         chStmt.close();
      }
      return names;
   }

   public static Map<Integer, String> addTestUnitToDb(String guid, String name) throws OseeCoreException {
      // TODO Check if in name table
      // TODO Insert into name table
      // TODO Insert into test unit table
      IOseeStatement chStmt = ConnectionHandler.getStatement();
      Map<Integer, String> names = new HashMap<Integer, String>();
      try {
         chStmt.runPreparedQuery(INSERT_TEST_UNIT_ID, guid, name);
         while (chStmt.next()) {
            names.put(chStmt.getInt("name_id"), chStmt.getString("name"));
         }
      } finally {
         chStmt.close();
      }
      return names;
   }

   public static void removeTestUnitsFromDb(String guid, List<Integer> toDelete) throws OseeCoreException {
      IOseeStatement chStmt = ConnectionHandler.getStatement();
      try {
         chStmt.runPreparedQuery(DELETE_TEST_UNIT_ID, guid, Collections.toString(",", toDelete));
      } finally {
         chStmt.close();
      }
   }

}
