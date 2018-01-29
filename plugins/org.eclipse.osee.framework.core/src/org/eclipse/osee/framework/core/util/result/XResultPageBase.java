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
package org.eclipse.osee.framework.core.util.result;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Donald G. Dunne
 */
public class XResultPageBase {

   protected String title;
   protected String html;
   protected final String id; // Used to add and remove to menu item
   protected String manipulatedHtml;
   protected Set<Manipulations> manipulations = new HashSet<>();
   private int numWarnings = Integer.MAX_VALUE;
   private int numErrors = Integer.MAX_VALUE;

   /**
    * Create and display result page with all Manipulations available
    */
   public XResultPageBase(String title, String text) {
      this(title, text, Manipulations.ALL);
   }

   /**
    * Create and display result page with given Manipulations
    *
    * @param title title of the page (include date/time due or something unique due to multi-page view of results)
    * @param html html to display (minus manipulations). this html MUST already handle new lines (eg
    * text.replaceAll("\n",AHTML.newLine())) or use the CONVERT_NEWLINES manipultion
    * @param manipulations manipulations desired for the input HTML
    */
   public XResultPageBase(String title, String html, Manipulations... manipulations) {
      this.title = title;
      this.html = html;
      id = GUID.create();
      for (Manipulations man : manipulations) {
         switch (man) {
            case ALL:
               this.manipulations.add(Manipulations.GUID_CMD_HYPER);
               this.manipulations.add(Manipulations.ERROR_RED);
               this.manipulations.add(Manipulations.CONVERT_NEWLINES);
               this.manipulations.add(Manipulations.WARNING_YELLOW);
               break;
            case HTML_MANIPULATIONS:
               this.manipulations.add(Manipulations.GUID_CMD_HYPER);
               this.manipulations.add(Manipulations.ERROR_RED);
               this.manipulations.add(Manipulations.WARNING_YELLOW);
               break;
            default:
               this.manipulations.add(man);
               break;
         }
      }
   }

   public int getNumWarnings() {
      if (numWarnings == Integer.MAX_VALUE) {
         if (manipulations.contains(Manipulations.WARNING_YELLOW)) {
            numWarnings = Lib.numOccurances(html, "Warning:");
         }
         return 0;
      } else {
         return numWarnings;
      }
   }

   public int getNumErrors() {
      if (numErrors == Integer.MAX_VALUE) {
         if (manipulations.contains(Manipulations.WARNING_YELLOW)) {
            numErrors = Lib.numOccurances(html, "Error:");
         }
         return 0;
      } else {
         return numErrors;
      }
   }

   public String getId() {
      return id;
   }

   public String getErrorWarningHtml() {
      int numErrors = getNumErrors();
      int numWarnings = getNumWarnings();
      return String.format("%s <b>Errors</b>: %d  <b>Warnings</b>: %d%s<br/><br/>",
         getErrorWarningColorPre(numErrors, numWarnings), numErrors, numWarnings,
         getErrorWarningColorPost(numErrors, numWarnings));
   }

   private String getErrorWarningColorPre(int numErrors, int numWarnings) {
      if (numErrors > 0) {
         return "<font color=\"red\">";
      } else if (numWarnings > 0) {
         return "<font color=\"yellow\">";
      }
      return "";
   }

   private String getErrorWarningColorPost(int numErrors, int numWarnings) {
      if (numErrors > 0 || numWarnings > 0) {
         return "</font>";
      }
      return "";
   }

   public String getManipulatedHtml() {
      return getManipulatedHtml(manipulations);
   }

   public String handleIdCmdHyper(String str) {
      return str;
   }

   public String getManipulatedHtml(Collection<Manipulations> manipulations) {
      if (manipulatedHtml == null) {
         String str =
            (manipulations.contains(Manipulations.ERROR_WARNING_HEADER) ? getErrorWarningHtml() : "") + getText();
         if (manipulations.contains(Manipulations.RAW_HTML)) {
            str = AHTML.simplePage(str);
         } else {
            if (manipulations.contains(Manipulations.CONVERT_NEWLINES)) {
               str = str.replaceAll("\n", AHTML.newline());
            }
            if (manipulations.contains(Manipulations.GUID_CMD_HYPER)) {
               str = handleIdCmdHyper(str);
            }
            if (manipulations.contains(Manipulations.ERROR_RED)) {
               str = str.replaceAll("Error:", AHTML.color("red", "Error:"));
            }
            if (manipulations.contains(Manipulations.WARNING_YELLOW)) {
               str = str.replaceAll("Warning:", AHTML.color("orange", "Warning:"));
            }
         }
         manipulatedHtml = str;
      }
      return manipulatedHtml;
   }

   public String getText() {
      return html;
   }

   public void setHtml(String html) {
      this.html = html;
   }

   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public Set<Manipulations> getManipulations() {
      return manipulations;
   }

   public void setManipulations(Set<Manipulations> manipulations) {
      this.manipulations = manipulations;
   }

   public void setNumErrors(int numErrors) {
      this.numErrors = numErrors;
   }

   public void setNumWarnings(int numWarnings) {
      this.numWarnings = numWarnings;
   }

}
