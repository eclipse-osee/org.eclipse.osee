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
package org.eclipse.osee.ote.ui.test.manager.models;

import java.io.File;
import java.io.FileInputStream;
import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.util.io.xml.AbstractSaxHandler;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.ui.test.manager.TestManagerPlugin;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Base Class for all TestManagers
 */
public class OutputModel extends FileModel {

   private static final String OUTPUT_EXTENSION = "tmo";

   private int failedTestPoints = 0;
   private int passedTestPoints = 0;
   private boolean aborted = false;
   private boolean exists = false;
   
   
   public OutputModel(String rawFilename) {
      super(rawFilename);
   }
   
   public boolean doesOutfileExist(){
      return exists;
   }
   
   public void updateTestPointsFromOutfile(){
      try{
         File outfile = getFile();
         exists = outfile.exists();
         if(outfile.exists() && outfile.length() > 0){
            XMLReader xmlReader = XMLReaderFactory.createXMLReader();
            xmlReader.setContentHandler(new ParseTestPoints());
            xmlReader.parse(new InputSource(new FileInputStream(outfile)));
         }
      } catch (Exception ex){
         OseeLog.log(TestManagerPlugin.class, Level.SEVERE, ex);
      }
   }
   
   public int getFailedTestPoints() {
      return failedTestPoints;
   }

   public int getPassedTestPoints() {
      return passedTestPoints;
   }

   public void setFailedTestPoints(int failedTestPoints) {
      this.failedTestPoints = failedTestPoints;
   }

   public void setPassedTestPoints(int passedTestPoints) {
      this.passedTestPoints = passedTestPoints;
   }

   private class ParseTestPoints extends AbstractSaxHandler {

      

	/* (non-Javadoc)
       * @see org.eclipse.osee.framework.jdk.core.util.io.xml.AbstractSaxHandler#endElementFound(java.lang.String, java.lang.String, java.lang.String)
       */
      @Override
      public void endElementFound(String uri, String localName, String name) throws SAXException {
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.jdk.core.util.io.xml.AbstractSaxHandler#startElementFound(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
       */
      @Override
      public void startElementFound(String uri, String localName, String name, Attributes attributes) throws SAXException {
         if("TestPointResults".equals(name)){
            String fail = attributes.getValue("fail");
            String pass = attributes.getValue("pass");
            String aborted = attributes.getValue("aborted");
            try{
               failedTestPoints = Integer.parseInt(fail);
               passedTestPoints = Integer.parseInt(pass);
               if(aborted != null && aborted.length() > 0){
            	   OutputModel.this.aborted = Boolean.parseBoolean(aborted);
               }
            } catch (NumberFormatException ex){
               
            }
         }
      }
   }

   

   public String getFileExtension() {
      return OUTPUT_EXTENSION;
   }

/**
 * @return
 */
public boolean isAborted() {
	return aborted;
}

public void setAborted(boolean b) {
	aborted = b;
	
}
}