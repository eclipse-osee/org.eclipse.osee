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
package org.eclipse.osee.define.report.api;

import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import org.eclipse.osee.framework.core.data.AttributeTypeId;

/**
 * @author David W. Miller
 */
@XmlRootElement
public class WordArtifactChange {
   long artId;
   List<Long> changedAttrTypes = new LinkedList<>();
   boolean changed = false;
   boolean created = false;
   boolean safetyRelated = false;

   public long getArtId() {
      return artId;
   }

   public void setArtId(long artId) {
      this.artId = artId;
   }

   public List<Long> getChangedAttrTypes() {
      return changedAttrTypes;
   }

   public void setChangedAttrTypes(List<Long> changedAttrs) {
      this.changedAttrTypes = changedAttrs;
   }

   public void addChangedAttrType(long attrId) {
      this.changedAttrTypes.add(attrId);
   }

   public void addChangedAttributeType(AttributeTypeId attributeType) {
      this.changedAttrTypes.add(attributeType.getId());
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
