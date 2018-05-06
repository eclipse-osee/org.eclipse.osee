/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.api;

import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.AccessContextId;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.ContentUrl;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.Dictionary;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.Name;
import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_1;
import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_2;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import com.google.common.collect.Ordering;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.jdk.core.type.MatchLocation;
import org.eclipse.osee.framework.jdk.core.type.Named;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.NamedComparator;
import org.eclipse.osee.framework.jdk.core.util.SortOrder;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsBranch;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.eclipse.osee.orcs.db.mock.OrcsIntegrationByClassRule;
import org.eclipse.osee.orcs.db.mock.OseeClassDatabase;
import org.eclipse.osee.orcs.db.mock.OsgiService;
import org.eclipse.osee.orcs.search.Match;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;
import org.eclipse.osee.orcs.transaction.TransactionFactory;
import org.eclipse.osee.orcs.utility.MatchComparator;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

/**
 * @author Roberto E. Escobar
 */
public class OrcsQueryTest {

   @Rule
   public TestRule db = OrcsIntegrationByClassRule.integrationRule(this);

   private final boolean includeMatchLocationTests = false;

   @OsgiService
   private OrcsApi orcsApi;

   private OrcsBranch branchApi;
   private TransactionFactory txFactory;
   private QueryFactory factory;
   private final UserId author = SystemUser.OseeSystem;

   @Before
   public void setup() {
      factory = orcsApi.getQueryFactory();
      branchApi = orcsApi.getBranchOps();
      txFactory = orcsApi.getTransactionFactory();
   }

   @AfterClass
   public static void cleanup() throws Exception {
      OseeClassDatabase.cleanup();
   }

   @Test
   public void testAllArtifactsFromBranch() {
      QueryBuilder builder = factory.fromBranch(COMMON);
      assertEquals(245, builder.getCount());

      assertEquals(245, builder.getResults().size());
   }

   @Test
   public void testNameAttributeNotExists() {
      QueryBuilder builder = factory.fromBranch(COMMON);
      builder.andNotExists(CoreAttributeTypes.Name);
      assertEquals(0, builder.getCount());
   }

   @Test
   public void testAttributeNotExists() {
      QueryBuilder builder = factory.fromBranch(COMMON);
      builder.andNotExists(CoreAttributeTypes.Afha);
      assertTrue(builder.getCount() >= 245);
   }

   @Test
   public void testQueryByIds() {
      QueryBuilder builder = factory.fromBranch(COMMON).andId(CoreArtifactTokens.UserGroups);
      assertEquals(1, builder.getCount());

      ArtifactReadable artifact = builder.getResults().getExactlyOne();
      assertEquals(CoreArtifactTokens.UserGroups.getGuid(), artifact.getGuid());
   }

   @Test
   public void testQueryArtifactType() {
      QueryBuilder builder = factory.fromBranch(COMMON).andTypeEquals(CoreArtifactTypes.Folder);

      assertTrue(builder.getCount() >= 6);

      ResultSet<ArtifactReadable> artifacts = builder.getResults();
      assertTrue(artifacts.size() >= 6);

      checkContainsTypes(artifacts, CoreArtifactTypes.Folder);

      Collection<String> names = Lib.getNames(artifacts.getList());
      assertTrue(names.contains("Document Templates"));
      assertTrue(names.contains("User Groups"));

      if (includeMatchLocationTests) {
         ResultSet<Match<ArtifactReadable, AttributeReadable<?>>> matches = builder.getMatches();
         assertEquals(2, matches.size());

         Iterator<Match<ArtifactReadable, AttributeReadable<?>>> matchIterator = matches.iterator();
         checkMatch(matchIterator.next(), "Document Templates");
         checkMatch(matchIterator.next(), "User Groups");
      }
   }

   @Test
   public void testQueryArtifactTypeInheritance() {
      QueryBuilder builder = factory.fromBranch(SAW_Bld_1).andTypeEquals(CoreArtifactTypes.AbstractSoftwareRequirement);

      assertEquals(0, builder.getCount());

      ArtifactReadable artifact = builder.getResults().getOneOrNull();
      assertNull(artifact);

      builder = factory.fromBranch(SAW_Bld_1).andIsOfType(CoreArtifactTypes.AbstractSoftwareRequirement);

      assertEquals(24, builder.getCount());

      ResultSet<ArtifactReadable> artifacts = builder.getResults();
      assertEquals(24, artifacts.size());

      checkContainsTypes(artifacts, CoreArtifactTypes.SoftwareRequirement);
   }

   @Test
   public void testQueryArtifactTypesMatch() {
      QueryBuilder builder = factory.fromBranch(COMMON);
      builder.andTypeEquals(CoreArtifactTypes.OseeTypeDefinition, CoreArtifactTypes.Folder);

      assertTrue(builder.getCount() >= 14);

      ResultSet<ArtifactReadable> artifacts = builder.getResults();
      assertTrue(artifacts.size() >= 14);

      checkContainsTypes(artifacts, CoreArtifactTypes.OseeTypeDefinition, CoreArtifactTypes.Folder);

      Set<String> names = new HashSet<>();
      for (ArtifactReadable art : artifacts) {
         names.add(art.getName());
      }
      assertTrue("Missing \"Document Templates\"", names.contains("Document Templates"));
      assertTrue("Missing \"User Groups\"", names.contains("User Groups"));

      assertTrue("Missing \"org.eclipse.osee.client.demo.OseeTypes_ClientDemo\"",
         names.contains("org.eclipse.osee.client.demo.OseeTypes_ClientDemo"));
      assertTrue("Missing \"org.eclipse.osee.framework.skynet.core.OseeTypes_Framework\"",
         names.contains("org.eclipse.osee.framework.skynet.core.OseeTypes_Framework"));
      assertTrue("Missing \"org.eclipse.osee.ote.define.OseeTypesOTE\"",
         names.contains("org.eclipse.osee.ote.define.OseeTypesOTE"));
   }

   @Test
   public void testQueryAttributeValue() {
      QueryBuilder builder = factory.fromBranch(COMMON);
      builder.and(CoreAttributeTypes.Name, "User Groups");

      assertEquals(1, builder.getCount());

      ArtifactReadable artifact = builder.getResults().getExactlyOne();

      assertEquals("User Groups", artifact.getName());
      assertEquals(CoreArtifactTypes.Folder, artifact.getArtifactTypeId());

      if (includeMatchLocationTests) {
         Match<ArtifactReadable, AttributeReadable<?>> result = builder.getMatches().getExactlyOne();

         assertEquals(artifact, result.getItem());
         checkMatch(result, "User Groups", CoreAttributeTypes.Name);

         AttributeReadable<?> attr = result.getElements().iterator().next();
         List<MatchLocation> location = result.getLocation(attr);
         assertEquals(1, location.size());

         MatchLocation loc1 = location.iterator().next();
         assertEquals(0, loc1.getStartPosition());
         assertEquals("User Groups".length(), loc1.getEndPosition());
      }
   }

   @Test
   public void testQueryMultipleArtifactType() {
      QueryBuilder builder = factory.fromBranch(COMMON);
      builder.andTypeEquals(CoreArtifactTypes.AccessControlModel, CoreArtifactTypes.GlobalPreferences);

      assertEquals(2, builder.getCount());
      ResultSet<ArtifactReadable> artifacts = builder.getResults();
      assertEquals(2, artifacts.size());

      checkContainsTypes(artifacts, CoreArtifactTypes.AccessControlModel, CoreArtifactTypes.GlobalPreferences);

      Iterator<ArtifactReadable> iterator = sort(artifacts);
      assertEquals("Framework Access Model", iterator.next().getName());
      assertEquals("Global Preferences", iterator.next().getName());
   }

   @Test
   public void testQueryMultipleAttributeExistsType() {
      QueryBuilder builder = factory.fromBranch(COMMON);
      builder.andExists(Arrays.asList(AccessContextId, Dictionary));

      assertEquals(0, builder.getCount());
      ResultSet<ArtifactReadable> artifacts = builder.getResults();
      assertEquals(0, artifacts.size());
   }

   @Test
   public void testQueryMultipleAttributeNotExistsType() {
      QueryBuilder builder = factory.fromBranch(COMMON);
      builder.andNotExists(Arrays.asList(ContentUrl, Name));

      assertTrue(builder.getCount() >= 245);
      ResultSet<ArtifactReadable> artifacts = builder.getResults();
      assertTrue(artifacts.size() >= 245);

      Collection<String> names = Lib.getNames(artifacts.getList());

      for (String name : Arrays.asList("Alex Kay", "Anonymous", "Boot Strap", "Default Hierarchy Root",
         "Document Templates", "Everyone", "Framework Access Model", "Global Preferences", "Inactive Steve",
         DemoUsers.Jason_Michael.getName(), "Joe Smith", "Kay Jones", "OSEE System", "OseeAdmin", "PREVIEW_ALL",
         "PREVIEW_ALL_RECURSE", "Root Artifact", "UnAssigned", "User Groups", "Word Edit Template",
         "Word Edit Template", "XViewer Global Customization", "org.eclipse.osee.client.demo.OseeTypes_ClientDemo",
         "org.eclipse.osee.framework.skynet.core.OseeTypes_Framework", "org.eclipse.osee.ote.define.OseeTypesOTE")) {
         assertTrue("Missing expected artifact named [" + name + "]", names.contains(name));
      }
   }

   @Test
   public void testQueryArtifactTypeAndNameValue() {
      QueryBuilder builder = factory.fromBranch(SAW_Bld_1);
      builder.and(CoreAttributeTypes.Name, "Requirements", QueryOption.TOKEN_COUNT__IGNORE);

      assertEquals(7, builder.getCount());
      ResultSet<ArtifactReadable> artifacts = builder.getResults();
      assertEquals(7, artifacts.size());
      checkContainsTypes(artifacts, CoreArtifactTypes.Folder, CoreArtifactTypes.SubsystemRequirementMSWord,
         CoreArtifactTypes.SystemRequirementMSWord);

      builder.andTypeEquals(CoreArtifactTypes.Folder);
      assertEquals(4, builder.getCount());

      ResultSet<ArtifactReadable> folders = builder.getResults();
      assertEquals(4, folders.size());
      Iterator<ArtifactReadable> folderIterator = sort(folders);
      assertEquals("Hardware Requirements", folderIterator.next().getName());
      assertEquals("Software Requirements", folderIterator.next().getName());
      assertEquals("Subsystem Requirements", folderIterator.next().getName());
      assertEquals("System Requirements", folderIterator.next().getName());
      checkContainsTypes(folders, CoreArtifactTypes.Folder);

      //////////////////////
      QueryBuilder builder1 = factory.fromBranch(SAW_Bld_1);
      builder1.and(CoreAttributeTypes.Name, "Requirements", QueryOption.TOKEN_COUNT__IGNORE);
      builder1.andTypeEquals(CoreArtifactTypes.SubsystemRequirementMSWord);
      assertEquals(1, builder1.getCount());
      ResultSet<ArtifactReadable> subSystemReqs = builder1.getResults();
      assertEquals(1, subSystemReqs.size());
      assertEquals("Subsystem Requirements", subSystemReqs.iterator().next().getName());
      checkContainsTypes(subSystemReqs, CoreArtifactTypes.SubsystemRequirementMSWord);

      //////////////////////
      QueryBuilder builder2 = factory.fromBranch(SAW_Bld_1);
      builder2.and(CoreAttributeTypes.Name, "Requirements", QueryOption.TOKEN_COUNT__IGNORE);
      builder2.andIsOfType(CoreArtifactTypes.Requirement);
      assertEquals(3, builder2.getCount());

      ResultSet<ArtifactReadable> requirements = builder2.getResults();
      assertEquals(3, requirements.size());
      Iterator<ArtifactReadable> reqIterator = sort(requirements);
      assertEquals("Performance Requirements", reqIterator.next().getName());
      assertEquals("Safety Requirements", reqIterator.next().getName());
      assertEquals("Subsystem Requirements", reqIterator.next().getName());
      checkContainsTypes(subSystemReqs, CoreArtifactTypes.SubsystemRequirementMSWord,
         CoreArtifactTypes.SystemRequirementMSWord);
   }

   @Test
   public void testQueryRequirementsAsLocalIds() {
      QueryBuilder builder = factory.fromBranch(SAW_Bld_1);
      builder.and(CoreAttributeTypes.Name, "REQUIREMENTS", QueryOption.CASE__IGNORE,
         QueryOption.TOKEN_MATCH_ORDER__MATCH, QueryOption.TOKEN_DELIMITER__ANY, QueryOption.TOKEN_COUNT__IGNORE);

      ResultSet<? extends ArtifactId> results = builder.getResultsIds();
      assertEquals(7, results.size());
      assertEquals(7, builder.getCount());
   }

   @Test
   public void testQueryAttributeKeyword() {
      QueryBuilder builder = factory.fromBranch(SAW_Bld_1);
      builder.and(CoreAttributeTypes.Name, "REQUIREMENTS", QueryOption.CONTAINS_MATCH_OPTIONS);

      assertEquals(7, builder.getCount());
      ResultSet<ArtifactReadable> requirements = builder.getResults();
      assertEquals(7, requirements.size());
      checkContainsTypes(requirements, CoreArtifactTypes.Folder, CoreArtifactTypes.SubsystemRequirementMSWord,
         CoreArtifactTypes.SystemRequirementMSWord);
      Iterator<ArtifactReadable> reqIterator = sort(requirements);
      assertEquals("Hardware Requirements", reqIterator.next().getName());
      assertEquals("Performance Requirements", reqIterator.next().getName());
      assertEquals("Safety Requirements", reqIterator.next().getName());
      assertEquals("Software Requirements", reqIterator.next().getName());
      assertEquals("Subsystem Requirements", reqIterator.next().getName());
      assertEquals("Subsystem Requirements", reqIterator.next().getName());
      assertEquals("System Requirements", reqIterator.next().getName());

      ResultSet<Match<ArtifactReadable, AttributeReadable<?>>> matches = builder.getMatches();
      assertEquals(7, matches.size());

      // @formatter:off
      Iterator<Match<ArtifactReadable, AttributeReadable<?>>> matchIterator = sortMatch(matches);
      checkMatchSingleAttribute(matchIterator.next(), "Hardware Requirements", CoreAttributeTypes.Name, "Requirements");
      checkMatchSingleAttribute(matchIterator.next(), "Performance Requirements", CoreAttributeTypes.Name, "Requirements");
      checkMatchSingleAttribute(matchIterator.next(), "Safety Requirements", CoreAttributeTypes.Name, "Requirements");
      checkMatchSingleAttribute(matchIterator.next(), "Software Requirements", CoreAttributeTypes.Name, "Requirements");
      checkMatchSingleAttribute(matchIterator.next(), "Subsystem Requirements", CoreAttributeTypes.Name, "Requirements");
      checkMatchSingleAttribute(matchIterator.next(), "Subsystem Requirements", CoreAttributeTypes.Name, "Requirements");
      checkMatchSingleAttribute(matchIterator.next(), "System Requirements", CoreAttributeTypes.Name, "Requirements");
      // @formatter:on

      QueryBuilder builder1 = factory.fromBranch(SAW_Bld_1);
      builder1.and(CoreAttributeTypes.Name, "REQUIREMENTS", QueryOption.TOKEN_DELIMITER__ANY, QueryOption.CASE__MATCH);
      assertEquals(0, builder1.getCount());
   }

   @Test
   public void testRelatedToTest() {
      QueryBuilder builder1 = factory.fromBranch(SAW_Bld_1);
      builder1.and(CoreAttributeTypes.Name, "Frame Synchronization");
      assertEquals("Frame Synchronization", builder1.getResults().getExactlyOne().getName());

      QueryBuilder builder2 = factory.fromBranch(SAW_Bld_1);
      builder2.andRelatedTo(CoreRelationTypes.Default_Hierarchical__Child, builder1.getResults().getExactlyOne());
      assertEquals("Video processing", builder2.getResults().getExactlyOne().getName());

      QueryBuilder builder3 = factory.fromBranch(SAW_Bld_1);
      builder3.andRelatedTo(CoreRelationTypes.Default_Hierarchical__Child, builder2.getResults().getExactlyOne());
      assertEquals("Subsystem Requirements", builder3.getResults().getExactlyOne().getName());
   }

   @Test
   public void testAndNameEquals() throws Exception {
      // This test sets up two folders, the name of the first has the name of the second in it
      // The goal is to make sure query.AndNameEquals doesn't return a match unless it matches exactly
      Branch branch = setupNameEqualsArtifacts();
      try {
         QueryBuilder builder = factory.fromBranch(branch);
         builder.andNameEquals("Folder");
         ResultSet<ArtifactReadable> artifacts = builder.getResults();
         assertEquals(1, artifacts.size());
         assertEquals("Folder", artifacts.getExactlyOne().getName());
      } finally {
         branchApi.purgeBranch(branch, true).call();
      }
   }

   @Test
   public void testFollowRelationType1() throws Exception {
      QueryBuilder builder = factory.fromBranch(SAW_Bld_1) //
         .andTypeEquals(CoreArtifactTypes.RootArtifact)//
         .followRelation(CoreRelationTypes.Default_Hierarchical__Child);

      ResultSet<ArtifactReadable> results = builder.getResults();
      assertEquals(9, results.size());

      Iterator<String> iterator = getNames(results).iterator();
      assertEquals("Hardware Requirements", iterator.next());
      assertEquals("Integration Tests", iterator.next());
      assertEquals("Product Line", iterator.next());
      assertEquals("SAW Product Decomposition", iterator.next());
      assertEquals("Software Requirements", iterator.next());
      assertEquals("Subsystem Requirements", iterator.next());
      assertEquals("System Requirements", iterator.next());
      assertEquals("Validation Tests", iterator.next());
      assertEquals("Verification Tests", iterator.next());
   }

   @Test
   public void testFollowRelationType2() throws Exception {
      QueryBuilder builder = factory.fromBranch(SAW_Bld_1) //
         .andTypeEquals(CoreArtifactTypes.RootArtifact)//
         .followRelation(CoreRelationTypes.Default_Hierarchical__Child)//
         .andTypeEquals(CoreArtifactTypes.Component);

      ResultSet<ArtifactReadable> results = builder.getResults();
      assertEquals(1, results.size());

      assertEquals("SAW Product Decomposition", results.getExactlyOne().getName());
   }

   @Test
   public void testFollowRelationType3() throws Exception {
      QueryBuilder builder = factory.fromBranch(SAW_Bld_1) //
         .and(CoreAttributeTypes.Name, "collaboration", QueryOption.TOKEN_MATCH_ORDER__ANY, QueryOption.CASE__IGNORE)//
         .followRelation(CoreRelationTypes.Default_Hierarchical__Child)//
         .and(CoreAttributeTypes.Name, "object", QueryOption.CONTAINS_MATCH_OPTIONS);

      ResultSet<ArtifactReadable> results = builder.getResults();
      assertEquals(1, results.size());

      assertEquals("Robot Object", results.getExactlyOne().getName());
   }

   @Test
   public void testFollowRelationTypeHistorical() throws Exception {
      QueryBuilder query = factory.fromBranch(SAW_Bld_2) //
         .andNameEquals("Robot API") //
         .andTypeEquals(CoreArtifactTypes.SoftwareRequirement)//
         .followRelation(CoreRelationTypes.Default_Hierarchical__Child)//
         .and(CoreAttributeTypes.Name, "Robot", QueryOption.CONTAINS_MATCH_OPTIONS);

      ResultSet<ArtifactReadable> results = query.getResults();
      assertEquals(2, results.size());

      Iterator<String> iterator = getNames(results).iterator();
      assertEquals("Robot Interfaces", iterator.next());
      assertEquals("Robot collaboration", iterator.next());

      // Add a child
      ArtifactReadable parent = results.iterator().next().getParent();
      TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(SAW_Bld_2, author, "FollowTest");
      ArtifactId child = tx.createArtifact(CoreArtifactTypes.SoftwareRequirement, "Dummy Robot");
      tx.relate(parent, CoreRelationTypes.Default_Hierarchical__Child, child);
      TransactionId commitTx = tx.commit();
      TransactionId headTx = commitTx;

      results = query.getResults();
      assertEquals(3, results.size());

      iterator = getNames(results).iterator();
      assertEquals("Dummy Robot", iterator.next());
      assertEquals("Robot Interfaces", iterator.next());
      assertEquals("Robot collaboration", iterator.next());

      query.fromTransaction(TransactionId.valueOf(headTx.getId() - 1));
      results = query.getResults();
      assertEquals(2, results.size());

      iterator = getNames(results).iterator();
      assertEquals("Robot Interfaces", iterator.next());
      assertEquals("Robot collaboration", iterator.next());
   }

   @Test
   public void testRelatedTo() {
      // do a query on branch
      ArtifactReadable robotApi = factory.fromBranch(SAW_Bld_2) //
         .andNameEquals("Robot API") //
         .andTypeEquals(CoreArtifactTypes.SoftwareRequirement).getResults().getExactlyOne();

      // create a tx on branch
      TransactionBuilder tx = txFactory.createTransaction(SAW_Bld_2, author, "Simple Tx");
      tx.createArtifact(CoreArtifactTypes.Folder, "Just a Folder");
      tx.commit();

      // do another query on branch
      ArtifactReadable robotInt = factory.fromBranch(SAW_Bld_2) //
         .andNameEquals("Robot Interfaces").getResults().getExactlyOne();

      // see if artifact from query 1 is related to artifact from query 2
      Assert.assertTrue(robotApi.areRelated(CoreRelationTypes.Default_Hierarchical__Child, robotInt));
   }

   @Test
   public void testMultipleTxs() {
      // do a query on branch
      ArtifactReadable robotApi = factory.fromBranch(SAW_Bld_2) //
         .andNameEquals("Robot API") //
         .andTypeEquals(CoreArtifactTypes.SoftwareRequirement).getResults().getExactlyOne();

      // create a tx on branch
      TransactionBuilder tx1 = txFactory.createTransaction(SAW_Bld_2, author, "Simple Tx1");
      TransactionBuilder tx2 = txFactory.createTransaction(SAW_Bld_2, author, "Simple Tx2");

      ArtifactId folder = tx1.createArtifact(CoreArtifactTypes.Folder, "Just a Folder");
      tx1.commit();

      tx2.addChild(robotApi, folder);

      tx2.commit();

      factory.fromBranch(SAW_Bld_2).andNameEquals("Robot Interfaces").getResults().getExactlyOne();

      ArtifactReadable folderArt = factory.fromBranch(SAW_Bld_2).andId(folder).getResults().getExactlyOne();
      // robotApi should be related to folder
      Assert.assertTrue(robotApi.areRelated(CoreRelationTypes.Default_Hierarchical__Child, folderArt));
   }

   private Set<String> getNames(ResultSet<ArtifactReadable> results) {
      Set<String> names = new TreeSet<>();
      for (ArtifactReadable art : results) {
         names.add(art.getName());
      }
      return names;
   }

   private Branch setupNameEqualsArtifacts() throws Exception {
      IOseeBranch branchToken = IOseeBranch.create("TestAndNameEquals");
      Branch branch = branchApi.createTopLevelBranch(branchToken, author).call();
      TransactionBuilder tx = txFactory.createTransaction(branch, author, "add folders");
      tx.createArtifact(CoreArtifactTypes.Folder, "First Folder");
      tx.createArtifact(CoreArtifactTypes.Folder, "Folder");
      tx.commit();
      return branch;
   }

   private static void checkContainsTypes(Iterable<ArtifactReadable> arts, IArtifactType... types) {
      List<IArtifactType> expected = Arrays.asList(types);
      for (ArtifactReadable art : arts) {
         assertTrue(String.format("artifact type [%s] not found", art.getArtifactType()),
            expected.contains(art.getArtifactTypeId()));
      }
   }

   private static void checkMatchSingleAttribute(Match<ArtifactReadable, AttributeReadable<?>> match, String artName, AttributeTypeId types, String matched) {
      assertEquals(artName, match.getItem().getName());

      AttributeReadable<?> attribute = match.getElements().iterator().next();
      assertEquals(types, attribute.getAttributeType());
      List<MatchLocation> locations = match.getLocation(attribute);
      assertEquals(1, locations.size());

      MatchLocation location = locations.get(0);
      String value = String.valueOf(attribute.getValue());
      assertEquals(matched, value.substring(location.getStartPosition() - 1, location.getEndPosition()));
   }

   private static void checkMatch(Match<ArtifactReadable, AttributeReadable<?>> match, String artName, AttributeTypeId... types) {
      assertEquals(artName, match.getItem().getName());
      if (types.length > 0) {
         assertEquals(types.length, match.getElements().size());

         Iterator<AttributeReadable<?>> iterator = match.getElements().iterator();
         for (int index = 0; index < types.length; index++) {
            assertEquals(types[index], iterator.next().getAttributeType());
         }
      }
   }

   private static Iterator<Match<ArtifactReadable, AttributeReadable<?>>> sortMatch(Iterable<Match<ArtifactReadable, AttributeReadable<?>>> iterable) {
      Ordering<Match<ArtifactReadable, AttributeReadable<?>>> from =
         Ordering.from(new MatchComparator(SortOrder.ASCENDING));
      return from.sortedCopy(iterable).iterator();
   }

   @SuppressWarnings("unchecked")
   private static <T extends Named> Iterator<T> sort(Iterable<T> iterable) {
      Ordering<T> from = (Ordering<T>) Ordering.from(new NamedComparator(SortOrder.ASCENDING));
      return from.sortedCopy(iterable).iterator();
   }
}
