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
package org.eclipse.osee.ats.ide.util;

import java.util.Date;
import java.util.logging.Level;
import org.eclipse.osee.ats.ide.column.DeadlineColumn;
import org.eclipse.osee.ats.ide.column.EstimatedCompletionDateColumn;
import org.eclipse.osee.ats.ide.column.EstimatedReleaseDateColumn;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.logging.OseeLog;

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
      Result result = Result.FalseResult;
      if (sma.isInWork() && new Date().after(getDeadlineDate(sma))) {
         return new Result(true, "Need By Date has passed.");
      }
      return result;
   }

   public static Result isEcdDateOverdue(AbstractWorkflowArtifact sma) {
      Result result = Result.FalseResult;
      if (sma.isInWork()) {
         Date ecdDate = getEcdDate(sma);
         Date deadlineDate = getDeadlineDate(sma);
         if (ecdDate != null) {
            if (new Date().after(ecdDate)) {
               result = new Result(true, "Estimated Completion Date has passed.");
            }
            if (deadlineDate != null) {
               if (ecdDate.after(deadlineDate)) {
                  result = new Result(true, "Estimated Completion Date after Need By Date.");
               }
            }
         }
      }
      return result;
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
         OseeLog.log(Activator.class, Level.SEVERE, ex);
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