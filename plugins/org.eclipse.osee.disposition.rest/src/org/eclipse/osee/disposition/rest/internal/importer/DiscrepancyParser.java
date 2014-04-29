/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.disposition.rest.internal.importer;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.eclipse.osee.disposition.model.Discrepancy;
import org.eclipse.osee.disposition.model.DispoItemData;
import org.eclipse.osee.disposition.rest.util.DispoUtil;
import org.eclipse.osee.framework.jdk.core.type.MutableBoolean;
import org.eclipse.osee.framework.jdk.core.type.MutableInteger;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * @author Angel Avila
 */
public class DiscrepancyParser {

   public DiscrepancyParser() {

   }

   public static final class MutableDate {

      private Date value;

      public Date getValue() {
         return value;
      }

      public void setValue(Date value) {
         this.value = value;
      }

   }

   public static final class MutableString {

      private String value;

      public String getValue() {
         return value;
      }

      public void setValue(String value) {
         this.value = value;
      }
   }

   public static boolean buildItemFromFile(DispoItemData dispoItem, String resourceName, InputStream inputStream, final boolean isNewImport, final Date lastUpdate) throws Exception {
      final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
      final MutableBoolean isWithinTestPointElement = new MutableBoolean(false);
      final MutableInteger idOfTestPoint = new MutableInteger(0);
      final StringBuilder textAppendable = new StringBuilder();
      final MutableBoolean isFailure = new MutableBoolean(false);
      final JSONObject discrepancies = new JSONObject();
      final MutableDate scriptRunDate = new MutableDate();
      final MutableDate firstTestPointDate = new MutableDate();
      final MutableBoolean stoppedParsing = new MutableBoolean(false);
      final MutableString version = new MutableString();
      final MutableString totalPoints = new MutableString();
      final MutableBoolean firstTestPointResultFound = new MutableBoolean(false);
      final MutableString abortedFlag = new MutableString();

      XMLReader xmlReader = XMLReaderFactory.createXMLReader();
      DispoSaxHandler handler = new DispoSaxHandler();
      xmlReader.setContentHandler(handler);
      xmlReader.setProperty("http://xml.org/sax/properties/lexical-handler", handler);

      handler.getHandler("Time").addListener(new SaxElementAdapter() {

         @Override
         public void onStartElement(Object obj) throws Exception {
            if (obj != null) {
               if (firstTestPointDate.getValue() == null) {
                  String timeString = (String) obj;
                  Long timeLong = Long.parseLong(timeString);
                  firstTestPointDate.setValue(new Date(timeLong));
                  if (!isNewImport && !firstTestPointDate.getValue().after(lastUpdate)) {
                     throw new BreakSaxException("Stopped Parsing");
                  }
               }
            }
         }
      });

      handler.getHandler("ScriptVersion").addListener(new SaxElementAdapter() {

         @Override
         public void onStartElement(Object obj) throws Exception {
            ScriptVersionData data = (ScriptVersionData) obj;
            version.setValue(data.getRevision());
         }
      });

      handler.getHandler("TimeSummary").addListener(new SaxElementAdapter() {
         @Override
         public void onStartElement(Object obj) throws Exception {
            TimeSummaryData data = (TimeSummaryData) obj;
            try {
               scriptRunDate.setValue(DATE_FORMAT.parse(data.getEndDate()));
               if (!isNewImport && !scriptRunDate.getValue().after(lastUpdate)) {
                  throw new BreakSaxException("Stopped Parsing");
               }

            } catch (ParseException ex) {
               throw ex;
            }
         }
      });

      handler.getHandler("TestPoint").addListener(new SaxElementAdapter() {

         @Override
         public void onEndElement(Object obj) throws JSONException {
            if (isWithinTestPointElement.getValue() && isFailure.getValue()) {
               Discrepancy discrepancy = new Discrepancy();
               discrepancy.setText(textAppendable.toString());
               discrepancy.setLocation(idOfTestPoint.getValue());
               String id = GUID.create();
               discrepancy.setId(id);
               JSONObject discrepancyAsJson = DispoUtil.discrepancyToJsonObj(discrepancy);
               discrepancies.put(id, discrepancyAsJson);

               isFailure.setValue(false);
            }
            textAppendable.delete(0, textAppendable.length());
            isWithinTestPointElement.setValue(false);
         }

         @Override
         public void onStartElement(Object obj) {
            isWithinTestPointElement.setValue(true);
            firstTestPointResultFound.setValue(false);
         }
      });

      handler.getHandler("Number").addListener(new SaxElementAdapter() {
         @Override
         public void onEndElement(Object obj) {
            if (isWithinTestPointElement.getValue()) {
               idOfTestPoint.setValue(Integer.valueOf(obj.toString()));

               textAppendable.append("Failure at Test Point ");
               textAppendable.append(idOfTestPoint);
               textAppendable.append(". ");
            }
         }
      });

      handler.getHandler("TestPointName").addListener(new SaxElementAdapter() {
         @Override
         public void onEndElement(Object obj) {
            if (isFailure.getValue()) {
               textAppendable.append("Check Point: ");
               textAppendable.append(obj);
               textAppendable.append(". ");
            }
         }
      });

      handler.getHandler("Result").addListener(new SaxElementAdapter() {

         @Override
         public void onEndElement(Object obj) {
            if (isWithinTestPointElement.getValue() && !firstTestPointResultFound.getValue()) {
               firstTestPointResultFound.setValue(true);
               if (obj.equals("PASSED")) {
                  isFailure.setValue(false);
               } else {
                  isFailure.setValue(true);
               }
            }
         }
      });

      handler.getHandler("CheckGroup").addListener(new SaxElementAdapter() {
         @Override
         public void onStartElement(Object obj) {
            if (isFailure.getValue() && isWithinTestPointElement.getValue()) {
               textAppendable.append("Check Group with Checkpoint Failures: ");
            }
         }
      });

      handler.getHandler("Expected").addListener(new SaxElementAdapter() {
         @Override
         public void onEndElement(Object obj) {
            if (isWithinTestPointElement.getValue() && isFailure.getValue()) {
               textAppendable.append("Expected: ");
               textAppendable.append(obj);
               textAppendable.append(". ");
            }
         }
      });
      handler.getHandler("Actual").addListener(new SaxElementAdapter() {
         @Override
         public void onEndElement(Object obj) {
            if (isWithinTestPointElement.getValue() && isFailure.getValue()) {
               textAppendable.append("Actual: ");
               textAppendable.append(obj);
               textAppendable.append(". ");
            }
         }
      });

      handler.getHandler("TestPointResults").addListener(new SaxElementAdapter() {
         @Override
         public void onStartElement(Object obj) {
            if (obj instanceof TestPointResultsData) {
               TestPointResultsData data = (TestPointResultsData) obj;
               String fail = data.getFail();
               String pass = data.getPass();
               abortedFlag.setValue(data.getAborted());
               try {
                  int failedTestPoints = Integer.parseInt(fail);
                  int passedTestPoints = Integer.parseInt(pass);
                  int totalTestPoints = passedTestPoints + failedTestPoints;
                  totalPoints.setValue(String.valueOf(totalTestPoints));
               } catch (NumberFormatException ex) {
                  throw new OseeCoreException(ex);
               }
            }
         }

      });

      try {
         if (inputStream.available() > 0) {
            xmlReader.parse(new InputSource(inputStream));
         } else {
            dispoItem.setVersion("EMPTY TMO");
            dispoItem.setCreationDate(new Date(1));
            dispoItem.setLastUpdate(new Date(1));
         }
      } catch (Exception ex) {
         if (ex.getMessage().equals("Stopped Parsing")) {
            stoppedParsing.setValue(true);
         } else {
            throw ex;
         }
      }

      if (!stoppedParsing.getValue()) {
         dispoItem.setName(resourceName);

         if (abortedFlag.getValue() == null || totalPoints.getValue() == null) {
            if (totalPoints.getValue() == null) {
               totalPoints.setValue("-1");
            }
            String id = String.valueOf(Lib.generateUuid());
            discrepancies.put(id,
               createAdditionalDiscrepancy(id, "No Test Point Result Summary Tag, file may have been aborted"));
         } else if (abortedFlag.getValue().equalsIgnoreCase("TRUE")) {
            String id = String.valueOf(Lib.generateUuid());
            discrepancies.put(id, createAdditionalDiscrepancy(id, "Aborted"));
         }

         dispoItem.setTotalPoints(totalPoints.getValue());
         dispoItem.setDiscrepanciesList(discrepancies);

         if (version.getValue() == null) { // version can be empty if not version control 
            version.setValue("No version control");
         }
         dispoItem.setVersion(version.getValue());

         if (scriptRunDate.getValue() == null) {
            scriptRunDate.setValue(new Date(0));
         }
         if (isNewImport) {
            dispoItem.setCreationDate(scriptRunDate.getValue());
         }
         dispoItem.setLastUpdate(scriptRunDate.getValue());
      }
      return stoppedParsing.getValue();
   }

   private static JSONObject createAdditionalDiscrepancy(String id, String message) {
      Discrepancy discrepancy = new Discrepancy();
      discrepancy.setText(message);
      discrepancy.setLocation(0);
      discrepancy.setId(id);
      return DispoUtil.discrepancyToJsonObj(discrepancy);
   }
}
