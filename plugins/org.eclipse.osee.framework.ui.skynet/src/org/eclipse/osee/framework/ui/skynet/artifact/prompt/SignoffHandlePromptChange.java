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
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Jeff C. Phillips
 */
public class SignoffHandlePromptChange implements IHandlePromptChange {
   private final MessageDialog dialog;
   private final Collection<? extends Artifact> artifacts;
   private final AttributeTypeToken attributeType;
   private final boolean persist;
   private int response;
   private final int cancelButtonNum;

   public SignoffHandlePromptChange(Collection<? extends Artifact> artifacts, AttributeTypeToken attributeType, String displayName, boolean persist, String toggleMessage) {
      super();
      this.artifacts = artifacts;
      this.attributeType = attributeType;
      this.persist = persist;

      toggleMessage = "Attribute not modifiable in Mass Editor, not yet implimented";
      List<String> buttonLabels = new ArrayList<String>();
      cancelButtonNum = 1;
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
      return false;
   }
}