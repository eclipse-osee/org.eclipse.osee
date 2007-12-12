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
package org.eclipse.osee.framework.ui.plugin.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

/**
 * Debug abstract class allows each plugin to subclass off a Debug class for the use by its own classes. Each plugin can
 * (and should) have an easter-egg way to turn on global debugging from the UI. This class allows each plugin to have
 * it's own Console window show only it's debug statments. Once subclassed in a plugin (standard is to simply use the
 * subclass "Debug" in the plugin), each class can instantiate a debug object to send debug to standard out normally or
 * to the Console if activate through the UI.
 * 
 * @author Donald G. Dunne
 */
public abstract class Debug {

   // True = debug everything for this instance of debug
   // False = don't debug unless globalDebug is set
   boolean debugOn = false;
   // Show timestamp with debug statement (eg: 12:12 This is debug)
   boolean timeStampOn = false;
   // Show (x) instance tag with statment (eg: StrView(4) This is)
   boolean displayInstanceTag = true;
   private static Map<String, Integer> instanceNumHash = new HashMap<String, Integer>(); // pluginName,
   // int
   private int thisInstanceNum = 0;
   // Namespace to give debug instance (eg: <namespace> this is)
   private String nameSpace = "";
   // True = everything debugs
   // False = only Debug(true, ..) debugs
   private static Map<String, Boolean> globalDebugHash = new HashMap<String, Boolean>(); // pluginName,
   // boolean
   private static Map<String, OseeConsole> oseeConsoleHash = new HashMap<String, OseeConsole>(); // pluginName,
   // OseeConsole
   private PrintStream out;
   // Color of debug in console window
   private int currentColor = SWT.COLOR_GREEN;

   // Return the plugin name to use in the console debug window
   protected abstract String getPluginName();

   protected int getNextInstanceNum() {
      if (!instanceNumHash.containsKey(getPluginName())) {
         setInstanceNum(1);
         return 1;
      }
      return instanceNumHash.get(getPluginName()).intValue();
   }

   protected void setInstanceNum(int num) {
      instanceNumHash.put(getPluginName(), new Integer(num));
   }

   /**
    * @return true if globalDebug flag is on for this plugin
    */
   protected boolean isGlobalDebug() {
      if (!globalDebugHash.containsKey(getPluginName())) {
         return false;
      }
      return globalDebugHash.get(getPluginName()).booleanValue();
   }

   protected OseeConsole getConsole() {
      if (!oseeConsoleHash.containsKey(getPluginName())) {
         return null;
      }
      return oseeConsoleHash.get(getPluginName());
   }

   protected void setConsole(OseeConsole console) {
      oseeConsoleHash.put(getPluginName(), console);
   }

   /**
    * Call to turn global debug on/off. Successive calls switch the state.
    */
   public void setGlobalDebug() {
      // All this to simply change from false to true or visa versa
      globalDebugHash.put(getPluginName(), new Boolean(!isGlobalDebug()));
      String debugOnStr = "Debug == " + ((isGlobalDebug()) ? "On" : "Off");
      System.out.println(debugOnStr);
      if (getConsole() == null) {
         setConsole(new OseeConsole(getPluginName() + " Debug Console"));
      }
      if (getConsole() != null) {
         OseeConsole console = (OseeConsole) getConsole();
         console.writeError(debugOnStr);
      }
   }

   public Debug(boolean debugOn, boolean timeStampOn, String nameSpace) {
      this(debugOn, nameSpace);
      this.timeStampOn = timeStampOn;
   }

   public Debug(boolean debugOn, String nameSpace) {
      thisInstanceNum = getNextInstanceNum();
      this.debugOn = debugOn;
      this.nameSpace = nameSpace;
      globalDebugHash.put(getPluginName(), false);
   }

   /**
    * Display (x) where x is # of instantiation of debug
    * 
    * @param set
    */
   public void setInstanceTag(boolean set) {
      displayInstanceTag = set;
   }

   /**
    * Report debug
    * 
    * @param str = string to display
    */
   public void report(String str) {
      report(str, currentColor);
   }

   public void report(String str, Throwable ex) {
      report(str + "\nException: " + Lib.exceptionToString(ex), currentColor);
   }

   public void report(String str, boolean showTimestamp) {
      if (showTimestamp)
         report(str + " " + (new SimpleDateFormat("hh:mm:ss")).format(new Date()));
      else
         report(str);

   }

   /**
    * Report debug in SWT.COLOR
    * 
    * @param str
    * @param color
    */
   public void report(String str, int color) {
      if (!debugOn && !isGlobalDebug()) return;
      String timeStr = "", nameStr = "";
      if (timeStampOn) {
         Calendar cal = Calendar.getInstance();
         cal.setTime(new Date());
         timeStr =
               "(" + cal.get(Calendar.HOUR) + ":" + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND) + ") ";
      }
      if (!nameSpace.equals("")) {
         String instStr = "";
         if (displayInstanceTag) {
            instStr = "(" + thisInstanceNum + ")";
         }
         nameStr = nameSpace + instStr + " => ";
      }
      String dispStr = timeStr + nameStr + str;

      if (isGlobalDebug() && getConsole() != null) {
         Display.getCurrent().getSystemColor(color);
         ((OseeConsole) getConsole()).write(dispStr);
      } else
         System.out.println(dispStr);
      if (out != null) out.println(dispStr);
   }

   /**
    * Set debug on for this instance of Debug
    * 
    * @param debug
    */
   public void setDebug(boolean debug) {
      this.debugOn = debug;
   }

   /**
    * Is debug on for this instance of Debug
    * 
    * @return boolean
    */
   public boolean isDebug() {
      return debugOn;
   }

   /**
    * @param timeStampOn The timeStampOn to set.
    */
   public void setTimeStampOn(boolean timeStampOn) {
      this.timeStampOn = timeStampOn;
   }

   /**
    * @return Returns the nameSpace.
    */
   public String getNameSpace() {
      return nameSpace;
   }

   /**
    * @param nameSpace The nameSpace to set. When print debug, will be namespace => <debug string>
    */
   public void setNameSpace(String nameSpace) {
      this.nameSpace = nameSpace;
   }

   /**
    * @param swtColor
    */
   public void setColor(int swtColor) {
      currentColor = swtColor;
   }

   public void setFileOutput(String filename) {
      try {
         out = new PrintStream(new AppendFileStream(filename));
         report("Output being sent to file " + filename);
      } catch (IOException ex) {
         ex.printStackTrace();
      }
   }

   class AppendFileStream extends OutputStream {
      RandomAccessFile fd;

      public AppendFileStream(String file) throws IOException {
         fd = new RandomAccessFile(file, "rw");
         fd.seek(fd.length());
      }

      public void close() throws IOException {
         fd.close();
      }

      public void write(byte[] b) throws IOException {
         fd.write(b);
      }

      public void write(byte[] b, int off, int len) throws IOException {
         fd.write(b, off, len);
      }

      public void write(int b) throws IOException {
         fd.write(b);
      }
   }
}