/*********************************************************************
 * Copyright (c) 2024 Boeing
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
package org.eclipse.osee.framework.core.applicability;

import java.util.ArrayList;
import java.util.List;

/**
 * Used by {@link ApplicabilityParseSubstituteAndSanitize} Java-Rust FFI
 */
public abstract class BatFile {

   private String name = "";
   private final List<String> features = new ArrayList<>();
   private final List<BatMatchText> substitutions = new ArrayList<>();

   public BatFile() {
      // Default constructor
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public List<String> getFeatures() {
      return new ArrayList<>(features);
   }

   public void addFeatures(List<String> features) {
      this.features.addAll(features);
   }

   public List<BatMatchText> getSubstitutions() {
      return new ArrayList<>(substitutions);
   }

   public void addSubstitutions(List<BatMatchText> substitutions) {
      this.substitutions.addAll(substitutions);
   }
}