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

package org.eclipse.osee.ats.ide.integration.tests.ats.resource;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
   OrcsWriterEndpointTest.class,
   AtsActionEndpointImplTest.class,
   AtsActionEndpointImplOptionsTest.class,
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
   // do nothing
}
