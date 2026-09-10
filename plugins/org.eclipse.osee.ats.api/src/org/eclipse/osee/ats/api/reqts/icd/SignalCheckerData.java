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

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * Per-run data object for the signal checker. Holds inputs, per-artifact data (ACDs), aggregate statistics, and the
 * run-wide XResultData log. Must remain Jackson-serializable for client-server transfer.
 *
 * @author Donald G. Dunne
 */
public class SignalCheckerData {

   // --- Inputs ---
   private BranchToken branch = BranchToken.SENTINEL;
   private List<ArtifactId> artsToCheck = new ArrayList<>();
   private String sigDbSystem = "";
   private String atsId = "";
   private boolean recurse = false;

   // --- Results / aggregates ---
   private List<ArtifactSignalData> artSigData = new ArrayList<>();
   private int totalSignalsFound = 0;
   private int totalExpandedSignals = 0;
   private int totalDefinedSignals = 0;
   private int totalUndefinedSignals = 0;
   private int totalWarnings = 0;
   private int totalErrors = 0;

   // --- Logging ---
   private XResultData rd = new XResultData();

   public BranchToken getBranch() {
      return branch;
   }

   public void setBranch(BranchToken branch) {
      this.branch = branch;
   }

   public String getSigDbSystem() {
      return sigDbSystem;
   }

   public void setSigDbSystem(String sigDbSystem) {
      this.sigDbSystem = sigDbSystem;
   }

   public boolean isRecurse() {
      return recurse;
   }

   public void setRecurse(boolean recurse) {
      this.recurse = recurse;
   }

   public int getTotalSignalsFound() {
      return totalSignalsFound;
   }

   public void setTotalSignalsFound(int totalSignalsFound) {
      this.totalSignalsFound = totalSignalsFound;
   }

   public int getTotalExpandedSignals() {
      return totalExpandedSignals;
   }

   public void setTotalExpandedSignals(int totalExpandedSignals) {
      this.totalExpandedSignals = totalExpandedSignals;
   }

   public int getTotalDefinedSignals() {
      return totalDefinedSignals;
   }

   public void setTotalDefinedSignals(int totalDefinedSignals) {
      this.totalDefinedSignals = totalDefinedSignals;
   }

   public int getTotalUndefinedSignals() {
      return totalUndefinedSignals;
   }

   public void setTotalUndefinedSignals(int totalUndefinedSignals) {
      this.totalUndefinedSignals = totalUndefinedSignals;
   }

   public int getTotalWarnings() {
      return totalWarnings;
   }

   public void setTotalWarnings(int totalWarnings) {
      this.totalWarnings = totalWarnings;
   }

   public int getTotalErrors() {
      return totalErrors;
   }

   public void setTotalErrors(int totalErrors) {
      this.totalErrors = totalErrors;
   }

   public XResultData getRd() {
      return rd;
   }

   public void setRd(XResultData rd) {
      this.rd = rd;
   }

   public List<ArtifactId> getArtsToCheck() {
      return artsToCheck;
   }

   public void setArtsToCheck(List<ArtifactId> artsToCheck) {
      this.artsToCheck = artsToCheck;
   }

   public List<ArtifactSignalData> getArtSigData() {
      return artSigData;
   }

   public void setArtSigData(List<ArtifactSignalData> artSigData) {
      this.artSigData = artSigData;
   }

   public String getAtsId() {
      return atsId;
   }

   public void setAtsId(String atsId) {
      this.atsId = atsId;
   }

}
