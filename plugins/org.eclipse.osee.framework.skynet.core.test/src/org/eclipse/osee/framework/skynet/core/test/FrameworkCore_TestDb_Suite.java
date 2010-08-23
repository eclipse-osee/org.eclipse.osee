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

import org.eclipse.osee.framework.skynet.core.test.cases.ArtifactLoaderTest;
import org.eclipse.osee.framework.skynet.core.test.cases.Artifact_getLastModified;
import org.eclipse.osee.framework.skynet.core.test.event.ArtifactEventFiltersTest;
import org.eclipse.osee.framework.skynet.core.test.event.ArtifactEventLoopbackTest;
import org.eclipse.osee.framework.skynet.core.test.event.ArtifactEventTest;
import org.eclipse.osee.framework.skynet.core.test.event.BranchEventFiltersTest;
import org.eclipse.osee.framework.skynet.core.test.event.BranchEventLoopbackTest;
import org.eclipse.osee.framework.skynet.core.test.event.BranchEventTest;
import org.eclipse.osee.framework.skynet.core.test.event.TransactionEventLoopbackTest;
import org.eclipse.osee.framework.skynet.core.test.event.TransactionEventTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ArtifactEventFiltersTest.class, BranchEventFiltersTest.class, TransactionEventTest.class,
   TransactionEventLoopbackTest.class, BranchEventLoopbackTest.class, BranchEventTest.class, ArtifactEventTest.class,
   ArtifactEventLoopbackTest.class, ArtifactLoaderTest.class, Artifact_getLastModified.class,})
/**
 * @author Donald G. Dunne
 */
public class FrameworkCore_TestDb_Suite {
   // do nothing
}
