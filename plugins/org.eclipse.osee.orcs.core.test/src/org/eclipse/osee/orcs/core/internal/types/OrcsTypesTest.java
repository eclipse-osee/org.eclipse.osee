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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.io.ByteStreams;
import com.google.common.io.InputSupplier;
import com.google.common.io.Resources;
import com.google.common.util.concurrent.Callables;
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
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.enums.CoreBranches;
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
public class OrcsTypesTest {

   //@formatter:off
   private static final String SESSION_ID = "Test Session";

   private static final String TEST_TYPE_MODEL = "testTypeModel.osee";

   private static final IArtifactType ARTIFACT = TokenFactory.createArtifactType(1, "Artifact");
   private static final IArtifactType REQUIREMENT = TokenFactory.createArtifactType(21, "Requirement");
   private static final IArtifactType SOFTWARE_REQUIREMENT = TokenFactory.createArtifactType(24, "Software Requirement");
   private static final IArtifactType SYSTEM_REQUIREMENT = TokenFactory.createArtifactType(30, "System Requirement");
   private static final IArtifactType SUBSYSTEM_REQUIREMENT = TokenFactory.createArtifactType(29, "SubSystem Requirement");
   private static final IArtifactType OTHER_ARTIFACT = TokenFactory.createArtifactType(32, "Other Artifact");
   private static final IArtifactType LAST_ARTIFACT = TokenFactory.createArtifactType(33, "Last Artifact");

   private static final AttributeTypeId NAME = AttributeTypeToken.valueOf(1152921504606847088L, "Name");
   private static final AttributeTypeId ANNOTATION = AttributeTypeToken.valueOf(1152921504606847094L, "Annotation");
   private static final AttributeTypeId WORDML = AttributeTypeToken.valueOf(1152921504606847098L, "WordML");
   private static final AttributeTypeId FIELD_1 = AttributeTypeToken.valueOf(1152921504606847104L, "Field 1");
   private static final AttributeTypeId FIELD_2 = AttributeTypeToken.valueOf(1152921504606847105L, "Field 2");

   private static final IRelationType REQUIREMENT_REL = TokenFactory.createRelationType(2305843009213694295L, "Requirement Relation");
   private static final IRelationType ANOTHER_REL = TokenFactory.createRelationType(2305843009213694296L, "Another Relation");

   static long BRANCH_A_UUID = 3458234234L;
   static long BRANCH_B_UUID = 9993245332L;
   private static final BranchId BRANCH_A = IOseeBranch.create(BRANCH_A_UUID, "Branch A");
   private static final BranchId BRANCH_B = IOseeBranch.create(BRANCH_B_UUID, "Branch B");
   private static final BranchId BRANCH_C = IOseeBranch.create("Branch C");
   private static final BranchId BRANCH_D = IOseeBranch.create("Branch D");
   private static final BranchId BRANCH_E = IOseeBranch.create("Branch E");

   @Mock private Log logger;
   @Mock private OrcsTypesDataStore dataStore;
   @Mock private BranchHierarchyProvider hierarchyProvider;
   @Mock private OrcsSession session;
   //@formatter:on

   private OrcsTypes orcsTypes;
   private List<InputSupplier<? extends InputStream>> resources;
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
      when(dataStore.getOrcsTypesLoader(session)).thenReturn(Callables.returning(resource));

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
      assertContains(artTypes.getAll(), ARTIFACT, REQUIREMENT, SOFTWARE_REQUIREMENT, SYSTEM_REQUIREMENT, SUBSYSTEM_REQUIREMENT, OTHER_ARTIFACT, LAST_ARTIFACT);
      //@formatter:on
   }

   @Test
   public void testGetArtifactTypesByUuid() {
      ArtifactTypes artTypes = orcsTypes.getArtifactTypes();

      assertEquals(ARTIFACT, artTypes.get(ARTIFACT));
      assertEquals(REQUIREMENT, artTypes.get(REQUIREMENT));
      assertEquals(SOFTWARE_REQUIREMENT, artTypes.get(SOFTWARE_REQUIREMENT));
      assertEquals(SYSTEM_REQUIREMENT, artTypes.get(SYSTEM_REQUIREMENT));
      assertEquals(SUBSYSTEM_REQUIREMENT, artTypes.get(SUBSYSTEM_REQUIREMENT));
      assertEquals(OTHER_ARTIFACT, artTypes.get(OTHER_ARTIFACT));
      assertEquals(LAST_ARTIFACT, artTypes.get(LAST_ARTIFACT));
   }

   @Test
   public void testExistsArtifactType() {
      ArtifactTypes artTypes = orcsTypes.getArtifactTypes();

      assertEquals(true, artTypes.exists(ARTIFACT));
      assertEquals(true, artTypes.exists(REQUIREMENT));
      assertEquals(true, artTypes.exists(SOFTWARE_REQUIREMENT));
      assertEquals(true, artTypes.exists(SYSTEM_REQUIREMENT));
      assertEquals(true, artTypes.exists(SUBSYSTEM_REQUIREMENT));
      assertEquals(true, artTypes.exists(OTHER_ARTIFACT));
      assertEquals(true, artTypes.exists(LAST_ARTIFACT));
   }

   @Test
   public void testIsAbstract() {
      ArtifactTypes artTypes = orcsTypes.getArtifactTypes();

      assertEquals(false, artTypes.isAbstract(ARTIFACT));
      assertEquals(true, artTypes.isAbstract(REQUIREMENT));
      assertEquals(false, artTypes.isAbstract(SOFTWARE_REQUIREMENT));
      assertEquals(false, artTypes.isAbstract(SYSTEM_REQUIREMENT));
      assertEquals(false, artTypes.isAbstract(SUBSYSTEM_REQUIREMENT));
      assertEquals(false, artTypes.isAbstract(OTHER_ARTIFACT));
      assertEquals(true, artTypes.isAbstract(LAST_ARTIFACT));
   }

   @Test
   public void testHasSuperArtifactTypes() {
      ArtifactTypes artTypes = orcsTypes.getArtifactTypes();

      assertEquals(false, artTypes.hasSuperArtifactTypes(ARTIFACT));
      assertEquals(true, artTypes.hasSuperArtifactTypes(REQUIREMENT));
      assertEquals(true, artTypes.hasSuperArtifactTypes(SOFTWARE_REQUIREMENT));
      assertEquals(true, artTypes.hasSuperArtifactTypes(SYSTEM_REQUIREMENT));
      assertEquals(true, artTypes.hasSuperArtifactTypes(SUBSYSTEM_REQUIREMENT));
      assertEquals(true, artTypes.hasSuperArtifactTypes(OTHER_ARTIFACT));
      assertEquals(true, artTypes.hasSuperArtifactTypes(LAST_ARTIFACT));
   }

   @Test
   public void testGetSuperTypes() {
      ArtifactTypes artTypes = orcsTypes.getArtifactTypes();

      assertEquals(true, artTypes.getSuperArtifactTypes(ARTIFACT).isEmpty());

      assertContains(artTypes.getSuperArtifactTypes(REQUIREMENT), ARTIFACT);
      assertContains(artTypes.getSuperArtifactTypes(SOFTWARE_REQUIREMENT), REQUIREMENT);
      assertContains(artTypes.getSuperArtifactTypes(SYSTEM_REQUIREMENT), REQUIREMENT);
      assertContains(artTypes.getSuperArtifactTypes(SUBSYSTEM_REQUIREMENT), REQUIREMENT, OTHER_ARTIFACT);
      assertContains(artTypes.getSuperArtifactTypes(OTHER_ARTIFACT), ARTIFACT);
      assertContains(artTypes.getSuperArtifactTypes(LAST_ARTIFACT), SUBSYSTEM_REQUIREMENT);
   }

   @Test
   public void testInheritsFrom() {
      ArtifactTypes artTypes = orcsTypes.getArtifactTypes();

      assertEquals(false, artTypes.inheritsFrom(ARTIFACT, REQUIREMENT));
      assertEquals(true, artTypes.inheritsFrom(REQUIREMENT, ARTIFACT));

      assertEquals(false, artTypes.inheritsFrom(ARTIFACT, OTHER_ARTIFACT));
      assertEquals(false, artTypes.inheritsFrom(OTHER_ARTIFACT, REQUIREMENT));
      assertEquals(true, artTypes.inheritsFrom(OTHER_ARTIFACT, ARTIFACT));

      assertEquals(false, artTypes.inheritsFrom(ARTIFACT, SOFTWARE_REQUIREMENT));
      assertEquals(false, artTypes.inheritsFrom(REQUIREMENT, SOFTWARE_REQUIREMENT));
      assertEquals(true, artTypes.inheritsFrom(SOFTWARE_REQUIREMENT, ARTIFACT));
      assertEquals(true, artTypes.inheritsFrom(SOFTWARE_REQUIREMENT, REQUIREMENT));
      assertEquals(false, artTypes.inheritsFrom(SOFTWARE_REQUIREMENT, OTHER_ARTIFACT));

      assertEquals(false, artTypes.inheritsFrom(ARTIFACT, SYSTEM_REQUIREMENT));
      assertEquals(false, artTypes.inheritsFrom(REQUIREMENT, SYSTEM_REQUIREMENT));
      assertEquals(true, artTypes.inheritsFrom(SYSTEM_REQUIREMENT, ARTIFACT));
      assertEquals(true, artTypes.inheritsFrom(SYSTEM_REQUIREMENT, REQUIREMENT));
      assertEquals(false, artTypes.inheritsFrom(SYSTEM_REQUIREMENT, OTHER_ARTIFACT));

      assertEquals(false, artTypes.inheritsFrom(ARTIFACT, SUBSYSTEM_REQUIREMENT));
      assertEquals(false, artTypes.inheritsFrom(REQUIREMENT, SUBSYSTEM_REQUIREMENT));
      assertEquals(true, artTypes.inheritsFrom(SUBSYSTEM_REQUIREMENT, ARTIFACT));
      assertEquals(true, artTypes.inheritsFrom(SUBSYSTEM_REQUIREMENT, REQUIREMENT));
      assertEquals(true, artTypes.inheritsFrom(SUBSYSTEM_REQUIREMENT, OTHER_ARTIFACT));

      assertEquals(false, artTypes.inheritsFrom(ARTIFACT, LAST_ARTIFACT));
      assertEquals(false, artTypes.inheritsFrom(REQUIREMENT, LAST_ARTIFACT));
      assertEquals(false, artTypes.inheritsFrom(SUBSYSTEM_REQUIREMENT, LAST_ARTIFACT));
      assertEquals(true, artTypes.inheritsFrom(LAST_ARTIFACT, ARTIFACT));
      assertEquals(true, artTypes.inheritsFrom(LAST_ARTIFACT, REQUIREMENT));
      assertEquals(true, artTypes.inheritsFrom(LAST_ARTIFACT, OTHER_ARTIFACT));
      assertEquals(true, artTypes.inheritsFrom(LAST_ARTIFACT, SUBSYSTEM_REQUIREMENT));
   }

   @Test
   public void testGetAllDescendants() {
      ArtifactTypes artTypes = orcsTypes.getArtifactTypes();

      //@formatter:off
      assertContains(artTypes.getAllDescendantTypes(ARTIFACT), REQUIREMENT, SOFTWARE_REQUIREMENT, SYSTEM_REQUIREMENT, SUBSYSTEM_REQUIREMENT, OTHER_ARTIFACT, LAST_ARTIFACT);
      assertContains(artTypes.getAllDescendantTypes(REQUIREMENT), SOFTWARE_REQUIREMENT, SYSTEM_REQUIREMENT, SUBSYSTEM_REQUIREMENT, LAST_ARTIFACT);
      assertEquals(true, artTypes.getAllDescendantTypes(SOFTWARE_REQUIREMENT).isEmpty());
      assertEquals(true, artTypes.getAllDescendantTypes(SYSTEM_REQUIREMENT).isEmpty());
      assertEquals(true, artTypes.getAllDescendantTypes(SYSTEM_REQUIREMENT).isEmpty());
      assertEquals(true, artTypes.getAllDescendantTypes(LAST_ARTIFACT).isEmpty());
      assertContains(artTypes.getAllDescendantTypes(SUBSYSTEM_REQUIREMENT), LAST_ARTIFACT);
      assertContains(artTypes.getAllDescendantTypes(OTHER_ARTIFACT), SUBSYSTEM_REQUIREMENT, LAST_ARTIFACT);
      //@formatter:on
   }

   @Test
   public void testIsValidAttributeType() {
      // Field 1 will only be visible on branch A and Branch D
      // Field 2 will only be visible on branch B and Branch E

      ArtifactTypes artTypes = orcsTypes.getArtifactTypes();

      assertEquals(true, artTypes.isValidAttributeType(OTHER_ARTIFACT, CoreBranches.SYSTEM_ROOT, NAME));
      assertEquals(true, artTypes.isValidAttributeType(OTHER_ARTIFACT, CoreBranches.SYSTEM_ROOT, ANNOTATION));
      assertEquals(false, artTypes.isValidAttributeType(OTHER_ARTIFACT, CoreBranches.SYSTEM_ROOT, WORDML));

      assertEquals(true, artTypes.isValidAttributeType(SUBSYSTEM_REQUIREMENT, CoreBranches.SYSTEM_ROOT, NAME));
      assertEquals(true, artTypes.isValidAttributeType(SUBSYSTEM_REQUIREMENT, CoreBranches.SYSTEM_ROOT, ANNOTATION));
      assertEquals(true, artTypes.isValidAttributeType(SUBSYSTEM_REQUIREMENT, CoreBranches.SYSTEM_ROOT, WORDML));
      assertEquals(false, artTypes.isValidAttributeType(SUBSYSTEM_REQUIREMENT, CoreBranches.SYSTEM_ROOT, FIELD_1));
      assertEquals(true, artTypes.isValidAttributeType(SUBSYSTEM_REQUIREMENT, BRANCH_A, FIELD_1));
      assertEquals(false, artTypes.isValidAttributeType(SUBSYSTEM_REQUIREMENT, BRANCH_B, FIELD_1));
      assertEquals(false, artTypes.isValidAttributeType(SUBSYSTEM_REQUIREMENT, BRANCH_C, FIELD_1));
      assertEquals(true, artTypes.isValidAttributeType(SUBSYSTEM_REQUIREMENT, BRANCH_D, FIELD_1));
      assertEquals(false, artTypes.isValidAttributeType(SUBSYSTEM_REQUIREMENT, BRANCH_E, FIELD_1));
      assertEquals(false, artTypes.isValidAttributeType(SUBSYSTEM_REQUIREMENT, CoreBranches.SYSTEM_ROOT, FIELD_2));
      assertEquals(false, artTypes.isValidAttributeType(SUBSYSTEM_REQUIREMENT, BRANCH_A, FIELD_2));
      assertEquals(false, artTypes.isValidAttributeType(SUBSYSTEM_REQUIREMENT, BRANCH_B, FIELD_2));
      assertEquals(false, artTypes.isValidAttributeType(SUBSYSTEM_REQUIREMENT, BRANCH_C, FIELD_2));
      assertEquals(false, artTypes.isValidAttributeType(SUBSYSTEM_REQUIREMENT, BRANCH_D, FIELD_2));
      assertEquals(false, artTypes.isValidAttributeType(SUBSYSTEM_REQUIREMENT, BRANCH_E, FIELD_2));

      assertEquals(true, artTypes.isValidAttributeType(SUBSYSTEM_REQUIREMENT, BRANCH_E, NAME));
      assertEquals(true, artTypes.isValidAttributeType(SUBSYSTEM_REQUIREMENT, BRANCH_E, ANNOTATION));
      assertEquals(true, artTypes.isValidAttributeType(SUBSYSTEM_REQUIREMENT, BRANCH_E, WORDML));

      assertEquals(true, artTypes.isValidAttributeType(LAST_ARTIFACT, CoreBranches.SYSTEM_ROOT, NAME));
      assertEquals(true, artTypes.isValidAttributeType(LAST_ARTIFACT, CoreBranches.SYSTEM_ROOT, ANNOTATION));
      assertEquals(true, artTypes.isValidAttributeType(LAST_ARTIFACT, CoreBranches.SYSTEM_ROOT, WORDML));
      assertEquals(false, artTypes.isValidAttributeType(LAST_ARTIFACT, CoreBranches.SYSTEM_ROOT, FIELD_1));
      assertEquals(true, artTypes.isValidAttributeType(LAST_ARTIFACT, BRANCH_A, FIELD_1));
      assertEquals(false, artTypes.isValidAttributeType(LAST_ARTIFACT, BRANCH_B, FIELD_1));
      assertEquals(false, artTypes.isValidAttributeType(LAST_ARTIFACT, BRANCH_C, FIELD_1));
      assertEquals(true, artTypes.isValidAttributeType(LAST_ARTIFACT, BRANCH_D, FIELD_1));
      assertEquals(false, artTypes.isValidAttributeType(LAST_ARTIFACT, BRANCH_E, FIELD_1));

      assertEquals(false, artTypes.isValidAttributeType(LAST_ARTIFACT, CoreBranches.SYSTEM_ROOT, FIELD_2));
      assertEquals(false, artTypes.isValidAttributeType(LAST_ARTIFACT, BRANCH_A, FIELD_2));
      assertEquals(true, artTypes.isValidAttributeType(LAST_ARTIFACT, BRANCH_B, FIELD_2));
      assertEquals(false, artTypes.isValidAttributeType(LAST_ARTIFACT, BRANCH_C, FIELD_2));
      assertEquals(false, artTypes.isValidAttributeType(LAST_ARTIFACT, BRANCH_D, FIELD_2));
      assertEquals(true, artTypes.isValidAttributeType(LAST_ARTIFACT, BRANCH_E, FIELD_2));
   }

   @Test
   public void testGetAttributeTypes() {
      ArtifactTypes artTypes = orcsTypes.getArtifactTypes();

      assertContains(artTypes.getAttributeTypes(OTHER_ARTIFACT, CoreBranches.SYSTEM_ROOT), NAME, ANNOTATION);
      assertContains(artTypes.getAttributeTypes(LAST_ARTIFACT, CoreBranches.SYSTEM_ROOT), NAME, ANNOTATION, WORDML);

      //@formatter:off
      assertContains(artTypes.getAttributeTypes(SUBSYSTEM_REQUIREMENT, CoreBranches.SYSTEM_ROOT), NAME, ANNOTATION, WORDML);
      assertContains(artTypes.getAttributeTypes(SUBSYSTEM_REQUIREMENT, BRANCH_A), NAME, ANNOTATION, WORDML, FIELD_1);
      assertContains(artTypes.getAttributeTypes(SUBSYSTEM_REQUIREMENT, BRANCH_B), NAME, ANNOTATION, WORDML);
      assertContains(artTypes.getAttributeTypes(SUBSYSTEM_REQUIREMENT, BRANCH_C), NAME, ANNOTATION, WORDML);
      assertContains(artTypes.getAttributeTypes(SUBSYSTEM_REQUIREMENT, BRANCH_D), NAME, ANNOTATION, WORDML, FIELD_1);
      assertContains(artTypes.getAttributeTypes(SUBSYSTEM_REQUIREMENT, BRANCH_E), NAME, ANNOTATION, WORDML);

      assertContains(artTypes.getAttributeTypes(LAST_ARTIFACT, CoreBranches.SYSTEM_ROOT), NAME, ANNOTATION, WORDML);
      assertContains(artTypes.getAttributeTypes(LAST_ARTIFACT, BRANCH_A), NAME, ANNOTATION, WORDML, FIELD_1);
      assertContains(artTypes.getAttributeTypes(LAST_ARTIFACT, BRANCH_B), NAME, ANNOTATION, WORDML, FIELD_2);
      assertContains(artTypes.getAttributeTypes(LAST_ARTIFACT, BRANCH_C), NAME, ANNOTATION, WORDML);
      assertContains(artTypes.getAttributeTypes(LAST_ARTIFACT, BRANCH_D), NAME, ANNOTATION, WORDML, FIELD_1);
      assertContains(artTypes.getAttributeTypes(LAST_ARTIFACT, BRANCH_E), NAME, ANNOTATION, WORDML, FIELD_2);
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
      IArtifactType artifactType = artTypes.get(35L);

      assertEquals("Added Artifact Type", artifactType.getName());
      assertEquals(Long.valueOf(35), artifactType.getGuid());

      assertEquals(false, artTypes.isAbstract(artifactType));
      assertEquals(true, artTypes.inheritsFrom(artifactType, OTHER_ARTIFACT));
      assertEquals(true, artTypes.inheritsFrom(artifactType, ARTIFACT));
      assertEquals(false, artTypes.inheritsFrom(artifactType, REQUIREMENT));

      assertEquals(true, artTypes.exists(artifactType));
   }

   @Test
   public void testArtifactTypeOverride() {
      ArtifactTypes artTypes = orcsTypes.getArtifactTypes();

      assertEquals(7, artTypes.size());

      assertContains(artTypes.getAttributeTypes(OTHER_ARTIFACT, CoreBranches.SYSTEM_ROOT), NAME, ANNOTATION);

      //@formatter:off
      assertContains(artTypes.getAttributeTypes(SUBSYSTEM_REQUIREMENT, CoreBranches.SYSTEM_ROOT), NAME, ANNOTATION, WORDML);
      assertContains(artTypes.getAttributeTypes(SUBSYSTEM_REQUIREMENT, BRANCH_A), NAME, ANNOTATION, WORDML, FIELD_1);
      assertContains(artTypes.getAttributeTypes(SUBSYSTEM_REQUIREMENT, BRANCH_B), NAME, ANNOTATION, WORDML);
      assertContains(artTypes.getAttributeTypes(SUBSYSTEM_REQUIREMENT, BRANCH_C), NAME, ANNOTATION, WORDML);
      assertContains(artTypes.getAttributeTypes(SUBSYSTEM_REQUIREMENT, BRANCH_D), NAME, ANNOTATION, WORDML, FIELD_1);
      assertContains(artTypes.getAttributeTypes(SUBSYSTEM_REQUIREMENT, BRANCH_E), NAME, ANNOTATION, WORDML);
      //@formatter:on

      //@formatter:off
      String overrideArtTypes =
         "\n overrides artifactType \"Artifact\" {\n" +
         "      inheritAll \n" +
         "      update attribute \"Annotation\" branchUuid "+BRANCH_A_UUID+"\n" +
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

      assertContains(artTypes.getAttributeTypes(OTHER_ARTIFACT, CoreBranches.SYSTEM_ROOT), NAME, FIELD_2);
      assertContains(artTypes.getAttributeTypes(OTHER_ARTIFACT, BRANCH_A), NAME, ANNOTATION, FIELD_2);
      assertContains(artTypes.getAttributeTypes(OTHER_ARTIFACT, BRANCH_B), NAME, FIELD_2);
      assertContains(artTypes.getAttributeTypes(OTHER_ARTIFACT, BRANCH_C), NAME, FIELD_2);
      assertContains(artTypes.getAttributeTypes(OTHER_ARTIFACT, BRANCH_D), NAME, ANNOTATION, FIELD_2);
      assertContains(artTypes.getAttributeTypes(OTHER_ARTIFACT, BRANCH_E), NAME, FIELD_2);

      assertContains(artTypes.getAttributeTypes(SUBSYSTEM_REQUIREMENT, CoreBranches.SYSTEM_ROOT), NAME, WORDML,
         FIELD_2);
      assertContains(artTypes.getAttributeTypes(SUBSYSTEM_REQUIREMENT, BRANCH_A), NAME, ANNOTATION, WORDML, FIELD_2);
      assertContains(artTypes.getAttributeTypes(SUBSYSTEM_REQUIREMENT, BRANCH_B), NAME, WORDML, FIELD_2);
      assertContains(artTypes.getAttributeTypes(SUBSYSTEM_REQUIREMENT, BRANCH_C), NAME, WORDML, FIELD_2);
      assertContains(artTypes.getAttributeTypes(SUBSYSTEM_REQUIREMENT, BRANCH_D), NAME, ANNOTATION, WORDML, FIELD_2);
      assertContains(artTypes.getAttributeTypes(SUBSYSTEM_REQUIREMENT, BRANCH_E), NAME, WORDML, FIELD_2);
   }

   @Test
   public void testGetAllAttributeTypes() {
      AttributeTypes attrTypes = orcsTypes.getAttributeTypes();

      assertEquals(5, attrTypes.size());
      assertEquals(false, attrTypes.isEmpty());

      //@formatter:off
      assertContains(attrTypes.getAll(), NAME, ANNOTATION, WORDML, FIELD_1, FIELD_2);
      //@formatter:on
   }

   @Test
   public void testGetAttributeTypesByUuid() {
      AttributeTypes attrTypes = orcsTypes.getAttributeTypes();

      assertEquals(NAME, attrTypes.get(NAME));
      assertEquals(ANNOTATION, attrTypes.get(ANNOTATION));
      assertEquals(WORDML, attrTypes.get(WORDML));
      assertEquals(FIELD_1, attrTypes.get(FIELD_1));
      assertEquals(FIELD_2, attrTypes.get(FIELD_2));
   }

   @Test
   public void testExistsAttributeTypes() {
      AttributeTypes attrTypes = orcsTypes.getAttributeTypes();

      assertEquals(true, attrTypes.exists(NAME));
      assertEquals(true, attrTypes.exists(ANNOTATION));
      assertEquals(true, attrTypes.exists(WORDML));
      assertEquals(true, attrTypes.exists(FIELD_1));
      assertEquals(true, attrTypes.exists(FIELD_2));
   }

   @Test
   public void testGetAttributeProviderId() {
      AttributeTypes attrTypes = orcsTypes.getAttributeTypes();
      assertEquals("DefaultAttributeDataProvider", attrTypes.getAttributeProviderId(NAME));
      assertEquals("UriAttributeDataProvider", attrTypes.getAttributeProviderId(ANNOTATION));
      assertEquals("UriAttributeDataProvider", attrTypes.getAttributeProviderId(WORDML));
      assertEquals("DefaultAttributeDataProvider", attrTypes.getAttributeProviderId(FIELD_1));
      assertEquals("UriAttributeDataProvider", attrTypes.getAttributeProviderId(FIELD_2));
   }

   @Test
   public void testGetBaseAttributeTypeId() {
      AttributeTypes attrTypes = orcsTypes.getAttributeTypes();

      //@formatter:off
      assertEquals("StringAttribute", attrTypes.getBaseAttributeTypeId(NAME));
      assertEquals("CompressedContentAttribute", attrTypes.getBaseAttributeTypeId(ANNOTATION));
      assertEquals("WordAttribute", attrTypes.getBaseAttributeTypeId(WORDML));
      assertEquals("EnumeratedAttribute", attrTypes.getBaseAttributeTypeId(FIELD_1));
      assertEquals("DateAttribute", attrTypes.getBaseAttributeTypeId(FIELD_2));
      //@formatter:on
   }

   @Test
   public void testGetDefaultValue() {
      AttributeTypes attrTypes = orcsTypes.getAttributeTypes();

      //@formatter:off
      assertEquals("unnamed", attrTypes.getDefaultValue(NAME));
      assertEquals(null, attrTypes.getDefaultValue(ANNOTATION));
      assertEquals("<w:p xmlns:w=\"http://schemas.microsoft.com/office/word/2003/wordml\"><w:r><w:t></w:t></w:r></w:p>", attrTypes.getDefaultValue(WORDML));
      assertEquals("this is a field", attrTypes.getDefaultValue(FIELD_1));
      assertEquals("field2", attrTypes.getDefaultValue(FIELD_2));
      //@formatter:on
   }

   @Test
   public void testGetDescription() {
      AttributeTypes attrTypes = orcsTypes.getAttributeTypes();

      //@formatter:off
      assertEquals("Descriptive Name", attrTypes.getDescription(NAME));
      assertEquals("the version \'1.0\' is this \"1.2.0\"", attrTypes.getDescription(ANNOTATION));
      assertEquals("value must comply with WordML xml schema", attrTypes.getDescription(WORDML));
      assertEquals("", attrTypes.getDescription(FIELD_1));
      assertEquals("field 2 description", attrTypes.getDescription(FIELD_2));
      //@formatter:on
   }

   @Test
   public void testGetFileExtension() {
      AttributeTypes attrTypes = orcsTypes.getAttributeTypes();

      //@formatter:off
      assertEquals("", attrTypes.getFileTypeExtension(NAME));
      assertEquals("", attrTypes.getFileTypeExtension(ANNOTATION));
      assertEquals("xml", attrTypes.getFileTypeExtension(WORDML));
      assertEquals("", attrTypes.getFileTypeExtension(FIELD_1));
      assertEquals("hello", attrTypes.getFileTypeExtension(FIELD_2));
      //@formatter:on
   }

   @Test
   public void testGetMinOccurrence() {
      AttributeTypes attrTypes = orcsTypes.getAttributeTypes();

      //@formatter:off
      assertEquals(1, attrTypes.getMinOccurrences(NAME));
      assertEquals(0, attrTypes.getMinOccurrences(ANNOTATION));
      assertEquals(0, attrTypes.getMinOccurrences(WORDML));
      assertEquals(2, attrTypes.getMinOccurrences(FIELD_1));
      assertEquals(1, attrTypes.getMinOccurrences(FIELD_2));
      //@formatter:on
   }

   @Test
   public void testGetMaxOccurrences() {
      AttributeTypes attrTypes = orcsTypes.getAttributeTypes();

      //@formatter:off
      assertEquals(1, attrTypes.getMaxOccurrences(NAME));
      assertEquals(Integer.MAX_VALUE, attrTypes.getMaxOccurrences(ANNOTATION));
      assertEquals(1, attrTypes.getMaxOccurrences(WORDML));
      assertEquals(3, attrTypes.getMaxOccurrences(FIELD_1));
      assertEquals(1, attrTypes.getMaxOccurrences(FIELD_2));
      //@formatter:on
   }

   @Test
   public void testGetTaggerId() {
      AttributeTypes attrTypes = orcsTypes.getAttributeTypes();

      //@formatter:off
      assertEquals("DefaultAttributeTaggerProvider", attrTypes.getTaggerId(NAME));
      assertEquals("DefaultAttributeTaggerProvider", attrTypes.getTaggerId(ANNOTATION));
      assertEquals("XmlAttributeTaggerProvider", attrTypes.getTaggerId(WORDML));
      assertEquals("", attrTypes.getTaggerId(FIELD_1));
      assertEquals("SomeOtherTagger", attrTypes.getTaggerId(FIELD_2));
      //@formatter:on
   }

   @Test
   public void testGetMediaType() {
      AttributeTypes attrTypes = orcsTypes.getAttributeTypes();

      //@formatter:off
      assertEquals("plan/text", attrTypes.getMediaType(NAME));
      assertEquals("plan/text", attrTypes.getMediaType(ANNOTATION));
      assertEquals("application/xml", attrTypes.getMediaType(WORDML));
      assertEquals("application/custom", attrTypes.getMediaType(FIELD_1));
      assertEquals("**", attrTypes.getMediaType(FIELD_2));
      //@formatter:on
   }

   @Test
   public void testGetAllTaggable() {
      AttributeTypes attrTypes = orcsTypes.getAttributeTypes();
      assertContains(attrTypes.getAllTaggable(), NAME, ANNOTATION, WORDML, FIELD_2);
   }

   @Test
   public void testGetOseeEnum() {
      AttributeTypes attrTypes = orcsTypes.getAttributeTypes();

      EnumType enumType = attrTypes.getEnumType(FIELD_1);

      assertEquals("enum.test.proc.status", enumType.getName());
      assertEquals(Long.valueOf(3458764513820541304L), enumType.getId());

      EnumEntry[] values = enumType.values();

      assertEnumEntry(values[0], "Completed -- Analysis in Work", 1, "");
      assertEnumEntry(values[1], "Completed -- Passed", 2, "");
      assertEnumEntry(values[2], "Completed -- With Issues", 3, "");
      assertEnumEntry(values[3], "Completed -- With Issues Resolved", 4, "");
      assertEnumEntry(values[4], "Not Performed", 0, "it was not performed");
      assertEnumEntry(values[5], "Partially Complete", 5, "is a partial");

      assertEnumEntry(enumType.valueOf(0), "Not Performed", 0, "it was not performed");
      assertEnumEntry(enumType.valueOf(1), "Completed -- Analysis in Work", 1, "");
      assertEnumEntry(enumType.valueOf(2), "Completed -- Passed", 2, "");
      assertEnumEntry(enumType.valueOf(3), "Completed -- With Issues", 3, "");
      assertEnumEntry(enumType.valueOf(4), "Completed -- With Issues Resolved", 4, "");
      assertEnumEntry(enumType.valueOf(5), "Partially Complete", 5, "is a partial");

      //@formatter:off
      assertEnumEntry(enumType.valueOf("Not Performed"), "Not Performed",  0, "it was not performed");
      assertEnumEntry(enumType.valueOf("Completed -- Analysis in Work"), "Completed -- Analysis in Work",  1, "");
      assertEnumEntry(enumType.valueOf("Completed -- Passed"), "Completed -- Passed",2, "");
      assertEnumEntry(enumType.valueOf("Completed -- With Issues"), "Completed -- With Issues",  3, "");
      assertEnumEntry(enumType.valueOf("Completed -- With Issues Resolved"), "Completed -- With Issues Resolved",  4, "");
      assertEnumEntry(enumType.valueOf("Partially Complete"), "Partially Complete",  5, "is a partial");
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
      assertEquals(ordinal, actual.ordinal());
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

      EnumType enumType = attrTypes.getEnumType(FIELD_1);

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
      assertEquals("AnotherTagger", attrTypes.getTaggerId(attrType));
      assertEquals(null, attrTypes.getEnumType(attrType));
      assertEquals(false, attrTypes.isEnumerated(attrType));
      assertEquals(true, attrTypes.isTaggable(attrType));
      assertEquals(true, attrTypes.exists(attrType));
   }

   @Test
   public void testGetAllRelationTypes() {
      RelationTypes relTypes = orcsTypes.getRelationTypes();

      assertEquals(2, relTypes.size());
      assertEquals(false, relTypes.isEmpty());

      //@formatter:off
      assertContains(relTypes.getAll(), REQUIREMENT_REL, ANOTHER_REL);
      //@formatter:on
   }

   @Test
   public void testGetRelationTypesByUuid() {
      RelationTypes relTypes = orcsTypes.getRelationTypes();

      assertEquals(REQUIREMENT_REL, relTypes.get(REQUIREMENT_REL));
      assertEquals(ANOTHER_REL, relTypes.get(ANOTHER_REL));
   }

   @Test
   public void testExistsRelationTypes() {
      RelationTypes relTypes = orcsTypes.getRelationTypes();

      assertTrue(relTypes.exists(REQUIREMENT_REL));
      assertTrue(relTypes.exists(ANOTHER_REL));
   }

   @Test
   public void testGetArtifactType() {
      RelationTypes relTypes = orcsTypes.getRelationTypes();

      assertEquals(REQUIREMENT, relTypes.getArtifactType(REQUIREMENT_REL, SIDE_A));
      assertEquals(SUBSYSTEM_REQUIREMENT, relTypes.getArtifactType(REQUIREMENT_REL, SIDE_B));

      assertEquals(OTHER_ARTIFACT, relTypes.getArtifactType(ANOTHER_REL, SIDE_A));
      assertEquals(LAST_ARTIFACT, relTypes.getArtifactType(ANOTHER_REL, SIDE_B));
   }

   @Test
   public void testGetArtifactTypeSideA() {
      RelationTypes relTypes = orcsTypes.getRelationTypes();

      assertEquals(REQUIREMENT, relTypes.getArtifactTypeSideA(REQUIREMENT_REL));
      assertEquals(OTHER_ARTIFACT, relTypes.getArtifactTypeSideA(ANOTHER_REL));
   }

   @Test
   public void testGetArtifactTypeSideB() {
      RelationTypes relTypes = orcsTypes.getRelationTypes();

      assertEquals(SUBSYSTEM_REQUIREMENT, relTypes.getArtifactTypeSideB(REQUIREMENT_REL));
      assertEquals(LAST_ARTIFACT, relTypes.getArtifactTypeSideB(ANOTHER_REL));
   }

   @Test
   public void testGetDefaultOrderTypeGuid() {
      RelationTypes relTypes = orcsTypes.getRelationTypes();

      //@formatter:off
      assertEquals(LEXICOGRAPHICAL_ASC, relTypes.getDefaultOrderTypeGuid(REQUIREMENT_REL));
      assertEquals(UNORDERED, relTypes.getDefaultOrderTypeGuid(ANOTHER_REL));
      //@formatter:on
   }

   @Test
   public void testGetMultiplicity() {
      RelationTypes relTypes = orcsTypes.getRelationTypes();

      assertEquals(RelationTypeMultiplicity.ONE_TO_MANY, relTypes.getMultiplicity(REQUIREMENT_REL));
      assertEquals(RelationTypeMultiplicity.MANY_TO_MANY, relTypes.getMultiplicity(ANOTHER_REL));
   }

   @Test
   public void testGetSideNameA() {
      RelationTypes relTypes = orcsTypes.getRelationTypes();

      assertEquals("requirement-sideA", relTypes.getSideAName(REQUIREMENT_REL));
      assertEquals("other-sideA", relTypes.getSideAName(ANOTHER_REL));
   }

   @Test
   public void testGetSideNameB() {
      RelationTypes relTypes = orcsTypes.getRelationTypes();

      assertEquals("subsystem-sideB", relTypes.getSideBName(REQUIREMENT_REL));
      assertEquals("last-sideB", relTypes.getSideBName(ANOTHER_REL));
   }

   @Test
   public void testGetSideName() {
      RelationTypes relTypes = orcsTypes.getRelationTypes();

      assertEquals("requirement-sideA", relTypes.getSideName(REQUIREMENT_REL, SIDE_A));
      assertEquals("subsystem-sideB", relTypes.getSideName(REQUIREMENT_REL, SIDE_B));

      assertEquals("other-sideA", relTypes.getSideName(ANOTHER_REL, SIDE_A));
      assertEquals("last-sideB", relTypes.getSideName(ANOTHER_REL, SIDE_B));
   }

   @Test
   public void testIsSideName() {
      RelationTypes relTypes = orcsTypes.getRelationTypes();

      assertEquals(true, relTypes.isSideAName(REQUIREMENT_REL, "requirement-sideA"));
      assertEquals(false, relTypes.isSideAName(REQUIREMENT_REL, "subsystem-sideB"));

      assertEquals(true, relTypes.isSideAName(ANOTHER_REL, "other-sideA"));
      assertEquals(false, relTypes.isSideAName(ANOTHER_REL, "last-sideB"));
   }

   @Test
   public void testIsOrdered() {
      RelationTypes relTypes = orcsTypes.getRelationTypes();

      assertEquals(true, relTypes.isOrdered(REQUIREMENT_REL));
      assertEquals(false, relTypes.isOrdered(ANOTHER_REL));
   }

   @Test
   public void testIsArtifactTypeAllowed() {
      RelationTypes relTypes = orcsTypes.getRelationTypes();

      assertEquals(false, relTypes.isArtifactTypeAllowed(REQUIREMENT_REL, SIDE_A, ARTIFACT));
      assertEquals(true, relTypes.isArtifactTypeAllowed(REQUIREMENT_REL, SIDE_A, REQUIREMENT));
      assertEquals(true, relTypes.isArtifactTypeAllowed(REQUIREMENT_REL, SIDE_A, SOFTWARE_REQUIREMENT));
      assertEquals(true, relTypes.isArtifactTypeAllowed(REQUIREMENT_REL, SIDE_A, SYSTEM_REQUIREMENT));
      assertEquals(true, relTypes.isArtifactTypeAllowed(REQUIREMENT_REL, SIDE_A, SUBSYSTEM_REQUIREMENT));
      assertEquals(false, relTypes.isArtifactTypeAllowed(REQUIREMENT_REL, SIDE_A, OTHER_ARTIFACT));
      assertEquals(true, relTypes.isArtifactTypeAllowed(REQUIREMENT_REL, SIDE_A, LAST_ARTIFACT));

      assertEquals(false, relTypes.isArtifactTypeAllowed(REQUIREMENT_REL, SIDE_B, ARTIFACT));
      assertEquals(false, relTypes.isArtifactTypeAllowed(REQUIREMENT_REL, SIDE_B, REQUIREMENT));
      assertEquals(false, relTypes.isArtifactTypeAllowed(REQUIREMENT_REL, SIDE_B, SOFTWARE_REQUIREMENT));
      assertEquals(false, relTypes.isArtifactTypeAllowed(REQUIREMENT_REL, SIDE_B, SYSTEM_REQUIREMENT));
      assertEquals(true, relTypes.isArtifactTypeAllowed(REQUIREMENT_REL, SIDE_B, SUBSYSTEM_REQUIREMENT));
      assertEquals(false, relTypes.isArtifactTypeAllowed(REQUIREMENT_REL, SIDE_B, OTHER_ARTIFACT));
      assertEquals(true, relTypes.isArtifactTypeAllowed(REQUIREMENT_REL, SIDE_B, LAST_ARTIFACT));

      assertEquals(false, relTypes.isArtifactTypeAllowed(ANOTHER_REL, SIDE_A, ARTIFACT));
      assertEquals(false, relTypes.isArtifactTypeAllowed(ANOTHER_REL, SIDE_A, REQUIREMENT));
      assertEquals(false, relTypes.isArtifactTypeAllowed(ANOTHER_REL, SIDE_A, SOFTWARE_REQUIREMENT));
      assertEquals(false, relTypes.isArtifactTypeAllowed(ANOTHER_REL, SIDE_A, SYSTEM_REQUIREMENT));
      assertEquals(true, relTypes.isArtifactTypeAllowed(ANOTHER_REL, SIDE_A, SUBSYSTEM_REQUIREMENT));
      assertEquals(true, relTypes.isArtifactTypeAllowed(ANOTHER_REL, SIDE_A, OTHER_ARTIFACT));
      assertEquals(true, relTypes.isArtifactTypeAllowed(ANOTHER_REL, SIDE_A, LAST_ARTIFACT));

      assertEquals(false, relTypes.isArtifactTypeAllowed(ANOTHER_REL, SIDE_B, ARTIFACT));
      assertEquals(false, relTypes.isArtifactTypeAllowed(ANOTHER_REL, SIDE_B, REQUIREMENT));
      assertEquals(false, relTypes.isArtifactTypeAllowed(ANOTHER_REL, SIDE_B, SOFTWARE_REQUIREMENT));
      assertEquals(false, relTypes.isArtifactTypeAllowed(ANOTHER_REL, SIDE_B, SYSTEM_REQUIREMENT));
      assertEquals(false, relTypes.isArtifactTypeAllowed(ANOTHER_REL, SIDE_B, SUBSYSTEM_REQUIREMENT));
      assertEquals(false, relTypes.isArtifactTypeAllowed(ANOTHER_REL, SIDE_B, OTHER_ARTIFACT));
      assertEquals(true, relTypes.isArtifactTypeAllowed(ANOTHER_REL, SIDE_B, LAST_ARTIFACT));
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

      assertEquals(ARTIFACT, relTypes.getArtifactType(relation, SIDE_A));
      assertEquals(OTHER_ARTIFACT, relTypes.getArtifactType(relation, SIDE_B));
      assertEquals(ARTIFACT, relTypes.getArtifactTypeSideA(relation));
      assertEquals(OTHER_ARTIFACT, relTypes.getArtifactTypeSideB(relation));
      assertEquals(LEXICOGRAPHICAL_DESC, relTypes.getDefaultOrderTypeGuid(relation));
      assertEquals(RelationTypeMultiplicity.MANY_TO_ONE, relTypes.getMultiplicity(relation));
      assertEquals("dynamic-sideA", relTypes.getSideName(relation, SIDE_A));
      assertEquals("dynamic-sideB", relTypes.getSideName(relation, SIDE_B));
      assertEquals("dynamic-sideA", relTypes.getSideAName(relation));
      assertEquals("dynamic-sideB", relTypes.getSideBName(relation));
      assertEquals(true, relTypes.isOrdered(relation));
      assertEquals(true, relTypes.isSideAName(relation, "dynamic-sideA"));
      assertEquals(false, relTypes.isSideAName(relation, "dynamic-sideB"));
      assertEquals(true, relTypes.isArtifactTypeAllowed(relation, SIDE_A, LAST_ARTIFACT));
      assertEquals(false, relTypes.isArtifactTypeAllowed(relation, SIDE_B, REQUIREMENT));
      assertEquals(true, relTypes.isArtifactTypeAllowed(relation, SIDE_B, OTHER_ARTIFACT));
      assertEquals(true, relTypes.isArtifactTypeAllowed(relation, SIDE_B, LAST_ARTIFACT));

      assertEquals(true, relTypes.exists(relation));
   }

   private static void assertContains(Collection<?> actual, Id... expected) {
      List<?> asList = Arrays.asList(expected);

      String message = String.format("Actual: [%s] Expected: [%s]", actual, Arrays.deepToString(expected));

      assertEquals(message, asList.size(), actual.size());
      assertEquals(message, true, actual.containsAll(asList));
   }

   private static InputSupplier<? extends InputStream> getResource(String resourcePath) {
      URL resource = Resources.getResource(OrcsTypesTest.class, resourcePath);
      return Resources.newInputStreamSupplier(resource);
   }

   private static InputSupplier<? extends InputStream> asInput(final String data) {
      return new InputSupplier<InputStream>() {
         @Override
         public InputStream getInput() throws java.io.IOException {
            return new ByteArrayInputStream(data.getBytes("UTF-8"));
         }
      };
   }
   private static final class MultiResource implements IResource {
      private final Iterable<? extends InputSupplier<? extends InputStream>> suppliers;
      private final URI resourceUri;

      public MultiResource(URI resourceUri, Iterable<? extends InputSupplier<? extends InputStream>> suppliers) {
         super();
         this.suppliers = suppliers;
         this.resourceUri = resourceUri;
      }

      @Override
      public InputStream getContent() {
         try {
            return ByteStreams.join(suppliers).getInput();
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
