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
package org.eclipse.osee.ats.editor;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.util.widgets.dialog.TaskOptionStatusDialog;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.ui.PlatformUI;

/**
 * @author Donald G. Dunne
 */
public class SMAPromptChangeHoursSpent {

   private final Collection<? extends StateMachineArtifact> smas;

   public SMAPromptChangeHoursSpent(StateMachineArtifact sma) {
      this(Arrays.asList(sma));
   }

   public SMAPromptChangeHoursSpent(final Collection<? extends StateMachineArtifact> smas) {
      this.smas = smas;
   }

   public static boolean promptChangeStatus(Collection<? extends StateMachineArtifact> smas, boolean persist) throws OseeCoreException {
      SMAPromptChangeHoursSpent promptChangeStatus = new SMAPromptChangeHoursSpent(smas);
      return promptChangeStatus.promptChangeStatus(persist).isTrue();
   }

   public Result promptChangeStatus(boolean persist) throws OseeCoreException {
      Result result = SMAPromptChangeStatus.isValidToChangeStatus(smas);
      if (result.isFalse()) {
         result.popup();
         return result;
      }

      TaskOptionStatusDialog tsd =
         new TaskOptionStatusDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
            "Enter State Hours Spend", "Enter number of hours you spent since last status.", false, null, smas);
      if (tsd.open() == 0) {
         SMAPromptChangeStatus.performChangeStatus(smas, null,
            tsd.getSelectedOptionDef() != null ? tsd.getSelectedOptionDef().getName() : null,
            tsd.getHours().getFloat(), tsd.getPercent().getInt(), tsd.isSplitHours(), persist);
         return Result.TrueResult;
      }
      return Result.FalseResult;
   }

}
