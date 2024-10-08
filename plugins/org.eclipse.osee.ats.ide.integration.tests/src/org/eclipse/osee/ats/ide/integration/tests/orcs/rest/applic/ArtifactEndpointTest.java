/*********************************************************************
 * Copyright (c) 2017 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.orcs.rest.applic;

import static org.eclipse.osee.framework.core.enums.CoreArtifactTokens.DefaultHierarchyRoot;
import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.util.ServiceUtil;
import org.eclipse.osee.client.test.framework.NotProductionDataStoreRule;
import org.eclipse.osee.framework.core.JaxRsApi;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable.ArtifactReadableImpl;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.core.util.ArtifactSearchOptions;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.orcs.rest.model.ApplicabilityEndpoint;
import org.eclipse.osee.orcs.rest.model.ArtifactEndpoint;
import org.eclipse.osee.orcs.rest.model.AttributeEndpoint;
import org.eclipse.osee.orcs.rest.model.search.artifact.Predicate;
import org.eclipse.osee.orcs.rest.model.search.artifact.RequestType;
import org.eclipse.osee.orcs.rest.model.search.artifact.SearchMethod;
import org.eclipse.osee.orcs.rest.model.search.artifact.SearchRequest;
import org.eclipse.osee.orcs.rest.model.search.artifact.SearchResponse;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Ryan D. Brooks
 */
public class ArtifactEndpointTest {

   private final JaxRsApi jaxRsApi = AtsApiService.get().jaxRsApi();

   @Rule
   public NotProductionDataStoreRule notProduction = new NotProductionDataStoreRule();

   private static ArtifactEndpoint artifactEndpoint;
   private static ArtifactEndpoint workingBranchArtifactEndpoint;
   private static ApplicabilityEndpoint applicEndpoint;

   @BeforeClass
   public static void testSetup() {
      artifactEndpoint = ServiceUtil.getOseeClient().getArtifactEndpoint(COMMON);
      workingBranchArtifactEndpoint =
         ServiceUtil.getOseeClient().getArtifactEndpoint(DemoBranches.SAW_PL_Working_Branch);
      applicEndpoint = ServiceUtil.getOseeClient().getApplicabilityEndpoint(DemoBranches.SAW_PL_Working_Branch);
   }

   @AfterClass
   public static void testCleanup() {
      SkynetTransaction transaction = TransactionManager.createTransaction(COMMON, "ArtifactEndpointTest");
      List<Artifact> artifacts = ArtifactQuery.getArtifactListFromName("ArtifactEndpointTest", COMMON,
         DeletionFlag.EXCLUDE_DELETED, QueryOption.CONTAINS_MATCH_OPTIONS);
      ArtifactPersistenceManager.deleteArtifactCollection(transaction, false, new XResultData(), artifacts);
      transaction.execute();
   }

   @Test
   public void testFindArtifacts() {
      ArtifactToken newArtifact = workingBranchArtifactEndpoint.createArtifact(DemoBranches.SAW_PL_Working_Branch,
         CoreArtifactTypes.Folder, CoreArtifactTokens.DefaultHierarchyRoot, "TestFolder");

      ArtifactSearchOptions options1 = new ArtifactSearchOptions();
      List<ArtifactTypeToken> artTypes = options1.getArtTypeIds();
      artTypes.add(CoreArtifactTypes.Folder);
      List<AttributeTypeToken> attrTypes = options1.getAttrTypeIds();
      attrTypes.add(CoreAttributeTypes.Name);

      options1.setSearchString(newArtifact.getName());
      options1.setArtTypeIds(artTypes);
      options1.setAttrTypeIds(attrTypes);
      List<ArtifactId> artIds = workingBranchArtifactEndpoint.findArtifactIds(options1);
      Assert.assertEquals(1, artIds.size());

      Collection<ApplicabilityToken> appTokens = applicEndpoint.getApplicabilityTokens();
      ApplicabilityToken robotSpkrA =
         appTokens.stream().filter(appToken -> "ROBOT_SPEAKER = SPKR_A".equals(appToken.getName())).findAny().orElse(
            ApplicabilityToken.SENTINEL);

      applicEndpoint.setApplicability(robotSpkrA, Collections.singletonList(newArtifact));
      ArtifactSearchOptions options2 = new ArtifactSearchOptions();
      options2.setApplic(robotSpkrA);
      artIds = workingBranchArtifactEndpoint.findArtifactIds(options2);
      Assert.assertEquals(1, artIds.size());

      ArtifactSearchOptions options3 = new ArtifactSearchOptions();
      options3.setArtTypeIds(artTypes);
      options3.setView(ArtifactId.valueOf(applicEndpoint.getView("Product C").getId()));
      artIds = workingBranchArtifactEndpoint.findArtifactIds(options3);
      Assert.assertEquals(19, artIds.size());

      ArtifactSearchOptions options4 = new ArtifactSearchOptions();
      options4.setArtTypeIds(artTypes);
      options4.setView(ArtifactId.valueOf(applicEndpoint.getView("Product A").getId()));
      artIds = workingBranchArtifactEndpoint.findArtifactIds(options4);
      Assert.assertEquals(20, artIds.size());
   }

   @Test
   public void testSearchWithMatrixParams() {
      ArtifactToken root = CoreArtifactTokens.DefaultHierarchyRoot;

      List<String> emptyStringList = Collections.emptyList();
      List<String> artifactTypeList = Arrays.asList(root.getArtifactType().getIdString());
      Predicate predicate = new Predicate(SearchMethod.TYPE_EQUALS, emptyStringList, artifactTypeList);
      SearchRequest params =
         new SearchRequest(COMMON, Arrays.asList(predicate), RequestType.IDS, TransactionId.valueOf(0), false);
      SearchResponse response = artifactEndpoint.getSearchWithMatrixParams(params);
      List<ArtifactId> ids = response.getIds();

      Assert.assertEquals(1, ids.size());
      Assert.assertEquals(root, ids.get(0));
   }

   @Test
   public void getRootChildrenAsHtml() {
      String rootChildrenHtml = artifactEndpoint.getRootChildrenAsHtml();
      boolean isPopulated = rootChildrenHtml.contains("Name:");
      Assert.assertNotNull(rootChildrenHtml);
      Assert.assertTrue(isPopulated);
   }

   @Test
   public void getArtifactAsHtml() {
      String artifactHtml = artifactEndpoint.getArtifactAsHtml(DefaultHierarchyRoot);
      boolean isPopulated = artifactHtml.contains("Name:");
      Assert.assertTrue(isPopulated);
   }

   @Test
   public void getArtifactToken() {
      Assert.assertNotNull(artifactEndpoint.getArtifactToken(DefaultHierarchyRoot));
   }

   @Test
   public void getAttributes() {
      Assert.assertNotNull(artifactEndpoint.getAttributes(DefaultHierarchyRoot));
   }

   @Test
   public void getArtifactTokensByAttribute() {
      List<ArtifactToken> users =
         artifactEndpoint.getArtifactTokensByAttribute(CoreAttributeTypes.Email, "", true, CoreArtifactTypes.User);
      Assert.assertFalse(users.isEmpty());
      users.clear();
      users = artifactEndpoint.getArtifactTokensByAttribute(CoreAttributeTypes.Name, "OSEE System", true,
         CoreArtifactTypes.User);
      Assert.assertFalse(users.isEmpty());
      users.clear();
      users = artifactEndpoint.getArtifactTokensByAttribute(CoreAttributeTypes.Name, "", true, CoreArtifactTypes.User);
      Assert.assertTrue(users.isEmpty());
   }

   @Test
   public void getArtifactIdsByAttribute() {
      List<ArtifactId> users =
         artifactEndpoint.getArtifactIdsByAttribute(CoreAttributeTypes.Email, "", true, CoreArtifactTypes.User);
      Assert.assertFalse(users.isEmpty());
      users.clear();
      users = artifactEndpoint.getArtifactIdsByAttribute(CoreAttributeTypes.Name, "OSEE System", true,
         CoreArtifactTypes.User);
      Assert.assertFalse(users.isEmpty());
      users.clear();
      users = artifactEndpoint.getArtifactIdsByAttribute(CoreAttributeTypes.Name, "", true, CoreArtifactTypes.User);
      Assert.assertTrue(users.isEmpty());
   }

   @Test
   public void getArtifactTokensByType() {
      List<ArtifactToken> users = artifactEndpoint.getArtifactTokensByType(CoreArtifactTypes.User);
      Assert.assertFalse(users.isEmpty());
   }

   @Test
   public void getArtifactIdsByType() {
      List<ArtifactId> users = artifactEndpoint.getArtifactIdsByType(CoreArtifactTypes.User);
      Assert.assertFalse(users.isEmpty());
   }

   @Test
   public void createArtifacts() {
      ArtifactId parentArtifact = DefaultHierarchyRoot;
      List<String> names = Arrays.asList(getClass().getSimpleName() + " 1", getClass().getSimpleName() + " 2",
         getClass().getSimpleName() + " 3");

      List<ArtifactToken> newArtifacts =
         artifactEndpoint.createArtifacts(COMMON, CoreArtifactTypes.PlainText, parentArtifact, names);
      Assert.assertFalse(newArtifacts.isEmpty());
   }

   @Test
   public void createArtifact() {
      ArtifactId parentArtifact = DefaultHierarchyRoot;
      String name = getClass().getSimpleName() + " A";
      ArtifactToken artifactToken =
         artifactEndpoint.createArtifact(COMMON, CoreArtifactTypes.PlainText, parentArtifact, name);
      Assert.assertEquals(name, artifactToken.getName());
   }

   @Test
   public void deleteArtifact() {
      ArtifactToken toDelete = createTestArtifact();

      artifactEndpoint.deleteArtifact(COMMON, toDelete);

      List<ArtifactId> artifacts = artifactEndpoint.getArtifactIdsByAttribute(CoreAttributeTypes.Name, "TestUser", true,
         CoreArtifactTypes.PlainText);
      Assert.assertTrue(artifacts.isEmpty());
   }

   @Test
   public void deleteAttributesOfType() {

      ArtifactToken testArtifact = createTestArtifact();

      artifactEndpoint.setSoleAttributeValue(COMMON, testArtifact, CoreAttributeTypes.Annotation, "1234");

      AttributeEndpoint attributesBeforeDelete = artifactEndpoint.getAttributes(testArtifact);
      Assert.assertTrue(attributesBeforeDelete.getAttributes(testArtifact).stream().anyMatch(
         a -> a.getAttrTypeId().equals(CoreAttributeTypes.Annotation)));
      artifactEndpoint.deleteAttributesOfType(COMMON, ArtifactId.SENTINEL, CoreArtifactTypes.PlainText,
         CoreAttributeTypes.Annotation);
      AttributeEndpoint attributesAfterDelete = artifactEndpoint.getAttributes(testArtifact);

      Assert.assertFalse(attributesAfterDelete.getAttributes(testArtifact).stream().anyMatch(
         a -> a.getAttrTypeId().equals(CoreAttributeTypes.Annotation)));

   }

   @Test
   public void setSoleAttributeValue() {
      ArtifactToken artifact = createTestArtifact();

      artifactEndpoint.setSoleAttributeValue(COMMON, artifact, CoreAttributeTypes.Name,
         getClass().getSimpleName() + " ResetNameAttribute");

      List<ArtifactId> artifacts = artifactEndpoint.getArtifactIdsByAttribute(CoreAttributeTypes.Name,
         getClass().getSimpleName() + " ResetNameAttribute", true, CoreArtifactTypes.PlainText);
      Assert.assertFalse(artifacts.isEmpty());
   }

   private ArtifactToken createTestArtifact() {
      ArtifactId parentArtifact = DefaultHierarchyRoot;
      String name = getClass().getSimpleName() + " B";
      ArtifactToken artifactToken =
         artifactEndpoint.createArtifact(COMMON, CoreArtifactTypes.PlainText, parentArtifact, name);
      return artifactToken;
   }

   @Test
   public void getChangedArtifactTokens() {
      List<ArtifactToken> changedArtifactTokens =
         artifactEndpoint.getChangedArtifactTokens(Artifact.SENTINEL, CoreAttributeTypes.NameWord, ".*[a-z].*");
      Assert.assertFalse(changedArtifactTokens.isEmpty());
   }

   @Test
   public void testArtifactReadable() throws IOException {
      ArtifactToken newArtifact = workingBranchArtifactEndpoint.createArtifact(DemoBranches.SAW_PL_Working_Branch,
         CoreArtifactTypes.Folder, CoreArtifactTokens.DefaultHierarchyRoot, "TestFolder2");
      String branchId = DemoBranches.SAW_PL_Working_Branch.getIdString();
      String artId = newArtifact.getIdString();

      String json = jaxRsApi.newTarget("orcs/branch/" + branchId + "/artifact/" + artId + "/related/maps").request(
         MediaType.APPLICATION_JSON_TYPE).get().readEntity(String.class);

      ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      List<ArtifactReadableImpl> readables = mapper.readValue(json, new TypeReference<List<ArtifactReadableImpl>>() {
         //
      });
      Assert.assertEquals(readables.get(0).getName(), "TestFolder2");
      Assert.assertEquals(readables.get(0).getId().toString(), artId);
   }
}
