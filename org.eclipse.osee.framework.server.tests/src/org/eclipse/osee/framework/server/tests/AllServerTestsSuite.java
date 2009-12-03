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
package org.eclipse.osee.framework.server.tests;

import org.eclipse.osee.framework.branch.management.test.BranchManagementTestSuite;
import org.eclipse.osee.framework.core.test.FrameworkCoreTestSuite;
import org.eclipse.osee.framework.jdk.core.test.JdkCoreTestSuite;
import org.eclipse.osee.framework.resource.locator.attribute.test.AttributeLocatorProviderTestSuite;
import org.eclipse.osee.framework.resource.management.test.ResourceManagementTestSuite;
import org.eclipse.osee.framework.resource.provider.attribute.test.AttributeProviderTestSuite;
import org.eclipse.osee.framework.search.engine.test.AllSearchEngineTestSuite;
import org.eclipse.osee.framework.types.tests.OseeTypesTestSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author Roberto E. Escobar
 */
@RunWith(Suite.class)
@Suite.SuiteClasses( { //
JdkCoreTestSuite.class, //
      FrameworkCoreTestSuite.class, //
      BranchManagementTestSuite.class, //
      ResourceManagementTestSuite.class, //
      AttributeLocatorProviderTestSuite.class, //
      AttributeProviderTestSuite.class, //
      AllSearchEngineTestSuite.class, //
      OseeTypesTestSuite.class, //
})
public class AllServerTestsSuite {

}
