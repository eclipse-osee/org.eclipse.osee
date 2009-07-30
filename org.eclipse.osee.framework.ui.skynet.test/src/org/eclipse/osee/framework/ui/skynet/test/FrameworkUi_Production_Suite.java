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
package org.eclipse.osee.framework.ui.skynet.test;

import org.eclipse.osee.framework.ui.skynet.test.cases.DatabaseIntegrityTest;
import org.eclipse.osee.framework.ui.skynet.test.cases.FrameworkImageTest;
import org.eclipse.osee.framework.ui.skynet.test.cases.OseeEmailTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( {FrameworkImageTest.class, OseeEmailTest.class, DatabaseIntegrityTest.class})
/**
 * @author Donald G. Dunne
 */
public class FrameworkUi_Production_Suite {

}
