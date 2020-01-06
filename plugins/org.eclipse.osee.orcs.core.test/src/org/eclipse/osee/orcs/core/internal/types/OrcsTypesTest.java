/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal.types;

import static org.eclipse.osee.framework.core.enums.RelationSide.SIDE_A;
import static org.eclipse.osee.framework.core.enums.RelationSide.SIDE_B;
import static org.eclipse.osee.framework.core.enums.RelationSorter.LEXICOGRAPHICAL_ASC;
import static org.eclipse.osee.framework.core.enums.RelationSorter.LEXICOGRAPHICAL_DESC;
import static org.eclipse.osee.framework.core.enums.RelationSorter.UNORDERED;
import static org.eclipse.osee.orcs.core.internal.types.OrcsTestTypeTokenProvider.test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.io.ByteSource;
import com.google.common.io.Resources;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeDate;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeInputStream;
import org.eclipse.osee.framework.core.data.AttributeTypeString;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.data.TaggerTypeToken;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.enums.RelationSorter;
import org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.OrcsTypes;
import org.eclipse.osee.orcs.core.ds.OrcsTypesDataStore;
import org.eclipse.osee.orcs.core.internal.types.impl.OrcsTypesImpl;
import org.eclipse.osee.orcs.data.ArtifactTypes;
import org.eclipse.osee.orcs.data.AttributeTypes;
import org.eclipse.osee.orcs.data.EnumEntry;
import org.eclipse.osee.orcs.data.EnumType;
import org.eclipse.osee.orcs.data.RelationTypes;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * Test Case for {@link OrcsTypesImpl}
 *
 * @author Roberto E. Escobar
 */
public final class OrcsTypesTest {

   //@formatter:off
   private static final String SESSION_ID = "Test Session";

   private static final String TEST_TYPE_MODEL = "testTypeModel.osee";

   private static final AttributeTypeString Name = test.createString(1152921504606957090L, "Name", MediaType.TEXT_PLAIN, "");
   private static final AttributeTypeInputStream Annotation = test.createInputStream(1152921504606957091L, "Annotation", MediaType.TEXT_PLAIN, "");
   private static final AttributeTypeId Wordml = test.createString(1152921504606957092L, "WordML", MediaType.APPLICATION_XML, "");
   private static final TestProcedureStatusAttributeType Field1 = test.createEnumNoTag(TestProcedureStatusAttributeType::new, MediaType.TEXT_PLAIN);
   private static final AttributeTypeDate Field2 = test.createDate(1152921504606957094L, "Field 2", MediaType.TEXT_PLAIN, "");

   public static final ArtifactTypeToken Artifact = test.add(test.artifactType(1152921504606957083L, "Artifact", false)
      .atLeastOne(Name, "unnamed")
      .any(Annotation, ""));
   public static final ArtifactTypeToken OtherArtifact = test.add(test.artifactType(1152921504606957088L, "Other Artifact", false, Artifact));
   public static final ArtifactTypeToken Requirement = test.add(test.artifactType(1152921504606957084L, "Requirement", false, Artifact)
      .zeroOrOne(Annotation, "<w:p xmlns:w=\"http://schemas.microsoft.com/office/word/2003/wordml\"><w:r><w:t></w:t></w:r></w:p>"));
   public static final ArtifactTypeToken SoftwareRequirement = test.add(test.artifactType(1152921504606957085L, "Software Requirement", false, Requirement));
   public static final ArtifactTypeToken SystemRequirement = test.add(test.artifactType(1152921504606957086L, "System Requirement", false, Requirement));
   public static final ArtifactTypeToken SubsystemRequirement = test.add(test.artifactType(1152921504606957087L, "SubSystem Requirement", false, Requirement, OtherArtifact)
      .any(Field1, "this is a field"));
   public static final ArtifactTypeToken LastArtifact = test.add(test.artifactType(1152921504606957089L, "Last Artifact", false, SubsystemRequirement)
      .exactlyOne(Field2, "field2"));

   public static final RelationTypeToken RequirementRelation = test.add(2305843009213695295L, "Requirement Relation", RelationTypeMultiplicity.ONE_TO_MANY, RelationSorter.LEXICOGRAPHICAL_ASC, Requirement, "Requirement", SubsystemRequirement, "SubSystem Requirement");
   public static final RelationTypeSide RequirementRelation_RequirementSideA = RelationTypeSide.create(RequirementRelation, RelationSide.SIDE_A);
   public static final RelationTypeSide RequirementRelation_SubsystemSideB = RelationTypeSide.create(RequirementRelation, RelationSide.SIDE_B);

   public static final RelationTypeToken AnotherRelation = test.add(2305843009213695296L, "Another Relation", RelationTypeMultiplicity.MANY_TO_MANY, RelationSorter.UNORDERED, OtherArtifact, "Other Artifact", LastArtifact, "Last Artifact");
   public static final RelationTypeSide AnotherRelation_OtherSideA = RelationTypeSide.create(AnotherRelation, RelationSide.SIDE_A);
   public static final RelationTypeSide AnotherRelation_LastSideB = RelationTypeSide.create(AnotherRelation, RelationSide.SIDE_B);


   private static final BranchId BRANCH_A = IOseeBranch.create(3458234234L, "Branch A");
   private static final BranchId BRANCH_B = IOseeBranch.create(9993245332L, "Branch B");
   private static final BranchId BRANCH_C = IOseeBranch.create("Branch C");
   private static final BranchId BRANCH_D = IOseeBranch.create("Branch D");
   private static final BranchId BRANCH_E = IOseeBranch.create("Branch E");

   @Mock private Log logger;
   @Mock private OrcsTypesDataStore dataStore;
   @Mock private BranchHierarchyProvider hierarchyProvider;
   @Mock private OrcsSession session;
   //@formatter:on

   private OrcsTypes orcsTypes;
   private List<ByteSource> resources;
   private Multimap<BranchId, BranchId> branchHierachies;
   private OrcsTypesModule module;

   @Before
   public void setUp() throws Exception {
      MockitoAnnotations.initMocks(this);

      when(session.getGuid()).thenReturn(SESSION_ID);

      module = new OrcsTypesModule(logger, dataStore, hierarchyProvider);
      module.start(session);

      orcsTypes = module.createOrcsTypes(session);
      resources = new ArrayList<>();

      URI uri = new URI("osee:/types.test.data.osee");

      IResource resource = new MultiResource(uri, resources);
      when(dataStore.getOrcsTypesLoader(session)).thenReturn(resource);

      resources.add(getResource(TEST_TYPE_MODEL));

      branchHierachies = ArrayListMultimap.create();

      // Field 1 will only be visible on branch A and its descendants
      // Field 2 will only be visible on branch B and its descendants
      branchHierachies.put(CoreBranches.SYSTEM_ROOT, CoreBranches.SYSTEM_ROOT);
      branchHierachies.putAll(BRANCH_A, Arrays.asList(BRANCH_A, CoreBranches.SYSTEM_ROOT));
      branchHierachies.putAll(BRANCH_B, Arrays.asList(BRANCH_B, CoreBranches.SYSTEM_ROOT));
      branchHierachies.putAll(BRANCH_C, Arrays.asList(BRANCH_C, CoreBranches.SYSTEM_ROOT));
      branchHierachies.putAll(BRANCH_D, Arrays.asList(BRANCH_D, BRANCH_A, CoreBranches.SYSTEM_ROOT));
      branchHierachies.putAll(BRANCH_E, Arrays.asList(BRANCH_E, BRANCH_B, CoreBranches.SYSTEM_ROOT));

      when(hierarchyProvider.getParentHierarchy(any(BranchId.class))).thenAnswer(
         new Answer<Iterable<? extends BranchId>>() {

            @Override
            public Iterable<? extends BranchId> answer(InvocationOnMock invocation) throws Throwable {
               BranchId branchToGet = (BranchId) invocation.getArguments()[0];
               return branchHierachies.get(branchToGet);
            }
         });

   }

   @After
   public void tearDown() {
      if (module != null) {
         module.stop();
      }
   }

   @Test
   public void testGetAllArtifactTypes() {
      ArtifactTypes artTypes = orcsTypes.getArtifactTypes();

      assertEquals(7, artTypes.size());
      assertEquals(false, artTypes.isEmpty());

      //@formatter:off
      assertContains(artTypes.getAll(), Artifact, Requirement, SoftwareRequirement, SystemRequirement, SubsystemRequirement, OtherArtifact, LastArtifact);
      //@formatter:on
   }

   @Test
   public void testGetArtifactTypesByUuid() {
      ArtifactTypes artTypes = orcsTypes.getArtifactTypes();

      assertEquals(Artifact, artTypes.get(Artifact));
      assertEquals(Requirement, artTypes.get(Requirement));
      assertEquals(SoftwareRequirement, artTypes.get(SoftwareRequirement));
      assertEquals(SystemRequirement, artTypes.get(SystemRequirement));
      assertEquals(SubsystemRequirement, artTypes.get(SubsystemRequirement));
      assertEquals(OtherArtifact, artTypes.get(OtherArtifact));
      assertEquals(LastArtifact, artTypes.get(LastArtifact));
   }

   @Test
   public void testExistsArtifactType() {
      ArtifactTypes artTypes = orcsTypes.getArtifactTypes();

      assertEquals(true, artTypes.exists(Artifact));
      assertEquals(true, artTypes.exists(Requirement));
      assertEquals(true, artTypes.exists(SoftwareRequirement));
      assertEquals(true, artTypes.exists(SystemRequirement));
      assertEquals(true, artTypes.exists(SubsystemRequirement));
      assertEquals(true, artTypes.exists(OtherArtifact));
      assertEquals(true, artTypes.exists(LastArtifact));
   }

   @Test
   public void testGetAllDescendants() {
      ArtifactTypes artTypes = orcsTypes.getArtifactTypes();

      //@formatter:off
      assertContains(artTypes.getAllDescendantTypes(Artifact), Requirement, SoftwareRequirement, SystemRequirement, SubsystemRequirement, OtherArtifact, LastArtifact);
      assertContains(artTypes.getAllDescendantTypes(Requirement), SoftwareRequirement, SystemRequirement, SubsystemRequirement, LastArtifact);
      assertEquals(true, artTypes.getAllDescendantTypes(SoftwareRequirement).isEmpty());
      assertEquals(true, artTypes.getAllDescendantTypes(SystemRequirement).isEmpty());
      assertEquals(true, artTypes.getAllDescendantTypes(SystemRequirement).isEmpty());
      assertEquals(true, artTypes.getAllDescendantTypes(LastArtifact).isEmpty());
      assertContains(artTypes.getAllDescendantTypes(SubsystemRequirement), LastArtifact);
      assertContains(artTypes.getAllDescendantTypes(OtherArtifact), SubsystemRequirement, LastArtifact);
      //@formatter:on
   }

   @Test
   public void testIsValidAttributeType() {
      // Field 1 will only be visible on branch A and Branch D
      // Field 2 will only be visible on branch B and Branch E

      ArtifactTypes artTypes = orcsTypes.getArtifactTypes();

      assertEquals(true, artTypes.isValidAttributeType(OtherArtifact, CoreBranches.SYSTEM_ROOT, Name));
      assertEquals(true, artTypes.isValidAttributeType(OtherArtifact, CoreBranches.SYSTEM_ROOT, Annotation));
      assertEquals(false, artTypes.isValidAttributeType(OtherArtifact, CoreBranches.SYSTEM_ROOT, Wordml));

      assertEquals(true, artTypes.isValidAttributeType(SubsystemRequirement, CoreBranches.SYSTEM_ROOT, Name));
      assertEquals(true, artTypes.isValidAttributeType(SubsystemRequirement, CoreBranches.SYSTEM_ROOT, Annotation));
      assertEquals(true, artTypes.isValidAttributeType(SubsystemRequirement, CoreBranches.SYSTEM_ROOT, Wordml));
      assertEquals(false, artTypes.isValidAttributeType(SubsystemRequirement, CoreBranches.SYSTEM_ROOT, Field1));
      assertEquals(true, artTypes.isValidAttributeType(SubsystemRequirement, BRANCH_A, Field1));
      assertEquals(false, artTypes.isValidAttributeType(SubsystemRequirement, BRANCH_B, Field1));
      assertEquals(false, artTypes.isValidAttributeType(SubsystemRequirement, BRANCH_C, Field1));
      assertEquals(true, artTypes.isValidAttributeType(SubsystemRequirement, BRANCH_D, Field1));
      assertEquals(false, artTypes.isValidAttributeType(SubsystemRequirement, BRANCH_E, Field1));
      assertEquals(false, artTypes.isValidAttributeType(SubsystemRequirement, CoreBranches.SYSTEM_ROOT, Field2));
      assertEquals(false, artTypes.isValidAttributeType(SubsystemRequirement, BRANCH_A, Field2));
      assertEquals(false, artTypes.isValidAttributeType(SubsystemRequirement, BRANCH_B, Field2));
      assertEquals(false, artTypes.isValidAttributeType(SubsystemRequirement, BRANCH_C, Field2));
      assertEquals(false, artTypes.isValidAttributeType(SubsystemRequirement, BRANCH_D, Field2));
      assertEquals(false, artTypes.isValidAttributeType(SubsystemRequirement, BRANCH_E, Field2));

      assertEquals(true, artTypes.isValidAttributeType(SubsystemRequirement, BRANCH_E, Name));
      assertEquals(true, artTypes.isValidAttributeType(SubsystemRequirement, BRANCH_E, Annotation));
      assertEquals(true, artTypes.isValidAttributeType(SubsystemRequirement, BRANCH_E, Wordml));

      assertEquals(true, artTypes.isValidAttributeType(LastArtifact, CoreBranches.SYSTEM_ROOT, Name));
      assertEquals(true, artTypes.isValidAttributeType(LastArtifact, CoreBranches.SYSTEM_ROOT, Annotation));
      assertEquals(true, artTypes.isValidAttributeType(LastArtifact, CoreBranches.SYSTEM_ROOT, Wordml));
      assertEquals(false, artTypes.isValidAttributeType(LastArtifact, CoreBranches.SYSTEM_ROOT, Field1));
      assertEquals(true, artTypes.isValidAttributeType(LastArtifact, BRANCH_A, Field1));
      assertEquals(false, artTypes.isValidAttributeType(LastArtifact, BRANCH_B, Field1));
      assertEquals(false, artTypes.isValidAttributeType(LastArtifact, BRANCH_C, Field1));
      assertEquals(true, artTypes.isValidAttributeType(LastArtifact, BRANCH_D, Field1));
      assertEquals(false, artTypes.isValidAttributeType(LastArtifact, BRANCH_E, Field1));

      assertEquals(false, artTypes.isValidAttributeType(LastArtifact, CoreBranches.SYSTEM_ROOT, Field2));
      assertEquals(false, artTypes.isValidAttributeType(LastArtifact, BRANCH_A, Field2));
      assertEquals(true, artTypes.isValidAttributeType(LastArtifact, BRANCH_B, Field2));
      assertEquals(false, artTypes.isValidAttributeType(LastArtifact, BRANCH_C, Field2));
      assertEquals(false, artTypes.isValidAttributeType(LastArtifact, BRANCH_D, Field2));
      assertEquals(true, artTypes.isValidAttributeType(LastArtifact, BRANCH_E, Field2));
   }

   @Test
   public void testGetAttributeTypes() {
      ArtifactTypes artTypes = orcsTypes.getArtifactTypes();

      assertContains(artTypes.getAttributeTypes(OtherArtifact, CoreBranches.SYSTEM_ROOT), Name, Annotation);
      assertContains(artTypes.getAttributeTypes(LastArtifact, CoreBranches.SYSTEM_ROOT), Name, Annotation, Wordml);

      //@formatter:off
      assertContains(artTypes.getAttributeTypes(SubsystemRequirement, CoreBranches.SYSTEM_ROOT), Name, Annotation, Wordml);
      assertContains(artTypes.getAttributeTypes(SubsystemRequirement, BRANCH_A), Name, Annotation, Wordml, Field1);
      assertContains(artTypes.getAttributeTypes(SubsystemRequirement, BRANCH_B), Name, Annotation, Wordml);
      assertContains(artTypes.getAttributeTypes(SubsystemRequirement, BRANCH_C), Name, Annotation, Wordml);
      assertContains(artTypes.getAttributeTypes(SubsystemRequirement, BRANCH_D), Name, Annotation, Wordml, Field1);
      assertContains(artTypes.getAttributeTypes(SubsystemRequirement, BRANCH_E), Name, Annotation, Wordml);

      assertContains(artTypes.getAttributeTypes(LastArtifact, CoreBranches.SYSTEM_ROOT), Name, Annotation, Wordml);
      assertContains(artTypes.getAttributeTypes(LastArtifact, BRANCH_A), Name, Annotation, Wordml, Field1);
      assertContains(artTypes.getAttributeTypes(LastArtifact, BRANCH_B), Name, Annotation, Wordml, Field2);
      assertContains(artTypes.getAttributeTypes(LastArtifact, BRANCH_C), Name, Annotation, Wordml);
      assertContains(artTypes.getAttributeTypes(LastArtifact, BRANCH_D), Name, Annotation, Wordml, Field1);
      assertContains(artTypes.getAttributeTypes(LastArtifact, BRANCH_E), Name, Annotation, Wordml, Field2);
      //@formatter:on
   }

   @Test
   public void testReloadAddArtifactType() {
      String addTypeDef = "artifactType \"Added Artifact Type\" extends \"Other Artifact\" {\n" + //
         "id 35 \n" + //
         "}";

      ArtifactTypes artTypes = orcsTypes.getArtifactTypes();

      assertEquals(7, artTypes.size());

      orcsTypes.invalidateAll();

      resources.add(asInput(addTypeDef));

      assertEquals(8, artTypes.size());
      ArtifactTypeToken artifactType = artTypes.get(35L);

      assertEquals("Added Artifact Type", artifactType.getName());
      assertEquals(Long.valueOf(35), artifactType.getId());

      assertFalse(artifactType.isAbstract());
      assertTrue(artifactType.inheritsFrom(OtherArtifact));
      assertTrue(artifactType.inheritsFrom(Artifact));
      assertFalse(OtherArtifact.inheritsFrom(artifactType));
      assertFalse(artifactType.inheritsFrom(Requirement));

      assertEquals(true, artTypes.exists(artifactType));
   }

   @Test
   public void testArtifactTypeOverride() {
      ArtifactTypes artTypes = orcsTypes.getArtifactTypes();

      assertEquals(7, artTypes.size());

      assertContains(artTypes.getAttributeTypes(OtherArtifact, CoreBranches.SYSTEM_ROOT), Name, Annotation);

      //@formatter:off
      assertContains(artTypes.getAttributeTypes(SubsystemRequirement, CoreBranches.SYSTEM_ROOT), Name, Annotation, Wordml);
      assertContains(artTypes.getAttributeTypes(SubsystemRequirement, BRANCH_A), Name, Annotation, Wordml, Field1);
      assertContains(artTypes.getAttributeTypes(SubsystemRequirement, BRANCH_B), Name, Annotation, Wordml);
      assertContains(artTypes.getAttributeTypes(SubsystemRequirement, BRANCH_C), Name, Annotation, Wordml);
      assertContains(artTypes.getAttributeTypes(SubsystemRequirement, BRANCH_D), Name, Annotation, Wordml, Field1);
      assertContains(artTypes.getAttributeTypes(SubsystemRequirement, BRANCH_E), Name, Annotation, Wordml);
      //@formatter:on

      //@formatter:off
      String overrideArtTypes =
         "\n overrides artifactType \"Artifact\" {\n" +
         "      inheritAll \n" +
         "      update attribute \"Annotation\" branchUuid "+BRANCH_A.getIdString()+"\n" +
         "}\n" +
         "\n overrides artifactType \"Other Artifact\" {\n" +
         "      inheritAll \n" +
         "      add attribute \"Field 2\" \n" +
         "}\n" +
         "\n overrides artifactType \"SubSystem Requirement\" {\n" +
         "      inheritAll \n" +
         "      remove attribute \"Field 1\" \n" +
         "}\n"
         ;
      //@formatter:on

      resources.add(asInput(overrideArtTypes));
      orcsTypes.invalidateAll();

      assertEquals(7, artTypes.size());

      assertContains(artTypes.getAttributeTypes(OtherArtifact, CoreBranches.SYSTEM_ROOT), Name, Field2);
      assertContains(artTypes.getAttributeTypes(OtherArtifact, BRANCH_A), Name, Annotation, Field2);
      assertContains(artTypes.getAttributeTypes(OtherArtifact, BRANCH_B), Name, Field2);
      assertContains(artTypes.getAttributeTypes(OtherArtifact, BRANCH_C), Name, Field2);
      assertContains(artTypes.getAttributeTypes(OtherArtifact, BRANCH_D), Name, Annotation, Field2);
      assertContains(artTypes.getAttributeTypes(OtherArtifact, BRANCH_E), Name, Field2);

      assertContains(artTypes.getAttributeTypes(SubsystemRequirement, CoreBranches.SYSTEM_ROOT), Name, Wordml, Field2);
      assertContains(artTypes.getAttributeTypes(SubsystemRequirement, BRANCH_A), Name, Annotation, Wordml, Field2);
      assertContains(artTypes.getAttributeTypes(SubsystemRequirement, BRANCH_B), Name, Wordml, Field2);
      assertContains(artTypes.getAttributeTypes(SubsystemRequirement, BRANCH_C), Name, Wordml, Field2);
      assertContains(artTypes.getAttributeTypes(SubsystemRequirement, BRANCH_D), Name, Annotation, Wordml, Field2);
      assertContains(artTypes.getAttributeTypes(SubsystemRequirement, BRANCH_E), Name, Wordml, Field2);
   }

   @Test
   public void testGetAllAttributeTypes() {
      AttributeTypes attrTypes = orcsTypes.getAttributeTypes();

      assertEquals(5, attrTypes.size());
      assertEquals(false, attrTypes.isEmpty());

      //@formatter:off
      assertContains(attrTypes.getAll(), Name, Annotation, Wordml, Field1, Field2);
      //@formatter:on
   }

   @Test
   public void testGetAttributeTypesByUuid() {
      AttributeTypes attrTypes = orcsTypes.getAttributeTypes();

      assertEquals(Name, attrTypes.get(Name));
      assertEquals(Annotation, attrTypes.get(Annotation));
      assertEquals(Wordml, attrTypes.get(Wordml));
      assertEquals(Field1, attrTypes.get(Field1));
      assertEquals(Field2, attrTypes.get(Field2));
   }

   @Test
   public void testExistsAttributeTypes() {
      AttributeTypes attrTypes = orcsTypes.getAttributeTypes();

      assertEquals(true, attrTypes.exists(Name));
      assertEquals(true, attrTypes.exists(Annotation));
      assertEquals(true, attrTypes.exists(Wordml));
      assertEquals(true, attrTypes.exists(Field1));
      assertEquals(true, attrTypes.exists(Field2));
   }

   @Test
   public void testGetAttributeProviderId() {
      AttributeTypes attrTypes = orcsTypes.getAttributeTypes();
      assertEquals("DefaultAttributeDataProvider", attrTypes.getAttributeProviderId(Name));
      assertEquals("UriAttributeDataProvider", attrTypes.getAttributeProviderId(Annotation));
      assertEquals("UriAttributeDataProvider", attrTypes.getAttributeProviderId(Wordml));
      assertEquals("DefaultAttributeDataProvider", attrTypes.getAttributeProviderId(Field1));
      assertEquals("UriAttributeDataProvider", attrTypes.getAttributeProviderId(Field2));
   }

   @Test
   public void testGetBaseAttributeTypeId() {
      AttributeTypes attrTypes = orcsTypes.getAttributeTypes();

      //@formatter:off
      assertEquals("StringAttribute", attrTypes.getBaseAttributeTypeId(Name));
      assertEquals("CompressedContentAttribute", attrTypes.getBaseAttributeTypeId(Annotation));
      assertEquals("WordAttribute", attrTypes.getBaseAttributeTypeId(Wordml));
      assertEquals("EnumeratedAttribute", attrTypes.getBaseAttributeTypeId(Field1));
      assertEquals("DateAttribute", attrTypes.getBaseAttributeTypeId(Field2));
      //@formatter:on
   }

   @Test
   public void testGetDefaultValue() {
      AttributeTypes attrTypes = orcsTypes.getAttributeTypes();

      //@formatter:off
      assertEquals("unnamed", attrTypes.getDefaultValue(Name));
      assertEquals(null, attrTypes.getDefaultValue(Annotation));
      assertEquals("<w:p xmlns:w=\"http://schemas.microsoft.com/office/word/2003/wordml\"><w:r><w:t></w:t></w:r></w:p>", attrTypes.getDefaultValue(Wordml));
      assertEquals("this is a field", attrTypes.getDefaultValue(Field1));
      assertEquals("field2", attrTypes.getDefaultValue(Field2));
      //@formatter:on
   }

   @Test
   public void testGetDescription() {
      AttributeTypes attrTypes = orcsTypes.getAttributeTypes();

      //@formatter:off
      assertEquals("Descriptive Name", attrTypes.getDescription(Name));
      assertEquals("the version \'1.0\' is this \"1.2.0\"", attrTypes.getDescription(Annotation));
      assertEquals("value must comply with WordML xml schema", attrTypes.getDescription(Wordml));
      assertEquals("", attrTypes.getDescription(Field1));
      assertEquals("field 2 description", attrTypes.getDescription(Field2));
      //@formatter:on
   }

   @Test
   public void testGetFileExtension() {
      AttributeTypes attrTypes = orcsTypes.getAttributeTypes();

      //@formatter:off
      assertEquals("", attrTypes.getFileTypeExtension(Name));
      assertEquals("", attrTypes.getFileTypeExtension(Annotation));
      assertEquals("xml", attrTypes.getFileTypeExtension(Wordml));
      assertEquals("", attrTypes.getFileTypeExtension(Field1));
      assertEquals("hello", attrTypes.getFileTypeExtension(Field2));
      //@formatter:on
   }

   @Test
   public void testGetMinOccurrence() {
      AttributeTypes attrTypes = orcsTypes.getAttributeTypes();

      //@formatter:off
      assertEquals(1, attrTypes.getMinOccurrences(Name));
      assertEquals(0, attrTypes.getMinOccurrences(Annotation));
      assertEquals(0, attrTypes.getMinOccurrences(Wordml));
      assertEquals(2, attrTypes.getMinOccurrences(Field1));
      assertEquals(1, attrTypes.getMinOccurrences(Field2));
      //@formatter:on
   }

   @Test
   public void testGetMaxOccurrences() {
      AttributeTypes attrTypes = orcsTypes.getAttributeTypes();

      //@formatter:off
      assertEquals(1, attrTypes.getMaxOccurrences(Name));
      assertEquals(Integer.MAX_VALUE, attrTypes.getMaxOccurrences(Annotation));
      assertEquals(1, attrTypes.getMaxOccurrences(Wordml));
      assertEquals(3, attrTypes.getMaxOccurrences(Field1));
      assertEquals(1, attrTypes.getMaxOccurrences(Field2));
      //@formatter:on
   }

   @Test
   public void testGetTaggerId() {
      AttributeTypes attrTypes = orcsTypes.getAttributeTypes();

      //@formatter:off
      assertEquals(TaggerTypeToken.PlainTextTagger, attrTypes.getTaggerId(Name));
      assertEquals(TaggerTypeToken.PlainTextTagger, attrTypes.getTaggerId(Annotation));
      assertEquals(TaggerTypeToken.XmlTagger, attrTypes.getTaggerId(Wordml));
      assertEquals(TaggerTypeToken.SENTINEL, attrTypes.getTaggerId(Field1));
      assertEquals(TaggerTypeToken.SENTINEL, attrTypes.getTaggerId(Field2));
      //@formatter:on
   }

   @Test
   public void testGetMediaType() {
      AttributeTypes attrTypes = orcsTypes.getAttributeTypes();

      //@formatter:off
      assertEquals("plan/text", attrTypes.getMediaType(Name));
      assertEquals("plan/text", attrTypes.getMediaType(Annotation));
      assertEquals("application/xml", attrTypes.getMediaType(Wordml));
      assertEquals("application/custom", attrTypes.getMediaType(Field1));
      assertEquals("**", attrTypes.getMediaType(Field2));
      //@formatter:on
   }

   @Test
   public void testGetAllTaggable() {
      AttributeTypes attrTypes = orcsTypes.getAttributeTypes();
      assertContains(attrTypes.getAllTaggable(), Name, Annotation, Wordml, Field2);
   }

   @Test
   public void testGetOseeEnum() {
      AttributeTypes attrTypes = orcsTypes.getAttributeTypes();

      EnumType enumType = attrTypes.getEnumType(Field1);

      assertEquals("enum.test.proc.status", enumType.getName());
      assertEquals(Long.valueOf(3458764513820541304L), enumType.getId());

      EnumEntry[] values = enumType.values();

      assertEnumEntry(values[0], "Completed -- Analysis in Work", 1, "");
      assertEnumEntry(values[1], "Completed -- Passed", 2, "");
      assertEnumEntry(values[2], "Completed -- With Issues", 3, "");
      assertEnumEntry(values[3], "Completed -- With Issues Resolved", 4, "");
      assertEnumEntry(values[4], "Not Performed", 0, "it was not performed");
      assertEnumEntry(values[5], "Partially Complete", 5, "is a partial");

      //@formatter:off
      assertEnumEntry(enumType.getEntryByName("Not Performed"), "Not Performed",  0, "it was not performed");
      assertEnumEntry(enumType.getEntryByName("Completed -- Analysis in Work"), "Completed -- Analysis in Work",  1, "");
      assertEnumEntry(enumType.getEntryByName("Completed -- Passed"), "Completed -- Passed",2, "");
      assertEnumEntry(enumType.getEntryByName("Completed -- With Issues"), "Completed -- With Issues",  3, "");
      assertEnumEntry(enumType.getEntryByName("Completed -- With Issues Resolved"), "Completed -- With Issues Resolved",  4, "");
      assertEnumEntry(enumType.getEntryByName("Partially Complete"), "Partially Complete",  5, "is a partial");
      //@formatter:on

      Iterator<String> iterator = enumType.valuesAsOrderedStringSet().iterator();
      assertEquals("Completed -- Analysis in Work", iterator.next());
      assertEquals("Completed -- Passed", iterator.next());
      assertEquals("Completed -- With Issues", iterator.next());
      assertEquals("Completed -- With Issues Resolved", iterator.next());
      assertEquals("Not Performed", iterator.next());
      assertEquals("Partially Complete", iterator.next());
   }

   private void assertEnumEntry(EnumEntry actual, String name, int ordinal, String description) {
      assertEquals(name, actual.getName());
      assertEquals(Long.valueOf(ordinal), actual.getId());
      assertEquals(description, actual.getDescription());
   }

   @Test
   public void testEnumOverride() {
      //@formatter:off
      String enumOverride = "overrides enum \"enum.test.proc.status\" { \n" +
         "inheritAll \n" +
         "add \"In Work\" description \"this is in work\"\n" +
         "remove \"enum.test.proc.status.Completed -- With Issues\" \n" +
      "}\n";
      //@formatter:on

      orcsTypes.invalidateAll();
      resources.add(asInput(enumOverride));

      AttributeTypes attrTypes = orcsTypes.getAttributeTypes();

      EnumType enumType = attrTypes.getEnumType(Field1);

      assertEquals("enum.test.proc.status", enumType.getName());
      assertEquals(Long.valueOf(3458764513820541304L), enumType.getId());

      Iterator<String> iterator = enumType.valuesAsOrderedStringSet().iterator();
      assertEquals("Completed -- Analysis in Work", iterator.next());
      assertEquals("Completed -- Passed", iterator.next());
      assertEquals("Completed -- With Issues Resolved", iterator.next());
      assertEquals("In Work", iterator.next());
      assertEquals("Not Performed", iterator.next());
      assertEquals("Partially Complete", iterator.next());

      EnumEntry[] values = enumType.values();

      assertEnumEntry(values[0], "Completed -- Analysis in Work", 1, "");
      assertEnumEntry(values[1], "Completed -- Passed", 2, "");
      assertEnumEntry(values[2], "Completed -- With Issues Resolved", 3, "");

      assertEnumEntry(values[3], "In Work", 5, "this is in work");

      assertEnumEntry(values[4], "Not Performed", 0, "it was not performed");
      assertEnumEntry(values[5], "Partially Complete", 4, "is a partial");
   }

   @Test
   public void testReloadAddAttributeType() {
      AttributeTypes attrTypes = orcsTypes.getAttributeTypes();

      assertEquals(5, attrTypes.size());

      orcsTypes.invalidateAll();

      //@formatter:off
      String addAttributeType = "attributeType \"Field 3\" extends DateAttribute {" +
        "id 1152921504606847106 \n" +
        "dataProvider DefaultAttributeDataProvider \n" +
        "min 1 \n" +
        "max 1 \n" +
        "taggerId AnotherTagger \n" +
        "description \"Added dynamically\" \n" +
      "}\n";
      //@formatter:on

      resources.add(asInput(addAttributeType));

      assertEquals(6, attrTypes.size());

      AttributeTypeToken attrType = attrTypes.get(1152921504606847106L);

      assertEquals("Field 3", attrType.getName());
      assertEquals(attrType, 1152921504606847106L);
      assertEquals("DefaultAttributeDataProvider", attrTypes.getAttributeProviderId(attrType));
      assertEquals("DateAttribute", attrTypes.getBaseAttributeTypeId(attrType));
      assertEquals(null, attrTypes.getDefaultValue(attrType));
      assertEquals("Added dynamically", attrTypes.getDescription(attrType));
      assertEquals("", attrTypes.getFileTypeExtension(attrType));
      assertEquals(1, attrTypes.getMinOccurrences(attrType));
      assertEquals(1, attrTypes.getMaxOccurrences(attrType));
      assertEquals(TaggerTypeToken.SENTINEL, attrTypes.getTaggerId(attrType));
      assertEquals(null, attrTypes.getEnumType(attrType));
      assertEquals(false, attrTypes.isEnumerated(attrType));
      assertEquals(false, attrTypes.isTaggable(attrType));
      assertEquals(true, attrTypes.exists(attrType));
   }

   @Test
   public void testGetAllRelationTypes() {
      RelationTypes relTypes = orcsTypes.getRelationTypes();

      assertEquals(2, relTypes.size());
      assertEquals(false, relTypes.isEmpty());

      //@formatter:off
      assertContains(relTypes.getAll(), RequirementRelation, AnotherRelation);
      //@formatter:on
   }

   @Test
   public void testGetRelationTypesByUuid() {
      RelationTypes relTypes = orcsTypes.getRelationTypes();

      assertEquals(RequirementRelation, relTypes.get(RequirementRelation));
      assertEquals(AnotherRelation, relTypes.get(AnotherRelation));
   }

   @Test
   public void testExistsRelationTypes() {
      RelationTypes relTypes = orcsTypes.getRelationTypes();

      assertTrue(relTypes.exists(RequirementRelation));
      assertTrue(relTypes.exists(AnotherRelation));
   }

   @Test
   public void testGetArtifactType() {
      RelationTypes relTypes = orcsTypes.getRelationTypes();

      assertEquals(Requirement, relTypes.getArtifactType(RequirementRelation, SIDE_A));
      assertEquals(SubsystemRequirement, relTypes.getArtifactType(RequirementRelation, SIDE_B));

      assertEquals(OtherArtifact, relTypes.getArtifactType(AnotherRelation, SIDE_A));
      assertEquals(LastArtifact, relTypes.getArtifactType(AnotherRelation, SIDE_B));
   }

   @Test
   public void testGetArtifactTypeSideA() {
      RelationTypes relTypes = orcsTypes.getRelationTypes();

      assertEquals(Requirement, relTypes.getArtifactTypeSideA(RequirementRelation));
      assertEquals(OtherArtifact, relTypes.getArtifactTypeSideA(AnotherRelation));
   }

   @Test
   public void testGetArtifactTypeSideB() {
      RelationTypes relTypes = orcsTypes.getRelationTypes();

      assertEquals(SubsystemRequirement, relTypes.getArtifactTypeSideB(RequirementRelation));
      assertEquals(LastArtifact, relTypes.getArtifactTypeSideB(AnotherRelation));
   }

   @Test
   public void testGetDefaultOrderTypeGuid() {
      RelationTypes relTypes = orcsTypes.getRelationTypes();

      //@formatter:off
      assertEquals(LEXICOGRAPHICAL_ASC, relTypes.getDefaultOrderTypeGuid(RequirementRelation));
      assertEquals(UNORDERED, relTypes.getDefaultOrderTypeGuid(AnotherRelation));
      //@formatter:on
   }

   @Test
   public void testGetMultiplicity() {
      RelationTypes relTypes = orcsTypes.getRelationTypes();

      assertEquals(RelationTypeMultiplicity.ONE_TO_MANY, relTypes.getMultiplicity(RequirementRelation));
      assertEquals(RelationTypeMultiplicity.MANY_TO_MANY, relTypes.getMultiplicity(AnotherRelation));
   }

   @Test
   public void testGetSideNameA() {
      RelationTypes relTypes = orcsTypes.getRelationTypes();

      assertEquals("requirement-sideA", relTypes.getSideAName(RequirementRelation));
      assertEquals("other-sideA", relTypes.getSideAName(AnotherRelation));
   }

   @Test
   public void testGetSideNameB() {
      RelationTypes relTypes = orcsTypes.getRelationTypes();

      assertEquals("subsystem-sideB", relTypes.getSideBName(RequirementRelation));
      assertEquals("last-sideB", relTypes.getSideBName(AnotherRelation));
   }

   @Test
   public void testGetSideName() {
      RelationTypes relTypes = orcsTypes.getRelationTypes();

      assertEquals("requirement-sideA", relTypes.getSideName(RequirementRelation, SIDE_A));
      assertEquals("subsystem-sideB", relTypes.getSideName(RequirementRelation, SIDE_B));

      assertEquals("other-sideA", relTypes.getSideName(AnotherRelation, SIDE_A));
      assertEquals("last-sideB", relTypes.getSideName(AnotherRelation, SIDE_B));
   }

   @Test
   public void testIsSideName() {
      RelationTypes relTypes = orcsTypes.getRelationTypes();

      assertEquals(true, relTypes.isSideAName(RequirementRelation, "requirement-sideA"));
      assertEquals(false, relTypes.isSideAName(RequirementRelation, "subsystem-sideB"));

      assertEquals(true, relTypes.isSideAName(AnotherRelation, "other-sideA"));
      assertEquals(false, relTypes.isSideAName(AnotherRelation, "last-sideB"));
   }

   @Test
   public void testIsOrdered() {
      RelationTypes relTypes = orcsTypes.getRelationTypes();

      assertEquals(true, relTypes.isOrdered(RequirementRelation));
      assertEquals(false, relTypes.isOrdered(AnotherRelation));
   }

   @Test
   public void testIsArtifactTypeAllowed() {
      RelationTypes relTypes = orcsTypes.getRelationTypes();

      assertEquals(false, relTypes.isArtifactTypeAllowed(RequirementRelation, SIDE_A, Artifact));
      assertEquals(true, relTypes.isArtifactTypeAllowed(RequirementRelation, SIDE_A, Requirement));
      assertEquals(true, relTypes.isArtifactTypeAllowed(RequirementRelation, SIDE_A, SoftwareRequirement));
      assertEquals(true, relTypes.isArtifactTypeAllowed(RequirementRelation, SIDE_A, SystemRequirement));
      assertEquals(true, relTypes.isArtifactTypeAllowed(RequirementRelation, SIDE_A, SubsystemRequirement));
      assertEquals(false, relTypes.isArtifactTypeAllowed(RequirementRelation, SIDE_A, OtherArtifact));
      assertEquals(true, relTypes.isArtifactTypeAllowed(RequirementRelation, SIDE_A, LastArtifact));

      assertEquals(false, relTypes.isArtifactTypeAllowed(RequirementRelation, SIDE_B, Artifact));
      assertEquals(false, relTypes.isArtifactTypeAllowed(RequirementRelation, SIDE_B, Requirement));
      assertEquals(false, relTypes.isArtifactTypeAllowed(RequirementRelation, SIDE_B, SoftwareRequirement));
      assertEquals(false, relTypes.isArtifactTypeAllowed(RequirementRelation, SIDE_B, SystemRequirement));
      assertEquals(true, relTypes.isArtifactTypeAllowed(RequirementRelation, SIDE_B, SubsystemRequirement));
      assertEquals(false, relTypes.isArtifactTypeAllowed(RequirementRelation, SIDE_B, OtherArtifact));
      assertEquals(true, relTypes.isArtifactTypeAllowed(RequirementRelation, SIDE_B, LastArtifact));

      assertEquals(false, relTypes.isArtifactTypeAllowed(AnotherRelation, SIDE_A, Artifact));
      assertEquals(false, relTypes.isArtifactTypeAllowed(AnotherRelation, SIDE_A, Requirement));
      assertEquals(false, relTypes.isArtifactTypeAllowed(AnotherRelation, SIDE_A, SoftwareRequirement));
      assertEquals(false, relTypes.isArtifactTypeAllowed(AnotherRelation, SIDE_A, SystemRequirement));
      assertEquals(true, relTypes.isArtifactTypeAllowed(AnotherRelation, SIDE_A, SubsystemRequirement));
      assertEquals(true, relTypes.isArtifactTypeAllowed(AnotherRelation, SIDE_A, OtherArtifact));
      assertEquals(true, relTypes.isArtifactTypeAllowed(AnotherRelation, SIDE_A, LastArtifact));

      assertEquals(false, relTypes.isArtifactTypeAllowed(AnotherRelation, SIDE_B, Artifact));
      assertEquals(false, relTypes.isArtifactTypeAllowed(AnotherRelation, SIDE_B, Requirement));
      assertEquals(false, relTypes.isArtifactTypeAllowed(AnotherRelation, SIDE_B, SoftwareRequirement));
      assertEquals(false, relTypes.isArtifactTypeAllowed(AnotherRelation, SIDE_B, SystemRequirement));
      assertEquals(false, relTypes.isArtifactTypeAllowed(AnotherRelation, SIDE_B, SubsystemRequirement));
      assertEquals(false, relTypes.isArtifactTypeAllowed(AnotherRelation, SIDE_B, OtherArtifact));
      assertEquals(true, relTypes.isArtifactTypeAllowed(AnotherRelation, SIDE_B, LastArtifact));
   }

   @Test
   public void testReloadAddRelationType() {
      RelationTypes relTypes = orcsTypes.getRelationTypes();

      assertEquals(2, relTypes.size());
      orcsTypes.invalidateAll();

      //@formatter:off
      String addType = "relationType \"Dynamic Relation\" {\n"+
          "id 2305843009213694297 \n"+
          "sideAName \"dynamic-sideA\" \n"+
          "sideAArtifactType \"Artifact\" \n"+
          "sideBName \"dynamic-sideB\" \n"+
          "sideBArtifactType \"Other Artifact\" \n"+
          "defaultOrderType Lexicographical_Descending \n"+
          "multiplicity MANY_TO_ONE \n" +
      "}\n";
      //@formatter:on

      resources.add(asInput(addType));

      assertEquals(3, relTypes.size());

      RelationTypeToken relation = relTypes.get(2305843009213694297L);

      assertEquals("Dynamic Relation", relation.getName());
      assertEquals(Long.valueOf(2305843009213694297L), relation.getId());

      assertEquals(Artifact, relTypes.getArtifactType(relation, SIDE_A));
      assertEquals(OtherArtifact, relTypes.getArtifactType(relation, SIDE_B));
      assertEquals(Artifact, relTypes.getArtifactTypeSideA(relation));
      assertEquals(OtherArtifact, relTypes.getArtifactTypeSideB(relation));
      assertEquals(LEXICOGRAPHICAL_DESC, relTypes.getDefaultOrderTypeGuid(relation));
      assertEquals(RelationTypeMultiplicity.MANY_TO_ONE, relTypes.getMultiplicity(relation));
      assertEquals("dynamic-sideA", relTypes.getSideName(relation, SIDE_A));
      assertEquals("dynamic-sideB", relTypes.getSideName(relation, SIDE_B));
      assertEquals("dynamic-sideA", relTypes.getSideAName(relation));
      assertEquals("dynamic-sideB", relTypes.getSideBName(relation));
      assertEquals(true, relTypes.isOrdered(relation));
      assertEquals(true, relTypes.isSideAName(relation, "dynamic-sideA"));
      assertEquals(false, relTypes.isSideAName(relation, "dynamic-sideB"));
      assertEquals(true, relTypes.isArtifactTypeAllowed(relation, SIDE_A, LastArtifact));
      assertEquals(false, relTypes.isArtifactTypeAllowed(relation, SIDE_B, Requirement));
      assertEquals(true, relTypes.isArtifactTypeAllowed(relation, SIDE_B, OtherArtifact));
      assertEquals(true, relTypes.isArtifactTypeAllowed(relation, SIDE_B, LastArtifact));

      assertEquals(true, relTypes.exists(relation));
   }

   private static void assertContains(Collection<?> actual, Id... expected) {
      List<?> asList = Arrays.asList(expected);

      String message = String.format("Actual: [%s] Expected: [%s]", actual, Arrays.deepToString(expected));

      assertEquals(message, asList.size(), actual.size());
      assertEquals(message, true, actual.containsAll(asList));
   }

   private static ByteSource getResource(String resourcePath) {
      URL resource = Resources.getResource(OrcsTypesTest.class, resourcePath);
      return Resources.asByteSource(resource);
   }

   private static ByteSource asInput(final String data) {
      return new ByteSource() {
         @Override
         public InputStream openStream() throws java.io.IOException {
            return new ByteArrayInputStream(data.getBytes("UTF-8"));
         }
      };
   }
   private static final class MultiResource implements IResource {
      private final Iterable<? extends ByteSource> suppliers;
      private final URI resourceUri;

      public MultiResource(URI resourceUri, Iterable<? extends ByteSource> suppliers) {
         super();
         this.suppliers = suppliers;
         this.resourceUri = resourceUri;
      }

      @Override
      public InputStream getContent() {
         try {
            return ByteSource.concat(suppliers).openStream();
         } catch (IOException ex) {
            throw OseeCoreException.wrap(ex);
         }
      }

      @Override
      public URI getLocation() {
         return resourceUri;
      }

      @Override
      public String getName() {
         String value = resourceUri.toASCIIString();
         return value.substring(value.lastIndexOf("/") + 1, value.length());
      }

      @Override
      public boolean isCompressed() {
         return false;
      }

   }

}