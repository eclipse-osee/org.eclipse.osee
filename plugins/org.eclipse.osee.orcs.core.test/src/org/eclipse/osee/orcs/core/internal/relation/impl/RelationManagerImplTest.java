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
package org.eclipse.osee.orcs.core.internal.relation.impl;

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import static org.eclipse.osee.framework.core.enums.CoreBranches.SYSTEM_ROOT;
import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.DEFAULT_HIERARCHY;
import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.Default_Hierarchical__Child;
import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.IS_CHILD;
import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.IS_PARENT;
import static org.eclipse.osee.framework.core.enums.DeletionFlag.EXCLUDE_DELETED;
import static org.eclipse.osee.framework.core.enums.DeletionFlag.INCLUDE_DELETED;
import static org.eclipse.osee.framework.core.enums.RelationSide.SIDE_A;
import static org.eclipse.osee.framework.core.enums.RelationSide.SIDE_B;
import static org.eclipse.osee.framework.core.enums.RelationSorter.LEXICOGRAPHICAL_ASC;
import static org.eclipse.osee.framework.core.enums.RelationSorter.PREEXISTING;
import static org.eclipse.osee.framework.core.enums.RelationSorter.UNORDERED;
import static org.eclipse.osee.framework.core.enums.RelationSorter.USER_DEFINED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.data.RelationTypeId;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.jdk.core.type.Identifiable;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.internal.graph.GraphData;
import org.eclipse.osee.orcs.core.internal.relation.Relation;
import org.eclipse.osee.orcs.core.internal.relation.RelationFactory;
import org.eclipse.osee.orcs.core.internal.relation.RelationManager;
import org.eclipse.osee.orcs.core.internal.relation.RelationNode;
import org.eclipse.osee.orcs.core.internal.relation.RelationResolver;
import org.eclipse.osee.orcs.core.internal.relation.RelationTypeValidity;
import org.eclipse.osee.orcs.core.internal.relation.RelationVisitor;
import org.eclipse.osee.orcs.core.internal.relation.order.OrderManager;
import org.eclipse.osee.orcs.core.internal.relation.order.OrderManagerFactory;
import org.eclipse.osee.orcs.core.internal.search.QueryModule.QueryModuleProvider;
import org.eclipse.osee.orcs.core.internal.types.impl.RelationTypesImpl;
import org.eclipse.osee.orcs.data.RelationTypes;
import org.junit.Assert;
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
 * Test Case for {@link RelationManagerImpl}
 *
 * @author Roberto E. Escobar
 */
public class RelationManagerImplTest {

   private static final RelationTypeToken TYPE_1 = RelationTypeToken.create(123456789, "TYPE_1");

   @Rule
   public ExpectedException thrown = ExpectedException.none();

   // @formatter:off
   @Mock private Log logger;
   @Mock private RelationTypeValidity validity;
   @Mock private RelationResolver resolver;
   @Mock private OrderManagerFactory orderFactory;

   @Mock private RelationFactory relationFactory;
   @Mock private OrcsSession session;
   @Mock private QueryModuleProvider provider;

   @Mock private GraphData graph;

   @Mock private RelationNode node1;
   @Mock private RelationNode node2;
   @Mock private RelationNode node3;
   @Mock private RelationNode node4;
   @Mock private RelationNode node5;
   @Mock private RelationNode node6;

   @Mock private RelationNodeAdjacencies container1;
   @Mock private RelationNodeAdjacencies container2;

   @Mock private IArtifactType artifactType1;
   @Mock private IArtifactType artifactType2;

   @Mock private Relation relation1;
   @Mock private Relation relation2;
   @Mock private Relation relation3;
   @Mock private Relation relation4;

   @Mock private IRelationType relType1;
   @Mock private IRelationType relType2;

   @Mock private RelationTypeSide typeAndSide1;

   @Mock private ResultSet<Relation> rSet1;
   @Mock private ResultSet<Relation> rSet2;

   @Mock private OrderManager orderManager1;
   @Captor private ArgumentCaptor<List<? extends Identifiable<String>>> sortedListCaptor;
   // @formatter:on

   private final RelationTypes relationTypes = new RelationTypesImpl(null);
   private RelationManager manager;

   @Before
   public void setUp() {
      MockitoAnnotations.initMocks(this);
      manager =
         new RelationManagerImpl(logger, validity, resolver, relationFactory, orderFactory, provider, relationTypes);

      String sessionId = GUID.create();
      when(session.getGuid()).thenReturn(sessionId);

      when(node1.getLocalId()).thenReturn(11);
      when(node2.getLocalId()).thenReturn(22);
      when(node3.getLocalId()).thenReturn(33);
      when(node4.getLocalId()).thenReturn(44);
      when(node5.getLocalId()).thenReturn(55);
      when(node6.getLocalId()).thenReturn(66);

      when(node1.getGraph()).thenReturn(graph);
      when(node2.getGraph()).thenReturn(graph);
      when(node3.getGraph()).thenReturn(graph);
      when(node4.getGraph()).thenReturn(graph);
      when(node5.getGraph()).thenReturn(graph);
      when(node6.getGraph()).thenReturn(graph);

      when(node1.getBranch()).thenReturn(COMMON);
      when(node2.getBranch()).thenReturn(COMMON);
      when(node3.getBranch()).thenReturn(SYSTEM_ROOT);

      when(graph.getTransaction()).thenReturn(TransactionId.SENTINEL);

      when(graph.getAdjacencies(node1)).thenReturn(container1);
      when(graph.getAdjacencies(node2)).thenReturn(container2);

      when(relation1.getIdForSide(RelationSide.SIDE_A)).thenReturn(11);
      when(relation1.getIdForSide(RelationSide.SIDE_B)).thenReturn(22);

      when(relation2.getIdForSide(RelationSide.SIDE_A)).thenReturn(33);
      when(relation2.getIdForSide(RelationSide.SIDE_B)).thenReturn(44);

      when(relation3.getIdForSide(RelationSide.SIDE_A)).thenReturn(55);
      when(relation3.getIdForSide(RelationSide.SIDE_B)).thenReturn(66);

      when(relation4.getIdForSide(RelationSide.SIDE_A)).thenReturn(11);
      when(relation4.getIdForSide(RelationSide.SIDE_B)).thenReturn(66);
   }

   @Test
   public void testGetValidRelationTypes() {
      final List<IRelationType> expected = new ArrayList<>();
      when(node1.getArtifactType()).thenReturn(artifactType1);
      when(node2.getArtifactType()).thenReturn(artifactType2);
      when(validity.getValidRelationTypes(artifactType1)).thenAnswer(new Answer<List<IRelationType>>() {
         @Override
         public List<IRelationType> answer(InvocationOnMock invocation) throws Throwable {
            return expected;
         }
      });

      assertEquals(expected, manager.getValidRelationTypes(session, node1));
      verify(validity).getValidRelationTypes(artifactType1);
   }

   @Test
   public void testGetMaximumRelationAllowed() {
      when(node1.getArtifactType()).thenReturn(artifactType1);
      when(node2.getArtifactType()).thenReturn(artifactType2);
      when(validity.getMaximumRelationsAllowed(TYPE_1, artifactType1, SIDE_A)).thenReturn(11);

      int actual = manager.getMaximumRelationAllowed(session, TYPE_1, node1, SIDE_A);

      assertEquals(11, actual);
      verify(validity).getMaximumRelationsAllowed(TYPE_1, artifactType1, SIDE_A);
   }

   @Test
   public void testAccept() {
      RelationVisitor visitor = mock(RelationVisitor.class);

      manager.accept(session, graph, node1, visitor);

      verify(container1).accept(visitor);
   }

   @Test
   public void testHasDirtyRelations() {
      when(graph.getAdjacencies(node1)).thenReturn(null);
      assertFalse(manager.hasDirtyRelations(session, node1));

      when(container1.hasDirty()).thenReturn(true);
      when(graph.getAdjacencies(node1)).thenReturn(container1);
      assertTrue(manager.hasDirtyRelations(session, node1));

      when(container1.hasDirty()).thenReturn(false);
      assertFalse(manager.hasDirtyRelations(session, node1));
   }

   @Test
   public void testGetExistingRelationTypeNullNode() {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("node cannot be null");
      manager.getExistingRelationTypes(session, null);
   }

   @Test
   public void testGetExistingRelationType() {
      when(graph.getAdjacencies(node1)).thenReturn(null);
      Collection<RelationTypeId> actuals = manager.getExistingRelationTypes(session, node1);
      assertEquals(Collections.emptyList(), actuals);

      final List<IRelationType> types = Arrays.asList(relType1, relType2);
      when(graph.getAdjacencies(node1)).thenReturn(container1);
      when(container1.getExistingTypes(EXCLUDE_DELETED)).thenAnswer(new Answer<List<IRelationType>>() {

         @Override
         public List<IRelationType> answer(InvocationOnMock invocation) throws Throwable {
            return types;
         }

      });
      actuals = manager.getExistingRelationTypes(session, node1);
      verify(container1).getExistingTypes(EXCLUDE_DELETED);
      assertEquals(2, actuals.size());
      Iterator<RelationTypeId> iterator = actuals.iterator();
      assertEquals(relType1, iterator.next());
      assertEquals(relType2, iterator.next());
   }

   @Test
   public void testGetParentNullNode() {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("node cannot be null");
      manager.getParent(session, null);
   }

   @Test
   public void testGetParent() {
      List<Relation> relations = Arrays.asList(relation1);
      List<RelationNode> nodes = Arrays.asList(node1);

      when(container1.getList(DEFAULT_HIERARCHY, EXCLUDE_DELETED, node1, IS_CHILD)).thenReturn(relations);
      when(resolver.resolve(session, graph, relations, SIDE_A)).thenReturn(nodes);

      RelationNode parentNode = manager.getParent(session, node1);

      assertEquals(node1, parentNode);
      verify(resolver).resolve(session, graph, relations, SIDE_A);
   }

   @Test
   public void testGetParentMoreThanOne() {
      List<Relation> relations = Arrays.asList(relation1, relation4);
      List<RelationNode> arts = Arrays.asList(node1, node3);

      when(container1.getList(DEFAULT_HIERARCHY, EXCLUDE_DELETED, node1, IS_CHILD)).thenReturn(relations);
      when(resolver.resolve(session, graph, relations, SIDE_A)).thenReturn(arts);
      when(orderFactory.createOrderManager(node1)).thenReturn(orderManager1);

      RelationNode parent = manager.getParent(session, node1);
      assertEquals(node1, parent);

      verify(resolver).resolve(session, graph, relations, SIDE_A);
      verify(orderManager1).sort(CoreRelationTypes.Default_Hierarchical__Parent, arts);
   }

   @Test
   public void testGetChildrenNullNode() {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("node cannot be null");
      manager.getChildren(session, null);
   }

   @Test
   public void testGetChildren() {
      List<Relation> relations = Arrays.asList(relation1, relation4);
      List<RelationNode> nodes = Arrays.asList(node2, node6);

      when(container1.getList(DEFAULT_HIERARCHY, EXCLUDE_DELETED, node1, IS_PARENT)).thenReturn(relations);
      when(resolver.resolve(session, graph, relations, SIDE_B)).thenReturn(nodes);
      when(orderFactory.createOrderManager(node1)).thenReturn(orderManager1);

      ResultSet<RelationNode> result = manager.getChildren(session, node1);
      assertEquals(2, result.size());
      Iterator<RelationNode> iterator = result.iterator();
      assertEquals(node2, iterator.next());
      assertEquals(node6, iterator.next());

      verify(resolver).resolve(session, graph, relations, SIDE_B);
      verify(orderManager1).sort(CoreRelationTypes.Default_Hierarchical__Child, nodes);
   }

   @Test
   public void testGetRelatedNullNode() {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("node cannot be null");
      manager.getRelated(session, TYPE_1, null, SIDE_A);
   }

   @Test
   public void testGetRelatedNullType() {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("relationType cannot be null");
      manager.getRelated(session, null, node1, SIDE_A);
   }

   @Test
   public void testGetRelatedNullSide() {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("relationSide cannot be null");
      manager.getRelated(session, TYPE_1, node1, null);
   }

   @Test
   public void testGetRelated() {
      List<Relation> relations = Arrays.asList(relation1, relation2, relation3);
      List<RelationNode> nodes = Arrays.asList(node2, node3, node5);

      when(container1.getList(TYPE_1, EXCLUDE_DELETED, node1, SIDE_B)).thenReturn(relations);

      when(resolver.resolve(session, graph, relations, SIDE_A)).thenReturn(nodes);
      when(orderFactory.createOrderManager(node1)).thenReturn(orderManager1);

      ResultSet<RelationNode> result = manager.getRelated(session, TYPE_1, node1, SIDE_B);
      assertEquals(3, result.size());
      Iterator<RelationNode> iterator = result.iterator();
      assertEquals(node2, iterator.next());
      assertEquals(node3, iterator.next());
      assertEquals(node5, iterator.next());

      verify(resolver).resolve(session, graph, relations, SIDE_A);

      RelationTypeSide typeSide = RelationTypeSide.create(TYPE_1, SIDE_A);
      verify(orderManager1).sort(typeSide, nodes);
   }

   @Test
   public void testAreRelated() {
      when(container1.getRelation(node1, TYPE_1, node2, EXCLUDE_DELETED)).thenReturn(relation1);

      boolean value = manager.areRelated(session, node1, TYPE_1, node2);
      assertTrue(value);

      when(container1.getRelation(node1, TYPE_1, node2, EXCLUDE_DELETED)).thenReturn(null);
      when(container2.getRelation(node1, TYPE_1, node2, EXCLUDE_DELETED)).thenReturn(null);

      boolean value2 = manager.areRelated(session, node1, TYPE_1, node2);
      assertFalse(value2);
   }

   @Test
   public void testGetRationale() {
      when(container1.getRelation(node1, TYPE_1, node2, EXCLUDE_DELETED)).thenReturn(relation1);
      when(relation1.getRationale()).thenReturn("Hello rationale");

      String value = manager.getRationale(session, node1, TYPE_1, node2);
      assertEquals("Hello rationale", value);

      verify(container1).getRelation(node1, TYPE_1, node2, EXCLUDE_DELETED);
      verify(relation1).getRationale();
   }

   @Test
   public void testGetRelatedCount() {
      List<Relation> list = Arrays.asList(relation1, relation2, relation3);

      when(container1.getList(TYPE_1, EXCLUDE_DELETED, node1, SIDE_B)).thenReturn(list);

      int actual = manager.getRelatedCount(session, TYPE_1, node1, SIDE_B);
      Assert.assertEquals(3, actual);

      verify(container1).getList(TYPE_1, EXCLUDE_DELETED, node1, SIDE_B);
   }

   @Test
   public void testGetRelatedCountIncludeDeleted() {
      List<Relation> list = Arrays.asList(relation1, relation2);

      when(container1.getList(TYPE_1, INCLUDE_DELETED, node1, SIDE_A)).thenReturn(list);

      int actual = manager.getRelatedCount(session, TYPE_1, node1, SIDE_A, INCLUDE_DELETED);
      Assert.assertEquals(2, actual);

      verify(container1).getList(TYPE_1, INCLUDE_DELETED, node1, SIDE_A);
   }

   @Test
   public void testRelateErrorOnDifferentBranches() {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("Cross branch linking is not yet supported.");
      manager.relate(session, node2, typeAndSide1, node3);
   }

   @Test
   public void testRelateErrorCycle() {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage(String.format("Not valid to relate [%s] to itself", node1));
      manager.relate(session, node1, typeAndSide1, node1);
   }

   @Test
   public void testRelateErrorMultiplicityInvalid() {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage(String.format("Not valid to relate [%s] to itself", node1));
      manager.relate(session, node1, typeAndSide1, node1);
   }

   @Test
   public void testRelateErrorTypeInvalidNode1() {
      OseeCoreException myException = new OseeCoreException("Test Type Exception");

      doThrow(myException).when(validity).checkRelationTypeValid(TYPE_1, node1, SIDE_A);

      thrown.expect(OseeCoreException.class);
      thrown.expectMessage("Test Type Exception");
      manager.relate(session, node1, TYPE_1, node2);

      verify(validity).checkRelationTypeValid(TYPE_1, node1, SIDE_A);
      verify(validity, times(0)).checkRelationTypeValid(TYPE_1, node2, SIDE_A);
   }

   @Test
   public void testRelateErrorTypeInvalidNode2() {
      when(relationFactory.createRelation(node1, TYPE_1, node2)).thenReturn(relation1);
      when(container1.getResultSet(TYPE_1, INCLUDE_DELETED, node1, SIDE_A)).thenReturn(rSet1);
      when(rSet1.getOneOrNull()).thenReturn(relation1);

      OseeCoreException myException = new OseeCoreException("Test Type Exception");

      doThrow(myException).when(validity).checkRelationTypeValid(TYPE_1, node2, SIDE_B);

      thrown.expect(OseeCoreException.class);
      thrown.expectMessage("Test Type Exception");
      manager.relate(session, node1, TYPE_1, node2);

      verify(validity).checkRelationTypeValid(TYPE_1, node1, SIDE_A);
      verify(validity).checkRelationTypeValid(TYPE_1, node2, SIDE_B);
   }

   @Test
   public void testRelateErrorMultiplicityNode1() {
      thrown.expect(OseeStateException.class);

      when(node1.getArtifactType()).thenReturn(artifactType1);
      when(node2.getArtifactType()).thenReturn(artifactType2);
      when(validity.getMaximumRelationsAllowed(Default_Hierarchical__Child, artifactType1, SIDE_A)).thenReturn(1);

      manager.relate(session, node1, TYPE_1, node2);

      verify(validity).checkRelationTypeValid(TYPE_1, node1, SIDE_A);
      verify(validity).checkRelationTypeMultiplicity(TYPE_1, node1, SIDE_A, 1);
      verify(validity, times(0)).checkRelationTypeValid(TYPE_1, node2, SIDE_B);
   }

   @Test
   public void testRelateErrorMultiplicityNode2() {
      when(relationFactory.createRelation(node1, TYPE_1, node2)).thenReturn(relation1);
      when(container1.getResultSet(TYPE_1, INCLUDE_DELETED, node1, SIDE_A)).thenReturn(rSet1);
      when(rSet1.getOneOrNull()).thenReturn(relation1);

      thrown.expect(OseeStateException.class);
      manager.relate(session, node1, TYPE_1, node2);

      verify(validity).checkRelationTypeValid(TYPE_1, node1, SIDE_A);
      verify(validity).checkRelationTypeMultiplicity(TYPE_1, node1, SIDE_A, 1);
      verify(validity).checkRelationTypeValid(TYPE_1, node2, SIDE_B);
      verify(validity).checkRelationTypeMultiplicity(TYPE_1, node2, SIDE_B, 1);
   }

   @Test
   public void testSetRationale() {
      String rationale = "New Rationale";

      when(container2.getRelation(node1, TYPE_1, node2, EXCLUDE_DELETED)).thenReturn(relation1);

      manager.setRationale(session, node1, TYPE_1, node2, rationale);

      verify(container1).getRelation(node1, TYPE_1, node2, EXCLUDE_DELETED);
      verify(container2).getRelation(node1, TYPE_1, node2, EXCLUDE_DELETED);
      verify(relation1).setRationale(rationale);
   }

   @Test
   public void testRelateWithSorting() {
      when(container1.getRelation(node1, TYPE_1, node2, INCLUDE_DELETED)).thenReturn(null);
      when(container2.getRelation(node1, TYPE_1, node2, INCLUDE_DELETED)).thenReturn(null);

      when(relationFactory.createRelation(eq(node1), eq(TYPE_1), eq(node2), Matchers.anyString())).thenReturn(
         relation1);
      when(orderFactory.createOrderManager(node1)).thenReturn(orderManager1);

      when(node1.getArtifactType()).thenReturn(artifactType1);
      when(node2.getArtifactType()).thenReturn(artifactType2);
      when(validity.getMaximumRelationsAllowed(TYPE_1, artifactType1, SIDE_A)).thenReturn(10);
      when(validity.getMaximumRelationsAllowed(TYPE_1, artifactType2, SIDE_B)).thenReturn(10);

      manager.relate(session, node1, TYPE_1, node2, LEXICOGRAPHICAL_ASC);

      RelationTypeSide typeSide = RelationTypeSide.create(TYPE_1, SIDE_B);

      verify(container1).getRelation(node1, TYPE_1, node2, INCLUDE_DELETED);
      verify(container2).getRelation(node1, TYPE_1, node2, INCLUDE_DELETED);
      verify(orderManager1).setOrder(eq(typeSide), eq(LEXICOGRAPHICAL_ASC), sortedListCaptor.capture());
      verify(container1).add(TYPE_1, relation1);
      verify(container2).add(TYPE_1, relation1);
   }

   @Test
   public void testRelateNoSorting() {
      when(container1.getRelation(node1, TYPE_1, node2, INCLUDE_DELETED)).thenReturn(null);
      when(container2.getRelation(node1, TYPE_1, node2, INCLUDE_DELETED)).thenReturn(null);

      when(relationFactory.createRelation(eq(node1), eq(TYPE_1), eq(node2), Matchers.anyString())).thenReturn(
         relation1);

      when(orderFactory.createOrderManager(node1)).thenReturn(orderManager1);

      RelationTypeSide typeSide = RelationTypeSide.create(TYPE_1, SIDE_B);
      when(orderManager1.getSorterId(typeSide)).thenReturn(UNORDERED);

      when(node1.getArtifactType()).thenReturn(artifactType1);
      when(node2.getArtifactType()).thenReturn(artifactType2);
      when(validity.getMaximumRelationsAllowed(TYPE_1, artifactType1, SIDE_A)).thenReturn(10);
      when(validity.getMaximumRelationsAllowed(TYPE_1, artifactType2, SIDE_B)).thenReturn(10);

      manager.relate(session, node1, TYPE_1, node2);

      verify(container1).getRelation(node1, TYPE_1, node2, INCLUDE_DELETED);
      verify(container2).getRelation(node1, TYPE_1, node2, INCLUDE_DELETED);
      verify(orderManager1).getSorterId(typeSide);
      verify(orderManager1).setOrder(eq(typeSide), eq(UNORDERED), sortedListCaptor.capture());
      verify(container1).add(TYPE_1, relation1);
      verify(container2).add(TYPE_1, relation1);
   }

   @Test
   public void testRelateWithSortingUserDefined() {
      when(container1.getRelation(node1, TYPE_1, node2, INCLUDE_DELETED)).thenReturn(null);
      when(container2.getRelation(node1, TYPE_1, node2, INCLUDE_DELETED)).thenReturn(null);

      when(relationFactory.createRelation(eq(node1), eq(TYPE_1), eq(node2), Matchers.anyString())).thenReturn(
         relation1);
      when(orderFactory.createOrderManager(node1)).thenReturn(orderManager1);

      List<Relation> toOrder = Arrays.asList(relation3, relation4);
      when(container1.getList(TYPE_1, EXCLUDE_DELETED, node1, SIDE_A)).thenReturn(toOrder);

      List<RelationNode> nodesToOrder = Arrays.asList(node3, node4, node5, node6);
      when(resolver.resolve(session, graph, toOrder, SIDE_B)).thenReturn(nodesToOrder);

      when(node1.getArtifactType()).thenReturn(artifactType1);
      when(node2.getArtifactType()).thenReturn(artifactType2);
      when(validity.getMaximumRelationsAllowed(TYPE_1, artifactType1, SIDE_A)).thenReturn(10);
      when(validity.getMaximumRelationsAllowed(TYPE_1, artifactType2, SIDE_B)).thenReturn(10);

      manager.relate(session, node1, TYPE_1, node2, USER_DEFINED);

      verify(container1).getRelation(node1, TYPE_1, node2, INCLUDE_DELETED);
      verify(container2).getRelation(node1, TYPE_1, node2, INCLUDE_DELETED);
      verify(container1).add(TYPE_1, relation1);
      verify(container2).add(TYPE_1, relation1);

      verify(resolver).resolve(session, graph, toOrder, SIDE_B);

      RelationTypeSide typeSide = RelationTypeSide.create(TYPE_1, SIDE_B);
      verify(orderManager1).sort(typeSide, nodesToOrder);
      verify(orderManager1).setOrder(eq(typeSide), eq(USER_DEFINED), sortedListCaptor.capture());

      Iterator<? extends Identifiable<String>> iterator = sortedListCaptor.getValue().iterator();
      assertEquals(node3, iterator.next());
      assertEquals(node4, iterator.next());
      assertEquals(node5, iterator.next());
      assertEquals(node6, iterator.next());
      assertEquals(node2, iterator.next());
   }

   @Test
   public void testAddChild() {
      when(container1.getRelation(node1, DEFAULT_HIERARCHY, node2, INCLUDE_DELETED)).thenReturn(null);
      when(container2.getRelation(node1, DEFAULT_HIERARCHY, node2, INCLUDE_DELETED)).thenReturn(null);

      when(
         relationFactory.createRelation(eq(node1), eq(DEFAULT_HIERARCHY), eq(node2), Matchers.anyString())).thenReturn(
            relation1);
      when(orderFactory.createOrderManager(node1)).thenReturn(orderManager1);

      when(orderFactory.createOrderManager(node1)).thenReturn(orderManager1);
      when(orderManager1.getSorterId(Default_Hierarchical__Child)).thenReturn(UNORDERED);

      when(node1.getArtifactType()).thenReturn(artifactType1);
      when(node2.getArtifactType()).thenReturn(artifactType2);
      when(validity.getMaximumRelationsAllowed(DEFAULT_HIERARCHY, artifactType1, SIDE_A)).thenReturn(10);
      when(validity.getMaximumRelationsAllowed(DEFAULT_HIERARCHY, artifactType2, SIDE_B)).thenReturn(10);

      manager.addChild(session, node1, node2);

      verify(container1).getRelation(node1, DEFAULT_HIERARCHY, node2, INCLUDE_DELETED);
      verify(container2).getRelation(node1, DEFAULT_HIERARCHY, node2, INCLUDE_DELETED);
      verify(orderManager1).getSorterId(Default_Hierarchical__Child);
      verify(orderManager1).setOrder(eq(Default_Hierarchical__Child), eq(UNORDERED), sortedListCaptor.capture());
      verify(container1).add(DEFAULT_HIERARCHY, relation1);
      verify(container2).add(DEFAULT_HIERARCHY, relation1);
   }

   @Test
   public void testAddChildren() {
      when(container1.getRelation(node1, TYPE_1, node2, INCLUDE_DELETED)).thenReturn(null);
      when(container2.getRelation(node1, TYPE_1, node2, INCLUDE_DELETED)).thenReturn(null);

      when(
         relationFactory.createRelation(eq(node1), eq(DEFAULT_HIERARCHY), eq(node2), Matchers.anyString())).thenReturn(
            relation1);
      when(orderFactory.createOrderManager(node1)).thenReturn(orderManager1);
      when(orderManager1.getSorterId(Default_Hierarchical__Child)).thenReturn(UNORDERED);

      when(node1.getArtifactType()).thenReturn(artifactType1);
      when(node2.getArtifactType()).thenReturn(artifactType2);
      when(validity.getMaximumRelationsAllowed(DEFAULT_HIERARCHY, artifactType1, SIDE_A)).thenReturn(10);
      when(validity.getMaximumRelationsAllowed(DEFAULT_HIERARCHY, artifactType2, SIDE_B)).thenReturn(10);

      List<? extends RelationNode> children = Arrays.asList(node2);
      manager.addChildren(session, node1, children);

      verify(container1).getRelation(node1, DEFAULT_HIERARCHY, node2, INCLUDE_DELETED);
      verify(container2).getRelation(node1, DEFAULT_HIERARCHY, node2, INCLUDE_DELETED);

      verify(orderManager1).getSorterId(Default_Hierarchical__Child);
      verify(orderManager1).setOrder(eq(Default_Hierarchical__Child), eq(UNORDERED), sortedListCaptor.capture());
      verify(container1).add(DEFAULT_HIERARCHY, relation1);
      verify(container2).add(DEFAULT_HIERARCHY, relation1);
   }

   @Test
   public void testAddPreviouslyDeletedChild() {
      when(container1.getRelation(node1, DEFAULT_HIERARCHY, node2, INCLUDE_DELETED)).thenReturn(null);
      when(container2.getRelation(node1, DEFAULT_HIERARCHY, node2, INCLUDE_DELETED)).thenReturn(relation1);

      when(relation1.isDeleted()).thenReturn(true);

      when(orderFactory.createOrderManager(node1)).thenReturn(orderManager1);
      when(orderManager1.getSorterId(Default_Hierarchical__Child)).thenReturn(UNORDERED);

      when(node1.getArtifactType()).thenReturn(artifactType1);
      when(node2.getArtifactType()).thenReturn(artifactType2);
      when(validity.getMaximumRelationsAllowed(DEFAULT_HIERARCHY, artifactType1, SIDE_A)).thenReturn(10);
      when(validity.getMaximumRelationsAllowed(DEFAULT_HIERARCHY, artifactType2, SIDE_B)).thenReturn(10);

      manager.addChild(session, node1, node2);

      verify(orderManager1).getSorterId(Default_Hierarchical__Child);
      verify(orderManager1).setOrder(eq(Default_Hierarchical__Child), eq(UNORDERED), sortedListCaptor.capture());
      verify(container1, times(1)).add(DEFAULT_HIERARCHY, relation1);
      verify(container2, times(0)).add(DEFAULT_HIERARCHY, relation1);

      verify(relation1).unDelete();
   }

   @Test
   public void testUnrelate() {
      when(container1.getRelation(node1, DEFAULT_HIERARCHY, node2, EXCLUDE_DELETED)).thenReturn(relation1);

      when(orderFactory.createOrderManager(node1)).thenReturn(orderManager1);
      when(orderManager1.getSorterId(Default_Hierarchical__Child)).thenReturn(USER_DEFINED);

      List<Relation> relations = Arrays.asList(relation1);
      when(container1.getList(DEFAULT_HIERARCHY, EXCLUDE_DELETED, node1, IS_PARENT)).thenReturn(relations);

      List<RelationNode> nodesToOrder = Arrays.asList(node3, node2, node5, node6);
      when(resolver.resolve(session, graph, relations, SIDE_B)).thenReturn(nodesToOrder);

      manager.unrelate(session, node1, DEFAULT_HIERARCHY, node2);

      verify(relation1).delete();

      verify(orderManager1).getSorterId(Default_Hierarchical__Child);

      verify(container1, times(0)).remove(DEFAULT_HIERARCHY, relation1);

      verify(resolver).resolve(session, graph, relations, SIDE_B);

      verify(orderManager1).setOrder(eq(Default_Hierarchical__Child), eq(USER_DEFINED), sortedListCaptor.capture());

      assertEquals(3, sortedListCaptor.getValue().size());
      Iterator<? extends Identifiable<String>> iterator = sortedListCaptor.getValue().iterator();
      assertEquals(node3, iterator.next());
      assertEquals(node5, iterator.next());
      assertEquals(node6, iterator.next());
   }

   @Test
   public void testUnrelateFromAllByType() {
      List<Relation> relations1 = Arrays.asList(relation1);

      when(container1.getList(DEFAULT_HIERARCHY, EXCLUDE_DELETED, node1, IS_PARENT)).thenReturn(relations1);

      when(relation1.getIdForSide(SIDE_B)).thenReturn(22);
      when(graph.getNode(22)).thenReturn(node2);

      when(orderFactory.createOrderManager(node1)).thenReturn(orderManager1);
      when(orderManager1.getSorterId(Default_Hierarchical__Child)).thenReturn(PREEXISTING);

      manager.unrelateFromAll(session, DEFAULT_HIERARCHY, node1, IS_PARENT);

      verify(relation1).getIdForSide(SIDE_B);
      verify(graph).getNode(22);
      verify(container1).getList(DEFAULT_HIERARCHY, EXCLUDE_DELETED, node1, IS_PARENT);
      verify(resolver).resolve(session, graph, relations1, SIDE_B);
      verify(relation1).delete();
      verify(orderManager1).getSorterId(Default_Hierarchical__Child);
   }

   @Test
   public void testUnrelateFromAll() {
      List<Relation> allRelations = Arrays.asList(relation1);
      List<Relation> asAParent = Collections.emptyList();
      List<Relation> asAChild = Arrays.asList(relation2);
      List<RelationNode> children = Arrays.asList(node2);

      when(relation1.getRelationType()).thenReturn(DEFAULT_HIERARCHY);
      when(node1.isDeleteAllowed()).thenReturn(true);
      when(container1.getList(EXCLUDE_DELETED)).thenReturn(allRelations);
      when(container1.getList(DEFAULT_HIERARCHY, EXCLUDE_DELETED, node1, IS_PARENT)).thenReturn(allRelations);
      when(container1.getList(DEFAULT_HIERARCHY, EXCLUDE_DELETED, node1, IS_PARENT)).thenReturn(asAParent);
      when(container1.getList(DEFAULT_HIERARCHY, EXCLUDE_DELETED, node1, IS_CHILD)).thenReturn(asAChild);

      when(relation1.getIdForSide(SIDE_A)).thenReturn(11);

      when(orderFactory.createOrderManager(node1)).thenReturn(orderManager1);

      when(resolver.resolve(session, graph, asAChild, IS_CHILD)).thenReturn(children);
      when(orderManager1.getSorterId(Default_Hierarchical__Child)).thenReturn(PREEXISTING);

      manager.unrelateFromAll(session, node1);

      verify(container1).getList(EXCLUDE_DELETED);
      verify(resolver).resolve(session, graph, allRelations, SIDE_A, SIDE_B);
      verify(container1).getList(DEFAULT_HIERARCHY, EXCLUDE_DELETED, node1, IS_PARENT);
      verify(node1).delete();

      verify(relation1).getIdForSide(SIDE_A);
      verify(relation1).delete();
      verify(container1).getList(DEFAULT_HIERARCHY, EXCLUDE_DELETED, node1, IS_CHILD);
      verify(resolver).resolve(session, graph, asAChild, SIDE_B);

      verify(orderManager1).setOrder(Default_Hierarchical__Child, children);
   }
}
