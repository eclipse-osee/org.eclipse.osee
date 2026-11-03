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
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * Code review for all AbstractBlam files. Validates conventions:
 * <ul>
 * <li>Must have @Component(service = AbstractBlam.class) annotation</li>
 * <li>Must use getXWidgetItems() with builder pattern, not getXWidgetsXml()</li>
 * <li>Must have BLAM_NAME static final String field</li>
 * <li>Constructor must use BLAM_NAME constant, not string literal</li>
 * <li>Must have default no-arg constructor for OSGi</li>
 * <li>Widget labels used in both getXWidgetItems and runOperation must be static final String</li>
 * <li>Should use convenience builder methods (andCheckBoxWidget, andTextWidget, etc.)</li>
 * <li>Should not use .endWidget()</li>
 * <li>Should have getDescriptionUsage()</li>
 * <li>Checkbox widgets should not add .andLabelAfter() or .andHorizLabel() (already defaults)</li>
 * </ul>
 *
 * @author Donald G. Dunne
 */
public class CodeReviewBlams extends AbstractCodeReview {

   @Override
   protected String getReviewName() {
      return "CodeReviewBlams";
   }

   @Override
   protected List<String> getChecksPerformed() {
      return Arrays.asList( //
         "@Component(service = AbstractBlam.class) annotation present",
         "Uses getXWidgetItems() with builder, not getXWidgetsXml()", "BLAM_NAME static final String field exists",
         "Constructor uses BLAM_NAME constant, not string literal",
         "Constructor passes OseeImage to super(BLAM_NAME, image)", "Default no-arg constructor exists",
         "getDescriptionUsage() method present", "No .endWidget() calls",
         "Checkbox widgets don't use redundant .andLabelAfter()/.andHorizLabel()",
         "Prefer convenience methods over generic andWidget()",
         "Widget labels used in both builder and runOperation are static final String",
         "Matching OSGI-INF XML file exists and is valid",
         "Matching test class (ClassNameTest.java) exists in same package");
   }

   @Override
   protected String getFilePattern() {
      return ".*Blam\\.java";
   }

   @Override
   protected List<String> getIgnoreDirs() {
      return Arrays.asList(".git", "\\target\\", "\\bin\\", ".integration.tests.", ".help.", ".support.", "nebula",
         "scripts");
   }

   @Override
   protected void performReview(List<String> files) {
      int x = 0;
      for (String blamFile : files) {
         x++;
         validateBlam(blamFile);
         if (breakAfterFile != null && x == breakAfterFile) {
            break;
         }
      }
   }

   private void validateBlam(String filePath) {
      String contents = readFile(filePath);
      if (Strings.isInvalid(contents)) {
         return;
      }
      String className = getClassName(filePath);

      if (contents.contains("public abstract class") || contents.contains("public interface")) {
         return;
      }
      if (!contents.contains("extends Abstract") && !contents.contains("extends RunAbstract")) {
         return;
      }

      FileResult fr = startFile(filePath);

      boolean hasOsgiComponent = contents.contains("@Component(service = AbstractBlam.class");
      boolean hasGetXWidgetItems = contents.contains("getXWidgetItems()");
      boolean hasGetXWidgetsXml = contents.contains("getXWidgetsXml()");
      boolean hasBlamName = contents.contains("BLAM_NAME");
      boolean hasDefaultConstructor = contents.contains("public " + className + "()");
      boolean hasDescriptionUsage = contents.contains("getDescriptionUsage()");
      boolean usesEndWidget = contents.contains(".endWidget()");
      boolean usesRedundantCheckboxOpts = checkRedundantCheckboxOptions(contents);
      boolean hasStringLiteralInSuper = checkStringLiteralInSuperConstructor(contents);
      boolean superWithoutImage = checkSuperWithoutImage(contents);
      boolean usesAndWidget = contents.contains(".andWidget(");

      if (!hasOsgiComponent) {
         fr.errorf("Missing @Component(service = AbstractBlam.class) annotation [%s]\n", className);
      }
      if (hasGetXWidgetsXml) {
         fr.errorf("Uses deprecated getXWidgetsXml() [%s] - Convert to getXWidgetItems() with builder pattern\n",
            className);
      }
      if (!hasGetXWidgetItems && !hasGetXWidgetsXml) {
         fr.warningf("No getXWidgetItems() or getXWidgetsXml() found [%s]\n", className);
      }
      if (!hasBlamName) {
         fr.errorf("Missing BLAM_NAME static final String field [%s]\n", className);
      }
      if (hasStringLiteralInSuper && hasBlamName) {
         fr.errorf("Constructor uses string literal instead of BLAM_NAME [%s]\n", className);
      }
      if (superWithoutImage) {
         fr.warningf("Constructor calls super(BLAM_NAME) without OseeImage parameter [%s]\n", className);
      }
      if (!hasDefaultConstructor) {
         fr.errorf("Missing default no-arg constructor [%s]\n", className);
      }
      if (!hasDescriptionUsage) {
         fr.warningf("Missing getDescriptionUsage() [%s]\n", className);
      }
      if (usesEndWidget) {
         fr.warningf("Uses .endWidget() [%s] - Not necessary with current builder\n", className);
      }
      if (usesRedundantCheckboxOpts) {
         fr.warningf("Checkbox widget uses redundant .andLabelAfter() or .andHorizLabel() [%s] - Already defaults\n",
            className);
      }
      if (usesAndWidget) {
         fr.warningf(
            "Uses generic andWidget() [%s] - Prefer convenience methods (andCheckBoxWidget, andTextWidget, etc.)\n",
            className);
      }
      checkWidgetLabelConstants(fr, contents, className);
      // Check for matching OSGI-INF XML
      checkOsgiXml(fr, filePath, className);
      // Check for matching test class
      checkTestExists(fr, filePath, className);
   }

   private void checkOsgiXml(FileResult fr, String filePath, String className) {
      String osgiXmlPath = findOsgiXmlForClass(filePath);
      if (osgiXmlPath == null) {
         fr.errorf("No matching OSGI-INF XML file found for [%s]\n", className);
      } else if (!isValidXml(osgiXmlPath)) {
         fr.errorf("OSGI-INF XML file is malformed for [%s]: %s\n", className, shortPath(osgiXmlPath));
      }
   }

   private void checkTestExists(FileResult fr, String filePath, String className) {
      java.io.File blamFile = new java.io.File(filePath);
      java.io.File testFile = new java.io.File(blamFile.getParent(), className + "Test.java");
      if (!testFile.exists()) {
         fr.warningf("No matching test class [%sTest.java] in same package [%s]\n", className, className);
      }
   }

   private boolean checkRedundantCheckboxOptions(String contents) {
      for (String line : contents.split("\n")) {
         if (line.contains(
            "andCheckBoxWidget") && (line.contains(".andLabelAfter()") || line.contains(".andHorizLabel()"))) {
            return true;
         }
      }
      return false;
   }

   private boolean checkStringLiteralInSuperConstructor(String contents) {
      for (String line : contents.split("\n")) {
         if (line.trim().startsWith("super(") && line.contains("\"") && !line.contains("BLAM_NAME")) {
            return true;
         }
      }
      return false;
   }

   private boolean checkSuperWithoutImage(String contents) {
      for (String line : contents.split("\n")) {
         if (line.trim().startsWith(
            "super(BLAM_NAME)") || (line.contains("super(BLAM_NAME") && !line.contains("super(BLAM_NAME,"))) {
            return true;
         }
      }
      return false;
   }

   private void checkWidgetLabelConstants(FileResult fr, String contents, String className) {
      for (String line : contents.split("\n")) {
         if (line.contains("variableMap.get") && line.contains("\"")) {
            String label = line.replaceFirst("^.*variableMap\\.get\\w+\\(\"", "").replaceFirst("\".*$", "");
            if (Strings.isValid(label) && label.length() > 2) {
               if (contents.contains("andWidget(\"" + label + "\"") || //
                  contents.contains("andTextWidget(\"" + label + "\"") || //
                  contents.contains("andCheckBoxWidget(\"" + label + "\"") || //
                  contents.contains("andListDropWidget(\"" + label + "\"") || //
                  contents.contains("andStringsSelWidget(\"" + label + "\"") || //
                  contents.contains("andBranchSelWidget(\"" + label + "\"")) {
                  fr.warningf(
                     "Widget label \"%s\" used as literal in both builder and runOperation [%s] - Extract to static final String\n",
                     label, className);
                  break;
               }
            }
         }
      }
   }

   public static void main(String[] args) {
      new CodeReviewBlams().run();
   }

}
