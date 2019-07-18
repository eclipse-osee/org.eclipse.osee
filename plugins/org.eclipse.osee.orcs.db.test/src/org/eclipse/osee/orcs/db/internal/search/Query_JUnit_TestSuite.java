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
package org.eclipse.osee.orcs.db.internal.search;

import org.eclipse.osee.orcs.db.internal.search.handlers.Handlers_JUnit_TestSuite;
import org.eclipse.osee.orcs.db.internal.search.indexer.callable.consumer.IndexingTaskDatabaseTxCallableTest;
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
   IndexingTaskDatabaseTxCallableTest.class,
   Utility_Test_Suite.class})
public class Query_JUnit_TestSuite {
   // Test Suite
}
