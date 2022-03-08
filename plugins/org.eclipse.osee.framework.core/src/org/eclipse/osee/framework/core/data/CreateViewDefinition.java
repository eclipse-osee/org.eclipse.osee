/*********************************************************************
 * Copyright (c) 2018 Boeing
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

package org.eclipse.osee.framework.core.data;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;

/**
 * @author Donald G. Dunne
 */
public class CreateViewDefinition extends NamedIdBase {

   public Object data;
   public ArtifactId copyFrom = ArtifactId.SENTINEL;
   private List<String> productApplicabilities;
   private List<ArtifactId> configurationGroup;

   public CreateViewDefinition() {
      super(ArtifactId.SENTINEL.getId(), "");
      this.configurationGroup = new ArrayList<>();
   }

   public CreateViewDefinition(Long id, String name, List<String> productApplicabilities, List<ArtifactId> configurationGroups) {
      super(id, name);
      this.productApplicabilities = productApplicabilities;
      this.configurationGroup = configurationGroups;
   }

   public Object getData() {
      return data;
   }

   public void setData(Object data) {
      this.data = data;
   }

   public ArtifactId getCopyFrom() {
      if (copyFrom == null) {
         return ArtifactId.SENTINEL;
      } else {
         return copyFrom;
      }
   }

   public void setCopyFrom(ArtifactId copyFrom) {
      this.copyFrom = copyFrom;
   }

   public List<String> getProductApplicabilities() {
      return productApplicabilities;
   }

   public void setProductApplicabilities(List<String> productApplicabilities) {
      this.productApplicabilities = productApplicabilities;
   }

   public List<ArtifactId> getConfigurationGroup() {
      return configurationGroup;
   }

   public void setConfigurationGroup(List<ArtifactId> configurationGroup) {
      this.configurationGroup = configurationGroup;
   }

}
