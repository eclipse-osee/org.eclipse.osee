/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.ats.api.task;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Donald G. Dunne
 */
public class JaxAtsTasks {

   public List<JaxAtsTask> tasks = new ArrayList<>();
   public XResultData results = new XResultData();

   public List<JaxAtsTask> getTasks() {
      return tasks;
   }

   public void setTasks(List<JaxAtsTask> tasks) {
      this.tasks = tasks;
   }

   public XResultData getResults() {
      return results;
   }

   public void setResults(XResultData rd) {
      this.results = rd;
   }

}
