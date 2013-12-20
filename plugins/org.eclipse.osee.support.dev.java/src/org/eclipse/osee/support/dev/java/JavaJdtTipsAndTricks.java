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
package org.eclipse.osee.support.dev.java;

import org.eclipse.swt.graphics.Image;

/**
 * This package contains Eclipse JDT / Java tips and tricks for developers.<br/>
 * <br/>
 * View README.txt file for instructions on how to use this for training
 * 
 * @author Donald G. Dunne
 */
public class JavaJdtTipsAndTricks {

   public Image image;

   public JavaJdtTipsAndTricks() {
   }

   public void getImage() {
      // JavaTip 01) Content assist - Ctrl-Space
      // How: Place cursor after "out." > Ctrl-Space
      System.out.println("hello world");
   }

   public void includeThis(Object obj) {
      // JavaTip 02) Content assist after insanceof condition - Cast added for you
      // How: type "obj." > Ctrl-Space > select getLocalizedMessage; Exception cast added automatically 
      if (obj instanceof Exception) {
         // do nothing
      }
   }

   // JavaTip 03) Ctrl-O - Searchable Outline popup
   // How: Ctrl-O in java editor > type "clear" to see filtering > select to goto

   public void camelCaseCompletion() {
      try {
         System.out.println("here");
         // JavaTip 04) Camel case support in code completion - type "ISE" then Ctrl-Space
         // How: Remove IllegalStateException > type "ISE" > Ctrl-Space
      } catch (IllegalStateException ex) {
         // JavaTip 05) Customize content assist categories - Ctrl-Space, Space, Space
         // How: uncomment lines below - Ctrl-Space - Space - Space after both - cycles through assist categories
         // Configure through Preferences - Editor - Content Assist - Advanced preference page.  
         // sy
         //                  Button
      }
   }
   // JavaTip 05a) Create Get/Set from field 
   // How: select "fName" > Right-click > Source > Generate Getter and Setter
   // JavaTip 05b) Create Get/Set using Ctrl-1
   // How: cursor on "fName" > Ctrl-1 > Creage getter and setter for 'fName'
   public String fName;

   // JavaTip 06) Create hashcode/equals
   // How: Right-click > Source > Generate hashCode() and equals(). 

   // JavaTip 07) Use templates to create methods
   // How: type "private" > Ctrl-Space > private_method > type "String" > tab > Type name > 
   // tab > type parameters > tab > start coding

   // JavaTip 08) Create your own templates
   // How: Preferences > Java > Editor > Templates > New > named: testing > type "this is my" > 
   // Insert Variable > select field > type "template" > save
   // In code, type try > Ctrl-Space > select template > tab > enter name

   public int quickFix(int start, int length) {
      // JavaTip 09) Quick-Fix - create a new method
      // How: replace "return 0;" with "return getThis(start, length);" > select getThis > Ctrl+1 > create method
      return 0;
      // JavaTip 10) Quick-Fix - create local variable
      // How: Select "start" parameter above > Ctrl+1 > assign parameter to new field
   }

   @SuppressWarnings("unused")
   public void quickFixField() {
      // JavaTip 11) Quick-Fix - convert local variable to global
      // How: type 'String str = "this is it";' > select "str" > Ctrl+1 > convert local to field
      String str = "this is it";
   }

   // JavaTip 12) Content Assist - Override a method
   // How: uncomment below > Ctrl-Space after toSt > select "toString() override"
   //   toSt

   public String renameInFile(String thisStr) {
      if (thisStr.contains("that")) {
         thisStr += thisStr + " and that";
      } else {
         thisStr = "that";
      }
      // JavaTip 13) Rename in file 
      // How: select "thisStr" > Ctrl+1 > rename in file > type something
      // JavaTip 14) Rename in workspace (refactor)
      // How: select "thisStr" > Ctrl+1 > rename in workspace > type something
      return thisStr;
   }

   public void quickFixExceptions() {
      // JavaTip 15) Quick-Fix - convert local variable to global
      // How: uncomment line below > Ctrl+1 or hover over to throw, add try/catch
      // throw new Exception("now is the time");
   }

   public void quickFixAssignments() {
      // JavaTip 16) Quick-Fix - assignments
      // How: place cursor on getIt > Ctrl+1 > assign to new local/field
      getIt("now");
   }

   public void quickFixCasting() {
      // JavaTip 17) Quick-Fix - casting
      // How: uncomment below > Ctrl+1 or hover over error > cast to String
      //      List<Object> names = new ArrayList<Object>();
      //      String name = names.get(1);

      // JavaTip 18) Quick-Fix - casting instanceof
      // How: uncomment below > place cursor after "o.ru" > Ctrl+Space > select run > Cast appears
      //      Object o = "this";
      //      if (o instanceof Runnable) {
      //         o.ru
      //      }
   }

   public void quickFixesAdvanced(Object obj) {
      // JavaTip 19) Quick-Fix - cast and assign off instanceof 
      // How: cursor on instanceof > Ctrl+1 > Introduce new local with cast type
      if (obj instanceof String) {
         // do nothing
      }

      // JavaTip 20) Quick-Fix - Inverse boolean expression 
      // How: select 'thisVar && !thatVar > Ctrl+1 > Inverse conditions
      // JavaTip 21) Quick-Fix - Split && 
      // How: select 'thisVar && !thatVar > Ctrl+1 > Split &&
      // JavaTip 22) Quick-Fix - Join nested if statements
      // How: perform previous java tip > select outer if statements > Ctrl+1 > Join if statements
      boolean thisVar = false, thatVar = true;
      if (thisVar && !thatVar) {
         // do nothing
      }

      // JavaTip 23) Quick-Fix - Add parentheses
      // How: select line below > Ctrl+1 > Add paranoidal parentheses
      boolean a = false, b = true, c = true, d = false;
      if (a == b && c != d) {
         // do nothing
      }

      // JavaTip 24) Quick-Fix - Conditional expresion to if/else and back
      // How: select "a ? 1: 2" below > Ctrl+1 > Replace conditional with if/else
      // JavaTip 25) Quick-Fix - if/else to conditional
      // How: perform previous java tip > select conditional below > Ctrl+1 > Replace with conditional
      @SuppressWarnings("unused")
      int value;
      value = a ? 1 : 2;

      // JavaTip 26) Quick-Fix - Convert string to StringBuffer
      // How: select '"Hello " + name' below > Ctrl+1 > Use StringBuffer for concatenation
      String name = "world";
      @SuppressWarnings("unused")
      String str = "Hello " + name;

      // JavaTip 27) Quick-Fix - extract local
      // How: select "getIt()" below > Ctrl+1 > Extract to local
      foo(getIt("hellow"));

      // JavaTip 28) Quick-Fix - extract to method
      // How: select code logic below > Ctrl+1 > Extract to method
      String fooStr = "this is";
      int myValue = 3;
      // select here down
      if (fooStr.length() > 4 && myValue == 2) {
         fooStr.replace("3", "23");
      } else {
         myValue = 2;
      }

      // JavaTip 29) Quick-Fix - remove surrounding if block
      // How: place cursor after closing or opening } > Ctrl+1 > Remove surrounding statement

      if (fooStr.length() > 4 && myValue == 2) {
         fooStr.replace("3", "23");
         myValue = 33;
      }

      // JavaTip 30) More Quick-Fix examples
      // How: http://help.eclipse.org/helios/index.jsp?topic=/org.eclipse.jdt.doc.user/tips/jdt_tips.html

      // JavaTip 31) Structured Selections - expanding current selection
      // How: select myValue > Alt+Shift+Arrow Up
      // Usage: to extract method or hightlight something for rename
      if (fooStr.length() > 4 && myValue == 2) {
         fooStr.replace("3", "23");
         myValue = 33;
      }

      // JavaTip 32) Structured Selections - selecting contents of block
      // How: double-click next to open or closing bracket
      // Usage: visualize whole block or cut/paste
      if (fooStr.length() > 4 && myValue == 2) {
         fooStr.replace("3", "23");
         myValue = 33;
      }

      // JavaTip 33) Wrap strings
      // How: Select after word "very" > press Enter
      @SuppressWarnings("unused")
      String str4 = "now is the time to very for all good men";

      // JavaTip 34) Move lines up / down without cut/paste
      // How: Select lines > Alt up/down arrows

      // JavaTip 35) Mark occurrences of field/method
      // How: Select fooStr > Alt+Shift+O > works on any selection till turn off
      fooStr.replace("3", "23");

   }

   // JavaTip 36) Go to next/preivous method
   // How: Ctrl+Shift+Arrow Up / Down

   // JavaTip 37) Paste code snippets to create class
   // How: copy below, paste in Package Explorer under java package
   public class HelloWorld {
      public void main(String[] args) {
         System.out.println("Hello World");
      }
   }

   // JavaTip 38) Debugging by writing breakpoint conditions to the console
   // How: add breakpoint on if line > right-click on breakpoint > properties > conditional
   // check suspend when true > add "System.out.println ("value is "+x); return false"
   // Usage: Allows compiled print statements that will disappear when breakpoint goes away
   //    and can add/remove without file re-compiling during debug session
   public void loopIt() {
      for (int x = 0; x < 100; x++) {
         if (x == 45) {
            getIt(String.valueOf(x));
         }
      }
      System.out.println("loopIt Complete");
   }

   // JavaTip 39) Debugging - Stepping into selections
   // How: uncomment stepInto call in main method in this file > 
   // add breakpoint to line below > right-click this class > debug as > java application > 
   // select "displayThis" > right-click > Step into selection
   // Usage: step into a single method within a series of chained or nested method calls
   // without having to step in each nested call
   public String stepInto() {
      return displayThis("[%s] shows [%s] that and that", getIt("now"), getIt("blah"));
   }

   // JavaTip 42) More tips and tricks
   // How: http://help.eclipse.org/helios/index.jsp?topic=/org.eclipse.jdt.doc.user/tips/jdt_tips.html

   private String getIt(String str) {
      return str.toString();
   }

   private String foo(String str) {
      return str.toString();
   }

   private String displayThis(String str, String str2, String str3) {
      return str.toString();
   }

   public static void main(String[] args) {
      JavaJdtTipsAndTricks tips = new JavaJdtTipsAndTricks();
      //      tips.stepInto();

      tips.loopIt();
   }

}
