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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.reqts.icd.ArtifactSignalData;
import org.eclipse.osee.ats.api.reqts.icd.SignalCheckerData;
import org.eclipse.osee.ats.api.reqts.icd.SignalData;
import org.eclipse.osee.ats.api.reqts.icd.SignalExpander;
import org.eclipse.osee.ats.core.reqts.icd.AbstractAtsIcdService;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * Server-side implementation of AtsIcdService. Runs the signal checker phases and returns the populated SCD.
 *
 * @author Donald G. Dunne
 */
public class AtsIcdServiceServerImpl extends AbstractAtsIcdService {

   public AtsIcdServiceServerImpl(AtsApi atsApi) {
      super(atsApi);
   }

   @Override
   public SignalCheckerData checkSignals(SignalCheckerData scd) {
      // TBD: Full pipeline implementation (phases 2-6) will be wired here
      // For now, log that the real operation was called
      scd.getRd().log("CheckSignals: real pipeline not yet implemented");
      return scd;
   }

   @Override
   public SignalCheckerData checkSignalsTest(SignalCheckerData scd) {
      XResultData rd = scd.getRd();
      rd.log("CheckSignalsTest: Starting test round-trip");
      rd.log(
         "CheckSignalsTest: branch=" + scd.getBranch() + ", sigDbSystem=" + scd.getSigDbSystem() + ", recurse=" + scd.isRecurse());

      // --- Phase 2 simulation: Loading artifacts ---
      ArtifactSignalData acd1 = new ArtifactSignalData();
      acd1.setArtifact(ArtifactToken.valueOf(1001, "SRS-COMM-Requirements"));
      acd1.setTextData("The system shall output [COMM_(PLT|CPG)_PAGE] upon pilot request.\n" + //
         "Signal [EMITTERS.E(01..05)_MODE] indicates emitter operational mode.\n" + //
         "Display [HUD_(F|B|L|R)_QUAD] shall update at 20Hz.\n" + //
         "Reference signal [NAV.GPS_STATUS] for navigation health.\n" + //
         "Invalid signal [BAD SIGNAL!@#] should produce an error.\n");
      rd.log("Loading: artifact 1/2 - 1001 - SRS-COMM-Requirements");

      ArtifactSignalData acd2 = new ArtifactSignalData();
      acd2.setArtifact(ArtifactToken.valueOf(1002, "SRS-NAV-Requirements"));
      acd2.setTextData("The FMS shall process [NAV.WPT_LAT] and [NAV.WPT_LON] for waypoint data.\n" + //
         "Altitude source [BARO_(PRI|SEC)_ALT] provides redundancy.\n");
      rd.log("Loading: artifact 2/2 - 1002 - SRS-NAV-Requirements");

      scd.getArtSigData().add(acd1);
      scd.getArtSigData().add(acd2);

      // --- Phase 4 simulation: FindSignals ---
      addSignal(acd1, "COMM_(PLT|CPG)_PAGE", 1, rd);
      addSignal(acd1, "EMITTERS.E(01..05)_MODE", 2, rd);
      addSignal(acd1, "HUD_(F|B|L|R)_QUAD", 3, rd);
      addSignal(acd1, "NAV.GPS_STATUS", 4, rd);
      addSignal(acd1, "BAD SIGNAL!@#", 5, rd);

      addSignal(acd2, "NAV.WPT_LAT", 1, rd);
      addSignal(acd2, "NAV.WPT_LON", 1, rd);
      addSignal(acd2, "BARO_(PRI|SEC)_ALT", 2, rd);

      // --- Phase 5 simulation: ExpandSignals ---
      int totalExpanded = 0;
      for (ArtifactSignalData acd : scd.getArtSigData()) {
         for (SignalData sd : acd.getSignals()) {
            List<String> errors = new ArrayList<>();
            sd.setExpandable(SignalExpander.isExpandable(sd.getRawSignal()));

            if (sd.isExpandable()) {
               List<String> expanded = SignalExpander.expand(sd.getRawSignal(), errors);
               if (!errors.isEmpty()) {
                  sd.setSeverity("ERROR");
                  sd.setValidationMessage("Expansion failed: " + String.join("; ", errors));
                  rd.errorf("ExpandSignals: '%s' -> ERROR: %s", sd.getRawSignal(), sd.getValidationMessage());
                  scd.setTotalErrors(scd.getTotalErrors() + 1);
               } else {
                  sd.setExpandedSignals(expanded);
                  // Validate expanded names
                  for (String name : expanded) {
                     if (!SignalExpander.isValidName(name)) {
                        sd.getInvalidNames().add(name);
                     }
                  }
                  rd.logf("ExpandSignals: '%s' -> %d expanded signals", sd.getRawSignal(), expanded.size());
                  totalExpanded += expanded.size();
               }
            } else {
               // Check if the non-expandable name is valid
               if (!SignalExpander.isValidName(sd.getRawSignal())) {
                  sd.getInvalidNames().add(sd.getRawSignal());
                  sd.setSeverity("ERROR");
                  sd.setValidationMessage("Invalid characters in signal name: " + sd.getRawSignal());
                  rd.errorf("ExpandSignals: '%s' -> invalid characters", sd.getRawSignal());
                  scd.setTotalErrors(scd.getTotalErrors() + 1);
               }
            }
         }
      }
      scd.setTotalExpandedSignals(totalExpanded);

      // --- Phase 6 simulation: CheckSigDb (dummy - mark some as found, some as missing) ---
      // Simulate: all expanded signals found except EMITTERS.E03_MODE and EMITTERS.E05_MODE
      List<String> simulatedMissing = Arrays.asList("EMITTERS.E03_MODE", "EMITTERS.E05_MODE");
      int totalDefined = 0;
      int totalUndefined = 0;

      for (ArtifactSignalData acd : scd.getArtSigData()) {
         for (SignalData sd : acd.getSignals()) {
            if (!sd.getInvalidNames().isEmpty() || "ERROR".equals(sd.getSeverity())) {
               continue; // skip already-errored signals
            }

            List<String> namesToCheck =
               sd.getExpandedSignals().isEmpty() ? Arrays.asList(sd.getRawSignal()) : sd.getExpandedSignals();
            int found = 0;
            int missing = 0;
            List<String> missingList = new ArrayList<>();

            for (String name : namesToCheck) {
               if (simulatedMissing.contains(name)) {
                  missing++;
                  missingList.add(name);
               } else {
                  found++;
               }
            }

            sd.setFoundCount(found);
            sd.setMissingCount(missing);
            sd.setMissingSignals(missingList);
            sd.setDefinedInSigDb(missing == 0);
            totalDefined += found;
            totalUndefined += missing;

            if (missing == 0) {
               sd.setSeverity("OK");
               sd.setValidationMessage(
                  "All " + namesToCheck.size() + " signal(s) found in SigDbSystem=" + scd.getSigDbSystem());
               rd.logf("CheckSigDb: '%s' (line %d) -> FOUND (%d signals)", sd.getRawSignal(), sd.getLineNumber(),
                  found);
            } else {
               sd.setSeverity("ERROR");
               sd.setValidationMessage(
                  "Missing " + missing + "/" + namesToCheck.size() + " signals: " + String.join(", ", missingList));
               rd.errorf("CheckSigDb: '%s' (line %d) -> %d found, %d missing: %s", sd.getRawSignal(),
                  sd.getLineNumber(), found, missing, String.join(", ", missingList));
               scd.setTotalErrors(scd.getTotalErrors() + 1);
            }
         }
      }

      scd.setTotalDefinedSignals(totalDefined);
      scd.setTotalUndefinedSignals(totalUndefined);
      scd.setTotalSignalsFound(scd.getArtSigData().stream().mapToInt(a -> a.getSignals().size()).sum());

      rd.log("CheckSignalsTest: Complete");
      rd.logf("  Artifacts: %d", scd.getArtSigData().size());
      rd.logf("  Signals found: %d", scd.getTotalSignalsFound());
      rd.logf("  Expanded signals: %d", scd.getTotalExpandedSignals());
      rd.logf("  Defined in SigDb: %d", scd.getTotalDefinedSignals());
      rd.logf("  Undefined in SigDb: %d", scd.getTotalUndefinedSignals());
      rd.logf("  Errors: %d", scd.getTotalErrors());

      return scd;
   }

   private void addSignal(ArtifactSignalData acd, String rawSignal, int lineNumber, XResultData rd) {
      SignalData sd = new SignalData();
      sd.setRawSignal(rawSignal);
      sd.setLineNumber(lineNumber);
      acd.getSignals().add(sd);
      rd.logf("FindSignals: '%s' on line %d in artifact %s (%s)", rawSignal, lineNumber,
         acd.getArtifact().getIdString(), acd.getArtifact().getName());
   }

}
