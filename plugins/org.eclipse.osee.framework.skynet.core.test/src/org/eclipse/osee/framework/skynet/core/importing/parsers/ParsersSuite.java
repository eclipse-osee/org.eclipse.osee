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

package org.eclipse.osee.framework.skynet.core.importing.parsers;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
   DoorsColumnTypeTest.class,
   DoorsDataTypeTest.class,
   DoorsTableRowCollectorTest.class,
   DoorsTableRowTest.class,
   OutlineResolutionAndNumberTest.class,
   RoughArtifactMetaDataTest.class,
   WordMLExtractorDelegateTableOfContentsTest.class,
   WordMLExtractorDelegateNoNumberTest.class})
public class ParsersSuite {
   // do nothing
}
