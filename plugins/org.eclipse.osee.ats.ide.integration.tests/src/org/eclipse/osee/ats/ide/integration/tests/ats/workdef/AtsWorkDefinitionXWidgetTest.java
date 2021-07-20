/*********************************************************************
 * Copyright (c) 2021 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ats.workdef;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.workdef.ValidateWorkDefXWidgetOperation;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test that all XWidgetName values specified in Workflow Definitions are provided by an IXWidgetProvider
 *
 * @author Donald G. Dunne
 */
public class AtsWorkDefinitionXWidgetTest {

   @Test
   public void workDefXWidgetTest() {

      AtsApi atsApi = AtsApiService.get();
      ValidateWorkDefXWidgetOperation op = new ValidateWorkDefXWidgetOperation(atsApi);
      XResultData rd = op.run();
      Assert.assertTrue(rd.toString(), rd.isSuccess());

   }

}
