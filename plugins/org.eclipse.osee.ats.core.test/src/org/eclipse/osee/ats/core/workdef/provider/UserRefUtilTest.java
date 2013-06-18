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
package org.eclipse.osee.ats.core.workdef.provider;

import java.util.Set;
import org.junit.Assert;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.osee.ats.dsl.UserRefUtil;
import org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage;
import org.eclipse.osee.ats.dsl.atsDsl.UserByName;
import org.eclipse.osee.ats.dsl.atsDsl.UserByUserId;
import org.eclipse.osee.ats.dsl.atsDsl.UserRef;
import org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslFactoryImpl;
import org.eclipse.osee.ats.dsl.atsDsl.impl.TeamDefImpl;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class UserRefUtilTest {

   @Test
   public void testConstructor() {
      new UserRefUtil();
   }

   @Test
   public void testGetUserIds() {

      TeamDefImpl teamDef = (TeamDefImpl) AtsDslFactoryImpl.init().createTeamDef();

      EList<UserRef> lead =
         new EObjectContainmentEList<UserRef>(UserRef.class, teamDef, AtsDslPackage.TEAM_DEF__MEMBER);

      UserByUserId userRef = AtsDslFactoryImpl.init().createUserByUserId();
      userRef.setUserId("asdf");
      lead.add(userRef);

      UserByUserId userRef2 = AtsDslFactoryImpl.init().createUserByUserId();
      userRef2.setUserId("qwer");
      lead.add(userRef2);

      UserByName userRef3 = AtsDslFactoryImpl.init().createUserByName();
      userRef3.setUserName("name");
      lead.add(userRef3);

      Set<String> userIds = UserRefUtil.getUserIds(lead);
      Assert.assertEquals(2, userIds.size());
      Assert.assertTrue(userIds.contains("asdf"));
      Assert.assertTrue(userIds.contains("qwer"));
   }

   @Test
   public void testGetUserNames() {
      TeamDefImpl teamDef = (TeamDefImpl) AtsDslFactoryImpl.init().createTeamDef();

      EList<UserRef> lead =
         new EObjectContainmentEList<UserRef>(UserRef.class, teamDef, AtsDslPackage.TEAM_DEF__MEMBER);

      UserByName userRef = AtsDslFactoryImpl.init().createUserByName();
      userRef.setUserName("asdf");
      lead.add(userRef);

      UserByName userRef2 = AtsDslFactoryImpl.init().createUserByName();
      userRef2.setUserName("qwer");
      lead.add(userRef2);

      UserByUserId userRef3 = AtsDslFactoryImpl.init().createUserByUserId();
      userRef3.setUserId("name");
      lead.add(userRef3);

      Set<String> userIds = UserRefUtil.getUserNames(lead);
      Assert.assertEquals(2, userIds.size());
      Assert.assertTrue(userIds.contains("asdf"));
      Assert.assertTrue(userIds.contains("qwer"));
   }

}
