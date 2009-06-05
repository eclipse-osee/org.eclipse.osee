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

import org.eclipse.osee.framework.skynet.core.test2.cases.ArtifactPurgeTest;
import org.eclipse.osee.framework.skynet.core.test2.cases.Artifact_setAttributeValues;
import org.eclipse.osee.framework.skynet.core.test2.cases.BranchPurgeTest;
import org.eclipse.osee.framework.skynet.core.test2.cases.ChangeManagerTest;
import org.eclipse.osee.framework.skynet.core.test2.cases.ConflictTestSuite;
import org.eclipse.osee.framework.skynet.core.test2.cases.CsvArtifactTest;
import org.eclipse.osee.framework.skynet.core.test2.cases.NativeArtifactTest;
import org.eclipse.osee.framework.skynet.core.test2.cases.OseeEnumTypeManagerTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( {ArtifactPurgeTest.class, BranchPurgeTest.class, Artifact_setAttributeValues.class,
      CsvArtifactTest.class, NativeArtifactTest.class, OseeEnumTypeManagerTest.class, ConflictTestSuite.class,
      ChangeManagerTest.class})
/**
 * @author Donald G. Dunne
 */
public class FrameworkCore_Demo_Suite {

}
