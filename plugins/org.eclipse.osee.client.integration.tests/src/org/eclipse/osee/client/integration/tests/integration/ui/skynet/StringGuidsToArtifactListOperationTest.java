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
package org.eclipse.osee.client.integration.tests.integration.ui.skynet;

import static org.eclipse.osee.client.demo.DemoChoice.OSEE_CLIENT_DEMO;
import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_1;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import org.eclipse.osee.client.test.framework.OseeClientIntegrationRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.core.operation.StringOperationLogger;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.httpRequests.PurgeBranchHttpRequestOperation;
import org.eclipse.osee.framework.ui.skynet.blam.operation.StringGuidsToArtifactListOperation;
import org.eclipse.osee.framework.ui.skynet.widgets.IXWidgetInputAddable;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * {@link StringGuidsToArtifactListOperation}
 *
 * @author Karol M. Wilk
 */
public class StringGuidsToArtifactListOperationTest {

   @Rule
   public OseeClientIntegrationRule integration = new OseeClientIntegrationRule(OSEE_CLIENT_DEMO);

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   private static final String SAMPLE_SEPARATOR = "\r\n";
   private static final String invalidGuid = String.format("4F@3g@#$G@GZS%s", SAMPLE_SEPARATOR);
   private static final int capacity = 10;

   private IOseeBranch testBranch;

   private final Collection<Object> artifacts = new ArrayList<>(capacity);
   private final String[] guids = new String[capacity];

   @Before
   public void setUpOnce()  {
      testBranch = BranchManager.createWorkingBranch(SAW_Bld_1,
         StringGuidsToArtifactListOperationTest.class.getSimpleName() + " Branch");

      for (int i = 0; i < capacity; ++i) {
         Artifact artifact = ArtifactTypeManager.addArtifact(CoreArtifactTypes.Folder, testBranch, "Test" + i);
         guids[i] = artifact.getGuid();
         artifact.persist("Save");
         artifacts.add(artifact);
      }
   }

   @After
   public void tearDownOnce()  {
      Operations.executeWorkAndCheckStatus(new PurgeBranchHttpRequestOperation(testBranch, true));
   }

   @Test
   public void test_doWork_findCreatedArtifacts()  {
      Operations.executeWorkAndCheckStatus(new StringGuidsToArtifactListOperation(new StringOperationLogger(),
         generateSampleClipboardContent(), testBranch, widgetMock_Equal));
   }

   @Test
   public void test_doWork_guidGarbageData()  {
      Operations.executeWorkAndCheckStatus(new StringGuidsToArtifactListOperation(new StringOperationLogger(),
         generateGarbageClipboardContent(), testBranch, widgetMock_2Uniques));
   }

   @Test
   public void test_doWork_nullClipboardData()  {
      Operations.executeWorkAndCheckStatus(
         new StringGuidsToArtifactListOperation(new StringOperationLogger(), null, testBranch, widgetMock_Equal));
   }

   private String generateGarbageClipboardContent() {
      StringBuilder builder = new StringBuilder(guids.length);
      builder.append(invalidGuid);
      for (int i = 0; i < guids.length; i++) {
         switch (i) {
            case 1:
            case 5:
               //inject at some random places
               builder.append(invalidGuid);
               break;
            default:
               builder.append(String.format("%s%s", guids[i], SAMPLE_SEPARATOR));
               break;
         }
      }
      return builder.toString();
   }

   private String generateSampleClipboardContent() {
      StringBuilder builder = new StringBuilder();
      for (String guid : guids) {
         builder.append(String.format("%s%s", guid, SAMPLE_SEPARATOR));
      }
      return builder.toString();
   }

   private final IXWidgetInputAddable widgetMock_Equal = new IXWidgetInputAddable() {
      @Override
      public void addToInput(Collection<Object> objects) {
         List<Object> uniques = Collections.setComplement(new HashSet<Object>(objects), new HashSet<Object>(artifacts));
         Assert.assertTrue(uniques.isEmpty());
      }
   };

   private final IXWidgetInputAddable widgetMock_2Uniques = new IXWidgetInputAddable() {
      @Override
      public void addToInput(Collection<Object> objects) {
         List<Object> uniques = Collections.setComplement(new HashSet<Object>(artifacts), new HashSet<Object>(objects));
         Assert.assertTrue(uniques.size() == 2); //generateGarbageClipboardContent() takes out 1 and 5
      }
   };

}