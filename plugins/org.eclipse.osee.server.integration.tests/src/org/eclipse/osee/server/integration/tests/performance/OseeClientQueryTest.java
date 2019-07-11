/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.server.integration.tests.performance;

import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.Folder;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.GeneralData;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.GeneralDocument;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.Requirement;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.SoftwareRequirement;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.AccessContextId;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.Active;
import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_1;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.orcs.rest.client.OseeClient;
import org.eclipse.osee.orcs.rest.model.search.artifact.RequestType;
import org.eclipse.osee.orcs.rest.model.search.artifact.SearchResult;
import org.eclipse.osee.server.integration.tests.util.IntegrationUtil;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class OseeClientQueryTest {
   private static OseeClient oseeClient;

   @BeforeClass
   public static void testSetup() {
      oseeClient = IntegrationUtil.createClient();

      // Establish initial connection to the db using this random query
      oseeClient.createQueryBuilder(COMMON).andId(SystemUser.OseeSystem).getSearchResult(RequestType.IDS);
   }

   @Test
   public void searchForAttributeTypeByTokenIds() {
      List<ArtifactToken> tokens = Arrays.asList(SystemUser.OseeSystem, SystemUser.Anonymous);
      SearchResult results = oseeClient.createQueryBuilder(COMMON).andIds(tokens).getSearchResult(RequestType.IDS);
      assertEquals(tokens.size(), results.getTotal());
   }

   @Test
   public void searchForArtifactByLocalId() {
      final int EXPECTED_RESULTS = 1;
      SearchResult results =
         oseeClient.createQueryBuilder(COMMON).andId(CoreArtifactTokens.UserGroups).getSearchResult(RequestType.IDS);
      assertEquals(EXPECTED_RESULTS, results.getTotal());
   }

   @Test
   public void searchForArtifactByName() {
      final int EXPECTED_RESULTS = 1;
      SearchResult results =
         oseeClient.createQueryBuilder(COMMON).andNameEquals(DemoUsers.Joe_Smith.getName()).getSearchResult(
            RequestType.IDS);
      assertEquals(EXPECTED_RESULTS, results.getTotal());
   }

   @Test
   public void searchForArtifactWithActionInName() {
      final int EXPECTED_RESULTS = 48;
      SearchResult results = oseeClient.createQueryBuilder(COMMON).and(CoreAttributeTypes.Name, "SAW",
         QueryOption.CASE__IGNORE, QueryOption.TOKEN_MATCH_ORDER__MATCH, QueryOption.TOKEN_DELIMITER__ANY,
         QueryOption.TOKEN_COUNT__IGNORE).getSearchResult(RequestType.IDS);
      assertGreaterOrEqual(EXPECTED_RESULTS, results.getTotal());
   }

   private void assertGreaterOrEqual(int expected, int actual) {
      Assert.assertTrue("Expected " + expected + " not " + actual, actual >= expected);
   }

   @Test
   public void searchForArtifactType() {
      final int EXPECTED_RESULTS = 9;
      SearchResult results =
         oseeClient.createQueryBuilder(SAW_Bld_1).andTypeEquals(Folder).getSearchResult(RequestType.IDS);
      assertGreaterOrEqual(EXPECTED_RESULTS, results.getTotal());
   }

   @Test
   public void searchForArtifactTypes() {
      final int EXPECTED_RESULTS = 24;
      SearchResult results = oseeClient.createQueryBuilder(SAW_Bld_1).andTypeEquals(GeneralData, GeneralDocument,
         SoftwareRequirement).getSearchResult(RequestType.IDS);
      assertGreaterOrEqual(EXPECTED_RESULTS, results.getTotal());
   }

   @Test
   public void searchForArtifactTypesIncludeTypeInheritance() {
      final int EXPECTED_RESULTS = 150;
      SearchResult results = oseeClient.createQueryBuilder(SAW_Bld_1).andIsOfType(GeneralData, GeneralDocument,
         Requirement).getSearchResult(RequestType.IDS);
      assertGreaterOrEqual(EXPECTED_RESULTS, results.getTotal());
   }

   @Test
   public void searchForExistenceOfAttributeType() {
      final int EXPECTED_RESULTS = 24;
      SearchResult results = oseeClient.createQueryBuilder(COMMON).andExists(Active).getSearchResult(RequestType.IDS);
      assertGreaterOrEqual(EXPECTED_RESULTS, results.getTotal());
   }

   @Test
   public void searchForExistenceOfAttributeTypeIncludeDeleted() {
      final int EXPECTED_RESULTS = 24;
      SearchResult results =
         oseeClient.createQueryBuilder(COMMON).andExists(Active).includeDeleted().getSearchResult(RequestType.IDS);
      assertGreaterOrEqual(EXPECTED_RESULTS, results.getTotal());
   }

   @Test
   public void searchForExistenceOfAttributeTypes() {
      final int EXPECTED_RESULTS = 24;
      SearchResult results =
         oseeClient.createQueryBuilder(COMMON).andExists(Active, AccessContextId).getSearchResult(RequestType.IDS);
      assertGreaterOrEqual(EXPECTED_RESULTS, results.getTotal());
   }

   /**
    * This test simply ensures that the client endpoint to run Orcs Script works. OrcsScriptTest is the more exhaustive
    * test of Orcs Script.
    */
   @Test
   public void orcsScript() {
      String script =
         "start from branch 570 find artifacts where art-type = 'Folder' collect artifacts {id, attributes { value } };";

      StringWriter writer = new StringWriter();
      Properties properties = new Properties();
      oseeClient.runOrcsScript(script, properties, false, MediaType.APPLICATION_JSON_TYPE, writer);

      String results = writer.toString();
      assertTrue("actual results: " + results, results.contains("value\" : \"User Groups"));
   }
}