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
package org.eclipse.osee.ats.ide.reqts.icd;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.reqts.icd.SignalCheckerData;
import org.eclipse.osee.ats.core.reqts.icd.AbstractAtsIcdService;

/**
 * Client-side implementation of AtsIcdService. Delegates check operations to the server REST endpoint via the JAX-RS
 * proxy. Shared utility methods (like getHtmlReport) are inherited from AbstractAtsIcdService.
 *
 * @author Donald G. Dunne
 */
public class AtsIcdServiceIdeImpl extends AbstractAtsIcdService {

   public AtsIcdServiceIdeImpl(AtsApi atsApi) {
      super(atsApi);
   }

   @Override
   public SignalCheckerData checkSignals(SignalCheckerData scd) {
      return atsApi.getServerEndpoints().getIcdEp().checkSignals(scd);
   }

   @Override
   public SignalCheckerData checkSignalsTest(SignalCheckerData scd) {
      return atsApi.getServerEndpoints().getIcdEp().checkSignalsTest(scd);
   }

}
