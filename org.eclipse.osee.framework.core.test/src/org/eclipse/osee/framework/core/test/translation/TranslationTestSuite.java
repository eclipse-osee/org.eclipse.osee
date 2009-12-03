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
package org.eclipse.osee.framework.core.test.translation;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( {//
BranchCommitRequestTranslatorTest.class, //
      BranchCommitResponseTranslatorTest.class, //
      BranchCreationRequestTranslatorTest.class, //
      BranchCreationResponseTranslatorTest.class, //
      CacheUpdateRequestTranslatorTest.class, //
      ChangeItemTranslatorTest.class, //
      ChangeVersionTranslatorTest.class, //
      ChangeReportResponseTranslatorTest.class,//
      DataTranslationServiceFactoryTest.class, //
      DataTranslationServiceTest.class, //
      OseeImportModelRequestTranslatorTest.class, //
      OseeImportModelResponseTranslatorTest.class, //
      TableDataTranslatorTest.class, //
      TransactionCacheUpdateResponseTranslatorTest.class, //
      TransactionRecordTranslatorTest.class, //
      PurgeBranchRequestTranslatorTest.class //
})
/**
 * @author Roberto E. Escobar
 */
public class TranslationTestSuite {

}
