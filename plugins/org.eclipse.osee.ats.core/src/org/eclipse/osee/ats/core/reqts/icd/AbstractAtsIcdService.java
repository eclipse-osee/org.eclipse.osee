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
package org.eclipse.osee.ats.core.reqts.icd;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.reqts.icd.AtsIcdService;
import org.eclipse.osee.ats.api.reqts.icd.SignalCheckerData;
import org.eclipse.osee.ats.api.reqts.icd.SignalCheckerHtmlReport;

/**
 * Base implementation of AtsIcdService with shared utility methods. Both client-side and server-side implementations
 * extend this class.
 *
 * @author Donald G. Dunne
 */
public abstract class AbstractAtsIcdService implements AtsIcdService {

   protected final AtsApi atsApi;

   protected AbstractAtsIcdService(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   @Override
   public String getHtmlReport(SignalCheckerData scd) {
      return SignalCheckerHtmlReport.generate(scd);
   }

}
