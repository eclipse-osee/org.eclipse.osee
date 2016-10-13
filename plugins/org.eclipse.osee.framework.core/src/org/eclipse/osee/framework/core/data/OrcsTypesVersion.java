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

import java.util.LinkedList;
import java.util.List;

/**
 * @author Donald G. Dunne
 */
public class OrcsTypesVersion {

   private int versionNum;
   private List<OrcsTypesSheet> sheets = new LinkedList<>();

   public int getVersionNum() {
      return versionNum;
   }

   public void setVersionNum(int versionNum) {
      this.versionNum = versionNum;
   }

   public List<OrcsTypesSheet> getSheets() {
      return sheets;
   }

   public void setSheets(List<OrcsTypesSheet> sheets) {
      this.sheets = sheets;
   }

   @Override
   public String toString() {
      return "OrcsTypesVersion [verNum=" + versionNum + ", sheets=" + sheets + "]";
   }

}
