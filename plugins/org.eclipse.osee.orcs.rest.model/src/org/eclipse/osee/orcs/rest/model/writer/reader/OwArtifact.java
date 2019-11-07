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
package org.eclipse.osee.orcs.rest.model.writer.reader;

import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.type.Id;

/**
 * Data Transfer object for Orcs Writer
 *
 * @author Donald G. Dunne
 */
public class OwArtifact extends OwBase {

   public OwArtifact() {
      // for jax-rs instantiation
      super(Id.SENTINEL, "");
   }

   public OwArtifact(Long id, String name) {
      super(id, name);
   }

   OwArtifactType type;
   OwApplicability appId;
   List<OwAttribute> attributes;
   List<OwRelation> relations;

   public OwArtifactType getType() {
      return type;
   }

   public void setType(OwArtifactType type) {
      this.type = type;
   }

   public List<OwAttribute> getAttributes() {
      if (attributes == null) {
         attributes = new LinkedList<>();
      }
      return attributes;
   }

   public void setAttributes(List<OwAttribute> attributes) {
      this.attributes = attributes;
   }

   public List<OwRelation> getRelations() {
      if (relations == null) {
         relations = new LinkedList<>();
      }
      return relations;
   }

   public void setRelations(List<OwRelation> relations) {
      this.relations = relations;
   }

   @Override
   public String toString() {
      return "OwArtifact [type=" + type + ", id=" + getId() + ", data=" + data + "]";
   }

   public OwApplicability getAppId() {
      return appId;
   }

   public void setAppId(OwApplicability appId) {
      this.appId = appId;
   }

}
