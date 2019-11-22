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
package org.eclipse.osee.orcs.core.internal.graph.impl;

import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.SoftwareRequirementMsWord;
import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.Allocation_Component;
import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.CodeRequirement_CodeUnit;
import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.DefaultHierarchical_Child;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.OrcsMockUtility;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.ds.LoadDescription;
import org.eclipse.osee.orcs.core.ds.RelationData;
import org.eclipse.osee.orcs.core.internal.artifact.Artifact;
import org.eclipse.osee.orcs.core.internal.artifact.ArtifactFactory;
import org.eclipse.osee.orcs.core.internal.attribute.AttributeFactory;
import org.eclipse.osee.orcs.core.internal.graph.GraphData;
import org.eclipse.osee.orcs.core.internal.graph.GraphProvider;
import org.eclipse.osee.orcs.core.internal.relation.Relation;
import org.eclipse.osee.orcs.core.internal.relation.RelationFactory;
import org.eclipse.osee.orcs.core.internal.relation.impl.RelationNodeAdjacencies;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * @author Megumi Telles
 */
public class GraphDataImplTest {
   private static final ArtifactId artifactId20 = ArtifactId.valueOf(20);
   private static final ArtifactId artifactId21 = ArtifactId.valueOf(21);
   private static final ArtifactId artifactId30 = ArtifactId.valueOf(30);
   private static final ArtifactId artifactId31 = ArtifactId.valueOf(31);
   private static final TransactionId TRANSACTION_ID = TransactionId.valueOf(231214214);

   @Rule
   public ExpectedException thrown = ExpectedException.none();

   // @formatter:off
   @Mock private Log logger;
   @Mock private ArtifactFactory artifactFactory;
   @Mock private AttributeFactory attributeFactory;
   @Mock private RelationFactory relationFactory;

   @Mock private GraphProvider graphProvider;
   @Mock private GraphData graphData;

   @Mock private LoadDescription description;

   @Mock private AttributeData attributeData;
   @Mock private AttributeData attributeData1;
   @Mock private AttributeData attributeData2;
   @Mock private RelationData relationData;

   @Mock private Artifact container;

   @Mock private Relation relation;
   @Mock private Relation relation1;
   @Mock private Relation relation2;
   @Mock private Relation relation3;
   // @formatter:on

   private final BranchId branch = CoreBranches.COMMON;
   private Artifact artifact1;
   private Artifact artifact2;
   private Artifact container1;
   private Artifact container2;
   private ArtifactData artifactData1;
   private ArtifactData artifactData2;

   private GraphDataImpl graph;

   @Before
   public void setUp() throws Exception {
      MockitoAnnotations.initMocks(this);

      graph = new GraphDataImpl(null, branch, TRANSACTION_ID);

      artifact1 = OrcsMockUtility.createTestArtifact(branch, SoftwareRequirementMsWord, artifactId20.getId(), "artifact1");
      artifact2 = OrcsMockUtility.createTestArtifact(branch, SoftwareRequirementMsWord, artifactId21.getId(), "artifact2");
      container1 = OrcsMockUtility.createTestArtifact(branch, SoftwareRequirementMsWord, artifactId30.getId(), "container1");
      container2 = OrcsMockUtility.createTestArtifact(branch, SoftwareRequirementMsWord, artifactId31.getId(), "container2");

      artifactData1 = artifact1.getOrcsData();
      artifactData2 = artifact2.getOrcsData();
   }

   @Test
   public void testGetBranchUuid() {
      assertTrue(graph.isOnBranch(branch));
   }

   @Test
   public void testGetTransaction() {
      assertEquals(TRANSACTION_ID, graph.getTransaction());
   }

   @Test
   public void testAddNodeArtifact() {
      graph.addNode(artifact1, false);
      assertEquals(artifact1, graph.getNode(artifact1));
      assertEquals(artifact1, graph.getNode(artifactData1));
   }

   @Test
   public void testGetNodeId() {
      graph.addNode(artifact1, false);
      graph.addNode(artifact2, false);
      graph.addNode(container1, false);
      graph.addNode(container2, false);

      assertEquals(artifact1, graph.getNode(artifactId20));
      assertEquals(artifact2, graph.getNode(artifactId21));
      assertEquals(container1, graph.getNode(artifactId30));
      assertEquals(container2, graph.getNode(artifactId31));
   }

   @Test
   public void testGetNodeData() {
      graph.addNode(artifact1, false);
      graph.addNode(artifact2, false);
      graph.addNode(container1, false);
      graph.addNode(container2, false);

      assertEquals(artifact1, graph.getNode(artifactData1));
      assertEquals(artifact2, graph.getNode(artifactData2));
   }

   @Test
   public void testRemoveNode() {
      graph.addNode(artifact1, false);
      graph.addNode(artifact2, false);
      graph.addNode(container1, false);
      graph.addNode(container2, false);

      assertEquals(artifact1, graph.removeNode(artifactData1));
      assertNull(graph.getNode(artifactData1));
      assertEquals(artifact2, graph.removeNode(artifact2));
      assertNull(graph.getNode(artifactData2));
      assertEquals(container1, graph.removeNode(container1));
   }

   @Test
   public void testAddAdjacencies() {
      RelationNodeAdjacencies adjacencies = new RelationNodeAdjacencies();
      RelationNodeAdjacencies adj;
      Collection<Relation> all;

      adjacencies.add(Allocation_Component, relation1);
      graph.addAdjacencies(artifact1, adjacencies);
      adj = graph.getAdjacencies(artifactId20);
      List<Relation> list = adj.getList(Allocation_Component, DeletionFlag.EXCLUDE_DELETED);
      assertFalse(list.isEmpty());
      assertTrue(list.size() == 1);

      adjacencies.add(CodeRequirement_CodeUnit, relation2);
      graph.addAdjacencies(artifactId20, adjacencies);
      adj = graph.getAdjacencies(artifact1);
      all = adj.getAll();
      assertFalse(all.isEmpty());
      assertTrue(all.size() == 2);
      assertTrue(adj.getList(Allocation_Component, DeletionFlag.EXCLUDE_DELETED).size() == 1);
      assertTrue(adj.getList(CodeRequirement_CodeUnit, DeletionFlag.EXCLUDE_DELETED).size() == 1);

      adjacencies.add(DefaultHierarchical_Child, relation3);
      graph.addAdjacencies(artifactId21, adjacencies);
      adj = graph.getAdjacencies(artifact2);
      all = adj.getAll();
      assertFalse(all.isEmpty());
      assertTrue(all.size() == 3);
      assertTrue(adj.getList(Allocation_Component, DeletionFlag.EXCLUDE_DELETED).size() == 1);
      assertTrue(adj.getList(CodeRequirement_CodeUnit, DeletionFlag.EXCLUDE_DELETED).size() == 1);
      assertTrue(adj.getList(DefaultHierarchical_Child, DeletionFlag.EXCLUDE_DELETED).size() == 1);
   }

   @Test
   public void testRemoveAdjacencies() {
      RelationNodeAdjacencies adjacencies = new RelationNodeAdjacencies();
      RelationNodeAdjacencies adj;

      adjacencies.add(Allocation_Component, relation1);
      adjacencies.add(CodeRequirement_CodeUnit, relation2);
      adjacencies.add(DefaultHierarchical_Child, relation3);

      graph.addAdjacencies(artifact1, adjacencies);
      graph.addAdjacencies(artifactId20, adjacencies);
      graph.addAdjacencies(artifact2, adjacencies);

      graph.removeAdjacencies(artifact2);
      adj = graph.getAdjacencies(artifactId21);
      assertNull(adj);

      graph.removeAdjacencies(artifactId20);
      adj = graph.getAdjacencies(artifactId20);
      assertNull(adj);
   }
}