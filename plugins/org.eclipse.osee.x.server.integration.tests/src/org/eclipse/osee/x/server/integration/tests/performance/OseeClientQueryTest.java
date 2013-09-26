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
import static org.junit.Assert.assertEquals;
import org.databene.contiperf.PerfTest;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.Identity;
import org.eclipse.osee.framework.core.enums.CaseType;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.MatchTokenCountType;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.enums.TokenDelimiterMatch;
import org.eclipse.osee.framework.core.enums.TokenOrderType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.rest.client.OseeClient;
import org.eclipse.osee.orcs.rest.model.search.SearchResult;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;

@PerfTest(threads = 2, invocations = 100)
public class OseeClientQueryTest {

   // Randomly selected guids
   private static final String GUID1 = "ABNRvbZxXHICYklfslwA";
   private static final String GUID2 = "D2hZ1_MwKRsqTILDKawA";

   private static final IOseeBranch SAW_1 = new IOseeBranch() {

      @Override
      public String getGuid() {
         return "AyH_f2sSKy3l07fIvAAA";
      }

      @Override
      public boolean matches(Identity<?>... identities) {
         return false;
      }

      @Override
      public String getName() {
         return "SAW_Bld_1";
      }
   };

   @Rule
   public MethodRule performanceRule = IntegrationUtil.createPerformanceRule();

   private static OseeClient createClient;

   @BeforeClass
   public static void testSetup() throws OseeCoreException {
      createClient = IntegrationUtil.createClient();

      // Establish initial connection to the db using this random query
      createClient.createQueryBuilder(COMMON).andIds(SystemUser.OseeSystem).getSearchResult();
   }

   @Test
   public void searchForAttributeTypeByTokenId() throws OseeCoreException {
      final int EXPECTED_RESULTS = 1;
      SearchResult results = createClient.createQueryBuilder(COMMON).andIds(SystemUser.OseeSystem).getSearchResult();
      assertEquals(EXPECTED_RESULTS, results.getTotal());
   }

   @Test
   public void searchForAttributeTypeByTokenIds() throws OseeCoreException {
      final int EXPECTED_RESULTS = 2;
      SearchResult results =
         createClient.createQueryBuilder(COMMON).andIds(SystemUser.OseeSystem, SystemUser.Guest).getSearchResult();
      assertEquals(EXPECTED_RESULTS, results.getTotal());
   }

   @Test
   public void searchForArtifactByGuid() throws OseeCoreException {
      final int EXPECTED_RESULTS = 1;
      SearchResult results = createClient.createQueryBuilder(COMMON).andGuids(GUID1).getSearchResult();
      assertEquals(EXPECTED_RESULTS, results.getTotal());
   }

   @Test
   public void searchForArtifactByGuids() throws OseeCoreException {
      final int EXPECTED_RESULTS = 2;
      SearchResult results = createClient.createQueryBuilder(COMMON).andGuids(GUID1, GUID2).getSearchResult();
      assertEquals(EXPECTED_RESULTS, results.getTotal());
   }

   @Test
   public void searchForArtifactByGuidsExcludeCache() throws OseeCoreException {
      final int EXPECTED_RESULTS = 2;
      SearchResult results =
         createClient.createQueryBuilder(COMMON).andGuids(GUID1, GUID2).excludeCache().getSearchResult();
      assertEquals(EXPECTED_RESULTS, results.getTotal());
   }

   @Test
   public void searchForArtifactByGuidsIncludeCache() throws OseeCoreException {
      final int EXPECTED_RESULTS = 2;
      SearchResult results =
         createClient.createQueryBuilder(COMMON).andGuids(GUID1, GUID2).includeCache().getSearchResult();
      assertEquals(EXPECTED_RESULTS, results.getTotal());
   }

   @Test
   public void searchForArtifactByLocalId() throws OseeCoreException {
      final int EXPECTED_RESULTS = 1;
      SearchResult results = createClient.createQueryBuilder(COMMON).andLocalId(9).getSearchResult();
      assertEquals(EXPECTED_RESULTS, results.getTotal());
   }

   @Test
   public void searchForArtifactByLocalIds() throws OseeCoreException {
      final int EXPECTED_RESULTS = 2;
      SearchResult results = createClient.createQueryBuilder(COMMON).andLocalId(19, 9).getSearchResult();
      assertEquals(EXPECTED_RESULTS, results.getTotal());
   }

   @Test
   public void searchForArtifactByName() throws OseeCoreException {
      final int EXPECTED_RESULTS = 1;
      SearchResult results = createClient.createQueryBuilder(COMMON).andNameEquals("Joe Smith").getSearchResult();
      assertEquals(EXPECTED_RESULTS, results.getTotal());
   }

   @Test
   public void searchForArtifactWithActionInName() throws OseeCoreException {
      final int EXPECTED_RESULTS = 43;
      SearchResult results =
         createClient.createQueryBuilder(COMMON).and(CoreAttributeTypes.Name, "SAW", CaseType.IGNORE_CASE,
            TokenOrderType.MATCH_ORDER, TokenDelimiterMatch.ANY, MatchTokenCountType.IGNORE_TOKEN_COUNT).getSearchResult();
      assertEquals(EXPECTED_RESULTS, results.getTotal());
   }

   @Test
   public void searchForArtifactType() throws OseeCoreException {
      final int EXPECTED_RESULTS = 7;
      SearchResult results = createClient.createQueryBuilder(SAW_1).andTypeEquals(Folder).getSearchResult();
      assertEquals(EXPECTED_RESULTS, results.getTotal());
   }

   @Test
   public void searchForArtifactTypes() throws OseeCoreException {
      final int EXPECTED_RESULTS = 24;
      SearchResult results =
         createClient.createQueryBuilder(SAW_1).andTypeEquals(GeneralData, GeneralDocument, SoftwareRequirement).getSearchResult();
      assertEquals(EXPECTED_RESULTS, results.getTotal());
   }

   @Test
   public void searchForArtifactTypesIncludeTypeInheritance() throws OseeCoreException {
      final int EXPECTED_RESULTS = 150;
      SearchResult results =
         createClient.createQueryBuilder(SAW_1).andIsOfType(GeneralData, GeneralDocument, Requirement).getSearchResult();
      assertEquals(EXPECTED_RESULTS, results.getTotal());
   }

   @Test
   public void searchForExistenceOfAttributeType() throws OseeCoreException {
      final int EXPECTED_RESULTS = 28;
      SearchResult results = createClient.createQueryBuilder(COMMON).andExists(Active).getSearchResult();
      assertEquals(EXPECTED_RESULTS, results.getTotal());
   }

   @Test
   public void searchForExistenceOfAttributeTypeIncludeDeleted() throws OseeCoreException {
      final int EXPECTED_RESULTS = 28;
      SearchResult results =
         createClient.createQueryBuilder(COMMON).andExists(Active).includeDeleted().getSearchResult();
      assertEquals(EXPECTED_RESULTS, results.getTotal());
   }

   @Test
   public void searchForExistenceOfAttributeTypes() throws OseeCoreException {
      final int EXPECTED_RESULTS = 28;
      SearchResult results =
         createClient.createQueryBuilder(COMMON).andExists(Active, AccessContextId).getSearchResult();
      assertEquals(EXPECTED_RESULTS, results.getTotal());
   }

}
