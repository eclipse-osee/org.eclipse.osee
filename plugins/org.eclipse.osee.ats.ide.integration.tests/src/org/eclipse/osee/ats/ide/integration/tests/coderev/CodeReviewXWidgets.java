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

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * Code review for all XX Widget files. Validates coding standards:
 * <ul>
 * <li>Widget should have @Component(service = XWidget.class) annotation</li>
 * <li>Widget should have WidgetId ID that matches class name</li>
 * <li>Widget should have default no-arg constructor for OSGi</li>
 * <li>Only one constructor should call super(); others chain via this()</li>
 * <li>ATS widgets should have xxWid (XXWorkItemData) field</li>
 * <li>setArtifact should not call refresh() (called automatically)</li>
 * <li>setArtifact should not call setSelected() (use initializeSelected)</li>
 * <li>initializeSelected should use setSelected(value, false)</li>
 * <li>Widget should not directly use XWidgetUtility.setStatus</li>
 * <li>Widget should not override validate(); override getValidator() instead</li>
 * <li>Widget should not override getSelected()</li>
 * <li>Abstract classes must start with XAbstract or XXAbstract</li>
 * <li>No System.err/out print statements</li>
 * <li>No TBD markers</li>
 * </ul>
 *
 * @author Donald G. Dunne
 */
public class CodeReviewXWidgets extends AbstractCodeReview {

   /** Matches a class that overrides validate(). Compiled once and reused per file. */
   private static final Pattern OVERRIDES_VALIDATE =
      Pattern.compile("(?s).*@Override[\\s\\n]*.*void validate\\(\\).*");

   @Override
   protected String getReviewName() {
      return "CodeReviewXWidgets";
   }

   @Override
   protected List<String> getChecksPerformed() {
      return Arrays.asList( //
         "@Component(service = XWidget.class) annotation present", "WidgetId ID field matches class name",
         "Default no-arg constructor exists (for OSGi)", "Only one constructor calls super(); others chain via this()",
         "super/this uses ID constant, not WidgetId.XXX directly", "ATS widgets have xxWid (XXWorkItemData) field",
         "setArtifact() does not call refresh() or setSelected()",
         "initializeSelected() uses setSelected(value, false)", "No direct XWidgetUtility.setStatus usage",
         "No validate() override — use getValidator()", "No getSelected() override",
         "Abstract classes named XAbstract* or XXAbstract*", "Concrete class name does not contain 'Abstract'",
         "XX naming: extends XX class must be named XX*, and vice versa",
         "No 'Dam' in name (retired pattern, use 'Art')", "No 'Default Label' leftover strings",
         "No System.err/out.print statements", "No TBD/TODO markers",
         "Copyright header present (org.eclipse.osee bundles only)",
         "Distribution statement present (org.eclipse.osee bundles only)",
         "Matching OSGI-INF XML file exists and is valid");
   }

   @Override
   protected String getFilePattern() {
      return "X.*Widget\\.java";
   }

   @Override
   protected List<String> getIgnoreDirs() {
      return Arrays.asList("admin", ".git", ".postgresql.", ".help.", ".support.", "web", "nebula",
         ".integration.tests.", "\\target\\", "\\bin");
   }

   @Override
   protected void performReview(List<String> files) {
      int x = 0;
      for (String widgetFile : files) {
         x++;
         validateWidget(widgetFile);
         if (breakAfterFile != null && x == breakAfterFile) {
            break;
         }
      }
   }

   private void validateWidget(String filePath) {
      String contents = readFile(filePath);
      if (Strings.isInvalid(contents)) {
         return;
      }
      FileResult fr = startFile(filePath);
      String className = getClassName(filePath);
      boolean isAbstract = contents.contains("public abstract class");
      boolean isAtsWidget = filePath.contains(".ats.");

      String widgetId = extractWidgetId(contents);
      boolean hasOsgiComponent = contents.contains("@Component(service = XWidget.class");
      boolean hasDefaultConstructor = contents.contains("public " + className + "()");
      boolean hasXxWid = contents.contains("xxWid") || contents.contains("XXWorkItemData");
      // Widgets in search packages or that extend button/viewer widgets don't need xxWid
      boolean isSearchWidget = filePath.contains("\\search\\");
      boolean isButtonWidget =
         contents.contains("extends XXButton") || contents.contains("extends XXAbstractOpenSprint");
      boolean isViewerWidget = contents.contains(
         "Viewer") && (contents.contains("XViewer") || contents.contains("extends X") && contents.contains("Widget"));
      boolean xxWidNotApplicable =
         isSearchWidget || isButtonWidget || isViewerWidget || contents.contains("CodeReview.IgnoreXxWid");
      boolean setArtifactCallsRefresh = checkMethodBodyContains(contents, "void setArtifact(", "refresh()");
      boolean setArtifactCallsSetSelected = checkMethodBodyContains(contents, "void setArtifact(", "setSelected(");
      boolean initSelectedBadPattern = checkInitializeSelectedBadPattern(contents);
      boolean usesSetStatus = contents.contains("XWidgetUtility.setStatus");
      boolean overridesValidate = OVERRIDES_VALIDATE.matcher(contents).matches();
      boolean overridesGetSelected = checkOverridesGetSelected(contents);
      boolean hasTbd = contents.contains("TBD") || contents.contains("TODO");
      boolean hasSysOut = contents.contains("System.err.print") || contents.contains("System.out.print");
      boolean hasDamInName = className.contains("Dam");
      boolean hasDefaultLabel = contents.contains("\"Default Label\"");
      boolean hasCopyright = contents.contains("Copyright");
      boolean extendsXX = contents.contains("extends XX");
      boolean usesFullWidgetIdInConstructor = contents.contains("super(WidgetId") || contents.contains("this(WidgetId");
      int superCallCount = countSuperConstructorCalls(contents);
      boolean idInConstructor = contents.contains("super(ID,") || contents.contains("super(ID)") || contents.contains(
         "this(ID,") || contents.contains("this(ID)");

      if (isAbstract) {
         checkAbstractClass(fr, className, widgetId, idInConstructor || contents.contains("super(widgetId"));
      } else {
         checkConcreteClass(fr, className, contents, widgetId, hasOsgiComponent, hasDefaultConstructor, isAtsWidget,
            hasXxWid, xxWidNotApplicable, setArtifactCallsRefresh, setArtifactCallsSetSelected, initSelectedBadPattern,
            usesSetStatus, overridesValidate, overridesGetSelected, hasTbd, hasSysOut, superCallCount, idInConstructor,
            hasDamInName, hasDefaultLabel, hasCopyright, extendsXX, usesFullWidgetIdInConstructor);
         checkOsgiXml(fr, filePath, className);
      }
   }

   private void checkAbstractClass(FileResult fr, String className, String widgetId, boolean isWidgetIdInConstructor) {
      // XWidget is the root base class - skip constructor chaining checks
      if (className.equals("XWidget")) {
         return;
      }
      if (!isWidgetIdInConstructor) {
         fr.errorf("Abstract: at least one constructor should have super(widgetId, or this(widgetId, [%s]\n",
            className);
      }
      if (Strings.isValid(widgetId)) {
         fr.errorf("Abstract class should not have a \"WidgetId ID\" field [%s]\n", className);
      }
      if (!className.equals("XWidget") && !className.startsWith("XAbstract") && !className.startsWith("XXAbstract")) {
         fr.errorf("Abstract class should start with XAbstract or XXAbstract [%s]\n", className);
      }
   }

   private void checkConcreteClass(FileResult fr, String className, String contents, String widgetId,
      boolean hasOsgiComponent, boolean hasDefaultConstructor, boolean isAtsWidget, boolean hasXxWid,
      boolean xxWidNotApplicable, boolean setArtifactCallsRefresh, boolean setArtifactCallsSetSelected,
      boolean initSelectedBadPattern, boolean usesSetStatus, boolean overridesValidate, boolean overridesGetSelected,
      boolean hasTbd, boolean hasSysOut, int superCallCount, boolean idInConstructor, boolean hasDamInName,
      boolean hasDefaultLabel, boolean hasCopyright, boolean extendsXX, boolean usesFullWidgetIdInConstructor) {

      // Name should not contain "Abstract" for concrete class
      if (className.contains("Abstract")) {
         fr.errorf("Concrete class name contains 'Abstract' [%s] - Remove or make class abstract\n", className);
      }
      // "Dam" retired check
      if (hasDamInName) {
         fr.errorf("'Dam' in name is retired [%s] - Replace with 'Art'\n", className);
      }
      // XX naming convention
      if (extendsXX && !className.startsWith("XX")) {
         fr.errorf("Extends XX widget but not named XX* [%s]\n", className);
      }
      if (!extendsXX && className.startsWith("XX") && !className.equals("XXAbstractBaseXWidget")) {
         fr.warningf("Named XX* but does not extend XX widget [%s]\n", className);
      }
      if (!hasOsgiComponent) {
         fr.errorf("Missing @Component(service = XWidget.class) annotation [%s]\n", className);
      }
      if (Strings.isInvalid(widgetId)) {
         fr.errorf("Missing \"WidgetId ID = \" field [%s]\n", className);
      } else if (!className.equals(widgetId)) {
         fr.errorf("Widget Id [%s] does not match class name [%s]\n", widgetId, className);
      }
      if (!hasDefaultConstructor) {
         fr.errorf("Missing default no-arg constructor [%s]\n", className);
      }
      if (superCallCount > 1) {
         fr.errorf("Multiple constructors call super() [%s] - Only one should; others chain via this()\n", className);
      }
      if (!idInConstructor) {
         fr.errorf("At least one constructor should have super(ID, or this(ID, [%s]\n", className);
      }
      if (usesFullWidgetIdInConstructor) {
         fr.errorf("super/this uses WidgetId.XXX directly [%s] - Use local ID constant instead\n", className);
      }
      if (isAtsWidget && !hasXxWid && !xxWidNotApplicable) {
         fr.warningf("ATS widget missing xxWid (XXWorkItemData) field [%s]\n", className);
      }
      if (setArtifactCallsRefresh) {
         fr.errorf("setArtifact() calls refresh() [%s] - refresh is called automatically\n", className);
      }
      if (setArtifactCallsSetSelected) {
         fr.errorf("setArtifact() calls setSelected() [%s] - Use initializeSelected() instead\n", className);
      }
      if (initSelectedBadPattern) {
         fr.errorf("initializeSelected() calls setSelected() without boolean [%s] - Use setSelected(value, false)\n",
            className);
      }
      if (usesSetStatus) {
         fr.errorf("Widget uses XWidgetUtility.setStatus [%s] - Use built-in validation framework\n", className);
      }
      if (overridesValidate) {
         fr.errorf("Widget overrides validate() [%s] - Override getValidator() instead\n", className);
      }
      if (overridesGetSelected) {
         fr.errorf("Widget overrides getSelected() [%s] - Use setSelected/initializeSelected instead\n", className);
      }
      if (hasDefaultLabel) {
         fr.errorf("Contains 'Default Label' leftover [%s] - Replace with actual label\n", className);
      }
      if (!hasCopyright && fr.getFilePath().contains("org.eclipse.osee")) {
         fr.errorf("Missing copyright header [%s]\n", className);
      }
      if (fr.getFilePath().contains("org.eclipse.osee") && !contents.contains("Eclipse Public License")) {
         fr.errorf("Missing distribution statement (Eclipse Public License) [%s]\n", className);
      }
      if (hasTbd) {
         fr.warningf("Widget contains TBD/TODO marker [%s]\n", className);
      }
      if (hasSysOut) {
         fr.errorf("Widget contains System.err/out.print [%s] - Use OseeLog or remove\n", className);
      }
   }

   private void checkOsgiXml(FileResult fr, String filePath, String className) {
      String osgiXmlPath = findOsgiXmlForClass(filePath);
      if (osgiXmlPath == null) {
         fr.errorf("No matching OSGI-INF XML file found for [%s]\n", className);
      } else if (!isValidXml(osgiXmlPath)) {
         fr.errorf("OSGI-INF XML file is malformed for [%s]: %s\n", className, shortPath(osgiXmlPath));
      }
   }

   private String extractWidgetId(String contents) {
      // contents already normalized by readFile(); split directly.
      String[] lines = contents.split("\n");
      for (String line : lines) {
         if (line.contains("WidgetId ID =") || line.contains("WidgetId ID=")) {
            // Extract the value after the = sign, strip prefix like WidgetIdXxx.
            String id = line.replaceFirst("^.*=\\s*", "").replaceFirst(";.*$", "").trim();
            // Remove any qualifier prefix (e.g., WidgetIdAms.XXWidget -> XXWidget)
            if (id.contains(".")) {
               id = id.substring(id.lastIndexOf('.') + 1);
            }
            return id;
         }
      }
      return "";
   }

   private boolean checkMethodBodyContains(String contents, String methodSignature, String searchStr) {
      String[] lines = contents.split("\n");
      boolean inMethod = false;
      int braceCount = 0;
      for (String line : lines) {
         if (line.contains(methodSignature)) {
            inMethod = true;
            braceCount = 0;
         }
         if (inMethod) {
            braceCount += countChar(line, '{') - countChar(line, '}');
            if (line.contains(searchStr) && !line.trim().startsWith("//") && !isIgnored(line)) {
               return true;
            }
            if (braceCount <= 0 && inMethod && line.contains("}")) {
               inMethod = false;
            }
         }
      }
      return false;
   }

   private boolean checkInitializeSelectedBadPattern(String contents) {
      String[] lines = contents.split("\n");
      boolean inMethod = false;
      int braceCount = 0;
      for (String line : lines) {
         if (line.contains("initializeSelected()") && (line.contains("@Override") || line.contains(
            "protected") || line.contains("void"))) {
            inMethod = true;
            braceCount = 0;
         }
         if (inMethod) {
            braceCount += countChar(line, '{') - countChar(line, '}');
            if (line.contains("setSelected(") && !line.contains(", false)") && !line.contains(
               ", true)") && !line.trim().startsWith("//") && !isIgnored(line)) {
               return true;
            }
            if (braceCount <= 0 && inMethod && line.contains("}")) {
               inMethod = false;
            }
         }
      }
      return false;
   }

   private boolean checkOverridesGetSelected(String contents) {
      String[] lines = contents.split("\n");
      for (int i = 0; i < lines.length - 1; i++) {
         if (lines[i].trim().startsWith("@Override")) {
            // Check next non-blank line for getSelected() method declaration
            for (int j = i + 1; j < lines.length && j <= i + 3; j++) {
               String next = lines[j].trim();
               if (next.isEmpty()) {
                  continue;
               }
               if (next.contains("getSelected(") && next.contains("Collection")) {
                  return true;
               }
               break;
            }
         }
      }
      return false;
   }

   private int countSuperConstructorCalls(String contents) {
      int count = 0;
      int braceDepth = 0;
      for (String line : contents.split("\n")) {
         String trimmed = line.trim();
         // Count braces to track depth — outer class body is depth 1
         for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == '{') {
               braceDepth++;
            } else if (line.charAt(i) == '}') {
               braceDepth--;
            }
         }
         // super( at brace depth 2 means we're inside a method in the outer class (depth 1 = class body, 2 = method body)
         // Inner class methods would be at depth 3+
         if (trimmed.startsWith("super(") && !trimmed.startsWith("//") && braceDepth <= 2) {
            count++;
         }
      }
      return count;
   }

   private int countChar(String line, char c) {
      int count = 0;
      for (int i = 0; i < line.length(); i++) {
         if (line.charAt(i) == c) {
            count++;
         }
      }
      return count;
   }

   public static void main(String[] args) {
      new CodeReviewXWidgets().run();
   }

}
