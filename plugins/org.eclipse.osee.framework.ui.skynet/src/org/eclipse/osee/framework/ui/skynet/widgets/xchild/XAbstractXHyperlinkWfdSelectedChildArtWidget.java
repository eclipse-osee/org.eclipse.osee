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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.plugin.util.StringLabelProvider;
import org.eclipse.osee.framework.ui.skynet.ArtifactLabelProvider;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.util.StringNameComparator;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredCheckboxTreeDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredTreeDialog;
import org.eclipse.osee.framework.ui.swt.Widgets;

/**
 * Widget backed by a single parent artifact (eg: folder) with it's children UserGroups as the selectable items for the
 * filtered dialog. Persist to given artifact/attr type.
 *
 * @author Donald G. Dunne
 */
public abstract class XAbstractXHyperlinkWfdSelectedChildArtWidget extends XAbstractXHyperlinkWfdSelectedChildWidget {

   public XAbstractXHyperlinkWfdSelectedChildArtWidget(WidgetId widgetId, String label, ArtifactToken parentArt) {
      super(widgetId, label, parentArt);
   }

   @Override
   public boolean handleSelection() {
      Collection<ArtifactToken> selectable = getSelectable();
      return handleSelected(this.getArtifact(), getAttributeType(), selectable);
   }

   private boolean handleSelected(Artifact artifact, AttributeTypeToken attrType,
      Collection<ArtifactToken> selectable) {
      String title = "Select " + getLabel();
      if (artifact.getArtifactType().getMax(attrType) != 1) {
         FilteredCheckboxTreeDialog<ArtifactToken> dialog = new FilteredCheckboxTreeDialog<ArtifactToken>(title, title,
            new ArrayTreeContentProvider(), new StringLabelProvider(), new StringNameComparator(), true);
         dialog.setInput(selectable);
         int result = dialog.open();
         if (result == Window.OK || dialog.isClearSelected()) {
            List<ArtifactToken> checked = new ArrayList<ArtifactToken>();
            checked.addAll(dialog.getChecked());
            SkynetTransaction transaction =
               TransactionManager.createTransaction(artifact.getBranch(), "Set " + getLabel());
            getArtifact().setAttributeValues(attrType,
               checked.size() > 0 ? checked.stream().map(c -> c.getName()).collect(
                  Collectors.toList()) : Collections.emptyList());
            transaction.addArtifact(artifact);
            TransactionToken execute = transaction.execute();
            if (execute.isValid()) {
               for (ArtifactToken chk : checked) {
                  handleTransactionCompleted(chk);
               }
            }
            return true;
         }
      } else {
         FilteredTreeDialog dialog =
            new FilteredTreeDialog(title, title, new ArrayTreeContentProvider(), new ArtifactLabelProvider(), true);
         dialog.setInput(selectable);
         dialog.setMultiSelect(false);
         int result = dialog.open();
         if (result == Window.OK || dialog.isClearSelected()) {
            ArtifactToken selected = dialog.getSelectedFirst();
            SkynetTransaction transaction =
               TransactionManager.createTransaction(artifact.getBranch(), "Set " + getLabel());
            if (dialog.isClearSelected()) {
               if (!artifact.getSoleAttributeValueAsString(attrType, "").isEmpty()) {
                  getArtifact().deleteAttributes(attrType);
               } else {
                  return false;
               }
            } else {
               getArtifact().setSoleAttributeFromString(attrType, selected.getName());
            }
            transaction.addArtifact(artifact);
            TransactionToken execute = transaction.execute();
            if (execute.isValid()) {
               handleTransactionCompleted(selected);
            }
            return true;
         }
      }
      return false;
   }

   protected abstract void handleTransactionCompleted(ArtifactToken selected);

   @Override
   public String getCurrentValue() {
      String value = Widgets.NOT_SET;
      List<String> values = getArtifact().getAttributesToStringList(getAttributeType());
      if (values.size() > 0) {
         value = org.eclipse.osee.framework.jdk.core.util.Collections.toString(", ", values);
      }
      return value;
   }

   @Override
   public IStatus isValid() {
      IStatus status = Status.OK_STATUS;
      List<String> values = getArtifact().getAttributesToStringList(getAttributeType());
      if (isRequiredEntry() && values.isEmpty()) {
         status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, getLabel() + " must be selected.");
      }
      return status;
   }

}
