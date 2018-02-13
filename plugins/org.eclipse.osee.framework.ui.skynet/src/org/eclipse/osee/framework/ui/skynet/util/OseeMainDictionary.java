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
package org.eclipse.osee.framework.ui.skynet.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.osgi.framework.Bundle;

/**
 * @author Donald G. Dunne
 */
public class OseeMainDictionary implements IOseeDictionary {
   private final Set<String> dictionary = new HashSet<>();
   private volatile boolean wasLoaded = false;

   @Override
   public boolean isWord(String word) {
      ensureDictionaryLoaded();
      return dictionary.contains(word);
   }

   private synchronized void ensureDictionaryLoaded() {
      if (!wasLoaded) {
         // open OSEE english dictionary
         BufferedReader br = null;
         try {
            Bundle bundle = Activator.getInstance().getBundle();
            URL url = bundle.getEntry("OSEE-INF/spellCheck/AllWords.txt");

            br = new BufferedReader(new InputStreamReader(url.openStream()));
            String line = null;
            while ((line = br.readLine()) != null) {
               line.replaceAll("[\t\r\n ]+$", "");
               dictionary.add(line);
            }
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         } finally {
            wasLoaded = true;
            Lib.close(br);
         }
      }
   }

}
