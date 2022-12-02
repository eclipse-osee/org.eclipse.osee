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

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Audrey Denk
 */
public class BranchViewDefinition {
   private String id = Strings.EMPTY_STRING;
   private String name = Strings.EMPTY_STRING;
   private String description = Strings.EMPTY_STRING;
   private List<String> productApplicabilities = new ArrayList<>();
   private boolean hasFeatureApplicabilities = true;

   public BranchViewDefinition() {
      //Empty constructor is required for json compatibility
   }

   public BranchViewDefinition(String id, String name, String desc, List<String> productApplicabilities, boolean hasFeatureApplicabilities) {
      this.setId(id);
      this.setName(name);
      this.setDescription(desc);
      this.setProductApplicabilities(productApplicabilities);
      this.setHasFeatureApplicabilities(hasFeatureApplicabilities);
   }

   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String desc) {
      this.description = desc;
   }

   public boolean isHasFeatureApplicabilities() {
      return hasFeatureApplicabilities;
   }

   public void setHasFeatureApplicabilities(boolean hasFeatureApplicabilities) {
      this.hasFeatureApplicabilities = hasFeatureApplicabilities;
   }

   public List<String> getProductApplicabilities() {
      return productApplicabilities;
   }

   public void setProductApplicabilities(List<String> productApplicabilities) {
      this.productApplicabilities = productApplicabilities;
   }

}
