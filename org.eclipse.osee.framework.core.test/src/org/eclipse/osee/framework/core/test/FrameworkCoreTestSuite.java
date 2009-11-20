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
package org.eclipse.osee.framework.core.test;

import org.eclipse.osee.framework.core.test.cache.CacheTestSuite;
import org.eclipse.osee.framework.core.test.data.DataTestSuite;
import org.eclipse.osee.framework.core.test.fields.FieldTestSuite;
import org.eclipse.osee.framework.core.test.model.ModelTestSuite;
import org.eclipse.osee.framework.core.test.translation.TranslationTestSuite;
import org.eclipse.osee.framework.core.test.util.UtilTestSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( {//
CacheTestSuite.class, //
      DataTestSuite.class, //
      TranslationTestSuite.class, //
      FieldTestSuite.class, //
      ModelTestSuite.class, //
      UtilTestSuite.class, //
})
/**
 * @author Roberto E. Escobar
 */
public class FrameworkCoreTestSuite {

}
