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
package org.eclipse.osee.ats.test.cases;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.osee.ats.config.AtsBulkLoadCache;
import org.eclipse.osee.ats.world.search.MyWorldSearchItem;
import org.eclipse.osee.ats.world.search.TeamWorldSearchItem;
import org.eclipse.osee.ats.world.search.WorldSearchItem.SearchType;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Andrew M. Finkbeiner
 */
public class AtsQueryPerformanceTests  {

   public AtsQueryPerformanceTests() {
      AtsBulkLoadCache.run(true);
   }

   @org.junit.Test
public void testUserWorldSearch() throws Exception {
      User usr = getAUser();
      assertNotNull("User does not exist", usr);
      MyWorldSearchItem search = new MyWorldSearchItem();
      long startTime = System.currentTimeMillis();
      Collection<Artifact> artifacts = search.searchIt(usr);
      long elapsedTime = System.currentTimeMillis() - startTime;
      System.out.println(String.format("testUserWorldSearch took %dms for %d artifacts", elapsedTime, artifacts.size()));
      assertTrue("No artifacts found", artifacts.size() > 0);
      assertTrue("testUserWorldSearch should take less than 2500ms", elapsedTime < 2500);
   }

   @org.junit.Test
public void testTeamWorldSearchItem() throws Exception {
      TeamWorldSearchItem searchItem =
            new TeamWorldSearchItem("Show Open OSEE Actions", new String[] {"ATS", "Define", "OTE"}, false, true,
                  false, null, null, null, null);
      long startTime = System.currentTimeMillis();
      Collection<Artifact> artifacts = searchItem.performSearch(SearchType.Search);
      long elapsedTime = System.currentTimeMillis() - startTime;
      System.out.println(String.format("testTeamWorldSearchItem took %dms for %d artifacts", elapsedTime,
            artifacts.size()));
      assertTrue("No artifacts found", artifacts.size() > 0);
      assertTrue("testTeamWorldSearchItem should take less than 9000ms", elapsedTime < 9000);
   }

   private User getAUser() throws OseeCoreException {
      ArrayList<User> users = UserManager.getUsers();
      User andy = null;
      for (User usr : users) {
         if (usr.getSoleAttributeValueAsString("Name", "unknown").contains("Finkbeiner")) {
            andy = usr;
            break;
         }
      }
      return andy;
   }

}
