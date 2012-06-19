/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.client.task.createTasks;

import org.eclipse.osee.ats.core.client.task.createtasks.ITaskTitleProvider;
import org.eclipse.osee.ats.core.client.task.createtasks.TaskMetadata;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Shawn F. Cook
 */
public class MockTaskTitleProvider implements ITaskTitleProvider {

   public final static String mockTaskTitlePrefix = "Task for ChangedArt:";

   public MockTaskTitleProvider() {
      System.out.println("test");
   }

   @Override
   public String getTaskTitle(TaskMetadata metadata) {
      Artifact changedArt = metadata.getChangedArtifact();
      String changedArtGuid = changedArt.getGuid();
      return mockTaskTitlePrefix + changedArtGuid;
   }

   @Override
   public boolean isKeySupported(String taskTitleProviderKey) {
      return taskTitleProviderKey.equals(mockTaskTitlePrefix);
   }

}
