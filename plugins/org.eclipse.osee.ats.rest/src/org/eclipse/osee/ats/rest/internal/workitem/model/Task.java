/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.workitem.model;

import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.core.workflow.WorkItem;
import org.eclipse.osee.ats.rest.IAtsServer;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Donald G. Dunne
 */
public class Task extends WorkItem implements IAtsTask {

   public Task(Log logger, IAtsServer atsServer, ArtifactReadable artifact) {
      super(logger, atsServer, artifact);
   }

}
