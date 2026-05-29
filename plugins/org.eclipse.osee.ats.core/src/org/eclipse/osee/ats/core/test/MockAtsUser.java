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

package org.eclipse.osee.ats.core.test;

import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.framework.core.data.UserToken;

/**
 * @author Donald G. Dunne
 */
public class MockAtsUser extends AtsUser {

   public MockAtsUser(UserToken userToken) {
      this(userToken.getId(), userToken.getName(), userToken.getUserId());
   }

   public MockAtsUser(String name) {
      this(name, name);
   }

   public MockAtsUser(String name, String userId) {
      this(0L, name, userId);
   }

   public MockAtsUser(Long id, String name, String userId) {
      super(id, name, userId, name + "@mock.com", true);
   }

}
