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

import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.Allocation__Component;
import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.CodeRequirement_CodeUnit;
import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.Default_Hierarchical__Child;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
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

   @Mock private ArtifactData artifactData;
   @Mock private ArtifactData artifactData1;
   @Mock private ArtifactData artifactData2;
   @Mock private AttributeData attributeData;
   @Mock private AttributeData attributeData1;
   @Mock private AttributeData attributeData2;
   @Mock private RelationData relationData;

   @Mock private Artifact artifact;
   @Mock private Artifact artifact1;
   @Mock private Artifact artifact2;
   @Mock private Artifact container;
   @Mock private Artifact container1;
   @Mock private Artifact container2;

   @Mock private Relation relation;
   @Mock private Relation relation1;
   @Mock private Relation relation2;
   @Mock private Relation relation3;

   @Mock private OrcsSession session;
   private final BranchId branch = CoreBranches.COMMON;

   // @formatter:on

   private GraphDataImpl graph;
   private static final TransactionId TRANSACTION_ID = TransactionId.valueOf(231214214);

   @Before
   public void setUp() throws Exception {
      MockitoAnnotations.initMocks(this);

      graph = new GraphDataImpl(session, branch, TRANSACTION_ID);
      when(artifact.getBranch()).thenReturn(branch);
      when(artifact1.getBranch()).thenReturn(branch);
      when(artifact2.getBranch()).thenReturn(branch);
      when(container.getBranch()).thenReturn(branch);
      when(container1.getBranch()).thenReturn(branch);
      when(container2.getBranch()).thenReturn(branch);
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
      when(artifact.getLocalId()).thenReturn(10);
      when(artifactData.getLocalId()).thenReturn(10);

      graph.addNode(artifact, false);
      assertEquals(artifact, graph.getNode(10));
      assertEquals(artifact, graph.getNode(artifactData));
   }

   @Test
   public void testAddNodeAttribute() {
      when(container.getLocalId()).thenReturn(11);
      when(attributeData.getLocalId()).thenReturn(11);

      graph.addNode(container, false);
      assertEquals(container, graph.getNode(11));
      assertEquals(container, graph.getNode(attributeData));
   }

   @Test
   public void testGetNodeId() {
      when(artifact1.getLocalId()).thenReturn(20);
      when(artifact2.getLocalId()).thenReturn(21);
      when(container1.getLocalId()).thenReturn(30);
      when(container2.getLocalId()).thenReturn(31);

      graph.addNode(artifact1, false);
      graph.addNode(artifact2, false);
      graph.addNode(container1, false);
      graph.addNode(container2, false);

      verify(artifact1).getLocalId();
      verify(artifact2).getLocalId();
      verify(container1).getLocalId();
      verify(container2).getLocalId();

      assertEquals(artifact1, graph.getNode(20));
      assertEquals(artifact2, graph.getNode(21));
      assertEquals(container1, graph.getNode(30));
      assertEquals(container2, graph.getNode(31));
   }

   @Test
   public void testGetNodeData() {
      when(artifact1.getLocalId()).thenReturn(20);
      when(artifact2.getLocalId()).thenReturn(21);
      when(container1.getLocalId()).thenReturn(30);
      when(container2.getLocalId()).thenReturn(31);

      when(artifactData1.getLocalId()).thenReturn(20);
      when(artifactData2.getLocalId()).thenReturn(21);
      when(attributeData1.getLocalId()).thenReturn(30);
      when(attributeData2.getLocalId()).thenReturn(31);

      graph.addNode(artifact1, false);
      graph.addNode(artifact2, false);
      graph.addNode(container1, false);
      graph.addNode(container2, false);

      verify(artifact1).getLocalId();
      verify(artifact2).getLocalId();
      verify(container1).getLocalId();
      verify(container2).getLocalId();

      assertEquals(artifact1, graph.getNode(artifactData1));
      assertEquals(artifact2, graph.getNode(artifactData2));
      assertEquals(container1, graph.getNode(attributeData1));
      assertEquals(container2, graph.getNode(attributeData2));
   }

   @Test
   public void testRemoveNode() {
      when(artifact1.getLocalId()).thenReturn(20);
      when(artifact2.getLocalId()).thenReturn(21);
      when(container1.getLocalId()).thenReturn(30);
      when(container2.getLocalId()).thenReturn(31);

      when(artifactData1.getLocalId()).thenReturn(20);
      when(artifactData2.getLocalId()).thenReturn(21);
      when(attributeData1.getLocalId()).thenReturn(30);
      when(attributeData2.getLocalId()).thenReturn(31);

      graph.addNode(artifact1, false);
      graph.addNode(artifact2, false);
      graph.addNode(container1, false);
      graph.addNode(container2, false);

      verify(artifact1).getLocalId();
      verify(artifact2).getLocalId();
      verify(container1).getLocalId();
      verify(container2).getLocalId();

      assertEquals(artifact1, graph.removeNode(artifactData1));
      assertNull(graph.getNode(artifactData1));
      assertEquals(artifact2, graph.removeNode(artifact2));
      assertNull(graph.getNode(artifactData2));
      assertEquals(container1, graph.removeNode(30));
      assertNull(graph.getNode(attributeData1));
      assertEquals(container2, graph.removeNode(container2));
      assertNull(graph.getNode(attributeData2));
   }

   @Test
   public void testAddAdjacencies() {
      RelationNodeAdjacencies adjacencies = new RelationNodeAdjacencies();
      RelationNodeAdjacencies adj;
      Collection<Relation> all;

      when(artifact1.getLocalId()).thenReturn(20);
      when(artifact2.getLocalId()).thenReturn(21);

      adjacencies.add(Allocation__Component, relation1);
      graph.addAdjacencies(artifact1, adjacencies);
      adj = graph.getAdjacencies(20);
      List<Relation> list = adj.getList(Allocation__Component, DeletionFlag.EXCLUDE_DELETED);
      assertFalse(list.isEmpty());
      assertTrue(list.size() == 1);

      adjacencies.add(CodeRequirement_CodeUnit, relation2);
      graph.addAdjacencies(20, adjacencies);
      adj = graph.getAdjacencies(artifact1);
      all = adj.getAll();
      assertFalse(all.isEmpty());
      assertTrue(all.size() == 2);
      assertTrue(adj.getList(Allocation__Component, DeletionFlag.EXCLUDE_DELETED).size() == 1);
      assertTrue(adj.getList(CodeRequirement_CodeUnit, DeletionFlag.EXCLUDE_DELETED).size() == 1);

      adjacencies.add(Default_Hierarchical__Child, relation3);
      graph.addAdjacencies(21, adjacencies);
      adj = graph.getAdjacencies(artifact2);
      all = adj.getAll();
      assertFalse(all.isEmpty());
      assertTrue(all.size() == 3);
      assertTrue(adj.getList(Allocation__Component, DeletionFlag.EXCLUDE_DELETED).size() == 1);
      assertTrue(adj.getList(CodeRequirement_CodeUnit, DeletionFlag.EXCLUDE_DELETED).size() == 1);
      assertTrue(adj.getList(Default_Hierarchical__Child, DeletionFlag.EXCLUDE_DELETED).size() == 1);
   }

   @Test
   public void testRemoveAdjacencies() {
      RelationNodeAdjacencies adjacencies = new RelationNodeAdjacencies();
      RelationNodeAdjacencies adj;

      when(artifact1.getLocalId()).thenReturn(20);
      when(artifact2.getLocalId()).thenReturn(21);

      adjacencies.add(Allocation__Component, relation1);
      adjacencies.add(CodeRequirement_CodeUnit, relation2);
      adjacencies.add(Default_Hierarchical__Child, relation3);

      graph.addAdjacencies(artifact1, adjacencies);
      graph.addAdjacencies(20, adjacencies);
      graph.addAdjacencies(artifact2, adjacencies);

      graph.removeAdjacencies(artifact2);
      adj = graph.getAdjacencies(21);
      assertNull(adj);

      graph.removeAdjacencies(20);
      adj = graph.getAdjacencies(20);
      assertNull(adj);

   }
}
