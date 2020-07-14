/*********************************************************************
 * Copyright (c) 2020 Boeing
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

/**
 * @author Donald G. Dunne
 */
public class JsonRelation {

   private String artA;
   private String artAName;
   private String artB;
   private String artBName;
   private String typeName;
   private String typeId;

   public String getTypeName() {
      return typeName;
   }

   public void setTypeName(String typeName) {
      this.typeName = typeName;
   }

   public String getTypeId() {
      return typeId;
   }

   public void setTypeId(String typeId) {
      this.typeId = typeId;
   }

   public String getArtA() {
      return artA;
   }

   public void setArtA(String artA) {
      this.artA = artA;
   }

   public String getArtB() {
      return artB;
   }

   public void setArtB(String artB) {
      this.artB = artB;
   }

   public String getArtAName() {
      return artAName;
   }

   public void setArtAName(String artAName) {
      this.artAName = artAName;
   }

   public String getArtBName() {
      return artBName;
   }

   public void setArtBName(String artBName) {
      this.artBName = artBName;
   }

}
