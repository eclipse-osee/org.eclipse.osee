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
package org.eclipse.osee.ote.core.framework.saxparse;

import java.io.InputStream;
import java.util.logging.Level;

import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.framework.saxparse.elements.TestPointResultsData;
import org.eclipse.osee.ote.core.framework.saxparse.elements.TimeSummaryData;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * @author Andrew M. Finkbeiner
 */
public class ProcessOutfileOverview {

   private String elapsedTime;
   private String scriptName;
   private String results = "";
   
   public ProcessOutfileOverview() {
   }

   public void run(InputStream inputStream) throws Exception {

      long time = System.currentTimeMillis();

      XMLReader xmlReader = XMLReaderFactory.createXMLReader();
      OteSaxHandler handler = new OteSaxHandler();
      xmlReader.setContentHandler(handler);
      xmlReader.setProperty("http://xml.org/sax/properties/lexical-handler", handler);

      handler.getHandler("TimeSummary").addListener(new IBaseSaxElementListener() {

         @Override
         public void onEndElement(Object obj) {
         }

         @Override
         public void onStartElement(Object obj) {
            if (obj instanceof TimeSummaryData) {
               TimeSummaryData data = (TimeSummaryData) obj;
               elapsedTime = data.getElapsed();
            }
         }

      });
      handler.getHandler("TestPointResults").addListener(new IBaseSaxElementListener() {

         @Override
         public void onEndElement(Object obj) {
         }

         @Override
         public void onStartElement(Object obj) {
            if (obj instanceof TestPointResultsData) {
               TestPointResultsData data = (TestPointResultsData) obj;
               String fail = data.getFail();
               String pass = data.getPass();
               String aborted = data.getAborted();
               try {
                  int failedTestPoints = Integer.parseInt(fail);
                  int passedTestPoints = Integer.parseInt(pass);
                  int totalTestPoints = passedTestPoints + failedTestPoints;
                  boolean abort = false;
                  if (aborted != null && aborted.length() > 0) {
                     abort = Boolean.parseBoolean(aborted);
                  }
                  results = "";
                  if (abort) {
                     results = String.format("ABORTED  -  Total[%d] Fail[%d]", totalTestPoints, failedTestPoints);
                  } else if (failedTestPoints > 0) {
                     results = String.format("FAILED  -  Total[%d] Fail[%d]", totalTestPoints, failedTestPoints);
                  } else {
                     results = String.format("PASSED  -  Total[%d] Fail[%d]", totalTestPoints, failedTestPoints);
                  }
               } catch (NumberFormatException ex) {

               }
            }
         }

      });
      handler.getHandler("ScriptName").addListener(new IBaseSaxElementListener() {

         @Override
         public void onEndElement(Object obj) {
            scriptName = obj.toString();
         }

         @Override
         public void onStartElement(Object obj) {
         }
      });
   
      xmlReader.parse(new InputSource(inputStream));
    
      long all = System.currentTimeMillis() - time;

      OseeLog.logf(getClass(), Level.INFO, "It took %d ms total to process.", all);
   }

   public String getElapsedTime() {
      return elapsedTime;
   }

   public String getResults() {
      return results;
   }
   
   public String getScriptName() {
      return scriptName;
   }


}
