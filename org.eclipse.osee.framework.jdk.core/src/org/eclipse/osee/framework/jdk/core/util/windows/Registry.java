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
package org.eclipse.osee.framework.jdk.core.util.windows;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.jdk.core.util.Lib;

public class Registry {

   public static final String HKEY_LOCAL_MACHINE = "HKLM";
   public static final String HKEY_CURRENT_USER = "HKCU";
   public static final String REG_SZ = "REG_SZ";
   public static final String REG_HELP_CMD = "reg /?";

   public abstract interface RegVersion {
      abstract public String getQueryCmd(String root, String path, String key);

      abstract public String getQueryPattern(String key);

      abstract public String getUpdateCommand(String root, String path, String key, String value, String[] regArray);

      abstract public String isVersion();
   }

   public class RegVersion_1_00 implements RegVersion {
      public String getQueryCmd(String root, String path, String key) {
         return "reg query " + root + "\\" + path + "\\" + key;
      }

      public String getQueryPattern(String key) {
         return "out:\\s*(\\w*)\\s*" + key + "\\s*(.*)";
      }

      public String getUpdateCommand(String root, String path, String key, String value, String[] regArray) {
         String cmd = "";
         if (regArray != null) {
            value = value + File.pathSeparator + regArray[1];
            System.out.println("THE VALUE IS: " + value);
            cmd = "reg update \"" + root + "\\" + path + "\\" + key + "=" + value + "\"";
         } else {
            cmd = "reg add \"" + root + "\\" + path + "\\" + key + "=" + value + "\"";
         }

         System.out.println("THE CMD IS: " + cmd);

         return cmd;
      }

      public String isVersion() {
         return "1.00";
      }
   }

   public class RegVersion_3_0 implements RegVersion {

      public String getQueryCmd(String root, String path, String key) {
         return "reg query " + root + "\\" + path + " /v " + key;
      }

      public String getQueryPattern(String key) {
         return ".*?" + key + "\\s*(\\w*)\\s*(\\S*).*";
      }

      public String getUpdateCommand(String root, String path, String key, String value, String[] regArray) {
         String type = Registry.REG_SZ;
         if (regArray != null) {
            value = value + File.pathSeparator + regArray[1];
            type = regArray[0];
         }
         return "reg add " + root + "\\" + path + " /v " + key + " /t " + type + " /f /d \"" + value + "\"";
      }

      public String isVersion() {
         return "3.0";
      }
   }

   public static boolean isRegVersion(double lowerBound, double upperBound) throws IOException {
      boolean matched = false;
      Process p = Runtime.getRuntime().exec(Registry.REG_HELP_CMD);
      StringWriter stringWriter = new StringWriter();
      Lib.handleProcess(p, stringWriter);

      Pattern pattern = Pattern.compile(".*version\\s(\\d*\\.\\d+).*", Pattern.DOTALL);
      Matcher matcher = pattern.matcher(stringWriter.toString());

      System.out.println(matcher.groupCount());
      if (matcher.matches()) {
         String regVersion = matcher.group(1).trim();
         System.out.println("Found Version: " + regVersion);

         double foundVersion = Double.parseDouble(regVersion);
         if ((foundVersion >= lowerBound) && (foundVersion <= upperBound)) {
            matched = true;
         }
      }
      return matched;
   }

   public static RegVersion getVersion() throws IOException {
      Registry r = new Registry();
      RegVersion regVersion = null;
      if (Registry.isRegVersion(1.00, 1.99)) {
         regVersion = r.new RegVersion_1_00();
      } else {
         regVersion = r.new RegVersion_3_0();
      }
      return regVersion;
   }

   public static String[] getValue(RegVersion version, String root, String path, String key) throws IOException {
      String toReturn[] = null;

      Process p = Runtime.getRuntime().exec(version.getQueryCmd(root, path, key));
      StringWriter stringWriter = new StringWriter();
      Lib.handleProcess(p, stringWriter);

      Pattern pattern = Pattern.compile(version.getQueryPattern(key), Pattern.DOTALL);
      Matcher matcher = pattern.matcher(stringWriter.toString());
      if (matcher.matches()) {
         toReturn = new String[matcher.groupCount()];

         for (int i = 1; i <= matcher.groupCount(); i++) {
            toReturn[i - 1] = matcher.group(i).trim();
         }
      }

      if (toReturn != null) {
         for (String temp : toReturn) {
            System.out.println("VAL: " + temp);
         }
      }
      return toReturn;
   }

   public static boolean prependRegistryValue(File updatedReg, String root, String path, String key, String value) throws IOException {

      RegVersion version = Registry.getVersion();

      String[] regArray = Registry.getValue(version, root, path, key);

      if (regArray != null) {
         if (regArray[1].contains(value)) {
            System.out.println("Value is already there.");
            return true;
         }
      }

      String command = version.getUpdateCommand(root, path, key, value, regArray);

      /*
       * If we are using a 1.0 version then use a provided reg.exe executable to use commands. If
       * not operation will fail.
       */
      if (version.isVersion().equals("1.00")) {
         if (updatedReg.exists() && updatedReg.isFile()) {
            command = updatedReg.getAbsolutePath() + command.replaceAll("reg", "");
            ;
         }
      }

      Process p = Runtime.getRuntime().exec(command);
      StringWriter stringWriter = new StringWriter();

      Lib.handleProcess(p, stringWriter);

      System.out.println("The string: " + stringWriter);

      if (stringWriter.toString().contains("err:")) {
         return false;
      }
      return true;
   }

   public static boolean prependRegistryValue(String root, String path, String key, String value) throws IOException {

      RegVersion version = Registry.getVersion();

      String[] regArray = Registry.getValue(version, root, path, key);

      Process p = Runtime.getRuntime().exec(version.getUpdateCommand(root, path, key, value, regArray));
      StringWriter stringWriter = new StringWriter();

      Lib.handleProcess(p, stringWriter);

      System.out.println("The string: " + stringWriter);

      if (stringWriter.toString().contains("err:")) {
         return false;
      }
      return true;
   }

   public static void main(String[] args) {
      try {
         Registry.prependRegistryValue(Registry.HKEY_CURRENT_USER, "environment", "path", "HI THERE");
      } catch (IOException ex) {
         ex.printStackTrace();
      }
   }

}
