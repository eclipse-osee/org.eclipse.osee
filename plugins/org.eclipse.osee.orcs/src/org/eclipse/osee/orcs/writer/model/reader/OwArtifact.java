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
package org.eclipse.osee.orcs.writer.model.reader;

import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Donald G. Dunne
 */
@XmlRootElement
public class OwArtifact extends OwBase {

   OwArtifactType type;
   String name;
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
      return "OwArtifact [type=" + type + ", uuid=" + uuid + ", data=" + data + "]";
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

}
