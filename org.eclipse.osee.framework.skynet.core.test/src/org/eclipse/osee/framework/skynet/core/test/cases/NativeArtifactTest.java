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
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collection;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.attribute.CoreAttributes;
import org.eclipse.osee.framework.skynet.core.test.util.FrameworkTestUtil;
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

   @org.junit.Test
   public void testNativeArtifact() throws Exception {
      CsvArtifact csvArtifact =
            CsvArtifact.getCsvArtifact(getClass().getSimpleName(),
                  BranchManager.getKeyedBranch(DemoSawBuilds.SAW_Bld_1.name()), true);
      assertNotNull(csvArtifact);
      Artifact artifact = csvArtifact.getArtifact();
      assertTrue(artifact.isAttributeTypeValid(CoreAttributes.NATIVE_CONTENT.getName()));
      assertTrue(artifact.isAttributeTypeValid(CoreAttributes.NATIVE_EXTENSION.getName()));
   }

   @org.junit.Test
   public void testGetFileExtension() throws Exception {
      Artifact nativeArtifact = getNativeArtifact();
      assertTrue(nativeArtifact.getSoleAttributeValue(CoreAttributes.NATIVE_EXTENSION.getName(), "").equals("csv"));
   }

   @org.junit.Test
   public void testSetAndGetValueAsString() throws Exception {
      Artifact nativeArtifact = getNativeArtifact();
      nativeArtifact.setSoleAttributeFromString(CoreAttributes.NATIVE_CONTENT.getName(), "hello world");
      nativeArtifact.persist();
      String content = nativeArtifact.getSoleAttributeValueAsString(CoreAttributes.NATIVE_CONTENT.getName(), "");
      assertEquals("hello world", content);
   }

   @org.junit.Test
   public void testSetAndGetNativeContent() throws Exception {
      File file = OseeData.getFile(GUID.create() + ".txt");
      Lib.writeStringToFile("hello world", file);
      Artifact nativeArtifact = getNativeArtifact();
      nativeArtifact.setSoleAttributeFromStream(CoreAttributes.NATIVE_CONTENT.getName(), new FileInputStream(file));
      nativeArtifact.persist();
      InputStream inputStream = null;
      try {
         inputStream = nativeArtifact.getSoleAttributeValue(CoreAttributes.NATIVE_CONTENT.getName(), null);
         String content = Lib.inputStreamToString(inputStream);
         assertEquals("hello world", content);
      } finally {
         if (inputStream != null) {
            inputStream.close();
         }
      }
   }

   @org.junit.Test
   public void testCleanupPost() throws Exception {
      cleanup();
   }

   private Artifact getNativeArtifact() throws Exception {
      Branch branch = BranchManager.getKeyedBranch(DemoSawBuilds.SAW_Bld_1.name());
      return CsvArtifact.getCsvArtifact(getClass().getSimpleName(), branch, false).getArtifact();
   }

   private void cleanup() throws Exception {
      Branch branch = BranchManager.getKeyedBranch(DemoSawBuilds.SAW_Bld_1.name());
      Collection<Artifact> arts = ArtifactQuery.getArtifactListFromName(getClass().getSimpleName(), branch, false);
      FrameworkTestUtil.purgeArtifacts(arts);
   }
}
