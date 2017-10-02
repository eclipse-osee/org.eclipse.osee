/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.widgets;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.validation.IOseeValidator;
import org.eclipse.osee.framework.skynet.core.validation.OseeValidator;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.swt.widgets.Composite;

/**
 * Single selection radio buttons that stores as sole boolean attribute
 *
 * @author Donald G. Dunne
 * @param optionToBoolean - map of radio button text to boolean to store
 * @param defaultValue - if null, buttons are un-selected, else select one matching defaultvalue
 */
public class XRadionButtonsBooleanDam extends XRadionButtonsDam {

   private final Map<String, Boolean> optionToBoolean;
   private Boolean defaultValue;

   public XRadionButtonsBooleanDam(String displayLabel) {
      super(displayLabel);
      this.optionToBoolean = new HashMap<>();
      this.optionToBoolean.put("No", false);
      this.optionToBoolean.put("Yes", true);
      this.defaultValue = false;
   }

   public XRadionButtonsBooleanDam(String displayLabel, Map<String, Boolean> optionToBoolean, Boolean defaultValue) {
      super(displayLabel);
      this.optionToBoolean = optionToBoolean;
      this.defaultValue = defaultValue;
      setMultiSelect(false);
   }

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      Boolean stored = getStored();
      for (Entry<String, Boolean> option : optionToBoolean.entrySet()) {
         XRadioButton button = addButton(option.getKey());
         button.setObject(option);
         if (option.getValue().equals(stored)) {
            button.setSelected(true);
         }
      }
      super.createControls(parent, horizontalSpan);
   }

   @Override
   public void saveToArtifact()  {
      if (isDirty().isTrue()) {
         Boolean selected = getSelected();
         if (selected == null) {
            artifact.deleteSoleAttribute(attributeType);
         } else {
            artifact.setSoleAttributeValue(attributeType, selected);
         }
      }
   }

   public Boolean getSelected() {
      Boolean selected = defaultValue;
      Set<String> selectedNames = getSelectedNames();
      if (!selectedNames.isEmpty()) {
         String sel = selectedNames.iterator().next();
         selected = optionToBoolean.get(sel);
      }
      return selected;
   }

   @Override
   public void revert()  {
      Boolean stored = getStored();
      artifact.setSoleAttributeValue(attributeType, stored);
   }

   @Override
   public Result isDirty()  {
      Boolean stored = getStored();
      Boolean selected = getSelected();
      if ((stored != null && !stored.equals(selected)) || //
         (selected != null && !selected.equals(stored))) {
         return new Result(true, String.format("Attribute Type [%s] dirty", getAttributeType()));
      }
      return Result.FalseResult;
   }

   private Boolean getStored() {
      return artifact.getSoleAttributeValue(attributeType, defaultValue);
   }

   public Boolean getDefaultValue() {
      return defaultValue;
   }

   public void setDefaultValue(Boolean defaultValue) {
      this.defaultValue = defaultValue;
   }

   @Override
   public IStatus isValid() {
      IStatus status = super.isValid();
      if (status.isOK()) {
         try {
            if (getArtifact() != null && getAttributeType() != null) {
               status = OseeValidator.getInstance().validate(IOseeValidator.SHORT, getArtifact(), getAttributeType(),
                  getStored());
               if (isRequiredEntry() && getSelected() == null) {
                  return new Status(IStatus.ERROR, getClass().getName(), "Option must be selected.");
               }
            }
         } catch (OseeCoreException ex) {
            status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Error getting Artifact", ex);
         }
      }
      return status;
   }

}
