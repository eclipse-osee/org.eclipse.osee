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

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Collection;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.DisplayHint;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;

/**
 * @author Jeff C. Phillips
 */
public class StringHandlePromptChange implements IHandlePromptChange {
   private final EntryDialog entryDialog;
   private final AttributeTypeToken attributeType;
   private final boolean persist;
   private final Collection<? extends Artifact> artifacts;
   private final NumberFormat format;

   public StringHandlePromptChange(AttributeTypeToken attributeType, boolean persist, String displayName, Collection<? extends Artifact> artifacts, NumberFormat format) {
      this.attributeType = attributeType;
      this.persist = persist;
      this.artifacts = artifacts;
      this.format = format;
      this.entryDialog = new EntryDialog("Enter " + displayName, "Enter " + displayName);
   }

   @Override
   public boolean promptOk() {
      boolean multiLine = false;
      if (attributeType.getDisplayHints().contains(DisplayHint.MultiLine)) {
         multiLine = true;
      }
      if (multiLine) {
         entryDialog.setFillVertically(multiLine);
      }
      setInitialText(artifacts, entryDialog, format, attributeType);
      entryDialog.setNumberFormat(format);
      return entryDialog.open() == Window.OK;
   }

   @Override
   public boolean store() {
      updateSmaAttributes(artifacts, attributeType, format, entryDialog);
      if (persist) {
         TransactionManager.persistInTransaction("Persist SMA attributes", artifacts);
      }
      return true;
   }

   private static void setInitialText(Collection<? extends Artifact> artifacts, EntryDialog entryDialog, NumberFormat format, AttributeTypeId attributeType) {
      if (artifacts.size() == 1) {
         Object smaObj = artifacts.iterator().next().getSoleAttributeValue(attributeType, "");
         String initialText = smaObj.equals("") ? "" : formatObject(smaObj, format);
         entryDialog.setEntry(initialText);
      } else {
         entryDialog.setEntry("");
      }
   }

   private static void updateSmaAttributes(final Collection<? extends Artifact> artifacts, AttributeTypeToken attributeType, NumberFormat format, EntryDialog entryDialog) {
      for (Artifact artifact : artifacts) {
         String value = entryDialog.getEntry();
         String safeValue = getSafeValue(value, format, attributeType);
         if (Strings.isValid(safeValue)) {
            artifact.setSoleAttributeFromString(attributeType, safeValue);
         } else {
            artifact.deleteAttributes(attributeType);
         }
      }
   }

   private static String getSafeValue(String value, NumberFormat format, AttributeTypeToken attributeType) {
      String toReturn = value;
      if (format != null && !toReturn.isEmpty()) {
         try {
            if (attributeType.isInteger()) {
               toReturn = String.valueOf(format.parse(value).intValue());
            } else if (attributeType.isArtifactId()) {
               toReturn = String.valueOf(format.parse(value).intValue());
            } else if (attributeType.isLong()) {
               toReturn = String.valueOf(format.parse(value).intValue());
            } else {
               toReturn = String.valueOf(format.parse(value).doubleValue()); // TODO check for dot in integers
            }
         } catch (ParseException ex) {
            OseeCoreException.wrapAndThrow(ex);
         }
      }
      return toReturn;
   }

   private static String formatObject(Object src, NumberFormat format) {
      if (format == null) {
         return String.valueOf(src);
      } else {
         return format.format(src);
      }
   }
}