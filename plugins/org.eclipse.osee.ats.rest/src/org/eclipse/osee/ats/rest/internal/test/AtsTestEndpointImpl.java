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
import org.eclipse.osee.ats.api.util.IAtsTestEndpoint;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Donald G. Dunne
 */
public class AtsTestEndpointImpl implements IAtsTestEndpoint {
   private final AtsApi atsApi;

   public AtsTestEndpointImpl(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   @Override
   public XResultData testVersions() {
      return (new VersionRelationToggleServerTest(atsApi, new XResultData())).run();
   }

}