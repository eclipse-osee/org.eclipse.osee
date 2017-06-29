/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.workflow;

import java.util.List;
import org.eclipse.osee.ats.api.IAtsWorkItem;

/**
 * @author David W. Miller
 */
public class WorkItemArray {

   private List<IAtsWorkItem> workItems;

   public List<IAtsWorkItem> getWorkItems() {
      return workItems;
   }

   public void setWorkItems(List<IAtsWorkItem> workItems) {
      this.workItems = workItems;
   }

}
