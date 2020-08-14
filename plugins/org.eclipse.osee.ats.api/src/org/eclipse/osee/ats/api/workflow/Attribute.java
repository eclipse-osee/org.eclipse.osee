/*********************************************************************
 * Copyright (c) 2017 Boeing
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

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.IAttribute;

/**
 * @author Donald G. Dunne
 */
public class Attribute {

   private ArtifactId artId = ArtifactId.SENTINEL;
   private AttributeTypeId attrTypeId = AttributeTypeId.SENTINEL;
   private Map<AttributeId, String> values = new HashMap<>();

   public Attribute() {
   }

   public ArtifactId getArtId() {
      return artId;
   }

   public void setArtId(ArtifactId artId) {
      this.artId = artId;
   }

   public AttributeTypeId getAttrTypeId() {
      return attrTypeId;
   }

   public void setAttrTypeId(AttributeTypeId attrTypeId) {
      this.attrTypeId = attrTypeId;
   }

   public void addAttribute(IAttribute<?> attr) {
      values.put(AttributeId.valueOf(attr.getId()), attr.getDisplayableString());
   }

   public Map<AttributeId, String> getValues() {
      return values;
   }

   public void setValues(Map<AttributeId, String> values) {
      this.values = values;
   }
}