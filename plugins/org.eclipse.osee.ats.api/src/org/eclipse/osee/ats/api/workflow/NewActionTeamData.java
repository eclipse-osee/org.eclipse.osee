/*********************************************************************
 * Copyright (c) 2025 Boeing
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

package org.eclipse.osee.ats.api.workflow;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * @author Donald G. Dunne
 */
public class NewActionTeamData {

   public ArtifactId teamId = ArtifactId.SENTINEL;
   public AttributeTypeToken attrType = AttributeTypeToken.SENTINEL;
   public List<Object> values = new ArrayList<>();

   public NewActionTeamData() {
      // for jax-rs
   }

   public NewActionTeamData(ArtifactId teamId, AttributeTypeToken attrType, List<Object> values) {
      if (teamId != null) {
         this.teamId = teamId;
      }
      Conditions.assertNotNull(attrType, "Attr Type must be specified");
      if (attrType != null) {
         this.attrType = attrType;
      }
      Conditions.assertNotNullOrEmpty(values, "Values must be specified");
      this.values = values;
   }

   public ArtifactId getTeamId() {
      return teamId;
   }

   public void setTeamId(ArtifactId teamId) {
      this.teamId = teamId;
   }

   public AttributeTypeToken getAttrType() {
      return attrType;
   }

   public void setAttrType(AttributeTypeToken attrType) {
      this.attrType = attrType;
   }

   public List<Object> getValues() {
      return values;
   }

   public void setValues(List<Object> values) {
      this.values = values;
   }

}
