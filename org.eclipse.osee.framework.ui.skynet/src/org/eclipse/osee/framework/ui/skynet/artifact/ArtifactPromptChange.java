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

package org.eclipse.osee.framework.ui.skynet.artifact;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.attribute.BooleanAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.DateAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.EnumeratedAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.FloatingPointAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.IntegerAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.StringAttribute;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.artifact.EnumSelectionDialog.Selection;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.DateSelectionDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class ArtifactPromptChange {

   public static boolean promptChangeAttribute(String attributeName, String displayName, final Collection<? extends Artifact> artifacts, boolean persist) {
      try {
         if (AttributeTypeManager.isBaseTypeCompatible(DateAttribute.class, attributeName)) {
            return ArtifactPromptChange.promptChangeDate(attributeName, displayName, artifacts, persist);
         } else if (AttributeTypeManager.isBaseTypeCompatible(FloatingPointAttribute.class, attributeName)) {
            return ArtifactPromptChange.promptChangeFloatAttribute(attributeName, displayName, artifacts, persist);
         } else if (AttributeTypeManager.isBaseTypeCompatible(IntegerAttribute.class, attributeName)) {
            return ArtifactPromptChange.promptChangeIntegerAttribute(attributeName, displayName, artifacts, persist);
         } else if (AttributeTypeManager.isBaseTypeCompatible(BooleanAttribute.class, attributeName)) {
            return ArtifactPromptChange.promptChangeBoolean(attributeName, displayName, artifacts, null, persist);
         } else if (AttributeTypeManager.isBaseTypeCompatible(EnumeratedAttribute.class, attributeName)) {
            return ArtifactPromptChange.promptChangeEnumeratedAttribute(attributeName, displayName, artifacts, persist);
         } else if (AttributeTypeManager.isBaseTypeCompatible(StringAttribute.class, attributeName)) {
            return ArtifactPromptChange.promptChangeStringAttribute(attributeName, displayName, artifacts, persist,
                  true);
         } else {
            AWorkbench.popup("ERROR", "Unhandled attribute type.  Can't edit through this view");
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   public static boolean promptChangeIntegerAttribute(String attributeName, String displayName, final Collection<? extends Artifact> artifacts, boolean persist) throws OseeCoreException {
      return promptChangeStringAttribute(attributeName, displayName, NumberFormat.getIntegerInstance(), artifacts,
            persist, false);
   }

   public static boolean promptChangeIntegerAttribute(String attributeName, String displayName, final Artifact artifact, boolean persist) throws OseeCoreException {
      return promptChangeStringAttribute(attributeName, displayName, NumberFormat.getIntegerInstance(),
            Arrays.asList(artifact), persist, false);
   }

   public static boolean promptChangePercentAttribute(String attributeName, String displayName, final Artifact artifact, boolean persist) throws OseeCoreException {
      return promptChangeStringAttribute(attributeName, displayName, NumberFormat.getPercentInstance(),
            Arrays.asList(artifact), persist, false);
   }

   public static boolean promptChangePercentAttribute(String attributeName, String displayName, final Collection<? extends Artifact> artifacts, boolean persist) throws OseeCoreException {
      return promptChangeStringAttribute(attributeName, displayName, NumberFormat.getPercentInstance(), artifacts,
            persist, false);
   }

   public static boolean promptChangeFloatAttribute(String attributeName, String displayName, final Artifact artifact, boolean persist) throws OseeCoreException {
      return promptChangeFloatAttribute(attributeName, displayName, Arrays.asList(artifact), persist);
   }

   public static boolean promptChangeFloatAttribute(String attributeName, String displayName, final Collection<? extends Artifact> smas, boolean persist) throws OseeCoreException {
      return promptChangeStringAttribute(attributeName, displayName, NumberFormat.getInstance(), smas, persist, false);
   }

   public static boolean promptChangeStringAttribute(String attributeName, String displayName, final Artifact artifact, boolean persist, boolean multiLine) throws OseeCoreException {
      return promptChangeStringAttribute(attributeName, displayName, null, Arrays.asList(artifact), persist, multiLine);
   }

   public static boolean promptChangeStringAttribute(String attributeName, String displayName, final Collection<? extends Artifact> smas, boolean persist, boolean multiLine) throws OseeCoreException {
      return promptChangeStringAttribute(attributeName, displayName, null, smas, persist, multiLine);
   }

   public static boolean promptChangeDate(String attributeName, String displayName, Artifact artifact, boolean persist) throws OseeCoreException {
      return promptChangeDate(attributeName, displayName, Arrays.asList(artifact), persist);
   }

   public static boolean promptChangeDate(String attributeName, String displayName, final Collection<? extends Artifact> artifacts, boolean persist) throws OseeCoreException {
      String diagTitle = "Select " + displayName;
      Date currentDate =
            artifacts.size() == 1 ? artifacts.iterator().next().getSoleAttributeValue(attributeName, null, Date.class) : null;
      DateSelectionDialog diag = new DateSelectionDialog(diagTitle, diagTitle, currentDate);
      if (diag.open() == DateSelectionDialog.OK) {
         for (Artifact artifact : artifacts) {
            if (diag.isNoneSelected()) {
               artifact.deleteSoleAttribute(attributeName);
            } else {
               artifact.setSoleAttributeValue(attributeName, diag.getSelectedDate());
            }
         }

         if (persist) {
            SkynetTransaction transaction =
                  new SkynetTransaction(artifacts.iterator().next().getBranch(), "Persist artifact date change");
            for (Artifact artifact : artifacts) {
               artifact.persist(transaction);
            }
            transaction.execute();
         }
      }

      return true;
   }

   public static boolean promptChangeEnumeratedAttribute(String attributeName, String displayName, Artifact artifact, boolean persist) throws Exception {
      return promptChangeEnumeratedAttribute(attributeName, displayName, Arrays.asList(artifact), persist);
   }

   public static boolean promptChangeEnumeratedAttribute(String attributeName, String displayName, final Collection<? extends Artifact> artifacts, boolean persist) throws Exception {
      String type = artifacts.iterator().next().getArtifactTypeName();
      for (Artifact art : artifacts) {
         if (!type.equals(art.getArtifactTypeName())) {
            AWorkbench.popup("ERROR", "Artifact types must all match to change enumerated type.");
            return false;
         }
      }
      EnumSelectionDialog diag = new EnumSelectionDialog(attributeName, artifacts);
      if (diag.open() == 0) {
         Set<String> selected = new HashSet<String>();
         for (Object obj : diag.getResult()) {
            selected.add((String) obj);
         }
         if (artifacts.size() > 0) {
            SkynetTransaction transaction =
                  !persist ? null : new SkynetTransaction(artifacts.iterator().next().getBranch(),
                        "Change enumerated attribute");
            for (Artifact artifact : artifacts) {
               List<String> current = artifact.getAttributesToStringList(attributeName);
               if (diag.getSelected() == Selection.AddSelection) {
                  current.addAll(selected);
                  artifact.setAttributeValues(attributeName, current);
               } else if (diag.getSelected() == Selection.DeleteSelected) {
                  current.removeAll(selected);
                  artifact.setAttributeValues(attributeName, current);
               } else if (diag.getSelected() == Selection.ReplaceAll) {
                  artifact.setAttributeValues(attributeName, selected);
               } else {
                  AWorkbench.popup("ERROR", "Unhandled selection type => " + diag.getSelected().name());
                  return false;
               }
               if (persist) {
                  artifact.persist(transaction);
               }
            }
            if (persist) {
               transaction.execute();
            }
         }
      }
      return true;
   }

   private static String formatObject(Object src, NumberFormat format) {
      if (format == null) {
         return String.valueOf(src);
      } else {
         return format.format(src);
      }
   }

   private static void setInitialText(Collection<? extends Artifact> smas, EntryDialog ed, NumberFormat format, String attributeName) throws OseeCoreException {
      if (smas.size() == 1) {
         Object smaObj = smas.iterator().next().getSoleAttributeValue(attributeName, "");
         String initialText = smaObj.equals("") ? "" : formatObject(smaObj, format);
         ed.setEntry(initialText);
      } else {
         ed.setEntry("");
      }
   }

   public static boolean promptChangeStringAttribute(String attributeName, String displayName, NumberFormat format, final Collection<? extends Artifact> smas, boolean persist, boolean multiLine) throws OseeCoreException {
      EntryDialog ed = new EntryDialog("Enter " + displayName, "Enter " + displayName);

      ed.setFillVertically(multiLine);
      setInitialText(smas, ed, format, attributeName);
      ed.setNumberFormat(format);

      int result = ed.open();
      if (result == EntryDialog.OK) {
         updateSmaAttributes(smas, attributeName, format, ed);

         if (persist) {
            persistSmaAttributes(smas);
         }
         return true;
      }
      return false;
   }

   private static String getSafeValue(String value, NumberFormat format) throws OseeWrappedException {
      if (format == null) {
         return value;
      }

      try {
         String safeValue = String.valueOf(format.parse(value).doubleValue()); // TODO check for dot in integers
         return safeValue;
      } catch (ParseException ex) {
         throw new OseeWrappedException(ex);
      }
   }

   private static void updateSmaAttributes(final Collection<? extends Artifact> smas, String attributeName, NumberFormat format, EntryDialog ed) throws OseeCoreException {
      for (Artifact sma : smas) {
         String value = ed.getEntry();
         String safeValue = getSafeValue(value, format);
         sma.setSoleAttributeFromString(attributeName, safeValue);
      }
   }

   private static void persistSmaAttributes(final Collection<? extends Artifact> smas) throws OseeCoreException {
      SkynetTransaction transaction =
            new SkynetTransaction(smas.iterator().next().getBranch(), "Persist SMA attributes");
      for (Artifact sma : smas) {
         sma.persist(transaction);
      }

      transaction.execute();
   }

   public static boolean promptChangeBoolean(String attributeName, String displayName, final Artifact artifact, String toggleMessage, boolean persist) throws OseeCoreException {
      return promptChangeBoolean(attributeName, displayName, Arrays.asList(artifact), toggleMessage, persist);
   }

   public static boolean promptChangeBoolean(String attributeName, String displayName, final Collection<? extends Artifact> smas, String toggleMessage, boolean persist) throws OseeCoreException {
      boolean set = false;
      if (smas.size() == 1) {
         set = smas.iterator().next().getSoleAttributeValue(attributeName, false);
      }
      MessageDialogWithToggle md =
            new MessageDialogWithToggle(Display.getCurrent().getActiveShell(), displayName, null, displayName,
                  MessageDialog.QUESTION, new String[] {"Ok", "Cancel"}, MessageDialog.OK,
                  toggleMessage != null ? toggleMessage : displayName, set);

      int result = md.open();
      if (result == 256) {
         if (smas.size() > 0) {
            SkynetTransaction transaction =
                  !persist ? null : new SkynetTransaction(smas.iterator().next().getBranch(), "Prompt change boolean");
            for (Artifact sma : smas) {
               sma.setSoleAttributeValue(attributeName, md.getToggleState());
               if (persist) {
                  sma.persist();
               }
            }
            if (persist) {
               transaction.execute();
            }
         }
         return true;
      }
      return false;
   }

}
