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
package org.eclipse.osee.ats.ide.integration.tests.coderev.scripts;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.ats.api.workdef.StateColor;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.ide.integration.tests.coderev.AbstractCodeClean;
import org.eclipse.osee.ats.ide.integration.tests.coderev.AbstractQual;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * THROWAWAY refactor script. Removes redundant {@code .andColor(StateColor.XXX)} lines from Work Definition
 * {@code build()} chains so the state color resolves from the default instead (via {@code TeamState.resolveStateColor}:
 * explicit color -&gt; {@link TeamState} default for the state name -&gt; BLACK). A {@code .andColor} line is removed
 * when its color is either the stale {@code BLACK} placeholder OR already equals the {@link TeamState} default for that
 * state's name; a non-{@code BLACK} color that differs from the default (e.g. a {@code Cancelled} state colored
 * {@code DARK_GREEN} when {@code TeamState} says {@code DARK_RED}) is kept as an intentional override.
 * <p>
 * Scope: scans {@code WorkDef*.java} files that call {@code super(AtsWorkDefinitionTokens...)} in their constructor.
 * Like the CodeClean tooling it is report-only until {@link #changeFiles} is set true, and (via {@link AbstractQual})
 * only scans the {@code org.eclipse.osee} repo unless {@code onlyOpenSource} is set false. Modeled on
 * {@link AbstractCodeClean} and reuses its discovery / reporting machinery via an overridden {@link #performClean()}.
 *
 * @author Donald G. Dunne
 */
public class CodeRefactor_UpdateWorkDefAndColors extends AbstractCodeClean {

   /** Captures the state name string from a {@code bld.andState(<n>, "<Name>", ...)} call. */
   private static final Pattern STATE_NAME = Pattern.compile("andState\\s*\\(\\s*\\d+\\s*,\\s*\"([^\"]+)\"");
   /** Captures the color from a {@code .andColor(StateColor.XXX)} call. */
   private static final Pattern AND_COLOR = Pattern.compile("\\.andColor\\s*\\(\\s*StateColor\\.(\\w+)\\s*\\)");

   public CodeRefactor_UpdateWorkDefAndColors() {
      // Report only by default; set changeFiles = true manually when ready to write.
      changeFiles = false;
      // Only run on open source repo or walk up to find man dir and all repos
      onlyOpenSource = false;
   }

   @Override
   protected String getCleanName() {
      return "CodeRefactor_UpdateWorkDefAndColors";
   }

   @Override
   protected String getFilePattern() {
      return "WorkDef.*\\.java";
   }

   /** Per-file redundant-andColor removal (overrides the member-reorganization default). */
   @Override
   protected void performClean() {
      // Force TeamState class init so its static state instances register with StateTypeAdapter before any lookup.
      TeamState.values();

      List<String> files = findFiles();
      if (files.isEmpty()) {
         rd.logf("No WorkDef files found\n");
         return;
      }
      rd.logf("Scanning %d WorkDef*.java candidate(s)\n", files.size());
      for (String filePath : files) {
         processWorkDef(filePath);
      }
   }

   private void processWorkDef(String fullFilename) {
      try {
         File file = new File(fullFilename);
         String justFilename = file.getName().replaceFirst("\\.java", "");
         String original = Lib.fileToString(file);

         // Only real Work Definitions name their token via super(AtsWorkDefinitionTokens...).
         if (!original.contains("super(AtsWorkDefinitionTokens")) {
            return;
         }

         String normalized = original.replace("\r\n", "\n").replace("\r", "\n");
         String[] lines = normalized.split("\n", -1);

         List<String> kept = new ArrayList<>();
         String currentState = null;
         int removed = 0;

         for (String line : lines) {
            Matcher stateM = STATE_NAME.matcher(line);
            if (stateM.find()) {
               currentState = stateM.group(1);
            }

            Matcher colorM = AND_COLOR.matcher(line);
            if (colorM.find() && currentState != null) {
               String lineColor = colorM.group(1);
               // Remove the .andColor line (let the default handle it) when the color is either the stale BLACK
               // placeholder OR already equals the TeamState default for this state name. Keep only a non-default,
               // non-BLACK color (an intentional override).
               if ("BLACK".equals(lineColor) || isDefaultColorForState(currentState, lineColor)) {
                  rd.logf("  [%s] state '%s': removing redundant .andColor(StateColor.%s)\n", justFilename,
                     currentState, lineColor);
                  removed++;
                  continue; // drop this line entirely
               }
            }
            kept.add(line);
         }

         if (removed == 0) {
            return;
         }

         String rebuilt = String.join("\n", kept);
         String output = rebuilt.replace("\n", "\r\n");
         if (output.equals(original)) {
            return;
         }

         if (changeFiles) {
            Lib.writeStringToFile(output, file);
            changedFiles.add(justFilename);
            rd.logf("Updated [%s] - removed %d redundant .andColor line(s)\n", justFilename, removed);
         } else {
            changedFiles.add(justFilename);
            rd.logf("Would update [%s] - %d redundant .andColor line(s)\n", justFilename, removed);
         }
      } catch (Exception ex) {
         rd.errorf("Error processing [%s]: %s\n", fullFilename, Lib.exceptionToString(ex));
      }
   }

   /** @return true if {@code lineColorName} equals the {@link TeamState} default color for the given state name. */
   private boolean isDefaultColorForState(String stateName, String lineColorName) {
      TeamState teamState = TeamState.valueOf(stateName);
      if (teamState == null || teamState.getStateColor() == null) {
         return false;
      }
      StateColor lineColor;
      try {
         lineColor = StateColor.valueOf(lineColorName);
      } catch (IllegalArgumentException ex) {
         return false;
      }
      return teamState.getStateColor() == lineColor;
   }

   public static void main(String[] args) {
      new CodeRefactor_UpdateWorkDefAndColors().run();
   }

}
