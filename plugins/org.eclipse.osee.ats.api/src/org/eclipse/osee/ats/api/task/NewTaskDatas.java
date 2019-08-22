/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.task;

import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Donald G. Dunne
 */
public class NewTaskDatas {

   private List<NewTaskData> taskDatas = new LinkedList<>();
   private XResultData results;

   public NewTaskDatas() {
      // for jax-rs instantiation
   }

   public NewTaskDatas(NewTaskData newTaskData) {
      taskDatas.add(newTaskData);
   }

   public List<NewTaskData> getTaskDatas() {
      return taskDatas;
   }

   public void setTaskDatas(List<NewTaskData> taskDatas) {
      this.taskDatas = taskDatas;
   }

   public void add(NewTaskData newTaskData) {
      taskDatas.add(newTaskData);
   }

   @Override
   public String toString() {
      return "NewTaskDatas [datas=" + taskDatas + "]";
   }

   public XResultData getResults() {
      return results;
   }

   public void setResults(XResultData results) {
      this.results = results;
   }

}
