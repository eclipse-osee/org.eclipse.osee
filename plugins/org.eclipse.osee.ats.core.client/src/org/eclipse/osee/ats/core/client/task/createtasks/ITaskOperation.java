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
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Shawn F. Cook
 */
public interface ITaskOperation {
   public final static String AUTO_GENERATED_STATIC_ID = "taskAutoGen";

   IStatus execute(TaskMetadata metadata, IAtsChangeSet changes) throws OseeCoreException;
}
