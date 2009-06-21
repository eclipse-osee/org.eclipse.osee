/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.framework.skynet.core.test.cases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.io.File;
import java.util.Collection;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.NativeArtifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.utility.CsvArtifact;
import org.eclipse.osee.framework.skynet.core.utility.OseeData;
import org.eclipse.osee.support.test.util.DemoSawBuilds;

/**
 * @author Ryan D. Brooks
 */
public class NativeArtifactTest {

   @org.junit.Test
   public void testCleanupPre() throws Exception {
      cleanup();
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.artifact.NativeArtifact#NativeArtifact(org.eclipse.osee.framework.skynet.core.artifact.ArtifactFactory, java.lang.String, java.lang.String, org.eclipse.osee.framework.skynet.core.artifact.Branch, org.eclipse.osee.framework.skynet.core.artifact.ArtifactType)}
    * .
    */
   @org.junit.Test
   public void testNativeArtifact() throws Exception {
      CsvArtifact csvArtifact =
            CsvArtifact.getCsvArtifact(getClass().getSimpleName(),
                  BranchManager.getKeyedBranch(DemoSawBuilds.SAW_Bld_1.name()), true);
      assertNotNull(csvArtifact);
      assertTrue(csvArtifact.getArtifact() instanceof NativeArtifact);
   }

   /**
    * Test method for {@link org.eclipse.osee.framework.skynet.core.artifact.NativeArtifact#getImage()}.
    */
   @org.junit.Test
   public void testGetImage() throws Exception {
      NativeArtifact nativeArtifact = getNativeArtifact();
      assertTrue(nativeArtifact.getFileExtension().equals("csv"));
   }

   /**
    * Test method for {@link org.eclipse.osee.framework.skynet.core.artifact.NativeArtifact#getFileName()}.
    */
   @org.junit.Test
   public void testGetFileName() throws Exception {
      NativeArtifact nativeArtifact = getNativeArtifact();
      assertEquals(nativeArtifact.getFileName(), "NativeArtifactTest.csv");
   }

   /**
    * Test method for {@link org.eclipse.osee.framework.skynet.core.artifact.NativeArtifact#getFileExtension()}.
    */
   @org.junit.Test
   public void testGetFileExtension() throws Exception {
      NativeArtifact nativeArtifact = getNativeArtifact();
      assertTrue(nativeArtifact.getFileExtension().equals("csv"));
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.artifact.NativeArtifact#setNativeContent(java.io.File)}.
    */
   @org.junit.Test
   public void testSetNativeContentFile() throws Exception {
      File file = OseeData.getFile(GUID.generateGuidStr() + ".txt");
      Lib.writeStringToFile("hello world", file);
      NativeArtifact nativeArtifact = getNativeArtifact();
      nativeArtifact.setNativeContent(file);
      nativeArtifact.persistAttributes();
      String content = Lib.inputStreamToString(nativeArtifact.getNativeContent());
      assertEquals("hello world", content);
   }

   /**
    * Test method for {@link org.eclipse.osee.framework.skynet.core.artifact.NativeArtifact#getNativeContent()}.
    */
   @org.junit.Test
   public void testSetAndGetNativeContent() throws Exception {
      NativeArtifact nativeArtifact = getNativeArtifact();
      nativeArtifact.setNativeContent(Lib.stringToInputStream("hello world"));
      nativeArtifact.persistAttributes();
      String content = Lib.inputStreamToString(nativeArtifact.getNativeContent());
      assertEquals("hello world", content);
   }

   @org.junit.Test
   public void testGetValueAsString() throws Exception {
      NativeArtifact nativeArtifact = getNativeArtifact();
      nativeArtifact.setNativeContent(Lib.stringToInputStream("hello world"));
      nativeArtifact.persistAttributes();
      String content = nativeArtifact.getSoleAttributeValueAsString(NativeArtifact.CONTENT_NAME, "");
      assertEquals("hello world", content);
   }

   @org.junit.Test
   public void testCleanupPost() throws Exception {
      cleanup();
   }

   private NativeArtifact getNativeArtifact() throws Exception {
      return CsvArtifact.getCsvArtifact(getClass().getSimpleName(),
            BranchManager.getKeyedBranch(DemoSawBuilds.SAW_Bld_1.name()), false).getArtifact();
   }

   private void cleanup() throws Exception {
      Collection<Artifact> arts =
            ArtifactQuery.getArtifactsFromName(getClass().getSimpleName(),
                  BranchManager.getKeyedBranch(DemoSawBuilds.SAW_Bld_1.name()), false);
      ArtifactPersistenceManager.purgeArtifacts(arts);
   }
}
