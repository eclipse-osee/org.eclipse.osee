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

package org.eclipse.osee.framework.jdk.core.text;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.xml.parsers.ParserConfigurationException;
import org.eclipse.osee.framework.jdk.core.text.change.ChangeSet;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Ryan D. Brooks
 */
public abstract class Rule {
   private final String outExtension;
   private String currentOutfileName;
   protected File inputFile;
   private String subdirectoryName;
   protected Pattern fileNamePattern;
   protected boolean ruleWasApplicable;
   protected Logger logger;
   private int modCount;
   protected String charsetString = "UTF8";

   public Rule() {
      this("done");
   }

   public Rule(String outExtension) {
      this.outExtension = outExtension;
      this.ruleWasApplicable = false;
      logger = Logger.getLogger(this.getClass().getName());
      logger.setLevel(Level.ALL);
      this.subdirectoryName = null;
   }

   /**
    * Implement this to receive the content of the entire file in {@code seq}
    */
   public abstract ChangeSet computeChanges(CharSequence seq);

   public final int process(Collection<File> list) {
      for (File file : list) {
         try {
            process(file);
         } catch (Exception ex) {
            System.out.println(currentOutfileName + ": " + ex.getMessage());
         }
      }

      return modCount;
   }

   public final int process(File file) throws IOException {
      if (file.isDirectory()) {
         List<File> files = Lib.recursivelyListFiles(file, fileNamePattern);
         for (File aFile : files) {
            try {
               process(aFile);
            } catch (Exception ex) {
               System.out.println(currentOutfileName + ": " + ex);
            }
         }
      } else {
         inputFile = file;
         process(file, getResultFile(file));
      }

      return modCount;
   }

   public final void process(File inFile, File outFile) throws IOException {
      File subdirectory;
      if (subdirectoryName != null) {
         File parent = outFile.getParentFile();
         subdirectory = new File(parent, subdirectoryName);
         if (!subdirectory.exists() && !subdirectory.mkdir()) {
            throw new IOException("Could not create directory");
         }
         outFile = new File(subdirectory, outFile.getName());
      }

      this.currentOutfileName = outFile.getName();
      if (inFile.exists()) {
         RulesLogHandler handler = null;
         ChangeSet changeSet = null;
         try {
            handler = new RulesLogHandler(new File(Lib.changeExtension(outFile.getPath(), "xml")));
            logger.addHandler(handler);
            ruleWasApplicable = false;
            changeSet = computeChanges(Lib.fileToCharBuffer(inFile, charsetString));
         } catch (ParserConfigurationException ex) {
            logger.log(Level.SEVERE, ex.toString(), ex);
         } finally {
            if (handler != null) {
               handler.close();
               logger.removeHandler(handler);
            }
         }
         if (ruleWasApplicable) {
            String path = subdirectoryName == null ? "" : subdirectoryName;
            System.out.println("Rule was applied to " + path + currentOutfileName);
            modCount++;
            if (changeSet != null) {
               changeSet.applyChanges(outFile);
            }
         }
      }
   }

   protected final File getResultFile(File file) {
      if (outExtension == null) {
         return file;
      }
      return new File(Lib.removeExtension(file.getPath()) + "." + outExtension);
   }

   public static void main(String[] args) throws Exception {
      if (args.length < 3) {
         System.out.println("Usage: " + Rule.class.getName() + " <ruleClassPath> <ruleClass> <file list>");
         return;
      }

      String ruleName = args[1];
      String classPath = args[0];
      try (URLClassLoader classLoader = new URLClassLoader(new URL[] {Lib.getUrlFromString(classPath)})) {
         System.out.println("class path: " + classLoader.getURLs()[0]);
         Object obj = classLoader.loadClass(ruleName).newInstance();

         if (obj instanceof org.eclipse.osee.framework.jdk.core.text.Rule) {
            Rule rule = (Rule) obj;
            for (int i = 2; i < args.length; i++) {
               try {
                  File file = new File(args[i]);
                  if (!file.exists()) {
                     System.out.println("The file " + file + " does not exist!");
                  } else {
                     rule.process(file);
                  }
               } catch (Exception ex) {
                  System.out.println("Exception in Rule!!! " + rule.currentOutfileName + ": " + ex.getMessage());
                  ex.printStackTrace();
               }
            }
         } else {
            throw new IllegalArgumentException(ruleName + " is not of type text.Rule.");
         }
      }
   }

   public final boolean ruleWasApplicable() {
      return ruleWasApplicable;
   }

   public final String getCurrentOutfileName() {
      return currentOutfileName;
   }

   public final void setRuleWasApplicable(boolean ruleWasApplicable) {
      this.ruleWasApplicable = ruleWasApplicable;
   }

   public final File getInputFile() {
      return inputFile;
   }

   public final void setSubdirectoryNameToPlaceResultFilesIn(String subdirectoryName) {
      this.subdirectoryName = subdirectoryName;
   }

   public final void setFileNamePattern(String fileNamePattern) {
      this.fileNamePattern = Pattern.compile(fileNamePattern);
   }

   public final String getCharsetString() {
      return charsetString;
   }

   public final void setCharsetString(String charsetString) {
      this.charsetString = charsetString;
   }
}
