/*********************************************************************
 * Copyright (c) 2022 Boeing
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
package org.eclipse.osee.framework.core.applicability;

import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;

public class ProductTypeDefinition extends NamedIdBase {

   private String description;

   public ProductTypeDefinition(Long id, String name) {
      super(id, name);
   }

   public ProductTypeDefinition(int id, String name) {
      super(id, name);
   }

   public ProductTypeDefinition(String name, String description) {
      this(-1L, name, description);
   }

   public ProductTypeDefinition(Long id, String name, String description) {
      super(id, name);
      this.setDescription(description);
   }

   public ProductTypeDefinition(int id, String name, String description) {
      super(id, name);
      this.setDescription(description);
   }

   public ProductTypeDefinition() {
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

}
