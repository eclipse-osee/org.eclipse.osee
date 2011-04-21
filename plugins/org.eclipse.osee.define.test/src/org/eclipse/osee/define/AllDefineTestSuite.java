/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define;

import org.eclipse.osee.define.blam.operation.BlamTestSuite;
import org.eclipse.osee.define.jobs.JobsTestSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({BlamTestSuite.class, JobsTestSuite.class})
public final class AllDefineTestSuite {
   // Tests added above
}
