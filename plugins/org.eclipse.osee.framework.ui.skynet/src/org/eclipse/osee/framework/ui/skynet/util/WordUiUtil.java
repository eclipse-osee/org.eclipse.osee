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

package org.eclipse.osee.framework.ui.skynet.util;

import java.util.Collection;
import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.result.Manipulations;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
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

   private static final XResultData storedRd = new XResultData(false);

   public static void displayUnknownGuids(Artifact artifact, Collection<String> unknownGuids) {
      if (Conditions.hasValues(unknownGuids)) {
         String invalidLinkMessage = "";
         for (String unknownGuid : unknownGuids) {
            invalidLinkMessage +=
               "\t\nInvalid Link: artifact with guid: " + unknownGuid + " does not exist on this branch.";
         }
         displayUnhandledArtifact(artifact,
            String.format("\nThe following referenced GUIDs cannot be found:  \n\n%s", invalidLinkMessage));
      }
   }

   public static void displayUnhandledArtifact(final Artifact artifact, final String warningString) {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            XResultData rd = createUnhandledArtifactsReport(java.util.Collections.singleton(artifact), warningString);
            rd.log("\n\n");
            storedRd.combine(rd);
            if (!RenderingUtil.arePopupsAllowed()) {
               OseeLog.logf(Activator.class, Level.INFO, "Test - Skip Unhandled Artifacts Report - %s - [%s]",
                  warningString, artifact);
            }
         }
      });
   }

   public static void displayUnhandledArtifacts(final Collection<Artifact> artifacts, final String warningString) {
      if (!artifacts.isEmpty()) {
         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               XResultData rd = createUnhandledArtifactsReport(artifacts, warningString);
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

   public static XResultData createUnhandledArtifactsReport(final Collection<Artifact> artifacts,
      final String warningString) {
      XResultData rd = new XResultData(false);
      rd.warning("\nYou chose to preview/edit artifacts that could not be handled: ");
      rd.log(warningString + "\n");
      rd.addRaw(AHTML.beginMultiColumnTable(60, 1));
      rd.addRaw(AHTML.addHeaderRowMultiColumnTable(new String[] {"Artifact Name", "GUID"}));
      for (Artifact artifact : artifacts) {
         try {
            rd.addRaw(
               AHTML.addRowMultiColumnTable(new String[] {artifact.toString(), XResultDataUI.getHyperlink(artifact)}));
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
      rd.addRaw(AHTML.endMultiColumnTable());
      return rd;
   }

   public static void displayErrorMessage(Artifact artifact, String errorMessage) {
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
            if (err.isEmpty()) {
               err = errorMessage;
            }

            if (RenderingUtil.arePopupsAllowed()) {
               XResultData rd = new XResultData(false);
               rd.setTitle("Artifact Word Content - Failed To Parse");
               rd.addRaw(String.format("Error: There was a problem parsing content for the artifact %s\n\n",
                  artifact.toStringWithId()));
               rd.addRaw(String.format("Error Stacktrace: %s", err));
               XResultDataUI.report(rd, rd.getTitle(), Manipulations.ERROR_RED, Manipulations.CONVERT_NEWLINES);
            } else {
               OseeLog.logf(Activator.class, Level.SEVERE, err);
            }
         }
      });
   }

   public static void getStoredResultData() {
      if (!storedRd.isEmpty()) {
         if (RenderingUtil.arePopupsAllowed()) {
            XResultDataUI.report(storedRd, "Artifact Warning");
         }
         storedRd.clear();
      }
   }

   public static IVbaDiffGenerator createScriptGenerator(boolean merge, boolean show, boolean detectFormatChanges,
      boolean executeVbScript, boolean skipErrors, boolean diffFieldCodes) {
      return new VbaWordDiffGenerator(merge, show, detectFormatChanges, executeVbScript, skipErrors, diffFieldCodes);
   }
}