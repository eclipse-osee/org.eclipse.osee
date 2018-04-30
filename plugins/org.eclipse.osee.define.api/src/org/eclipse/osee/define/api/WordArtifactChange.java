/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.api;

import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.core.data.AttributeTypeId;

/**
 * @author David W. Miller
 */
public class WordArtifactChange {
   long artId;
   List<AttributeTypeId> changedAttrTypes = new LinkedList<>();
   boolean changed = false;
   boolean created = false;
   boolean safetyRelated = false;

   public long getArtId() {
      return artId;
   }

   public void setArtId(long artId) {
      this.artId = artId;
   }

   public List<AttributeTypeId> getChangedAttrTypes() {
      return changedAttrTypes;
   }

   public void setChangedAttrTypes(List<AttributeTypeId> changedAttrs) {
      this.changedAttrTypes = changedAttrs;
   }

   public void addChangedAttributeType(AttributeTypeId attributeType) {
      this.changedAttrTypes.add(attributeType);
   }

   public boolean isChanged() {
      return changed;
   }

   public void setChanged(boolean changed) {
      this.changed = changed;
   }

   public boolean isCreated() {
      return created;
   }

   public void setCreated(boolean created) {
      this.created = created;
   }

   public boolean isSafetyRelated() {
      return safetyRelated;
   }

   public void setSafetyRelated(boolean safetyRelated) {
      this.safetyRelated = safetyRelated;
   }
}
