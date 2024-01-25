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
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
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

   protected AttributeTypeToken attributeType;
   protected ArtifactTypeToken artifactType;
   protected ArtifactToken enumeratedArt;
   protected List<String> checked = new ArrayList<>();

   public XHyperlinkLabelEnumeratedArt() {
      super("");
   }

   public XHyperlinkLabelEnumeratedArt(String label) {
      super(label);
   }

   @Override
   public void setAttributeType(AttributeTypeToken attributeType) {
      this.attributeType = attributeType;
   }

   @Override
   public boolean handleSelection() {
      try {
         String title = "Select " + attributeType.getUnqualifiedName();
         if (artifactType.getMax(attributeType) != 1) {
            FilteredCheckboxTreeDialog<String> dialog = new FilteredCheckboxTreeDialog<String>(title, title,
               new ArrayTreeContentProvider(), new StringLabelProvider(), new StringNameComparator());
            dialog.setInput(getSelectable());
            Collection<String> selectedValues = getCurrentSelected();
            if (!selectedValues.isEmpty()) {
               dialog.setInitialSelections(selectedValues);
            }
            dialog.setShowSelectButtons(true);
            if (dialog.open() == Window.OK) {
               checked.clear();
               checked.addAll(dialog.getChecked());
               return true;
            }
         } else {
            FilteredListDialog<String> dialog = new FilteredListDialog<String>(title, title);
            dialog.setInput(getSelectable());
            if (dialog.open() == Window.OK) {
               if (dialog.getSelected() != null) {
                  checked.clear();
                  checked.add(dialog.getSelected());
                  return true;
               }
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
   public AttributeTypeToken getAttributeType() {
      return attributeType;
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

   public Collection<String> getSelectable() {
      if (enumeratedArt != null) {
         Artifact art = ArtifactQuery.getArtifactFromToken(enumeratedArt);
         if (art != null) {
            return art.getAttributesToStringList(CoreAttributeTypes.IdValue);
         }
      }
      return Collections.emptyList();
   }

   @Override
   public ArtifactToken getEnumeratedArt() {
      return enumeratedArt;
   }

   @Override
   public void setEnumeratedArt(ArtifactToken enumeratedArt) {
      this.enumeratedArt = enumeratedArt;
   }

   @Override
   public ArtifactTypeToken getArtifactType() {
      return artifactType;
   }

   @Override
   public void setArtifactType(ArtifactTypeToken artifactType) {
      this.artifactType = artifactType;
   }

   public List<String> getChecked() {
      return checked;
   }

   public void setChecked(List<String> checked) {
      this.checked.clear();
      this.checked.addAll(checked);
      refresh();
   }

}
