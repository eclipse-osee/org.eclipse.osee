/*
 * Created on May 24, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.test2.cases;

import java.io.File;
import java.util.Collection;
import junit.framework.TestCase;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.NativeArtifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.utility.CsvArtifact;
import org.eclipse.osee.framework.ui.plugin.util.OseeData;
import org.eclipse.osee.support.test.util.DemoSawBuilds;

/**
 * @author Ryan D. Brooks
 */
public class NativeArtifactTest extends TestCase {

   public void testCleanupPre() throws Exception {
      cleanup();
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.artifact.NativeArtifact#NativeArtifact(org.eclipse.osee.framework.skynet.core.artifact.ArtifactFactory, java.lang.String, java.lang.String, org.eclipse.osee.framework.skynet.core.artifact.Branch, org.eclipse.osee.framework.skynet.core.artifact.ArtifactType)}
    * .
    */
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
   public void testGetImage() throws Exception {
      NativeArtifact nativeArtifact = getNativeArtifact();
      assertTrue(nativeArtifact.getFileExtension().equals("csv"));
   }

   /**
    * Test method for {@link org.eclipse.osee.framework.skynet.core.artifact.NativeArtifact#getFileName()}.
    */
   public void testGetFileName() throws Exception {
      NativeArtifact nativeArtifact = getNativeArtifact();
      assertEquals(nativeArtifact.getFileName(), "NativeArtifactTest.csv");
   }

   /**
    * Test method for {@link org.eclipse.osee.framework.skynet.core.artifact.NativeArtifact#getFileExtension()}.
    */
   public void testGetFileExtension() throws Exception {
      NativeArtifact nativeArtifact = getNativeArtifact();
      assertTrue(nativeArtifact.getFileExtension().equals("csv"));
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.artifact.NativeArtifact#setNativeContent(java.io.File)}.
    */
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
   public void testSetAndGetNativeContent() throws Exception {
      NativeArtifact nativeArtifact = getNativeArtifact();
      nativeArtifact.setNativeContent(Lib.stringToInputStream("hello world"));
      nativeArtifact.persistAttributes();
      String content = Lib.inputStreamToString(nativeArtifact.getNativeContent());
      assertEquals("hello world", content);
   }

   public void testGetValueAsString() throws Exception {
      NativeArtifact nativeArtifact = getNativeArtifact();
      nativeArtifact.setNativeContent(Lib.stringToInputStream("hello world"));
      nativeArtifact.persistAttributes();
      String content = nativeArtifact.getSoleAttributeValueAsString(NativeArtifact.CONTENT_NAME, "");
      // TODO Failure expected cause getSoleAttributeValueAsString not working; this needs to be fixed
      assertEquals("hello world", content);
   }

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
