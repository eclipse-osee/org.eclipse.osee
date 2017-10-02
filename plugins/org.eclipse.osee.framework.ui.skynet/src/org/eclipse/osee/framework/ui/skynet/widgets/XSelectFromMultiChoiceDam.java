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
package org.eclipse.osee.framework.ui.skynet.widgets;

import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.validation.IOseeValidator;
import org.eclipse.osee.framework.skynet.core.validation.OseeValidator;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.skynet.util.ArtifactNameSorter;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredCheckboxTreeDialog;

/**
 * @author Roberto E. Escobar
 */
public class XSelectFromMultiChoiceDam extends XSelectFromDialog<String> implements IAttributeWidget {

   private Artifact artifact;
   private AttributeTypeToken attributeType;

   public XSelectFromMultiChoiceDam(String displayLabel) {
      super(displayLabel);
      this.artifact = null;
   }

   @Override
   public Artifact getArtifact() {
      return artifact;
   }

   @Override
   public AttributeTypeToken getAttributeType() {
      return attributeType;
   }

   @Override
   public void setAttributeType(Artifact artifact, AttributeTypeToken attributeType)  {
      this.artifact = artifact;
      this.attributeType = attributeType;
      int minOccurrence = AttributeTypeManager.getMinOccurrences(attributeType);
      int maxOccurrence = AttributeTypeManager.getMaxOccurrences(attributeType);

      setRequiredSelection(minOccurrence, maxOccurrence);
      setSelected(getStored());
      setRequiredEntry(true);
   }

   @Override
   public FilteredCheckboxTreeDialog createDialog() {
      FilteredCheckboxTreeDialog dialog = new FilteredCheckboxTreeDialog(getLabel(), "Select from the items below",
         new ArrayTreeContentProvider(), new LabelProvider(), new ArtifactNameSorter());
      return dialog;
   }

   public Collection<String> getStored()  {
      return getArtifact().getAttributesToStringList(getAttributeType());
   }

   @Override
   public Result isDirty()  {
      if (isEditable()) {
         try {
            Collection<String> enteredValues = getSelected();
            Collection<String> storedValues = getStored();
            if (!Collections.isEqual(enteredValues, storedValues)) {
               return new Result(true, getAttributeType() + " is dirty");
            }
         } catch (NumberFormatException ex) {
            // do nothing
         }
      }
      return Result.FalseResult;
   }

   @Override
   public void revert()  {
      setAttributeType(getArtifact(), getAttributeType());
   }

   @Override
   public void saveToArtifact()  {
      getArtifact().setAttributeValues(getAttributeType(), getSelected());
   }

   @Override
   public IStatus isValid() {
      IStatus status = super.isValid();
      if (status.isOK()) {
         List<String> items = getSelected();
         for (String item : items) {
            status =
               OseeValidator.getInstance().validate(IOseeValidator.SHORT, getArtifact(), getAttributeType(), item);
            if (!status.isOK()) {
               break;
            }
         }
      }
      return status;
   }

   @Override
   public boolean isEmpty() {
      return getSelected().isEmpty();
   }

}
