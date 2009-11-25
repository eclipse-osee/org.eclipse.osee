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
BasicArtifactTranslatorTest.class, //
      BranchTranslatorTest.class, //
      BranchCommitRequestTranslatorTest.class, //
      BranchCommitResponseTranslatorTest.class, //
      CacheUpdateRequestTranslatorTest.class, //
      CacheUpdateResponseTranslatorTest.class, //
      ChangeItemTranslatorTest.class, //
      ChangeVersionTranslatorTest.class, //
      DataTranslationServiceFactoryTest.class, //
      DataTranslationServiceTest.class, //
      OseeEnumEntryTranslatorTest.class, //
      OseeEnumTypeTranslatorTest.class, //
      RelationTypeTranslatorTest.class, //
      TransactionRecordTranslatorTest.class //
})
/**
 * @author Roberto E. Escobar
 */
public class TranslationTestSuite {

}
