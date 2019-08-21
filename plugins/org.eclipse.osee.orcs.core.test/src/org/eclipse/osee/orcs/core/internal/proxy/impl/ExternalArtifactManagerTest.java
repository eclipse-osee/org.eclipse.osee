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
package org.eclipse.osee.orcs.core.internal.proxy.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.Attribute;
import org.eclipse.osee.orcs.core.internal.artifact.Artifact;
import org.eclipse.osee.orcs.core.internal.proxy.ExternalArtifactManager;
import org.eclipse.osee.orcs.core.internal.relation.RelationManager;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.ArtifactTypes;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;

/**
 * @author Megumi Telles
 */
public class ExternalArtifactManagerTest {

   @Rule
   public ExpectedException thrown = ExpectedException.none();

   // @formatter:off
   @Mock private RelationManager relationManager;
   @Mock private ArtifactTypes artifactTypeCache;
   @Mock private OrcsSession session;

   @Mock private Artifact artifact1;
   @Mock private Artifact artifact2;
   @Mock private Artifact artifact3;

   @Mock private Attribute<Integer> attribute1;
   @Mock private Attribute<Integer> attribute2;
   @Mock private Attribute<Integer> attribute3;

   @Mock private AttributeReadable<Integer> attributeReadable1;
   @Mock private AttributeReadable<Integer> attributeReadable2;
   @Mock private AttributeReadable<Integer> attributeReadable3;
   // @formatter:on

   private ExternalArtifactManager proxyManager;
   private ArtifactReadOnlyImpl readable1;
   private ArtifactReadOnlyImpl readable2;
   private ArtifactReadOnlyImpl readable3;

   @Before
   public void setUp() throws Exception {
      initMocks(this);

      proxyManager = new ExternalArtifactManagerImpl(relationManager, artifactTypeCache);

      when(artifact1.getLocalId()).thenReturn(1);
      when(artifact2.getLocalId()).thenReturn(2);
      when(artifact3.getLocalId()).thenReturn(3);

      readable1 =
         new ArtifactReadOnlyImpl(proxyManager, relationManager, session, artifact1, CoreArtifactTypes.Artifact);
      readable2 =
         new ArtifactReadOnlyImpl(proxyManager, relationManager, session, artifact2, CoreArtifactTypes.Artifact);
      readable3 =
         new ArtifactReadOnlyImpl(proxyManager, relationManager, session, artifact3, CoreArtifactTypes.Artifact);
   }

   @Test
   public void testNullArtifactReadable() {
      Artifact actual = proxyManager.asInternalArtifact(null);
      assertNull(actual);
   }

   @Test
   public void testNullArtifact() {
      ArtifactReadable actual = proxyManager.asExternalArtifact(session, null);
      assertNull(actual);
   }

   @Test
   public void testNullAttribute() {
      AttributeReadable<?> actual = proxyManager.asExternalAttribute(session, null);
      assertNull(actual);
   }

   @Test
   public void testAsArtifactReadable() {
      ArtifactReadable actual = proxyManager.asExternalArtifact(session, artifact1);

      checkProxied(artifact1, actual);
   }

   @Test
   public void testAsAttributeReadable() {
      AttributeReadable<Integer> actual = proxyManager.asExternalAttribute(session, attribute1);

      checkProxied(attribute1, actual);
   }

   @Test
   public void testAsArtifacts() {
      List<? extends ArtifactReadable> expected = Arrays.asList(readable1, readable2, readable3);

      ResultSet<? extends Artifact> actuals = proxyManager.asInternalArtifacts(expected);
      assertFalse(actuals.isEmpty());
      assertEquals(3, actuals.size());

      Iterator<? extends Artifact> iterator = actuals.iterator();
      assertEquals(artifact1, iterator.next());
      assertEquals(artifact2, iterator.next());
      assertEquals(artifact3, iterator.next());

      expected = Arrays.asList(readable1, null, readable3);
      actuals = proxyManager.asInternalArtifacts(expected);
      assertEquals(3, actuals.size());

      iterator = actuals.iterator();
      assertEquals(artifact1, iterator.next());
      assertNull(iterator.next());
      assertEquals(artifact3, iterator.next());
   }

   @Test
   public void testAsArtifactReadables() {
      List<? extends Artifact> expected = Arrays.asList(artifact1, artifact2, artifact3);
      ResultSet<ArtifactReadable> actuals = proxyManager.asExternalArtifacts(session, expected);
      assertFalse(actuals.isEmpty());
      assertEquals(3, actuals.size());

      Iterator<ArtifactReadable> iterator = actuals.iterator();
      for (int index = 0; index < expected.size(); index++) {
         checkProxied(expected.get(index), iterator.next());
      }

      expected = Arrays.asList(artifact1, null, artifact3);
      actuals = proxyManager.asExternalArtifacts(session, expected);
      assertEquals(3, actuals.size());

      iterator = actuals.iterator();
      checkProxied(expected.get(0), iterator.next());
      assertNull(iterator.next());
      checkProxied(expected.get(2), iterator.next());
   }

   @Test
   public void testAsAttributeReadables() {
      List<? extends Attribute<Integer>> expected = Arrays.asList(attribute1, attribute2, attribute3);
      ResultSet<AttributeReadable<Integer>> actuals = proxyManager.asExternalAttributes(session, expected);
      assertFalse(actuals.isEmpty());
      assertEquals(3, actuals.size());

      Iterator<AttributeReadable<Integer>> iterator = actuals.iterator();
      for (int index = 0; index < expected.size(); index++) {
         checkProxied(expected.get(index), iterator.next());
      }

      expected = Arrays.asList(attribute1, null, attribute3);
      actuals = proxyManager.asExternalAttributes(session, expected);
      assertEquals(3, actuals.size());

      iterator = actuals.iterator();
      checkProxied(expected.get(0), iterator.next());
      assertNull(iterator.next());
      checkProxied(expected.get(2), iterator.next());
   }

   @SuppressWarnings("unchecked")
   private <T> void checkProxied(T expected, Object actual) {
      assertNotNull(actual);

      AbstractProxied<T> proxy = (AbstractProxied<T>) actual;
      T proxied = proxy.getProxiedObject();

      assertEquals(expected, proxied);
      assertEquals(proxyManager, proxy.getProxyManager());
      assertEquals(session, proxy.getSession());
   }
}