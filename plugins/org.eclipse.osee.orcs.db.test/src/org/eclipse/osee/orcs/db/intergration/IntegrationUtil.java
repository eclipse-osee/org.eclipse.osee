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
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.TransactionId;
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

   public static TestRule integrationRule(Object testObject) {
      return RuleChain.outerRule(new OseeDatabase("orcs.jdbc.service")).around(new OsgiRule(testObject));
   }

   public static void sort(List<? extends OrcsData> data) {
      Collections.sort(data, SORT_BY_LOCAL_ID);
   }

   public static void verifyData(ArtifactData data, Object... values) {
      int index = 0;
      assertEquals(values[index++], data);
      assertEquals(values[index++], data.getGuid());

      verifyData(data, index, values);
   }

   public static void verifyData(AttributeData data, Object... values)  {
      int index = 0;
      assertEquals(values[index++], data);
      assertEquals(values[index++], ArtifactId.valueOf(data.getArtifactId()));

      index = verifyData(data, index, values);

      assertEquals(values[index++], data.getDataProxy().getRawValue());
      assertEquals(values[index++], data.getDataProxy().getUri());
   }

   public static void verifyData(RelationData data, Object... values) {
      int index = 0;
      assertEquals(values[index++], data.getLocalId());

      assertEquals(values[index++], data.getArtifactIdA());
      assertEquals(values[index++], data.getArtifactIdB());
      assertEquals(values[index++], data.getRationale());

      verifyData(data, index, values);
   }

   private static int verifyData(OrcsData orcsData, int index, Object... values) {
      assertEquals(values[index++], orcsData.getModType());
      assertEquals(values[index++], orcsData.getTypeUuid());

      VersionData version = orcsData.getVersion();
      assertEquals(values[index++], version.getBranch());
      assertEquals(values[index++], version.getTransactionId());
      assertEquals(TransactionId.SENTINEL, version.getStripeId());
      assertEquals(values[index++], version.getGammaId());
      return index;
   }

   private static final class IdComparator implements Comparator<OrcsData> {

      @Override
      public int compare(OrcsData arg0, OrcsData arg1) {
         return arg0.getLocalId() - arg1.getLocalId();
      }
   };
}
