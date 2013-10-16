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

import static org.eclipse.osee.orcs.OrcsIntegrationRule.integrationRule;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.ResultSet;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.enums.CaseType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.MatchTokenCountType;
import org.eclipse.osee.framework.core.enums.Operator;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.enums.TokenDelimiterMatch;
import org.eclipse.osee.framework.core.enums.TokenOrderType;
import org.eclipse.osee.framework.core.model.ReadableBranch;
import org.eclipse.osee.framework.jdk.core.type.MatchLocation;
import org.eclipse.osee.framework.jdk.core.type.Named;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.orcs.ApplicationContext;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsBranch;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.eclipse.osee.orcs.data.HasLocalId;
import org.eclipse.osee.orcs.db.mock.OsgiService;
import org.eclipse.osee.orcs.search.Match;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;
import org.eclipse.osee.orcs.transaction.TransactionFactory;
import org.eclipse.osee.orcs.utility.MatchComparator;
import org.eclipse.osee.orcs.utility.NameComparator;
import org.eclipse.osee.orcs.utility.SortOrder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import com.google.common.collect.Ordering;

/**
 * @author Roberto E. Escobar
 */
public class OrcsQueryTest {

   @Rule
   public TestRule osgi = integrationRule(this, "osee.demo.hsql");

   private final boolean includeMatchLocationTests = false;

   @OsgiService
   private OrcsApi orcsApi;

   private OrcsBranch branchApi;
   private TransactionFactory txFactory;
   private QueryFactory factory;
   private ArtifactReadable author;

   @Before
   public void setup() {
      ApplicationContext context = null; // TODO use real application context
      factory = orcsApi.getQueryFactory(context);
      branchApi = orcsApi.getBranchOps(context);
      txFactory = orcsApi.getTransactionFactory(context);
   }

   @Test
   public void testAllArtifactsFromBranch() throws OseeCoreException {
      QueryBuilder builder = factory.fromBranch(CoreBranches.COMMON);
      assertEquals(26, builder.getCount());

      assertEquals(26, builder.getResults().size());
   }

   @Test
   public void testQueryByIds() throws OseeCoreException {
      QueryBuilder builder = factory.fromBranch(CoreBranches.COMMON).andGuid("AkA2AmNuEDDL4VolM9AA");
      assertEquals(1, builder.getCount());

      ArtifactReadable artifact = builder.getResults().getExactlyOne();
      assertEquals("AkA2AmNuEDDL4VolM9AA", artifact.getGuid());
   }

   @Test
   public void testQueryArtifactType() throws OseeCoreException {
      QueryBuilder builder = factory.fromBranch(CoreBranches.COMMON).andIsOfType(CoreArtifactTypes.Folder);

      assertEquals(2, builder.getCount());

      ResultSet<ArtifactReadable> artifacts = builder.getResults();
      assertEquals(2, artifacts.size());

      checkContainsTypes(artifacts, CoreArtifactTypes.Folder);

      Iterator<ArtifactReadable> iterator = sort(artifacts);
      assertEquals("Document Templates", iterator.next().getName());
      assertEquals("User Groups", iterator.next().getName());

      if (includeMatchLocationTests) {
         ResultSet<Match<ArtifactReadable, AttributeReadable<?>>> matches = builder.getMatches();
         assertEquals(2, matches.size());

         Iterator<Match<ArtifactReadable, AttributeReadable<?>>> matchIterator = matches.iterator();
         checkMatch(matchIterator.next(), "Document Templates");
         checkMatch(matchIterator.next(), "User Groups");
      }
   }

   @Test
   public void testQueryArtifactTypeInheritance() throws OseeCoreException {
      QueryBuilder builder =
         factory.fromBranch(TestBranches.SAW_Bld_1).andTypeEquals(CoreArtifactTypes.AbstractSoftwareRequirement);

      assertEquals(0, builder.getCount());

      ArtifactReadable artifact = builder.getResults().getOneOrNull();
      assertNull(artifact);

      builder = factory.fromBranch(TestBranches.SAW_Bld_1).andIsOfType(CoreArtifactTypes.AbstractSoftwareRequirement);

      assertEquals(24, builder.getCount());

      ResultSet<ArtifactReadable> artifacts = builder.getResults();
      assertEquals(24, artifacts.size());

      checkContainsTypes(artifacts, CoreArtifactTypes.SoftwareRequirement);
   }

   @Test
   public void testQueryArtifactTypesMatch() throws OseeCoreException {
      QueryBuilder builder = factory.fromBranch(CoreBranches.COMMON);
      builder.andTypeEquals(CoreArtifactTypes.OseeTypeDefinition, CoreArtifactTypes.Folder);

      assertEquals(6, builder.getCount());

      ResultSet<ArtifactReadable> artifacts = builder.getResults();
      assertEquals(6, artifacts.size());

      checkContainsTypes(artifacts, CoreArtifactTypes.OseeTypeDefinition, CoreArtifactTypes.Folder);

      Iterator<ArtifactReadable> iterator = sort(artifacts);
      assertEquals("Document Templates", iterator.next().getName());
      assertEquals("User Groups", iterator.next().getName());

      assertEquals("org.eclipse.osee.client.demo.OseeTypes_ClientDemo", iterator.next().getName());
      assertEquals("org.eclipse.osee.coverage.OseeTypes_Coverage", iterator.next().getName());
      assertEquals("org.eclipse.osee.framework.skynet.core.OseeTypes_Framework", iterator.next().getName());
      assertEquals("org.eclipse.osee.ote.define.OseeTypesOTE", iterator.next().getName());
   }

   @Test
   public void testQueryAttributeValue() throws OseeCoreException {
      QueryBuilder builder = factory.fromBranch(CoreBranches.COMMON);
      builder.and(CoreAttributeTypes.Name, Operator.EQUAL, "User Groups");

      assertEquals(1, builder.getCount());

      ArtifactReadable artifact = builder.getResults().getExactlyOne();

      assertEquals("User Groups", artifact.getName());
      assertEquals(CoreArtifactTypes.Folder, artifact.getArtifactType());

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
   public void testQueryArtifactTypeAndNameValue() throws OseeCoreException {
      QueryBuilder builder = factory.fromBranch(TestBranches.SAW_Bld_1);
      builder.and(CoreAttributeTypes.Name, "Requirements", MatchTokenCountType.IGNORE_TOKEN_COUNT);

      assertEquals(7, builder.getCount());
      ResultSet<ArtifactReadable> artifacts = builder.getResults();
      assertEquals(7, artifacts.size());
      checkContainsTypes(artifacts, CoreArtifactTypes.Folder, CoreArtifactTypes.SubsystemRequirementMSWord,
         CoreArtifactTypes.SystemRequirementMSWord);

      builder.andIsOfType(CoreArtifactTypes.Folder);
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
      QueryBuilder builder1 = factory.fromBranch(TestBranches.SAW_Bld_1);
      builder1.and(CoreAttributeTypes.Name, "Requirements", MatchTokenCountType.IGNORE_TOKEN_COUNT);
      builder1.andTypeEquals(CoreArtifactTypes.SubsystemRequirementMSWord);
      assertEquals(1, builder1.getCount());
      ResultSet<ArtifactReadable> subSystemReqs = builder1.getResults();
      assertEquals(1, subSystemReqs.size());
      assertEquals("Subsystem Requirements", subSystemReqs.iterator().next().getName());
      checkContainsTypes(subSystemReqs, CoreArtifactTypes.SubsystemRequirementMSWord);

      //////////////////////
      QueryBuilder builder2 = factory.fromBranch(TestBranches.SAW_Bld_1);
      builder2.and(CoreAttributeTypes.Name, "Requirements", MatchTokenCountType.IGNORE_TOKEN_COUNT);
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
   public void testQueryRequirementsAsLocalIds() throws OseeCoreException {
      QueryBuilder builder = factory.fromBranch(TestBranches.SAW_Bld_1);
      builder.and(CoreAttributeTypes.Name, "REQUIREMENTS", CaseType.IGNORE_CASE, TokenOrderType.MATCH_ORDER,
         TokenDelimiterMatch.ANY, MatchTokenCountType.IGNORE_TOKEN_COUNT);

      ResultSet<HasLocalId> results = builder.getResultsAsLocalIds();
      assertEquals(7, results.size());
      assertEquals(7, builder.getCount());
   }

   @Test
   public void testQueryAttributeKeyword() throws OseeCoreException {
      QueryBuilder builder = factory.fromBranch(TestBranches.SAW_Bld_1);
      builder.and(CoreAttributeTypes.Name, "REQUIREMENTS", TokenDelimiterMatch.ANY);

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

      QueryBuilder builder1 = factory.fromBranch(TestBranches.SAW_Bld_1);
      builder1.and(CoreAttributeTypes.Name, "REQUIREMENTS", TokenDelimiterMatch.ANY, CaseType.MATCH_CASE);
      assertEquals(0, builder1.getCount());
   }

   @Test
   public void testRelatedToTest() throws OseeCoreException {
      QueryBuilder builder1 = factory.fromBranch(TestBranches.SAW_Bld_1);
      builder1.and(CoreAttributeTypes.Name, Operator.EQUAL, "Frame Synchronization");
      assertEquals("Frame Synchronization", builder1.getResults().getExactlyOne().getName());

      QueryBuilder builder2 = factory.fromBranch(TestBranches.SAW_Bld_1);
      builder2.andRelatedTo(CoreRelationTypes.Default_Hierarchical__Child, builder1.getResults().getExactlyOne());
      assertEquals("Video processing", builder2.getResults().getExactlyOne().getName());

      QueryBuilder builder3 = factory.fromBranch(TestBranches.SAW_Bld_1);
      builder3.andRelatedTo(CoreRelationTypes.Default_Hierarchical__Child, builder2.getResults().getExactlyOne());
      assertEquals("Subsystem Requirements", builder3.getResults().getExactlyOne().getName());
   }

   @Test
   public void testAndNameEquals() throws Exception {
      // This test sets up two folders, the name of the first has the name of the second in it
      // The goal is to make sure query.AndNameEquals doesn't return a match unless it matches exactly
      ReadableBranch branch = setupNameEqualsArtifacts();
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

   private ReadableBranch setupNameEqualsArtifacts() throws Exception {
      author = factory.fromBranch(CoreBranches.COMMON).andIds(SystemUser.OseeSystem).getResults().getExactlyOne();
      IOseeBranch branchToken = TokenFactory.createBranch(GUID.create(), "TestAndNameEquals");
      ReadableBranch branch = branchApi.createTopLevelBranch(branchToken, author).call();
      TransactionBuilder tx = txFactory.createTransaction(branch, author, "add folders");
      tx.createArtifact(CoreArtifactTypes.Folder, "First Folder");
      tx.createArtifact(CoreArtifactTypes.Folder, "Folder");
      tx.commit();
      return branch;
   }

   private static void checkContainsTypes(Iterable<ArtifactReadable> arts, IArtifactType... types) throws OseeCoreException {
      List<IArtifactType> expected = Arrays.asList(types);
      for (ArtifactReadable art : arts) {
         assertTrue(String.format("artifact type [%s] not found", art.getArtifactType()),
            expected.contains(art.getArtifactType()));
      }
   }

   private static void checkMatchSingleAttribute(Match<ArtifactReadable, AttributeReadable<?>> match, String artName, IAttributeType types, String matched) throws OseeCoreException {
      assertEquals(artName, match.getItem().getName());

      AttributeReadable<?> attribute = match.getElements().iterator().next();
      assertEquals(types, attribute.getAttributeType());
      List<MatchLocation> locations = match.getLocation(attribute);
      assertEquals(1, locations.size());

      MatchLocation location = locations.get(0);
      String value = String.valueOf(attribute.getValue());
      assertEquals(matched, value.substring(location.getStartPosition() - 1, location.getEndPosition()));
   }

   private static void checkMatch(Match<ArtifactReadable, AttributeReadable<?>> match, String artName, IAttributeType... types) throws OseeCoreException {
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
      Ordering<T> from = (Ordering<T>) Ordering.from(new NameComparator(SortOrder.ASCENDING));
      return from.sortedCopy(iterable).iterator();
   }
}
