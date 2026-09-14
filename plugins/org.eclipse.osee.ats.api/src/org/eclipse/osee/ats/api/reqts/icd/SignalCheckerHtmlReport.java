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

import org.eclipse.osee.framework.jdk.core.util.AHTML;

/**
 * Generates an HTML report from a completed SignalCheckerData for display in the IDE ResultsEditor.
 *
 * @author Donald G. Dunne
 */
public final class SignalCheckerHtmlReport {

   private SignalCheckerHtmlReport() {
      // utility class
   }

   public static String generate(SignalCheckerData scd) {
      StringBuilder sb = new StringBuilder();

      sb.append(AHTML.heading(3, "Signal Checker Results"));

      // Summary table
      sb.append(AHTML.beginMultiColumnTable(95, 1));
      sb.append(AHTML.addHeaderRowMultiColumnTable(new String[] {"Metric", "Value"}));
      sb.append(row("Branch", scd.getBranch().getName()));
      sb.append(row("SigDb System", scd.getSigDbSystem()));
      sb.append(row("Recurse", String.valueOf(scd.isRecurse())));
      sb.append(row("Artifacts Processed", String.valueOf(scd.getArtSigData().size())));
      sb.append(row("Signals Found", String.valueOf(scd.getTotalSignalsFound())));
      sb.append(row("Expanded Signals", String.valueOf(scd.getTotalExpandedSignals())));
      sb.append(row("Defined in SigDb", String.valueOf(scd.getTotalDefinedSignals())));
      sb.append(row("Undefined in SigDb", coloredValue(scd.getTotalUndefinedSignals(), "red")));
      sb.append(row("Errors", coloredValue(scd.getTotalErrors(), "red")));
      sb.append(row("Warnings", coloredValue(scd.getTotalWarnings(), "orange")));
      sb.append(AHTML.endMultiColumnTable());

      sb.append("<br/>");

      // Per-artifact details
      for (ArtifactSignalData acd : scd.getArtSigData()) {
         sb.append(AHTML.heading(4,
            "Artifact: " + acd.getArtifact().getName() + " [" + acd.getArtifact().getIdString() + "]"));

         if (acd.isFailed()) {
            sb.append(AHTML.textToHtml("FAILED: " + acd.getFailureReason()));
            continue;
         }

         if (acd.getSignals().isEmpty()) {
            sb.append(AHTML.textToHtml("No signals found in this artifact."));
            continue;
         }

         sb.append(AHTML.beginMultiColumnTable(95, 1));
         sb.append(AHTML.addHeaderRowMultiColumnTable(
            new String[] {"Line", "Raw Signal", "Expandable", "Expanded Count", "Status", "Details"}));

         for (SignalData sd : acd.getSignals()) {
            String status = sd.getSeverity().isEmpty() ? "-" : sd.getSeverity();
            String statusCell = "ERROR".equals(sd.getSeverity()) ? AHTML.color("red", status) : //
               "OK".equals(sd.getSeverity()) ? AHTML.color("green", status) : status;

            String details = sd.getValidationMessage();
            if (!sd.getExpandedSignals().isEmpty() && sd.getExpandedSignals().size() <= 10) {
               details += details.isEmpty() ? "" : "<br/>";
               details += "<small>" + String.join(", ", sd.getExpandedSignals()) + "</small>";
            }

            sb.append(AHTML.addRowMultiColumnTable(new String[] { //
               String.valueOf(sd.getLineNumber()), //
               AHTML.bold(sd.getRawSignal()), //
               sd.isExpandable() ? "Yes" : "No", //
               sd.isExpandable() ? String.valueOf(sd.getExpandedSignals().size()) : "-", //
               statusCell, //
               details //
            }));
         }
         sb.append(AHTML.endMultiColumnTable());
         sb.append("<br/>");
      }

      return AHTML.simplePage(sb.toString());
   }

   private static String row(String label, String value) {
      return AHTML.addRowMultiColumnTable(new String[] {AHTML.bold(label), value});
   }

   private static String coloredValue(int value, String color) {
      return value > 0 ? AHTML.color(color, String.valueOf(value)) : "0";
   }

}
