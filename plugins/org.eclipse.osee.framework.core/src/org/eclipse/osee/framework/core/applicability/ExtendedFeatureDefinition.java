/*********************************************************************
 * Copyright (c) 2021 Boeing
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

import java.util.LinkedList;
import java.util.List;

/**
 * @author Luciano T. Vaglienti
 */
public class ExtendedFeatureDefinition extends FeatureDefinition {
   public static final ExtendedFeatureDefinition SENTINEL = new ExtendedFeatureDefinition();
   private final List<NameValuePair> configurations = new LinkedList<>();

   public ExtendedFeatureDefinition() {
      super();
   }

   public ExtendedFeatureDefinition(Long id, String name, String valueType, List<String> values, String defaultValue, boolean multiValued, String description, List<String> productApplicabilities) {
      super(id, name, valueType, values, defaultValue, multiValued, description, productApplicabilities);
   }

   public ExtendedFeatureDefinition(FeatureDefinition fD) {
      super(fD.getId(), fD.getName(), fD.getValueType(), fD.getValues(), fD.getDefaultValue(), fD.isMultiValued(),
         fD.getDescription(), fD.getProductApplicabilities());
   }

   public List<NameValuePair> getConfigurations() {
      return this.configurations;
   }

   public void addConfiguration(NameValuePair configuration) {
      this.configurations.add(configuration);
   }
}
