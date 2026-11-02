/*********************************************************************
 * Copyright (c) 2026 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 **********************************************************************/
package org.eclipse.osee.ats.api.workdef.model;

public class WorkDefArtifactTypeContext {
   private long workDefId;
   private long artifactTypeId;
   private long workflowCount;

   public WorkDefArtifactTypeContext() {
      // For JSON
   }

   public WorkDefArtifactTypeContext(long workDefId, long artifactTypeId, long workflowCount) {
      this.workDefId = workDefId;
      this.artifactTypeId = artifactTypeId;
      this.workflowCount = workflowCount;
   }

   public long getWorkDefId() {
      return workDefId;
   }

   public long getArtifactTypeId() {
      return artifactTypeId;
   }

   public long getWorkflowCount() {
      return workflowCount;
   }
}
