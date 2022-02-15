/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.disposition.model;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Angel Avila
 */

@XmlRootElement(name = "DispoSetDescriptorData")
public class DispoSetDescriptorData {

   private String name;
   private String importPath;
   private String dispoType;

   public String getName() {
      return name;
   }

   public String getImportPath() {
      return importPath;
   }

   public String getDispoType() {
      return dispoType;
   }

   public void setImportPath(String importPath) {
      this.importPath = importPath;
   }

   public void setName(String name) {
      this.name = name;
   }

   public void setDispoType(String dispoType) {
      this.dispoType = dispoType;
   }

}