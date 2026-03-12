/*********************************************************************
 * Copyright (c) 2026 Boeing
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
package org.eclipse.osee.ats.ide.integration.tests.ats.branch;

import java.util.List;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchQueryData;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.ui.skynet.widgets.builder.BranchQueryBuilder;
import org.eclipse.osee.framework.ui.skynet.widgets.builder.XWidgetBuilder;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test for BranchQueryBuilder
 *
 * @author Donald G. Dunne
 */
public class BranchQueryBuilderTest {

   @Test
   public void test() {
      XWidgetBuilder wb = new XWidgetBuilder();
      BranchQueryBuilder qb = wb.andBranchQuery();

      qb.andBranchType(BranchType.WORKING).andBranchState(BranchState.MODIFIED);
      BranchQueryData branchQueryData = qb.getBranchQueryData();

      List<Branch> branches = AtsApiService.get().getServerEndpoints().getBrchEp().getBranches(branchQueryData);

      Assert.assertEquals(2, branches.size());
      for (Branch branch : branches) {
         Assert.assertEquals(BranchType.WORKING, branch.getBranchType());
         Assert.assertEquals(BranchState.MODIFIED, branch.getBranchState());
      }
   }

}
