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
package org.eclipse.osee.ats.core.client.internal.store;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.client.internal.config.AtsArtifactConfigCache;
import org.eclipse.osee.ats.core.util.CacheProvider;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Test case for {@link AtsVersionCache}
 * 
 * @author Megumi Telles
 */

public class AtsVersionCacheTest {

   // @formatter:off
   @Mock private CacheProvider<AtsArtifactConfigCache> cacheProvider;
   @Mock private IAtsTeamWorkflow teamWf;
   @Mock private IAtsVersion atsVersion;   
   @Mock private IAtsConfigObject atsConfig;   
   // @formatter:on

   private AtsVersionCache atsVersionCache;

   @Before
   public void setup() throws Exception {
      MockitoAnnotations.initMocks(this);

      when(teamWf.getUuid()).thenReturn(35L);
      when(teamWf.getAtsId()).thenReturn("ZAQWS");
      when(atsVersion.getName()).thenReturn("Test Version");
      when(atsVersion.getUuid()).thenReturn(99L);

      AtsArtifactConfigCache cache = new AtsArtifactConfigCache();
      cache.cacheById(teamWf.getUuid(), atsVersion);

      when(cacheProvider.get()).thenReturn(cache);

      atsVersionCache = new AtsVersionCache(cacheProvider);
   }

   @Test
   public void testGetVersion() throws Exception {
      assertEquals("Test Version", atsVersionCache.getVersion(teamWf).getName());
   }

   @Test
   public void testCache() throws Exception {
      IAtsVersion testVersion = atsVersionCache.cache(teamWf, atsVersionCache.getVersion(teamWf));
      long uuid = testVersion.getUuid();
      assertEquals(99L, uuid);
   }

   @Test
   public void testHasVersion() throws Exception {
      assertTrue(atsVersionCache.hasVersion(teamWf));
   }

}