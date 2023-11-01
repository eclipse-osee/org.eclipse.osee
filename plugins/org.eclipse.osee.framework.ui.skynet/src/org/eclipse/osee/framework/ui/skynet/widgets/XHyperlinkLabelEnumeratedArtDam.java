/*******************************************************************************
 * Copyright (c) 2023 Boeing.
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
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.util.Result;
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
 * @author Vaibhav Patel
 */
public class XHyperlinkLabelEnumeratedArtDam extends XHyperlinkLabelValueSelection implements AttributeWidget {

   public static final String WIDGET_ID = XHyperlinkLabelEnumeratedArtDam.class.getSimpleName();

   protected AttributeTypeToken attributeType;
   protected ArtifactToken enumeratedArt;
   protected Artifact artifact;

   public XHyperlinkLabelEnumeratedArtDam() {
      super("");
   }

   public XHyperlinkLabelEnumeratedArtDam(String label) {
      super(label);
   }

   public void setAttributeType(AttributeTypeToken attributeType) {
      this.attributeType = attributeType;
   }

   @Override
   public boolean handleSelection() {
      try {
         String title = "Select " + attributeType.getUnqualifiedName();
         if (artifact.getArtifactType().getMax(attributeType) != 1) {
            FilteredCheckboxTreeDialog<String> dialog = new FilteredCheckboxTreeDialog<String>(title, title,
               new ArrayTreeContentProvider(), new StringLabelProvider(), new StringNameComparator());
            dialog.setInput(getSelectable());
            Collection<String> selectedValues = getCurrentSelected();
            if (!selectedValues.isEmpty()) {
               dialog.setInitialSelections(selectedValues);
            }
            dialog.setShowSelectButtons(true);
            if (dialog.open() == Window.OK) {
               List<String> checked = new ArrayList<String>();
               checked.addAll(dialog.getChecked());
               artifact.setAttributeValues(attributeType, checked);
               artifact.persistInThread("Set Value(s)");
               return true;
            }
         } else {
            FilteredListDialog<String> dialog = new FilteredListDialog<String>(title, title);
            dialog.setInput(getSelectable());
            if (dialog.open() == Window.OK) {
               if (dialog.getSelected() != null) {
                  artifact.setSoleAttributeValue(attributeType, dialog.getSelected());
                  artifact.persistInThread("Set Value");
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
      List<String> values = artifact.getAttributesToStringList(attributeType);
      if (values.size() > 0) {
         value = org.eclipse.osee.framework.jdk.core.util.Collections.toString(", ", values);
      }
      return value;
   }

   public List<String> getCurrentSelected() {
      return artifact.getAttributesToStringList(attributeType);
   }

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
   public void setAttributeType(Artifact artifact, AttributeTypeToken attributeType) {
      this.artifact = artifact;
      this.attributeType = attributeType;
      if (Strings.isInValid(getLabel())) {
         setLabel(attributeType.getUnqualifiedName());
      }
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
            if (getArtifact() != null && getAttributeType() != null) {
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
            status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Error getting Artifact", ex);
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

   public ArtifactToken getEnumeratedArt() {
      return enumeratedArt;
   }

   public void setEnumeratedArt(ArtifactToken enumeratedArt) {
      this.enumeratedArt = enumeratedArt;
   }

}
