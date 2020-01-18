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
package org.eclipse.osee.ats.core.users;

import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.user.IAtsUser;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractUserTest {
   public static final IAtsUser joe = new AtsUser(123456L, "joe", "joe", "b@b.com", true);
   public static final IAtsUser steve = new AtsUser(234567L, "steve", "steve", "asdf", false);
   public static final IAtsUser alice = new AtsUser(345678L, "alice", "alice", null, true);
}