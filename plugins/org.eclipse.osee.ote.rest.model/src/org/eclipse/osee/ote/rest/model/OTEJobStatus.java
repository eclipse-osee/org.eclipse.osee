package org.eclipse.osee.ote.rest.model;

import java.net.URL;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class OTEJobStatus {

   private URL updatedJobStatus;
   private String jobId;
   
   private int totalUnitsOfWork;
   private int unitsWorked;
   private String errorLog;
   private boolean jobComplete;
   private boolean success;
   
   public OTEJobStatus(){
      errorLog = "";
      jobComplete = false;
      success = true;
      totalUnitsOfWork = 0;
      unitsWorked = 0;
   }
   
   public int getTotalUnitsOfWork() {
      return totalUnitsOfWork;
   }
   
   public int getUnitsWorked() {
      return unitsWorked;
   }

   public String getErrorLog() {
      return errorLog;
   }

   public boolean isJobComplete() {
      return jobComplete;
   }

   public void setTotalUnitsOfWork(int i) {
      totalUnitsOfWork = i;
   }

   public void jobComplete() {
      jobComplete = true;
   }

   public void incrememtUnitsWorked() {
      unitsWorked++;
   }
   
   public void setUnitsWorked(int unitsWorked) {
      this.unitsWorked = unitsWorked;
   }

   public void setErrorLog(String log) {
      this.errorLog = log;
   }

   public void setJobComplete(boolean jobComplete) {
      this.jobComplete = jobComplete;
   }
   
   public boolean isSuccess() {
      return success;
   }

   public void setSuccess(boolean success) {
      this.success = success;
   }

   public URL getUpdatedJobStatus() {
      return updatedJobStatus;
   }

   public void setUpdatedJobStatus(URL updatedJobStatus) {
      this.updatedJobStatus = updatedJobStatus;
   }

   public String getJobId() {
      return jobId;
   }

   public void setJobId(String jobId) {
      this.jobId = jobId;
   }
   
}
