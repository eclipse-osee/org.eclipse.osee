/*********************************************************************
 * Copyright (c) 2022 Boeing
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

import java.text.NumberFormat;
import java.util.Collection;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;

/**
 * @author Donald G. Dunne
 */
public class ArtifactIdHandlePromptChange implements IHandlePromptChange {
   private final EntryDialog dialog;
   private final Collection<? extends Artifact> artifacts;
   private final AttributeTypeToken attributeType;
   private final boolean persist;
   private int response;

   public ArtifactIdHandlePromptChange(Collection<? extends Artifact> artifacts, AttributeTypeToken attrType, String displayName, boolean persist) {
      super();
      this.artifacts = artifacts;
      this.attributeType = attrType;
      this.persist = persist;

      dialog = new EntryDialog("Change " + attrType.getName(), "Enter new " + attrType.getName());
      dialog.setNumberFormat(NumberFormat.getNumberInstance());
   }

   @Override
   public boolean promptOk() {
      response = dialog.open();
      return true;
   }

   @Override
   public boolean store() {
      if (response != Window.OK) {
         return false;
      }
      SkynetTransaction transaction = null;
      if (persist) {
         transaction =
            TransactionManager.createTransaction(artifacts.iterator().next().getBranch(), "Prompt change ArtifactId");
      }
      String valueStr = dialog.getEntry();
      if (Strings.isNotNumeric(valueStr)) {
         AWorkbench.popup("Value [%s] is not Numeric", valueStr);
         return false;
      }
      ArtifactId value = ArtifactId.valueOf(valueStr);
      for (Artifact artifact : artifacts) {
         artifact.setSoleAttributeValue(attributeType, value);
         artifact.persist(transaction);
      }
      if (persist) {
         TransactionToken tok = transaction.execute();
         return tok != null && tok.isValid();
      }
      return false;
   }
}