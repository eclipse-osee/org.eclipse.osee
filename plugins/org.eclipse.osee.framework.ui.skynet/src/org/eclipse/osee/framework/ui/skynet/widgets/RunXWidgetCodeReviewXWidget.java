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
package org.eclipse.osee.framework.ui.skynet.widgets;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.util.OseeInf;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.HashCollectionSet;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * This will read all X.*Widget files and test for commonality rules<br/>
 * <ul>
 *
 * @author Donald G. Dunne
 */
public class RunXWidgetCodeReviewXWidget {

   private static final String WIDGET_ID = "WidgetId ID = ";
   public static String IGNORE_LINE = "XWidgetReview.IgnoreLine";
   private static final boolean changeFiles = true;

   /**
    * In support of multiple repos and token files (eg: WidgetId (Framework) and WidgetIdAts (ATS) and other repos),
    * tokens will be split into separate files. While files are being searched/loaded, WidgetId will get token "Default"
    * and others will get class name minus WidgetId. So, WidgetIdAts token will be "Ats". As XWidget files are loaded
    * and processed, tokens are collected depending on their repos having the string of their token and then written to
    * that file between \\START and \\END tags in sorted order. So, anything in ATS bundle will get written to
    * WidgetIdAts. Anything in "Default" repo will be written to WidgetId.
    */
   private static Map<String, String> widgetTokenTypeToBundleContains = new HashMap<String, String>();
   private static Map<String, String> widgetTokenTypeToFile = new HashMap<String, String>();
   private static Map<String, String> widgetTokenTypeToClassname = new HashMap<String, String>();
   private static List<String> widgetTokenTypes = new ArrayList<String>();
   HashCollectionSet<String, String> widgetTokenTypeToTokens = new HashCollectionSet<String, String>();

   Integer breakAfterFile = null;
   XResultData rd = new XResultData();
   private String orgCopyright;

   public void run() {
      rd.logf("Reviewing XWidget Files...\n\n");
      rd.logf("Search for Error: and Warning: and resolve\n");
      rd.addResultsTag();
      reviewXWidgetFiles();
   }

   private void reviewXWidgetFiles() {
      Collection<String> files = findXWidgetFiles();

      if (files.isEmpty()) {
         rd.logf("No Files found");
         return;
      }
      int x = 0;

      for (String widgetFile : files) {
         x = x + 1;
         rd.logf("\n=================================================================\n");
         rd.logf("\nWidget [%s]\n", widgetFile);
         validateFile(widgetFile);

         if (breakAfterFile != null && x == breakAfterFile) {
            rd.logf("\n\nStopping early at count itemCount\n");
            break;
         }
      }
      rd.logf("\n\n");

      updateTokenFiles();

      String path = System.getProperty("user.home") + File.separator + getClass().getSimpleName() + ".html";
      File outFile = new File(path);
      String html = rd.getHtml();
      try {
         Lib.writeStringToFile(html, outFile);
         Desktop.getDesktop().open(outFile);
      } catch (Exception ex) {
         // do nothing
      }
   }

   public void validateFile(String fullFilename) {
      String widgetFileContents;
      try {
         File file = new File(fullFilename);
         String justFilename = file.getName().replaceFirst("\\.java", "");
         if (justFilename.contains("Dam")) {
            rd.errorf("Dam retired, replace with Art for [%s]\n", justFilename);
         }
         widgetFileContents = Lib.fileToString(file);
         widgetFileContents = widgetFileContents.replaceAll("[\n\r]+", "\r\n");
         String[] lines = widgetFileContents.split("\n");
         if (lines.length < 5) {
            rd.errorf("File [%s] did not split correctly", justFilename);
            return;
         }

         String widgetId = null;
         String copyRightLine = null;
         String defaultConstructorLine = null;
         String osgiComponentLine = null;

         String widgetClassName = "WidgetId";
         String widgetTokenType = "Default";
         for (String tokStr : widgetTokenTypes) {
            if (!tokStr.equals("Default")) {
               String bundleContains = widgetTokenTypeToBundleContains.get(tokStr);
               if (fullFilename.contains(bundleContains)) {
                  widgetTokenType = tokStr;
                  widgetClassName = widgetTokenTypeToClassname.get(widgetTokenType);
               }
            }
         }

         boolean isAbstractClass = false;
         boolean isIdInConstructor = false;
         boolean isWidgetIdInConstructor = false;
         //         boolean hasArtifactField = false;
         //         boolean hasGetArtifactMethod = false;
         //         boolean hasArtifactAssign = false;
         //         boolean hasArtifactDot = false;
         //         boolean hasArtifactEqualNull = false;
         //         boolean hasArtifactComma = false;
         //         boolean hasArtifactParenDot = false;
         //         boolean hasAttrAssign = false;
         //         boolean hasAttrField = false;
         for (String line : lines) {
            //            rd.logf("line   %s\n", line);
            //            if (line.contains(" this.attributeType = attributeType;")) {
            //               hasAttrAssign = true;
            //            }
            //            if (line.contains(" private AttributeTypeToken attributeType;") || line.contains(
            //               " public AttributeTypeToken attributeType;") || line.contains(
            //                  " protected AttributeTypeToken attributeType;")) {
            //               hasAttrField = true;
            //            }
            //            if (line.contains(") artifact,")) {
            //               hasArtifactComma = true;
            //            }
            //            if (line.contains("(artifact == null)")) {
            //               hasArtifactEqualNull = true;
            //            }
            if (line.contains("public abstract ")) {
               isAbstractClass = true;
            }
            if (line.contains("Copyright ")) {
               copyRightLine = line;
            }
            if (line.contains("@Component(service = XWidget")) {
               osgiComponentLine = line;
            }
            if (line.contains("super(WidgetId")) {
               rd.errorf(line + "\nsuper should call super(ID) not super(WidgetId\n");
            }
            if (line.contains("Default Label")) {
               rd.errorf("Review inserted default constructor and remove \"Default Label\" [%s]\n", justFilename);
            }
            if (line.contains("this(WidgetId")) {
               rd.errorf(line + "\nsuper should call this(ID) not this(WidgetId\n");
            }
            if (line.contains("this(ID")) {
               isIdInConstructor = true;
            }
            //            if (line.contains(" private Artifact artifact;")) {
            //               hasArtifactField = true;
            //            }
            //            if (line.contains(" public Artifact getArtifact()")) {
            //               hasGetArtifactMethod = true;
            //            }
            //            if (line.contains(" this.artifact = artifact;")) {
            //               hasArtifactAssign = true;
            //            }
            //            if (line.contains(" artifact.")) {
            //               hasArtifactDot = true;
            //            }
            //            if (line.contains("(artifact.")) {
            //               hasArtifactParenDot = true;
            //            }
            if (line.contains("super(ID")) {
               isIdInConstructor = true;
            }
            if (line.contains("this(widgetId")) {
               isWidgetIdInConstructor = true;
            }
            if (line.contains("super(widgetId")) {
               isWidgetIdInConstructor = true;
            }
            if (line.contains("implements ArtifactWidget")) {
               if (!justFilename.contains(
                  "ArtWidget") && (!justFilename.contains("Viewer") && !justFilename.contains("Persist"))) {
                  rd.errorf("ArtifactWidget Classname should include ArtWidget, Viewer or Persist [%s]\n",
                     justFilename);
               }
            }
            if (line.contains("implements AttributeWidget") && (!justFilename.contains(
               "Viewer") && !justFilename.contains("Persist"))) {
               if (!justFilename.contains("ArtWidget")) {
                  rd.errorf("AttributeWidget Classname should include ArtWidget, Viewer or Persist [%s]\n",
                     justFilename);
               }
            }
            if (line.contains(justFilename + "()")) {
               defaultConstructorLine = line;
            }
            if (!isAbstractClass && line.contains(WIDGET_ID)) {
               widgetId = line;
               widgetId = widgetId.replaceFirst("^.*\\.", "");
               widgetId = widgetId.replaceFirst("[;\r\n]+", "");
               rd.logf(line + "\nWidgetId [%s]\n", widgetId);
            }
         }

         if (isAbstractClass) {

            if (!isWidgetIdInConstructor) {
               rd.errorf("At least one constructor should have super(widgetId, or this(widgetId, [%s]\n", justFilename);
            }

            if (Strings.isValid(widgetId)) {
               rd.errorf("Abstract class should not have a \"WidgetId ID\" [%s]\n", justFilename);
               return; // Fix abstract error before continuing tests
            }
            // All abstracts, except XWiget, must start with XAbstract
            if (!justFilename.equals("XWidget") && !justFilename.startsWith("XAbstract")) {
               rd.errorf("Abstract class should start with XAbstract for [%s]\n", justFilename);
               return; // Fix abstract error before continuing tests
            }

         }
         if (!isAbstractClass) {

            if (!isIdInConstructor) {
               rd.errorf("At least one constructor should have super(ID, or this(ID, [%s]\n", justFilename);
            }

            if (justFilename.contains("Abstract")) {
               rd.errorf("Remove \"Abstract\" from name or add \"public abstract\" [%s]\n", justFilename);
               return; // Fix abstract error before continuing tests
            }

            // Must have ID field
            if (Strings.isInvalid(widgetId)) {
               rd.error("\"" + WIDGET_ID + " = \" NOT FOUND");
               addMissingWidgetIdToken(fullFilename, justFilename, widgetFileContents, widgetId, widgetClassName, rd);
            }

            // ID field must match classname cause it is used to load widgets
            else {
               if (!justFilename.equals(widgetId)) {
                  rd.errorf("Widget Id [%s] does not match filename [%s]\n", widgetId, justFilename);
               } else {
                  rd.logf("Success: Widget Id [%s] matches filename [%s]\n", widgetId, justFilename);
               }
            }

            // Must have OSGI Component line
            if (Strings.isValid(osgiComponentLine)) {
               rd.logf("Success: OSGI Componet Line Found [%s]\n", osgiComponentLine);
            } else {
               rd.errorf("Missing OSGI Componet[%s] - Attemping to add, Needs review.\n", justFilename);
               addOsgiComponent(fullFilename, justFilename, widgetFileContents, widgetId, widgetClassName, rd);
            }

            // Must have default constructor if not abstract
            if (Strings.isValid(defaultConstructorLine)) {
               rd.logf("Success: Default Constructor Found [%s]\n", defaultConstructorLine);
            } else {
               rd.errorf("Missing Default Contructor [%s] - Attemping to add, Needs review.\n", justFilename);
               addMissingDefaultConstructor(fullFilename, justFilename, widgetFileContents, widgetId, widgetClassName,
                  rd);
            }

            //            if (hasArtifactField) {
            //               rd.errorf("Has Artifact field [%s] - Attemping to remove, Needs review.\n", justFilename);
            //               String search = " private Artifact artifact; *[\r\n]+";
            //               String replace = "\r\n";
            //               searchReplaceFile(fullFilename, justFilename, search, replace, rd);
            //
            //               System.err.println("here");
            //            }
            //
            //            if (hasGetArtifactMethod) {
            //               rd.errorf("Has getArtifact() method [%s] - Attemping to remove, Needs review.\n", justFilename);
            //               String search =
            //                  "(?s)@Override\\s*public\\s+Artifact\\s+getArtifact\\s*\\(\\s*\\)\\s*\\{\\s*return\\s+artifact;\\s*\\}\\s+";
            //               String replace = "";
            //               searchReplaceFile(fullFilename, justFilename, search, replace, rd);
            //
            //               System.err.println("here");
            //            }
            //
            //            if (hasAttrAssign) {
            //               String search = "   this.attributeType = attributeType;";
            //               String replace = "   setAttributeType(attributeType);";
            //               searchReplaceFile(fullFilename, justFilename, search, replace, rd, true);
            //
            //               System.err.println("here");
            //            }
            //
            //            if (hasAttrField) {
            //               for (String visibStr : Arrays.asList("public", "private", "protected")) {
            //                  String search = " " + visibStr + " AttributeTypeToken attributeType; *[\r\n]+";
            //                  String replace = "\r\n";
            //                  searchReplaceFile(fullFilename, justFilename, search, replace, rd);
            //               }
            //
            //               System.err.println("here");
            //            }
            //            if (hasArtifactAssign) {
            //               rd.errorf("Has art assign method [%s] - Attemping to remove, Needs review.\n", justFilename);
            //               String search = "      this.artifact = artifact;\r\n";
            //               String replace = "      setArtifact(artifact);\r\n";
            //               searchReplaceFile(fullFilename, justFilename, search, replace, rd, true);
            //
            //               System.err.println("here");
            //            }
            //
            //            if (hasArtifactDot) {
            //               String search2 = " artifact\\.";
            //               String replace2 = " getArtifact().";
            //               searchReplaceFile(fullFilename, justFilename, search2, replace2, rd, true);
            //
            //               System.err.println("here");
            //            }
            //
            //            if (hasArtifactEqualNull) {
            //               String search2 = "\\(artifact == null\\)";
            //               String replace2 = "(getArtifact() == null)";
            //               searchReplaceFile(fullFilename, justFilename, search2, replace2, rd, true);
            //
            //               System.err.println("here");
            //            }
            //
            //            if (hasArtifactComma) {
            //               String search2 = "\\) artifact,";
            //               String replace2 = ") getArtifact(),";
            //               searchReplaceFile(fullFilename, justFilename, search2, replace2, rd, true);
            //
            //               System.err.println("here");
            //            }
            //
            //            if (hasArtifactParenDot) {
            //               String search2 = "\\(artifact\\.";
            //               String replace2 = "(getArtifact().";
            //               searchReplaceFile(fullFilename, justFilename, search2, replace2, rd, true);
            //
            //               System.err.println("here");
            //            }

            // Create tokens for non-abstract classes
            String widgetTokenStr = String.format("   public static final %s %s = new %s(\"%s\");", widgetClassName,
               justFilename, widgetClassName, justFilename);
            widgetTokenTypeToTokens.put(widgetTokenType, widgetTokenStr);

         }

         // Must have Copyright
         if ((widgetTokenType.equals("Ats") || (widgetTokenType.equals("Default"))) && Strings.isInvalid(
            copyRightLine)) {
            rd.errorf("Missing Copyright [%s]\n", justFilename);
            addCopyright(fullFilename, justFilename, widgetFileContents, widgetId, widgetClassName, widgetTokenType,
               rd);
         } else {
            rd.logf("Success: Copyright Found [%s]\n", copyRightLine);
         }

      } catch (IOException ex) {
         rd.errorf("===> Error: Exception loading java file [%s] exception %s\n", fullFilename,
            Lib.exceptionToString(ex));
         return;
      }
   }

   private void addOsgiComponent(String fullFilename, String justFilename, String widgetFileContents, String widgetId,
      String widgetClassName, XResultData rd) {
      //eg: @Component(service = XWidget.class, immediate = true)
      String search = "public class " + justFilename;
      String replace = String.format("@Component(service = XWidget.class, immediate = true)\n" + search,
         widgetClassName, justFilename);
      searchReplaceFile(fullFilename, justFilename, search, replace, rd);
   }

   private void addMissingDefaultConstructor(String fullFilename, String justFilename, String widgetFileContents,
      String widgetId, String widgetClassName, XResultData rd) {
      if (!changeFiles) {
         return;
      }
      try {
         File file = new File(fullFilename);
         widgetFileContents = Lib.fileToString(file);
         String newline = "\r\n";
         String[] lines = widgetFileContents.split(newline);
         boolean found = false;
         StringBuilder sb = new StringBuilder();
         for (String line : lines) {
            line = line.replaceAll("[\r\n]+", "");
            if (!found && line.contains("WidgetId ID =")) {
               sb.append(line);
               sb.append("\n");
               sb.append(newline);
               sb.append("\n\n   public " + justFilename + "() {\n      this(ID, \"Default Label\");\n   }\n\n");
               found = true;
            } else {
               sb.append(line);
               sb.append("\n");
            }
         }
         Lib.writeStringToFile(sb.toString(), new File(fullFilename));
      } catch (IOException ex) {
         System.err.println(Lib.exceptionToString(ex));
      }
   }

   private String getOrgCopyright() {
      if (orgCopyright == null) {
         try {
            orgCopyright = OseeInf.getResourceContents("misc/OseeOrgCopyright.txt", getClass());
         } catch (Exception ex) {
            // do nothing
         }
      }
      return orgCopyright;
   }

   private void addCopyright(String fullFilename, String justFilename, String widgetFileContents, String widgetId,
      String widgetClassName, String widgetTokenType, XResultData rd2) {

      String copyright = getOrgCopyright();
      if (Strings.isInvalid(copyright)) {
         return;
      }
      Conditions.assertTrue(Strings.isValid(copyright), "Couldn't load copyright");

      try {
         rd.logf("Adding copyright...\n");
         File file = new File(fullFilename);
         String fileContents = Lib.fileToString(file);
         String newFileContents = copyright + fileContents;
         if (changeFiles) {
            Lib.writeStringToFile(newFileContents, new File(fullFilename));
         }
      } catch (IOException ex) {
         System.err.println(Lib.exceptionToString(ex));
      }

   }

   private void addMissingWidgetIdToken(String fullFilename, String justFilename, String widgetFileContents,
      String widgetId, String widgetClassName, XResultData rd) {
      //eg: public static final WidgetId ID = WidgetIdAts.XAgileFeatureHyperlinkWidget;
      String search = " \\{";
      String replace =
         String.format(" {\n\n   public static final WidgetId ID = %s.%s;", widgetClassName, justFilename);
      searchReplaceFile(fullFilename, justFilename, search, replace, rd);
   }

   private void searchReplaceFile(String fullFilename, String justFilename, String searchStr, String replaceStr,
      XResultData rd) {
      searchReplaceFile(fullFilename, justFilename, searchStr, replaceStr, rd, false);
   }

   private void searchReplaceFile(String fullFilename, String justFilename, String searchStr, String replaceStr,
      XResultData rd, boolean all) {
      try {
         rd.logf("SrchReplace: [%s] Srch[%s] Rplc[%s]\n", justFilename, searchStr, replaceStr);
         File file = new File(fullFilename);
         String fileContents = Lib.fileToString(file);
         if (all) {
            fileContents = fileContents.replaceAll(searchStr, replaceStr);
         } else {
            fileContents = fileContents.replaceFirst(searchStr, replaceStr);
         }
         if (changeFiles) {
            Lib.writeStringToFile(fileContents, new File(fullFilename));
         }
      } catch (IOException ex) {
         System.err.println(Lib.exceptionToString(ex));
      }
   }

   private void updateTokenFiles() {
      for (String widgetTokenType : widgetTokenTypes) {
         List<String> tokens = new ArrayList<String>();
         tokens.addAll(widgetTokenTypeToTokens.getValues(widgetTokenType));
         tokens.sort(Comparator.naturalOrder());
         String searchStr = "(?s)START.*END";
         String replaceStr = "START\n" + Collections.toString("\n", tokens) + "\n   //END";
         String fullFilename = widgetTokenTypeToFile.get(widgetTokenType);
         searchReplaceFile(fullFilename, fullFilename, searchStr, replaceStr, rd);
      }
   }

   private Collection<String> findXWidgetFiles() {
      File thisClass =
         new File(RunXWidgetCodeReviewXWidget.class.getProtectionDomain().getCodeSource().getLocation().getPath());
      String pathStr = thisClass.getAbsolutePath();
      String basePath = pathStr;
      basePath = basePath.replaceFirst("org.eclipse.osee.*$", "");
      return getXWidgetFiles(new File(basePath), new ArrayList<>());
   }

   public static List<String> getXWidgetFiles(File dir, List<String> filenames) {
      //      System.err.println("dir: " + dir.getAbsolutePath());
      // Do not ever need to check these dirs; Improves search performance
      List<String> ignoreDirs = Arrays.asList("admin", ".git", //
         ".postgresql.", ".help.", ".support.", "web", "nebula", //
         ".integration.tests.", "\\target\\", "\\bin" //
      );
      try {
         File[] fileList = dir.listFiles();
         for (File file : fileList) {
            //            System.err.println(file.getAbsolutePath());
            if (file.isDirectory()) {
               boolean skipDir = false;
               for (String dirName : ignoreDirs) {
                  if (file.getAbsolutePath().contains(dirName)) {
                     skipDir = true;
                     break;
                  }
               }
               if (skipDir) {
                  continue;
               }
               getXWidgetFiles(file, filenames);
            } else {
               if (file.getName().matches("X.*Widget.java")) {
                  System.err.println("File: " + file.getName());
                  filenames.add(file.getAbsolutePath());
               }
               if (file.getName().contains("WidgetId")) {
                  String justFilename = file.getName();
                  String widgetToken = justFilename;
                  widgetToken = widgetToken.replace(".java", "");
                  String className = widgetToken;
                  widgetToken = widgetToken.replace("WidgetId", "");
                  String bundleContains = "";
                  if (Strings.isInvalid(widgetToken)) {
                     widgetToken = "Default";
                     bundleContains = ".skynet";
                  } else {
                     bundleContains = "." + widgetToken.toLowerCase();
                  }
                  widgetTokenTypes.add(widgetToken);
                  widgetTokenTypeToBundleContains.put(widgetToken, bundleContains);
                  widgetTokenTypeToFile.put(widgetToken, file.getAbsolutePath());
                  widgetTokenTypeToClassname.put(widgetToken, className);
               }
            }
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
      return filenames;
   }

   public static void main(String[] args) {
      RunXWidgetCodeReviewXWidget op = new RunXWidgetCodeReviewXWidget();
      op.run();
   }

}
