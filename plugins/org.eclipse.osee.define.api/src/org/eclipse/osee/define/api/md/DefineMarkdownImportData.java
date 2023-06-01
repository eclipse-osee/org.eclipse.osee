/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.define.api.md;

import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Donald G. Dunne
 */
public class DefineMarkdownImportData {

   private BranchId branch;
   private ArtifactToken parent;
   private XResultData rd = new XResultData();

   public DefineMarkdownImportData() {
      // for jax-rs
   }

   public DefineMarkdownImportData(BranchId branch, ArtifactToken parent) {
      this.branch = branch;
      this.parent = parent;
   }

   public BranchId getBranch() {
      return branch;
   }

   public void setBranch(BranchId branch) {
      this.branch = branch;
   }

   public ArtifactToken getParent() {
      return parent;
   }

   public void setParent(ArtifactToken parent) {
      this.parent = parent;
   }

   public XResultData getRd() {
      return rd;
   }

   public void setRd(XResultData rd) {
      this.rd = rd;
   }

}
