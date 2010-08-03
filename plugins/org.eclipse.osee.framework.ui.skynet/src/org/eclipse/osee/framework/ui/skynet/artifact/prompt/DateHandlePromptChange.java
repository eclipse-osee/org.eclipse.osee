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
package org.eclipse.osee.framework.ui.skynet.artifact.prompt;

import java.util.Collection;
import java.util.Date;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.DateSelectionDialog;

/**
 * @author Jeff C. Phillips
 */
public class DateHandlePromptChange implements IHandlePromptChange {
   private final DateSelectionDialog diag;
   private final Collection<? extends Artifact> artifacts;
   private final String attributeName;
   private final boolean persist;

   public DateHandlePromptChange(Collection<? extends Artifact> artifacts, String attributeName, String displayName, boolean persist) {
      super();
      this.artifacts = artifacts;
      this.attributeName = attributeName;
      this.persist = persist;
      String diagTitle = "Select " + displayName;
      Date currentDate = null;
      try {
         currentDate =
            artifacts.size() == 1 ? artifacts.iterator().next().getSoleAttributeValue(attributeName, null, Date.class) : null;
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      this.diag = new DateSelectionDialog(diagTitle, diagTitle, currentDate);
   }

   @Override
   public boolean promptOk() {
      return diag.open() == Window.OK;
   }

   @Override
   public boolean store() throws OseeCoreException {
      for (Artifact artifact : artifacts) {
         if (diag.isNoneSelected()) {
            artifact.deleteSoleAttribute(attributeName);
         } else {
            artifact.setSoleAttributeValue(attributeName, diag.getSelectedDate());
         }
      }

      if (persist) {
         SkynetTransaction transaction =
            new SkynetTransaction(artifacts.iterator().next().getBranch(), "Persist artifact date change");
         for (Artifact artifact : artifacts) {
            artifact.persist(transaction);
         }
         transaction.execute();
      }
      return true;
   }
}