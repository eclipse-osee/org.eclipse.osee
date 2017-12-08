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

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.internal.artifact.Artifact;
import org.eclipse.osee.orcs.core.internal.graph.GraphData;
import org.eclipse.osee.orcs.core.internal.relation.Relation;
import org.eclipse.osee.orcs.core.internal.relation.RelationNodeLoader;
import org.eclipse.osee.orcs.core.internal.relation.RelationResolver;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Test Case for {@link RelationResolver}
 *
 * @author Roberto E. Escobar
 */
public class RelationResolverTest {

   // @formatter:off
   @Mock private RelationNodeLoader loader;
   @Mock private OrcsSession session;
   @Mock private GraphData graphData;

   @Mock private Relation relation1;
   @Mock private Relation relation2;
   @Mock private Relation relation3;
   @Mock private Relation relation4;

   @Mock private Artifact node1;
   @Mock private Artifact node2;
   @Mock private Artifact node3;
   @Mock private Artifact node4;
   @Mock private Artifact node5;
   @Mock private Artifact node6;

   @Mock private ResultSet<Artifact> resultSet;
   @Captor private ArgumentCaptor<Collection<Integer>> captor;
   // @formatter:on

   private RelationResolver resolver;
   private List<Relation> links;

   @Before
   public void init() {
      MockitoAnnotations.initMocks(this);

      resolver = new RelationResolverImpl(loader);

      links = Arrays.asList(relation1, relation2, relation3, relation4);

      String sessionId = GUID.create();
      when(session.getGuid()).thenReturn(sessionId);

      when(node1.getLocalId()).thenReturn(11);
      when(node2.getLocalId()).thenReturn(22);
      when(node3.getLocalId()).thenReturn(33);
      when(node4.getLocalId()).thenReturn(44);
      when(node5.getLocalId()).thenReturn(55);
      when(node6.getLocalId()).thenReturn(66);

      when(relation1.getIdForSide(RelationSide.SIDE_A)).thenReturn(11);
      when(relation1.getIdForSide(RelationSide.SIDE_B)).thenReturn(22);

      when(relation2.getIdForSide(RelationSide.SIDE_A)).thenReturn(33);
      when(relation2.getIdForSide(RelationSide.SIDE_B)).thenReturn(44);

      when(relation3.getIdForSide(RelationSide.SIDE_A)).thenReturn(55);
      when(relation3.getIdForSide(RelationSide.SIDE_B)).thenReturn(66);

      when(relation4.getIdForSide(RelationSide.SIDE_A)).thenReturn(11);
      when(relation4.getIdForSide(RelationSide.SIDE_B)).thenReturn(66);

      when(loader.loadNodes(eq(session), eq(graphData), anyCollectionOf(Integer.class), eq(LoadLevel.ALL))).thenReturn(
         resultSet);
   }

   @Test
   public void testLoadAll() {
      List<Artifact> loaded = Arrays.asList(node1, node2, node3, node4, node5, node6);

      when(resultSet.iterator()).thenReturn(loaded.iterator());

      List<Artifact> arts = resolver.resolve(session, graphData, links, RelationSide.SIDE_A, RelationSide.SIDE_B);

      verify(loader).loadNodes(eq(session), eq(graphData), captor.capture(), eq(LoadLevel.ALL));
      assertCollection(captor.getValue(), 11, 22, 33, 44, 55, 66);
      assertCollection(arts, node1, node2, node3, node4, node5, node6);
   }

   @Test
   public void testLoadSideAOnly() {
      List<Artifact> loaded = Arrays.asList(node1, node3, node5);

      when(resultSet.iterator()).thenReturn(loaded.iterator());

      List<Artifact> arts = resolver.resolve(session, graphData, links, RelationSide.SIDE_A);

      verify(loader).loadNodes(eq(session), eq(graphData), captor.capture(), eq(LoadLevel.ALL));

      assertCollection(captor.getValue(), 11, 33, 55);
      assertCollection(arts, node1, node3, node5);
   }

   @Test
   public void testLoadSideBOnly() {
      List<Artifact> loaded = Arrays.asList(node2, node4, node6);

      when(resultSet.iterator()).thenReturn(loaded.iterator());

      List<Artifact> arts = resolver.resolve(session, graphData, links, RelationSide.SIDE_B);

      verify(loader).loadNodes(eq(session), eq(graphData), captor.capture(), eq(LoadLevel.ALL));

      assertCollection(captor.getValue(), 22, 44, 66);
      assertCollection(arts, node2, node4, node6);
   }

   @Test
   public void testLoadSideAFromCacheAndSideBFromLoader() {
      List<Artifact> loaded = Arrays.asList(node2, node4, node6);

      when(graphData.getNode(11)).thenReturn(node1);
      when(graphData.getNode(33)).thenReturn(node3);
      when(graphData.getNode(55)).thenReturn(node5);

      when(resultSet.iterator()).thenReturn(loaded.iterator());

      List<Artifact> arts = resolver.resolve(session, graphData, links, RelationSide.SIDE_A, RelationSide.SIDE_B);

      verify(graphData, times(2)).getNode(11);
      verify(graphData).getNode(33);
      verify(graphData).getNode(55);

      verify(loader).loadNodes(eq(session), eq(graphData), captor.capture(), eq(LoadLevel.ALL));

      assertCollection(captor.getValue(), 22, 44, 66);
      assertCollection(arts, node1, node2, node3, node4, node5, node6);
   }

   @SuppressWarnings("unchecked")
   private static <T> void assertCollection(Collection<T> actual, T... expecteds) {
      assertEquals(expecteds.length, actual.size());
      int index = 0;
      for (Iterator<T> iterator = actual.iterator(); iterator.hasNext();) {
         T value = iterator.next();
         assertEquals(expecteds[index++], value);
      }
   }
}
