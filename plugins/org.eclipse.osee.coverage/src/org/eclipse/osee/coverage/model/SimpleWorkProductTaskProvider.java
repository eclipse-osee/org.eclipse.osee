/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.coverage.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SimpleWorkProductTaskProvider implements IWorkProductTaskProvider {

   List<WorkProductAction> actions = new ArrayList<WorkProductAction>();
   private CoveragePackage coveragePackage;

   @Override
   public List<WorkProductAction> getWorkProductRelatedActions() {
      return actions;
   }

   @Override
   public void removeWorkProductAction(WorkProductAction action) {
      actions.remove(action);
   }

   @Override
   public void addWorkProductAction(Collection<WorkProductAction> actions) {
      actions.addAll(actions);
   }

   @Override
   public List<WorkProductTask> getWorkProductTasks() {
      List<WorkProductTask> tasks = new ArrayList<WorkProductTask>();
      for (WorkProductAction action : actions) {
         tasks.addAll(action.getTasks());
      }
      return tasks;
   }

   @Override
   public WorkProductTask getWorkProductTask(String guid) {
      for (WorkProductAction action : actions) {
         for (WorkProductTask task : action.getTasks()) {
            if (task.getGuid().equals(guid)) {
               return task;
            }
         }
      }
      return null;
   }

   @Override
   public void setCoveragePackage(CoveragePackage coveragePackage) {
      this.coveragePackage = coveragePackage;
   }

   @Override
   public CoveragePackage getCoveragePackage() {
      return coveragePackage;
   }

   @Override
   public void reload() {
      // do nothing
   }

}
