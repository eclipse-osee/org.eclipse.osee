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
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactURL;
import org.eclipse.osee.framework.skynet.core.preferences.PreferenceConstants;

/**
 * @author Jeff C. Phillips
 */
public class HTMLTransferFormatter {
   private static final String START = "<A href=\"";
   private static final String END = "</A>";
   private static IPreferenceStore prefStore = SkynetGuiPlugin.getInstance().getPreferenceStore();

   public static String getHtml(Artifact... artifacts) {
      boolean applyWordTagWrap =
            prefStore.getString(PreferenceConstants.WORDWRAP_KEY) != null && prefStore.getString(
                  PreferenceConstants.WORDWRAP_KEY).equals(IPreferenceStore.TRUE);

      if (artifacts != null) {
         StringBuilder sb = new StringBuilder();

         if (applyWordTagWrap) {
            sb.append("Version:1.0\r\nStartHTML:2\r\nEndHTML:170\r\nStartFragment:140\r\nEndFragment:160\r\n" + "StartSelection:140\r\nEndSelection:160\r\n<HTML><HEAD><TITLE> The HTML Clipboard</TITLE></HEAD><BODY>\r\n" + "<!--StartFragment -->\r\n");
         }

         List<String> urls = new ArrayList<String>(artifacts.length);
         for (Artifact artifact : artifacts) {
            String link = null;
            try {
               link = ArtifactURL.getOpenInOseeLink(artifact).toString();
            } catch (OseeCoreException ex) {
               link =
                     String.format("guid:[%s] branch:[%s] gammaId:[%s]", artifact.getGuid(),
                           artifact.getBranch().getBranchId(), artifact.getGammaId());
               OseeLog.log(SkynetGuiPlugin.class, Level.WARNING, String.format("Error creating link for: [%s]",
                     artifact), ex);
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
