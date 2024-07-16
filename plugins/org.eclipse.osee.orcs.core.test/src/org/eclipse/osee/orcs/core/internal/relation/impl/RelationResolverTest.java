/*********************************************************************
 * Copyright (c) 2013 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.orcs.core.internal.relation.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyCollectionOf;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
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
   private static final ArtifactId artifactId11 = ArtifactId.valueOf(11);
   private static final ArtifactId artifactId22 = ArtifactId.valueOf(22);
   private static final ArtifactId artifactId33 = ArtifactId.valueOf(33);
   private static final ArtifactId artifactId44 = ArtifactId.valueOf(44);
   private static final ArtifactId artifactId55 = ArtifactId.valueOf(55);
   private static final ArtifactId artifactId66 = ArtifactId.valueOf(66);

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
   @Captor private ArgumentCaptor<Collection<ArtifactId>> captor;
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

      when(node1.getId()).thenReturn(11L);
      when(node2.getId()).thenReturn(22L);
      when(node3.getId()).thenReturn(33L);
      when(node4.getId()).thenReturn(44L);
      when(node5.getId()).thenReturn(55L);
      when(node6.getId()).thenReturn(66L);

      when(relation1.getIdForSide(RelationSide.SIDE_A)).thenReturn(artifactId11);
      when(relation1.getIdForSide(RelationSide.SIDE_B)).thenReturn(artifactId22);

      when(relation2.getIdForSide(RelationSide.SIDE_A)).thenReturn(artifactId33);
      when(relation2.getIdForSide(RelationSide.SIDE_B)).thenReturn(artifactId44);

      when(relation3.getIdForSide(RelationSide.SIDE_A)).thenReturn(artifactId55);
      when(relation3.getIdForSide(RelationSide.SIDE_B)).thenReturn(artifactId66);

      when(relation4.getIdForSide(RelationSide.SIDE_A)).thenReturn(artifactId11);
      when(relation4.getIdForSide(RelationSide.SIDE_B)).thenReturn(artifactId66);

      when(
         loader.loadNodes(eq(session), eq(graphData), anyCollectionOf(ArtifactId.class), eq(LoadLevel.ALL))).thenReturn(
            resultSet);
   }

   @Test
   public void testLoadAll() {
      List<Artifact> loaded = Arrays.asList(node1, node2, node3, node4, node5, node6);

      when(resultSet.iterator()).thenReturn(loaded.iterator());

      List<Artifact> arts = resolver.resolve(session, graphData, links, RelationSide.SIDE_A, RelationSide.SIDE_B);

      verify(loader).loadNodes(eq(session), eq(graphData), captor.capture(), eq(LoadLevel.ALL));
      assertCollection(captor.getValue(), ArtifactId.valueOf(11), ArtifactId.valueOf(22), ArtifactId.valueOf(33),
         ArtifactId.valueOf(44), ArtifactId.valueOf(55), ArtifactId.valueOf(66));
      assertCollection(arts, node1, node2, node3, node4, node5, node6);
   }

   @Test
   public void testLoadSideAOnly() {
      List<Artifact> loaded = Arrays.asList(node1, node3, node5);

      when(resultSet.iterator()).thenReturn(loaded.iterator());

      List<Artifact> arts = resolver.resolve(session, graphData, links, RelationSide.SIDE_A);

      verify(loader).loadNodes(eq(session), eq(graphData), captor.capture(), eq(LoadLevel.ALL));

      assertCollection(captor.getValue(), ArtifactId.valueOf(11), ArtifactId.valueOf(33), ArtifactId.valueOf(55));
      assertCollection(arts, node1, node3, node5);
   }

   @Test
   public void testLoadSideBOnly() {
      List<Artifact> loaded = Arrays.asList(node2, node4, node6);

      when(resultSet.iterator()).thenReturn(loaded.iterator());

      List<Artifact> arts = resolver.resolve(session, graphData, links, RelationSide.SIDE_B);

      verify(loader).loadNodes(eq(session), eq(graphData), captor.capture(), eq(LoadLevel.ALL));

      assertCollection(captor.getValue(), ArtifactId.valueOf(22), ArtifactId.valueOf(44), ArtifactId.valueOf(66));
      assertCollection(arts, node2, node4, node6);
   }

   @Test
   public void testLoadSideAFromCacheAndSideBFromLoader() {
      List<Artifact> loaded = Arrays.asList(node2, node4, node6);

      when(graphData.getNode(artifactId11)).thenReturn(node1);
      when(graphData.getNode(artifactId33)).thenReturn(node3);
      when(graphData.getNode(artifactId55)).thenReturn(node5);

      when(resultSet.iterator()).thenReturn(loaded.iterator());

      List<Artifact> arts = resolver.resolve(session, graphData, links, RelationSide.SIDE_A, RelationSide.SIDE_B);

      verify(loader).loadNodes(eq(session), eq(graphData), captor.capture(), eq(LoadLevel.ALL));

      assertCollection(captor.getValue(), ArtifactId.valueOf(22), ArtifactId.valueOf(44), ArtifactId.valueOf(66));
      assertCollection(arts, node1, node3, node5, node2, node4, node6);
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
