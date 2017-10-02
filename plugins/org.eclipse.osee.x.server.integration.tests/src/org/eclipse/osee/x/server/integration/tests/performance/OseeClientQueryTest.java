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
package org.eclipse.osee.x.server.integration.tests.performance;

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
import java.util.Properties;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.orcs.rest.client.OseeClient;
import org.eclipse.osee.orcs.rest.model.search.artifact.RequestType;
import org.eclipse.osee.orcs.rest.model.search.artifact.SearchResult;
import org.eclipse.osee.x.server.integration.tests.util.IntegrationUtil;
import org.junit.BeforeClass;
import org.junit.Test;

public class OseeClientQueryTest {

   private static final String GUID1 = SystemUser.Anonymous.getGuid();
   private static final String GUID2 = SystemUser.OseeSystem.getGuid();

   private static OseeClient createClient;

   @BeforeClass
   public static void testSetup() {
      createClient = IntegrationUtil.createClient();
      if (!createClient.isLocalHost()) {
         throw new OseeStateException("This test should be run with local test server, not %s",
            createClient.getBaseUri());
      }

      // Establish initial connection to the db using this random query
      createClient.createQueryBuilder(COMMON).andIds(SystemUser.OseeSystem).getSearchResult(RequestType.IDS);
   }

   @Test
   public void searchForAttributeTypeByTokenId() {
      final int EXPECTED_RESULTS = 1;
      SearchResult results =
         createClient.createQueryBuilder(COMMON).andIds(SystemUser.OseeSystem).getSearchResult(RequestType.IDS);
      assertEquals(EXPECTED_RESULTS, results.getTotal());
   }

   @Test
   public void searchForAttributeTypeByTokenIds() {
      final int EXPECTED_RESULTS = 2;
      SearchResult results =
         createClient.createQueryBuilder(COMMON).andIds(SystemUser.OseeSystem, SystemUser.Anonymous).getSearchResult(
            RequestType.IDS);
      assertEquals(EXPECTED_RESULTS, results.getTotal());
   }

   @Test
   public void searchForArtifactByGuid() {
      final int EXPECTED_RESULTS = 1;
      SearchResult results = createClient.createQueryBuilder(COMMON).andGuids(GUID1).getSearchResult(RequestType.IDS);
      assertEquals(EXPECTED_RESULTS, results.getTotal());
   }

   @Test
   public void searchForArtifactByGuids() {
      final int EXPECTED_RESULTS = 2;
      SearchResult results =
         createClient.createQueryBuilder(COMMON).andGuids(GUID1, GUID2).getSearchResult(RequestType.IDS);
      assertEquals(EXPECTED_RESULTS, results.getTotal());
   }

   @Test
   public void searchForArtifactByLocalId() {
      final int EXPECTED_RESULTS = 1;
      SearchResult results =
         createClient.createQueryBuilder(COMMON).andLocalId(CoreArtifactTokens.UserGroups).getSearchResult(
            RequestType.IDS);
      assertEquals(EXPECTED_RESULTS, results.getTotal());
   }

   @Test
   public void searchForArtifactByName() {
      final int EXPECTED_RESULTS = 1;
      SearchResult results =
         createClient.createQueryBuilder(COMMON).andNameEquals("Joe Smith").getSearchResult(RequestType.IDS);
      assertEquals(EXPECTED_RESULTS, results.getTotal());
   }

   @Test
   public void searchForArtifactWithActionInName() {
      final int EXPECTED_RESULTS = 45;
      SearchResult results = createClient.createQueryBuilder(COMMON).and(CoreAttributeTypes.Name, "SAW",
         QueryOption.CASE__IGNORE, QueryOption.TOKEN_MATCH_ORDER__MATCH, QueryOption.TOKEN_DELIMITER__ANY,
         QueryOption.TOKEN_COUNT__IGNORE).getSearchResult(RequestType.IDS);
      assertEquals(EXPECTED_RESULTS, results.getTotal());
   }

   @Test
   public void searchForArtifactType() {
      SearchResult results =
         createClient.createQueryBuilder(SAW_Bld_1).andTypeEquals(Folder).getSearchResult(RequestType.IDS);
      assertTrue(results.getTotal() > 8);
   }

   @Test
   public void searchForArtifactTypes() {
      SearchResult results = createClient.createQueryBuilder(SAW_Bld_1).andTypeEquals(GeneralData, GeneralDocument,
         SoftwareRequirement).getSearchResult(RequestType.IDS);
      assertTrue(results.getTotal() > 24);
   }

   @Test
   public void searchForArtifactTypesIncludeTypeInheritance() {
      SearchResult results = createClient.createQueryBuilder(SAW_Bld_1).andIsOfType(GeneralData, GeneralDocument,
         Requirement).getSearchResult(RequestType.IDS);
      assertTrue(results.getTotal() > 150);
   }

   @Test
   public void searchForExistenceOfAttributeType() {
      final int EXPECTED_RESULTS = 28;
      SearchResult results = createClient.createQueryBuilder(COMMON).andExists(Active).getSearchResult(RequestType.IDS);
      assertEquals(EXPECTED_RESULTS, results.getTotal());
   }

   @Test
   public void searchForExistenceOfAttributeTypeIncludeDeleted() {
      final int EXPECTED_RESULTS = 28;
      SearchResult results =
         createClient.createQueryBuilder(COMMON).andExists(Active).includeDeleted().getSearchResult(RequestType.IDS);
      assertEquals(EXPECTED_RESULTS, results.getTotal());
   }

   @Test
   public void searchForExistenceOfAttributeTypes() {
      final int EXPECTED_RESULTS = 28;
      SearchResult results =
         createClient.createQueryBuilder(COMMON).andExists(Active, AccessContextId).getSearchResult(RequestType.IDS);
      assertEquals(EXPECTED_RESULTS, results.getTotal());
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
      createClient.executeScript(script, properties, false, MediaType.APPLICATION_JSON_TYPE, writer);

      assertTrue(normalize(writer.toString()).contains("'value' : 'User Groups'"));
   }

   private String normalize(String value) {
      value = value.replaceAll("\r\n", "\n");
      value = value.replaceAll("\"", "'");
      return value;
   }
}
