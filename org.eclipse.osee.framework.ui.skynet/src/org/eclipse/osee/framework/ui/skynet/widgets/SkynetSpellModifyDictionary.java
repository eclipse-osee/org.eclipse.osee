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
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.GlobalPreferences;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.IOseeDictionary;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class SkynetSpellModifyDictionary implements XTextSpellModifyDictionary, IOseeDictionary {

   private static String ATTRIBUTE_NAME = "Dictionary";
   private static Set<String> dictionary;
   private boolean debug = false;

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XTextSpellModifyDictionary#addToGlobalDictionary(java.lang.String)
    */
   public boolean addToGlobalDictionary(String word) {
      try {
         return updateArtifact("Global", word, GlobalPreferences.get());
      } catch (Exception ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, true);
      }
      return false;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XTextSpellModifyDictionary#addToLocalDictionary(java.lang.String)
    */
   public boolean addToLocalDictionary(String word) {
      return updateArtifact("Local", word, SkynetAuthentication.getUser());
   }

   private boolean updateArtifact(String type, String word, Artifact art) {
      if (MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), "Add to " + type + " Dictionary",
            "Add \"" + word + "\" to " + type + " Dictionary")) {
         try {
            Set<String> words = new HashSet<String>();
            for (String str : art.getSoleAttributeValue(ATTRIBUTE_NAME, "").split(";"))
               words.add(str);
            words.add(word);
            art.setSoleAttributeValue(ATTRIBUTE_NAME, Collections.toString(";", words));
            art.persistAttributes();
            loadDictionary(true);
            return true;
         } catch (Exception ex) {
            OSEELog.logException(SkynetGuiPlugin.class, ex, true);
         }
      }
      return false;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.util.IOseeDictionary#isWord(java.lang.String)
    */
   public boolean isWord(String word) {
      loadDictionary(false);
      boolean contains = dictionary.contains(word);
      if (debug) System.out.println("Checking SkynetSpell => \"" + word + "\" " + contains);
      if (debug) System.out.println("SkynetSpellDict => " + Collections.toString(",", dictionary));
      return contains;
   }

   public void loadDictionary(boolean force) {
      if (!force && dictionary != null) return;
      try {
         dictionary = new HashSet<String>();
         User user = SkynetAuthentication.getUser();
         if (user != null) {
            String value = user.getSoleAttributeValue(ATTRIBUTE_NAME, "");
            if (value != null) {
               String[] entries = value.split(";");
               for (String str : entries) {
                  if (debug) System.out.println("Adding Local => \"" + str + "\"");
                  if (str != null && !str.equals("")) dictionary.add(str);
               }
               if (GlobalPreferences.get() != null) {
                  for (String str : GlobalPreferences.get().getSoleAttributeValue(ATTRIBUTE_NAME, "").split(";")) {
                     if (debug) System.out.println("Adding Global => \"" + str + "\"");
                     if (str != null && !str.equals("")) dictionary.add(str);
                  }
               }
            }
         }
      } catch (Exception ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, true);
      }
   }
}
