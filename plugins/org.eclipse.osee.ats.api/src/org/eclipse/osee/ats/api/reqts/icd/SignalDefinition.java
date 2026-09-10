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

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a signal definition from the SigDb. Carries the signal name and any associated attributes (ranges, types,
 * message membership, etc.) as provided by the program-specific SigDb implementation.
 *
 * @author Donald G. Dunne
 */
public class SignalDefinition {

   private String name = "";
   private Map<String, String> attributes = new HashMap<>();

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public Map<String, String> getAttributes() {
      return attributes;
   }

   public void setAttributes(Map<String, String> attributes) {
      this.attributes = attributes;
   }

}
