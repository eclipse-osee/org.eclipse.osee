/*********************************************************************
 * Copyright (c) 2010 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.skynet.core.importing;

import org.eclipse.osee.framework.skynet.core.importing.parsers.ParsersSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ParsersSuite.class, ArtifactExtractorTest.class, ReqNumberingTest.class})
/**
 * @author Karol M. Wilk
 */
public class ImportingSuite {
   //
}
