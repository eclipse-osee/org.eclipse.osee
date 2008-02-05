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

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.BooleanAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.DateAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.DynamicAttributeManager;
import org.eclipse.osee.framework.skynet.core.attribute.EnumeratedAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.FloatingPointAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.IntegerAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.StringAttribute;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.artifact.EnumSelectionDialog.Selection;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.DateSelectionDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class ArtifactPromptChange {

   private final static String VALID_FLOAT_REG_EX = "^[0-9\\.]+$";
   private final static String VALID_INTEGER_REG_EX = "^[0-9]+$";
   private final static String VALID_PERCENT_REG_EX =
         "^(0*100{1,1}\\.?((?<=\\.)0*)?%?$)|(^0*\\d{0,2}\\.?((?<=\\.)\\d*)?%?)$";

   public static boolean promptChangeAttribute(String attributeName, String displayName, final Collection<? extends Artifact> artifacts, boolean persist) throws SQLException {
      try {
         DynamicAttributeManager dam = artifacts.iterator().next().getAttributeManager(attributeName);
         if (dam.getDescriptor().getBaseAttributeClass().equals(DateAttribute.class)) {
            return ArtifactPromptChange.promptChangeDate(attributeName, displayName, artifacts, persist);
         } else if (dam.getDescriptor().getBaseAttributeClass().equals(FloatingPointAttribute.class)) {
            return ArtifactPromptChange.promptChangeFloatAttribute(attributeName, displayName, artifacts, persist);
         } else if (dam.getDescriptor().getBaseAttributeClass().equals(IntegerAttribute.class)) {
            return ArtifactPromptChange.promptChangeIntegerAttribute(attributeName, displayName, artifacts, persist);
         } else if (dam.getDescriptor().getBaseAttributeClass().equals(BooleanAttribute.class)) {
            return ArtifactPromptChange.promptChangeBoolean(attributeName, displayName, artifacts, null, persist);
         } else if (dam.getDescriptor().getBaseAttributeClass().equals(EnumeratedAttribute.class)) {
            return ArtifactPromptChange.promptChangeEnumeratedAttribute(attributeName, displayName, artifacts, persist);
         } else if (dam.getDescriptor().getBaseAttributeClass().equals(StringAttribute.class)) {
            return ArtifactPromptChange.promptChangeStringAttribute(attributeName, displayName, artifacts, persist);
         } else
            AWorkbench.popup("ERROR", "Unhandled attribute type.  Can't edit through this view");
      } catch (Exception ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, true);
      }
      return false;
   }

   public static boolean promptChangeIntegerAttribute(String attributeName, String displayName, final Collection<? extends Artifact> artifacts, boolean persist) throws SQLException {
      return promptChangeStringAttribute(attributeName, displayName, VALID_INTEGER_REG_EX, artifacts, persist);
   }

   public static boolean promptChangeIntegerAttribute(String attributeName, String displayName, final Artifact artifact, boolean persist) throws SQLException {
      return promptChangeStringAttribute(attributeName, displayName, VALID_INTEGER_REG_EX,
            Arrays.asList(new Artifact[] {artifact}), persist);
   }

   public static boolean promptChangePercentAttribute(String attributeName, String displayName, final Artifact artifact, boolean persist) throws SQLException {
      return promptChangeStringAttribute(attributeName, displayName, VALID_PERCENT_REG_EX,
            Arrays.asList(new Artifact[] {artifact}), persist);
   }

   public static boolean promptChangePercentAttribute(String attributeName, String displayName, final Collection<? extends Artifact> artifacts, boolean persist) throws SQLException {
      return promptChangeStringAttribute(attributeName, displayName, VALID_PERCENT_REG_EX, artifacts, persist);
   }

   public static boolean promptChangeFloatAttribute(String attributeName, String displayName, final Artifact artifact, boolean persist) throws SQLException {
      return promptChangeStringAttribute(attributeName, displayName, VALID_FLOAT_REG_EX,
            Arrays.asList(new Artifact[] {artifact}), persist);
   }

   public static boolean promptChangeFloatAttribute(String attributeName, String displayName, final Collection<? extends Artifact> smas, boolean persist) throws SQLException {
      return promptChangeStringAttribute(attributeName, displayName, VALID_FLOAT_REG_EX, smas, persist);
   }

   public static boolean promptChangeStringAttribute(String attributeName, String displayName, final Artifact artifact, boolean persist) throws SQLException {
      return promptChangeStringAttribute(attributeName, displayName, null, Arrays.asList(new Artifact[] {artifact}),
            persist);
   }

   public static boolean promptChangeStringAttribute(String attributeName, String displayName, final Collection<? extends Artifact> smas, boolean persist) throws SQLException {
      return promptChangeStringAttribute(attributeName, displayName, null, smas, persist);
   }

   public static boolean promptChangeDate(String attributeName, String displayName, Artifact artifact, boolean persist) throws SQLException {
      return promptChangeDate(attributeName, displayName, Arrays.asList(new Artifact[] {artifact}), persist);
   }

   public static boolean promptChangeDate(String attributeName, String displayName, final Collection<? extends Artifact> artifacts, boolean persist) throws SQLException {
      // prompt that current release is (get from attribute); want to
      // change
      DateSelectionDialog diag =
            new DateSelectionDialog("Select " + displayName, "Select " + displayName,
                  artifacts.size() == 1 ? artifacts.iterator().next().getSoleDateAttributeValue(attributeName) : null);
      if (diag.open() == 0) {
         for (Artifact artifact : artifacts) {
            if (diag.isNoneSelected())
               artifact.clearSoleAttributeValue(attributeName);
            else
               artifact.setSoleAttributeValue(attributeName, diag.getSelectedDate().getTime() + "");
            if (persist) artifact.persistAttributes();
         }
      }
      return true;
   }

   public static boolean promptChangeEnumeratedAttribute(String attributeName, String displayName, Artifact artifact, boolean persist) throws SQLException {
      return promptChangeEnumeratedAttribute(attributeName, displayName, Arrays.asList(new Artifact[] {artifact}),
            persist);
   }

   public static boolean promptChangeEnumeratedAttribute(String attributeName, String displayName, final Collection<? extends Artifact> artifacts, boolean persist) throws SQLException {
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
         for (Object obj : diag.getResult())
            selected.add((String) obj);
         for (Artifact artifact : artifacts) {
            Set<String> current = artifact.getAttributesToStringCollection(attributeName);
            if (diag.getSelected() == Selection.AddSelection) {
               current.addAll(selected);
               artifact.setDamAttributes(attributeName, current);
            } else if (diag.getSelected() == Selection.DeleteSelected) {
               current.removeAll(selected);
               artifact.setDamAttributes(attributeName, current);
            } else if (diag.getSelected() == Selection.ReplaceAll) {
               artifact.setDamAttributes(attributeName, selected);
            } else {
               AWorkbench.popup("ERROR", "Unhandled selection type => " + diag.getSelected().name());
               return false;
            }
            if (persist) artifact.persistAttributes();
         }
      }
      return true;
   }

   public static boolean promptChangeStringAttribute(String attributeName, String displayName, String validationRegEx, final Collection<? extends Artifact> smas, boolean persist) throws SQLException {
      EntryDialog ed =
            new EntryDialog(Display.getCurrent().getActiveShell(), "Enter " + displayName, null,
                  "Enter " + displayName, MessageDialog.QUESTION, new String[] {"OK", "Clear", "Cancel"}, 0);
      if (smas.size() == 1) ed.setEntry(smas.iterator().next().getSoleAttributeValue(attributeName));
      if (validationRegEx != null) ed.setValidationRegularExpression(validationRegEx);
      int result = ed.open();
      if (result == 0 || result == 1) {
         for (Artifact sma : smas) {
            if (result == 0)
               sma.setSoleAttributeValue(attributeName, ed.getEntry());
            else
               sma.setSoleAttributeValue(attributeName, "");
            if (persist) sma.persistAttributes();
         }
         return true;
      }
      return false;
   }

   public static boolean promptChangeBoolean(String attributeName, String displayName, final Artifact artifact, String toggleMessage, boolean persist) throws SQLException {
      return promptChangeBoolean(attributeName, displayName, Arrays.asList(new Artifact[] {artifact}), toggleMessage,
            persist);
   }

   public static boolean promptChangeBoolean(String attributeName, String displayName, final Collection<? extends Artifact> smas, String toggleMessage, boolean persist) throws SQLException {
      boolean set = false;
      if (smas.size() == 1) set = smas.iterator().next().getSoleBooleanAttributeValue(attributeName);
      MessageDialogWithToggle md =
            new MessageDialogWithToggle(Display.getCurrent().getActiveShell(), displayName, null, displayName,
                  MessageDialog.QUESTION, new String[] {"Ok", "Cancel"}, MessageDialog.OK,
                  toggleMessage != null ? toggleMessage : displayName, set);

      int result = md.open();
      if (result == 256) {
         for (Artifact sma : smas) {
            sma.setSoleBooleanAttributeValue(attributeName, md.getToggleState());
            if (persist) sma.persistAttributes();
         }
         return true;
      }
      return false;
   }

}
