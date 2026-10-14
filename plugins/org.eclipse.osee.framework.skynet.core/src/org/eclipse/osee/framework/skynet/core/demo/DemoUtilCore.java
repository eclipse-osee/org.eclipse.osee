/*********************************************************************
 * Copyright (c) 2026 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.framework.skynet.core.demo;

import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_1;
import java.util.Collection;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.utility.OseeInfo;

/**
 * @author Donald G. Dunne
 */
public class DemoUtilCore {

   private static final String OSEE_BLAM_TEST = "osee.blam.test";

   private DemoUtilCore() {
      // Utility class
   }

   public static Artifact getRobotInterfacesArt() {
      return ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.SoftwareRequirementMsWord, "Robot Interfaces",
         SAW_Bld_1);
   }

   public static Result isDbPopulatedWithDemoData() {
      Collection<Artifact> robotArtifacts = ArtifactQuery.getArtifactListFromTypeAndName(
         CoreArtifactTypes.SoftwareRequirementMsWord, "Robot", SAW_Bld_1, QueryOption.CONTAINS_MATCH_OPTIONS);
      if (robotArtifacts.size() < 6) {
         return new Result(String.format(
            "Expected at least 6 Software Requirements with name \"Robot\" but found [%s].  Database is not be populated with demo data.",
            robotArtifacts.size()));
      }
      return Result.TrueResult;
   }

   public static void checkDbInitSuccess() {
      if (!isDbInitSuccessful()) {
         throw new OseeStateException("DbInit must be successful to continue");
      }
   }

   public static void checkDbInitAndPopulateSuccess() {
      if (!isDbInitSuccessful()) {
         throw new OseeStateException("DbInit must be successful to continue");
      }
      if (!isPopulateDbSuccessful()) {
         throw new OseeStateException("PopulateDemoDb must be successful to continue");
      }
   }

   public static boolean isDbInitSuccessful() {
      return isOseeInfo("DbInitSuccess", "true");
   }

   public static void setDbInitSuccessful(boolean success) {
      setOseeInfo("DbInitSuccess", String.valueOf(success));
   }

   public static boolean isPopulateDbSuccessful() {
      return isOseeInfo("PopulateSuccessful", "true");
   }

   public static void setOseeInfo(String key, String value) {
      OseeInfo.setValue(key, value);
   }

   public static void removeOseeInfo(String key) {
      OseeInfo.setValue(key, null);
   }

   public static void setPopulateDbSuccessful(boolean success) {
      setOseeInfo("PopulateSuccessful", String.valueOf(success));
   }

   public static String getOseeInfo(String key) {
      return OseeInfo.getValue(key);
   }

   public static boolean isOseeInfo(String key, String value) {
      String val = getOseeInfo(key);
      return val.equals(value);
   }

   public static boolean isInTest() {
      return OseeProperties.isInTest() || isOseeInfo("IsInTest", "true");
   }

   public static void setIsInTest(boolean set) {
      OseeProperties.setIsInTest(set);
      setOseeInfo("IsInTest", String.valueOf(set));
   }

   public static void assertNotNull(Object obj, XResultData rd) {
      if (obj == null) {
         rd.errorf("Object should not be null\n");
      }
   }

   public static boolean isDemoDatabase() {
      return System.getProperty("osee.db", "").equals("orgdemo");
   }

   public static void setDemoDbBlamTest(Boolean set) {
      System.setProperty(OSEE_BLAM_TEST, set.toString());
   }

   /**
    * @return true if running integration BLAM tests
    */
   public static boolean isDemoDbBlamTest() {
      return System.getProperty(OSEE_BLAM_TEST, "").equals("true");
   }

}
