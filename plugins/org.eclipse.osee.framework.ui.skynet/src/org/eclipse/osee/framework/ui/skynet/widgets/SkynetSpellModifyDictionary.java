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

package org.eclipse.osee.framework.ui.skynet.widgets;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.util.IOseeDictionary;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Donald G. Dunne
 */
public class SkynetSpellModifyDictionary implements XTextSpellModifyDictionary, IOseeDictionary {
   private static Set<String> dictionary;
   private final boolean debug = false;

   @Override
   public boolean addToGlobalDictionary(String word) {
      try {
         return updateArtifact("Global", word, OseeSystemArtifacts.getGlobalPreferenceArtifact());
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   @Override
   public boolean addToLocalDictionary(String word) {
      try {
         return updateArtifact("Local", word, UserManager.getUser());
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         return false;
      }
   }

   private boolean updateArtifact(String type, String word, Artifact art) {
      if (MessageDialog.openConfirm(Displays.getActiveShell(), "Add to " + type + " Dictionary",
         "Add \"" + word + "\" to " + type + " Dictionary")) {
         try {
            Set<String> words = new HashSet<>();
            for (String str : art.getSoleAttributeValue(CoreAttributeTypes.Dictionary, "").split(";")) {
               words.add(str);
            }
            words.add(word);
            art.setSoleAttributeValue(CoreAttributeTypes.Dictionary, Collections.toString(";", words));
            art.persist(getClass().getSimpleName());
            loadDictionary(true);
            return true;
         } catch (Exception ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
      return false;
   }

   @Override
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
         dictionary = new HashSet<>();
         User user = UserManager.getUser();
         if (user != null) {
            String value = user.getSoleAttributeValue(CoreAttributeTypes.Dictionary, "");
            if (value != null) {
               String[] entries = value.split(";");
               for (String str : entries) {
                  if (debug) {
                     System.out.println("Adding Local => \"" + str + "\"");
                  }
                  if (Strings.isValid(str)) {
                     dictionary.add(str);
                  }
               }
               for (String str : OseeSystemArtifacts.getGlobalPreferenceArtifact().getSoleAttributeValue(
                  CoreAttributeTypes.Dictionary, "").split(";")) {
                  if (debug) {
                     System.out.println("Adding Global => \"" + str + "\"");
                  }
                  if (Strings.isValid(str)) {
                     dictionary.add(str);
                  }
               }
            }
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }
}
