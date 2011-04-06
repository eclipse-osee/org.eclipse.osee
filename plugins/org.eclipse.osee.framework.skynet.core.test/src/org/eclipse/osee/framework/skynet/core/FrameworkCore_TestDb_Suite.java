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
package org.eclipse.osee.framework.skynet.core;

import org.eclipse.osee.framework.skynet.core.artifact.Artifact_getLastModified;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactLoaderTest;
import org.eclipse.osee.framework.skynet.core.event.filter.ArtifactEventFiltersTest;
import org.eclipse.osee.framework.skynet.core.event.filter.BranchEventFiltersTest;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEventLoopbackTest;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEventTest;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEventLoopbackTest;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEventTest;
import org.eclipse.osee.framework.skynet.core.event.model.TransactionEventLocalTest;
import org.eclipse.osee.framework.skynet.core.event.model.TransactionEventRemoteTest;
import org.eclipse.osee.framework.skynet.core.word.UpdateBookmarkIdTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
   UpdateBookmarkIdTest.class,
   ArtifactEventFiltersTest.class,
   BranchEventFiltersTest.class,
   TransactionEventLocalTest.class,
   TransactionEventRemoteTest.class,
   BranchEventLoopbackTest.class,
   BranchEventTest.class,
   ArtifactEventTest.class,
   ArtifactEventLoopbackTest.class,
   ArtifactLoaderTest.class,
   Artifact_getLastModified.class,})
/**
 * @author Donald G. Dunne
 */
public class FrameworkCore_TestDb_Suite {
   // do nothing
}
