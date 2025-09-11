/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.ats.rest.internal.test;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.util.AtsTestEndpointApi;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Donald G. Dunne
 */
public class AtsTestEndpointImpl implements AtsTestEndpointApi {
   private final AtsApi atsApi;
   private final OrcsApi orcsApi;

   public AtsTestEndpointImpl(AtsApi atsApi, OrcsApi orcsApi) {
      this.atsApi = atsApi;
      this.orcsApi = orcsApi;
   }

   @Override
   public XResultData testVersions() {
      return (new VersionRelationToggleServerTest(atsApi, new XResultData())).run();
   }

   @Override
   public XResultData testTransactions() {
      return (new TransactionsServerTest(atsApi, orcsApi, new XResultData())).run();
   }

}