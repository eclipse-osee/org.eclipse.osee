/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.client.integration.tests.integration.orcs.rest;

import static org.eclipse.osee.client.demo.DemoChoice.OSEE_CLIENT_DEMO;
import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.eclipse.osee.client.demo.DemoChoice;
import org.eclipse.osee.client.test.framework.OseeClientIntegrationRule;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.util.OsgiUtil;
import org.eclipse.osee.orcs.rest.client.OseeClient;
import org.eclipse.osee.orcs.rest.model.ArtifactEndpoint;
import org.eclipse.osee.orcs.rest.model.AttributeEndpoint;
import org.eclipse.osee.orcs.rest.model.search.artifact.Predicate;
import org.eclipse.osee.orcs.rest.model.search.artifact.RequestType;
import org.eclipse.osee.orcs.rest.model.search.artifact.SearchMethod;
import org.eclipse.osee.orcs.rest.model.search.artifact.SearchRequest;
import org.eclipse.osee.orcs.rest.model.search.artifact.SearchResponse;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Ryan D. Brooks
 */
public class ArtifactEndpointTest {

   @Rule
   public OseeClientIntegrationRule integration = new OseeClientIntegrationRule(OSEE_CLIENT_DEMO);

   private static ArtifactEndpoint artifactEndpoint;

   @BeforeClass
   public static void testSetup() {
      OseeClient oseeclient = OsgiUtil.getService(DemoChoice.class, OseeClient.class);
      artifactEndpoint = oseeclient.getArtifactEndpoint(COMMON);
   }

   @Test
   public void testSearchWithMatrixParams() {
      ArtifactToken root = CoreArtifactTokens.DefaultHierarchyRoot;

      List<String> emptyStringList = Collections.emptyList();
      List<String> artifactTypeList = Arrays.asList(root.getArtifactTypeId().getIdString());
      Predicate predicate = new Predicate(SearchMethod.TYPE_EQUALS, emptyStringList, artifactTypeList);
      SearchRequest params = new SearchRequest(COMMON, Arrays.asList(predicate), RequestType.IDS, 0, false);
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
      ArtifactId artifact = CoreArtifactTokens.OseeTypesAndAccessFolder;
      String artifactHtml = artifactEndpoint.getArtifactAsHtml(artifact);
      boolean isPopulated = artifactHtml.contains("Name:");
      Assert.assertNotNull(artifactHtml);
      Assert.assertTrue(isPopulated);
   }

   @Test
   public void getArtifactToken() {
      ArtifactId artifact = CoreArtifactTokens.OseeTypesAndAccessFolder;
      ArtifactToken userToken = artifactEndpoint.getArtifactToken(artifact);
      Assert.assertNotNull(userToken);
   }

   @Test
   public void getAttributes() {
      ArtifactId artifact = CoreArtifactTokens.OseeTypesAndAccessFolder;
      AttributeEndpoint attributes = artifactEndpoint.getAttributes(artifact);
      Assert.assertNotNull(attributes);
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
      ArtifactId parentArtifact = CoreArtifactTokens.OseeTypesAndAccessFolder;
      List<String> names = Arrays.asList("TestArtifact1", "TestArtifact2", "TestArtifact3");

      List<ArtifactToken> newArtifacts =
         artifactEndpoint.createArtifacts(COMMON, CoreArtifactTypes.PlainText, parentArtifact, names);
      Assert.assertFalse(newArtifacts.isEmpty());
   }

   @Test
   public void createArtifact() {
      ArtifactId parentArtifact = CoreArtifactTokens.OseeTypesAndAccessFolder;
      String name = "TestArtifactA";
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
   public void setSoleAttributeValue() {
      ArtifactToken artifact = createTestArtifact();

      artifactEndpoint.setSoleAttributeValue(COMMON, artifact, CoreAttributeTypes.Name, "ResetNameAttribute");

      List<ArtifactId> artifacts = artifactEndpoint.getArtifactIdsByAttribute(CoreAttributeTypes.Name,
         "ResetNameAttribute", true, CoreArtifactTypes.PlainText);
      Assert.assertFalse(artifacts.isEmpty());
   }

   private ArtifactToken createTestArtifact() {
      ArtifactId parentArtifact = CoreArtifactTokens.OseeTypesAndAccessFolder;
      String name = "TestArtifactB";
      ArtifactToken artifactToken =
         artifactEndpoint.createArtifact(COMMON, CoreArtifactTypes.PlainText, parentArtifact, name);
      return artifactToken;
   }

}