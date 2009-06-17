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
package org.eclipse.osee.ote.ui.test.manager.models;

import java.util.concurrent.ConcurrentLinkedQueue;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.ote.ui.test.manager.connection.ScriptManager;
import org.eclipse.osee.ote.ui.test.manager.pages.scriptTable.ScriptTask;

/**
 * @author Andrew M. Finkbeiner
 *
 */
public class OutputModelJob extends Job {

   private static OutputModelJob singleton = null;
   private ScriptManager scriptManager;
   private ConcurrentLinkedQueue<ScriptTask> outputModels = new ConcurrentLinkedQueue<ScriptTask>(); 
   
   
   public static void createSingleton(ScriptManager scriptManager){
      if(singleton == null){
         singleton = new OutputModelJob(scriptManager);
      }
   }
   
   public static OutputModelJob getSingleton(){
      return singleton;
   }
   
   /**
    * @param name
    */
   private OutputModelJob(ScriptManager scriptManager) {
      super("Parsing OTE Output File");
      setUser(false);
      this.scriptManager = scriptManager;
   }

   /* (non-Javadoc)
    * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   protected IStatus run(IProgressMonitor monitor) {
      while(!outputModels.isEmpty()){
         ScriptTask task = outputModels.remove();
         task.getScriptModel().getOutputModel().updateTestPointsFromOutfile();
         task.getPassFail();
         scriptManager.updateScriptTableViewerTimed(task);
      }
      return Status.OK_STATUS;
   }
   
   public void addTask(ScriptTask task){
      outputModels.add(task);
      schedule();
   }
   
}
