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
package org.eclipse.osee.framework.jdk.core.util.io;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Provides a way to produce simple lnk files.
 * 
 * @author Robert A. Fisher
 */
public class LnkWriter {

   private String description;
   private String longName;
   private String shortName;
   private char shareDriveLetter;
   private String shareName;
   private String relativePath;
   private String commandLineArguments;
   private String iconFilename;
   private String volumeLabel;
   private String workingDirectory;
   private String localPath;
   private String finalPath;
   private int iconNumber;
   private int fileLength;
   private int volumeSerialNumber;
   private DriveType driveType;

   private static final int DWORD = 4; // A dword is 4 bytes

   private static byte MAGIC_VAL[] = new byte[] {0x4C, 0x00, 0x00, 0x00};
   private static byte GUID[] =
         new byte[] {0x01, 0x14, 0x02, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0xC0, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
               0x46};

   public LnkWriter() {
      this.description = null;
      this.longName = null;
      this.shortName = null;
      this.shareName = null;
      this.relativePath = null;
      this.commandLineArguments = null;
      this.iconFilename = null;
      this.volumeLabel = null;
      this.workingDirectory = null;
      this.localPath = null;
      this.finalPath = null;
      this.iconNumber = 0;
      this.fileLength = 0;
      this.volumeSerialNumber = 0;
      this.driveType = DriveType.Unknown;
   }

   /**
    * @param commandLineArguments The commandLineArguments to set.
    */
   public void setCommandLineArguments(String commandLineArguments) {
      this.commandLineArguments = commandLineArguments;
   }

   /**
    * @param description The description to set.
    */
   public void setDescription(String description) {
      this.description = description;
   }

   /**
    * @param driveType The driveType to set.
    */
   public void setDriveType(DriveType driveType) {
      this.driveType = driveType;
   }

   /**
    * @param fileLength The fileLength to set.
    */
   public void setFileLength(int fileLength) {
      this.fileLength = fileLength;
   }

   /**
    * @param finalPath The finalPath to set.
    */
   public void setFinalPath(String finalPath) {
      this.finalPath = finalPath;
   }

   /**
    * @param iconFilename The iconFilename to set.
    */
   public void setIconFilename(String iconFilename) {
      this.iconFilename = iconFilename;
   }

   /**
    * @param iconNumber The iconNumber to set.
    */
   public void setIconNumber(int iconNumber) {
      this.iconNumber = iconNumber;
   }

   /**
    * @param localPath The localPath to set.
    */
   public void setLocalPath(String localPath) {
      this.localPath = localPath;
   }

   /**
    * @param longName The longName to set.
    */
   public void setLongName(String longName) {
      this.longName = longName;
   }

   /**
    * @param shareName The networkShare to set.
    */
   public void setShareName(String shareName, char driveLetter) {
      this.shareName = shareName;
      this.shareDriveLetter = driveLetter;
   }

   /**
    * @param relativePath The relativePath to set.
    */
   public void setRelativePath(String relativePath) {
      this.relativePath = relativePath;
   }

   /**
    * @param shortName The shortName to set.
    */
   public void setShortName(String shortName) {
      this.shortName = shortName;
   }

   /**
    * @param volumeLabel The volumeLabel to set.
    */
   public void setVolumeLabel(String volumeLabel) {
      this.volumeLabel = volumeLabel;
   }

   /**
    * @param volumeSerialNumber The volumeSerialNumber to set.
    */
   public void setVolumeSerialNumber(int volumeSerialNumber) {
      this.volumeSerialNumber = volumeSerialNumber;
   }

   /**
    * @param workingDirectory The workingDirectory to set.
    */
   public void setWorkingDirectory(String workingDirectory) {
      this.workingDirectory = workingDirectory;
   }

   public static void writeOsee(String shortcutName) throws IOException {
      writeFile(shortcutName, OSEE);
   }

   public static void writeOseeMs(String shortcutName) throws IOException {
      writeFile(shortcutName, OSEE_MS);
   }

   private static void writeFile(String name, byte[] data) throws IOException {
      File outfile = new File(name);
      FileOutputStream out;
      out = new FileOutputStream(outfile);

      ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
      byteOut.write(data);
      byteOut.writeTo(out);
      out.close();
   }

   public void write(String shortcutName) {

      File outfile = new File(shortcutName);
      FileOutputStream out;
      try {
         out = new FileOutputStream(outfile);
      } catch (FileNotFoundException ex) {
         ex.printStackTrace();
         return;
      }

      ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
      try {
         byteOut.write(MAGIC_VAL);
         byteOut.write(GUID);
         byteOut.write(getTheFlags());
         byteOut.write(getFileAttributes());
         byteOut.write(getTimeFields());
         byteOut.write(getFileLength());
         byteOut.write(getIconNumber());
         byteOut.write(getShowWnd());
         byteOut.write(getHotKey());
         byteOut.write(new byte[2 * DWORD]); // There is a 2 DWORD bubble in the format
         byteOut.write(getShellItemIdList());
         byteOut.write(getFileLocationInfo());
         if (description != null) byteOut.write(getStringByteData(description));
         if (relativePath != null) byteOut.write(getStringByteData(relativePath));
         if (workingDirectory != null) byteOut.write(getStringByteData(workingDirectory));
         if (commandLineArguments != null) byteOut.write(getStringByteData(commandLineArguments));
         if (iconFilename != null) byteOut.write(getStringByteData(iconFilename));
         byteOut.write(intToByteArray(0)); // End stuff ...
         byteOut.writeTo(out);
         out.close();
      } catch (IOException ex) {
         ex.printStackTrace();
         return;
      }
   }

   private byte[] getTheFlags() {
      // The flags field is one DWORD
      byte data[] = new byte[DWORD];

      // This will always be the case
      data[0] |= 1 << 0; // Points to a file or directory

      if (description != null) data[0] |= 1 << 1; // Has a description string
      if (relativePath != null) data[0] |= 1 << 2; // Has a relative path string
      if (workingDirectory != null) data[0] |= 1 << 3; // Has a working directory string
      if (commandLineArguments != null) data[0] |= 1 << 4; // Has command line arguments
      if (iconFilename != null) data[0] |= 1 << 5; // Has a custom icon
      return data;
   }

   private byte[] getFileAttributes() {
      // The file attributes field is one DWORD
      byte data[] = new byte[DWORD];

      // Nothing set here yet

      return data;
   }

   private byte[] getTimeFields() {
      // The 3 time fields are 64 bits each
      byte data[] = new byte[3 * 8];

      // Nothing to set here yet

      return data;
   }

   private byte[] getFileLength() {
      return intToByteArray(fileLength);
   }

   private byte[] getIconNumber() {
      return intToByteArray(iconNumber);
   }

   private byte[] getShowWnd() {

      // SW_NORMAL = 1
      // SW_SHOWMINIMIZED = 2
      // SW_SHOWMAXIMIXED = 3
      return intToByteArray(1);
   }

   private byte[] getHotKey() {
      // No hot key setting yet
      return intToByteArray(0);
   }

   private byte[] getShellItemIdList() {

      // First must acquire all items that go within the table
      byte shortNameString[] = getNullTerminatedBytes(shortName.getBytes());
      byte longNameString[] = getNullTerminatedBytes(longName.getBytes());

      // The table has 4.5 dword's in addition to the two strings
      byte data[] = new byte[(int) (4.5 * DWORD) + shortNameString.length + longNameString.length];

      // Put the length of the list (everything except this 2 bytes
      System.arraycopy(shortToByteArray((short) (data.length - 2)), 0, data, 0 * DWORD, 2);

      // Length of the first item (which is this whole block, minus one dword
      System.arraycopy(shortToByteArray((short) (data.length - DWORD)), 0, data, (int) (0.5 * DWORD), 2);

      System.arraycopy(shortToByteArray((short) 0x32), 0, data, 1 * DWORD, 2); // Some mystical
      // value
      System.arraycopy(intToByteArray(fileLength), 0, data, (int) (1.5 * DWORD), 4);
      System.arraycopy(intToByteArray(0x3E712576), 0, data, (int) (2.5 * DWORD), 4);// Some mystical
      // value
      System.arraycopy(shortToByteArray((short) (0x20)), 0, data, (int) (3.5 * DWORD), 2);// Some
      // mystical
      // value

      // Place the longname string in now
      System.arraycopy(longNameString, 0, data, 4 * DWORD, longNameString.length);

      // Place the shortname string right after the longname
      System.arraycopy(shortNameString, 0, data, 4 * DWORD + longNameString.length, shortNameString.length);

      return data;
   }

   private byte[] getFileLocationInfo() {
      // First must acquire all tables that follow this structure
      byte localVolumeTable[] = getLocalVolumeTable();
      byte localPathString[] = (localPath != null) ? getNullTerminatedBytes(localPath.getBytes()) : new byte[0];
      byte networkVolumeTable[] = getNetworkVolumeTable();
      byte finalPathString[] = getNullTerminatedBytes(finalPath.getBytes());

      // The file attributes field is seven DWORD's, plus the structures above
      byte data[] =
            new byte[7 * DWORD + localVolumeTable.length + localPathString.length + networkVolumeTable.length + finalPathString.length];

      // This flag specifies whether local volume, and/or network volume data is available
      int flags = 0;
      if (localVolumeTable.length > 0) flags |= 0x1;
      if (networkVolumeTable.length > 0) flags |= 0x2;

      // Calculate the necessary offets
      int localVolumeOffset = 7 * DWORD;
      int localPathOffset = localVolumeOffset + localVolumeTable.length;
      int networkVolumeOffset = localPathOffset + localPathString.length;
      int finalPathOffset = networkVolumeOffset + networkVolumeTable.length;

      // Put the length value in the first dword
      System.arraycopy(intToByteArray(data.length), 0, data, 0 * DWORD, 4);

      // Write the following words
      System.arraycopy(intToByteArray(0x1C), 0, data, 1 * DWORD, 4); // First offset after this
      // structure
      System.arraycopy(intToByteArray(flags), 0, data, 2 * DWORD, 4);
      System.arraycopy(intToByteArray(localVolumeOffset), 0, data, 3 * DWORD, 4);
      System.arraycopy(intToByteArray(localPathOffset), 0, data, 4 * DWORD, 4);
      System.arraycopy(intToByteArray(networkVolumeOffset), 0, data, 5 * DWORD, 4);
      System.arraycopy(intToByteArray(finalPathOffset), 0, data, 6 * DWORD, 4);

      // Write the referenced blocks
      System.arraycopy(localVolumeTable, 0, data, localVolumeOffset, localVolumeTable.length);
      System.arraycopy(localPathString, 0, data, localPathOffset, localPathString.length);
      System.arraycopy(networkVolumeTable, 0, data, networkVolumeOffset, networkVolumeTable.length);
      System.arraycopy(finalPathString, 0, data, finalPathOffset, finalPathString.length);

      return data;
   }

   private byte[] getLocalVolumeTable() {
      // If missing the label then don't put it in
      if (volumeLabel == null) return new byte[0];

      byte volumeLabelString[] = getNullTerminatedBytes(volumeLabel.getBytes());

      // The first 4 dword's are static, then there is the volume label string
      byte data[] = new byte[(4 * DWORD) + volumeLabelString.length];

      // Put the length value in the first dword
      System.arraycopy(intToByteArray((short) data.length), 0, data, 0 * DWORD, 4);

      // Write the following words
      System.arraycopy(intToByteArray(driveType.getValue()), 0, data, 1 * DWORD, 4);
      System.arraycopy(intToByteArray(volumeSerialNumber), 0, data, 2 * DWORD, 4);
      System.arraycopy(intToByteArray(0x10), 0, data, 3 * DWORD, 4);

      // Place the string right after that
      System.arraycopy(volumeLabelString, 0, data, 4 * DWORD, volumeLabelString.length);

      return data;
   }

   private byte[] getNetworkVolumeTable() {
      // If the network share is null, then no table
      if (shareName == null) return new byte[0];

      byte networkShareString[] = getNullTerminatedBytes(shareName.getBytes());

      // The first 5 dword's are static, then there is the network share name, and drive letter
      byte data[] = new byte[(5 * DWORD) + networkShareString.length + 3];

      // Put the length value in the first dword
      System.arraycopy(intToByteArray(data.length), 0, data, 0 * DWORD, 4);

      // Write the following words
      System.arraycopy(intToByteArray(0x2), 0, data, 1 * DWORD, 4);
      System.arraycopy(intToByteArray(0x14), 0, data, 2 * DWORD, 4);
      System.arraycopy(intToByteArray(0x0), 0, data, 3 * DWORD, 4);
      System.arraycopy(intToByteArray(0x200), 0, data, 4 * DWORD, 4);

      // Place the string right after that, then drive letter
      System.arraycopy(networkShareString, 0, data, 5 * DWORD, networkShareString.length);
      System.arraycopy(new byte[] {(byte) shareDriveLetter, ':'}, 0, data, data.length - 3, 2);

      return data;
   }

   private byte[] getStringByteData(String theString) {
      byte data[] = null;

      if (theString != null) {
         byte string[] = getNullTerminatedBytes(theString.getBytes());

         // The first 2 bytes tell the size of the string
         // The following space is the string itself
         data = new byte[2 + string.length];

         // Put the length value in the first 2 bytes
         System.arraycopy(shortToByteArray((short) string.length), 0, data, 0, 2);

         // Place the string right after that
         System.arraycopy(string, 0, data, 2, string.length);
      }

      return data;
   }

   private byte[] getNullTerminatedBytes(byte string[]) {
      // Need an array one larger for the null terminator
      byte data[] = new byte[string.length + 1];

      // Place the string into the new array
      System.arraycopy(string, 0, data, 0, string.length);

      return data;
   }

   private byte[] shortToByteArray(short val) {
      byte data[] = new byte[2];

      for (int i = 0; i < 2; i++)
         data[i] = (byte) (0xFF & (val >> i * 8));

      return data;
   }

   private byte[] intToByteArray(int val) {
      byte data[] = new byte[4];

      // 32 bit values seem to be in reverse order
      for (int i = 0; i < 4; i++)
         data[i] = (byte) (0xFF & (val >> i * 8));

      return data;
   }

   // This is the binary for the OSEE lnk ;-)
   private static final byte OSEE[] =
         new byte[] {(byte) 0x4C, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x14, (byte) 0x02,
               (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xC0, (byte) 0x00, (byte) 0x00,
               (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x46, (byte) 0xFB, (byte) 0x02, (byte) 0x00,
               (byte) 0x00, (byte) 0x20, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x54, (byte) 0x20, (byte) 0x6E,
               (byte) 0x73, (byte) 0x3B, (byte) 0xF8, (byte) 0xC4, (byte) 0x01, (byte) 0xEE, (byte) 0xDF, (byte) 0x76,
               (byte) 0x9A, (byte) 0xD4, (byte) 0x83, (byte) 0xC5, (byte) 0x01, (byte) 0x7C, (byte) 0xF6, (byte) 0xA1,
               (byte) 0x69, (byte) 0x3B, (byte) 0xF8, (byte) 0xC4, (byte) 0x01, (byte) 0x5F, (byte) 0xC0, (byte) 0x00,
               (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00,
               (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
               (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x75, (byte) 0x00, (byte) 0x14,
               (byte) 0x00, (byte) 0x1F, (byte) 0x50, (byte) 0xE0, (byte) 0x4F, (byte) 0xD0, (byte) 0x20, (byte) 0xEA,
               (byte) 0x3A, (byte) 0x69, (byte) 0x10, (byte) 0xA2, (byte) 0xD8, (byte) 0x08, (byte) 0x00, (byte) 0x2B,
               (byte) 0x30, (byte) 0x30, (byte) 0x9D, (byte) 0x19, (byte) 0x00, (byte) 0x23, (byte) 0x43, (byte) 0x3A,
               (byte) 0x5C, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
               (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
               (byte) 0x00, (byte) 0x00, (byte) 0x91, (byte) 0x65, (byte) 0x15, (byte) 0x00, (byte) 0x31, (byte) 0x00,
               (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xF3, (byte) 0x32, (byte) 0xCA, (byte) 0x7D,
               (byte) 0x30, (byte) 0x00, (byte) 0x57, (byte) 0x49, (byte) 0x4E, (byte) 0x4E, (byte) 0x54, (byte) 0x00,
               (byte) 0x00, (byte) 0x18, (byte) 0x00, (byte) 0x31, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
               (byte) 0x00, (byte) 0xE8, (byte) 0x32, (byte) 0xE1, (byte) 0x6A, (byte) 0x30, (byte) 0x00, (byte) 0x73,
               (byte) 0x79, (byte) 0x73, (byte) 0x74, (byte) 0x65, (byte) 0x6D, (byte) 0x33, (byte) 0x32, (byte) 0x00,
               (byte) 0x00, (byte) 0x19, (byte) 0x00, (byte) 0x32, (byte) 0x00, (byte) 0x5F, (byte) 0xC0, (byte) 0x00,
               (byte) 0x00, (byte) 0x2C, (byte) 0x32, (byte) 0x91, (byte) 0x01, (byte) 0x20, (byte) 0x00, (byte) 0x6A,
               (byte) 0x61, (byte) 0x76, (byte) 0x61, (byte) 0x77, (byte) 0x2E, (byte) 0x65, (byte) 0x78, (byte) 0x65,
               (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x51, (byte) 0x00, (byte) 0x00, (byte) 0x00,
               (byte) 0x1C, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00,
               (byte) 0x1C, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x34, (byte) 0x00, (byte) 0x00, (byte) 0x00,
               (byte) 0x1C, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x50, (byte) 0x00, (byte) 0x00, (byte) 0x00,
               (byte) 0x18, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00,
               (byte) 0xC9, (byte) 0x72, (byte) 0x53, (byte) 0xAC, (byte) 0x10, (byte) 0x00, (byte) 0x00, (byte) 0x00,
               (byte) 0x45, (byte) 0x32, (byte) 0x38, (byte) 0x32, (byte) 0x32, (byte) 0x30, (byte) 0x34, (byte) 0x00,
               (byte) 0x43, (byte) 0x3A, (byte) 0x5C, (byte) 0x57, (byte) 0x49, (byte) 0x4E, (byte) 0x4E, (byte) 0x54,
               (byte) 0x5C, (byte) 0x73, (byte) 0x79, (byte) 0x73, (byte) 0x74, (byte) 0x65, (byte) 0x6D, (byte) 0x33,
               (byte) 0x32, (byte) 0x5C, (byte) 0x6A, (byte) 0x61, (byte) 0x76, (byte) 0x61, (byte) 0x77, (byte) 0x2E,
               (byte) 0x65, (byte) 0x78, (byte) 0x65, (byte) 0x00, (byte) 0x00, (byte) 0x21, (byte) 0x00, (byte) 0x2E,
               (byte) 0x00, (byte) 0x2E, (byte) 0x00, (byte) 0x5C, (byte) 0x00, (byte) 0x2E, (byte) 0x00, (byte) 0x2E,
               (byte) 0x00, (byte) 0x5C, (byte) 0x00, (byte) 0x2E, (byte) 0x00, (byte) 0x2E, (byte) 0x00, (byte) 0x5C,
               (byte) 0x00, (byte) 0x57, (byte) 0x00, (byte) 0x49, (byte) 0x00, (byte) 0x4E, (byte) 0x00, (byte) 0x4E,
               (byte) 0x00, (byte) 0x54, (byte) 0x00, (byte) 0x5C, (byte) 0x00, (byte) 0x73, (byte) 0x00, (byte) 0x79,
               (byte) 0x00, (byte) 0x73, (byte) 0x00, (byte) 0x74, (byte) 0x00, (byte) 0x65, (byte) 0x00, (byte) 0x6D,
               (byte) 0x00, (byte) 0x33, (byte) 0x00, (byte) 0x32, (byte) 0x00, (byte) 0x5C, (byte) 0x00, (byte) 0x6A,
               (byte) 0x00, (byte) 0x61, (byte) 0x00, (byte) 0x76, (byte) 0x00, (byte) 0x61, (byte) 0x00, (byte) 0x77,
               (byte) 0x00, (byte) 0x2E, (byte) 0x00, (byte) 0x65, (byte) 0x00, (byte) 0x78, (byte) 0x00, (byte) 0x65,
               (byte) 0x00, (byte) 0x12, (byte) 0x00, (byte) 0x59, (byte) 0x00, (byte) 0x3A, (byte) 0x00, (byte) 0x5C,
               (byte) 0x00, (byte) 0x4F, (byte) 0x00, (byte) 0x53, (byte) 0x00, (byte) 0x45, (byte) 0x00, (byte) 0x45,
               (byte) 0x00, (byte) 0x5C, (byte) 0x00, (byte) 0x77, (byte) 0x00, (byte) 0x69, (byte) 0x00, (byte) 0x6E,
               (byte) 0x00, (byte) 0x33, (byte) 0x00, (byte) 0x32, (byte) 0x00, (byte) 0x5C, (byte) 0x00, (byte) 0x4F,
               (byte) 0x00, (byte) 0x53, (byte) 0x00, (byte) 0x45, (byte) 0x00, (byte) 0x45, (byte) 0x00, (byte) 0x29,
               (byte) 0x00, (byte) 0x2D, (byte) 0x00, (byte) 0x58, (byte) 0x00, (byte) 0x6D, (byte) 0x00, (byte) 0x78,
               (byte) 0x00, (byte) 0x35, (byte) 0x00, (byte) 0x31, (byte) 0x00, (byte) 0x32, (byte) 0x00, (byte) 0x4D,
               (byte) 0x00, (byte) 0x20, (byte) 0x00, (byte) 0x2D, (byte) 0x00, (byte) 0x6A, (byte) 0x00, (byte) 0x61,
               (byte) 0x00, (byte) 0x72, (byte) 0x00, (byte) 0x20, (byte) 0x00, (byte) 0x59, (byte) 0x00, (byte) 0x3A,
               (byte) 0x00, (byte) 0x5C, (byte) 0x00, (byte) 0x4F, (byte) 0x00, (byte) 0x53, (byte) 0x00, (byte) 0x45,
               (byte) 0x00, (byte) 0x45, (byte) 0x00, (byte) 0x5C, (byte) 0x00, (byte) 0x77, (byte) 0x00, (byte) 0x69,
               (byte) 0x00, (byte) 0x6E, (byte) 0x00, (byte) 0x33, (byte) 0x00, (byte) 0x32, (byte) 0x00, (byte) 0x5C,
               (byte) 0x00, (byte) 0x4F, (byte) 0x00, (byte) 0x53, (byte) 0x00, (byte) 0x45, (byte) 0x00, (byte) 0x45,
               (byte) 0x00, (byte) 0x5C, (byte) 0x00, (byte) 0x4F, (byte) 0x00, (byte) 0x53, (byte) 0x00, (byte) 0x45,
               (byte) 0x00, (byte) 0x45, (byte) 0x00, (byte) 0x2E, (byte) 0x00, (byte) 0x6A, (byte) 0x00, (byte) 0x61,
               (byte) 0x00, (byte) 0x72, (byte) 0x00, (byte) 0x1B, (byte) 0x00, (byte) 0x59, (byte) 0x00, (byte) 0x3A,
               (byte) 0x00, (byte) 0x5C, (byte) 0x00, (byte) 0x4F, (byte) 0x00, (byte) 0x53, (byte) 0x00, (byte) 0x45,
               (byte) 0x00, (byte) 0x45, (byte) 0x00, (byte) 0x5C, (byte) 0x00, (byte) 0x77, (byte) 0x00, (byte) 0x69,
               (byte) 0x00, (byte) 0x6E, (byte) 0x00, (byte) 0x33, (byte) 0x00, (byte) 0x32, (byte) 0x00, (byte) 0x5C,
               (byte) 0x00, (byte) 0x4F, (byte) 0x00, (byte) 0x53, (byte) 0x00, (byte) 0x45, (byte) 0x00, (byte) 0x45,
               (byte) 0x00, (byte) 0x5C, (byte) 0x00, (byte) 0x4F, (byte) 0x00, (byte) 0x53, (byte) 0x00, (byte) 0x45,
               (byte) 0x00, (byte) 0x45, (byte) 0x00, (byte) 0x2E, (byte) 0x00, (byte) 0x69, (byte) 0x00, (byte) 0x63,
               (byte) 0x00, (byte) 0x6F, (byte) 0x00, (byte) 0x14, (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x01,
               (byte) 0x00, (byte) 0x00, (byte) 0xA0, (byte) 0x25, (byte) 0x53, (byte) 0x79, (byte) 0x73, (byte) 0x74,
               (byte) 0x65, (byte) 0x6D, (byte) 0x52, (byte) 0x6F, (byte) 0x6F, (byte) 0x74, (byte) 0x25, (byte) 0x5C,
               (byte) 0x73, (byte) 0x79, (byte) 0x73, (byte) 0x74, (byte) 0x65, (byte) 0x6D, (byte) 0x33, (byte) 0x32,
               (byte) 0x5C, (byte) 0x6A, (byte) 0x61, (byte) 0x76, (byte) 0x61, (byte) 0x77, (byte) 0x2E, (byte) 0x65,
               (byte) 0x78, (byte) 0x65, (byte) 0x00, (byte) 0x4C, (byte) 0x82, (byte) 0x57, (byte) 0x7C, (byte) 0x7B,
               (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x33, (byte) 0x00, (byte) 0x00, (byte) 0xC0, (byte) 0x78,
               (byte) 0x74, (byte) 0x58, (byte) 0x7C, (byte) 0x33, (byte) 0x00, (byte) 0x00, (byte) 0xC0, (byte) 0xE8,
               (byte) 0xF1, (byte) 0xE3, (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xC3,
               (byte) 0x52, (byte) 0x58, (byte) 0x7C, (byte) 0x0D, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x18,
               (byte) 0xEB, (byte) 0xE3, (byte) 0x03, (byte) 0xFC, (byte) 0x5F, (byte) 0xE4, (byte) 0x77, (byte) 0x30,
               (byte) 0x75, (byte) 0x48, (byte) 0x00, (byte) 0x0D, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x04,
               (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0xDC, (byte) 0xEF, (byte) 0xE3, (byte) 0x03, (byte) 0x00,
               (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x6C, (byte) 0x41, (byte) 0xB9, (byte) 0xB3,
               (byte) 0x5F, (byte) 0xE4, (byte) 0x77, (byte) 0x18, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xF0,
               (byte) 0x08, (byte) 0x00, (byte) 0x00, (byte) 0x3C, (byte) 0xEB, (byte) 0xE3, (byte) 0x03, (byte) 0x40,
               (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
               (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x92, (byte) 0x00, (byte) 0x92, (byte) 0x00, (byte) 0xB6,
               (byte) 0xE8, (byte) 0x56, (byte) 0x04, (byte) 0xF0, (byte) 0x08, (byte) 0x00, (byte) 0x00, (byte) 0x92,
               (byte) 0x00, (byte) 0x92, (byte) 0x00, (byte) 0xB6, (byte) 0xE8, (byte) 0x56, (byte) 0x04, (byte) 0x64,
               (byte) 0xED, (byte) 0xE3, (byte) 0x03, (byte) 0x5B, (byte) 0xDA, (byte) 0xA9, (byte) 0x70, (byte) 0xE8,
               (byte) 0xF1, (byte) 0xE3, (byte) 0x03, (byte) 0xDC, (byte) 0xEF, (byte) 0xE3, (byte) 0x03, (byte) 0x00,
               (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xD8, (byte) 0xEC, (byte) 0xE3, (byte) 0x03, (byte) 0x7C,
               (byte) 0xEB, (byte) 0xE3, (byte) 0x03, (byte) 0x13, (byte) 0x50, (byte) 0xE2, (byte) 0x77, (byte) 0xB3,
               (byte) 0x5F, (byte) 0xE4, (byte) 0x77, (byte) 0x7A, (byte) 0x03, (byte) 0x0D, (byte) 0x00, (byte) 0x0D,
               (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x04, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0xDC,
               (byte) 0xEF, (byte) 0xE3, (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x98,
               (byte) 0xEB, (byte) 0xE3, (byte) 0x03, (byte) 0xF0, (byte) 0x21, (byte) 0x75, (byte) 0x71, (byte) 0xB3,
               (byte) 0x5F, (byte) 0xE4, (byte) 0x77, (byte) 0x7A, (byte) 0x03, (byte) 0x0D, (byte) 0x00, (byte) 0x0D,
               (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x04, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0xDC,
               (byte) 0xEF, (byte) 0xE3, (byte) 0x03, (byte) 0xF4, (byte) 0xEB, (byte) 0xE3, (byte) 0x03, (byte) 0x6F,
               (byte) 0x27, (byte) 0x75, (byte) 0x71, (byte) 0x7A, (byte) 0x03, (byte) 0x0D, (byte) 0x00, (byte) 0x0D,
               (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x04, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0xDC,
               (byte) 0xEF, (byte) 0xE3, (byte) 0x03, (byte) 0x9B, (byte) 0x27, (byte) 0x75, (byte) 0x71, (byte) 0x25,
               (byte) 0x00, (byte) 0x53, (byte) 0x00, (byte) 0x79, (byte) 0x00, (byte) 0x73, (byte) 0x00, (byte) 0x74,
               (byte) 0x00, (byte) 0x65, (byte) 0x00, (byte) 0x6D, (byte) 0x00, (byte) 0x52, (byte) 0x00, (byte) 0x6F,
               (byte) 0x00, (byte) 0x6F, (byte) 0x00, (byte) 0x74, (byte) 0x00, (byte) 0x25, (byte) 0x00, (byte) 0x5C,
               (byte) 0x00, (byte) 0x73, (byte) 0x00, (byte) 0x79, (byte) 0x00, (byte) 0x73, (byte) 0x00, (byte) 0x74,
               (byte) 0x00, (byte) 0x65, (byte) 0x00, (byte) 0x6D, (byte) 0x00, (byte) 0x33, (byte) 0x00, (byte) 0x32,
               (byte) 0x00, (byte) 0x5C, (byte) 0x00, (byte) 0x6A, (byte) 0x00, (byte) 0x61, (byte) 0x00, (byte) 0x76,
               (byte) 0x00, (byte) 0x61, (byte) 0x00, (byte) 0x77, (byte) 0x00, (byte) 0x2E, (byte) 0x00, (byte) 0x65,
               (byte) 0x00, (byte) 0x78, (byte) 0x00, (byte) 0x65, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x18,
               (byte) 0xEC, (byte) 0xE3, (byte) 0x03, (byte) 0x74, (byte) 0x25, (byte) 0x75, (byte) 0x71, (byte) 0x20,
               (byte) 0xFC, (byte) 0x51, (byte) 0x04, (byte) 0x7A, (byte) 0xF2, (byte) 0xE3, (byte) 0x03, (byte) 0x00,
               (byte) 0x00, (byte) 0x07, (byte) 0x00, (byte) 0x54, (byte) 0xEC, (byte) 0xE3, (byte) 0x03, (byte) 0xD0,
               (byte) 0x57, (byte) 0xF8, (byte) 0x77, (byte) 0xEC, (byte) 0x57, (byte) 0xF8, (byte) 0x77, (byte) 0x08,
               (byte) 0x06, (byte) 0x07, (byte) 0x00, (byte) 0x30, (byte) 0x00, (byte) 0x0D, (byte) 0x01, (byte) 0x05,
               (byte) 0x96, (byte) 0x57, (byte) 0x7C, (byte) 0x00, (byte) 0x00, (byte) 0x07, (byte) 0x00, (byte) 0x7A,
               (byte) 0xF2, (byte) 0xE3, (byte) 0x03, (byte) 0xE8, (byte) 0x3C, (byte) 0x0F, (byte) 0x00, (byte) 0x49,
               (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x30, (byte) 0x00, (byte) 0x0D, (byte) 0x01, (byte) 0x30,
               (byte) 0x00, (byte) 0x0D, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x24,
               (byte) 0xEC, (byte) 0xE3, (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xE0,
               (byte) 0xED, (byte) 0xE3, (byte) 0x03, (byte) 0x44, (byte) 0x1F, (byte) 0x5C, (byte) 0x7C, (byte) 0x50,
               (byte) 0x24, (byte) 0x57, (byte) 0x7C, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0x78,
               (byte) 0xEC, (byte) 0xE3, (byte) 0x03, (byte) 0x30, (byte) 0x77, (byte) 0xE1, (byte) 0x77, (byte) 0x34,
               (byte) 0x00, (byte) 0x0D, (byte) 0x01, (byte) 0x87, (byte) 0x78, (byte) 0xE1, (byte) 0x77, (byte) 0x34,
               (byte) 0x00, (byte) 0x0D, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x2F, (byte) 0x78, (byte) 0xB2,
               (byte) 0xFC, (byte) 0x51, (byte) 0x04, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
               (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x90, (byte) 0xEC, (byte) 0xE3, (byte) 0x03, (byte) 0x0A,
               (byte) 0x78, (byte) 0xE1, (byte) 0x77, (byte) 0xE8, (byte) 0x3C, (byte) 0x0F, (byte) 0x00, (byte) 0xE8,
               (byte) 0x3C, (byte) 0x0F, (byte) 0x00, (byte) 0x30, (byte) 0x75, (byte) 0x48, (byte) 0x00, (byte) 0x7A,
               (byte) 0x03, (byte) 0x0D, (byte) 0x00, (byte) 0xCC, (byte) 0xEC, (byte) 0xE3, (byte) 0x03, (byte) 0x05,
               (byte) 0x7B, (byte) 0xE1, (byte) 0x77, (byte) 0xE8, (byte) 0x3C, (byte) 0x0F, (byte) 0x00, (byte) 0x04,
               (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0xE8, (byte) 0xF1, (byte) 0xE3, (byte) 0x03, (byte) 0x01,
               (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xA8, (byte) 0x19, (byte) 0x2F, (byte) 0x00, (byte) 0x30,
               (byte) 0x75, (byte) 0x48, (byte) 0x00, (byte) 0x7A, (byte) 0x03, (byte) 0x0D, (byte) 0x00, (byte) 0x20,
               (byte) 0x34, (byte) 0xE1, (byte) 0x77, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0x08,
               (byte) 0xED, (byte) 0xE3, (byte) 0x03, (byte) 0x76, (byte) 0x26, (byte) 0x75, (byte) 0x71, (byte) 0x7A,
               (byte) 0x03, (byte) 0x0D, (byte) 0x00, (byte) 0x49, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xF0,
               (byte) 0xEC, (byte) 0xE3, (byte) 0x03, (byte) 0xD7, (byte) 0x79, (byte) 0xE1, (byte) 0x77, (byte) 0x30,
               (byte) 0x75, (byte) 0x48, (byte) 0x00, (byte) 0x0D, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x04,
               (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0xE8, (byte) 0xF1, (byte) 0xE3, (byte) 0x03, (byte) 0xA8,
               (byte) 0x19, (byte) 0x2F, (byte) 0x00, (byte) 0x30, (byte) 0x75, (byte) 0x48, (byte) 0x00, (byte) 0x0D,
               (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x14, (byte) 0xED, (byte) 0xE3, (byte) 0x03, (byte) 0xFC,
               (byte) 0x5F, (byte) 0xE4, (byte) 0x77, (byte) 0x30, (byte) 0x75, (byte) 0x48, (byte) 0x00, (byte) 0x0D,
               (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x04, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0xE8,
               (byte) 0xF1, (byte) 0xE3, (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x0D,
               (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xB3, (byte) 0x5F, (byte) 0xE4, (byte) 0x77, (byte) 0x34,
               (byte) 0xED, (byte) 0xE3, (byte) 0x03, (byte) 0xD0, (byte) 0xA3, (byte) 0xE3, (byte) 0x77, (byte) 0x7A,
               (byte) 0x03, (byte) 0x0D, (byte) 0x00, (byte) 0x0D, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x04,
               (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0xE8, (byte) 0xF1, (byte) 0xE3, (byte) 0x03, (byte) 0xB3,
               (byte) 0x5F, (byte) 0xE4, (byte) 0x77, (byte) 0xCD, (byte) 0xAB, (byte) 0xBA, (byte) 0xDC, (byte) 0x58,
               (byte) 0xED, (byte) 0xE3, (byte) 0x03, (byte) 0xA6, (byte) 0x52, (byte) 0x58, (byte) 0x7C, (byte) 0xFF,
               (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0x0C, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x50,
               (byte) 0xED, (byte) 0xE3, (byte) 0x03, (byte) 0xEF, (byte) 0x52, (byte) 0x58, (byte) 0x7C, (byte) 0xFF,
               (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0x0C, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x60,
               (byte) 0xED, (byte) 0xE3, (byte) 0x03, (byte) 0x04, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
               (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x80, (byte) 0x00, (byte) 0x00, (byte) 0xA4,
               (byte) 0xEF, (byte) 0xE3, (byte) 0x03, (byte) 0x75, (byte) 0x4E, (byte) 0xAA, (byte) 0x70, (byte) 0x01,
               (byte) 0x80, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xE8,
               (byte) 0xF1, (byte) 0xE3, (byte) 0x03, (byte) 0xD8, (byte) 0x8F, (byte) 0x52, (byte) 0x04, (byte) 0xF0,
               (byte) 0x21, (byte) 0x75, (byte) 0x71, (byte) 0xB3, (byte) 0x5F, (byte) 0xE4, (byte) 0x77, (byte) 0x7A,
               (byte) 0x03, (byte) 0x0D, (byte) 0x00, (byte) 0x0D, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x04,
               (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0xE8, (byte) 0xF1, (byte) 0xE3, (byte) 0x03, (byte) 0xF0,
               (byte) 0xED, (byte) 0xE3, (byte) 0x03, (byte) 0x6F, (byte) 0x27, (byte) 0x75, (byte) 0x71, (byte) 0x7A,
               (byte) 0x03, (byte) 0x0D, (byte) 0x00, (byte) 0x0D, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x04,
               (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0xE8, (byte) 0xF1, (byte) 0xE3, (byte) 0x03, (byte) 0x9B,
               (byte) 0x27, (byte) 0x75, (byte) 0x71, (byte) 0xD4, (byte) 0xEE, (byte) 0xE3, (byte) 0x03, (byte) 0x00,
               (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x80, (byte) 0x04, (byte) 0x52, (byte) 0x04, (byte) 0x60,
               (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0xA0, (byte) 0x58,
               (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x65,
               (byte) 0x32, (byte) 0x38, (byte) 0x32, (byte) 0x32, (byte) 0x30, (byte) 0x34, (byte) 0x00, (byte) 0x00,
               (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x06,
               (byte) 0xA0, (byte) 0x78, (byte) 0xF9, (byte) 0x08, (byte) 0x80, (byte) 0xFF, (byte) 0x4E, (byte) 0x9C,
               (byte) 0x47, (byte) 0xF1, (byte) 0xD0, (byte) 0x77, (byte) 0xFE, (byte) 0x54, (byte) 0xB6, (byte) 0x46,
               (byte) 0x18, (byte) 0x99, (byte) 0x1F, (byte) 0xA2, (byte) 0xE1, (byte) 0xD9, (byte) 0x11, (byte) 0x86,
               (byte) 0xC3, (byte) 0x00, (byte) 0x0F, (byte) 0x1F, (byte) 0xD8, (byte) 0x1B, (byte) 0x0F, (byte) 0x06,
               (byte) 0xA0, (byte) 0x78, (byte) 0xF9, (byte) 0x08, (byte) 0x80, (byte) 0xFF, (byte) 0x4E, (byte) 0x9C,
               (byte) 0x47, (byte) 0xF1, (byte) 0xD0, (byte) 0x77, (byte) 0xFE, (byte) 0x54, (byte) 0xB6, (byte) 0x46,
               (byte) 0x18, (byte) 0x99, (byte) 0x1F, (byte) 0xA2, (byte) 0xE1, (byte) 0xD9, (byte) 0x11, (byte) 0x86,
               (byte) 0xC3, (byte) 0x00, (byte) 0x0F, (byte) 0x1F, (byte) 0xD8, (byte) 0x1B, (byte) 0x0F, (byte) 0x10,
               (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x05, (byte) 0x00, (byte) 0x00, (byte) 0xA0, (byte) 0x24,
               (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x42, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
               (byte) 0x00, (byte) 0x00, (byte) 0x00};

   // This is the binary for the OSEE_MS lnk ;-)
   private static final byte[] OSEE_MS =
         new byte[] {(byte) 0x4C, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x14, (byte) 0x02,
               (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xC0, (byte) 0x00, (byte) 0x00,
               (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x46, (byte) 0xFB, (byte) 0x02, (byte) 0x00,
               (byte) 0x00, (byte) 0x20, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x54, (byte) 0x20, (byte) 0x6E,
               (byte) 0x73, (byte) 0x3B, (byte) 0xF8, (byte) 0xC4, (byte) 0x01, (byte) 0xAB, (byte) 0x00, (byte) 0xE4,
               (byte) 0x8F, (byte) 0xA2, (byte) 0x9E, (byte) 0xC5, (byte) 0x01, (byte) 0x7C, (byte) 0xF6, (byte) 0xA1,
               (byte) 0x69, (byte) 0x3B, (byte) 0xF8, (byte) 0xC4, (byte) 0x01, (byte) 0x5F, (byte) 0xC0, (byte) 0x00,
               (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00,
               (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
               (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x9E, (byte) 0x00, (byte) 0x14,
               (byte) 0x00, (byte) 0x1F, (byte) 0x50, (byte) 0xE0, (byte) 0x4F, (byte) 0xD0, (byte) 0x20, (byte) 0xEA,
               (byte) 0x3A, (byte) 0x69, (byte) 0x10, (byte) 0xA2, (byte) 0xD8, (byte) 0x08, (byte) 0x00, (byte) 0x2B,
               (byte) 0x30, (byte) 0x30, (byte) 0x9D, (byte) 0x19, (byte) 0x00, (byte) 0x23, (byte) 0x43, (byte) 0x3A,
               (byte) 0x5C, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
               (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
               (byte) 0x00, (byte) 0x00, (byte) 0x91, (byte) 0x65, (byte) 0x15, (byte) 0x00, (byte) 0x31, (byte) 0x00,
               (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x17, (byte) 0x33, (byte) 0xB5, (byte) 0xBB,
               (byte) 0x30, (byte) 0x00, (byte) 0x57, (byte) 0x49, (byte) 0x4E, (byte) 0x4E, (byte) 0x54, (byte) 0x00,
               (byte) 0x00, (byte) 0x18, (byte) 0x00, (byte) 0x31, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
               (byte) 0x00, (byte) 0x08, (byte) 0x33, (byte) 0x1D, (byte) 0x6E, (byte) 0x30, (byte) 0x00, (byte) 0x73,
               (byte) 0x79, (byte) 0x73, (byte) 0x74, (byte) 0x65, (byte) 0x6D, (byte) 0x33, (byte) 0x32, (byte) 0x00,
               (byte) 0x00, (byte) 0x42, (byte) 0x00, (byte) 0x32, (byte) 0x00, (byte) 0x5F, (byte) 0xC0, (byte) 0x00,
               (byte) 0x00, (byte) 0xE5, (byte) 0x32, (byte) 0x10, (byte) 0x89, (byte) 0x20, (byte) 0x00, (byte) 0x6A,
               (byte) 0x61, (byte) 0x76, (byte) 0x61, (byte) 0x77, (byte) 0x2E, (byte) 0x65, (byte) 0x78, (byte) 0x65,
               (byte) 0x00, (byte) 0x2A, (byte) 0x00, (byte) 0x03, (byte) 0x00, (byte) 0x04, (byte) 0x00, (byte) 0xEF,
               (byte) 0xBE, (byte) 0xE5, (byte) 0x32, (byte) 0x19, (byte) 0x89, (byte) 0x04, (byte) 0x33, (byte) 0x60,
               (byte) 0x8D, (byte) 0x14, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x6A, (byte) 0x00, (byte) 0x61,
               (byte) 0x00, (byte) 0x76, (byte) 0x00, (byte) 0x61, (byte) 0x00, (byte) 0x77, (byte) 0x00, (byte) 0x2E,
               (byte) 0x00, (byte) 0x65, (byte) 0x00, (byte) 0x78, (byte) 0x00, (byte) 0x65, (byte) 0x00, (byte) 0x00,
               (byte) 0x00, (byte) 0x18, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x4C, (byte) 0x00, (byte) 0x00,
               (byte) 0x00, (byte) 0x1C, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00,
               (byte) 0x00, (byte) 0x1C, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x2D, (byte) 0x00, (byte) 0x00,
               (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x4B, (byte) 0x00, (byte) 0x00,
               (byte) 0x00, (byte) 0x11, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03, (byte) 0x00, (byte) 0x00,
               (byte) 0x00, (byte) 0x34, (byte) 0xCC, (byte) 0xE0, (byte) 0x84, (byte) 0x10, (byte) 0x00, (byte) 0x00,
               (byte) 0x00, (byte) 0x00, (byte) 0x43, (byte) 0x3A, (byte) 0x5C, (byte) 0x57, (byte) 0x49, (byte) 0x4E,
               (byte) 0x44, (byte) 0x4F, (byte) 0x57, (byte) 0x53, (byte) 0x5C, (byte) 0x73, (byte) 0x79, (byte) 0x73,
               (byte) 0x74, (byte) 0x65, (byte) 0x6D, (byte) 0x33, (byte) 0x32, (byte) 0x5C, (byte) 0x6A, (byte) 0x61,
               (byte) 0x76, (byte) 0x61, (byte) 0x77, (byte) 0x2E, (byte) 0x65, (byte) 0x78, (byte) 0x65, (byte) 0x00,
               (byte) 0x00, (byte) 0x1A, (byte) 0x00, (byte) 0x2E, (byte) 0x00, (byte) 0x5C, (byte) 0x00, (byte) 0x57,
               (byte) 0x00, (byte) 0x49, (byte) 0x00, (byte) 0x4E, (byte) 0x00, (byte) 0x4E, (byte) 0x00, (byte) 0x54,
               (byte) 0x00, (byte) 0x5C, (byte) 0x00, (byte) 0x73, (byte) 0x00, (byte) 0x79, (byte) 0x00, (byte) 0x73,
               (byte) 0x00, (byte) 0x74, (byte) 0x00, (byte) 0x65, (byte) 0x00, (byte) 0x6D, (byte) 0x00, (byte) 0x33,
               (byte) 0x00, (byte) 0x32, (byte) 0x00, (byte) 0x5C, (byte) 0x00, (byte) 0x6A, (byte) 0x00, (byte) 0x61,
               (byte) 0x00, (byte) 0x76, (byte) 0x00, (byte) 0x61, (byte) 0x00, (byte) 0x77, (byte) 0x00, (byte) 0x2E,
               (byte) 0x00, (byte) 0x65, (byte) 0x00, (byte) 0x78, (byte) 0x00, (byte) 0x65, (byte) 0x00, (byte) 0x15,
               (byte) 0x00, (byte) 0x59, (byte) 0x00, (byte) 0x3A, (byte) 0x00, (byte) 0x5C, (byte) 0x00, (byte) 0x4F,
               (byte) 0x00, (byte) 0x53, (byte) 0x00, (byte) 0x45, (byte) 0x00, (byte) 0x45, (byte) 0x00, (byte) 0x5C,
               (byte) 0x00, (byte) 0x77, (byte) 0x00, (byte) 0x69, (byte) 0x00, (byte) 0x6E, (byte) 0x00, (byte) 0x33,
               (byte) 0x00, (byte) 0x32, (byte) 0x00, (byte) 0x5C, (byte) 0x00, (byte) 0x4F, (byte) 0x00, (byte) 0x53,
               (byte) 0x00, (byte) 0x45, (byte) 0x00, (byte) 0x45, (byte) 0x00, (byte) 0x5F, (byte) 0x00, (byte) 0x4D,
               (byte) 0x00, (byte) 0x53, (byte) 0x00, (byte) 0x2C, (byte) 0x00, (byte) 0x2D, (byte) 0x00, (byte) 0x58,
               (byte) 0x00, (byte) 0x6D, (byte) 0x00, (byte) 0x78, (byte) 0x00, (byte) 0x35, (byte) 0x00, (byte) 0x31,
               (byte) 0x00, (byte) 0x32, (byte) 0x00, (byte) 0x4D, (byte) 0x00, (byte) 0x20, (byte) 0x00, (byte) 0x2D,
               (byte) 0x00, (byte) 0x6A, (byte) 0x00, (byte) 0x61, (byte) 0x00, (byte) 0x72, (byte) 0x00, (byte) 0x20,
               (byte) 0x00, (byte) 0x59, (byte) 0x00, (byte) 0x3A, (byte) 0x00, (byte) 0x5C, (byte) 0x00, (byte) 0x4F,
               (byte) 0x00, (byte) 0x53, (byte) 0x00, (byte) 0x45, (byte) 0x00, (byte) 0x45, (byte) 0x00, (byte) 0x5C,
               (byte) 0x00, (byte) 0x77, (byte) 0x00, (byte) 0x69, (byte) 0x00, (byte) 0x6E, (byte) 0x00, (byte) 0x33,
               (byte) 0x00, (byte) 0x32, (byte) 0x00, (byte) 0x5C, (byte) 0x00, (byte) 0x4F, (byte) 0x00, (byte) 0x53,
               (byte) 0x00, (byte) 0x45, (byte) 0x00, (byte) 0x45, (byte) 0x00, (byte) 0x5F, (byte) 0x00, (byte) 0x4D,
               (byte) 0x00, (byte) 0x53, (byte) 0x00, (byte) 0x5C, (byte) 0x00, (byte) 0x4F, (byte) 0x00, (byte) 0x53,
               (byte) 0x00, (byte) 0x45, (byte) 0x00, (byte) 0x45, (byte) 0x00, (byte) 0x2E, (byte) 0x00, (byte) 0x6A,
               (byte) 0x00, (byte) 0x61, (byte) 0x00, (byte) 0x72, (byte) 0x00, (byte) 0x1B, (byte) 0x00, (byte) 0x59,
               (byte) 0x00, (byte) 0x3A, (byte) 0x00, (byte) 0x5C, (byte) 0x00, (byte) 0x4F, (byte) 0x00, (byte) 0x53,
               (byte) 0x00, (byte) 0x45, (byte) 0x00, (byte) 0x45, (byte) 0x00, (byte) 0x5C, (byte) 0x00, (byte) 0x77,
               (byte) 0x00, (byte) 0x69, (byte) 0x00, (byte) 0x6E, (byte) 0x00, (byte) 0x33, (byte) 0x00, (byte) 0x32,
               (byte) 0x00, (byte) 0x5C, (byte) 0x00, (byte) 0x4F, (byte) 0x00, (byte) 0x53, (byte) 0x00, (byte) 0x45,
               (byte) 0x00, (byte) 0x45, (byte) 0x00, (byte) 0x5C, (byte) 0x00, (byte) 0x4F, (byte) 0x00, (byte) 0x53,
               (byte) 0x00, (byte) 0x45, (byte) 0x00, (byte) 0x45, (byte) 0x00, (byte) 0x2E, (byte) 0x00, (byte) 0x69,
               (byte) 0x00, (byte) 0x63, (byte) 0x00, (byte) 0x6F, (byte) 0x00, (byte) 0x14, (byte) 0x03, (byte) 0x00,
               (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0xA0, (byte) 0x25, (byte) 0x53, (byte) 0x79,
               (byte) 0x73, (byte) 0x74, (byte) 0x65, (byte) 0x6D, (byte) 0x52, (byte) 0x6F, (byte) 0x6F, (byte) 0x74,
               (byte) 0x25, (byte) 0x5C, (byte) 0x73, (byte) 0x79, (byte) 0x73, (byte) 0x74, (byte) 0x65, (byte) 0x6D,
               (byte) 0x33, (byte) 0x32, (byte) 0x5C, (byte) 0x6A, (byte) 0x61, (byte) 0x76, (byte) 0x61, (byte) 0x77,
               (byte) 0x2E, (byte) 0x65, (byte) 0x78, (byte) 0x65, (byte) 0x00, (byte) 0x4C, (byte) 0x82, (byte) 0x57,
               (byte) 0x7C, (byte) 0x7B, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x33, (byte) 0x00, (byte) 0x00,
               (byte) 0xC0, (byte) 0x78, (byte) 0x74, (byte) 0x58, (byte) 0x7C, (byte) 0x33, (byte) 0x00, (byte) 0x00,
               (byte) 0xC0, (byte) 0xE8, (byte) 0xF1, (byte) 0xE3, (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00,
               (byte) 0x00, (byte) 0xC3, (byte) 0x52, (byte) 0x58, (byte) 0x7C, (byte) 0x0D, (byte) 0x00, (byte) 0x00,
               (byte) 0x00, (byte) 0x18, (byte) 0xEB, (byte) 0xE3, (byte) 0x03, (byte) 0xFC, (byte) 0x5F, (byte) 0xE4,
               (byte) 0x77, (byte) 0x30, (byte) 0x75, (byte) 0x48, (byte) 0x00, (byte) 0x0D, (byte) 0x00, (byte) 0x00,
               (byte) 0x00, (byte) 0x04, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0xDC, (byte) 0xEF, (byte) 0xE3,
               (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x6C, (byte) 0x41,
               (byte) 0xB9, (byte) 0xB3, (byte) 0x5F, (byte) 0xE4, (byte) 0x77, (byte) 0x18, (byte) 0x00, (byte) 0x00,
               (byte) 0x00, (byte) 0xF0, (byte) 0x08, (byte) 0x00, (byte) 0x00, (byte) 0x3C, (byte) 0xEB, (byte) 0xE3,
               (byte) 0x03, (byte) 0x40, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
               (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x92, (byte) 0x00, (byte) 0x92,
               (byte) 0x00, (byte) 0xB6, (byte) 0xE8, (byte) 0x56, (byte) 0x04, (byte) 0xF0, (byte) 0x08, (byte) 0x00,
               (byte) 0x00, (byte) 0x92, (byte) 0x00, (byte) 0x92, (byte) 0x00, (byte) 0xB6, (byte) 0xE8, (byte) 0x56,
               (byte) 0x04, (byte) 0x64, (byte) 0xED, (byte) 0xE3, (byte) 0x03, (byte) 0x5B, (byte) 0xDA, (byte) 0xA9,
               (byte) 0x70, (byte) 0xE8, (byte) 0xF1, (byte) 0xE3, (byte) 0x03, (byte) 0xDC, (byte) 0xEF, (byte) 0xE3,
               (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xD8, (byte) 0xEC, (byte) 0xE3,
               (byte) 0x03, (byte) 0x7C, (byte) 0xEB, (byte) 0xE3, (byte) 0x03, (byte) 0x13, (byte) 0x50, (byte) 0xE2,
               (byte) 0x77, (byte) 0xB3, (byte) 0x5F, (byte) 0xE4, (byte) 0x77, (byte) 0x7A, (byte) 0x03, (byte) 0x0D,
               (byte) 0x00, (byte) 0x0D, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x04, (byte) 0x01, (byte) 0x00,
               (byte) 0x00, (byte) 0xDC, (byte) 0xEF, (byte) 0xE3, (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00,
               (byte) 0x00, (byte) 0x98, (byte) 0xEB, (byte) 0xE3, (byte) 0x03, (byte) 0xF0, (byte) 0x21, (byte) 0x75,
               (byte) 0x71, (byte) 0xB3, (byte) 0x5F, (byte) 0xE4, (byte) 0x77, (byte) 0x7A, (byte) 0x03, (byte) 0x0D,
               (byte) 0x00, (byte) 0x0D, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x04, (byte) 0x01, (byte) 0x00,
               (byte) 0x00, (byte) 0xDC, (byte) 0xEF, (byte) 0xE3, (byte) 0x03, (byte) 0xF4, (byte) 0xEB, (byte) 0xE3,
               (byte) 0x03, (byte) 0x6F, (byte) 0x27, (byte) 0x75, (byte) 0x71, (byte) 0x7A, (byte) 0x03, (byte) 0x0D,
               (byte) 0x00, (byte) 0x0D, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x04, (byte) 0x01, (byte) 0x00,
               (byte) 0x00, (byte) 0xDC, (byte) 0xEF, (byte) 0xE3, (byte) 0x03, (byte) 0x9B, (byte) 0x27, (byte) 0x75,
               (byte) 0x71, (byte) 0x25, (byte) 0x00, (byte) 0x53, (byte) 0x00, (byte) 0x79, (byte) 0x00, (byte) 0x73,
               (byte) 0x00, (byte) 0x74, (byte) 0x00, (byte) 0x65, (byte) 0x00, (byte) 0x6D, (byte) 0x00, (byte) 0x52,
               (byte) 0x00, (byte) 0x6F, (byte) 0x00, (byte) 0x6F, (byte) 0x00, (byte) 0x74, (byte) 0x00, (byte) 0x25,
               (byte) 0x00, (byte) 0x5C, (byte) 0x00, (byte) 0x73, (byte) 0x00, (byte) 0x79, (byte) 0x00, (byte) 0x73,
               (byte) 0x00, (byte) 0x74, (byte) 0x00, (byte) 0x65, (byte) 0x00, (byte) 0x6D, (byte) 0x00, (byte) 0x33,
               (byte) 0x00, (byte) 0x32, (byte) 0x00, (byte) 0x5C, (byte) 0x00, (byte) 0x6A, (byte) 0x00, (byte) 0x61,
               (byte) 0x00, (byte) 0x76, (byte) 0x00, (byte) 0x61, (byte) 0x00, (byte) 0x77, (byte) 0x00, (byte) 0x2E,
               (byte) 0x00, (byte) 0x65, (byte) 0x00, (byte) 0x78, (byte) 0x00, (byte) 0x65, (byte) 0x00, (byte) 0x00,
               (byte) 0x00, (byte) 0x18, (byte) 0xEC, (byte) 0xE3, (byte) 0x03, (byte) 0x74, (byte) 0x25, (byte) 0x75,
               (byte) 0x71, (byte) 0x20, (byte) 0xFC, (byte) 0x51, (byte) 0x04, (byte) 0x7A, (byte) 0xF2, (byte) 0xE3,
               (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x07, (byte) 0x00, (byte) 0x54, (byte) 0xEC, (byte) 0xE3,
               (byte) 0x03, (byte) 0xD0, (byte) 0x57, (byte) 0xF8, (byte) 0x77, (byte) 0xEC, (byte) 0x57, (byte) 0xF8,
               (byte) 0x77, (byte) 0x08, (byte) 0x06, (byte) 0x07, (byte) 0x00, (byte) 0x30, (byte) 0x00, (byte) 0x0D,
               (byte) 0x01, (byte) 0x05, (byte) 0x96, (byte) 0x57, (byte) 0x7C, (byte) 0x00, (byte) 0x00, (byte) 0x07,
               (byte) 0x00, (byte) 0x7A, (byte) 0xF2, (byte) 0xE3, (byte) 0x03, (byte) 0xE8, (byte) 0x3C, (byte) 0x0F,
               (byte) 0x00, (byte) 0x49, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x30, (byte) 0x00, (byte) 0x0D,
               (byte) 0x01, (byte) 0x30, (byte) 0x00, (byte) 0x0D, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00,
               (byte) 0x00, (byte) 0x24, (byte) 0xEC, (byte) 0xE3, (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00,
               (byte) 0x00, (byte) 0xE0, (byte) 0xED, (byte) 0xE3, (byte) 0x03, (byte) 0x44, (byte) 0x1F, (byte) 0x5C,
               (byte) 0x7C, (byte) 0x50, (byte) 0x24, (byte) 0x57, (byte) 0x7C, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
               (byte) 0xFF, (byte) 0x78, (byte) 0xEC, (byte) 0xE3, (byte) 0x03, (byte) 0x30, (byte) 0x77, (byte) 0xE1,
               (byte) 0x77, (byte) 0x34, (byte) 0x00, (byte) 0x0D, (byte) 0x01, (byte) 0x87, (byte) 0x78, (byte) 0xE1,
               (byte) 0x77, (byte) 0x34, (byte) 0x00, (byte) 0x0D, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x2F,
               (byte) 0x78, (byte) 0xB2, (byte) 0xFC, (byte) 0x51, (byte) 0x04, (byte) 0x00, (byte) 0x00, (byte) 0x00,
               (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x90, (byte) 0xEC, (byte) 0xE3,
               (byte) 0x03, (byte) 0x0A, (byte) 0x78, (byte) 0xE1, (byte) 0x77, (byte) 0xE8, (byte) 0x3C, (byte) 0x0F,
               (byte) 0x00, (byte) 0xE8, (byte) 0x3C, (byte) 0x0F, (byte) 0x00, (byte) 0x30, (byte) 0x75, (byte) 0x48,
               (byte) 0x00, (byte) 0x7A, (byte) 0x03, (byte) 0x0D, (byte) 0x00, (byte) 0xCC, (byte) 0xEC, (byte) 0xE3,
               (byte) 0x03, (byte) 0x05, (byte) 0x7B, (byte) 0xE1, (byte) 0x77, (byte) 0xE8, (byte) 0x3C, (byte) 0x0F,
               (byte) 0x00, (byte) 0x04, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0xE8, (byte) 0xF1, (byte) 0xE3,
               (byte) 0x03, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xA8, (byte) 0x19, (byte) 0x2F,
               (byte) 0x00, (byte) 0x30, (byte) 0x75, (byte) 0x48, (byte) 0x00, (byte) 0x7A, (byte) 0x03, (byte) 0x0D,
               (byte) 0x00, (byte) 0x20, (byte) 0x34, (byte) 0xE1, (byte) 0x77, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
               (byte) 0xFF, (byte) 0x08, (byte) 0xED, (byte) 0xE3, (byte) 0x03, (byte) 0x76, (byte) 0x26, (byte) 0x75,
               (byte) 0x71, (byte) 0x7A, (byte) 0x03, (byte) 0x0D, (byte) 0x00, (byte) 0x49, (byte) 0x00, (byte) 0x00,
               (byte) 0x00, (byte) 0xF0, (byte) 0xEC, (byte) 0xE3, (byte) 0x03, (byte) 0xD7, (byte) 0x79, (byte) 0xE1,
               (byte) 0x77, (byte) 0x30, (byte) 0x75, (byte) 0x48, (byte) 0x00, (byte) 0x0D, (byte) 0x00, (byte) 0x00,
               (byte) 0x00, (byte) 0x04, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0xE8, (byte) 0xF1, (byte) 0xE3,
               (byte) 0x03, (byte) 0xA8, (byte) 0x19, (byte) 0x2F, (byte) 0x00, (byte) 0x30, (byte) 0x75, (byte) 0x48,
               (byte) 0x00, (byte) 0x0D, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x14, (byte) 0xED, (byte) 0xE3,
               (byte) 0x03, (byte) 0xFC, (byte) 0x5F, (byte) 0xE4, (byte) 0x77, (byte) 0x30, (byte) 0x75, (byte) 0x48,
               (byte) 0x00, (byte) 0x0D, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x04, (byte) 0x01, (byte) 0x00,
               (byte) 0x00, (byte) 0xE8, (byte) 0xF1, (byte) 0xE3, (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00,
               (byte) 0x00, (byte) 0x0D, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xB3, (byte) 0x5F, (byte) 0xE4,
               (byte) 0x77, (byte) 0x34, (byte) 0xED, (byte) 0xE3, (byte) 0x03, (byte) 0xD0, (byte) 0xA3, (byte) 0xE3,
               (byte) 0x77, (byte) 0x7A, (byte) 0x03, (byte) 0x0D, (byte) 0x00, (byte) 0x0D, (byte) 0x00, (byte) 0x00,
               (byte) 0x00, (byte) 0x04, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0xE8, (byte) 0xF1, (byte) 0xE3,
               (byte) 0x03, (byte) 0xB3, (byte) 0x5F, (byte) 0xE4, (byte) 0x77, (byte) 0xCD, (byte) 0xAB, (byte) 0xBA,
               (byte) 0xDC, (byte) 0x58, (byte) 0xED, (byte) 0xE3, (byte) 0x03, (byte) 0xA6, (byte) 0x52, (byte) 0x58,
               (byte) 0x7C, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0x0C, (byte) 0x00, (byte) 0x00,
               (byte) 0x00, (byte) 0x50, (byte) 0xED, (byte) 0xE3, (byte) 0x03, (byte) 0xEF, (byte) 0x52, (byte) 0x58,
               (byte) 0x7C, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0x0C, (byte) 0x00, (byte) 0x00,
               (byte) 0x00, (byte) 0x60, (byte) 0xED, (byte) 0xE3, (byte) 0x03, (byte) 0x04, (byte) 0x00, (byte) 0x00,
               (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x80, (byte) 0x00,
               (byte) 0x00, (byte) 0xA4, (byte) 0xEF, (byte) 0xE3, (byte) 0x03, (byte) 0x75, (byte) 0x4E, (byte) 0xAA,
               (byte) 0x70, (byte) 0x01, (byte) 0x80, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
               (byte) 0x00, (byte) 0xE8, (byte) 0xF1, (byte) 0xE3, (byte) 0x03, (byte) 0xD8, (byte) 0x8F, (byte) 0x52,
               (byte) 0x04, (byte) 0xF0, (byte) 0x21, (byte) 0x75, (byte) 0x71, (byte) 0xB3, (byte) 0x5F, (byte) 0xE4,
               (byte) 0x77, (byte) 0x7A, (byte) 0x03, (byte) 0x0D, (byte) 0x00, (byte) 0x0D, (byte) 0x00, (byte) 0x00,
               (byte) 0x00, (byte) 0x04, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0xE8, (byte) 0xF1, (byte) 0xE3,
               (byte) 0x03, (byte) 0xF0, (byte) 0xED, (byte) 0xE3, (byte) 0x03, (byte) 0x6F, (byte) 0x27, (byte) 0x75,
               (byte) 0x71, (byte) 0x7A, (byte) 0x03, (byte) 0x0D, (byte) 0x00, (byte) 0x0D, (byte) 0x00, (byte) 0x00,
               (byte) 0x00, (byte) 0x04, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0xE8, (byte) 0xF1, (byte) 0xE3,
               (byte) 0x03, (byte) 0x9B, (byte) 0x27, (byte) 0x75, (byte) 0x71, (byte) 0xD4, (byte) 0xEE, (byte) 0xE3,
               (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x80, (byte) 0x04, (byte) 0x52,
               (byte) 0x04, (byte) 0x60, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03, (byte) 0x00, (byte) 0x00,
               (byte) 0xA0, (byte) 0x58, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
               (byte) 0x00, (byte) 0x65, (byte) 0x32, (byte) 0x38, (byte) 0x32, (byte) 0x32, (byte) 0x30, (byte) 0x34,
               (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
               (byte) 0x00, (byte) 0x06, (byte) 0xA0, (byte) 0x78, (byte) 0xF9, (byte) 0x08, (byte) 0x80, (byte) 0xFF,
               (byte) 0x4E, (byte) 0x9C, (byte) 0x47, (byte) 0xF1, (byte) 0xD0, (byte) 0x77, (byte) 0xFE, (byte) 0x54,
               (byte) 0xB6, (byte) 0x46, (byte) 0x18, (byte) 0x99, (byte) 0x1F, (byte) 0xA2, (byte) 0xE1, (byte) 0xD9,
               (byte) 0x11, (byte) 0x86, (byte) 0xC3, (byte) 0x00, (byte) 0x0F, (byte) 0x1F, (byte) 0xD8, (byte) 0x1B,
               (byte) 0x0F, (byte) 0x06, (byte) 0xA0, (byte) 0x78, (byte) 0xF9, (byte) 0x08, (byte) 0x80, (byte) 0xFF,
               (byte) 0x4E, (byte) 0x9C, (byte) 0x47, (byte) 0xF1, (byte) 0xD0, (byte) 0x77, (byte) 0xFE, (byte) 0x54,
               (byte) 0xB6, (byte) 0x46, (byte) 0x18, (byte) 0x99, (byte) 0x1F, (byte) 0xA2, (byte) 0xE1, (byte) 0xD9,
               (byte) 0x11, (byte) 0x86, (byte) 0xC3, (byte) 0x00, (byte) 0x0F, (byte) 0x1F, (byte) 0xD8, (byte) 0x1B,
               (byte) 0x0F, (byte) 0x10, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x05, (byte) 0x00, (byte) 0x00,
               (byte) 0xA0, (byte) 0x24, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x42, (byte) 0x00, (byte) 0x00,
               (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
}
