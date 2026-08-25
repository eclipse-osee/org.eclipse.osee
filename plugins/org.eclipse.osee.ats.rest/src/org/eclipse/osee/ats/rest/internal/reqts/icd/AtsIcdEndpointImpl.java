/*********************************************************************
 * Copyright (c) 2026 Boeing
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
package org.eclipse.osee.ats.rest.internal.reqts.icd;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.reqts.icd.AtsIcdEndpointApi;
import org.eclipse.osee.ats.api.reqts.icd.SignalCheckerData;

/**
 * Server-side JAX-RS endpoint implementation for ICD signal checking. Delegates to AtsIcdService.
 *
 * @author Donald G. Dunne
 */
public class AtsIcdEndpointImpl implements AtsIcdEndpointApi {

   private final AtsApi atsApi;

   public AtsIcdEndpointImpl(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   @Override
   public SignalCheckerData checkSignals(SignalCheckerData scd) {
      return atsApi.getAtsIcdService().checkSignals(scd);
   }

   @Override
   public SignalCheckerData checkSignalsTest(SignalCheckerData scd) {
      return atsApi.getAtsIcdService().checkSignalsTest(scd);
   }

}
