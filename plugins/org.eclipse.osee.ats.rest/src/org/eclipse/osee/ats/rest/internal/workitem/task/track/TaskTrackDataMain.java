/*********************************************************************
 * Copyright (c) 2024 Boeing
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
package org.eclipse.osee.ats.rest.internal.workitem.task.track;

import org.eclipse.osee.ats.api.task.track.TaskTrackItem;
import org.eclipse.osee.ats.api.task.track.TaskTrackingData;
import org.eclipse.osee.framework.core.util.JsonUtil;

/**
 * Run to see sample output of TaskTrackingData and TaskTrackItems
 *
 * @author Donald G. Dunne
 */
public class TaskTrackDataMain {

   public TaskTrackDataMain() {
   }

   public static void main(String[] args) {
      TaskTrackingData tasksData = new TaskTrackingData();

      TaskTrackItem jTask = new TaskTrackItem();
      tasksData.getTrackItems().getTasks().add(jTask);
      jTask.setTitle("Test MIM Web");
      jTask.setAssigneesArtIds("128030"); // Denk
      jTask.setDescription("Load MIM Web and do minor checks");

      TaskTrackItem jTask2 = new TaskTrackItem();
      tasksData.getTrackItems().getTasks().add(jTask2);
      jTask2.setTitle("Test Web");
      jTask2.setAssigneesArtIds("122982"); // Miller
      jTask2.setDescription("Load customer facing Web");

      System.out.println(JsonUtil.toJson(tasksData));

      System.out.println(JsonUtil.toJson(tasksData.getTrackItems()));
   }

}
