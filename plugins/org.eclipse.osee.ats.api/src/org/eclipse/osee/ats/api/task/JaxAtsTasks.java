/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
