/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.intergration;

import static org.junit.Assert.assertEquals;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.ds.OrcsData;
import org.eclipse.osee.orcs.core.ds.RelationData;
import org.eclipse.osee.orcs.core.ds.VersionData;
import org.eclipse.osee.orcs.db.mock.OseeDatabase;
import org.eclipse.osee.orcs.db.mock.OsgiRule;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;

/**
 * @author Roberto E. Escobar
 */
public class IntegrationUtil {

   private static final Comparator<OrcsData> SORT_BY_LOCAL_ID = new IdComparator();

   public static TestRule integrationRule(Object testObject, String dbId) {
      return RuleChain.outerRule(new OseeDatabase(dbId)).around(new OsgiRule(testObject));
   }

   public static void sort(List<? extends OrcsData> data) {
      Collections.sort(data, SORT_BY_LOCAL_ID);
   }

   public static void verifyData(ArtifactData data, Object... values) {
      int index = 0;
      assertEquals(data.getLocalId(), values[index++]);
      assertEquals(data.getGuid(), values[index++]);
      assertEquals(data.getModType(), values[index++]);
      assertEquals(data.getTypeUuid(), values[index++]);

      verifyData(data.getVersion(), index, values);
   }

   public static void verifyData(AttributeData data, Object... values) throws OseeCoreException {
      int index = 0;
      assertEquals(data.getLocalId(), values[index++]);
      assertEquals(data.getArtifactId(), values[index++]);
      assertEquals(data.getModType(), values[index++]);
      assertEquals(data.getTypeUuid(), values[index++]);

      index = verifyData(data.getVersion(), index, values);

      Object[] proxied = data.getDataProxy().getData();
      assertEquals(proxied[0], values[index++]); // value
      assertEquals(proxied[1], values[index++]); // uri
   }

   public static void verifyData(RelationData data, Object... values) {
      int index = 0;
      assertEquals(data.getLocalId(), values[index++]);

      assertEquals(data.getArtIdA(), values[index++]);
      assertEquals(data.getArtIdB(), values[index++]);
      assertEquals(data.getRationale(), values[index++]);

      assertEquals(data.getModType(), values[index++]);
      assertEquals(data.getTypeUuid(), values[index++]);

      verifyData(data.getVersion(), index, values);
   }

   public static int verifyData(VersionData version, int index, Object... values) {
      assertEquals(version.getBranchId(), values[index++]);
      assertEquals(version.getTransactionId(), values[index++]);
      assertEquals(version.getStripeId(), values[index++]);
      assertEquals(version.getGammaId(), values[index++]);
      return index;
   }

   private static final class IdComparator implements Comparator<OrcsData> {

      @Override
      public int compare(OrcsData arg0, OrcsData arg1) {
         return arg0.getLocalId() - arg1.getLocalId();
      }
   };

}
