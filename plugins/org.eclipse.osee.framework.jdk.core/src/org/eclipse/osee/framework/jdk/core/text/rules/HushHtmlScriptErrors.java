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

package org.eclipse.osee.framework.jdk.core.text.rules;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.jdk.core.text.Rule;
import org.eclipse.osee.framework.jdk.core.text.change.ChangeSet;

/**
 * This rule is to be used on html files. It adds error handling so that unsupported java scripting calls do not result
 * in pesky windows being shown to the user. For example, when javadoc produced API's are viewed through the eclipse
 * help system and the javadoc attempts to set the title of the parent.
 * 
 * @author Robert A. Fisher
 */
public class HushHtmlScriptErrors extends Rule {

   // This is the code that must be entered in to the HTML to hush script errors
   private final static String HUSH =
      "\nfunction handleError() {\n" + "return true;\n" + "}\n\n" + "window.onerror = handleError;\n";

   public HushHtmlScriptErrors() {
      super(null);
      setFileNamePattern(".*\\.html");
   }

   @Override
   public ChangeSet computeChanges(CharSequence seq) {
      Pattern detectHusherInPlace = Pattern.compile("function handleError");
      Pattern findJavaScriptArea = Pattern.compile("<SCRIPT type=\"text/javascript\">");
      ChangeSet changeSet = new ChangeSet(seq);

      // Only add in hushing if it is not in place
      Matcher matchHusher = detectHusherInPlace.matcher(seq);
      if (!matchHusher.find()) {

         Matcher matcher = findJavaScriptArea.matcher(seq);
         if (matcher.find()) {
            ruleWasApplicable = true;

            changeSet.insertBefore(matcher.end() + 1, HUSH);
         }
      }

      return changeSet;
   }

   public static void main(String[] args) {
      HushHtmlScriptErrors hushRule = new HushHtmlScriptErrors();
      for (int i = 0; i < args.length; i++) {
         try {
            System.out.print("File " + (i + 1) + "/" + args.length + ":");
            hushRule.process(new File(args[i]));
         } catch (Exception ex) {
            System.out.println("Exception in Rule!!! " + hushRule.getCurrentOutfileName() + ": " + ex.getMessage());
            ex.printStackTrace();
         }
      }
      System.out.println("Finished");
   }
}