/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.authentication.admin.internal;

import static org.junit.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;
import org.eclipse.osee.authentication.admin.AuthenticatedUser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test Case for {@link AnonymousUser}
 * 
 * @author Roberto E. Escobar
 */
public class AnonymousUserTest {

   private static final String NAME = AnonymousUser.ANONYMOUS_NAME;
   private AuthenticatedUser actual;

   @Before
   public void testSetup() {
      initMocks(this);

      actual = new AnonymousUser();
   }

   @Test
   public void testUser() {
      assertEquals(NAME, actual.getName());
      assertEquals(NAME, actual.getDisplayName());

      assertEquals("", actual.getUserName());
      assertEquals("", actual.getEmailAddress());

      assertEquals(false, actual.getRoles().iterator().hasNext());
      assertEquals(true, actual.isActive());
      assertEquals(false, actual.isAuthenticated());
   }

}
