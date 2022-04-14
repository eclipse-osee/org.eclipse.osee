/*********************************************************************
 * Copyright (c) 2010,2022 Boeing
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

package org.eclipse.osee.framework.jdk.core.util;

import org.eclipse.osee.framework.jdk.core.util.annotation.AnnotationTestSuite;
import org.eclipse.osee.framework.jdk.core.util.io.IoTestSuite;
import org.eclipse.osee.framework.jdk.core.util.xml.XmlTestSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author Roberto E. Escobar
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
   AnnotationTestSuite.class,
   IoTestSuite.class,
   XmlTestSuite.class,
   CollectionsTest.class,
   CompareTest.class,
   DateIteratorTest.class,
   EncryptUtilityTest.class,
   GUIDTest.class,
   HashCollectionTest.class,
   HexUtilTest.class,
   LibTest.class,
   ProcessesTest.class,
   ReservedCharactersTest.class,
   StringsTest.class,
   UrlQueryTest.class,
   EnumFunctionalInterafaceMapsTest.class})
public class JdkCoreUtilTestSuite {
   // Test Suite
}
