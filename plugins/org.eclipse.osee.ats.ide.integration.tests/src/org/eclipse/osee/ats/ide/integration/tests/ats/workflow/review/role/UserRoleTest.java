/*********************************************************************
 * Copyright (c) 2011 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ats.workflow.review.role;

import org.eclipse.osee.ats.api.review.ReviewRole;
import org.eclipse.osee.ats.api.review.UserRole;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.junit.Assert;

/**
 * Test unit for {@link UserRole}
 *
 * @author Donald G. Dunne
 */
public class UserRoleTest {

   @org.junit.Test
   public void testToXmlFromXml() {
      IAtsWorkDefinition workDefinition =
         AtsApiService.get().getWorkDefinitionService().getDefaultPeerToPeerWorkflowDefinition();

      UserRole item = new UserRole(ReviewRole.Author, "1233", 23.3, true);
      UserRole fromItem = new UserRole(item.toXml(), workDefinition);
      Assert.assertEquals("1233", fromItem.getUserId());
      Assert.assertEquals(item.getRole(), fromItem.getRole());
      Assert.assertEquals(item.getHoursSpent(), fromItem.getHoursSpent());
      Assert.assertEquals(item.isCompleted(), fromItem.isCompleted());
   }
}
