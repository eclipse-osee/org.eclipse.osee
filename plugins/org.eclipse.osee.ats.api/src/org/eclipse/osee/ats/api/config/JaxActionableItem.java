/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.config;

import java.util.ArrayList;
import java.util.List;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.ser.std.ToStringSerializer;

/**
 * @author Donald G. Dunne
 */
public class JaxActionableItem extends JaxAtsConfigObject {

   @JsonSerialize(using = ToStringSerializer.class)
   Long parentId;
   @JsonSerialize(using = ToStringSerializer.class)
   Long teamDefId;
   List<Long> children = new ArrayList<>();

   public JaxActionableItem() {
      // for jax-rs
   }

   public Long getParentId() {
      return parentId;
   }

   public void setParentId(Long parentId) {
      this.parentId = parentId;
   }

   public Long getTeamDefId() {
      return teamDefId;
   }

   public void setTeamDefId(Long teamDefId) {
      this.teamDefId = teamDefId;
   }

   public List<Long> getChildren() {
      return children;
   }

   public void setChildren(List<Long> children) {
      this.children = children;
   }

   public void addChild(JaxActionableItem child) {
      children.add(child.getUuid());
   }

}
