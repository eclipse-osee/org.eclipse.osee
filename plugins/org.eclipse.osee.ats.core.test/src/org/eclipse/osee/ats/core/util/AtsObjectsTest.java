/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.ats.core.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class AtsObjectsTest {

   @Test
   public void testConstructor() {
      new AtsObjects();
   }

   @Test
   public void testToIds() {
      List<IAtsObject> objs = new ArrayList<>();
      objs.add(AtsCoreUsers.SYSTEM_USER);
      objs.add(AtsCoreUsers.UNASSIGNED_USER);
      Assert.assertEquals(Arrays.asList(AtsCoreUsers.SYSTEM_USER.getId(), AtsCoreUsers.UNASSIGNED_USER.getId()),
         AtsObjects.toIds(objs));
   }

   @Test
   public void testToString() {
      Assert.assertEquals("", AtsObjects.toString("; ", Collections.emptyList()));

      List<Object> objs = new ArrayList<>();
      objs.add(AtsCoreUsers.SYSTEM_USER);
      objs.add(AtsCoreUsers.UNASSIGNED_USER);
      objs.add("Just a String");
      Assert.assertEquals(String.format("%s; %s; Just a String", AtsCoreUsers.SYSTEM_USER.getName(),
         AtsCoreUsers.UNASSIGNED_USER.getName()), AtsObjects.toString("; ", objs));
   }

   @Test
   public void testGetNames() {
      List<IAtsObject> objs = new ArrayList<>();
      objs.add(AtsCoreUsers.SYSTEM_USER);
      objs.add(AtsCoreUsers.UNASSIGNED_USER);
      Assert.assertEquals(Arrays.asList(AtsCoreUsers.SYSTEM_USER.getName(), AtsCoreUsers.UNASSIGNED_USER.getName()),
         AtsObjects.getNames(objs));
   }
}