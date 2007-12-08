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
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.world.IWorldViewArtifact;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;

/**
 * @author Donald G. Dunne
 */
public class DeadlineManager {

   private final SMAManager smaMgr;

   public DeadlineManager(SMAManager smaMgr) {
      this.smaMgr = smaMgr;
   }

   public Date getDeadlineDate() {
      try {
         return ((IWorldViewArtifact) smaMgr.getSma()).getWorldViewDeadlineDate();
      } catch (Exception ex) {
         // do nothing
      }
      return null;
   }

   public boolean isDeadlineDateSet() {
      return getDeadlineDate() != null;
   }

   public Result isDeadlineDateOverdue() {
      if (smaMgr.isCompleted() || smaMgr.isCancelled()) return Result.FalseResult;
      if ((new Date()).after(getDeadlineDate())) return new Result(true, "Deadline Date has past.");
      return Result.FalseResult;
   }

   public Result isDeadlinePastRelease() {
      try {
         if (smaMgr.isCompleted() || smaMgr.isCancelled()) return Result.FalseResult;
         Date deadDate = getDeadlineDate();
         if (deadDate == null) return Result.FalseResult;
         Date releaseDate = smaMgr.getSma().getWorldViewEstimatedReleaseDate();
         if (releaseDate == null) return Result.FalseResult;
         if (releaseDate.after(deadDate)) return new Result(true, "Deadline Date is past current Release Date.");
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, false);
      }
      return Result.FalseResult;
   }

   public Result isDeadlineDateAlerting() {
      if (!isDeadlineDateSet()) return Result.FalseResult;
      Result r = isDeadlineDateOverdue();
      if (r.isTrue()) return r;
      r = isDeadlinePastRelease();
      if (r.isTrue()) return r;
      return Result.FalseResult;
   }

}