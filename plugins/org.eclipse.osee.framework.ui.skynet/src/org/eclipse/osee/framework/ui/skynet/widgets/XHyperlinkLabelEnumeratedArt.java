/*******************************************************************************
 * Copyright (c) 2024 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.widgets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.plugin.util.StringLabelProvider;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.util.StringNameComparator;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredCheckboxTreeDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredListDialog;
import org.eclipse.osee.framework.ui.swt.Widgets;

/**
 * @author Donald G. Dunne
 */
public class XHyperlinkLabelEnumeratedArt extends XHyperlinkLabelValueSelection implements EnumeratedArtifactWidget {

   public static final String WIDGET_ID = XHyperlinkLabelEnumeratedArt.class.getSimpleName();
   protected List<String> checked = new ArrayList<>();

   public XHyperlinkLabelEnumeratedArt() {
      super("");
   }

   public XHyperlinkLabelEnumeratedArt(String label) {
      super(label);
   }

   @Override
   public boolean handleSelection() {
      try {
         String title = "Select " + attributeType.getUnqualifiedName();
         ArtifactToken enumArt = checkEnumeratedArtifact();
         if (enumArt.isInvalid()) {
            AWorkbench.popupf("Enumerated Artifact %s does not exist", getEnumeratedArt().toStringWithId());
            return false;
         }
         Collection<String> selectable = getSelectable();
         if (selectable.isEmpty()) {
            AWorkbench.popupf("No [%s] options configured for this workflow", label);
            return false;
         }
         boolean multiSelect = isMultiSelect();
         if (artifactType.isValid() && attributeType.isValid()) {
            multiSelect = artifactType.getMax(attributeType) > 1;
         }
         if (multiSelect) {
            FilteredCheckboxTreeDialog<String> dialog = new FilteredCheckboxTreeDialog<String>(title, title,
               new ArrayTreeContentProvider(), new StringLabelProvider(), new StringNameComparator());
            dialog.setInput(selectable);
            dialog.setClearAllowed(true);
            Collection<String> selectedValues = getCurrentSelected();
            if (!selectedValues.isEmpty()) {
               dialog.setInitialSelections(selectedValues);
            }
            dialog.setShowSelectButtons(true);
            if (dialog.open() == Window.OK) {
               checked.clear();
               if (!dialog.isClearSelected()) {
                  checked.addAll(dialog.getChecked());
               }
               return true;
            }
         } else {
            FilteredListDialog<String> dialog = new FilteredListDialog<String>(title, title);
            dialog.setInput(selectable);
            dialog.setClearAllowed(true);
            if (dialog.open() == Window.OK) {
               checked.clear();
               if (!dialog.isClearSelected() && dialog.getSelected() != null) {
                  checked.add(dialog.getSelected());
               }
               return true;
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return false;
   }

   @Override
   public String getCurrentValue() {
      String value = Widgets.NOT_SET;
      if (checked.size() > 0) {
         value = org.eclipse.osee.framework.jdk.core.util.Collections.toString(", ", checked);
      }
      return value;
   }

   public List<String> getCurrentSelected() {
      return checked;
   }

   @Override
   public IStatus isValid() {
      IStatus status = super.isValid();
      if (status.isOK()) {
         try {
            if (getArtifactType() != null && getAttributeType() != null) {
               String currValue = getCurrentValue();
               if (Widgets.NOT_SET.equals(currValue)) {
                  currValue = "";
               }
               if (isRequiredEntry() && Strings.isInValid(currValue)) {
                  status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
                     String.format("Must select [%s]", getAttributeType().getUnqualifiedName()));
               }
            }
         } catch (OseeCoreException ex) {
            status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Error validating", ex);
         }
      }
      return status;
   }

   public ArtifactToken checkEnumeratedArtifact() {
      ArtifactToken enumArt = getEnumeratedArt();
      if (enumArt.isInvalid()) {
         return ArtifactToken.SENTINEL;
      }
      enumArt = ArtifactQuery.getArtifactFromTokenOrSentinel(enumArt);
      return enumArt;
   }

   public Collection<String> getSelectable() {
      ArtifactToken enumArt = getEnumeratedArt();
      if (enumArt != null && enumArt.isValid()) {
         ArtifactToken art = ArtifactQuery.getArtifactFromTokenOrSentinel(enumArt);
         if (art.isValid()) {
            return ((Artifact) art).getAttributesToStringList(CoreAttributeTypes.IdValue);
         }
      }
      return Collections.emptyList();
   }

   public List<String> getChecked() {
      return checked;
   }

   public void setChecked(List<String> checked) {
      this.checked.clear();
      this.checked.addAll(checked);
      refresh();
   }

   public String getFirstSelected() {
      String first = "";
      List<String> selected = getCurrentSelected();
      if (selected.size() > 0) {
         first = selected.iterator().next();
      }
      return first;
   }

}
