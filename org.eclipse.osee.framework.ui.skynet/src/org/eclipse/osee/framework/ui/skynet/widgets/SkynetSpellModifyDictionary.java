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

import java.util.HashSet;
import java.util.Set;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.IOseeDictionary;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class SkynetSpellModifyDictionary implements XTextSpellModifyDictionary, IOseeDictionary {

   private static String ATTRIBUTE_NAME = "Dictionary";
   private static Set<String> dictionary;
   private final boolean debug = false;

   public boolean addToGlobalDictionary(String word) {
      try {
         return updateArtifact("Global", word, OseeSystemArtifacts.getGlobalPreferenceArtifact());
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   public boolean addToLocalDictionary(String word) {
      try {
         return updateArtifact("Local", word, UserManager.getUser());
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         return false;
      }
   }

   private boolean updateArtifact(String type, String word, Artifact art) {
      if (MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), "Add to " + type + " Dictionary",
            "Add \"" + word + "\" to " + type + " Dictionary")) {
         try {
            Set<String> words = new HashSet<String>();
            for (String str : art.getSoleAttributeValue(ATTRIBUTE_NAME, "").split(";")) {
               words.add(str);
            }
            words.add(word);
            art.setSoleAttributeValue(ATTRIBUTE_NAME, Collections.toString(";", words));
            art.persist();
            loadDictionary(true);
            return true;
         } catch (Exception ex) {
            OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
      return false;
   }

   public boolean isWord(String word) {
      loadDictionary(false);
      boolean contains = dictionary.contains(word);
      if (debug) {
         System.out.println("Checking OSEESpell => \"" + word + "\" " + contains);
      }
      if (debug) {
         System.out.println("OSEESpellDict => " + Collections.toString(",", dictionary));
      }
      return contains;
   }

   public void loadDictionary(boolean force) {
      if (!force && dictionary != null) {
         return;
      }
      try {
         dictionary = new HashSet<String>();
         User user = UserManager.getUser();
         if (user != null) {
            String value = user.getSoleAttributeValue(ATTRIBUTE_NAME, "");
            if (value != null) {
               String[] entries = value.split(";");
               for (String str : entries) {
                  if (debug) {
                     System.out.println("Adding Local => \"" + str + "\"");
                  }
                  if (str != null && !str.equals("")) {
                     dictionary.add(str);
                  }
               }
               for (String str : OseeSystemArtifacts.getGlobalPreferenceArtifact().getSoleAttributeValue(
                     ATTRIBUTE_NAME, "").split(";")) {
                  if (debug) {
                     System.out.println("Adding Global => \"" + str + "\"");
                  }
                  if (str != null && !str.equals("")) {
                     dictionary.add(str);
                  }
               }
            }
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }
}
