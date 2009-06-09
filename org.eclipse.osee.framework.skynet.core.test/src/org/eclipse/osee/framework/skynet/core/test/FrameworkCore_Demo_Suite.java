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
package org.eclipse.osee.framework.skynet.core.test;

import org.eclipse.osee.framework.skynet.core.test.cases.ArtifactPurgeTest;
import org.eclipse.osee.framework.skynet.core.test.cases.Artifact_setAttributeValues;
import org.eclipse.osee.framework.skynet.core.test.cases.BranchPurgeTest;
import org.eclipse.osee.framework.skynet.core.test.cases.ChangeManagerTest;
import org.eclipse.osee.framework.skynet.core.test.cases.ConflictTest;
import org.eclipse.osee.framework.skynet.core.test.cases.CsvArtifactTest;
import org.eclipse.osee.framework.skynet.core.test.cases.NativeArtifactTest;
import org.eclipse.osee.framework.skynet.core.test.cases.OseeEnumTypeManagerTest;
import org.eclipse.osee.framework.skynet.core.test.cases.RelationDeletionTest;
import org.eclipse.osee.framework.skynet.core.test.cases.SevereLogMonitorTest;
import org.eclipse.osee.framework.skynet.core.test.cases.StaticIdManagerTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( {ArtifactPurgeTest.class, BranchPurgeTest.class, Artifact_setAttributeValues.class,
      CsvArtifactTest.class, NativeArtifactTest.class, OseeEnumTypeManagerTest.class, ConflictTest.class,
      ChangeManagerTest.class, SevereLogMonitorTest.class, RelationDeletionTest.class, StaticIdManagerTest.class})
/**
 * @author Donald G. Dunne
 */
public class FrameworkCore_Demo_Suite {

}
