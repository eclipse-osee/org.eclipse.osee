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
package org.eclipse.osee.framework.ui.skynet.test.cases;

import java.util.Collection;
import junit.framework.TestCase;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.test.nonproduction.components.ConflictTestManager;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.blam.operation.RebaselineArtifacts;

/**
 * Tests the BLAM Rebaseline operation.
 * 
 * @author Jeff C. Phillips
 */
public class RebaselineArtifactTest extends TestCase {
   private static final boolean DEBUG =
         "TRUE".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.osee.framework.ui.skynet.test/debug/Junit"));

   protected void tearDown() throws Exception {
      super.tearDown();
      ConflictTestManager.cleanUpConflictTest();
   }

   protected void setUp() throws Exception {
      super.setUp();
      ConflictTestManager.initializeConflictTest();
      assertFalse(ClientSessionManager.isProductionDataStore());
   }

   public void testUpdateFromParent() throws Exception {
      SevereLoggingMonitor monitorLog = new SevereLoggingMonitor();
      OseeLog.registerLoggerListener(monitorLog);
      VariableMap variableMap = new VariableMap();
      Collection<Artifact> sourceArtifacts =
            ConflictTestManager.getArtifacts(true, ConflictTestManager.UPDATE_PARENT_QUERY);
      Collection<Artifact> destinationArtifacts =
            ConflictTestManager.getArtifacts(false, ConflictTestManager.UPDATE_PARENT_QUERY);
      Artifact sourceArtifact = sourceArtifacts.iterator().next();
      Artifact destArtifact = destinationArtifacts.iterator().next();

      if (DEBUG) {
         System.out.println("Before update");
         System.out.println("Source Artifact Name: " + sourceArtifact.getDescriptiveName());
         System.out.println("Dest Artifact Name: " + destArtifact.getDescriptiveName());
      }

      variableMap.setValue("Parent Branch Artifacts to update to Child Branch", destinationArtifacts);
      variableMap.setValue("Child Branch Name", sourceArtifacts.iterator().next().getBranch());
      RebaselineArtifacts rebaselineArtifacts = new RebaselineArtifacts();
      rebaselineArtifacts.runOperation(variableMap, new NullProgressMonitor());
      sourceArtifact.reloadAttributesAndRelations();

      if (DEBUG) {
         System.out.println("After update");
         System.out.println("Source Artifact Name: " + sourceArtifact.getDescriptiveName());
         System.out.println("Dest Artifact Name: " + destArtifact.getDescriptiveName());
      }
      assertTrue(sourceArtifact.getDescriptiveName().equals(destArtifact.getDescriptiveName()));
   }
}
