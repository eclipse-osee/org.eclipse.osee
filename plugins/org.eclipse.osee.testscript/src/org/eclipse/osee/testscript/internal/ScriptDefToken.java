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

package org.eclipse.osee.testscript.internal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.eclipse.osee.accessor.types.ArtifactAccessorResult;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;

/**
 * @author Stephen J. Molaro
 */
public class ScriptDefToken extends ArtifactAccessorResult {

   public static final ScriptDefToken SENTINEL = new ScriptDefToken();

   private String programName;
   private Date executionDate;
   private String executionEnvironment;
   private String machine;
   private String revision;
   private String repositoryType;
   private String team;
   private String lastAuthor;
   private Date lastModified;
   private String modified;
   private String repositoryUrl;
   private String user;
   private String qualification;
   private String property;
   private String notes;
   private boolean safety;
   private boolean scheduled;
   private Date scheduledTime;
   private String scheduledMachine;
   private String executedBy;
   private String witness;
   private String statusBy;
   private Date statusDate;
   private String description;
   private List<ScriptResultToken> scriptResults;

   public ScriptDefToken(ArtifactToken art) {
      this((ArtifactReadable) art);
   }

   public ScriptDefToken(ArtifactReadable art) {
      super(art);
      this.setId(art.getId());
      this.setName(art.getName());
      this.setProgramName(art.getSoleAttributeValue(CoreAttributeTypes.ProgramName, ""));
      this.setExecutionDate(art.getSoleAttributeValue(CoreAttributeTypes.ExecutionDate, new Date()));
      this.setExecutionEnvironment(art.getSoleAttributeAsString(CoreAttributeTypes.ExecutionEnvironment, ""));
      this.setMachineName(art.getSoleAttributeAsString(CoreAttributeTypes.Machine, ""));
      this.setRevision(art.getSoleAttributeAsString(CoreAttributeTypes.Revision, ""));
      this.setRepositoryType(art.getSoleAttributeAsString(CoreAttributeTypes.RepositoryType, ""));
      this.setTeam(art.getSoleAttributeAsString(CoreAttributeTypes.TeamName, ""));
      this.setLastAuthor(art.getSoleAttributeAsString(CoreAttributeTypes.LastAuthor, ""));
      this.setLastModified(art.getSoleAttributeValue(CoreAttributeTypes.LastModifiedDate, new Date()));
      this.setModified(art.getSoleAttributeAsString(CoreAttributeTypes.ModifiedFlag, ""));
      this.setRepositoryUrl(art.getSoleAttributeAsString(CoreAttributeTypes.RepositoryUrl, ""));
      this.setUser(art.getSoleAttributeAsString(CoreAttributeTypes.UserId, ""));
      this.setQualification(art.getSoleAttributeAsString(CoreAttributeTypes.QualificationLevel, ""));
      this.setProperty(art.getSoleAttributeAsString(CoreAttributeTypes.PropertyKey, ""));
      this.setNotes(art.getSoleAttributeAsString(CoreAttributeTypes.Notes, ""));
      this.setSafety(art.getSoleAttributeValue(CoreAttributeTypes.Safety, false));
      this.setScheduled(art.getSoleAttributeValue(CoreAttributeTypes.Scheduled, false));
      this.setScheduledTime(art.getSoleAttributeValue(CoreAttributeTypes.ScheduledTime, new Date()));
      this.setScheduledMachine(art.getSoleAttributeAsString(CoreAttributeTypes.ScheduleMachine, ""));
      this.setExecutedBy(art.getSoleAttributeAsString(CoreAttributeTypes.ExecutedBy, ""));
      this.setWitness(art.getSoleAttributeAsString(CoreAttributeTypes.Witness, ""));
      this.setStatusBy(art.getSoleAttributeAsString(CoreAttributeTypes.StatusBy, ""));
      this.setStatusDate(art.getSoleAttributeValue(CoreAttributeTypes.StatusDate, new Date()));
      this.setDescription(art.getSoleAttributeAsString(CoreAttributeTypes.Description, ""));
      this.setScriptResults(
         art.getRelated(CoreRelationTypes.TestScriptDefToTestScriptResults_TestScriptResults).getList().stream().map(
            a -> new ScriptResultToken(a)).collect(Collectors.toList()));
   }

   public ScriptDefToken(Long id, String name) {
      super(id, name);
      this.setProgramName("");
      this.setExecutionDate(new Date());
      this.setExecutionEnvironment("");
      this.setMachineName("");
      this.setRevision("");
      this.setRepositoryType("");
      this.setTeam("");
      this.setLastAuthor("");
      this.setLastModified(new Date());
      this.setModified("");
      this.setRepositoryUrl("");
      this.setUser("");
      this.setQualification("");
      this.setProperty("");
      this.setNotes("");
      this.setSafety(false);
      this.setScheduled(false);
      this.setScheduledTime(new Date());
      this.setScheduledMachine("");
      this.setExecutedBy("");
      this.setWitness("");
      this.setStatusBy("");
      this.setStatusDate(new Date());
      this.setDescription("");
      this.setScriptResults(new ArrayList<ScriptResultToken>());
   }

   public ScriptDefToken() {
      super();
   }

   /**
    * @return the programName
    */
   public String getProgramName() {
      return programName;
   }

   /**
    * @param programName the programName to set
    */
   public void setProgramName(String programName) {
      this.programName = programName;
   }

   /**
    * @return the executionDate
    */
   public Date getExecutionDate() {
      return executionDate;
   }

   /**
    * @param executionDate the executionDate to set
    */
   public void setExecutionDate(Date executionDate) {
      this.executionDate = executionDate;
   }

   /**
    * @return the executionEnvironment
    */
   public String getExecutionEnvironment() {
      return executionEnvironment;
   }

   /**
    * @param executionEnvironment the executionEnvironment to set
    */
   public void setExecutionEnvironment(String executionEnvironment) {
      this.executionEnvironment = executionEnvironment;
   }

   /**
    * @return the machine
    */
   public String getMachineName() {
      return machine;
   }

   /**
    * @param interfaceMessageWriteAccess the interfaceMessageWriteAccess to set
    */
   public void setMachineName(String machine) {
      this.machine = machine;
   }

   /**
    * @return the revision
    */
   public String getRevision() {
      return revision;
   }

   /**
    * @param revision the revision to set
    */
   public void setRevision(String revision) {
      this.revision = revision;
   }

   /**
    * @return the repositoryType
    */
   public String getRepositoryType() {
      return repositoryType;
   }

   /**
    * @param repositoryType the repositoryType to set
    */
   public void setRepositoryType(String repositoryType) {
      this.repositoryType = repositoryType;
   }

   /**
    * @return the team
    */
   public String getTeam() {
      return team;
   }

   /**
    * @param team the team to set
    */
   public void setTeam(String team) {
      this.team = team;
   }

   /**
    * @return the lastAuthor
    */
   public String getLastAuthor() {
      return lastAuthor;
   }

   /**
    * @param lastAuthor the lastAuthor to set
    */
   public void setLastAuthor(String lastAuthor) {
      this.lastAuthor = lastAuthor;
   }

   /**
    * @return the lastModified
    */
   public Date getLastModified() {
      return lastModified;
   }

   /**
    * @param lastModified the lastModified to set
    */
   public void setLastModified(Date lastModified) {
      this.lastModified = lastModified;
   }

   /**
    * @return the modified
    */
   public String getModified() {
      return modified;
   }

   /**
    * @param modified the modified to set
    */
   public void setModified(String modified) {
      this.modified = modified;
   }

   /**
    * @return the repositoryUrl
    */
   public String setRepositoryUrl() {
      return repositoryUrl;
   }

   /**
    * @param repositoryUrl the repositoryUrl to set
    */
   public void setRepositoryUrl(String repositoryUrl) {
      this.repositoryUrl = repositoryUrl;
   }

   /**
    * @return the user
    */
   public String setUser() {
      return user;
   }

   /**
    * @param repositoryUrl the repositoryUrl to set
    */
   public void setUser(String user) {
      this.user = user;
   }

   /**
    * @return the qualification
    */
   public String getQualification() {
      return qualification;
   }

   /**
    * @param revision the revision to set
    */
   public void setQualification(String qualification) {
      this.qualification = qualification;
   }

   /**
    * @return the property
    */
   public String getProperty() {
      return property;
   }

   /**
    * @param property the property to set
    */
   public void setProperty(String property) {
      this.property = property;
   }

   /**
    * @return the notes
    */
   public String getNotes() {
      return notes;
   }

   /**
    * @param notes the notes to set
    */
   public void setNotes(String notes) {
      this.notes = notes;
   }

   /**
    * @return the safety
    */
   public boolean getSafety() {
      return safety;
   }

   /**
    * @param safety the safety to set
    */
   public void setSafety(boolean safety) {
      this.safety = safety;
   }

   /**
    * @return the scheduled
    */
   public boolean getScheduled() {
      return scheduled;
   }

   /**
    * @param scheduled the scheduled to set
    */
   public void setScheduled(boolean scheduled) {
      this.scheduled = scheduled;
   }

   /**
    * @return the scheduledTime
    */
   public Date getScheduledTime() {
      return scheduledTime;
   }

   /**
    * @param scheduledTime the scheduledTime to set
    */
   public void setScheduledTime(Date scheduledTime) {
      this.scheduledTime = scheduledTime;
   }

   /**
    * @return the scheduledMachine
    */
   public String getScheduledMachine() {
      return scheduledMachine;
   }

   /**
    * @param scheduledMachine the scheduledMachine to set
    */
   public void setScheduledMachine(String scheduledMachine) {
      this.scheduledMachine = scheduledMachine;
   }

   /**
    * @return the executedBy
    */
   public String getExecutedBy() {
      return executedBy;
   }

   /**
    * @param executedBy the executedBy to set
    */
   public void setExecutedBy(String executedBy) {
      this.executedBy = executedBy;
   }

   /**
    * @return the witness
    */
   public String getWitness() {
      return witness;
   }

   /**
    * @param witness the witness to set
    */
   public void setWitness(String witness) {
      this.witness = witness;
   }

   /**
    * @return the statusBy
    */
   public String getStatusBy() {
      return statusBy;
   }

   /**
    * @param statusBy the statusBy to set
    */
   public void setStatusBy(String statusBy) {
      this.statusBy = statusBy;
   }

   /**
    * @return the statusDate
    */
   public Date getStatusDate() {
      return statusDate;
   }

   /**
    * @param statusDate the statusDate to set
    */
   public void setStatusDate(Date statusDate) {
      this.statusDate = statusDate;
   }

   /**
    * @return the description
    */
   public String getDescription() {
      return description;
   }

   /**
    * @param description the description to set
    */
   public void setDescription(String description) {
      this.description = description;
   }

   /**
    * @return the scriptResults
    */
   public List<ScriptResultToken> getScriptResults() {
      return scriptResults;
   }

   /**
    * @param scriptResults the scriptResults to set
    */
   public void setScriptResults(List<ScriptResultToken> scriptResults) {
      this.scriptResults = scriptResults;
   }
}
