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
package org.eclipse.osee.disposition.rest.internal;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author Angel Avila
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
   DispoApiTest.class,
   DispoConnectorTest.class,
   LocationRangesCompressorTest.class,
   DispoResolutionValidatorTest.class})
public class InternalTestSuite {
   // Test Suite
}
