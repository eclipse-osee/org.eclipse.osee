/*
 * Created on Mar 20, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.workdef.provider;

import java.util.Set;
import junit.framework.Assert;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage;
import org.eclipse.osee.ats.dsl.atsDsl.UserByName;
import org.eclipse.osee.ats.dsl.atsDsl.UserByUserId;
import org.eclipse.osee.ats.dsl.atsDsl.UserRef;
import org.eclipse.osee.ats.dsl.atsDsl.impl.AtsDslFactoryImpl;
import org.eclipse.osee.ats.dsl.atsDsl.impl.TeamDefImpl;
import org.junit.Test;

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
