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
package org.eclipse.osee.framework.skynet.core.test2;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.osee.framework.skynet.core.test2.cases.ArtifactPurgeTest;
import org.eclipse.osee.framework.skynet.core.test2.cases.Artifact_setAttributeValues;
import org.eclipse.osee.framework.skynet.core.test2.cases.BranchPurgeTest;
import org.eclipse.osee.framework.skynet.core.test2.cases.ChangeManagerTest;
import org.eclipse.osee.framework.skynet.core.test2.cases.ConflictTestSuite;
import org.eclipse.osee.framework.skynet.core.test2.cases.CsvArtifactTest;
import org.eclipse.osee.framework.skynet.core.test2.cases.NativeArtifactTest;
import org.eclipse.osee.framework.skynet.core.test2.cases.OseeEnumTypeManagerTest;

/**
 * @author Donald G. Dunne
 */
public class FrameworkCore_Demo_Suite extends TestSuite {

   public static Test suite() {
      TestSuite suite = new TestSuite("FrameworkCore_Demo_Suite");
      //$JUnit-BEGIN$
      suite.addTestSuite(ArtifactPurgeTest.class);
      suite.addTestSuite(BranchPurgeTest.class);
      suite.addTestSuite(Artifact_setAttributeValues.class);
      suite.addTestSuite(CsvArtifactTest.class);
      suite.addTestSuite(NativeArtifactTest.class);
      suite.addTestSuite(OseeEnumTypeManagerTest.class);
      suite.addTest(ConflictTestSuite.suite());
      suite.addTestSuite(ChangeManagerTest.class);
      //$JUnit-END$
      return suite;
   }

}
