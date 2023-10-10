/*********************************************************************
 * Copyright (c) 2023 Boeing
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
package org.eclipse.osee.framework.ui.skynet.widgets.xchild;

import java.util.Collection;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.skynet.ArtifactLabelProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.AttributeWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredTreeDialog;

/**
 * Widget backed by a single parent artifact (eg: folder) with it's children UserGroups as the selectable items for the
 * filtered dialog. Persist to given artifact/attr type.
 *
 * @author Donald G. Dunne
 */
public abstract class AbstractXHyperlinkWfdSelectedChildDam extends AbstractXHyperlinkWfdSelectedChild implements AttributeWidget {

   protected Artifact artifact;
   protected AttributeTypeToken attributeTypeToken;

   public AbstractXHyperlinkWfdSelectedChildDam(String label, ArtifactToken parentArt) {
      super(label, parentArt);
   }

   @Override
   public boolean handleSelection() {
      Collection<ArtifactToken> selectable = getSelectable();
      return handleSelected(this.artifact, this.attributeTypeToken, selectable);
   }

   private boolean handleSelected(Artifact artifact, AttributeTypeToken attrType,
      Collection<ArtifactToken> selectable) {
      FilteredTreeDialog dialog = new FilteredTreeDialog("Select " + label, "Select " + label,
         new ArrayTreeContentProvider(), new ArtifactLabelProvider());
      dialog.setInput(selectable);
      dialog.setMultiSelect(false);
      if (dialog.open() == Window.OK) {
         ArtifactToken selected = dialog.getSelectedFirst();
         SkynetTransaction transaction = TransactionManager.createTransaction(artifact.getBranch(), "Set " + label);
         artifact.setSoleAttributeFromString(attrType, selected.getName());
         transaction.addArtifact(artifact);
         TransactionToken execute = transaction.execute();
         if (execute.isValid()) {
            handleTransactionCompleted(selected);
         }
      }

      return false;
   }

   protected abstract void handleTransactionCompleted(ArtifactToken selected);

   @Override
   public Artifact getArtifact() {
      return artifact;
   }

   @Override
   public void saveToArtifact() {
      // do nothing
   }

   @Override
   public void revert() {
      // do nothing
   }

   @Override
   public Result isDirty() {
      return Result.FalseResult;
   }

   @Override
   public void setAttributeType(Artifact artifact, AttributeTypeToken attributeTypeToken) {
      this.artifact = artifact;
      this.attributeTypeToken = attributeTypeToken;
   }

   @Override
   public AttributeTypeToken getAttributeType() {
      return attributeTypeToken;
   }

   @Override
   public String getCurrentValue() {
      String sel = "Not Set";
      if (artifact != null) {
         String value = artifact.getSoleAttributeValueAsString(attributeTypeToken, null);
         if (Strings.isValid(value)) {
            sel = value;
         }
      }
      return sel;
   }

}
