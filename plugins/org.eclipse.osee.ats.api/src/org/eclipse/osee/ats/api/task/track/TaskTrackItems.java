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
package org.eclipse.osee.ats.api.task.track;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Donald G. Dunne
 */
public class TaskTrackItems {

   List<TaskTrackItem> tasks = new ArrayList<>();

   public TaskTrackItems() {
      // for jax-rs
   }

   public List<TaskTrackItem> getTasks() {
      return tasks;
   }

   public void setTasks(List<TaskTrackItem> tasks) {
      this.tasks = tasks;
   }

}
