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
package org.eclipse.osee.ats.client.integration.tests.ats.resource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
   OrcsWriterEndpointTest.class,
   AtsActionEndpointImplTest.class,
   AtsAttributeEndpointImplTest.class,
   ActionUiResourceTest.class,
   ConvertResourceTest.class,
   UserResourceTest.class,
   StateResourceTest.class
   //WordUpdateEndpointImplTest.class
})
/**
 * This test suite contains test that can be run against any production db
 *
 * @author Donald G. Dunne
 */
public class AtsTest_Resource_Suite {
   @BeforeClass
   public static void setUp() throws Exception {
      System.out.println("\n\nBegin " + AtsTest_Resource_Suite.class.getSimpleName());
   }

   @AfterClass
   public static void tearDown() throws Exception {
      System.out.println("End " + AtsTest_Resource_Suite.class.getSimpleName());
   }
}
