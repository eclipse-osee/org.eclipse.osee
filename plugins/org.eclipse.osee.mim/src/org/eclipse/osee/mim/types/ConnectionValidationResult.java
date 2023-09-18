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
package org.eclipse.osee.mim.types;

import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;

/**
 * @author Ryan T. Baldwin
 */
public class ConnectionValidationResult {

   private final BranchId branch;
   private final ArtifactId viewId;
   private final String connectionName;
   private boolean passed = true;
   private List<String> errors = new LinkedList<>();

   public ConnectionValidationResult(BranchId branch, ArtifactId viewId, String connectionName) {
      this.branch = branch;
      this.viewId = viewId;
      this.connectionName = connectionName;
   }

   public String getBranch() {
      return branch.getIdString();
   }

   public ArtifactId getViewId() {
      return viewId;
   }

   public String getConnectionName() {
      return connectionName;
   }

   public boolean isPassed() {
      return passed;
   }

   public void setPassed(boolean passed) {
      this.passed = passed;
   }

   public List<String> getErrors() {
      return errors;
   }

   public void setErrors(List<String> errors) {
      this.errors = errors;
   }

}