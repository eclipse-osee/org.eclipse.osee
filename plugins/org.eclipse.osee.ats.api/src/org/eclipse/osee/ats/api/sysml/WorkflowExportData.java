/*********************************************************************
 * Copyright (c) 2026 Boeing
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

package org.eclipse.osee.ats.api.sysml;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * Request/result object for the ATS Program to SysML V2 export. Carries the query options (program is required; team
 * definition, artifact type and version are optional filters for the workflow export) and, on return, the generated
 * config and workflow SysML text along with an {@link XResultData} for result reporting.
 *
 * @author Donald G. Dunne
 */
public class WorkflowExportData {

   ArtifactId programId = ArtifactId.SENTINEL;
   ArtifactId teamDefId = ArtifactId.SENTINEL;
   ArtifactTypeToken artifactType = ArtifactTypeToken.SENTINEL;
   ArtifactId versionId = ArtifactId.SENTINEL;

   String configSysml = "";
   String workflowSysml = "";
   String configJson = "";
   List<String> workflowJson = new ArrayList<>();

   XResultData results = new XResultData();

   public WorkflowExportData() {
      // jax-rs
   }

   public ArtifactId getProgramId() {
      return programId;
   }

   public void setProgramId(ArtifactId programId) {
      this.programId = programId;
   }

   public ArtifactId getTeamDefId() {
      return teamDefId;
   }

   public void setTeamDefId(ArtifactId teamDefId) {
      this.teamDefId = teamDefId;
   }

   public ArtifactTypeToken getArtifactType() {
      return artifactType;
   }

   public void setArtifactType(ArtifactTypeToken artifactType) {
      this.artifactType = artifactType;
   }

   public ArtifactId getVersionId() {
      return versionId;
   }

   public void setVersionId(ArtifactId versionId) {
      this.versionId = versionId;
   }

   public String getConfigSysml() {
      return configSysml;
   }

   public void setConfigSysml(String configSysml) {
      this.configSysml = configSysml;
   }

   public String getWorkflowSysml() {
      return workflowSysml;
   }

   public void setWorkflowSysml(String workflowSysml) {
      this.workflowSysml = workflowSysml;
   }

   public String getConfigJson() {
      return configJson;
   }

   public void setConfigJson(String configJson) {
      this.configJson = configJson;
   }

   public List<String> getWorkflowJson() {
      return workflowJson;
   }

   public void setWorkflowJson(List<String> workflowJson) {
      this.workflowJson = workflowJson;
   }

   public XResultData getResults() {
      return results;
   }

   public void setResults(XResultData results) {
      this.results = results;
   }

   public boolean isTeamDefIdValid() {
      return teamDefId != null && teamDefId.isValid();
   }

   public boolean isArtifactTypeValid() {
      return artifactType != null && artifactType.isValid();
   }

   public boolean isVersionIdValid() {
      return versionId != null && versionId.isValid();
   }
}
