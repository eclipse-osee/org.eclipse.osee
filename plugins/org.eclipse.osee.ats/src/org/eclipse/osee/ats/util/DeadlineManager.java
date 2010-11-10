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
package org.eclipse.osee.ats.util;

import java.util.Date;
import java.util.logging.Level;
import org.eclipse.osee.ats.artifact.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.field.DeadlineColumn;
import org.eclipse.osee.ats.field.EstimatedCompletionDateColumn;
import org.eclipse.osee.ats.field.EstimatedReleaseDateColumn;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.Result;

/**
 * @author Donald G. Dunne
 */
public class DeadlineManager {

   public static Date getEcdDate(AbstractWorkflowArtifact sma) {
      try {
         return EstimatedCompletionDateColumn.getDate(sma);
      } catch (Exception ex) {
         // do nothing
      }
      return null;
   }

   public static Date getDeadlineDate(AbstractWorkflowArtifact sma) {
      try {
         return DeadlineColumn.getDate(sma);
      } catch (Exception ex) {
         // do nothing
      }
      return null;
   }

   public static boolean isDeadlineDateSet(AbstractWorkflowArtifact sma) {
      return getDeadlineDate(sma) != null;
   }

   public static boolean isEcdDateSet(AbstractWorkflowArtifact sma) {
      return getEcdDate(sma) != null;
   }

   public static Result isDeadlineDateOverdue(AbstractWorkflowArtifact sma) {
      if (sma.isCompleted() || sma.isCancelled()) {
         return Result.FalseResult;
      }
      if (new Date().after(getDeadlineDate(sma))) {
         return new Result(true, "Need By Date has passed.");
      }
      return Result.FalseResult;
   }

   public static Result isEcdDateOverdue(AbstractWorkflowArtifact sma) {
      if (sma.isCompleted() || sma.isCancelled()) {
         return Result.FalseResult;
      }
      Date ecdDate = getEcdDate(sma);
      Date deadlineDate = getDeadlineDate(sma);
      if (ecdDate == null) {
         return Result.FalseResult;
      }
      if (new Date().after(ecdDate)) {
         return new Result(true, "Estimated Completion Date has passed.");
      }
      if (deadlineDate == null) {
         return Result.FalseResult;
      }
      if (ecdDate.after(deadlineDate)) {
         return new Result(true, "Estimated Completion Date after Need By Date.");
      }
      return Result.FalseResult;
   }

   public static Result isDeadlinePastRelease(AbstractWorkflowArtifact sma) {
      try {
         if (sma.isCompleted() || sma.isCancelled()) {
            return Result.FalseResult;
         }
         Date deadDate = getDeadlineDate(sma);
         if (deadDate == null) {
            return Result.FalseResult;
         }
         Date releaseDate = EstimatedReleaseDateColumn.getDateFromWorkflow(sma);
         if (releaseDate == null) {
            releaseDate = EstimatedReleaseDateColumn.getDateFromTargetedVersion(sma);
            if (releaseDate == null) {
               return Result.FalseResult;
            }
         }
         if (releaseDate.after(deadDate)) {
            return new Result(true, "Need By Date is past current Release Date.");
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
      return Result.FalseResult;
   }

   public static Result isDeadlineDateAlerting(AbstractWorkflowArtifact sma) {
      if (!isDeadlineDateSet(sma)) {
         return Result.FalseResult;
      }
      Result r = isDeadlineDateOverdue(sma);
      if (r.isTrue()) {
         return r;
      }
      r = isDeadlinePastRelease(sma);
      if (r.isTrue()) {
         return r;
      }
      return Result.FalseResult;
   }

   public static Result isEcdDateAlerting(AbstractWorkflowArtifact sma) {
      if (!isEcdDateSet(sma)) {
         return Result.FalseResult;
      }
      Result r = isEcdDateOverdue(sma);
      if (r.isTrue()) {
         return r;
      }
      return Result.FalseResult;
   }

}