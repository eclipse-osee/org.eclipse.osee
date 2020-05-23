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

package org.eclipse.osee.ats.core.users;


import org.eclipse.osee.ats.api.user.AtsUser;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractUserTest {
   public static final AtsUser joe = new AtsUser(123456L, "joe", "joe", "b@b.com", true);
   public static final AtsUser steve = new AtsUser(234567L, "steve", "steve", "asdf", false);
   public static final AtsUser alice = new AtsUser(345678L, "alice", "alice", null, true);
}