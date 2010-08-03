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
package org.eclipse.osee.framework.ui.skynet.artifact.prompt;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Collection;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;

/**
 * @author Jeff C. Phillips
 */
public class StringHandlePromptChange implements IHandlePromptChange {
   private final EntryDialog entryDialog;
   private final String attributeName;
   private final boolean persist;
   private final boolean multiLine;
   private final Collection<? extends Artifact> artifacts;
   private final NumberFormat format;

   public StringHandlePromptChange(String attributeName, boolean persist, String displayName, Collection<? extends Artifact> artifacts, NumberFormat format, boolean multiLine) {
      super();
      this.attributeName = attributeName;
      this.persist = persist;
      this.artifacts = artifacts;
      this.multiLine = multiLine;
      this.format = format;
      this.entryDialog = new EntryDialog("Enter " + displayName, "Enter " + displayName);
   }

   @Override
   public boolean promptOk() throws OseeCoreException {
      entryDialog.setFillVertically(multiLine);
      setInitialText(artifacts, entryDialog, format, attributeName);
      entryDialog.setNumberFormat(format);
      return entryDialog.open() == Window.OK;
   }

   @Override
   public boolean store() throws OseeCoreException {
      updateSmaAttributes(artifacts, attributeName, format, entryDialog);
      if (persist) {
         persistSmaAttributes(artifacts);
      }
      return true;
   }

   private static void setInitialText(Collection<? extends Artifact> artifacts, EntryDialog entryDialog, NumberFormat format, String attributeName) throws OseeCoreException {
      if (artifacts.size() == 1) {
         Object smaObj = artifacts.iterator().next().getSoleAttributeValue(attributeName, "");
         String initialText = smaObj.equals("") ? "" : formatObject(smaObj, format);
         entryDialog.setEntry(initialText);
      } else {
         entryDialog.setEntry("");
      }
   }

   private static void updateSmaAttributes(final Collection<? extends Artifact> artifacts, String attributeName, NumberFormat format, EntryDialog entryDialog) throws OseeCoreException {
      for (Artifact artifact : artifacts) {
         String value = entryDialog.getEntry();
         String safeValue = getSafeValue(value, format);
         artifact.setSoleAttributeFromString(attributeName, safeValue);
      }
   }

   private static String getSafeValue(String value, NumberFormat format) throws OseeCoreException {
      String toReturn = value;
      if (format != null) {
         try {
            toReturn = String.valueOf(format.parse(value).doubleValue()); // TODO check for dot in integers
         } catch (ParseException ex) {
            OseeExceptions.wrapAndThrow(ex);
         }
      }
      return toReturn;
   }

   private static void persistSmaAttributes(final Collection<? extends Artifact> artifacts) throws OseeCoreException {
      SkynetTransaction transaction =
         new SkynetTransaction(artifacts.iterator().next().getBranch(), "Persist SMA attributes");
      for (Artifact artifact : artifacts) {
         artifact.persist(transaction);
      }
      transaction.execute();
   }

   private static String formatObject(Object src, NumberFormat format) {
      if (format == null) {
         return String.valueOf(src);
      } else {
         return format.format(src);
      }
   }
}