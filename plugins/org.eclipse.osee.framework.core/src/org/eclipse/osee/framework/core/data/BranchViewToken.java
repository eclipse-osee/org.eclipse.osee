/*********************************************************************
 * Copyright (c) 2018 Boeing
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;

/**
 * @author Donald G. Dunne
 */
public class BranchViewToken extends NamedIdBase {

   private ArtifactId viewId;

   public BranchViewToken() {
      super(BranchId.SENTINEL.getId(), "");
      // Do Nothing
   }

   public BranchViewToken(BranchId branch, String name, ArtifactId viewId) {
      super(branch.getId(), name);
      this.viewId = viewId;
   }

   public BranchViewToken(Long id, String name, ArtifactId viewId) {
      this(BranchId.valueOf(id), name, viewId);
   }

   public ArtifactId getViewId() {
      return viewId;
   }

   public void setViewId(ArtifactId viewId) {
      this.viewId = viewId;
   }

   @Override
   @JsonIgnore
   public String getIdString() {
      return super.getIdString();
   }
}
