/*********************************************************************
 * Copyright (c) 2026 Boeing
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
package org.eclipse.osee.ats.ide.integration.tests.coderev;

import java.io.File;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * Shared utilities for the CodeClean and CodeReview quality tooling. Currently the home for report output (reports are
 * written under a dedicated folder in the user's home directory - {@code ~/CodeCleanOutput} or
 * {@code ~/CodeReviewOutput} - instead of cluttering the home directory itself; the folder is created on first use with
 * a README explaining what it is and that it is safe to delete). Intended to also hold any other utility methods shared
 * across the quality runs.
 *
 * @author Donald G. Dunne
 */
public class CodeQualityUtil {

   public static final String CODE_CLEAN_DIR = "CodeCleanOutput";
   public static final String CODE_REVIEW_DIR = "CodeReviewOutput";
   public static final String CODE_XWIDGET_REVIEW_DIR = "CodeXWidgetReviewCUI";

   private CodeQualityUtil() {
      // static utility
   }

   /**
    * Resolve (and create if needed) the output directory under the user's home for the given subfolder name. On
    * creation, drops a README.txt explaining the folder's purpose.
    *
    * @param outputDirName {@link #CODE_CLEAN_DIR} or {@link #CODE_REVIEW_DIR}
    * @param toolDescription short description of the tooling that writes here, used in the README
    * @return the output directory (may not exist if creation failed - caller should still attempt to write)
    */
   public static File getOutputDir(String outputDirName, String toolDescription) {
      File dir = new File(System.getProperty("user.home"), outputDirName);
      if (!dir.isDirectory()) {
         try {
            dir.mkdirs();
            writeReadme(dir, outputDirName, toolDescription);
         } catch (Exception ex) {
            // Fall through; caller handles a missing/failed dir by writing where it can.
         }
      }
      return dir;
   }

   /**
    * Resolve the full path for a named HTML report inside the output dir, creating the dir + README on first use.
    *
    * @return absolute path for {@code <outputDir>/<reportName>.html}
    */
   public static File getReportFile(String outputDirName, String toolDescription, String reportName) {
      return new File(getOutputDir(outputDirName, toolDescription), reportName + ".html");
   }

   private static void writeReadme(File dir, String outputDirName, String toolDescription) {
      File readme = new File(dir, "README.txt");
      if (readme.exists()) {
         return;
      }
      String text = "" //
         + "This folder (" + outputDirName + ") was created by the OSEE " + toolDescription + " tooling\n" //
         + "(org.eclipse.osee.ats.ide.integration.tests.coderev).\n" //
         + "\n" //
         + "It holds the generated HTML reports from running those tools. Nothing here is source-controlled or\n" //
         + "required by the build - it is purely local output.\n" //
         + "\n" //
         + "You can delete this folder (or any file in it) at any time; it will be recreated the next time a\n" //
         + "report is generated.\n";
      if (CODE_XWIDGET_REVIEW_DIR.equals(outputDirName)) {
         text += "" //
            + "\n" //
            + "TEMPORARY - this folder supports the one-time XWidget -> XXWidget migration review. Once that change\n" //
            + "has been reviewed and merged, this folder AND the migration-only source tools that produced it can be\n" //
            + "removed. Those source files (in org.eclipse.osee.ats.ide.integration.tests.coderev.packet) are:\n" //
            + "  - GenerateFullReviewPacket.java (the one entry point - runs all the below, then verifies output)\n" //
            + "  - ReportXWidgetChanges.java     (the XWidget change report generator)\n" //
            + "  - ReportBlamChanges.java        (the BLAM change report generator)\n" //
            + "  - ReportWorkDefChange.java      (the Work Definition change report generator)\n" //
            + "  - ReportXWidgetOverview.java    (the XWidget_Overview.html entry page generator)\n" //
            + "  - ReportXWidgetMapping.java     (old->new widget name mapping; superseded by the migration doc)\n" //
            + "The durable old->new mappings live in docs/ai/ide/ide-xwidget-to-xxwidget-migration.md, which is the\n" //
            + "source of truth and should be kept.\n";
      }
      try {
         Lib.writeStringToFile(text, readme);
      } catch (Exception ex) {
         // Best effort - a missing README is harmless.
      }
   }

}
