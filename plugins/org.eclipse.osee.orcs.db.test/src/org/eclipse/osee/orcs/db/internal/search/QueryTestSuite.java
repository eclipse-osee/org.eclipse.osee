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

import org.eclipse.osee.orcs.db.internal.search.engines.EnginesTestSuite;
import org.eclipse.osee.orcs.db.internal.search.handlers.HandlersTestSuite;
import org.eclipse.osee.orcs.db.internal.search.indexer.callable.consumer.IndexingTaskDatabaseTxCallableTest;
import org.eclipse.osee.orcs.db.internal.search.language.LanguageTestSuite;
import org.eclipse.osee.orcs.db.internal.search.tagger.TaggerTestSuite;
import org.eclipse.osee.orcs.db.internal.search.util.UtilityTestSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author Roberto E. Escobar
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
   EnginesTestSuite.class,
   HandlersTestSuite.class,
   LanguageTestSuite.class,
   TaggerTestSuite.class,
   IndexingTaskDatabaseTxCallableTest.class,
   UtilityTestSuite.class})
public class QueryTestSuite {
   // Test Suite
}
