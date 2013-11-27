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
package org.eclipse.osee.ats.impl.internal.workitem;

import org.eclipse.osee.ats.api.workflow.IAtsGoal;
import org.eclipse.osee.orcs.data.ArtifactReadable;

public class Goal extends WorkItem implements IAtsGoal {

   public Goal(ArtifactReadable artifact) {
      super(artifact);
   }

}
