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
import org.eclipse.osee.framework.core.data.ArtifactToken;

/**
 * Per-artifact data object. Holds artifact identity, raw/text content, and its SignalData instances.
 *
 * @author Donald G. Dunne
 */
public class ArtifactSignalData {

   private ArtifactToken artifact = ArtifactToken.SENTINEL;
   private String rawData = "";
   private String textData = "";
   private boolean failed = false;
   private String failureReason = "";
   private List<SignalData> signals = new ArrayList<>();

   public ArtifactToken getArtifact() {
      return artifact;
   }

   public void setArtifact(ArtifactToken artifact) {
      this.artifact = artifact;
   }

   public String getRawData() {
      return rawData;
   }

   public void setRawData(String rawData) {
      this.rawData = rawData;
   }

   public String getTextData() {
      return textData;
   }

   public void setTextData(String textData) {
      this.textData = textData;
   }

   public boolean isFailed() {
      return failed;
   }

   public void setFailed(boolean failed) {
      this.failed = failed;
   }

   public String getFailureReason() {
      return failureReason;
   }

   public void setFailureReason(String failureReason) {
      this.failureReason = failureReason;
   }

   public List<SignalData> getSignals() {
      return signals;
   }

   public void setSignals(List<SignalData> signals) {
      this.signals = signals;
   }

}
