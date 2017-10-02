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

package org.eclipse.osee.client.integration.tests.integration.skynet.core;

import static org.eclipse.osee.client.demo.DemoChoice.OSEE_CLIENT_DEMO;
import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_2;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.client.test.framework.OseeClientIntegrationRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.PurgeArtifacts;
import org.eclipse.osee.framework.skynet.core.utility.CsvArtifact;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author Ryan D. Brooks
 */
public class NativeArtifactTest {

   @Rule
   public OseeClientIntegrationRule integration = new OseeClientIntegrationRule(OSEE_CLIENT_DEMO);

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   @Rule
   public final TemporaryFolder folder = new TemporaryFolder();

   private static final String ARTIFACT_NAME = NativeArtifactTest.class.getSimpleName();

   private final Set<Artifact> testArtifacts = new HashSet<>();

   @After
   public void cleanup() throws Exception {
      Operations.executeWorkAndCheckStatus(new PurgeArtifacts(testArtifacts));
   }

   @Test
   public void testGetFileExtension() throws Exception {
      Artifact nativeArtifact = getNativeArtifact();
      assertTrue(nativeArtifact.getSoleAttributeValue(CoreAttributeTypes.Extension, "").equals("csv"));
   }

   @Test
   public void testNativeArtifact() throws Exception {
      CsvArtifact csvArtifact = getCsvArtifact(true);
      assertNotNull(csvArtifact);
      Artifact artifact = csvArtifact.getArtifact();
      assertTrue(artifact.isAttributeTypeValid(CoreAttributeTypes.NativeContent));
      assertTrue(artifact.isAttributeTypeValid(CoreAttributeTypes.Extension));
   }

   @Test
   public void testSetAndGetValueAsString() throws Exception {
      Artifact nativeArtifact = getNativeArtifact();
      nativeArtifact.setSoleAttributeFromString(CoreAttributeTypes.NativeContent, "hello world");
      nativeArtifact.persist(getClass().getSimpleName());
      String content = nativeArtifact.getSoleAttributeValueAsString(CoreAttributeTypes.NativeContent, "");
      assertEquals("hello world", content);
   }

   @Test
   public void testSetAndGetNativeContent() throws Exception {
      File file = folder.newFile(GUID.create() + ".txt");
      Lib.writeStringToFile("hello world", file);

      Artifact nativeArtifact = getNativeArtifact();
      nativeArtifact.setSoleAttributeFromStream(CoreAttributeTypes.NativeContent, new FileInputStream(file));
      nativeArtifact.persist(getClass().getSimpleName());

      InputStream inputStream = null;
      try {
         inputStream = nativeArtifact.getSoleAttributeValue(CoreAttributeTypes.NativeContent, null);
         String content = Lib.inputStreamToString(inputStream);
         assertEquals("hello world", content);
      } finally {
         Lib.close(inputStream);
      }
   }

   private Artifact getNativeArtifact()  {
      return getCsvArtifact(false).getArtifact();
   }

   private CsvArtifact getCsvArtifact(boolean create)  {
      CsvArtifact csvArtifact = CsvArtifact.getCsvArtifact(ARTIFACT_NAME, SAW_Bld_2, create);
      testArtifacts.add(csvArtifact.getArtifact());
      return csvArtifact;
   }
}