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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.enums.CaseType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.MatchTokenCountType;
import org.eclipse.osee.framework.core.enums.Operator;
import org.eclipse.osee.framework.core.enums.TokenDelimiterMatch;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.MatchLocation;
import org.eclipse.osee.orcs.ApplicationContext;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.eclipse.osee.orcs.db.mock.OsgiService;
import org.eclipse.osee.orcs.search.Match;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.utility.MatchComparator;
import org.eclipse.osee.orcs.utility.NameComparator;
import org.eclipse.osee.orcs.utility.SortOrder;
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
   public TestRule osgi = integrationRule(this, "osee.demo.hsql");

   private final boolean includeMatchLocationTests = false;

   @OsgiService
   private OrcsApi orcsApi;

   private QueryFactory factory;

   @Before
   public void setup() {
      ApplicationContext context = null; // TODO use real application context
      factory = orcsApi.getQueryFactory(context);
   }

   @Test
   public void testAllArtifactsFromBranch() throws OseeCoreException {
      QueryBuilder builder = factory.fromBranch(CoreBranches.COMMON);
      Assert.assertEquals(26, builder.getCount());

      Assert.assertEquals(26, builder.getResults().getList().size());
   }

   @Test
   public void testQueryByIds() throws OseeCoreException {
      QueryBuilder builder = factory.fromBranch(CoreBranches.COMMON).andGuidsOrHrids("AkA2AmNuEDDL4VolM9AA");
      Assert.assertEquals(1, builder.getCount());

      ArtifactReadable artifact = builder.getResults().getExactlyOne();
      Assert.assertEquals("AkA2AmNuEDDL4VolM9AA", artifact.getGuid());
   }

   @Test
   public void testQueryArtifactType() throws OseeCoreException {
      QueryBuilder builder = factory.fromBranch(CoreBranches.COMMON).andIsOfType(CoreArtifactTypes.Folder);

      Assert.assertEquals(2, builder.getCount());

      List<ArtifactReadable> artifacts = builder.getResults().getList();
      Assert.assertEquals(2, artifacts.size());

      checkContainsTypes(artifacts, CoreArtifactTypes.Folder);

      Collections.sort(artifacts, new NameComparator(SortOrder.ASCENDING));

      Iterator<ArtifactReadable> iterator = artifacts.iterator();
      Assert.assertEquals("Document Templates", iterator.next().getName());
      Assert.assertEquals("User Groups", iterator.next().getName());

      if (includeMatchLocationTests) {
         List<Match<ArtifactReadable, AttributeReadable<?>>> matches = builder.getMatches().getList();
         Assert.assertEquals(2, matches.size());

         Iterator<Match<ArtifactReadable, AttributeReadable<?>>> matchIterator = matches.iterator();
         checkMatch(matchIterator.next(), "Document Templates");
         checkMatch(matchIterator.next(), "User Groups");
      }
   }

   @Test
   public void testQueryArtifactTypeInheritance() throws OseeCoreException {
      QueryBuilder builder =
         factory.fromBranch(TestBranches.SAW_Bld_1).andTypeEquals(CoreArtifactTypes.AbstractSoftwareRequirement);

      Assert.assertEquals(0, builder.getCount());

      ArtifactReadable artifact = builder.getResults().getOneOrNull();
      Assert.assertNull(artifact);

      builder = factory.fromBranch(TestBranches.SAW_Bld_1).andIsOfType(CoreArtifactTypes.AbstractSoftwareRequirement);

      Assert.assertEquals(24, builder.getCount());

      List<ArtifactReadable> artifacts = builder.getResults().getList();
      Assert.assertEquals(24, artifacts.size());

      checkContainsTypes(artifacts, CoreArtifactTypes.SoftwareRequirement);
   }

   @Test
   public void testQueryArtifactTypesMatch() throws OseeCoreException {
      QueryBuilder builder = factory.fromBranch(CoreBranches.COMMON);
      builder.andTypeEquals(CoreArtifactTypes.OseeTypeDefinition, CoreArtifactTypes.Folder);

      Assert.assertEquals(6, builder.getCount());

      List<ArtifactReadable> artifacts = builder.getResults().getList();
      Assert.assertEquals(6, artifacts.size());

      checkContainsTypes(artifacts, CoreArtifactTypes.OseeTypeDefinition, CoreArtifactTypes.Folder);

      Collections.sort(artifacts, new NameComparator(SortOrder.ASCENDING));

      Iterator<ArtifactReadable> iterator = artifacts.iterator();
      Assert.assertEquals("Document Templates", iterator.next().getName());
      Assert.assertEquals("User Groups", iterator.next().getName());

      Assert.assertEquals("org.eclipse.osee.client.demo.OseeTypes_ClientDemo", iterator.next().getName());
      Assert.assertEquals("org.eclipse.osee.coverage.OseeTypes_Coverage", iterator.next().getName());
      Assert.assertEquals("org.eclipse.osee.framework.skynet.core.OseeTypes_Framework", iterator.next().getName());
      Assert.assertEquals("org.eclipse.osee.ote.define.OseeTypesOTE", iterator.next().getName());
   }

   @Test
   public void testQueryAttributeValue() throws OseeCoreException {
      QueryBuilder builder = factory.fromBranch(CoreBranches.COMMON);
      builder.and(CoreAttributeTypes.Name, Operator.EQUAL, "User Groups");

      Assert.assertEquals(1, builder.getCount());

      ArtifactReadable artifact = builder.getResults().getExactlyOne();

      Assert.assertEquals("User Groups", artifact.getName());
      Assert.assertEquals(CoreArtifactTypes.Folder, artifact.getArtifactType());

      if (includeMatchLocationTests) {
         Match<ArtifactReadable, AttributeReadable<?>> result = builder.getMatches().getExactlyOne();

         Assert.assertEquals(artifact, result.getItem());
         checkMatch(result, "User Groups", CoreAttributeTypes.Name);

         AttributeReadable<?> attr = result.getElements().iterator().next();
         List<MatchLocation> location = result.getLocation(attr);
         Assert.assertEquals(1, location.size());

         MatchLocation loc1 = location.iterator().next();
         Assert.assertEquals(0, loc1.getStartPosition());
         Assert.assertEquals("User Groups".length(), loc1.getEndPosition());
      }
   }

   @Test
   public void testQueryArtifactTypeAndNameValue() throws OseeCoreException {
      QueryBuilder builder = factory.fromBranch(TestBranches.SAW_Bld_1);
      builder.and(CoreAttributeTypes.Name, "Requirements", MatchTokenCountType.IGNORE_TOKEN_COUNT);

      Assert.assertEquals(7, builder.getCount());
      List<ArtifactReadable> artifacts = builder.getResults().getList();
      Assert.assertEquals(7, artifacts.size());
      checkContainsTypes(artifacts, CoreArtifactTypes.Folder, CoreArtifactTypes.SubsystemRequirementMSWord,
         CoreArtifactTypes.SystemRequirementMSWord);

      builder.andIsOfType(CoreArtifactTypes.Folder);
      Assert.assertEquals(4, builder.getCount());

      List<ArtifactReadable> folders = builder.getResults().getList();
      Assert.assertEquals(4, folders.size());
      Collections.sort(folders, new NameComparator(SortOrder.ASCENDING));
      Iterator<ArtifactReadable> folderIterator = folders.iterator();
      Assert.assertEquals("Hardware Requirements", folderIterator.next().getName());
      Assert.assertEquals("Software Requirements", folderIterator.next().getName());
      Assert.assertEquals("Subsystem Requirements", folderIterator.next().getName());
      Assert.assertEquals("System Requirements", folderIterator.next().getName());
      checkContainsTypes(folders, CoreArtifactTypes.Folder);

      //////////////////////
      QueryBuilder builder1 = factory.fromBranch(TestBranches.SAW_Bld_1);
      builder1.and(CoreAttributeTypes.Name, "Requirements", MatchTokenCountType.IGNORE_TOKEN_COUNT);
      builder1.andTypeEquals(CoreArtifactTypes.SubsystemRequirementMSWord);
      Assert.assertEquals(1, builder1.getCount());
      List<ArtifactReadable> subSystemReqs = builder1.getResults().getList();
      Assert.assertEquals(1, subSystemReqs.size());
      Assert.assertEquals("Subsystem Requirements", subSystemReqs.get(0).getName());
      checkContainsTypes(subSystemReqs, CoreArtifactTypes.SubsystemRequirementMSWord);

      //////////////////////
      QueryBuilder builder2 = factory.fromBranch(TestBranches.SAW_Bld_1);
      builder2.and(CoreAttributeTypes.Name, "Requirements", MatchTokenCountType.IGNORE_TOKEN_COUNT);
      builder2.andIsOfType(CoreArtifactTypes.Requirement);
      Assert.assertEquals(3, builder2.getCount());

      List<ArtifactReadable> requirements = builder2.getResults().getList();
      Assert.assertEquals(3, requirements.size());
      Collections.sort(requirements, new NameComparator(SortOrder.ASCENDING));
      Iterator<ArtifactReadable> reqIterator = requirements.iterator();
      Assert.assertEquals("Performance Requirements", reqIterator.next().getName());
      Assert.assertEquals("Safety Requirements", reqIterator.next().getName());
      Assert.assertEquals("Subsystem Requirements", reqIterator.next().getName());
      checkContainsTypes(subSystemReqs, CoreArtifactTypes.SubsystemRequirementMSWord,
         CoreArtifactTypes.SystemRequirementMSWord);
   }

   @Test
   public void testQueryAttributeKeyword() throws OseeCoreException {
      QueryBuilder builder = factory.fromBranch(TestBranches.SAW_Bld_1);
      builder.and(CoreAttributeTypes.Name, "REQUIREMENTS", TokenDelimiterMatch.ANY);

      Assert.assertEquals(7, builder.getCount());
      List<ArtifactReadable> requirements = builder.getResults().getList();
      Assert.assertEquals(7, requirements.size());
      checkContainsTypes(requirements, CoreArtifactTypes.Folder, CoreArtifactTypes.SubsystemRequirementMSWord,
         CoreArtifactTypes.SystemRequirementMSWord);
      Collections.sort(requirements, new NameComparator(SortOrder.ASCENDING));
      Iterator<ArtifactReadable> reqIterator = requirements.iterator();
      Assert.assertEquals("Hardware Requirements", reqIterator.next().getName());
      Assert.assertEquals("Performance Requirements", reqIterator.next().getName());
      Assert.assertEquals("Safety Requirements", reqIterator.next().getName());
      Assert.assertEquals("Software Requirements", reqIterator.next().getName());
      Assert.assertEquals("Subsystem Requirements", reqIterator.next().getName());
      Assert.assertEquals("Subsystem Requirements", reqIterator.next().getName());
      Assert.assertEquals("System Requirements", reqIterator.next().getName());

      //      if (includeMatchLocationTests) {
      List<Match<ArtifactReadable, AttributeReadable<?>>> matches = builder.getMatches().getList();
      Assert.assertEquals(7, matches.size());

      Collections.sort(matches, new MatchComparator(SortOrder.ASCENDING));

      // @formatter:off
      Iterator<Match<ArtifactReadable, AttributeReadable<?>>> matchIterator = matches.iterator();
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
      Assert.assertEquals(0, builder1.getCount());
   }

   @Test
   public void testRelatedToTest() throws OseeCoreException {
      QueryBuilder builder1 = factory.fromBranch(TestBranches.SAW_Bld_1);
      builder1.and(CoreAttributeTypes.Name, Operator.EQUAL, "Frame Synchronization");
      Assert.assertEquals("Frame Synchronization", builder1.getResults().getExactlyOne().getName());

      QueryBuilder builder2 = factory.fromBranch(TestBranches.SAW_Bld_1);
      builder2.andRelatedTo(CoreRelationTypes.Default_Hierarchical__Child, builder1.getResults().getExactlyOne());
      Assert.assertEquals("Video processing", builder2.getResults().getExactlyOne().getName());

      QueryBuilder builder3 = factory.fromBranch(TestBranches.SAW_Bld_1);
      builder3.andRelatedTo(CoreRelationTypes.Default_Hierarchical__Child, builder2.getResults().getExactlyOne());
      Assert.assertEquals("Subsystem Requirements", builder3.getResults().getExactlyOne().getName());
   }

   private static void checkContainsTypes(Collection<ArtifactReadable> arts, IArtifactType... types) throws OseeCoreException {
      List<IArtifactType> expected = Arrays.asList(types);
      for (ArtifactReadable art : arts) {
         Assert.assertTrue(String.format("artifact type [%s] not found", art.getArtifactType()),
            expected.contains(art.getArtifactType()));
      }
   }

   private static void checkMatchSingleAttribute(Match<ArtifactReadable, AttributeReadable<?>> match, String artName, IAttributeType types, String matched) throws OseeCoreException {
      Assert.assertEquals(artName, match.getItem().getName());

      AttributeReadable<?> attribute = match.getElements().iterator().next();
      Assert.assertEquals(types, attribute.getAttributeType());
      List<MatchLocation> locations = match.getLocation(attribute);
      Assert.assertEquals(1, locations.size());

      MatchLocation location = locations.get(0);
      String value = String.valueOf(attribute.getValue());
      Assert.assertEquals(matched, value.substring(location.getStartPosition() - 1, location.getEndPosition()));
   }

   private static void checkMatch(Match<ArtifactReadable, AttributeReadable<?>> match, String artName, IAttributeType... types) throws OseeCoreException {
      Assert.assertEquals(artName, match.getItem().getName());
      if (types.length > 0) {
         Assert.assertEquals(types.length, match.getElements().size());

         Iterator<AttributeReadable<?>> iterator = match.getElements().iterator();
         for (int index = 0; index < types.length; index++) {
            Assert.assertEquals(types[index], iterator.next().getAttributeType());
         }
      }
   }
}
