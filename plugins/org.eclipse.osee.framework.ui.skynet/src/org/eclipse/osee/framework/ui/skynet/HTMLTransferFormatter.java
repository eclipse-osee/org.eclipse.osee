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
package org.eclipse.osee.framework.ui.skynet;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.osee.framework.core.client.CoreClientConstants;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactUrlClient;
import org.eclipse.osee.framework.skynet.core.preferences.PreferenceConstants;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/**
 * @author Jeff C. Phillips
 */
public class HTMLTransferFormatter {
   private static final String START = "<A href=\"";
   private static final String END = "</A>";
   private static IPreferenceStore preferenceStore;

   @SuppressWarnings("deprecation")
   private synchronized static IPreferenceStore getPreferenceStore() {
      if (preferenceStore == null) {
         preferenceStore = new ScopedPreferenceStore(new InstanceScope(), CoreClientConstants.getBundleId());
      }
      return preferenceStore;
   }

   private static boolean isWordTagWrapEnabled() {
      String wrapKey = getPreferenceStore().getString(PreferenceConstants.WORDWRAP_KEY);
      return IPreferenceStore.TRUE.equals(wrapKey);
   }

   public static String getHtml(Artifact... artifacts) {
      boolean applyWordTagWrap = isWordTagWrapEnabled();

      if (artifacts != null) {
         StringBuilder sb = new StringBuilder();

         if (applyWordTagWrap) {
            sb.append(
               "Version:1.0\r\nStartHTML:2\r\nEndHTML:170\r\nStartFragment:140\r\nEndFragment:160\r\n" + "StartSelection:140\r\nEndSelection:160\r\n<HTML><HEAD><TITLE> The HTML Clipboard</TITLE></HEAD><BODY>\r\n" + "<!--StartFragment -->\r\n");
         }

         List<String> urls = new ArrayList<>(artifacts.length);
         for (Artifact artifact : artifacts) {
            String link = null;
            try {
               link = new ArtifactUrlClient().getOpenInOseeLink(artifact, PresentationType.SPECIALIZED_EDIT).toString();
            } catch (OseeCoreException ex) {
               link = String.format("artifactId:[%s] branch:[%s] gammaId:[%s]", artifact.getId(), artifact.getBranch(),
                  artifact.getGammaId());
               OseeLog.logf(Activator.class, Level.WARNING, ex, "Error creating link for: [%s]", artifact);
            }
            urls.add(link + "\">" + artifact.getName());
         }

         sb.append(Collections.toString(urls, START, END + ", " + START, END));

         if (applyWordTagWrap) {
            sb.append("\r\n<!--EndFragment --></BODY></HTML>");
         }

         return sb.toString();
      } else {
         throw new IllegalArgumentException("The artifact can not be null");
      }
   }
}
