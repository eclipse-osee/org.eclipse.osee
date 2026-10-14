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

import java.util.ArrayList;
import java.util.List;

/**
 * Reorganizes all {@code *Blam.java} files (concrete and, when {@link #includeAbstract} is true, abstract bases) into
 * the standard BLAM member order. Emitted order:
 * <ol>
 * <li>Package + Imports (untouched)</li>
 * <li>Class javadoc + annotations + class declaration (untouched)</li>
 * <li>BLAM_NAME constant, BLAM_DESC constant, label constants, other static fields, instance fields (no headers)</li>
 * <li>Constructor(s)</li>
 * <li>getXWidgetItems() + widgetCreated() (Widgets)</li>
 * <li>runOperation() / createOperation() (Operation)</li>
 * <li>Helper methods</li>
 * <li>getCategories() + getDescriptionUsage() (Categories &amp; Description, always last)</li>
 * </ol>
 * <p>
 * Discovery is filesystem-based across every repo under the workspace root: files whose name ends with
 * {@code Blam.java} (but not {@code BlamTest.java}). A file is organized only if it extends a {@code Blam} type. See
 * {@link AbstractCodeClean} for the shared pipeline.
 *
 * @author Donald G. Dunne
 */
public class CodeCleanOrganizeBlams extends AbstractCodeClean {

   public CodeCleanOrganizeBlams() {
      // Report only by default; set true manually when ready to make changes.
      changeFiles = false;
   }

   @Override
   protected String getCleanName() {
      return "CodeCleanOrganizeBlams";
   }

   @Override
   protected String getFilePattern() {
      // Matches *Blam.java production classes; *BlamTest.java ends in Test.java so it is excluded automatically.
      return ".*Blam\\.java";
   }

   @Override
   protected boolean shouldProcess(String contents, String justFilename) {
      // Abstract Blams: only process when includeAbstract is on.
      if (contents.contains("public abstract class") && !includeAbstract) {
         rd.logf("Skipping abstract class [%s]\n", justFilename);
         return false;
      }
      // Must be a Blam subclass (extends something with Blam in the name)
      if (!contents.contains("extends") || !contents.contains("Blam")) {
         rd.logf("Skipping non-Blam class [%s]\n", justFilename);
         return false;
      }
      return true;
   }

   @Override
   protected String rebuildBody(String justFilename, String header, List<Member> members) {
      List<Member> blamNameField = new ArrayList<>();
      List<Member> blamDescField = new ArrayList<>();
      List<Member> labelConstants = new ArrayList<>();
      List<Member> otherStaticFields = new ArrayList<>();
      List<Member> instanceFields = new ArrayList<>();
      List<Member> constructors = new ArrayList<>();
      List<Member> getXWidgetItems = new ArrayList<>();
      List<Member> widgetCreated = new ArrayList<>();
      List<Member> runOperation = new ArrayList<>();
      List<Member> helperMethods = new ArrayList<>();
      List<Member> getCategories = new ArrayList<>();
      List<Member> getDescriptionUsage = new ArrayList<>();

      for (Member m : members) {
         categorize(m, justFilename, blamNameField, blamDescField, labelConstants, otherStaticFields, instanceFields,
            constructors, getXWidgetItems, widgetCreated, runOperation, helperMethods, getCategories,
            getDescriptionUsage);
      }

      StringBuilder result = new StringBuilder();
      result.append(header);
      result.append("\n");

      for (Member m : blamNameField) {
         result.append(m.text).append("\n");
      }
      for (Member m : blamDescField) {
         result.append(m.text).append("\n");
      }
      for (Member m : labelConstants) {
         result.append(m.text).append("\n");
      }
      for (Member m : otherStaticFields) {
         result.append(m.text).append("\n");
      }
      if (!blamNameField.isEmpty() || !blamDescField.isEmpty() || !labelConstants.isEmpty() || !otherStaticFields.isEmpty()) {
         result.append("\n");
      }

      for (Member m : instanceFields) {
         result.append(m.text).append("\n");
      }
      if (!instanceFields.isEmpty()) {
         result.append("\n");
      }

      if (!constructors.isEmpty()) {
         result.append(String.format(SECTION_FORMAT, "Constructors")).append("\n\n");
         for (Member m : constructors) {
            result.append(m.text).append("\n\n");
         }
      }

      if (!getXWidgetItems.isEmpty() || !widgetCreated.isEmpty()) {
         result.append(String.format(SECTION_FORMAT, "Widgets")).append("\n\n");
         for (Member m : getXWidgetItems) {
            result.append(m.text).append("\n\n");
         }
         for (Member m : widgetCreated) {
            result.append(m.text).append("\n\n");
         }
      }

      if (!runOperation.isEmpty()) {
         result.append(String.format(SECTION_FORMAT, "Operation")).append("\n\n");
         for (Member m : runOperation) {
            result.append(m.text).append("\n\n");
         }
      }

      if (!helperMethods.isEmpty()) {
         result.append(String.format(SECTION_FORMAT, "Helpers")).append("\n\n");
         for (Member m : helperMethods) {
            result.append(m.text).append("\n\n");
         }
      }

      if (!getCategories.isEmpty() || !getDescriptionUsage.isEmpty()) {
         result.append(String.format(SECTION_FORMAT, "Categories & Description")).append("\n\n");
         for (Member m : getCategories) {
            result.append(m.text).append("\n\n");
         }
         for (Member m : getDescriptionUsage) {
            result.append(m.text).append("\n\n");
         }
      }

      result.append("}\n");
      return result.toString();
   }

   private void categorize(Member m, String className, List<Member> blamNameField, List<Member> blamDescField,
      List<Member> labelConstants, List<Member> otherStaticFields, List<Member> instanceFields,
      List<Member> constructors, List<Member> getXWidgetItems, List<Member> widgetCreated, List<Member> runOperation,
      List<Member> helperMethods, List<Member> getCategories, List<Member> getDescriptionUsage) {

      String text = m.text;
      String trimmedFirst = getFirstCodeLine(text);

      // Constructors - check before fields since some field heuristics can match constructor lines
      if (trimmedFirst.contains(className + "(") && !trimmedFirst.contains("=")) {
         constructors.add(m);
         return;
      }

      if (isField(text)) {
         if (text.contains("BLAM_NAME")) {
            blamNameField.add(m);
         } else if (text.contains("BLAM_DESC")) {
            blamDescField.add(m);
         } else if (text.contains("static ") && text.contains("final ") && text.contains("String ")) {
            labelConstants.add(m);
         } else if (text.contains("static ")) {
            otherStaticFields.add(m);
         } else {
            instanceFields.add(m);
         }
         return;
      }

      if (trimmedFirst.contains("getXWidgetItems(")) {
         getXWidgetItems.add(m);
         return;
      }
      if (trimmedFirst.contains("widgetCreated(")) {
         widgetCreated.add(m);
         return;
      }
      if (matchesAny(trimmedFirst, "runOperation(", "createOperation(")) {
         runOperation.add(m);
         return;
      }
      if (trimmedFirst.contains("getCategories(")) {
         getCategories.add(m);
         return;
      }
      if (trimmedFirst.contains("getDescriptionUsage(")) {
         getDescriptionUsage.add(m);
         return;
      }

      helperMethods.add(m);
   }

   public static void main(String[] args) {
      new CodeCleanOrganizeBlams().run();
   }

}
