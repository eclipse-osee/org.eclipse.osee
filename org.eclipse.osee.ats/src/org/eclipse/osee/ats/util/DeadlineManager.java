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
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.world.IWorldViewArtifact;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.Result;

/**
 * @author Donald G. Dunne
 */
public class DeadlineManager {

   private StateMachineArtifact sma;

   public DeadlineManager(StateMachineArtifact sma) {
      this.sma = sma;
   }

   public Date getEcdDate() {
      try {
         return ((IWorldViewArtifact) sma).getWorldViewEstimatedCompletionDate();
      } catch (Exception ex) {
         // do nothing
      }
      return null;
   }

   public Date getDeadlineDate() {
      try {
         return ((IWorldViewArtifact) sma).getWorldViewDeadlineDate();
      } catch (Exception ex) {
         // do nothing
      }
      return null;
   }

   public boolean isDeadlineDateSet() {
      return getDeadlineDate() != null;
   }

   public boolean isEcdDateSet() {
      return getEcdDate() != null;
   }

   public Result isDeadlineDateOverdue() throws OseeCoreException {
      if (sma.isCompleted() || sma.isCancelled()) return Result.FalseResult;
      if ((new Date()).after(getDeadlineDate())) return new Result(true, "Need By Date has passed.");
      return Result.FalseResult;
   }

   public Result isEcdDateOverdue() throws OseeCoreException {
      if (sma.isCompleted() || sma.isCancelled()) return Result.FalseResult;
      if (getEcdDate() == null) return Result.FalseResult;
      if ((new Date()).after(getEcdDate())) return new Result(true, "Estimated Completion Date has passed.");
      if (getDeadlineDate() == null) return Result.FalseResult;
      if (getEcdDate().after(getDeadlineDate())) return new Result(true,
            "Estimated Completion Date after Need By Date.");
      return Result.FalseResult;
   }

   public Result isDeadlinePastRelease() {
      try {
         if (sma.isCompleted() || sma.isCancelled()) return Result.FalseResult;
         Date deadDate = getDeadlineDate();
         if (deadDate == null) return Result.FalseResult;
         Date releaseDate = sma.getWorldViewEstimatedReleaseDate();
         if (releaseDate == null) return Result.FalseResult;
         if (releaseDate.after(deadDate)) return new Result(true, "Need By Date is past current Release Date.");
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
      return Result.FalseResult;
   }

   public Result isDeadlineDateAlerting() throws OseeCoreException {
      if (!isDeadlineDateSet()) return Result.FalseResult;
      Result r = isDeadlineDateOverdue();
      if (r.isTrue()) return r;
      r = isDeadlinePastRelease();
      if (r.isTrue()) return r;
      return Result.FalseResult;
   }

   public Result isEcdDateAlerting() throws OseeCoreException {
      if (!isEcdDateSet()) return Result.FalseResult;
      Result r = isEcdDateOverdue();
      if (r.isTrue()) return r;
      return Result.FalseResult;
   }

}