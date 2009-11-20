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
package org.eclipse.osee.framework.skynet.core.test.cases;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.After;
import org.junit.Before;

/**
 * @author Ryan Schmitt
 */
public class DuplicateHridTest {
   @Before
   public void setUp() throws Exception {
      assertFalse("Not to be run on production datbase.", TestUtil.isProductionDb());
   }

   @After
   public void tearDown() throws Exception {

   }

   @org.junit.Test
   public void testDuplicatePrevention() throws OseeDataStoreException {
      String known_duplicate = get_used_HRID();
      String random_HRID = generate_random_HRID();
      assertFalse("Duplicate check returned false positive", Artifact.isUniqueHRID(known_duplicate));
      assertTrue("Duplicate check returned false negative", Artifact.isUniqueHRID(random_HRID));
      System.out.println("isUniqueHrid(\"" + known_duplicate + "\") returns " + Artifact.isUniqueHRID(known_duplicate));
      System.out.println("isUnqiueHrid(\"" + random_HRID + "\") returns " + Artifact.isUniqueHRID(random_HRID));
   }

   /* Queries the database and grabs the first HRID it sees */
   private static String get_used_HRID() throws OseeDataStoreException {
      String ret;
      IOseeStatement chStmt = ConnectionHandler.getStatement();
      try {
         chStmt.runPreparedQuery(GET_ARTIFACTS);
         chStmt.next();
         ret = chStmt.getString("human_readable_id");
      } finally {
         chStmt.close();
      }
      return ret;
   }

   private static final String GET_ARTIFACTS =
         "SELECT t1.guid,  t1.human_readable_id,  t3.name FROM osee_artifact t1, osee_artifact_type t3 ";

   private static final char[][] chars =
         new char[][] {
               {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K',
                     'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'},
               {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'B', 'C', 'D', 'F', 'G', 'H', 'J', 'K', 'L', 'M',
                     'N', 'P', 'Q', 'R', 'S', 'T', 'V', 'W', 'X', 'Y', 'Z'}};
   private static final int[] charsIndexLookup = new int[] {0, 1, 1, 1, 0};

   /*
    * Copied from Artifact.java
    * The hope here is that we won't randomly generate an HRID that's already taken;
    * given the size of the demo database, this is unlikely
    */
   private static String generate_random_HRID() {
      int seed = (int) (Math.random() * 34438396);
      char id[] = new char[charsIndexLookup.length];

      for (int i = 0; i < id.length; i++) {
         int radix = chars[charsIndexLookup[i]].length;
         id[i] = chars[charsIndexLookup[i]][seed % radix];
         seed = seed / radix;
      }

      String id_string = new String(id);

      return id_string;
   }
}
