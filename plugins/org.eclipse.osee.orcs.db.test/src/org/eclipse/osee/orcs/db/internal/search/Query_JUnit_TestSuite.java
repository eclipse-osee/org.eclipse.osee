/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.orcs.db.internal.search;

import org.eclipse.osee.orcs.db.internal.search.handlers.Handlers_JUnit_TestSuite;
import org.eclipse.osee.orcs.db.internal.search.language.LanguageTestSuite;
import org.eclipse.osee.orcs.db.internal.search.tagger.Tagger_JUnit_TestSuite;
import org.eclipse.osee.orcs.db.internal.search.util.Utility_Test_Suite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author Roberto E. Escobar
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
   Handlers_JUnit_TestSuite.class,
   LanguageTestSuite.class,
   Tagger_JUnit_TestSuite.class,
   Utility_Test_Suite.class})
public class Query_JUnit_TestSuite {
   // Test Suite
}
