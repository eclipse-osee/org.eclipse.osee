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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.jdk.core.util.Processes;

/**
 * @author Ryan D. Brooks
 */
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
      @Override
      public String getQueryCmd(String root, String path, String key) {
         return "reg query " + root + "\\" + path + "\\" + key;
      }

      @Override
      public String getQueryPattern(String key) {
         return "out:\\s*(\\w*)\\s*" + key + "\\s*(.*)";
      }

      @Override
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

      @Override
      public String isVersion() {
         return "1.00";
      }
   }

   public class RegVersion_3_0 implements RegVersion {

      @Override
      public String getQueryCmd(String root, String path, String key) {
         return "reg query " + root + "\\" + path + " /v " + key;
      }

      @Override
      public String getQueryPattern(String key) {
         return ".*?" + key + "\\s*(\\w*)\\s*(\\S*).*";
      }

      @Override
      public String getUpdateCommand(String root, String path, String key, String value, String[] regArray) {
         String type = Registry.REG_SZ;
         if (regArray != null) {
            value = value + File.pathSeparator + regArray[1];
            type = regArray[0];
         }
         return "reg add " + root + "\\" + path + " /v " + key + " /t " + type + " /f /d \"" + value + "\"";
      }

      @Override
      public String isVersion() {
         return "3.0";
      }
   }

   public static boolean isRegVersion(double lowerBound, double upperBound) {
      boolean matched = false;
      String out = Processes.executeCommandToString(Registry.REG_HELP_CMD);

      Pattern pattern = Pattern.compile(".*version\\s(\\d*\\.\\d+).*", Pattern.DOTALL);
      Matcher matcher = pattern.matcher(out.toString());

      if (matcher.matches()) {
         String regVersion = matcher.group(1).trim();

         double foundVersion = Double.parseDouble(regVersion);
         if (foundVersion >= lowerBound && foundVersion <= upperBound) {
            matched = true;
         }
      }
      return matched;
   }

   public static RegVersion getVersion() {
      Registry r = new Registry();
      RegVersion regVersion = null;
      if (Registry.isRegVersion(1.00, 1.99)) {
         regVersion = r.new RegVersion_1_00();
      } else {
         regVersion = r.new RegVersion_3_0();
      }
      return regVersion;
   }

   public static String[] getValue(RegVersion version, String root, String path, String key) {
      String toReturn[] = null;

      String out = Processes.executeCommandToString(version.getQueryCmd(root, path, key));

      Pattern pattern = Pattern.compile(version.getQueryPattern(key), Pattern.DOTALL);
      Matcher matcher = pattern.matcher(out.toString());
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

   public static boolean prependRegistryValue(File updatedReg, String root, String path, String key, String value) {

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
       * If we are using a 1.0 version then use a provided reg.exe executable to use commands. If not operation will
       * fail.
       */
      if (version.isVersion().equals("1.00")) {
         if (updatedReg.exists() && updatedReg.isFile()) {
            command = updatedReg.getAbsolutePath() + command.replaceAll("reg", "");
            ;
         }
      }

      String out = Processes.executeCommandToString(command);
      System.out.println("The string: " + out);

      if (out.toString().contains("err:")) {
         return false;
      }
      return true;
   }

   public static boolean prependRegistryValue(String root, String path, String key, String value) {

      RegVersion version = Registry.getVersion();

      String[] regArray = Registry.getValue(version, root, path, key);

      String out = Processes.executeCommandToString(version.getUpdateCommand(root, path, key, value, regArray));

      System.out.println("The string: " + out);

      if (out.toString().contains("err:")) {
         return false;
      }
      return true;
   }

   public static void main(String[] args) {
      Registry.prependRegistryValue(Registry.HKEY_CURRENT_USER, "environment", "path", "HI THERE");
   }

}
