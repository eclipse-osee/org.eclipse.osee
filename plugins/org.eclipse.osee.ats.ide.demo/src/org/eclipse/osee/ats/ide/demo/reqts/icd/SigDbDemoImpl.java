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
package org.eclipse.osee.ats.ide.demo.reqts.icd;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.ats.api.reqts.icd.SigDbApi;
import org.eclipse.osee.ats.api.reqts.icd.SignalDefinition;

/**
 * Demo implementation of SigDbApi. Provides an in-memory signal database for testing the signal checker pipeline. This
 * serves as a reference implementation showing how other programs would implement their own SigDbApi to connect to
 * their particular signal database (Oracle, Postgres, file-based, etc.).
 *
 * @author Donald G. Dunne
 */
public class SigDbDemoImpl implements SigDbApi {

   private final Map<String, SignalDefinition> signals = new HashMap<>();
   private final Set<String> variantIds = new HashSet<>();

   public SigDbDemoImpl() {
      variantIds.add("DEMO_V1");
   }

   private void populateDemoSignals() {
      if (signals.isEmpty()) {
         // Communication signals
         addSignal("COMM_PLT_PAGE", "type", "discrete");
         addSignal("COMM_CPG_PAGE", "type", "discrete");

         // Emitter signals (01-05, but 03 and 05 intentionally missing for test)
         addSignal("EMITTERS.E01_MODE", "type", "enum", "range", "0-3");
         addSignal("EMITTERS.E02_MODE", "type", "enum", "range", "0-3");
         // EMITTERS.E03_MODE intentionally missing
         addSignal("EMITTERS.E04_MODE", "type", "enum", "range", "0-3");
         // EMITTERS.E05_MODE intentionally missing

         // HUD quadrant signals
         addSignal("HUD_F_QUAD", "type", "discrete");
         addSignal("HUD_B_QUAD", "type", "discrete");
         addSignal("HUD_L_QUAD", "type", "discrete");
         addSignal("HUD_R_QUAD", "type", "discrete");

         // Navigation signals
         addSignal("NAV.GPS_STATUS", "type", "enum", "range", "0-5");
         addSignal("NAV.WPT_LAT", "type", "float", "units", "degrees");
         addSignal("NAV.WPT_LON", "type", "float", "units", "degrees");

         // Barometric altitude signals
         addSignal("BARO_PRI_ALT", "type", "float", "units", "feet");
         addSignal("BARO_SEC_ALT", "type", "float", "units", "feet");
      }
   }

   private void addSignal(String name, String... keyValuePairs) {
      SignalDefinition def = new SignalDefinition();
      def.setName(name);
      for (int i = 0; i < keyValuePairs.length - 1; i += 2) {
         def.getAttributes().put(keyValuePairs[i], keyValuePairs[i + 1]);
      }
      signals.put(name, def);
   }

   @Override
   public boolean isSignalDefined(String signalName, String variantId) {
      populateDemoSignals();
      return signals.containsKey(signalName);
   }

   @Override
   public SignalDefinition readSignal(String signalName, String variantId) {
      return signals.get(signalName);
   }

   @Override
   public Set<String> listVariantIds() {
      return variantIds;
   }

}
