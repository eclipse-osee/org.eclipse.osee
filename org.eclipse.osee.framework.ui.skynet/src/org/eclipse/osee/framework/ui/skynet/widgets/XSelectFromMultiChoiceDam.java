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
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.validation.IOseeValidator;
import org.eclipse.osee.framework.skynet.core.validation.OseeValidator;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.util.ArtifactNameSorter;
import org.eclipse.osee.framework.ui.skynet.util.filteredTree.MinMaxOSEECheckedFilteredTreeDialog;
import org.eclipse.osee.framework.ui.skynet.util.filteredTree.SimpleCheckFilteredTreeDialog;

/**
 * @author Roberto E. Escobar
 */
public class XSelectFromMultiChoiceDam extends XSelectFromDialog<String> implements IArtifactWidget {

   private Artifact artifact;
   private String attributeTypeName;

   public XSelectFromMultiChoiceDam(String displayLabel) {
      super(displayLabel);
      this.artifact = null;
   }

   public void setArtifact(Artifact artifact, String attributeTypeName) throws OseeCoreException {
      this.artifact = artifact;
      this.attributeTypeName = attributeTypeName;
      AttributeType attributeType = AttributeTypeManager.getType(attributeTypeName);
      int minOccurrence = attributeType.getMinOccurrences();
      int maxOccurrence = attributeType.getMaxOccurrences();

      setRequiredSelection(minOccurrence, maxOccurrence);
      setSelected(getStored());
      setRequiredEntry(true);
   }

   @Override
   public MinMaxOSEECheckedFilteredTreeDialog createDialog() {
      SimpleCheckFilteredTreeDialog dialog =
            new SimpleCheckFilteredTreeDialog(getLabel(), "Select from the items below",
                  new ArrayTreeContentProvider(), new LabelProvider(), new ArtifactNameSorter(),
                  getMinSelectionRequired(), getMaxSelectionRequired());
      return dialog;
   }

   public Collection<String> getStored() throws OseeCoreException {
      return artifact.getAttributesToStringList(attributeTypeName);
   }

   @Override
   public Result isDirty() throws OseeCoreException {
      try {
         Collection<String> enteredValues = getSelected();
         Collection<String> storedValues = getStored();
         if (!Collections.isEqual(enteredValues, storedValues)) {
            return new Result(true, attributeTypeName + " is dirty");
         }
      } catch (NumberFormatException ex) {
         // do nothing
      }
      return Result.FalseResult;
   }

   @Override
   public void revert() throws OseeCoreException {
      setArtifact(artifact, attributeTypeName);
   }

   @Override
   public void saveToArtifact() throws OseeCoreException {
      artifact.setAttributeValues(attributeTypeName, getSelected());
   }

   @Override
   public IStatus isValid() {
      IStatus status = super.isValid();
      if (status.isOK()) {
         List<String> items = getSelected();
         for (String item : items) {
            status = OseeValidator.getInstance().validate(IOseeValidator.SHORT, artifact, attributeTypeName, item);
            if (!status.isOK()) {
               break;
            }
         }
      }
      return status;
   }
}
