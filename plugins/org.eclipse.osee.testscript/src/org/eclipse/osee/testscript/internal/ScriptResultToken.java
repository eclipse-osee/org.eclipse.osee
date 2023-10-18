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

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.eclipse.osee.accessor.types.ArtifactAccessorResult;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.rest.model.transaction.Attribute;
import org.eclipse.osee.orcs.rest.model.transaction.CreateArtifact;

/**
 * @author Stephen J. Molaro
 */
public class ScriptResultToken extends ArtifactAccessorResult {

   public static final ScriptResultToken SENTINEL = new ScriptResultToken();

   private String fileUrl;
   private String setId;
   private String processorId;
   private Date executionDate;
   private String executionEnvironment;
   private String machineName;
   private int passedCount;
   private int failedCount;
   private int interactiveCount;
   private boolean scriptAborted;
   private int elapsedTime;
   private Date startDate;
   private Date endDate;
   private String osArchitecture;
   private String osName;
   private String osVersion;
   private String oseeServerJar;
   private String oseeServer;
   private String oseeVersion;
   private String javaVersion;
   private String result;
   private int scriptHealth;
   private String qualificationLevel;
   private String executedBy;
   private String userId;
   private String userName;
   private String email;
   private int totalTestPoints;
   private List<String> witnesses;
   private List<String> runtimeVersions;
   private List<TestCaseToken> testCases;
   private List<TestPointToken> testPoints;
   private List<AttentionLocationToken> attentionMessages;
   private List<ScriptLogToken> logs;
   private List<VersionInformationToken> versionInformation;
   private List<LoggingSummaryToken> loggingSummaries;

   public ScriptResultToken(ArtifactToken art) {
      this((ArtifactReadable) art);
   }

   public ScriptResultToken(ArtifactReadable art) {
      super(art);
      this.setId(art.getId());
      this.setName(art.getName());
      this.setFileUrl(art.getSoleAttributeAsString(CoreAttributeTypes.ContentUrl, ""));
      this.setSetId(art.getSoleAttributeValue(CoreAttributeTypes.SetId, ""));
      this.setProcessorId(art.getSoleAttributeAsString(CoreAttributeTypes.ProcessorId, ""));
      this.setExecutionDate(art.getSoleAttributeValue(CoreAttributeTypes.ExecutionDate, new Date()));
      this.setExecutionEnvironment(art.getSoleAttributeAsString(CoreAttributeTypes.ExecutionEnvironment, ""));
      this.setMachineName(art.getSoleAttributeAsString(CoreAttributeTypes.MachineName, ""));
      this.setPassedCount(art.getSoleAttributeValue(CoreAttributeTypes.PassedCount, 0));
      this.setFailedCount(art.getSoleAttributeValue(CoreAttributeTypes.FailedCount, 0));
      this.setInteractiveCount(art.getSoleAttributeValue(CoreAttributeTypes.InteractiveCount, 0));
      this.setScriptAborted(art.getSoleAttributeValue(CoreAttributeTypes.ScriptAborted, false));
      this.setElapsedTime(art.getSoleAttributeValue(CoreAttributeTypes.ElapsedTime, 0));
      this.setStartDate(art.getSoleAttributeValue(CoreAttributeTypes.StartDate, new Date()));
      this.setEndDate(art.getSoleAttributeValue(CoreAttributeTypes.EndDate, new Date()));
      this.setOsArchitecture(art.getSoleAttributeAsString(CoreAttributeTypes.OsArchitecture, ""));
      this.setOsName(art.getSoleAttributeAsString(CoreAttributeTypes.OsName, ""));
      this.setOsVersion(art.getSoleAttributeAsString(CoreAttributeTypes.OsVersion, ""));
      this.setOseeServerJar(art.getSoleAttributeAsString(CoreAttributeTypes.OseeServerJarVersion, ""));
      this.setOseeServer(art.getSoleAttributeAsString(CoreAttributeTypes.OseeServerTitle, ""));
      this.setOseeVersion(art.getSoleAttributeAsString(CoreAttributeTypes.OseeVersion, ""));
      this.setJavaVersion(art.getSoleAttributeAsString(CoreAttributeTypes.JavaVersion, ""));
      this.setResult(art.getSoleAttributeAsString(CoreAttributeTypes.Result, ""));
      this.setScriptHealth(art.getSoleAttributeValue(CoreAttributeTypes.ScriptHealth, 0));
      this.setQualificationLevel(art.getSoleAttributeAsString(CoreAttributeTypes.QualificationLevel, ""));
      this.setExecutedBy(art.getSoleAttributeAsString(CoreAttributeTypes.ExecutedBy, ""));
      this.setUserId(art.getSoleAttributeAsString(CoreAttributeTypes.UserId, ""));
      this.setUserName(art.getSoleAttributeAsString(CoreAttributeTypes.UserName, ""));
      this.setEmail(art.getSoleAttributeAsString(CoreAttributeTypes.Email, ""));
      this.setWitnesses(art.getAttributeValues(CoreAttributeTypes.Witness));
      this.setRuntimeVersions(art.getAttributeValues(CoreAttributeTypes.RuntimeVersion));
      this.setTestCases(
         art.getRelated(CoreRelationTypes.TestScriptResultsToTestCase_TestCase).getList().stream().filter(
            a -> !a.getExistingAttributeTypes().isEmpty()).map(a -> new TestCaseToken(a)).collect(Collectors.toList()));
      this.setTestPoints(
         art.getRelated(CoreRelationTypes.TestScriptResultsToTestPoint_TestPoint).getList().stream().filter(
            a -> !a.getExistingAttributeTypes().isEmpty()).map(a -> new TestPointToken(a)).collect(
               Collectors.toList()));
      this.setAttentionMessages(art.getRelated(
         CoreRelationTypes.TestScriptResultsToAttentionMessage_AttentionMessage).getList().stream().filter(
            a -> !a.getExistingAttributeTypes().isEmpty()).map(a -> new AttentionLocationToken(a)).collect(
               Collectors.toList()));
      this.setLogs(art.getRelated(CoreRelationTypes.TestScriptResultsToScriptLog_ScriptLog).getList().stream().filter(
         a -> !a.getExistingAttributeTypes().isEmpty()).map(a -> new ScriptLogToken(a)).collect(Collectors.toList()));
      this.setVersionInformation(art.getRelated(
         CoreRelationTypes.TestScriptResultsToVersionInformation_VersionInformation).getList().stream().filter(
            a -> !a.getExistingAttributeTypes().isEmpty()).map(a -> new VersionInformationToken(a)).collect(
               Collectors.toList()));
      this.setLoggingSummaries(
         art.getRelated(CoreRelationTypes.TestScriptResultsToLoggingSummary_LoggingSummary).getList().stream().filter(
            a -> !a.getExistingAttributeTypes().isEmpty()).map(a -> new LoggingSummaryToken(a)).collect(
               Collectors.toList()));
      this.setTotalTestPoints(this.getTestPoints().size());
   }

   public ScriptResultToken(Long id, String name) {
      super(id, name);
      this.setFileUrl("");
      this.setSetId("");
      this.setProcessorId("");
      this.setExecutionDate(new Date());
      this.setExecutionEnvironment("");
      this.setMachineName("");
      this.setPassedCount(0);
      this.setFailedCount(0);
      this.setInteractiveCount(0);
      this.setScriptAborted(false);
      this.setElapsedTime(0);
      this.setStartDate(new Date());
      this.setEndDate(new Date());
      this.setOsArchitecture("");
      this.setOsName("");
      this.setOsVersion("");
      this.setOseeServerJar("");
      this.setOseeServer("");
      this.setOseeVersion("");
      this.setJavaVersion("");
      this.setResult("");
      this.setScriptHealth(-1);
      this.setQualificationLevel("");
      this.setExecutedBy("");
      this.setUserId("");
      this.setUserName("");
      this.setEmail("");
      this.setTotalTestPoints(0);
      this.setWitnesses(new LinkedList<>());
      this.setRuntimeVersions(new LinkedList<>());
      this.setTestCases(new LinkedList<>());
      this.setTestPoints(new LinkedList<>());
      this.setAttentionMessages(new LinkedList<>());
      this.setLogs(new LinkedList<>());
      this.setVersionInformation(new LinkedList<>());
      this.setLoggingSummaries(new LinkedList<>());
   }

   public ScriptResultToken() {
      super();
   }

   @JsonIgnore
   public String getFileUrl() {
      return fileUrl;
   }

   public void setFileUrl(String fileUrl) {
      this.fileUrl = fileUrl;
   }

   /**
    * @return the setId
    */
   public String getSetId() {
      return setId;
   }

   /**
    * @param set the setId to setId
    */
   public void setSetId(String setId) {
      this.setId = setId;
   }

   /**
    * @return the processorId
    */
   public String getProcessorId() {
      return processorId;
   }

   /**
    * @param set the processorId to processorId
    */
   public void setProcessorId(String processorId) {
      this.processorId = processorId;
   }

   /**
    * @return the runtimeVersion
    */
   @JsonIgnore
   public List<String> getRuntimeVersions() {
      return runtimeVersions;
   }

   /**
    * @param runtimeVersion the runtimeVersion to set
    */
   public void setRuntimeVersions(List<String> runtimeVersions) {
      this.runtimeVersions = runtimeVersions;
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
      return machineName;
   }

   /**
    * @param interfaceMessageWriteAccess the interfaceMessageWriteAccess to set
    */
   public void setMachineName(String machine) {
      this.machineName = machine;
   }

   /**
    * @return the passedCount
    */
   public int getPassedCount() {
      return passedCount;
   }

   /**
    * @param passedCount the passedCount to set
    */
   public void setPassedCount(int passedCount) {
      this.passedCount = passedCount;
   }

   /**
    * @return the failedCount
    */
   public int getFailedCount() {
      return failedCount;
   }

   /**
    * @param failedCount the failedCount to set
    */
   public void setFailedCount(int failedCount) {
      this.failedCount = failedCount;
   }

   /**
    * @return the interactiveCount
    */
   public int getInteractiveCount() {
      return interactiveCount;
   }

   /**
    * @param interactiveCount the interactiveCount to set
    */
   public void setInteractiveCount(int interactiveCount) {
      this.interactiveCount = interactiveCount;
   }

   /**
    * @return the scriptAborted
    */
   public boolean getScriptAborted() {
      return scriptAborted;
   }

   /**
    * @param scriptAborted the scriptAborted to set
    */
   public void setScriptAborted(boolean scriptAborted) {
      this.scriptAborted = scriptAborted;
   }

   /**
    * @return the elapsedTime
    */
   public int getElapsedTime() {
      return elapsedTime;
   }

   /**
    * @param elapsedTime the elapsedTime to set
    */
   public void setElapsedTime(int elapsedTime) {
      this.elapsedTime = elapsedTime;
   }

   /**
    * @return the startDate
    */
   public Date getStartDate() {
      return startDate;
   }

   /**
    * @param startDate the startDate to set
    */
   public void setStartDate(Date startDate) {
      this.startDate = startDate;
   }

   /**
    * @return the endDate
    */
   public Date getEndDate() {
      return endDate;
   }

   /**
    * @param endDate the endDate to set
    */
   public void setEndDate(Date endDate) {
      this.endDate = endDate;
   }

   /**
    * @return the osArchitecture
    */
   public String getOsArchitecture() {
      return osArchitecture;
   }

   /**
    * @param osArchitecture the osArchitecture to set
    */
   public void setOsArchitecture(String osArchitecture) {
      this.osArchitecture = osArchitecture;
   }

   /**
    * @return the osName
    */
   public String getOsName() {
      return osName;
   }

   /**
    * @param osName the osName to set
    */
   public void setOsName(String osName) {
      this.osName = osName;
   }

   /**
    * @return the osVersion
    */
   public String getOsVersion() {
      return osVersion;
   }

   /**
    * @param osVersion the osVersion to set
    */
   public void setOsVersion(String osVersion) {
      this.osVersion = osVersion;
   }

   /**
    * @return the oseeServerJar
    */
   public String getOseeServerJar() {
      return oseeServerJar;
   }

   /**
    * @param oseeServerJar the oseeServerJar to set
    */
   public void setOseeServerJar(String oseeServerJar) {
      this.oseeServerJar = oseeServerJar;
   }

   /**
    * @return the oseeServer
    */
   public String getOseeServer() {
      return oseeServer;
   }

   /**
    * @param oseeServer the oseeServer to set
    */
   public void setOseeServer(String oseeServer) {
      this.oseeServer = oseeServer;
   }

   /**
    * @return the oseeVersion
    */
   public String getOseeVersion() {
      return oseeVersion;
   }

   /**
    * @param oseeVersion the oseeVersion to set
    */
   public void setOseeVersion(String oseeVersion) {
      this.oseeVersion = oseeVersion;
   }

   public String getJavaVersion() {
      return javaVersion;
   }

   public void setJavaVersion(String javaVersion) {
      this.javaVersion = javaVersion;
   }

   /**
    * @return the result
    */
   public String getResult() {
      return result;
   }

   /**
    * @param result the result to set
    */
   public void setResult(String result) {
      this.result = result;
   }

   /**
    * @return the scriptHealth
    */
   public int getScriptHealth() {
      return scriptHealth;
   }

   /**
    * @param scriptHealth the scriptHealth to set
    */
   public void setScriptHealth(int scriptHealth) {
      this.scriptHealth = scriptHealth;
   }

   /**
    * @return the qualification
    */
   public String getQualificationLevel() {
      return qualificationLevel;
   }

   /**
    * @param revision the revision to set
    */
   public void setQualificationLevel(String qualification) {
      this.qualificationLevel = qualification;
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

   public String getUserId() {
      return userId;
   }

   public void setUserId(String userId) {
      this.userId = userId;
   }

   public String getUserName() {
      return userName;
   }

   public void setUserName(String userName) {
      this.userName = userName;
   }

   public String getEmail() {
      return email;
   }

   public void setEmail(String email) {
      this.email = email;
   }

   /**
    * @return the witness
    */
   public List<String> getWitnesses() {
      return witnesses;
   }

   /**
    * @param witness the witness to set
    */
   public void setWitnesses(List<String> witnesses) {
      this.witnesses = witnesses;
   }

   @JsonIgnore
   public List<TestCaseToken> getTestCases() {
      return testCases;
   }

   public void setTestCases(List<TestCaseToken> testCases) {
      this.testCases = testCases;
   }

   public List<TestPointToken> getTestPoints() {
      return testPoints;
   }

   public void setTestPoints(List<TestPointToken> testPoints) {
      this.testPoints = testPoints;
   }

   @JsonIgnore
   public List<AttentionLocationToken> getAttentionMessages() {
      return attentionMessages;
   }

   public void setAttentionMessages(List<AttentionLocationToken> attentionMessages) {
      this.attentionMessages = attentionMessages;
   }

   @JsonIgnore
   public List<ScriptLogToken> getLogs() {
      return logs;
   }

   public void setLogs(List<ScriptLogToken> logs) {
      this.logs = logs;
   }

   @JsonIgnore
   public List<VersionInformationToken> getVersionInformation() {
      return versionInformation;
   }

   public void setVersionInformation(List<VersionInformationToken> versionInformation) {
      this.versionInformation = versionInformation;
   }

   @JsonIgnore
   public List<LoggingSummaryToken> getLoggingSummaries() {
      return loggingSummaries;
   }

   public void setLoggingSummaries(List<LoggingSummaryToken> loggingSummaries) {
      this.loggingSummaries = loggingSummaries;
   }

   public int getTotalTestPoints() {
      return totalTestPoints;
   }

   public void setTotalTestPoints(int totalTestPoints) {
      this.totalTestPoints = totalTestPoints;
   }

   public CreateArtifact createArtifact(String key) {
      Map<AttributeTypeToken, String> values = new HashMap<>();
      values.put(CoreAttributeTypes.ContentUrl, this.getFileUrl());
      values.put(CoreAttributeTypes.ElapsedTime, Integer.toString(this.getElapsedTime()));
      values.put(CoreAttributeTypes.Email, this.getEmail());
      values.put(CoreAttributeTypes.EndDate, Long.toString(this.getEndDate().getTime()));
      values.put(CoreAttributeTypes.ExecutedBy, this.getExecutedBy());
      values.put(CoreAttributeTypes.ExecutionDate, Long.toString(this.getExecutionDate().getTime()));
      values.put(CoreAttributeTypes.ExecutionEnvironment, this.getExecutionEnvironment());
      values.put(CoreAttributeTypes.FailedCount, Integer.toString(this.getFailedCount()));
      values.put(CoreAttributeTypes.InteractiveCount, Integer.toString(this.getInteractiveCount()));
      values.put(CoreAttributeTypes.MachineName, this.getMachineName());
      values.put(CoreAttributeTypes.OsArchitecture, this.getOsArchitecture());
      values.put(CoreAttributeTypes.OsName, this.getOsName());
      values.put(CoreAttributeTypes.OsVersion, this.getOsVersion());
      values.put(CoreAttributeTypes.OseeServerJarVersion, this.getOseeServerJar());
      values.put(CoreAttributeTypes.OseeServerTitle, this.getOseeServer());
      values.put(CoreAttributeTypes.OseeVersion, this.getOseeVersion());
      values.put(CoreAttributeTypes.JavaVersion, this.getJavaVersion());
      values.put(CoreAttributeTypes.PassedCount, Integer.toString(this.getPassedCount()));
      values.put(CoreAttributeTypes.ProcessorId, this.getProcessorId());
      values.put(CoreAttributeTypes.QualificationLevel, this.getQualificationLevel());
      values.put(CoreAttributeTypes.Result, this.getResult());
      values.put(CoreAttributeTypes.ScriptAborted, Boolean.toString(this.getScriptAborted()));
      values.put(CoreAttributeTypes.ScriptHealth, Integer.toString(this.getScriptHealth()));
      values.put(CoreAttributeTypes.SetId, this.getSetId());
      values.put(CoreAttributeTypes.StartDate, Long.toString(this.getStartDate().getTime()));
      values.put(CoreAttributeTypes.UserId, this.getUserId());
      values.put(CoreAttributeTypes.UserName, this.getUserName());

      CreateArtifact art = new CreateArtifact();
      art.setName(this.getName());
      art.setTypeId(CoreArtifactTypes.TestScriptResults.getIdString());

      List<Attribute> attrs = new LinkedList<>();

      for (AttributeTypeToken type : CoreArtifactTypes.TestScriptResults.getValidAttributeTypes()) {
         String value = values.get(type);
         if (Strings.isInValid(value)) {
            continue;
         }
         Attribute attr = new Attribute(type.getIdString());
         attr.setValue(Arrays.asList(value));
         attrs.add(attr);
      }

      // Handle string list attributes
      if (this.getWitnesses().size() > 0) {
         Attribute attr = new Attribute(CoreAttributeTypes.Witness.getIdString());
         attr.setValue(this.getWitnesses());
         attrs.add(attr);
      }
      if (this.getRuntimeVersions().size() > 0) {
         Attribute attr = new Attribute(CoreAttributeTypes.RuntimeVersion.getIdString());
         attr.setValue(this.getRuntimeVersions());
         attrs.add(attr);
      }

      art.setAttributes(attrs);

      art.setkey(key);

      return art;
   }

}
