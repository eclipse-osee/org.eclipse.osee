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
package org.eclipse.osee.framework.ui.skynet.dbHealth;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.client.server.HttpUrlBuilder;
import org.eclipse.osee.framework.core.data.OseeServerContext;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeTypeDoesNotExist;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.HttpProcessor;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.HttpProcessor.AcquireResult;

/**
 * @author Roberto E. Escobar
 */
public class AttributeBackingDataCheck extends DatabaseHealthOperation {

   private static final String DESCRIPTION = "Reads Attribute Table and checks that binary data exists";
   private static final String FIX_DETAILS = "No fix Available";

   private static final String READ_VALID_ATTRIBUTES =
         "select oa.attr_id, oa.gamma_id, oa.art_id, oa.uri from osee_attribute oa where oa.uri is not null";

   public AttributeBackingDataCheck() {
      super("Attribute Binary Data Availability");
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.dbHealth.DatabaseHealthOperation#getFixTaskName()
    */
   @Override
   public String getFixTaskName() {
      return Strings.emptyString();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.dbHealth.DatabaseHealthOperation#doHealthCheck(org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   protected void doHealthCheck(IProgressMonitor monitor) throws Exception {
      List<AttrData> attrDatas = loadAttributeData(monitor);
      monitor.worked(calculateWork(0.40));

      List<AttrData> errors = new ArrayList<AttrData>();
      if (!attrDatas.isEmpty()) {
         int totalAttrs = attrDatas.size();
         int work = calculateWork(0.40) / totalAttrs;
         for (int index = 0; index < attrDatas.size(); index++) {
            checkForCancelledStatus(monitor);
            AttrData attrData = attrDatas.get(index);
            monitor.setTaskName(String.format("[%s of %s] - attributes [%s]", index, totalAttrs, attrData.getUri()));
            if (!isAttrDataValid(attrData)) {
               errors.add(attrData);
            }
            monitor.worked(work);
         }
      } else {
         monitor.worked(calculateWork(0.40));
      }
      setItemsToFix(errors.size());

      // Write Report;
      appendToDetails(AHTML.beginMultiColumnTable(100, 1));
      appendToDetails(AHTML.addHeaderRowMultiColumnTable(new String[] {"URI", "GAMMA ID", "ATTR ID", "ART ID", "REASON"}));
      for (AttrData attrData : errors) {
         appendToDetails(AHTML.addRowMultiColumnTable(new String[] {attrData.getUri(),
               String.valueOf(attrData.getGammaId()), String.valueOf(attrData.getAttrId()),
               String.valueOf(attrData.getArtId()), attrData.getReason()}));
      }
      appendToDetails(AHTML.endMultiColumnTable());
      monitor.worked(calculateWork(0.10));
      checkForCancelledStatus(monitor);

      getSummary().append(String.format("Found [%s] attributes missing binary data", getItemsToFixCount()));
      monitor.worked(calculateWork(0.10));
   }

   private boolean isAttrDataValid(AttrData attrData) {
      boolean result = false;
      try {
         Map<String, String> parameters = new HashMap<String, String>();
         parameters.put("sessionId", ClientSessionManager.getSessionId());
         parameters.put("uri", attrData.getUri());
         parameters.put("check.available", "true");
         String url =
               HttpUrlBuilder.getInstance().getOsgiServletServiceUrl(OseeServerContext.RESOURCE_CONTEXT, parameters);
         ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
         AcquireResult acquireResult = HttpProcessor.acquire(new URL(url), outputStream);
         if (acquireResult.wasSuccessful()) {
            result = true;
         } else {
            attrData.setReason(outputStream.toString(acquireResult.getEncoding()));
         }
      } catch (Exception ex) {
         attrData.setReason(Lib.exceptionToString(ex));
      }
      return result;
   }

   private List<AttrData> loadAttributeData(IProgressMonitor monitor) throws OseeDataStoreException, OseeTypeDoesNotExist {
      List<AttrData> data = new ArrayList<AttrData>();
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      try {
         chStmt.runPreparedQuery(READ_VALID_ATTRIBUTES);
         while (chStmt.next()) {
            checkForCancelledStatus(monitor);
            String uri = chStmt.getString("uri");
            if (Strings.isValid(uri)) {
               data.add(new AttrData(uri, chStmt.getLong("gamma_id"), chStmt.getInt("attr_id"), chStmt.getInt("art_id")));
            }
         }
      } finally {
         chStmt.close();
      }
      return data;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.dbHealth.DatabaseHealthOperation#getCheckDescription()
    */
   @Override
   public String getCheckDescription() {
      return DESCRIPTION;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.dbHealth.DatabaseHealthOperation#getFixDescription()
    */
   @Override
   public String getFixDescription() {
      return FIX_DETAILS;
   }

   private final class AttrData {
      private final String uri;
      private final long gammaId;
      private final int attrId;
      private final int artId;
      private String reason;

      public AttrData(String uri, long gammaId, int attrId, int artId) {
         super();
         this.uri = uri;
         this.gammaId = gammaId;
         this.attrId = attrId;
         this.artId = artId;
         this.reason = null;
      }

      public String getUri() {
         return uri;
      }

      public long getGammaId() {
         return gammaId;
      }

      public int getAttrId() {
         return attrId;
      }

      public int getArtId() {
         return artId;
      }

      public String getReason() {
         return reason;
      }

      public void setReason(String reason) {
         this.reason = reason;
      }
   }
}
