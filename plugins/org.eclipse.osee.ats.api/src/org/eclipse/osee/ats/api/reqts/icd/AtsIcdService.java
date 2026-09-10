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
package org.eclipse.osee.ats.api.reqts.icd;

/**
 * Service interface for ICD signal checking operations. On the client, the implementation delegates to the REST
 * endpoint. On the server, the implementation instantiates the signal checker and runs the phases.
 *
 * @author Donald G. Dunne
 */
public interface AtsIcdService {

   /**
    * Run the full signal check pipeline against the provided SCD. Returns the same SCD populated with results (ACDs,
    * SDs, rd log).
    */
   SignalCheckerData checkSignals(SignalCheckerData scd);

   /**
    * Test round-trip. Server fills the SCD with dummy artifacts, signals (with and without expansions), and logging as
    * if CS was run. Used to validate the full client-server round-trip before real data is wired.
    */
   SignalCheckerData checkSignalsTest(SignalCheckerData scd);

   /**
    * Formats a completed SCD into an HTML report suitable for display in the IDE ResultsEditor.
    */
   String getHtmlReport(SignalCheckerData scd);

}
