/*******************************************************************************
 * Copyright (c) 2004, 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
   ProcessesTest.class,
   ReservedCharactersTest.class,
   StringsTest.class,
   UrlQueryTest.class})
public class JdkCoreUtilTestSuite {
   // Test Suite
}
