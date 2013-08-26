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
package org.eclipse.osee.orcs.core.internal.loader;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.eclipse.osee.executor.admin.HasCancellation;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.ResultSet;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.DataLoader;
import org.eclipse.osee.orcs.core.ds.DataLoaderFactory;
import org.eclipse.osee.orcs.core.ds.QueryContext;
import org.eclipse.osee.orcs.core.internal.ArtifactBuilder;
import org.eclipse.osee.orcs.core.internal.ArtifactBuilderFactory;
import org.eclipse.osee.orcs.core.internal.ArtifactLoader;
import org.eclipse.osee.orcs.core.internal.ArtifactLoaderFactory;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Test Case for {@link ArtifactLoaderFactoryImpl}
 * 
 * @author Andrew M. Finkbeiner
 * @author Roberto E. Escobar
 */
public class ArtifactLoaderFactoryImplTest {

   // @formatter:off
   @Mock private DataLoaderFactory dataLoaderFactory;
   @Mock private ArtifactBuilderFactory builderFactory;
   
   @Mock private OrcsSession session;
   @Mock private QueryContext queryContext;
   @Mock private DataLoader dbLoader;
   @Mock private ArtifactBuilder builder;
   @Mock private HasCancellation cancellation;
   
   @Mock private ArtifactReadable art1;
   @Mock private ArtifactReadable art2;
   // @formatter:on

   private final IOseeBranch branch = CoreBranches.COMMON;
   private ArtifactLoaderFactory factory;
   private List<ArtifactReadable> artifacts;

   @Before
   public void setUp() {
      MockitoAnnotations.initMocks(this);
      factory = new ArtifactLoaderFactoryImpl(dataLoaderFactory, builderFactory);
      artifacts = Arrays.asList(art1, art2);

      String sessionId = GUID.create();
      when(session.getGuid()).thenReturn(sessionId);
   }

   @Test
   public void testLoadArtifactIdsAndMethodCalls() throws OseeCoreException {
      int[] ids = new int[] {1, 2, 3, 4, 5};

      when(dataLoaderFactory.fromBranchAndArtifactIds(session, branch, ids)).thenReturn(dbLoader);

      ArtifactLoader loader = factory.fromBranchAndArtifactIds(session, branch, ids);
      verify(dataLoaderFactory).fromBranchAndArtifactIds(session, branch, ids);

      loader.includeDeleted();
      verify(dbLoader).includeDeleted();

      loader.includeDeleted(true);
      verify(dbLoader).includeDeleted(true);

      when(dbLoader.isHeadTransaction()).thenReturn(true);
      assertEquals(true, loader.isHeadTransaction());

      when(dbLoader.getFromTransaction()).thenReturn(67);
      assertEquals(67, loader.getFromTransaction());

      when(dbLoader.getLoadLevel()).thenReturn(LoadLevel.SHALLOW);
      assertEquals(LoadLevel.SHALLOW, loader.getLoadLevel());

      loader.areDeletedIncluded();
      verify(dbLoader).areDeletedIncluded();

      loader.headTransaction();
      verify(dbLoader).headTransaction();

      loader.resetToDefaults();
      verify(dbLoader).resetToDefaults();

      loader.fromTransaction(36);
      verify(dbLoader).fromTransaction(36);

      loader.setLoadLevel(LoadLevel.FULL);
      verify(dbLoader).setLoadLevel(LoadLevel.FULL);
   }

   @Test
   public void testLoad() throws OseeCoreException {
      List<Integer> ids = Arrays.asList(1, 2, 3, 4, 5);

      when(dataLoaderFactory.fromBranchAndArtifactIds(session, branch, ids)).thenReturn(dbLoader);

      ArtifactLoader loader = factory.fromBranchAndArtifactIds(session, branch, ids);
      verify(dataLoaderFactory).fromBranchAndArtifactIds(session, branch, ids);

      when(builderFactory.createArtifactBuilder()).thenReturn(builder);
      when(builder.getArtifacts()).thenReturn(artifacts);

      List<ArtifactReadable> actual = loader.load(cancellation);
      verify(dbLoader).load(cancellation, builder);
      assertEquals(artifacts, actual);
   }

   @Test
   public void testGetResults() throws OseeCoreException {
      int[] ids = new int[] {1, 2, 3, 4, 5};

      when(dataLoaderFactory.fromBranchAndArtifactIds(session, branch, ids)).thenReturn(dbLoader);

      ArtifactLoader loader = factory.fromBranchAndArtifactIds(session, branch, ids);
      verify(dataLoaderFactory).fromBranchAndArtifactIds(session, branch, ids);

      when(builderFactory.createArtifactBuilder()).thenReturn(builder);
      when(builder.getArtifacts()).thenReturn(artifacts);

      ResultSet<ArtifactReadable> result = loader.getResults(cancellation);

      verify(dbLoader).load(cancellation, builder);
      assertEquals(artifacts.size(), result.size());
      checkEquals(artifacts, result);
   }

   @Test
   public void testQueryContext() throws OseeCoreException {
      when(dataLoaderFactory.fromQueryContext(queryContext)).thenReturn(dbLoader);

      ArtifactLoader loader = factory.fromQueryContext(session, queryContext);
      verify(dataLoaderFactory).fromQueryContext(queryContext);

      when(builderFactory.createArtifactBuilder()).thenReturn(builder);
      when(builder.getArtifacts()).thenReturn(artifacts);

      ResultSet<ArtifactReadable> result = loader.getResults(cancellation);

      verify(dbLoader).load(cancellation, builder);
      assertEquals(artifacts.size(), result.size());
      checkEquals(artifacts, result);
   }

   private void checkEquals(Iterable<ArtifactReadable> expected, Iterable<ArtifactReadable> actual) {
      Iterator<ArtifactReadable> actuals = actual.iterator();
      Iterator<ArtifactReadable> expecteds = expected.iterator();
      while (expecteds.hasNext() && actuals.hasNext()) {
         assertEquals(expecteds.next(), actuals.next());
      }
   }
}
