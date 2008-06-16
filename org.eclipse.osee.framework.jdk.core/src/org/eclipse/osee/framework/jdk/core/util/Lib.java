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
package org.eclipse.osee.framework.jdk.core.util;

import java.awt.Image;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import javax.swing.ImageIcon;
import org.eclipse.osee.framework.jdk.core.text.change.ChangeSet;
import org.eclipse.osee.framework.jdk.core.util.io.IOInputThread;
import org.eclipse.osee.framework.jdk.core.util.io.IOOutputThread;
import org.eclipse.osee.framework.jdk.core.util.io.InputBufferThread;
import org.eclipse.osee.framework.jdk.core.util.io.MatchFilter;

/**
 * @author Ryan D. Brooks
 */
public final class Lib {
   public final static Runtime runtime = Runtime.getRuntime();

   public final static String jarPath = getJarPath(Lib.class);

   public final static String basePath = getBasePath();

   public final static String lineSeparator = System.getProperty("line.separator");

   public static String toFirstCharUpperCase(String str) {
      if (str == null) {
         return null;
      }
      char[] chars = str.toCharArray();
      chars[0] = Character.toUpperCase(str.charAt(0));
      return new String(chars);
   }

   public static int numOccurances(String str, String regex) {
      int x = 0;
      Matcher m = Pattern.compile(regex).matcher(str);
      while (m.find())
         x++;
      return x;
   }

   public static String exceptionToString(Throwable ex) {
      StringBuilder sb = new StringBuilder();
      exceptionToString(ex, sb);
      return sb.toString();
   }

   public static boolean validateEmail(String toValidate) {
      Pattern pattern =
            Pattern.compile("^[a-z0-9\\._-]" + "+@([a-z0-9][a-z0-9-]*" + "[a-z0-9]\\.)+" + "([a-z]+\\.)?([a-z]+)$",
                  Pattern.CASE_INSENSITIVE);
      Matcher matcher = pattern.matcher(toValidate);
      return matcher.find();
   }

   private static void exceptionToString(Throwable ex, StringBuilder sb) {
      sb.append(ex.getMessage() + "\n");
      StackTraceElement st[] = ex.getStackTrace();
      for (int i = 0; i < st.length; i++) {
         StackTraceElement ste = st[i];
         sb.append("   at " + ste.toString() + "\n");
      }
      Throwable cause = ex.getCause();
      if (cause != null) {
         sb.append("   caused by ");
         exceptionToString(cause, sb);
      }
   }

   public static String changeExtension(String str, String newExt) {
      int pos = str.lastIndexOf('.');
      if (pos == -1) { // was -1 before + 1
         return str + "." + newExt;
      }
      return str.substring(0, pos + 1) + newExt;
   }

   /**
    * this version of changeExtension will work even if the extension we want to replace contains a .
    * 
    * @param str
    * @param oldExt
    * @param newExt
    * @return String
    */
   public static String changeExtension(String str, String oldExt, String newExt) {
      int pos = str.lastIndexOf(oldExt);
      if (pos == -1) {
         return str + "." + newExt;
      }
      return str.substring(0, pos) + newExt;
   }

   /**
    * Move an object one before the previous object
    * 
    * @param list
    * @param obj
    * @return true if successful
    */
   public static boolean moveBack(ArrayList<Object> list, Object obj) {
      if (list.contains(obj)) {
         int index = list.indexOf(obj);
         if (index > 0) {
            list.remove(index);
            list.add(index - 1, obj);
            return true;
         }
      }
      return false;
   }

   /**
    * Move an object one after the next object
    * 
    * @param obj
    * @return true if successful
    */
   public static boolean moveForward(ArrayList<Object> list, Object obj) {
      int size = list.size();
      if (list.contains(obj)) {
         int index = list.indexOf(obj);
         if (index < (size - 1)) {
            list.remove(index);
            list.add(index + 1, obj);
            return true;
         }
      }
      return false;
   }

   public static void copyDirectory(File source, File destination) throws IOException {
      File[] files = source.listFiles();

      for (int i = 0; i < files.length; i++) {
         if (files[i].isDirectory()) {
            File dir = new File(destination, files[i].getName());
            dir.mkdir();
            copyDirectory(files[i], dir);
         } else { // else is a file
            copyFile(files[i], destination);
         }
      }
   }

   public static String exceptionToString(Exception ex) {
      StringWriter stringWriter = new StringWriter();
      PrintWriter printWriter = new PrintWriter(stringWriter);
      ex.printStackTrace(printWriter);
      return stringWriter.toString();
   }

   public static void copyFile(File source, File destination) throws IOException {
      FileInputStream in = new FileInputStream(source);
      FileOutputStream out = null;

      if (destination.isDirectory()) {
         out = new FileOutputStream(new File(destination, source.getName()));
      } else {
         destination.delete(); // to work around some file permission
         // problems
         out = new FileOutputStream(destination);
      }

      byte[] bytes = new byte[(int) source.length()];
      in.read(bytes);
      out.write(bytes);

      in.close();
      out.close();
   }

   public static void copyFiles(File source, File destination) throws IOException {
      copyFiles(source, null, destination);
   }

   public static void copyFiles(File source, FilenameFilter filter, File destination) throws IOException {
      File[] files = source.listFiles(filter);
      if (!source.exists()) {
         throw new IllegalArgumentException("the directory " + source + " does not exist.");
      }

      if (files != null) {
         for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
               copyFile(files[i], destination);
            }
         }
      }
   }

   public static Image createImage(String path) {
      return createImageIcon(path).getImage();
   }

   public static ImageIcon createImageIcon(Class<?> clasaRef, String path) {
      URL imgURL = clasaRef.getResource(path);
      if (imgURL != null) {
         return new ImageIcon(imgURL);
      } else {
         System.err.println("Couldn't find the resource: " + path);
         return null;
      }
   }

   /** Returns an ImageIcon, or null if the path was invalid. */
   public static ImageIcon createImageIcon(String path) {
      return createImageIcon(Lib.class, path);
   }

   public static void deleteDir(File directory) {
      File[] files = directory.listFiles();
      if (files == null) {
         return;
      }

      for (int i = 0; i < files.length; i++) {
         if (files[i].isDirectory()) {
            deleteDir(files[i]);
         } else { // else is a file
            files[i].delete();
         }
      }
      directory.delete();
   }

   public static void deleteContents(File directory) {
      deleteContents(directory, null);
   }

   public static void deleteContents(File directory, FilenameFilter filter) {
      File[] files = directory.listFiles(filter);

      for (int i = 0; i < files.length; i++) {
         if (files[i].isDirectory()) {
            deleteContents(files[i]);
         }
         files[i].delete();
      }
   }

   public static void inputStreamToOutputStream(InputStream inputStream, OutputStream outputStream) throws IOException {
      byte[] buf = new byte[2024];
      int count = -1;
      while ((count = inputStream.read(buf)) != -1) {
         outputStream.write(buf, 0, count);
      }
   }

   public static String inputStreamToString(InputStream in) throws IOException {
      return inputStreamToStringBuffer(in).toString();
   }

   public static StringBuffer inputStreamToStringBuffer(InputStream in) throws IOException {
      InputStreamReader reader = new InputStreamReader(in, "UTF-8");
      StringBuffer strB = new StringBuffer(1000);
      char[] chars = new char[1000];
      int readCount = 0;
      while ((readCount = reader.read(chars)) != -1) {
         strB.append(chars, 0, readCount);
      }
      return strB;
   }

   public static byte[] inputStreamToBytes(InputStream in) throws IOException {
      byte[] bytes = new byte[2024];
      ByteArrayOutputStream out = new ByteArrayOutputStream(2024);

      int numBytesRead;
      while ((numBytesRead = in.read(bytes)) != -1) {
         out.write(bytes, 0, numBytesRead);
      }
      return out.toByteArray();
   }

   public static void inputStreamToFile(InputStream inputStream, File outFile) throws IOException {
      byte[] bytes = new byte[2024];
      FileOutputStream out = new FileOutputStream(outFile);

      int numBytesRead;
      while ((numBytesRead = inputStream.read(bytes)) != -1) {
         out.write(bytes, 0, numBytesRead);
      }
      out.close();
   }

   public static CharBuffer inputStreamToCharBuffer(InputStream in) throws IOException {
      StringBuffer strB = inputStreamToStringBuffer(in);
      char[] chars = new char[strB.length()];
      strB.getChars(0, strB.length(), chars, 0);
      return CharBuffer.wrap(chars);
   }

   public static java.io.InputStream stringToInputStream(String value) throws Exception {
      if (value == null) return null;
      value = value.trim();
      java.io.InputStream in = null;
      in = new java.io.ByteArrayInputStream(value.getBytes("UTF-8"));
      return in;
   }

   public static InputStream byteBufferToInputStream(final ByteBuffer byteBuffer) {
      return new InputStream() {
         public synchronized int read() throws IOException {
            if (!byteBuffer.hasRemaining()) {
               return -1;
            }
            return byteBuffer.get();
         }

         public synchronized int read(byte[] bytes, int off, int len) throws IOException {
            len = Math.min(len, byteBuffer.remaining());
            if (off != len) {
               byteBuffer.get(bytes, off, len);
            } else {
               len = -1;
            }
            return len;
         }

         /* (non-Javadoc)
          * @see java.io.InputStream#reset()
          */
         @Override
         public synchronized void reset() throws IOException {
            byteBuffer.rewind();
         }

      };
   }

   public static OutputStream byteBufferToOutputStream(final ByteBuffer byteBuffer) {
      return new OutputStream() {
         public synchronized void write(int b) throws IOException {
            byteBuffer.put((byte) b);
         }

         public synchronized void write(byte[] bytes, int off, int len) throws IOException {
            byteBuffer.put(bytes, off, len);
         }
      };
   }

   public static final Pattern numberListPattern = Pattern.compile("\\d+");

   public static List<Integer> stringToIntegerList(String numberList) {
      ArrayList<Integer> ints = new ArrayList<Integer>();
      Matcher transactionIdMatcher = numberListPattern.matcher(numberList);
      while (transactionIdMatcher.find()) {
         ints.add(Integer.parseInt(transactionIdMatcher.group()));
      }
      return ints;
   }

   public static String fileToString(File file) throws IOException {
      Reader in = new InputStreamReader(new FileInputStream(file), "UTF-8");
      char[] chars = new char[(int) file.length()];
      in.read(chars);
      in.close();
      return new String(chars);
   }

   public static byte[] fileToBytes(File file) throws IOException {
      InputStream inputStream = new FileInputStream(file);
      try {
         byte[] bytes = new byte[(int) file.length()];
         inputStream.read(bytes);
         return bytes;
      } finally {
         inputStream.close();
      }
   }

   /**
    * usage: char[] buf = new char[5500000]; for (Iterator iter = files.iterator(); iter.hasNext(); ) { // for each file
    * File file = (File)iter.next(); buf = Lib.fileToChars(file, buf);
    * 
    * @param file
    * @param buf
    * @return char[]
    * @throws IOException
    */
   public static char[] fileToChars(File file, char[] buf) throws IOException {
      FileReader in = new FileReader(file);
      int size = (int) file.length();

      if (buf == null) {
         buf = new char[size];
      } else if (size > buf.length) {
         buf = null;
         System.gc(); // since the currently allocated buf might already
         // be quite large
         buf = new char[size];
      }

      in.read(buf);
      in.close();
      return buf;
   }

   public static CharBuffer fileToCharBuffer(File file) throws IOException {
      FileReader in = new FileReader(file);
      char[] chars = new char[(int) file.length()];
      in.read(chars);
      in.close();
      return CharBuffer.wrap(chars);
   }

   public static String fillString(char c, int n) {
      char[] chars = new char[n];
      for (int i = 0; i < n; chars[i++] = c) {
         // must leave empty block here so the following line won't become
         // part of the loop
      }
      return new String(chars);
   }

   public static String getExtension(String filepath) {
      filepath = filepath.trim();
      String separatorRegEx = File.separator;
      if (Lib.isWindows()) {
         separatorRegEx = "\\\\";
      }
      String[] pathsArray = filepath.split(separatorRegEx);

      String fileName = pathsArray[0];
      if (pathsArray.length > 0) {
         fileName = pathsArray[pathsArray.length - 1];
      }

      int index = fileName.lastIndexOf('.');
      if (index >= 0 && index + 1 < fileName.length()) {
         return fileName.substring(index + 1);
      } else {
         return "";
      }
   }

   public static int handleProcess(Process proc) {
      return handleProcess(proc, new PrintWriter(System.out, true));
   }

   public static int handleProcess(Process proc, Writer output, String errName, String outName) {
      return handleProcess(proc, output, errName, outName, true);
   }

   public static int handleProcess(Process proc, Writer output, String errName, String outName, boolean verbose) {
      try {
         IOOutputThread errThread =
               new IOOutputThread(output, new BufferedReader(new InputStreamReader(proc.getErrorStream())), verbose);
         IOOutputThread outThread =
               new IOOutputThread(output, new BufferedReader(new InputStreamReader(proc.getInputStream())), verbose);

         errThread.setName(errName);
         outThread.setName(outName);
         errThread.start();
         outThread.start();
         proc.waitFor();
         int exitCode = proc.exitValue();
         if (exitCode != 0) {
            output.write("Process exit code is:  " + exitCode + "\n");
         }
         int count = 0;
         while (errThread.isAlive() || outThread.isAlive()) {
            if (count > 10) break;
            synchronized (Thread.currentThread()) {
               Thread.currentThread().wait(500);
            }
            count++;
         }
         output.flush();
         return exitCode;
      } catch (IOException ex) {
         System.err.println(ex);
      } catch (InterruptedException e) {
         e.printStackTrace();
      }
      return Integer.MIN_VALUE;
   }

   public static int handleProcess(Process proc, Writer output) {
      return handleProcess(proc, output, "err", "out");
   }

   public static int handleProcess(Process proc, Writer output, boolean verbose) {
      return handleProcess(proc, output, "err", "out", verbose);
   }

   /**
    * Sets up an error, input, and output stream for the given process. The error stream gives all information coming
    * FROM the process through it's err stream. The "outThread" will be what come from the FROM the process through it's
    * normal output stream. The "inThread" is the stream for issuing commands TO the process.
    * 
    * @param proc The process whose streams we are setting up
    * @param output Where all info coming FROM the minicom is sent
    * @param input Where all data going TO the minicom is sent
    * @return An array of threads in the following order:<br>
    *         --index 0 = Err Stream<br>
    *         --index 1 = output stream<br>
    *         --index 2 = input stream<br>
    */
   public static Thread[] handleMinicomProcess(Process proc, Writer output, Reader input) {
      IOOutputThread errThread =
            new IOOutputThread(output, new BufferedReader(new InputStreamReader(proc.getErrorStream())));

      InputBufferThread outThread = new InputBufferThread(proc.getInputStream());

      errThread.setName("err");
      outThread.setName("out");
      errThread.start();
      outThread.start();

      if (input != null) {
         IOInputThread inThread =
               new IOInputThread(input, new BufferedWriter(new OutputStreamWriter(proc.getOutputStream())));
         inThread.setName("in");
         inThread.start();
         return new Thread[] {errThread, outThread, inThread};
      }
      return new Thread[] {errThread, outThread};
   }

   public static Thread[] handleProcessNoWait(Process proc, Writer output) {
      return handleProcessNoWait(proc, output, output, null);
   }

   public static Thread[] handleProcessNoWait(Process proc, Writer output, boolean verbose) {
      return handleProcessNoWait(proc, output, output, null, "err", "out", verbose);
   }

   public static Thread[] handleProcessNoWait(Process proc, Writer output, Writer errorWriter, Reader reader) {
      return handleProcessNoWait(proc, output, errorWriter, reader, "err", "out", true);
   }

   public static Thread[] handleProcessNoWait(Process proc, Writer outputWriter, Writer errorWriter, Reader reader, String errName, String outName) {
      return handleProcessNoWait(proc, outputWriter, errorWriter, reader, errName, outName, true);
   }

   public static Thread[] handleProcessNoWait(Process proc, Writer outputWriter, Writer errorWriter, Reader reader, String errName, String outName, boolean verbose) {
      IOOutputThread errThread =
            new IOOutputThread(errorWriter, new BufferedReader(new InputStreamReader(proc.getErrorStream())), verbose);
      IOOutputThread outThread =
            new IOOutputThread(outputWriter, new BufferedReader(new InputStreamReader(proc.getInputStream())), verbose);
      errThread.setName(errName);
      outThread.setName(outName);
      errThread.start();
      outThread.start();
      if (reader != null) {
         IOInputThread inThread =
               new IOInputThread(reader, new BufferedWriter(new OutputStreamWriter(proc.getOutputStream())));
         inThread.setName("in");
         inThread.start();
         return new Thread[] {errThread, outThread, inThread};
      }
      return new Thread[] {errThread, outThread};
   }

   public static void makeDirClean(File directory) {
      if (directory.exists()) {
         Lib.deleteContents(directory);
      } else {
         directory.mkdir();
      }
   }

   public static void moveFiles(File source, File destination) {
      moveFiles(source, null, destination);
   }

   public static void moveFiles(File source, FilenameFilter filter, File destination) {
      File[] files = source.listFiles(filter);

      for (int i = 0; i < files.length; i++) {
         if (files[i].isFile()) {
            files[i].renameTo(new File(destination, files[i].getName()));
         }
      }
   }

   public static String padLeading(String str, char c, int n) {
      char[] chars = new char[n];
      int pos = Math.min(n, str.length());
      str.getChars(0, pos, chars, n - pos);
      for (int i = 0; i < n - pos; chars[i++] = c) {
         // must leave empty block here so the following line won't become
         // part of the loop
      }
      return new String(chars);
   }

   public static String padTrailing(String str, char c, int n) {
      char[] chars = new char[n];
      int pos = Math.min(n, str.length());
      str.getChars(0, pos, chars, 0);
      for (int i = pos; i < n; chars[i++] = c) {
         // must leave empty block here so the following line won't become
         // part of the loop
      }
      return new String(chars);
   }

   public static int printAndExec(String[] callAndArgs) {
      return printAndExec(callAndArgs, null, new BufferedWriter(new PrintWriter(System.out, true)));
   }

   public static int printAndExec(String[] callAndArgs, File dir) {
      return printAndExec(callAndArgs, dir, new PrintWriter(System.out, true));
   }

   public static int printAndExec(String[] callAndArgs, File dir, Writer output) {
      try {
         for (int j = 0; j < callAndArgs.length; j++) {
            output.write(callAndArgs[j] + " ");
         }
         output.write("\n");
         output.flush();

         return Lib.handleProcess(runtime.exec(callAndArgs, null, dir), output);
      } catch (IOException ex) {
         System.err.println("error: " + ex);
         return -1;
      }
   }

   public static ArrayList<String> readListFromDir(File directory, FilenameFilter filter, boolean keepExtension) {
      ArrayList<String> list = new ArrayList<String>(400);

      if (directory == null) {
         System.out.println("Invalid path: " + directory);
         return list;
      }

      File[] files = directory.listFiles(filter);
      if (files == null) {
         System.out.println("Invalid path: " + directory);
         return list;
      }
      if (files.length > 0) {
         Arrays.sort(files);
      }

      if (keepExtension) {
         for (int i = 0; i < files.length; i++) {
            list.add(files[i].getName());
         }
      } else {
         for (int i = 0; i < files.length; i++) {
            list.add(Lib.removeExtension(files[i].getName()));
         }
      }

      return list;
   }

   public static ArrayList<String> readListFromDir(String directory, FilenameFilter filter) {
      return readListFromDir(new File(directory), filter, false);
   }

   public static ArrayList<String> readListFromFile(File file, boolean keepExtension) throws IOException {
      BufferedReader in = new BufferedReader(new FileReader(file));
      ArrayList<String> list = new ArrayList<String>(120);

      String line = null;

      if (keepExtension) {
         while ((line = in.readLine()) != null) {
            list.add(line);
         }
      } else {
         while ((line = in.readLine()) != null) {
            list.add(Lib.removeExtension(line));
         }
      }
      in.close();
      return list;
   }

   public static ArrayList<String> readListFromFile(String file) throws IOException {
      return readListFromFile(new File(file), true);
   }

   public static boolean isLink(File file) {
      if (!file.exists()) {
         return true;
      }
      try {
         return !file.getAbsolutePath().equals(file.getCanonicalPath());
      } catch (IOException ex) {
         return true;
      }
   }

   public static List<File> recursivelyListFilesAndDirectories(ArrayList<File> fileList, File rootPath, Pattern filePathP, boolean includeDirectories) {
      LinkedList<File> dirList = new LinkedList<File>();
      dirList.add(rootPath);

      Matcher fileNameM = null;
      if (filePathP != null) {
         fileNameM = filePathP.matcher("");
      }

      while (!dirList.isEmpty()) {
         File parent = dirList.removeFirst();
         if (parent == null) {
            System.out.println("Invalid path.");
            continue;
         }

         if (parent.getName().startsWith(".Rational")) {
            continue;
         }

         File[] files = parent.listFiles();
         if (files == null) {
            System.out.println("Invalid path: " + parent);
            continue;
         }

         for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
               dirList.add(files[i]);
               if (includeDirectories) {
                  fileList.add(files[i]);
               }
            } else { // else is a file
               if (fileNameM == null) {
                  fileList.add(files[i]);
               } else {
                  fileNameM.reset(files[i].getPath());
                  if (fileNameM.matches()) {
                     fileList.add(files[i]);
                  }
               }
            }
         }
      }
      return fileList;
   }

   /**
    * Returns an array list of File objects
    * 
    * @param fileList
    * @param rootPath
    * @param fileNameP
    * @return List
    */
   public static List<File> recursivelyListFiles(ArrayList<File> fileList, File rootPath, Pattern filePathP) {
      return recursivelyListFilesAndDirectories(fileList, rootPath, filePathP, false);
   }

   public static List<File> recursivelyListFiles(File rootPath, Pattern fileNameP) {
      return recursivelyListFiles(new ArrayList<File>(400), rootPath, fileNameP);
   }

   /**
    * Assumptions: block comments are not nested and comment blocks have an ending Note: may be fooled by string
    * literals that contains the block comment indicators
    * 
    * @param charBuf
    * @return CharBuffer
    */
   public static CharBuffer stripBlockComments(CharBuffer charBuf) {
      char[] chars = charBuf.array();
      int i = 0;
      int k = 0;

      try {
         while (i < chars.length) {
            if (chars[i] == '/' && chars[i + 1] == '*') {
               i += 2;
               while (!(chars[i++] == '*' && chars[i] == '/')) {
                  // must leave empty block here so the following line
                  // won't become part of the loop
               }
               i++;
            } else {
               chars[k++] = chars[i++];
            }
         }
         charBuf.limit(k);
         return charBuf;
      } catch (ArrayIndexOutOfBoundsException ex) {
         throw new IllegalArgumentException("Incorrectly formatted comments.");
      }
   }

   public static String removeExtension(String filepath) {
      String ext = getExtension(filepath);
      if (ext != null && ext.length() > 0) {
         filepath = filepath.substring(0, filepath.length() - (ext.length() + 1));
      }
      return filepath;
   }

   // replaces the first capturing group of the match in fileToModify with
   // replaceSeq and write this back to fileToModify
   public static boolean updateFile(File fileToModify, Pattern pattern, CharSequence replaceSeq) throws IOException {
      CharBuffer modifyBuf = Lib.fileToCharBuffer(fileToModify);

      Matcher matcher = pattern.matcher(modifyBuf);
      if (!matcher.find()) {
         System.out.println(fileToModify.getPath() + " does not contain the pattern: " + pattern.pattern());
         return false;
      }
      CharBuffer topSection = (CharBuffer) modifyBuf.subSequence(0, matcher.start(1)); // everything
      // before the
      // pattern
      CharBuffer bottomSection = null;
      int bottomLen = 0;
      if (matcher.end(1) != modifyBuf.length()) {
         bottomSection = (CharBuffer) modifyBuf.subSequence(matcher.end(1), modifyBuf.length());
         bottomLen = bottomSection.length();
      }

      CharBuffer outBuf = CharBuffer.allocate(topSection.length() + replaceSeq.length() + bottomLen);
      outBuf.put(topSection);
      if (replaceSeq instanceof String) {
         outBuf.put((String) replaceSeq);
      } else if (replaceSeq instanceof CharBuffer) {
         outBuf.put((CharBuffer) replaceSeq);
      } else {
         outBuf.put(replaceSeq.toString());
      }
      if (bottomSection != null) {
         outBuf.put(bottomSection);
      }
      Lib.writeCharBufferToFile(outBuf, fileToModify);

      return true;
   }

   public static boolean updateFile(File fileToModify, Pattern pattern, File original) throws IOException {
      return updateFile(fileToModify, pattern, fileToCharBuffer(original));
   }

   public static void writeCharBufferToFile(CharBuffer charBuf, File outFile) throws IOException {
      FileWriter out = new FileWriter(outFile);
      out.write(charBuf.array(), 0, charBuf.limit());
      out.close();
   }

   public static String getBasePath() {
      return Lib.getBasePath(Lib.class);
   }

   public static String getDateTimeString() {
      return new SimpleDateFormat("yyyy-MM-dd_hh-mm").format(new Date());
   }

   public static String getJarPath(Class<Lib> base) {
      // the leading '/' tells getResource not to append the package name
      // (instead the leading / is just stripped off)
      String className = "/" + base.getName().replace('.', '/') + ".class";
      String path = base.getResource(className).getPath();

      int pos = path.lastIndexOf("!");
      if (pos == -1) { // class is not in a jar file
         return null;
      } else { // class is in a jar file
         String jarpath = path.substring("file:".length(), pos);
         return jarpath.replaceAll("%20", " ");
      }
   }

   /**
    * @param base
    * @return The path which was used to load the class file. If the file was loaded from a .jar, then the full path to
    *         the jar. If the file was loaded from a .class, then the path up to the root of the package.
    */
   public static String getClassLoadPath(Class<?> base) {
      /*
       * Using the getProtectionDomain() method seems to be yield more consistent results than
       * getResource(className).getPath(); particularly when being run with the Eclipse class
       * loader. It was found that the Eclipse class loader did not always return the full path when
       * asked for the resource.
       */

      ProtectionDomain pd = base.getProtectionDomain();
      CodeSource cs = pd.getCodeSource();
      URL csLoc = cs.getLocation();
      String path = csLoc.getPath();
      path = path.replaceAll("%20", " ");
      return path;
   }

   public static String getBasePath(Class<?> base) {
      String path = getClassLoadPath(base);
      path = path.replaceAll("%20", " ");

      if (path.endsWith(".jar")) {
         int end = path.lastIndexOf('/');
         path = path.substring(0, end);
      }
      return path;

   }

   public static URL resolveToUrl(String path) {
      URL url = null;
      System.out.println("resolveToUrl: " + path);
      if (path != null && !path.equals("")) {
         if (path.indexOf("://") == -1) { // if not a full URL yet
            if (!path.startsWith("/")) { // if not absolute then prepend
               // base path
               try {
                  path = new File(Lib.basePath + "/" + path).getCanonicalPath();
               } catch (IOException e) {
                  e.printStackTrace();
               }
            }
            path = "file://" + path;
         }
         try {
            url = new URL(path);
         } catch (MalformedURLException ex) {
            ex.printStackTrace();
         }
      }
      return url;
   }

   public static String escapeForRegex(String text) {
      char[] chars = text.toCharArray();
      StringBuffer strB = new StringBuffer(chars.length + 5);

      for (int i = 0; i < chars.length; i++) {
         switch (chars[i]) {
            case '\\':
            case '[':
            case ']':
            case '.':
            case '{':
            case '}':
            case '^':
            case '$':
            case '?':
            case '*':
            case '+':
            case '|':
               strB.append('\\');
         }
         strB.append(chars[i]);
      }
      return strB.toString();
   }

   public static String determineGroup() {
      try {
         Process proc =
               Runtime.getRuntime().exec(
                     new String[] {"/usr/bin/bash", "-c", "touch whichGroup; ls -g whichGroup; rm whichGroup"}, null,
                     new File("/tmp"));
         BufferedReader inOutput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
         String line = inOutput.readLine();
         return line.substring(15, line.indexOf(' ', 16)); // 15 based on
         // format of
         // line printed
         // by ls
      } catch (IOException ex) {
         System.out.println(ex);
         return "no group";
      }
   }

   /**
    * Returns a list of the arguments in the source String passed. It assumes that the first character is a '(' and
    * matches till it finds the matching ')'.
    * 
    * @param source
    * @return String[]
    */
   public static String[] getArguments(String source) {
      ArrayList<String> theResults = new ArrayList<String>();
      try {
         if (source.charAt(0) != '(') {
            System.err.println("In getArguments, first char must be \'(\'.");
            return null;
         }

         int currentArg = 1;
         int startPos = 1;
         int parens = 0;
         boolean foundValidChar = false;

         for (int i = 1; i < source.length(); i++) {
            char theChar = source.charAt(i);

            if (theChar == '(')
               parens++;
            else if (theChar == ')') {
               parens--;
               if (parens < 0) {
                  theResults.add(source.substring(startPos, i));
                  break;
               }
            } else if (theChar == ',' && parens == 0) {
               theResults.add(source.substring(startPos, i));
               startPos = i + 1;
               currentArg++;
               foundValidChar = false;
            } else if (!foundValidChar) {
               if (Character.isWhitespace(theChar))
                  startPos++;
               else
                  foundValidChar = true;
            }
         }

         String[] theTrueResults = new String[theResults.size()];
         theResults.toArray(theTrueResults);

         if (theTrueResults.length != currentArg) System.err.println("In getArguments, number of argument mismatch.");

         return theTrueResults;
      } catch (Exception e) {
         e.printStackTrace();
         return null;
      }
   }

   public static int getArgsLength(String source) {
      try {
         if (source.charAt(0) != '(') {
            System.err.println("In getArguments, first char must be \'(\'.");
            return -1;
         }
         int parens = 0;

         for (int i = 1; i < source.length(); i++) {
            char theChar = source.charAt(i);

            if (theChar == '(')
               parens++;
            else if (theChar == ')') {
               parens--;
               if (parens < 0) {
                  return i - 1;
               }
            }
         }

         System.err.println("In getArguments, problem occurred.");
         return -1;
      } catch (Exception e) {
         e.printStackTrace();
         return -1;
      }
   }

   /**
    * Returns a list of Strings representing each comma separated string in the string passed. It ignores commas inside ()
    * or {}.
    * 
    * @param source
    * @return String[]
    */
   public static String[] getCommaSeparatedItems(String source) {
      ArrayList<String> theResults = new ArrayList<String>();
      int startPos = 0;
      int parensCount = 0;

      for (int i = 0; i < source.length(); i++) {
         char theChar = source.charAt(i);

         if (theChar == '(' || theChar == '{')
            parensCount++;
         else if (theChar == ')' || theChar == '}')
            parensCount--;
         else if (parensCount == 0 && theChar == ',') {
            theResults.add(source.substring(startPos, i).trim());
            startPos = i + 1;
         }
      }
      theResults.add(source.substring(startPos).trim());
      String[] results = new String[theResults.size()];
      theResults.toArray(results);
      return results;
   }

   public static String getCommaString(Collection<String> strs) {
      StringBuffer sb = new StringBuffer();
      for (String str : strs)
         sb.append(str + ", ");
      return sb.toString().replaceFirst(", ", "");
   }

   public static String getCommaString(String[] strs) {
      StringBuffer sb = new StringBuffer();
      for (String str : strs)
         sb.append(str + ", ");
      return sb.toString().replaceFirst(", ", "");
   }

   /**
    * Finds the index of the closing bracket for a function.
    * 
    * @param start -the index of the character AFTER the opening bracket for the function
    * @param seq -sequence containing the local function
    * @return -the index of the matching bracket to the opening bracket of this function
    */
   public static int findTheEnd(int start, CharSequence seq) {
      int stack = 1;
      char[] array = seq.subSequence(start, seq.length()).toString().toCharArray();
      int i;
      // print( "checking: " + seq.subSequence(start,
      // seq.length()).toString());
      for (i = 0; i < array.length; i++) {
         // System.out.print( array[i]);
         if (array[i] == '/' && array[i + 1] == '*') {

            while (array[i] != '*' || array[i + 1] != '/') {
               i++;
            }
         }
         if (array[i] == '"') {
            i++;
            while (array[i] != '"' || array[i - 1] == '\\') {
               i++;
            }
         }
         if (array[i] == '{') {
            stack++;

         } else if (array[i] == '}') {
            stack--;

         }
         if (stack == 0) {
            return start + i - 1;
         }
      }
      return start + i - 1;

   }

   public static final URL getUrlFromString(String path) throws MalformedURLException {
      if (!path.matches("(file://|http://|C:).*")) {
         return new URL("file://" + path);
      }
      return new URL(path);
   }

   public static final URL[] getUrlFromString(String[] path) throws MalformedURLException {
      URL[] urls = new URL[path.length];
      for (int i = 0; i < urls.length; i++) {
         urls[i] = getUrlFromString(path[i]);
      }
      return urls;
   }

   public static final String convertToJavaClassName(String name) {
      ChangeSet changeSet = new ChangeSet(name);
      Matcher matcher = Pattern.compile("_([a-zA-Z])").matcher(changeSet.toString());

      changeSet.replace(0, 1, Character.toUpperCase(name.charAt(0)));
      while (matcher.find())
         changeSet.replace(matcher.start(), matcher.end(), Character.toUpperCase(matcher.group(1).charAt(0)));

      return changeSet.applyChangesToSelf().toString();
   }

   /**
    * This method takes in any name separated by underscores and converts it into a java standard variable name.
    * 
    * @param name
    * @return
    */
   public static final String convertToJavaVariableName(String name) {
      name = name.toLowerCase();
      StringBuffer sb = new StringBuffer();
      char[] chars = name.toCharArray();
      for (int i = 0; i < chars.length; i++) {
         if (chars[i] == '_') {
            if (chars.length > i + 1 && chars[i + 1] != '_') {
               sb.append(Character.toUpperCase(chars[i + 1]));
               i++;
            }
         } else
            sb.append(chars[i]);
      }
      return sb.toString();
   }

   public static URL getJarFileURL(File directory, String title, String version) throws IOException {
      if (!directory.isDirectory()) {
         throw new IllegalArgumentException(directory.getPath() + " is not a valid directory.");
      }
      File[] jars = directory.listFiles(new MatchFilter(".*\\.jar"));
      for (int i = 0; i < jars.length; i++) {
         JarFile jar = new JarFile(jars[i]);
         Attributes attributes = jar.getManifest().getMainAttributes();
         String jarTitle = attributes.getValue("Implementation-Title");
         String jarVersion = attributes.getValue("Implementation-Version");
         if (jarTitle != null && jarVersion != null && jarTitle.equals(title) && jarVersion.equals(version)) {
            return jars[i].toURI().toURL();
         }
      }
      throw new IllegalArgumentException("The specified version: " + version + " for " + title + " was not found.");
   }

   public static URL[] getClasspath() throws MalformedURLException {
      String[] strPaths = System.getProperty("java.class.path").split(System.getProperty("path.separator"));
      URL[] urlPaths = new URL[strPaths.length];
      for (int i = 0; i < strPaths.length; i++) {
         urlPaths[i] = new File(strPaths[i]).toURI().toURL();
      }
      return urlPaths;
   }

   public static String getJarFileVersion(String jarFilePath) throws IOException {
      Manifest manifest;
      File jarFile = new File(jarFilePath);
      if (!jarFile.isFile()) {// maybe it's an http connection
         try {
            URL u = new URL(jarFilePath);
            URLConnection uc = u.openConnection();
            JarInputStream jis = new JarInputStream(uc.getInputStream());
            manifest = jis.getManifest();
         } catch (Exception ex) {
            throw new IllegalArgumentException(jarFilePath + " is not a valid file or URL.");
         }
      } else {
         manifest = new JarFile(jarFile).getManifest();
      }
      return manifest.getMainAttributes().getValue("Implementation-Version");
   }

   public static String getElapseString(long startTime) {
      return String.valueOf((System.currentTimeMillis() - startTime) / 1000.0) + " secs";
   }

   public static boolean isWindows() {
      return System.getProperty("os.name").indexOf("indows") != -1;
   }

   public static void writeBytesToFile(byte[] data, File file) throws IOException {
      OutputStream os = new FileOutputStream(file);
      try {
         os.write(data);
      } finally {
         os.close();
      }
   }

   public static void extractJarEntry(File jarFile, File destination, String entry) throws IOException {
      if (!destination.getParentFile().exists()) {
         destination.getParentFile().mkdirs();
      }
      int BUFFER = 2048;
      try {
         JarFile jarfile = new JarFile(jarFile.getAbsolutePath());
         JarEntry jarEntry = jarfile.getJarEntry(entry);
         BufferedInputStream is = new BufferedInputStream(jarfile.getInputStream(jarEntry));
         int count;
         long total = 0;
         byte data[] = new byte[BUFFER];
         FileOutputStream fos = new FileOutputStream(destination);
         BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER);
         while ((count = is.read(data, 0, BUFFER)) != -1) {
            dest.write(data, 0, count);
            total += BUFFER;
         }
         dest.flush();
         dest.close();
         is.close();
      } catch (Exception ex) {
         String information =
               "JarFile: " + (jarFile != null ? jarFile.getAbsolutePath() : "NULL") + "\n" + "Entry: " + (entry != null ? entry.toString() : "NULL") + "\n" + "Destination: " + (destination != null ? destination.getAbsoluteFile().toString() : "NULL") + "\n";
         throw new IOException(information + ex.getMessage());
      }
   }

   public static byte[] compressFile(InputStream in, String name) throws IOException {
      // Create a buffer for reading the files
      byte[] buf = new byte[1024];
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      ZipOutputStream out = new ZipOutputStream(bos);

      // Add ZIP entry to output stream.
      out.putNextEntry(new ZipEntry(name));

      // Transfer bytes from the file to the ZIP file
      int len;
      while ((len = in.read(buf)) > 0) {
         out.write(buf, 0, len);
      }

      out.closeEntry();
      in.close();

      // Complete the ZIP file
      out.close();

      return bos.toByteArray();
   }

   public static byte[] compressFile(File file) throws IOException {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      ZipOutputStream outputStream = new ZipOutputStream(bos);
      compressFile(null, file, outputStream);
      outputStream.closeEntry();
      outputStream.close();
      return bos.toByteArray();
   }

   private static void compressFile(String basePath, File file, ZipOutputStream outputStream) throws IOException {
      FileInputStream inputStream = null;
      try {
         byte[] buffer = new byte[4096];
         int count = -1;
         inputStream = new FileInputStream(file);
         String entryName = file.getPath();
         if (Strings.isValid(basePath) && entryName.startsWith(basePath)) {
            if (basePath.endsWith(File.separator) != true) {
               basePath = basePath + File.separator;
            }
            entryName = entryName.replace(basePath, "");
         }
         ZipEntry entry = new ZipEntry(entryName);
         outputStream.putNextEntry(entry);
         while ((count = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, count);
         }
      } finally {
         if (inputStream != null) {
            inputStream.close();
         }
      }
   }

   private static void compressDirectory(String basePath, File source, ZipOutputStream outputStream, boolean includeSubDirectories) throws IOException {
      File[] children = source.listFiles();
      for (File file : children) {
         if (file.isDirectory() != true) {
            compressFile(basePath, file, outputStream);
         } else {
            if (includeSubDirectories) {
               compressDirectory(basePath, file, outputStream, includeSubDirectories);
            }
         }
      }
   }

   public static void compressDirectory(File directory, String zipTarget, boolean includeSubDirectories) throws IOException, IllegalArgumentException {
      if (directory.isDirectory() != true) {
         throw new IllegalArgumentException(String.format("Error source is not a directory: [%s]", directory));
      }
      if (Strings.isValid(zipTarget) != true) {
         throw new IllegalArgumentException("Error target zip filename is invalid");
      }
      ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipTarget));
      compressDirectory(directory.getPath(), directory, out, includeSubDirectories);
      out.close();
   }

   public static byte[] decompressBytes(InputStream inputStream) throws IOException {
      ByteArrayOutputStream out = null;
      // Open the ZIP file
      ZipInputStream in = new ZipInputStream(inputStream);

      // Get the first entry
      in.getNextEntry();

      // Open the output file
      out = new ByteArrayOutputStream();

      // Transfer bytes from the ZIP file to the output file
      byte[] buf = new byte[1024];
      int len;
      while ((len = in.read(buf)) > 0) {
         out.write(buf, 0, len);
      }

      // Close the streams
      out.close();
      in.close();
      return out.toByteArray();
   }

   public static void chmod777(File file) {
      if (file == null || !file.exists()) return;
      try {
         String command = "chmod 777 " + file.getAbsolutePath();
         Runtime r = Runtime.getRuntime();
         Process p = r.exec(command);

         Lib.handleProcess(p);
      } catch (IOException ioe) {
         ioe.printStackTrace();
      }
   }

   public static String getSortedJavaArrayInitializer(String[] strings) {
      Arrays.sort(strings);
      StringBuilder strB = new StringBuilder();
      strB.append("new String[] {\"");
      for (String element : strings) {
         strB.append(element);
         strB.append("\", \"");
      }
      strB.replace(strB.length() - 3, strB.length(), "};");
      return strB.toString();
   }
}