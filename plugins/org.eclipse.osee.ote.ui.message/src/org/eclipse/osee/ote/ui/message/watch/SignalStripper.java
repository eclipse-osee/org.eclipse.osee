/*
 * Created on Apr 14, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ote.ui.message.watch;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Michael P. Masterson
 */
public class SignalStripper {

   /**
    * 
    * @param args Requires one argument
    * @throws IOException
    */
   public static void main(String[] args) throws IOException {
      if( args.length != 1 )
      {
         throw new IllegalArgumentException("Usage: SignalStripper <Path to folder containing java scripts>");
      }
      File folderToStartAt = new File(args[0]);
      new SignalStripper().buildMwiForEachScript(folderToStartAt);
   }

   private void buildMwiForEachScript(File folderToStartAt) throws FileNotFoundException {
      File[] scriptFiles = findFiles(folderToStartAt);
      System.out.println("Updating " + scriptFiles.length + " projects...");
      
      for( File project : scriptFiles ){
         generateMwi(project);
      }
      System.out.println("Done.");

   }

   /**
    * @param folderToStartAt 
    * @return
    * @throws FileNotFoundException
    */
   private File[] findFiles(File folderToStartAt) throws FileNotFoundException {
      if( !folderToStartAt.exists() || !folderToStartAt.isDirectory() )
         throw new FileNotFoundException("Workspace root not found:" + folderToStartAt.getAbsolutePath());

      File[] scriptProjects = folderToStartAt.listFiles(new FilenameFilter() {

         @Override
         public boolean accept(File dir, String name) {
            return name.endsWith(".java");
         }
      });
      return scriptProjects;
   }

   private void generateMwi(File scriptFile) {
      try {
         System.out.println("-----------------------------------------------------------------");
         System.out.println("Looking at script " + scriptFile.getName());
         
         String fileAsString = Lib.fileToString(scriptFile);
         String mwiAsString = generateStringToWrite(fileAsString);
         if( mwiAsString != null )
            writeMwi(scriptFile, mwiAsString);
         else {
            System.err.println("No messages found for " + scriptFile);
            System.err.flush();
         }
         
      }
      catch (IOException ex) {
         System.err.println("Problem writing mwi files.");
         ex.printStackTrace();
      }
   }

   /**
    * 
    * @param fileAsString
    * @return String to use when writing an mwi file or null if something went wrong
    * @throws IOException
    */
   public String generateStringToWrite(String fileAsString) {
      HashCollection<String, String> fullyQualifiedMessageNameToElementListMap = getMessageClassToElementsNamesMap(fileAsString);
      String mwiAsString = generateMwiAsString(fullyQualifiedMessageNameToElementListMap);
      return mwiAsString;
   }
 
   private String generateMwiAsString(HashCollection<String, String> fullyQualifiedMessageNameToElementListMap) {
      StringBuilder builder = new StringBuilder();
      for( String className : fullyQualifiedMessageNameToElementListMap.keySet())
      {
         Collection<String> elements = fullyQualifiedMessageNameToElementListMap.getValues(className);
         for(String element : elements)
         {
            builder.append(className).append("+").append(element).append("\n");
         }
         
      }
      
      if( builder.length() == 0)
         return null;
      else 
         return "version=2.0\n" + builder.toString();
   }

   private void writeMwi(File scriptFile, String mwiAsString) throws IOException {
      String absolutePath = scriptFile.getAbsolutePath();
      String fileNameWithoutExtension = absolutePath.substring(0, absolutePath.length()-5);
      
      File outputFile = new File(fileNameWithoutExtension + ".mwi");
      
      BufferedWriter outputStream = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), "UTF-8"));
      System.out.println("Writing " + outputFile.getName());
      outputStream.write(mwiAsString);
      outputStream.flush();
      outputStream.close();
      
   }

   private HashCollection<String, String> getMessageClassToElementsNamesMap(String fileAsString) {
      List<String> importedMessages = extractMessageImports(fileAsString);
      Map<String, String> variableNameToMessageClassNameMap = findVariablesAndCreateVariableToClassMap(importedMessages,fileAsString);
      HashCollection<String, String> messageClassToElementNamesMap = findElementsUsed(variableNameToMessageClassNameMap,fileAsString);
      return messageClassToElementNamesMap;
   }

   private List<String> extractMessageImports(String fileAsString) {
      List<String> retVal = new ArrayList<String>();
      Pattern pattern = Pattern.compile("import (.*+.(\\w|.)+.[A-Z0-9_]+);");
      Matcher matcher = pattern.matcher(fileAsString);
      while( matcher.find()){
         String fullyQualifiesMessageClass = matcher.group(1);
         if( fullyQualifiesMessageClass.contains("enum"))
            continue;
         
         retVal.add(fullyQualifiesMessageClass);
      }
      return retVal;
   }

   private Map<String, String> findVariablesAndCreateVariableToClassMap(List<String> importedMessages, String fileAsString) {
      Map<String, String> retVal = new HashMap<String, String>();
      for( String fullyQualifiedMessage : importedMessages)
      {
         
         String[] split = fullyQualifiedMessage.split("\\.");
         String className = split[split.length-1];
         String variableName = findVariableNameFor(className, fileAsString);
         
         retVal.put(variableName, fullyQualifiedMessage);
      }
      return retVal;
   }

   private String findVariableNameFor(String className, String fileAsString) {
      Pattern pattern = Pattern.compile("\\s" + className + "\\s+(\\w+)\\s*(\\=|;)");
      Matcher matcher = pattern.matcher(fileAsString);
      if( matcher.find())
      {
         return matcher.group(1);
      }
      return null;
   }

   private HashCollection<String, String> findElementsUsed(Map<String, String> variableNameToMessageClassNameMap, String fileAsString) {
      HashCollection<String, String> retVal = new  HashCollection<String, String>(false,HashSet.class); 
      Pattern pattern = Pattern.compile("\\W(\\w+)\\.([A-Z0-9_]+)\\.");
      Matcher matcher = pattern.matcher(fileAsString);
      while( matcher.find()) {
         String variable = matcher.group(1);
         String className = variableNameToMessageClassNameMap.get(variable);
         String elementName = matcher.group(2);
         
         if( className != null) // it's possible someone forgot to instantiate something
            retVal.put(className, elementName);
         else {
            retVal.size();
         }
      }
         
      return retVal;
   }

}
