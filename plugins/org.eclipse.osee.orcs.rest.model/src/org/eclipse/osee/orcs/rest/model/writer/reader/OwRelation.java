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

/**
 * Data Transfer object for Orcs Writer
 * 
 * @author Donald G. Dunne
 */
public class OwRelation {

   public OwRelationType type;
   public OwArtifactToken artToken;
   public String data;

   public OwRelationType getType() {
      return type;
   }

   public void setType(OwRelationType type) {
      this.type = type;
   }

   public OwArtifactToken getArtToken() {
      return artToken;
   }

   public void setArtToken(OwArtifactToken artToken) {
      this.artToken = artToken;
   }

   public String getData() {
      return data;
   }

   public void setData(String data) {
      this.data = data;
   }

   @Override
   public String toString() {
      return "OwRelation [type=" + type + ", artToken=" + artToken + ", data=" + data + "]";
   }

}
