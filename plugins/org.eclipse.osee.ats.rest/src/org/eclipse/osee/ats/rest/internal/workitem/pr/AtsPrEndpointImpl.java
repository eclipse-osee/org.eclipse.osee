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

package org.eclipse.osee.ats.rest.internal.workitem.pr;

import javax.ws.rs.Path;
import org.eclipse.osee.ats.api.workflow.pr.AtsPrEndpointApi;
import org.eclipse.osee.ats.api.workflow.pr.PrViewData;
import org.eclipse.osee.ats.rest.AtsApiServer;

/**
 * @author Donald G. Dunne
 */
@Path("pr")
public class AtsPrEndpointImpl implements AtsPrEndpointApi {

   private final AtsApiServer atsApi;

   public AtsPrEndpointImpl(AtsApiServer atsApi) {
      this.atsApi = atsApi;
   }

   @Override
   public PrViewData generatePrView(PrViewData prViewData) {
      PrViewData prView = new AmsPrOperations(atsApi).getPrView(prViewData);
      return prView;
   }

}