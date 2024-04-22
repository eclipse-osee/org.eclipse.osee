/*********************************************************************
 * Copyright (c) 2024 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ats.workflow;

import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test workflow types. This must be copied to all "builds"/test suites as they configure/load different types and this
 * tests validates loaded types.
 *
 * @author Donald G. Dunne
 */
public class WorkflowTypesTest {

   /**
    * Duplicate workflow types should not exist. Workflows across different teams and programs should re-use the same
    * attribute types to reduce confusion on which attribute types to use when reporting and viewing.
    */
   @Test
   public void testWorkflowTypes() {
      XResultData rd = AtsApiService.get().getStoreService().validateTypes();
      Assert.assertTrue(rd.toString(), rd.isSuccess());
   }

}
