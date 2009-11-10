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
package org.eclipse.osee.framework.resource.management.test;

import org.eclipse.osee.framework.resource.management.test.util.ResourceUtilTestSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( {ResourceUtilTestSuite.class, OptionsTest.class, ResourceLocatorManagerTest.class,
      ResourceManagerTest.class})
/**
 * @author Roberto E. Escobar
 */
public class ResourceManagementTestSuite {

}
