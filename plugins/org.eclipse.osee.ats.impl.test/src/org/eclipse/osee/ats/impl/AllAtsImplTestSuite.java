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
package org.eclipse.osee.ats.impl;

import org.eclipse.osee.ats.impl.internal.model.ModelTestSuite;
import org.eclipse.osee.ats.impl.internal.workdef.WorkDefTestSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author Donald G. Dunne
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({WorkDefTestSuite.class, ModelTestSuite.class})
public class AllAtsImplTestSuite {
   // Test Suite
}
