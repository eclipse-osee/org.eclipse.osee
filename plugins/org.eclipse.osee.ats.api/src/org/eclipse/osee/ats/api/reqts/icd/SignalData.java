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

/**
 * Per-signal data object. Represents a single signal expression found in artifact text, its expansions, and validation
 * results.
 *
 * @author Donald G. Dunne
 */
public class SignalData {

   private String rawSignal = "";
   private int lineNumber = -1;
   private boolean isExpandable = false;
   private List<String> expandedSignals = new ArrayList<>();
   private List<String> invalidNames = new ArrayList<>();
   private boolean isDefinedInSigDb = false;
   private int foundCount = 0;
   private int missingCount = 0;
   private List<String> missingSignals = new ArrayList<>();
   private String severity = "";
   private String validationMessage = "";
   private SignalDefinition definition;

   public String getRawSignal() {
      return rawSignal;
   }

   public void setRawSignal(String rawSignal) {
      this.rawSignal = rawSignal;
   }

   public int getLineNumber() {
      return lineNumber;
   }

   public void setLineNumber(int lineNumber) {
      this.lineNumber = lineNumber;
   }

   public boolean isExpandable() {
      return isExpandable;
   }

   public void setExpandable(boolean expandable) {
      this.isExpandable = expandable;
   }

   public List<String> getExpandedSignals() {
      return expandedSignals;
   }

   public void setExpandedSignals(List<String> expandedSignals) {
      this.expandedSignals = expandedSignals;
   }

   public List<String> getInvalidNames() {
      return invalidNames;
   }

   public void setInvalidNames(List<String> invalidNames) {
      this.invalidNames = invalidNames;
   }

   public boolean isDefinedInSigDb() {
      return isDefinedInSigDb;
   }

   public void setDefinedInSigDb(boolean definedInSigDb) {
      this.isDefinedInSigDb = definedInSigDb;
   }

   public int getFoundCount() {
      return foundCount;
   }

   public void setFoundCount(int foundCount) {
      this.foundCount = foundCount;
   }

   public int getMissingCount() {
      return missingCount;
   }

   public void setMissingCount(int missingCount) {
      this.missingCount = missingCount;
   }

   public List<String> getMissingSignals() {
      return missingSignals;
   }

   public void setMissingSignals(List<String> missingSignals) {
      this.missingSignals = missingSignals;
   }

   public String getSeverity() {
      return severity;
   }

   public void setSeverity(String severity) {
      this.severity = severity;
   }

   public String getValidationMessage() {
      return validationMessage;
   }

   public void setValidationMessage(String validationMessage) {
      this.validationMessage = validationMessage;
   }

   public SignalDefinition getDefinition() {
      return definition;
   }

   public void setDefinition(SignalDefinition definition) {
      this.definition = definition;
   }

}
