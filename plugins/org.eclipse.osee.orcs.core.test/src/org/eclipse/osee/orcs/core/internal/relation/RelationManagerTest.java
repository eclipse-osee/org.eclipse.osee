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
package org.eclipse.osee.orcs.core.internal.relation;

import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.SoftwareRequirement;
import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.DEFAULT_HIERARCHY;
import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.IS_CHILD;
import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.IS_PARENT;
import static org.eclipse.osee.framework.core.enums.DeletionFlag.INCLUDE_DELETED;
import static org.eclipse.osee.framework.core.enums.RelationSorter.LEXICOGRAPHICAL_DESC;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.type.ResultSets;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.OrcsMockUtility;
import org.eclipse.osee.orcs.core.ds.RelationData;
import org.eclipse.osee.orcs.core.internal.artifact.Artifact;
import org.eclipse.osee.orcs.core.internal.graph.GraphData;
import org.eclipse.osee.orcs.core.internal.proxy.ExternalArtifactManager;
import org.eclipse.osee.orcs.core.internal.relation.impl.RelationNodeAdjacencies;
import org.eclipse.osee.orcs.core.internal.search.QueryModule.QueryModuleProvider;
import org.eclipse.osee.orcs.data.RelationTypes;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * Test Case for {@link RelationManagerFactory}
 *
 * @author Megumi Telles
 */
public class RelationManagerTest {

   @Rule
   public ExpectedException thrown = ExpectedException.none();

   // @formatter:off
   @Mock private Log logger;
   @Mock private RelationTypes types;
   @Mock private RelationFactory relationFactory;
   @Mock private ExternalArtifactManager proxyManager;
   @Mock private QueryFactory factory;
   @Mock private QueryModuleProvider provider;

   @Mock private RelationNodeLoader loader;
   @Mock private OrcsSession session;
   @Mock private GraphData graph;

   @Mock private RelationNodeAdjacencies adjancies1;
   @Mock private RelationNodeAdjacencies adjancies2;

   @Mock private Relation relation1;
   @Mock private Relation relation2;
   @Mock private Relation relation3;
   @Mock private Relation relation4;

   @Mock private RelationData data1;
   @Mock private RelationData data2;
   @Mock private RelationData data3;
   @Mock private RelationData data4;

   @Captor private ArgumentCaptor<Collection<Integer>> captor;
   // @formatter:on

   private RelationManager manager;
   private Map<Integer, Artifact> mockDb;
   private Artifact node1;
   private Artifact node2;
   private Artifact node3;
   private Artifact node4;
   private Artifact node5;
   private Artifact node6;

   @SuppressWarnings({"unchecked", "rawtypes"})
   @Before
   public void setUp() {
      MockitoAnnotations.initMocks(this);

      String sessionId = GUID.create();
      when(session.getGuid()).thenReturn(sessionId);

      node1 = OrcsMockUtility.createTestArtifact(graph, COMMON, SoftwareRequirement, 11L, "z");
      node2 = OrcsMockUtility.createTestArtifact(graph, COMMON, SoftwareRequirement, 22L, "y");
      node3 = OrcsMockUtility.createTestArtifact(graph, COMMON, SoftwareRequirement, 33L, "x");
      node4 = OrcsMockUtility.createTestArtifact(graph, COMMON, SoftwareRequirement, 44L, "w");
      node5 = OrcsMockUtility.createTestArtifact(graph, COMMON, SoftwareRequirement, 55L, "v");
      node6 = OrcsMockUtility.createTestArtifact(graph, COMMON, SoftwareRequirement, 66L, "u");

      manager = RelationManagerFactory.createRelationManager(logger, types, relationFactory, loader, provider);

      when(loader.loadNodes(eq(session), eq(graph), anyCollectionOf(Integer.class), eq(LoadLevel.ALL))).thenAnswer(
         new LoaderAnswer());

      when(graph.getTransaction()).thenReturn(TransactionId.SENTINEL);

      mockDb = new HashMap<>();
      mockDb.put(11, node1);
      mockDb.put(22, node2);
      mockDb.put(33, node3);
      mockDb.put(44, node4);
      mockDb.put(55, node5);
      mockDb.put(66, node6);

      ArtifactId artifactId11 = ArtifactId.valueOf(11);
      ArtifactId artifactId22 = ArtifactId.valueOf(22);
      ArtifactId artifactId33 = ArtifactId.valueOf(33);
      ArtifactId artifactId44 = ArtifactId.valueOf(44);
      ArtifactId artifactId55 = ArtifactId.valueOf(55);

      when(relation1.getIdForSide(RelationSide.SIDE_A)).thenReturn(artifactId11);
      when(relation1.getIdForSide(RelationSide.SIDE_B)).thenReturn(artifactId22);
      when(relation1.getRelationType()).thenReturn(DEFAULT_HIERARCHY);
      when(relation1.getRationale()).thenReturn("rationale on relation1");
      when(relation1.getOrcsData()).thenReturn(data1);
      when(relation1.getOrcsData().getLocalId()).thenReturn(10);
      when(relation1.getModificationType()).thenReturn(ModificationType.NEW);

      when(relation2.getIdForSide(RelationSide.SIDE_A)).thenReturn(artifactId11);
      when(relation2.getIdForSide(RelationSide.SIDE_B)).thenReturn(artifactId33);
      when(relation2.getRelationType()).thenReturn(DEFAULT_HIERARCHY);
      when(relation2.getRationale()).thenReturn("rationale on relation2");
      when(relation2.getOrcsData()).thenReturn(data2);
      when(relation2.getOrcsData().getLocalId()).thenReturn(11);

      when(relation3.getIdForSide(RelationSide.SIDE_A)).thenReturn(artifactId44);
      when(relation3.getIdForSide(RelationSide.SIDE_B)).thenReturn(artifactId11);
      when(relation3.getRelationType()).thenReturn(DEFAULT_HIERARCHY);
      when(relation3.getRationale()).thenReturn("rationale on relation3");
      when(relation3.getOrcsData()).thenReturn(data3);
      when(relation3.getOrcsData().getLocalId()).thenReturn(12);
      when(relation3.getModificationType()).thenReturn(ModificationType.NEW);

      when(relation4.getIdForSide(RelationSide.SIDE_A)).thenReturn(artifactId11);
      when(relation4.getIdForSide(RelationSide.SIDE_B)).thenReturn(artifactId55);
      when(relation4.getRelationType()).thenReturn(DEFAULT_HIERARCHY);
      when(relation4.getRationale()).thenReturn("rationale on relation4");
      when(relation4.getOrcsData()).thenReturn(data4);
      when(relation4.getOrcsData().getLocalId()).thenReturn(13);
      when(relation4.getModificationType()).thenReturn(ModificationType.NEW);

      setupAdjacencies(node1, relation1, relation2, relation3, relation4);
      setupAdjacencies(node2, relation1);
      setupAdjacencies(node3, relation1);
      setupAdjacencies(node4, relation3);
      setupAdjacencies(node5, relation4);
      setupAdjacencies(node6);

      when(types.getDefaultOrderTypeGuid(Matchers.any())).thenReturn(LEXICOGRAPHICAL_DESC);
      when(types.getAll()).thenReturn(new ArrayList(
         Arrays.asList(CoreRelationTypes.DefaultHierarchical_Child, CoreRelationTypes.DefaultHierarchical_Parent)));

      when(types.get((Id) Matchers.any())).thenReturn(CoreRelationTypes.DefaultHierarchical_Child);
   }

   private void setupAdjacencies(Artifact node, Relation... relations) {
      RelationNodeAdjacencies adjacents = new RelationNodeAdjacencies();
      when(graph.getAdjacencies(node)).thenReturn(adjacents);
      for (Relation relation : relations) {
         adjacents.add(relation.getRelationType(), relation);
      }
   }

   @Test
   public void testGetRelatedOnSideA() {
      ResultSet<Artifact> nodes = manager.getRelated(session, DEFAULT_HIERARCHY, node1, IS_PARENT);

      verify(loader).loadNodes(eq(session), eq(graph), captor.capture(), eq(LoadLevel.ALL));

      Collection<Integer> toLoad = captor.getValue();
      assertEquals(3, toLoad.size());
      Iterator<Integer> iterator = toLoad.iterator();
      assertEquals(22, iterator.next().intValue());
      assertEquals(33, iterator.next().intValue());
      assertEquals(55, iterator.next().intValue());

      assertEquals(3, nodes.size());
      Iterator<Artifact> iterator2 = nodes.iterator();
      assertEquals(node2, iterator2.next());
      assertEquals(node3, iterator2.next());
      assertEquals(node5, iterator2.next());
   }

   @Test
   public void testGetRelatedOnSideB() {
      ResultSet<Artifact> readables = manager.getRelated(session, DEFAULT_HIERARCHY, node1, IS_CHILD);

      verify(loader).loadNodes(eq(session), eq(graph), captor.capture(), eq(LoadLevel.ALL));

      Collection<Integer> toLoad = captor.getValue();
      assertEquals(1, toLoad.size());
      Iterator<Integer> iterator = toLoad.iterator();
      assertEquals(44, iterator.next().intValue());

      assertEquals(1, readables.size());
      Iterator<Artifact> iterator2 = readables.iterator();
      assertEquals(node4, iterator2.next());
   }

   @Test
   public void testGetParent() {
      Artifact actual = manager.getParent(session, node1);
      assertEquals(node4, actual);
   }

   @Test
   public void testGetChildren() {
      ResultSet<Artifact> actual = manager.getChildren(session, node1);

      assertEquals(3, actual.size());
      Iterator<Artifact> iterator = actual.iterator();
      assertEquals(node2, iterator.next());
      assertEquals(node3, iterator.next());
      assertEquals(node5, iterator.next());
   }

   @Test
   public void testGetRationale() {
      String rationale = manager.getRationale(node4, DEFAULT_HIERARCHY, node1);
      assertEquals("rationale on relation3", rationale);
   }

   @Test
   public void testGetRelatedCount() {
      int actual = manager.getRelatedCount(DEFAULT_HIERARCHY, node1, IS_PARENT);
      assertEquals(3, actual);

      when(relation2.getModificationType()).thenReturn(ModificationType.ARTIFACT_DELETED);
      when(relation2.isDeleted()).thenReturn(true);

      int actual2 = manager.getRelatedCount(DEFAULT_HIERARCHY, node1, IS_PARENT);
      assertEquals(2, actual2);

      int actual3 = manager.getRelatedCount(DEFAULT_HIERARCHY, node1, IS_PARENT, INCLUDE_DELETED);
      assertEquals(3, actual3);
   }

   @Test
   public void testAreRelated() {
      assertTrue(manager.areRelated(node4, DEFAULT_HIERARCHY, node1));

      assertTrue(manager.areRelated(node1, DEFAULT_HIERARCHY, node2));
      assertTrue(manager.areRelated(node1, DEFAULT_HIERARCHY, node3));
      assertTrue(manager.areRelated(node1, DEFAULT_HIERARCHY, node5));

      assertFalse(manager.areRelated(node1, DEFAULT_HIERARCHY, node4));
      assertFalse(manager.areRelated(node2, DEFAULT_HIERARCHY, node1));
      assertFalse(manager.areRelated(node3, DEFAULT_HIERARCHY, node1));
      assertFalse(manager.areRelated(node5, DEFAULT_HIERARCHY, node1));

      assertFalse(manager.areRelated(node4, DEFAULT_HIERARCHY, node2));
      assertFalse(manager.areRelated(node4, DEFAULT_HIERARCHY, node3));
      assertFalse(manager.areRelated(node4, DEFAULT_HIERARCHY, node5));
   }

   @Test
   public void testIntroduce() {
      when(types.isArtifactTypeAllowed(CoreRelationTypes.DefaultHierarchical_Parent, RelationSide.SIDE_A,
         CoreArtifactTypes.SoftwareRequirement)).thenReturn(true);
      when(types.getMultiplicity(CoreRelationTypes.DefaultHierarchical_Parent)).thenReturn(
         RelationTypeMultiplicity.ONE_TO_MANY);
      when(relationFactory.introduce(COMMON, data1)).thenReturn(relation1);

      manager.introduce(COMMON, node2, node3);
      RelationNodeAdjacencies node2Adj = node2.getGraph().getAdjacencies(node2);
      RelationNodeAdjacencies node3Adj = node3.getGraph().getAdjacencies(node3);

      Collection<Relation> node2Rel = node2Adj.getAll();
      Collection<Relation> node3Rel = node3Adj.getAll();

      Relation rel2 = node2Rel.iterator().next();
      Relation rel3 = node3Rel.iterator().next();

      assertTrue(rel2.equals(rel3));

   }
   private class LoaderAnswer implements Answer<Iterable<Artifact>> {

      @SuppressWarnings("unchecked")
      @Override
      public Iterable<Artifact> answer(InvocationOnMock invocation) throws Throwable {
         List<Artifact> artLoaded = new ArrayList<>();

         Collection<Integer> toLoad = (Collection<Integer>) invocation.getArguments()[2];
         artLoaded.clear();
         for (Integer item : toLoad) {
            Artifact node = mockDb.get(item);
            if (node != null) {
               artLoaded.add(node);
            }
         }
         return ResultSets.newResultSet(artLoaded);
      }
   }
}
