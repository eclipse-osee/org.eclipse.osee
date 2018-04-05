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

import static org.mockito.Mockito.when;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractUserTest {

   // @formatter:off
   @Mock protected IAtsUser joe, steve, alice;
   // @formatter:on

   @Before
   public void setup() {
      MockitoAnnotations.initMocks(this);
      when(joe.getName()).thenReturn("joe");
      when(joe.getUserId()).thenReturn("joe");

      when(steve.getName()).thenReturn("steve");
      when(steve.getUserId()).thenReturn("steve");

      when(alice.getName()).thenReturn("alice");
      when(alice.getUserId()).thenReturn("alice");

   }
}
