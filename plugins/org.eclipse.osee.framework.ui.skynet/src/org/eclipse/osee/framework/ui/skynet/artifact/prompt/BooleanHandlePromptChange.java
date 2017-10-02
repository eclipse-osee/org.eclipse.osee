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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Jeff C. Phillips
 */
public class BooleanHandlePromptChange implements IHandlePromptChange {
   private final MessageDialogWithToggle dialog;
   private final Collection<? extends Artifact> artifacts;
   private final AttributeTypeId attributeType;
   private final boolean persist;

   public BooleanHandlePromptChange(Collection<? extends Artifact> artifacts, AttributeTypeId attributeType, String displayName, boolean persist, String toggleMessage) {
      super();
      this.artifacts = artifacts;
      this.attributeType = attributeType;
      this.persist = persist;

      boolean set = false;
      if (artifacts.size() == 1) {
         try {
            set = artifacts.iterator().next().getSoleAttributeValue(attributeType, false);
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }

      this.dialog =
         new MessageDialogWithToggle(Displays.getActiveShell(), displayName, null, displayName, MessageDialog.QUESTION,
            new String[] {"Ok", "Cancel"}, Window.OK, toggleMessage != null ? toggleMessage : displayName, set);
   }

   @Override
   public boolean promptOk() {
      int response = dialog.open();
      return response == 256;
   }

   @Override
   public boolean store()  {
      if (persist) {
         SkynetTransaction transaction =
            TransactionManager.createTransaction(artifacts.iterator().next().getBranch(), "Prompt change boolean");
         for (Artifact artifact : artifacts) {
            artifact.setSoleAttributeValue(attributeType, dialog.getToggleState());
            artifact.persist(transaction);
         }
         transaction.execute();
      } else {
         for (Artifact artifact : artifacts) {
            artifact.setSoleAttributeValue(attributeType, dialog.getToggleState());
         }
      }
      return true;
   }
}