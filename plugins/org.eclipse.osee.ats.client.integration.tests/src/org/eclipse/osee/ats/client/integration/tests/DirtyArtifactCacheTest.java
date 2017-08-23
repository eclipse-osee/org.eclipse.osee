/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.client.integration.tests;

import java.util.Collection;
import java.util.logging.Level;
import org.eclipse.osee.ats.client.demo.DemoUtil;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.junit.Assert;
import org.junit.BeforeClass;

/**
 * This test should be run as the last test of a suite to make sure that the ArtifactCache has no dirty artifacts.
 *
 * @author Donald G. Dunne
 */
public class DirtyArtifactCacheTest {

   @BeforeClass
   public static void setUp() throws Exception {
      DemoUtil.checkDbInitAndPopulateSuccess();
   }

   @org.junit.Test
   public void testArtifactCacheNotDirty() {
      final Collection<Artifact> dirtyArtifacts = ArtifactCache.getDirtyArtifacts();
      for (Artifact artifact : dirtyArtifacts) {
         OseeLog.logf(getClass(), Level.WARNING, "Name: %s Type: %s ", artifact.getName(),
            artifact.getArtifactTypeName());
      }
      Assert.assertTrue(
         String.format("After all tests are run, there should be no dirty artifacts in Artifact Cache; \n Found [%s]",
            Artifacts.getNames(dirtyArtifacts)),
         dirtyArtifacts.isEmpty());
   }
}
