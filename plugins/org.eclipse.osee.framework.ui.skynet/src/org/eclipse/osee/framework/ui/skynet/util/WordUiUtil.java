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

import java.util.Collection;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.util.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.render.RenderingUtil;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Megumi Telles
 */
public final class WordUiUtil {

   public static void displayUnknownGuids(Artifact artifact, Collection<String> unknownGuids) {
      if (Conditions.hasValues(unknownGuids)) {
         String invalidLinkMessage = "";
         for (String unknownGuid : unknownGuids) {
            invalidLinkMessage +=
               "\t\nInvalid Link: artifact with guid: " + unknownGuid + " does not exist on this branch.";
         }
         displayUnhandledArtifacts(java.util.Collections.singleton(artifact),
            String.format("\nThe following referenced GUIDs cannot be found:  \n\n%s", invalidLinkMessage));
      }
   }

   public static void displayUnhandledArtifacts(final Collection<Artifact> artifacts, final String warningString) {
      if (!artifacts.isEmpty()) {
         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               XResultData rd = new XResultData(false);
               rd.warning("\nYou chose to preview/edit artifacts that could not be handled: ");
               rd.log(warningString + "\n");
               rd.addRaw(AHTML.beginMultiColumnTable(60, 1));
               rd.addRaw(AHTML.addHeaderRowMultiColumnTable(new String[] {"Artifact Name", "GUID"}));
               for (Artifact artifact : artifacts) {
                  try {
                     rd.addRaw(AHTML.addRowMultiColumnTable(
                        new String[] {artifact.toString(), XResultDataUI.getHyperlink(artifact)}));
                  } catch (OseeCoreException ex) {
                     OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
                  }
               }
               rd.addRaw(AHTML.endMultiColumnTable());
               if (RenderingUtil.arePopupsAllowed()) {
                  XResultDataUI.report(rd, "Artifact Warning");
               } else {
                  OseeLog.logf(Activator.class, Level.INFO, "Test - Skip Unhandled Artifacts Report - %s - [%s]",
                     warningString, artifacts);
               }
            }
         });
      }
   }

   public static void displayErrorMessage(String errorMessage) {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            int startIndex = errorMessage.indexOf("errorMessage=");
            String err = "";
            if (startIndex > 0) {
               startIndex = startIndex + 13; // add 13 so 'errorMessage=' is not part of the text display
               err = errorMessage.substring(startIndex, errorMessage.indexOf(",", startIndex));
            } else {
               err = errorMessage;
            }
            if (err == null || err.isEmpty()) {
               err = errorMessage;
            }

            if (RenderingUtil.arePopupsAllowed()) {
               XResultData rd = new XResultData(false);
               rd.addRaw(err);
               XResultDataUI.report(rd, "Artifact Word Content - Failed To Parse");
            } else {
               OseeLog.logf(Activator.class, Level.SEVERE, err);
            }

         }
      });
   }

   public static IVbaDiffGenerator createScriptGenerator(boolean merge, boolean show, boolean detectFormatChanges, boolean executeVbScript, boolean skipErrors, boolean diffFieldCodes) {
      return new VbaWordDiffGenerator(merge, show, detectFormatChanges, executeVbScript, skipErrors, diffFieldCodes);
   }
}