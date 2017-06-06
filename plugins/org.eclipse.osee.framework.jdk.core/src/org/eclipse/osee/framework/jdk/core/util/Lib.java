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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
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
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
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
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.framework.jdk.core.text.change.ChangeSet;
import org.eclipse.osee.framework.jdk.core.type.Named;
import org.eclipse.osee.framework.jdk.core.util.io.IOInputThread;
import org.eclipse.osee.framework.jdk.core.util.io.IOOutputThread;
import org.eclipse.osee.framework.jdk.core.util.io.InputBufferThread;
import org.eclipse.osee.framework.jdk.core.util.io.MatchFilter;

/**
 * @author Ryan D. Brooks
 */
public final class Lib {
   private static final Random RANDOM = new Random();

   private final static String INVALID_URI_CHARACTERS_REGEX = "[<>]";

   @Deprecated
   public final static Runtime runtime = Runtime.getRuntime();

   public final static String jarPath = getJarPath(Lib.class);

   public final static String basePath = getBasePath();

   public final static String lineSeparator = System.getProperty("line.separator");

   public static String getFileAtsClass(String filename, Class<?> clazz) {
      URL url = clazz.getResource(filename);
      File file = new File(url.getPath());
      try {
         return fileToString(file);
      } catch (IOException ex) {
         // do nothing
      }
      return "";
   }

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
      while (m.find()) {
         x++;
      }
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
      if (ex == null) {
         sb.append("Exception == null; can't display stack");
         return;
      }
      sb.append(ex.getClass().getName());
      sb.append("\n");
      if (ex.getMessage() != null && !ex.getMessage().isEmpty()) {
         sb.append("   \"");
         sb.append(ex.getMessage());
         sb.append("\"\n");
      }
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
    * @return true if successful
    */
   public static boolean moveForward(ArrayList<Object> list, Object obj) {
      int size = list.size();
      if (list.contains(obj)) {
         int index = list.indexOf(obj);
         if (index < size - 1) {
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

   @SuppressWarnings("resource")
   public static void copyFile(File source, File destination) throws IOException {
      final FileChannel in = new FileInputStream(source).getChannel();
      try {
         final FileChannel out;
         if (destination.isDirectory()) {
            out = new FileOutputStream(new File(destination, source.getName())).getChannel();
         } else {
            if (destination.exists()) {
               destination.delete(); // to work around some file permission
            }
            // problems
            out = new FileOutputStream(destination).getChannel();
         }
         try {
            long position = 0;
            long size = in.size();
            while (position < size) {
               position += in.transferTo(position, size, out);
            }
         } finally {
            Lib.close(out);
         }
      } finally {
         Lib.close(in);
      }
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

   /**
    * Delete the current file and all empty parents. The method will stop deleting empty parents once it reaches the
    * stopAt parent.
    *
    * @param stopAt path of the parent file to stop deleting at
    * @param file to delete
    * @return status <b>true</b> if successful
    */
   public static boolean deleteFileAndEmptyParents(String stopAt, File file) {
      boolean result = true;
      if (file != null) {
         if (file.isDirectory()) {
            if (file.list().length == 0) {
               File parent = file.getParentFile();
               if (parent != null && !parent.getAbsolutePath().equals(stopAt)) {
                  result &= deleteFileAndEmptyParents(stopAt, parent);
               }
               result &= file.delete();
            }
         } else {
            File parent = file.getParentFile();
            if (parent != null && !parent.getAbsolutePath().equals(stopAt)) {
               result &= deleteFileAndEmptyParents(stopAt, parent);
            }
            result &= file.delete();
         }
      }

      return result;
   }

   /**
    * Deletes all files from directory
    */
   public static void emptyDirectory(File directory) {
      File[] children = directory.listFiles();
      if (children != null) {
         for (File child : children) {
            if (child.isDirectory()) {
               emptyDirectory(child);
            } else { // else is a file
               child.delete();
            }
         }
      }
   }

   public static void inputStreamToOutputStream(InputStream inputStream, OutputStream outputStream) throws IOException {
      if (inputStream == null) {
         throw new IllegalArgumentException("inputStream was null");
      }
      if (outputStream == null) {
         throw new IllegalArgumentException("outputStream was null");
      }

      byte[] buf = new byte[10000];
      int count = -1;
      while ((count = inputStream.read(buf)) != -1) {
         outputStream.write(buf, 0, count);
      }
   }

   public static int getMatcherCount(Pattern p, String str) {
      return getMatcherCount(p.matcher(str));
   }

   public static int getMatcherCount(Matcher m) {
      int count = 0;
      while (m.find()) {
         count++;
      }
      return count;
   }

   public static String inputStreamToString(InputStream in) throws IOException {
      return inputStreamToChangeSet(in).toString();
   }

   public static ChangeSet inputStreamToChangeSet(InputStream in, String charset) throws IOException {
      InputStreamReader reader = new InputStreamReader(in, charset);
      try {
         ChangeSet set = new ChangeSet();
         char[] chars = new char[8000];
         int readCount = 0;
         while ((readCount = reader.read(chars)) != -1) {
            set.insertBefore(0, chars, 0, readCount, true);
         }
         return set;
      } finally {
         close(reader);
      }
   }

   /**
    * efficiently copy contents of inputStream into builder
    */
   public static StringBuilder inputStreamToStringBuilder(InputStream inputStream, String charset, StringBuilder builder) throws IOException {
      InputStreamReader reader = new InputStreamReader(inputStream, charset);
      try {
         char[] chars = new char[8000];
         int readCount = 0;
         while ((readCount = reader.read(chars)) != -1) {
            builder.append(chars, 0, readCount);
         }
         return builder;
      } finally {
         close(reader);
      }
   }

   public static StringBuilder inputStreamToStringBuilder(InputStream inputStream, StringBuilder builder) throws IOException {
      return inputStreamToStringBuilder(inputStream, "UTF-8", builder);
   }

   public static ChangeSet inputStreamToChangeSet(InputStream in) throws IOException {
      return inputStreamToChangeSet(in, "UTF-8");
   }

   public static byte[] inputStreamToBytes(InputStream inputStream) throws IOException {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      try {
         inputStreamToOutputStream(inputStream, outputStream);
      } finally {
         Lib.close(inputStream);
      }
      return outputStream.toByteArray();
   }

   public static void inputStreamToFile(InputStream inputStream, File outFile) throws IOException {
      OutputStream outputStream = null;
      try {
         outputStream = new FileOutputStream(outFile);
         inputStreamToOutputStream(inputStream, outputStream);
      } finally {
         Lib.close(outputStream);
      }
   }

   public static CharBuffer inputStreamToCharBuffer(InputStream in) throws IOException {
      return CharBuffer.wrap(inputStreamToChangeSet(in).toCharArray());
   }

   public static InputStream stringToInputStream(String value) throws UnsupportedEncodingException {
      InputStream stream = null;
      if (value != null) {
         String data = value.trim();
         stream = new ByteArrayInputStream(data.getBytes("UTF-8"));
      }
      return stream;

   }

   public static InputStream byteBufferToInputStream(final ByteBuffer byteBuffer) {

      return new InputStream() {
         @Override
         public synchronized int read() {
            if (!byteBuffer.hasRemaining()) {
               return -1;
            }
            return byteBuffer.get();
         }

         @Override
         public synchronized int read(byte[] bytes, int off, int len) {
            len = Math.min(len, byteBuffer.remaining());
            if (off != len) {
               byteBuffer.get(bytes, off, len);
            } else {
               len = -1;
            }
            return len;
         }

         @Override
         public synchronized void reset() {
            byteBuffer.rewind();
         }

      };
   }

   public static OutputStream byteBufferToOutputStream(final ByteBuffer byteBuffer) {
      return new OutputStream() {
         @Override
         public synchronized void write(int b) {
            byteBuffer.put((byte) b);
         }

         @Override
         public synchronized void write(byte[] bytes, int off, int len) {
            byteBuffer.put(bytes, off, len);
         }
      };
   }

   public static final Pattern numberListPattern = Pattern.compile("\\d+");

   public static List<Integer> stringToIntegerList(String numberList) {
      ArrayList<Integer> ints = new ArrayList<>();
      Matcher transactionIdMatcher = numberListPattern.matcher(numberList);
      while (transactionIdMatcher.find()) {
         ints.add(Integer.parseInt(transactionIdMatcher.group()));
      }
      return ints;
   }

   public static String fileToString(File file) throws IOException {
      return new String(fileToChars(file, "UTF-8"));
   }

   public static String fileToString(Class<?> clazz, String relativePath) throws IOException {
      InputStream stream = null;
      try {
         stream = clazz.getResourceAsStream(relativePath);
         return inputStreamToString(stream);
      } finally {
         Lib.close(stream);
      }
   }

   public static byte[] fileToBytes(File file) throws IOException {
      byte[] bytes = new byte[(int) file.length()];
      InputStream inputStream = null;
      try {
         inputStream = new FileInputStream(file);
         inputStream.read(bytes);
      } finally {
         if (inputStream != null) {
            inputStream.close();
         }
      }
      return bytes;
   }

   /**
    * usage: char[] buf = new char[5500000]; for (Iterator iter = files.iterator(); iter.hasNext(); ) { // for each file
    * File file = (File)iter.next(); buf = Lib.fileToChars(file, buf);
    */
   public static char[] fileToChars(File file, char[] buf) throws IOException {
      FileReader inputReader = new FileReader(file);
      try {
         int size = (int) file.length();
         if (buf == null) {
            buf = new char[size];
         } else if (size > buf.length) {
            buf = null;
            System.gc(); // since the currently allocated buf might already be quite large
            buf = new char[size];
         }
         inputReader.read(buf);
      } finally {
         Lib.close(inputReader);
      }
      return buf;
   }

   public static CharBuffer fileToCharBuffer(File file) throws IOException {
      return fileToCharBuffer(file, "UTF-8");
   }

   public static CharBuffer fileToCharBuffer(File file, String charset) throws IOException {
      return CharBuffer.wrap(fileToChars(file, charset));
   }

   public static char[] fileToChars(File file, String charset) throws IOException {
      return inputStreamToChangeSet(new FileInputStream(file), charset).toCharArray();
   }

   public static String fillString(char c, int n) {
      char[] chars = new char[n];
      for (int i = 0; i < n; chars[i++] = c) {
         // must leave empty block here so the following line won't become
         // part of the loop
      }
      return new String(chars);
   }

   /**
    * Get file extension from the file path
    *
    * @return file extension
    */
   public static String getExtension(String filepath) {
      String toReturn = "";

      if (Strings.isValid(filepath)) {
         String toProcess = filepath.trim();

         toProcess = toProcess.replaceAll("\\\\", "/");
         String[] pathsArray = toProcess.split("/");

         if (pathsArray.length > 0) {
            String fileName = pathsArray[0];
            if (pathsArray.length > 0) {
               fileName = pathsArray[pathsArray.length - 1];
            }

            int index = fileName.lastIndexOf('.');
            if (index >= 0 && index + 1 < fileName.length()) {
               toReturn = fileName.substring(index + 1);
            }
         }
      }
      return toReturn;
   }

   /**
    * Use Processes.handleProcess(Process process, Writer output) instead
    */
   @Deprecated
   public static int handleProcess(Process proc, Writer output) {
      try {
         return Processes.handleProcess(proc, output);
      } catch (InterruptedException ex) {
         ex.printStackTrace();
         return -1;
      } catch (ExecutionException ex) {
         ex.printStackTrace();
         return -1;
      } catch (TimeoutException ex) {
         ex.printStackTrace();
         return -1;
      }
   }

   /**
    * Use Processes.handleProcess(Process process) instead
    */
   @Deprecated
   public static int handleProcess(Process process) {
      return Processes.handleProcess(process);
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
    * --index 0 = Err Stream<br>
    * --index 1 = output stream<br>
    * --index 2 = input stream<br>
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

   public static Thread[] handleProcessNoWait(Process proc, Writer output, Writer errorWriter, Reader reader) {
      return handleProcessNoWait(proc, output, errorWriter, reader, "err", "out");
   }

   public static Thread[] handleProcessNoWait(Process proc, Writer outputWriter, Writer errorWriter, Reader reader, String errName, String outName) {
      IOOutputThread errThread =
         new IOOutputThread(errorWriter, new BufferedReader(new InputStreamReader(proc.getErrorStream())));
      IOOutputThread outThread =
         new IOOutputThread(outputWriter, new BufferedReader(new InputStreamReader(proc.getInputStream())));
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

   public static String getSpace(int length) {
      return getCharacter(' ', length);
   }

   public static String getCharacter(char c, int length) {
      StringBuilder out = new StringBuilder();
      for (int i = 0; i < length; i++) {
         out.append(c);
      }
      return out.toString();
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
      return Processes.executeCommandToStdOut(callAndArgs);
   }

   public static ArrayList<String> readListFromDir(File directory, FilenameFilter filter, boolean keepExtension) {
      ArrayList<String> list = new ArrayList<>(400);

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
      ArrayList<String> list = new ArrayList<>(120);

      BufferedReader in = null;
      try {
         in = new BufferedReader(new FileReader(file));

         String line = null;
         while ((line = in.readLine()) != null) {
            if (!keepExtension) {
               line = Lib.removeExtension(line);
            }
            if (Strings.isValid(line)) {
               list.add(line);
            }
         }
      } finally {
         Lib.close(in);
      }
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
      LinkedList<File> dirList = new LinkedList<>();
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
    */
   public static List<File> recursivelyListFiles(ArrayList<File> fileList, File rootPath, Pattern filePathP) {
      return recursivelyListFilesAndDirectories(fileList, rootPath, filePathP, false);
   }

   public static List<File> recursivelyListFiles(File rootPath, Pattern fileNameP) {
      return recursivelyListFiles(new ArrayList<File>(400), rootPath, fileNameP);
   }

   public static List<File> recursivelyListFiles(File rootPath) {
      return recursivelyListFiles(new ArrayList<File>(400), rootPath, null);
   }

   /**
    * Assumptions: block comments are not nested and comment blocks have an ending Note: may be fooled by string
    * literals that contains the block comment indicators
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

   /**
    * Remove the file extension from the file path
    *
    * @return modified file path
    */
   public static String removeExtension(String filepath) {
      String ext = getExtension(filepath);
      if (ext != null && ext.length() > 0) {
         filepath = filepath.substring(0, filepath.length() - (ext.length() + 1));
      }
      return filepath;
   }

   public static String removeExtension(File file) {
      return removeExtension(file.getName());
   }

   /**
    * Determine if file is a compressed file
    *
    * @param file to check
    * @return <b>true</b> if the files is a compressed file
    */
   public static boolean isCompressed(File file) {
      boolean toReturn = false;
      String ext = getExtension(file.getAbsolutePath());
      if (ext.equals("zip")) {
         toReturn = true;
      }
      return toReturn;
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
      CharBuffer topSection = modifyBuf.subSequence(0, matcher.start(1)); // everything
      // before the
      // pattern
      CharBuffer bottomSection = null;
      int bottomLen = 0;
      if (matcher.end(1) != modifyBuf.length()) {
         bottomSection = modifyBuf.subSequence(matcher.end(1), modifyBuf.length());
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
      writeCharsToFile(charBuf.array(), outFile);
   }

   public static void writeCharsToFile(char[] chars, File outFile) throws IOException {
      FileWriter out = null;
      try {
         out = new FileWriter(outFile);
         out.write(chars, 0, chars.length);
      } finally {
         Lib.close(out);
      }
   }

   public static void writeStringToFile(String str, File outFile) throws IOException {
      OutputStreamWriter out = null;
      try {
         out = new OutputStreamWriter(new FileOutputStream(outFile), "UTF-8");
         char[] chars = str.toCharArray();
         out.write(chars, 0, chars.length);
      } finally {
         Lib.close(out);
      }
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
    * @return The path which was used to load the class file. If the file was loaded from a .jar, then the full path to
    * the jar. If the file was loaded from a .class, then the path up to the root of the package.
    */
   public static String getClassLoadPath(Class<?> base) {
      /*
       * Using the getProtectionDomain() method seems to be yield more consistent results than
       * getResource(className).getPath(); particularly when being run with the Eclipse class loader. It was found that
       * the Eclipse class loader did not always return the full path when asked for the resource.
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
      if (Strings.isValid(path)) {
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
      String toReturn = "no group";
      Process process = null;
      try {
         String[] cmd = {"/usr/bin/bash", "-c", "groups | awk '{print $1}'"};
         ProcessBuilder builder = new ProcessBuilder(cmd);
         builder.redirectErrorStream(true);
         process = builder.start();

         InputStream inputStream = null;
         try {
            inputStream = process.getInputStream();
            toReturn = inputStreamToString(inputStream);
            if (toReturn != null) {
               toReturn = toReturn.trim();
            }
         } finally {
            if (inputStream != null) {
               inputStream.close();
            }
         }

      } catch (IOException ex) {
         ex.printStackTrace();
      } finally {
         if (process != null) {
            process.destroy();
         }
      }
      return toReturn;
   }

   /**
    * Returns a list of the arguments in the source String passed. It assumes that the first character is a '(' and
    * matches till it finds the matching ')'.
    */
   public static String[] getArguments(String source) {
      ArrayList<String> theResults = new ArrayList<>();
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

            if (theChar == '(') {
               parens++;
            } else if (theChar == ')') {
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
               if (Character.isWhitespace(theChar)) {
                  startPos++;
               } else {
                  foundValidChar = true;
               }
            }
         }

         String[] theTrueResults = new String[theResults.size()];
         theResults.toArray(theTrueResults);

         if (theTrueResults.length != currentArg) {
            System.err.println("In getArguments, number of argument mismatch.");
         }

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

            if (theChar == '(') {
               parens++;
            } else if (theChar == ')') {
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
    * Returns a list of Strings representing each comma separated string in the string passed. It ignores commas inside
    * () or {}.
    */
   public static String[] getCommaSeparatedItems(String source) {
      ArrayList<String> theResults = new ArrayList<>();
      int startPos = 0;
      int parensCount = 0;

      for (int i = 0; i < source.length(); i++) {
         char theChar = source.charAt(i);

         if (theChar == '(' || theChar == '{') {
            parensCount++;
         } else if (theChar == ')' || theChar == '}') {
            parensCount--;
         } else if (parensCount == 0 && theChar == ',') {
            theResults.add(source.substring(startPos, i).trim());
            startPos = i + 1;
         }
      }
      theResults.add(source.substring(startPos).trim());
      String[] results = new String[theResults.size()];
      theResults.toArray(results);
      return results;
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
      if (!path.matches("(file:/|file://|http://|C:).*")) {
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
      while (matcher.find()) {
         changeSet.replace(matcher.start(), matcher.end(), Character.toUpperCase(matcher.group(1).charAt(0)));
      }

      return changeSet.applyChangesToSelf().toString();
   }

   /**
    * This method takes in any name separated by underscores and converts it into a java standard variable name.
    *
    * @return java variable name
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
         } else {
            sb.append(chars[i]);
         }
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
         JarInputStream jis = null;
         try {
            URL u = new URL(jarFilePath);
            URLConnection uc = u.openConnection();
            jis = new JarInputStream(uc.getInputStream());
            manifest = jis.getManifest();
         } catch (IOException ex) {
            throw new IllegalArgumentException(jarFilePath + " is not a valid file or URL.");
         } finally {
            Lib.close(jis);
         }
      } else {
         manifest = new JarFile(jarFile).getManifest();
      }
      return manifest.getMainAttributes().getValue("Implementation-Version");
   }

   public static String getElapseString(long startTime) {
      return asTimeString(System.currentTimeMillis() - startTime);
   }

   public static String asTimeString(long value) {
      long leftOverMs = value % 1000;
      long seconds = value / 1000;
      long leftOverSeconds = seconds % 60;
      long minutes = seconds / 60;
      long leftOverMinutes = minutes % 60;
      long hours = minutes / 60;
      return String.format("%d:%02d:%02d.%03d", hours, leftOverMinutes, leftOverSeconds, leftOverMs);
   }

   /**
    * Determine is OS is windows
    *
    * @return <b>true</b> if OS is windows
    */
   public static boolean isWindows() {
      return System.getProperty("os.name").indexOf("indows") != -1;
   }

   public static void writeBytesToFile(byte[] data, File file) throws IOException {
      try (OutputStream os = new FileOutputStream(file)) {
         os.write(data);
      }
   }

   public static void extractJarEntry(File jarFile, File destination, String entry) throws IOException {
      if (!destination.getParentFile().exists()) {
         destination.getParentFile().mkdirs();
      }

      InputStream inputStream = null;
      OutputStream outputStream = null;
      try {
         JarFile jarfile = new JarFile(jarFile.getAbsolutePath());
         JarEntry jarEntry = jarfile.getJarEntry(entry);

         inputStream = new BufferedInputStream(jarfile.getInputStream(jarEntry));
         outputStream = new BufferedOutputStream(new FileOutputStream(destination));
         inputStreamToOutputStream(inputStream, outputStream);

         outputStream.flush();
      } catch (Exception ex) {
         String information = String.format("JarFile: %s\nEntry: %s\nDestination: %s\n", jarFile.getAbsolutePath(),
            entry, destination.getAbsolutePath());
         throw new IOException(information + ex.getMessage());
      } finally {
         Lib.close(outputStream);
         Lib.close(inputStream);
      }
   }

   public static byte[] compressStream(InputStream in, String name) throws IOException {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      ZipOutputStream out = null;
      try {
         out = new ZipOutputStream(bos);
         // Add ZIP entry to output stream.
         out.putNextEntry(new ZipEntry(name));
         inputStreamToOutputStream(in, out);
         out.closeEntry();
      } finally {
         Lib.close(out);
      }
      return bos.toByteArray();
   }

   public static byte[] compressFile(File file) throws IOException {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();

      ZipOutputStream outputStream = null;
      try {
         outputStream = new ZipOutputStream(bos);
         compressFile(null, file, outputStream);
         outputStream.closeEntry();
      } finally {
         Lib.close(outputStream);
         Lib.close(bos);
      }
      return bos.toByteArray();
   }

   public static void compressFiles(String basePath, Collection<File> files, String zipTarget) throws IOException {
      if (Strings.isValid(zipTarget) != true) {
         throw new IllegalArgumentException("Error target zip filename is invalid");
      }
      ZipOutputStream out = null;
      try {
         out = new ZipOutputStream(new FileOutputStream(zipTarget));
         for (File file : files) {
            if (file.isDirectory() != true) {
               Lib.compressFile(basePath, file, out);
            }
         }
      } finally {
         Lib.close(out);
      }
   }

   private static void compressFile(String basePath, File file, ZipOutputStream outputStream) throws IOException {
      FileInputStream inputStream = null;
      try {
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
         inputStreamToOutputStream(inputStream, outputStream);
      } finally {
         Lib.close(inputStream);
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
      ZipOutputStream outputStream = null;
      try {
         outputStream = new ZipOutputStream(new FileOutputStream(zipTarget));
         compressDirectory(directory.getPath(), directory, outputStream, includeSubDirectories);
      } finally {
         Lib.close(outputStream);
      }
   }

   public static String decompressStream(InputStream inputStream, OutputStream outputStream) throws IOException {
      String zipEntryName = null;
      ZipInputStream zipInputStream = null;
      try {
         zipInputStream = new ZipInputStream(inputStream);
         ZipEntry entry = zipInputStream.getNextEntry();
         zipEntryName = entry.getName();
         // Transfer bytes from the ZIP file to the output file
         inputStreamToOutputStream(zipInputStream, outputStream);
      } finally {
         Lib.close(zipInputStream);
      }
      return zipEntryName;
   }

   public static void decompressStream(InputStream inputStream, File targetDirectory) throws IOException {
      ZipInputStream zipInputStream = null;
      try {
         zipInputStream = new ZipInputStream(inputStream);
         if (!targetDirectory.exists()) {
            targetDirectory.mkdirs();
         }
         ZipEntry entry = null;
         while ((entry = zipInputStream.getNextEntry()) != null) {
            String zipEntryName = entry.getName();

            OutputStream outputStream = null;
            try {
               File target = new File(targetDirectory, zipEntryName);
               if (!entry.isDirectory()) {
                  File parent = target.getParentFile();
                  if (parent != null && !parent.exists()) {
                     parent.mkdirs();
                  }
                  outputStream = new BufferedOutputStream(new FileOutputStream(target));
                  inputStreamToOutputStream(zipInputStream, outputStream);
               }
            } finally {
               Lib.close(outputStream);
            }
         }
      } finally {
         Lib.close(zipInputStream);
      }
   }

   public static byte[] decompressBytes(InputStream inputStream) throws IOException {
      ByteArrayOutputStream out = new ByteArrayOutputStream();

      ZipInputStream in = null;
      try {
         in = new ZipInputStream(inputStream);
         in.getNextEntry();
         inputStreamToOutputStream(in, out);
      } finally {
         Lib.close(in);
         Lib.close(out);
      }
      return out.toByteArray();
   }

   public static void chmod777(File file) {
      if (file != null && file.exists()) {
         Process process = null;
         try {
            ProcessBuilder builder = new ProcessBuilder("chmod", "777", file.getAbsolutePath());
            process = builder.start();

            Lib.handleProcess(process);
         } catch (IOException ioe) {
            ioe.printStackTrace();
         } finally {
            if (process != null) {
               process.destroy();
            }
         }
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

   /**
    * Determine whether the input stream is word xml content.
    *
    * @return <b>true</b> is the input stream is word xml content.
    */
   public static boolean isWordML(InputStream inputStream) {
      boolean toReturn = false;
      try {
         inputStream.mark(250);
         byte[] buffer = new byte[200];
         int index = 0;
         for (; index < buffer.length; index++) {
            if (inputStream.available() > 0) {
               buffer[index] = (byte) inputStream.read();
            } else {
               break;
            }
         }
         if (index > 0) {
            String header = new String(buffer).toLowerCase();
            if (header.contains("word.document") || header.contains("worddocument") || header.contains("<w:")) {
               toReturn = true;
            }
         }
      } catch (Exception ex) {
         ex.printStackTrace();
      } finally {
         try {
            inputStream.reset();
         } catch (IOException ex) {
            // Do Nothing
         }
      }
      return toReturn;
   }

   public static void close(AutoCloseable closable) {
      if (closable != null) {
         try {
            closable.close();
         } catch (Exception ex) {
            // Do Nothing
         }
      }
   }

   public static String getMemoryInfo() {
      MemoryUsage heapMem = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
      StringBuffer buffer = new StringBuffer();
      buffer.append("Heap Memory Usage:\n");
      buffer.append(String.format("\tUsed:      [%s]\n", toMBytes(heapMem.getUsed())));
      buffer.append(String.format("\tAllocated: [%s]\n", toMBytes(heapMem.getCommitted())));
      buffer.append(String.format("\tMax:       [%s]\n", toMBytes(heapMem.getMax())));
      return buffer.toString();
   }

   public static String toMBytes(long valInBytes) {
      NumberFormat format = NumberFormat.getInstance();
      return String.format("%s MBytes", format.format(valInBytes / 1024.0 / 1024.0));
   }

   /**
    * @return randomly generated unique long > 0
    */
   public static Long generateUuid() {
      long id = Math.abs(RANDOM.nextLong());
      if (id == 0) {
         return generateUuid();
      }
      return id;
   }

   /**
    * This will go away once database takes long for artId
    *
    * @return unique > 0 int
    */
   public static Long generateArtifactIdAsInt() {
      return (long) RANDOM.nextInt(Integer.MAX_VALUE) + 1;
   }

   /**
    * Now Is The Time to nowIsTheTime
    */
   public static String toCamelCaseFromStringsWithSpaces(String str) {
      return toCamelCase(str.replaceAll(" ", "_"));
   }

   /**
    * NOW_IS_THE_TIME to nowIsTheTime
    */
   public static String toCamelCase(String str) {
      String[] words = str.split("_");
      String result = "";
      for (String word : words) {
         result = result + word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
      }
      String firstChar = result.substring(0, 1);
      return result.replaceFirst(firstChar, firstChar.toLowerCase());
   }

   /**
    * NOW_IS_THE_TIME to Now Is The Time
    */
   public static String toCamelCaseWithSpaces(String str) {
      String[] words = str.split("_");
      String result = "";
      for (String word : words) {
         result = result + word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase() + " ";
      }
      return result;
   }

   public static boolean isCollectionOfType(Class<?> type, Type genericType, Class<?> ofType) {
      boolean isWriteable = false;
      if (Collection.class.isAssignableFrom(type) && genericType instanceof ParameterizedType) {
         ParameterizedType parameterizedType = (ParameterizedType) genericType;
         Type[] actualTypeArgs = parameterizedType.getActualTypeArguments();
         if (actualTypeArgs.length == 1) {
            Type t = actualTypeArgs[0];
            if (t instanceof Class) {
               Class<?> clazz = (Class<?>) t;
               isWriteable = ofType.isAssignableFrom(clazz);
            }
         }
      }
      return isWriteable;
   }

   public static Collection<String> getNames(Collection<? extends Named> objects) {
      Collection<String> names = new LinkedList<>();
      for (Object obj : objects) {
         names.add(((Named) obj).getName());
      }
      return names;
   }

   /**
    * {@value #EPSILON} EPSILON is used to ignore extra floating numbers for compare
    */
   private static final double EPSILON = 1E-7;

   public static boolean lessThan(double d1, double d2) {
      return d1 + EPSILON < d2;
   }

   public static boolean greaterThan(double d1, double d2) {
      return d1 > d2 + EPSILON;
   }

   /**
    * Alternate method to UriInfo.getAbsolutePath() that supports invalid characters in the URI string, which are stored
    * in a Regex constant.
    *
    * @return <b>Absolute URI path String with invalid characters removed</b>.
    */
   public static String getURIAbsolutePath(UriInfo uriInfo) {
      String uriPath = uriInfo.getPath();
      if (uriPath.startsWith("/")) {
         uriPath = uriPath.replaceFirst("\\/+", "");
      }
      uriPath = uriPath.replaceAll(INVALID_URI_CHARACTERS_REGEX, "");
      URI baseUri = uriInfo.getBaseUri();
      String basePath = baseUri.toString();
      if (!basePath.endsWith("/")) {
         basePath += "/";
         baseUri = URI.create(basePath);
      }
      return baseUri.resolve(uriPath).getPath();
   }
}
