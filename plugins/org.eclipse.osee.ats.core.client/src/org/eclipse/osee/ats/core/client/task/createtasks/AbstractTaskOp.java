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
package org.eclipse.osee.ats.core.client.task.createtasks;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.core.client.internal.Activator;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Shawn F. Cook
 */
public abstract class AbstractTaskOp implements ITaskOperation {

   @Override
   public abstract IStatus execute(TaskMetadata metadata, IAtsChangeSet changes) throws OseeCoreException;

   protected Status generateGenericOkStatus(TaskEnum taskEnum, String task, String parentTeamWf, String changedArt) {

      StringBuilder sb = new StringBuilder();
      if (taskEnum.compareTo(TaskEnum.CREATE) == 0) {
         sb.append("Create");
      } else if (taskEnum.compareTo(TaskEnum.MODIFY) == 0) {
         sb.append("Modify");
      } else {
         sb.append("[Unhandled TaskEnum]");
      }

      sb.append(" Task:");
      sb.append(task);

      sb.append(" for ParentTeamWF:");
      sb.append(parentTeamWf);

      sb.append(" for ChangeData:");
      sb.append(changedArt);

      return new Status(IStatus.OK, Activator.PLUGIN_ID, sb.toString());
   }
}
