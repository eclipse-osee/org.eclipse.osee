/*********************************************************************
 * Copyright (c) 2011 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests;

import java.util.Collection;
import java.util.logging.Level;
import org.eclipse.osee.ats.ide.demo.DemoUtil;
import org.eclipse.osee.framework.jdk.core.type.Named;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.junit.AfterClass;
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

   @AfterClass
   public static void cleanup() throws Exception {
      System.out.println("End Integration Tests");
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
            Named.getNames(dirtyArtifacts)),
         dirtyArtifacts.isEmpty());
   }
}