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

import java.util.Collection;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.DisplayHint;
import org.eclipse.osee.framework.core.widget.validate.IValueValidator;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;

/**
 * @author Jeff C. Phillips
 */
public class StringHandlePromptChange implements IHandlePromptChange {
   private final EntryDialog dialog;
   private final AttributeTypeToken attrType;
   private final boolean persist;
   private final Collection<? extends Artifact> artifacts;
   private final IValueValidator validator;
   private final String viewName;

   public StringHandlePromptChange(AttributeTypeToken attributeType, boolean persist, String displayName, Collection<? extends Artifact> artifacts, IValueValidator validator, String viewName) {
      this.attrType = attributeType;
      this.persist = persist;
      this.artifacts = artifacts;
      this.validator = validator;
      this.viewName = viewName;
      this.dialog = new EntryDialog("Enter " + displayName, "Enter " + displayName);
   }

   @Override
   public boolean promptOk() {
      boolean multiLine = false;
      if (attrType.getDisplayHints().contains(DisplayHint.MultiLine)) {
         multiLine = true;
      }
      if (multiLine) {
         dialog.setFillVertically(multiLine);
      }
      dialog.setValidator(validator);
      String currValue = "";
      if (artifacts.size() == 1) {
         currValue = artifacts.iterator().next().getSoleAttributeValueAsString(attrType, "");
      }
      if (Strings.isValid(currValue)) {
         dialog.setEntry(currValue);
      }
      return dialog.open() == Window.OK;
   }

   @Override
   public boolean store() {
      String diagEntry = dialog.getEntry();
      if (Strings.isValid(diagEntry)) {
         diagEntry = validateEntry(dialog.getEntry(), attrType);
      }
      for (Artifact artifact : artifacts) {
         if (Strings.isValid(diagEntry)) {
            artifact.setSoleAttributeFromString(attrType, diagEntry);
         } else {
            artifact.deleteAttributes(attrType);
         }
      }
      if (persist) {
         TransactionManager.persistInTransaction(viewName + " - Persist Attributes", artifacts);
      }
      return true;
   }

   /**
    * This should not exception as the dialog validator should not allow invalid values to be entered. This is for a
    * double check to make sure we're not putting in wrong data
    */
   private static String validateEntry(String value, AttributeTypeToken attrType) {
      String toReturn = value;
      if (Strings.isValid(value)) {
         if (attrType.isInteger()) {
            toReturn = Integer.valueOf(value).toString();
         } else if (attrType.isArtifactId()) {
            toReturn = Long.valueOf(value).toString();
         } else if (attrType.isLong()) {
            toReturn = Long.valueOf(value).toString();
         } else if (attrType.isBranchId()) {
            toReturn = Long.valueOf(value).toString();
         } else if (attrType.isDouble()) {
            toReturn = Double.valueOf(value).toString();
         } else if (attrType.isBoolean()) {
            toReturn = Boolean.valueOf(value).toString();
         } else {
            throw new OseeArgumentException(String.format("Unhandled attribute type [%s]", attrType.toStringWithId()));
         }
      }
      return toReturn;
   }

}
