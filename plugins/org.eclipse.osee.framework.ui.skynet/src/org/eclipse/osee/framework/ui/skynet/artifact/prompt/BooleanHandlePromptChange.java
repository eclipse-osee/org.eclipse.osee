/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.ui.skynet.artifact.prompt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.core.data.AttributeTypeGeneric;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.DisplayHint;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Jeff C. Phillips
 */
public class BooleanHandlePromptChange implements IHandlePromptChange {
   private final MessageDialog dialog;
   private final Collection<? extends Artifact> artifacts;
   private final AttributeTypeId attributeType;
   private final boolean persist;
   private boolean yesNoBoolean = false;
   private boolean triStateBoolean = false;
   private int response;
   private int cancelButtonNum;

   public BooleanHandlePromptChange(Collection<? extends Artifact> artifacts, AttributeTypeId attributeType, String displayName, boolean persist, String toggleMessage) {
      super();
      this.artifacts = artifacts;
      this.attributeType = attributeType;
      this.persist = persist;

      List<String> buttonLabels = new ArrayList<String>();
      AttributeTypeGeneric<?> attrType = AttributeTypeManager.getAttributeType(attributeType.getId());
      yesNoBoolean = attrType.getDisplayHints().contains(DisplayHint.YesNoBoolean);
      triStateBoolean = attrType.getDisplayHints().contains(DisplayHint.YesNoBoolean);
      cancelButtonNum = 0;
      if (yesNoBoolean) {
         buttonLabels.add("Yes");
         buttonLabels.add("No");
      } else {
         buttonLabels.add("True");
         buttonLabels.add("False");
      }
      if (triStateBoolean) {
         buttonLabels.add("Clear");
         cancelButtonNum = 4;
      } else {
         cancelButtonNum = 3;
      }
      buttonLabels.add("Cancel");

      dialog = new MessageDialog(Displays.getActiveShell(), displayName, null, toggleMessage, MessageDialog.QUESTION,
         cancelButtonNum, buttonLabels.toArray(new String[buttonLabels.size()]));
   }

   @Override
   public boolean promptOk() {
      response = dialog.open();
      return true;
   }

   @Override
   public boolean store() {
      if (response == cancelButtonNum) {
         return false;
      }
      SkynetTransaction transaction = null;
      if (persist) {
         transaction =
            TransactionManager.createTransaction(artifacts.iterator().next().getBranch(), "Prompt change boolean");
      }
      for (Artifact artifact : artifacts) {
         if (response == 0) {
            artifact.setSoleAttributeValue(attributeType, true);
         } else if (response == 1) {
            artifact.setSoleAttributeValue(attributeType, false);
         } else if (triStateBoolean) {
            artifact.deleteAttributes(attributeType);
         }
         if (persist) {
            artifact.persist(transaction);
         }
      }
      if (persist) {
         transaction.execute();
      }
      return true;
   }
}