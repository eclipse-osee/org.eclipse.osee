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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.eclipse.osee.accessor.types.ArtifactAccessorResultWithGammas;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;

/**
 * @author Ryan T. Baldwin
 */
public class ConnectionValidationResult {

   private final BranchId branch;
   private final ArtifactId viewId;
   private final String connectionName;
   private final Map<ArtifactId, String> structureByteAlignmentErrors = new HashMap<>();
   private final Map<ArtifactId, String> structureWordAlignmentErrors = new HashMap<>();
   private final Map<ArtifactId, String> duplicateStructureNameErrors = new HashMap<>();
   private final Map<ArtifactId, String> messageTypeErrors = new HashMap<>();
   private final List<ArtifactAccessorResultWithGammas> affectedConfigurations = new ArrayList<>();

   public ConnectionValidationResult() {
      this.branch = BranchId.SENTINEL;
      this.viewId = ArtifactId.SENTINEL;
      this.connectionName = "";
   }

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
      return structureByteAlignmentErrors.isEmpty() && structureWordAlignmentErrors.isEmpty() && duplicateStructureNameErrors.isEmpty() && messageTypeErrors.isEmpty();
   }

   public Map<ArtifactId, String> getStructureByteAlignmentErrors() {
      return structureByteAlignmentErrors;
   }

   public Map<ArtifactId, String> getDuplicateStructureNameErrors() {
      return duplicateStructureNameErrors;
   }

   public Map<ArtifactId, String> getMessageTypeErrors() {
      return messageTypeErrors;
   }

   public Map<ArtifactId, String> getStructureWordAlignmentErrors() {
      return structureWordAlignmentErrors;
   }

   public void addAllAffectedConfigurations(List<ArtifactAccessorResultWithGammas> configurations) {
      this.affectedConfigurations.addAll(configurations);
   }

   public List<ArtifactAccessorResultWithGammas> getAffectedConfigurations() {
      return this.affectedConfigurations.stream().collect(Collectors.toList());
   }

}