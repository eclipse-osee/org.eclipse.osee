/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.ui.test.manager.pages.scriptTable;

import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.ote.ui.test.manager.models.OutputModelJob;
import org.eclipse.osee.ote.ui.test.manager.models.ScriptModel;
import org.eclipse.osee.ote.ui.test.manager.models.ScriptModel.ScriptInteractionEnum;

public class ScriptTask {

   public enum ScriptStatusEnum {
      CANCELLED, CANCELLING, COMPLETE, IN_QUEUE, INCOMPATIBLE, INVALID, NOT_CONNECTED, READY, RUNNING
   }

   private GUID guid = null;
   private boolean run = true;
   private ScriptModel scriptModel = null;

   private ScriptStatusEnum status = ScriptStatusEnum.INCOMPATIBLE;

   /**
    * @param rawFilename
    * @param outputDir alternate output directory for tmo output files null will default to script directory
    */
   public ScriptTask(String rawFilename, String outputDir) {
      scriptModel = new ScriptModel(rawFilename, outputDir);
      updateStatusOnConnected(false);
      OutputModelJob.getSingleton().addTask(this);
   }

   /**
    * @return Returns the guid.
    */
   public GUID getGuid() {
      return guid;
   }

   /**
    * @return Returns the interaction.
    */
   public ScriptInteractionEnum getInteraction() {
      return scriptModel.getInteraction();
   }

   /**
    * @return Returns the name.
    */
   public String getName() {
      if (scriptModel == null) {
         return "";
      } else {
         return scriptModel.getName();
      }
   }

   /**
    * @return Returns the passFail.
    */
   public String getPassFail() {

      int passTP = scriptModel.getOutputModel().getPassedTestPoints();
      int failTP = scriptModel.getOutputModel().getFailedTestPoints();
      if (scriptModel.getOutputModel().isAborted()) {
         return "ABORTED";
      } else if (passTP > 0 || failTP > 0) {
            if (failTP == 0) {
               return "PASS (" + passTP + ")";
            } else {
               return "FAIL (" + failTP + "/" + (passTP + failTP) + ")";
            }
      } else if (status == ScriptStatusEnum.RUNNING) {
         return "(0/0)";
      }
      return "";
   }

   /**
    * @return Returns the path.
    */
   public String getPath() {
      return scriptModel.getPath();
   }

   /**
    * @return Returns the scriptModel.
    */
   public ScriptModel getScriptModel() {
      return scriptModel;
   }

   /**
    * @return Returns the status.
    */
   public ScriptStatusEnum getStatus() {
      return status;
   }
   
   public Boolean getRunStatus() {
	  return run;
   }

   /**
    * @return Returns the outputExists.
    */
   public boolean isOutputExists() {
      return scriptModel.getOutputModel().exists();

   }

//   public void computeExists() {
//      exists = scriptModel.getOutputModel().exists();
//   }

   //   public boolean isOutputReadonly() {
   //      return !scriptModel.getOutputModel().getFile().canWrite();
   //   }

   /**
    * @return Returns the run.
    */
   public boolean isRun() {
      return run;
   }

   public boolean isRunnable() {
      return (isRun());// && status != ScriptStatusEnum.INCOMPATIBLE
      // && status != ScriptStatusEnum.INVALID);
   }

   /**
    * @param guid The guid to set.
    */
   public void setGuid(GUID guid) {
      this.guid = guid;
   }

   /**
    * @param run The run to set.
    */
   public void setRun(boolean run) {
      this.run = run;
   }

   /**
    * @param status The status to set.
    */
   public void setStatus(ScriptStatusEnum status) {
      this.status = status;
   }

   public void updateStatusOnConnected(boolean connected) {
      /* Always leave the status of INVALID alone */
      if (status != ScriptStatusEnum.INVALID) {
         /* If we're not connected, we'll leave the display blank */
         if (!connected) {
            status = ScriptStatusEnum.NOT_CONNECTED;
         } else {
            status = ScriptStatusEnum.READY;
         }
      }
   }
}