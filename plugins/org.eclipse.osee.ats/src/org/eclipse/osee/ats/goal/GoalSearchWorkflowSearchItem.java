/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.goal;

import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.world.WorldEditorParameterSearchItem;
import org.eclipse.osee.ats.world.search.GoalSearchItem;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class GoalSearchWorkflowSearchItem extends WorldEditorParameterSearchItem {

   private GoalSearchItem searchItem;

   public GoalSearchWorkflowSearchItem(String name) {
      super(name, AtsImage.GOAL);
      setShortName("Goal");
   }

   public GoalSearchWorkflowSearchItem() {
      this("Goal Search");
   }

   public GoalSearchWorkflowSearchItem(GoalSearchWorkflowSearchItem goalWorkflowSearchItem) {
      super(goalWorkflowSearchItem, AtsImage.GOAL);
      setShortName("Goal");
   }

   @Override
   public GoalSearchWorkflowSearchItem copy() {
      return new GoalSearchWorkflowSearchItem(this);
   }

   @Override
   public GoalSearchWorkflowSearchItem copyProvider() {
      return new GoalSearchWorkflowSearchItem(this);
   }

   @Override
   public String getParameterXWidgetXml() {
      addTitleWidget();
      addUserWidget("Assignee");
      addIncludeCompletedCancelledWidget();
      return super.getParameterXWidgetXml();
   }

   @Override
   public Result isParameterSelectionValid() {
      try {
         IAtsUser user = getUser("Assignee");
         boolean includeCompleted = isIncludeCompletedCancelled();
         if (user != null && includeCompleted) {
            return new Result("Assignee and Include Completed are not compatible selections.");
         }
         return Result.TrueResult;
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         return new Result("Exception: " + ex.getLocalizedMessage());
      }
   }

   @Override
   public Callable<Collection<? extends Artifact>> createSearch() throws OseeCoreException {
      return new Callable<Collection<? extends Artifact>>() {

         @Override
         public Collection<? extends Artifact> call() throws Exception {
            return searchItem.performSearchGetResults(false);
         }
      };
   }

   @Override
   public void createSearchItem() {
      searchItem =
         new GoalSearchItem("", this.getTitle(), this.isIncludeCompletedCancelled(), this.getUser("Assignee"));
   }

}
