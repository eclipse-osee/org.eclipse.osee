/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.data;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Donald G. Dunne
 */
@XmlRootElement
public class OrcsTypeSheet {

   public String name;
   public String typesSheet;
   public String id;
   public String guid;

   public OrcsTypeSheet() {
      // For JAX-RS Instantiation
   }

   public OrcsTypeSheet(String name, String typesSheet) {
      this.name = name;
      this.typesSheet = typesSheet;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getTypesSheet() {
      return typesSheet;
   }

   public void setTypesSheet(String typesSheet) {
      this.typesSheet = typesSheet;
   }

   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public String getGuid() {
      return guid;
   }

   public void setGuid(String guid) {
      this.guid = guid;
   }
}
