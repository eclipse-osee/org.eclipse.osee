/*********************************************************************
 * Copyright (c) 2025 Boeing
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

package org.eclipse.osee.ats.rest.internal.demo.servertest;

import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * Test server QueryBuilder against demo populated data
 *
 * @author Donald G. Dunne
 */
public class AtsDbTestQueryOp {

   private static final String ATSID_TW7 = "TW7";
   private final AtsApi atsApi;
   private final XResultData rd;
   private final OrcsApi orcsApi;

   public AtsDbTestQueryOp(XResultData rd, AtsApi atsApi, OrcsApi orcsApi) {
      this.rd = rd;
      this.atsApi = atsApi;
      this.orcsApi = orcsApi;
   }

   public XResultData run() {

      rd.log(getClass().getSimpleName() + " - Start");

      testAttrValueExactMatch();

      rd.log(getClass().getSimpleName() + " - End");
      return rd;
   }

   private void testAttrValueExactMatch() {
      List<ArtifactReadable> asArtifacts =
         orcsApi.getQueryFactory().fromBranch(atsApi.getAtsBranch()).and(AtsAttributeTypes.AtsId,
            Arrays.asList(ATSID_TW7), QueryOption.EXACT_MATCH_OPTIONS).asArtifacts();
      rd.assertEquals("testAttrValueExactMatch", 1, asArtifacts.size());
   }

}
