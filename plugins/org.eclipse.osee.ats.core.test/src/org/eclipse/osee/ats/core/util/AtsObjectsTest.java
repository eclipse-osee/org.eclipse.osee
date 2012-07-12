/*
 * Created on Mar 16, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import junit.framework.Assert;
import org.eclipse.osee.ats.core.model.IAtsObject;
import org.eclipse.osee.ats.core.users.Guest;
import org.eclipse.osee.ats.core.users.SystemUser;
import org.eclipse.osee.ats.core.users.UnAssigned;
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
   public void testToGuids() {
      List<IAtsObject> objs = new ArrayList<IAtsObject>();
      objs.add(SystemUser.instance);
      objs.add(Guest.instance);
      Assert.assertEquals(Arrays.asList(SystemUser.instance.getGuid(), Guest.instance.getGuid()),
         AtsObjects.toGuids(objs));
   }

   @Test
   public void testToString() {
      Assert.assertEquals("", AtsObjects.toString("; ", Collections.emptyList()));

      List<Object> objs = new ArrayList<Object>();
      objs.add(SystemUser.instance);
      objs.add(Guest.instance);
      objs.add(UnAssigned.instance);
      objs.add("Just a String");
      Assert.assertEquals(String.format("%s; %s; %s; Just a String", SystemUser.instance.getName(),
         Guest.instance.getName(), UnAssigned.instance.getName()), AtsObjects.toString("; ", objs));
   }

   @Test
   public void testGetNames() {
      List<IAtsObject> objs = new ArrayList<IAtsObject>();
      objs.add(SystemUser.instance);
      objs.add(Guest.instance);
      objs.add(UnAssigned.instance);
      Assert.assertEquals(
         Arrays.asList(SystemUser.instance.getName(), Guest.instance.getName(), UnAssigned.instance.getName()),
         AtsObjects.getNames(objs));
   }

}
