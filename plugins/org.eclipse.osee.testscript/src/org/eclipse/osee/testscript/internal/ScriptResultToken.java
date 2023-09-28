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

import java.util.Date;
import org.eclipse.osee.accessor.types.ArtifactAccessorResult;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;

/**
 * @author Stephen J. Molaro
 */
public class ScriptResultToken extends ArtifactAccessorResult {

   public static final ScriptResultToken SENTINEL = new ScriptResultToken();

   private String processorId;
   private String runtimeVersion;
   private Date executionDate;
   private String executionEnvironment;
   private String machine;
   private int passedCount;
   private int failedCount;
   private int interactiveCount;
   private boolean scriptAborted;
   private int elapsedTime;
   private Date startDate;
   private Date endDate;
   private Date elapsedDate;
   private String osArchitecture;
   private String osName;
   private String osVersion;
   private String oseeServerJar;
   private String oseeServer;
   private String oseeVersion;
   private String result;
   private int scriptHealth;

   public ScriptResultToken(ArtifactToken art) {
      this((ArtifactReadable) art);
   }

   public ScriptResultToken(ArtifactReadable art) {
      super(art);
      this.setId(art.getId());
      this.setName(art.getName());
      this.setProcessorId(art.getSoleAttributeAsString(CoreAttributeTypes.ProcessorId, ""));
      this.setRuntimeVersion(art.getSoleAttributeAsString(CoreAttributeTypes.RuntimeVersion, ""));
      this.setExecutionDate(art.getSoleAttributeValue(CoreAttributeTypes.ExecutionDate, new Date()));
      this.setExecutionEnvironment(art.getSoleAttributeAsString(CoreAttributeTypes.ExecutionEnvironment, ""));
      this.setMachineName(art.getSoleAttributeAsString(CoreAttributeTypes.Machine, ""));
      this.setPassedCount(art.getSoleAttributeValue(CoreAttributeTypes.PassedCount, -1));
      this.setFailedCount(art.getSoleAttributeValue(CoreAttributeTypes.FailedCount, -1));
      this.setInteractiveCount(art.getSoleAttributeValue(CoreAttributeTypes.InteractiveCount, -1));
      this.setScriptAborted(art.getSoleAttributeValue(CoreAttributeTypes.ScriptAborted, false));
      this.setElapsedTime(art.getSoleAttributeValue(CoreAttributeTypes.ElapsedTime, -1));
      this.setStartDate(art.getSoleAttributeValue(CoreAttributeTypes.StartDate, new Date()));
      this.setEndDate(art.getSoleAttributeValue(CoreAttributeTypes.EndDate, new Date()));
      this.setElapsedDate(art.getSoleAttributeValue(CoreAttributeTypes.ElapsedDate, new Date()));
      this.setOsArchitecture(art.getSoleAttributeAsString(CoreAttributeTypes.OsArchitecture, ""));
      this.setOsName(art.getSoleAttributeAsString(CoreAttributeTypes.OsName, ""));
      this.setOsVersion(art.getSoleAttributeAsString(CoreAttributeTypes.OsVersion, ""));
      this.setOseeServerJar(art.getSoleAttributeAsString(CoreAttributeTypes.OseeServerJarVersion, ""));
      this.setOseeServer(art.getSoleAttributeAsString(CoreAttributeTypes.OseeServerTitle, ""));
      this.setOseeVersion(art.getSoleAttributeAsString(CoreAttributeTypes.OseeVersion, ""));
      this.setResult(art.getSoleAttributeAsString(CoreAttributeTypes.Result, ""));
      this.setScriptHealth(art.getSoleAttributeValue(CoreAttributeTypes.ScriptHealth, -1));
   }

   public ScriptResultToken(Long id, String name) {
      super(id, name);
      this.setProcessorId("");
      this.setRuntimeVersion("");
      this.setExecutionDate(new Date());
      this.setExecutionEnvironment("");
      this.setMachineName("");
      this.setPassedCount(-1);
      this.setFailedCount(-1);
      this.setInteractiveCount(-1);
      this.setScriptAborted(false);
      this.setElapsedTime(-1);
      this.setStartDate(new Date());
      this.setEndDate(new Date());
      this.setElapsedDate(new Date());
      this.setOsArchitecture("");
      this.setOsName("");
      this.setOsVersion("");
      this.setOseeServerJar("");
      this.setOseeServer("");
      this.setOseeVersion("");
      this.setResult("");
      this.setScriptHealth(-1);
   }

   public ScriptResultToken() {
      super();
   }

   /**
    * @return the processorId
    */
   public String getProcessorId() {
      return processorId;
   }

   /**
    * @param processorId the processorId to set
    */
   public void setProcessorId(String processorId) {
      this.processorId = processorId;
   }

   /**
    * @return the runtimeVersion
    */
   public String getRuntimeVersion() {
      return runtimeVersion;
   }

   /**
    * @param runtimeVersion the runtimeVersion to set
    */
   public void setRuntimeVersion(String runtimeVersion) {
      this.runtimeVersion = runtimeVersion;
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
    * @return the elapsedDate
    */
   public Date getElapsedDate() {
      return elapsedDate;
   }

   /**
    * @param elapsedDate the elapsedDate to set
    */
   public void setElapsedDate(Date elapsedDate) {
      this.elapsedDate = elapsedDate;
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

}
