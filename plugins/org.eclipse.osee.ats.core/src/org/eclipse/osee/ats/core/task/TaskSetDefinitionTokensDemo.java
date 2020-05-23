/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.ats.core.task;

import org.eclipse.osee.ats.api.data.AtsTaskDefToken;

/**
 * @author Donald G. Dunne
 */
public class TaskSetDefinitionTokensDemo {

   public static AtsTaskDefToken SawSwDesignTestingChecklist =
      AtsTaskDefToken.valueOf(23492840234L, "Testing Checklist");
   public static AtsTaskDefToken SawSwDesignProcessChecklist =
      AtsTaskDefToken.valueOf(234965685392L, "Process Checklist");
   public static AtsTaskDefToken SawCreateTasksFromReqChanges =
      AtsTaskDefToken.valueOf(32948900200L, "SAW Create Tasks from Req Changes");

}
