/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.build.report;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import org.eclipse.osee.ats.rest.internal.build.report.model.AtsElementData;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.server.OseeServerProperties;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author John Misinco
 */
public class ArchiveCollector {

   private static final String CHANGE_REPORTS_PATH = "/atsData/changeReports/";

   private final String serverData = OseeServerProperties.getOseeApplicationServerData(null);
   private final Map<String, String> urlToEntryName = new LinkedHashMap<String, String>();
   private final Set<String> pcrIds = new LinkedHashSet<String>();
   private final String supportFilesUrl;

   public ArchiveCollector(String supportFilesUrl) {
      this.supportFilesUrl = supportFilesUrl;
   }

   public void onBuildToUrlPairs(String verifierName, List<Pair<String, String>> buildToUrlPairs) {
      for (Pair<String, String> pair : buildToUrlPairs) {
         String build = pair.getFirst();
         String url = pair.getSecond();
         if (Strings.isValid(url)) {
            String entryName = String.format(AtsElementData.ARCHIVE_SCRIPT_TEMPLATE, build, verifierName);
            urlToEntryName.put(url, entryName);
            //replace url with that of the local one
            pair.setSecond(entryName);
         }
      }
   }

   public void onPcrId(String pcrId) {
      pcrIds.add(pcrId);
   }

   public void writeArchive(ZipOutputStream zout) {
      InputStream inputStream = null;
      try {
         URL url = new URL(supportFilesUrl);
         inputStream = new BufferedInputStream(url.openStream());

         ZipInputStream zin = null;
         try {
            zin = new ZipInputStream(inputStream);
            ZipEntry entry = null;
            while ((entry = zin.getNextEntry()) != null) {
               zout.putNextEntry(new ZipEntry(AtsElementData.ARCHIVE_SCRIPT_DIR + entry.getName()));
               Lib.inputStreamToOutputStream(zin, zout);
               zout.closeEntry();
            }
         } catch (IOException ex) {
            OseeExceptions.wrapAndThrow(ex);
         } finally {
            Lib.close(zin);
         }
      } catch (IOException ex) {
         OseeExceptions.wrapAndThrow(ex);
      } finally {
         Lib.close(inputStream);
      }

      for (Entry<String, String> entry : urlToEntryName.entrySet()) {
         try {
            URL url2 = new URL(entry.getKey());

            InputStream inputStream2 = null;
            try {
               inputStream2 = new BufferedInputStream(url2.openStream());
               zout.putNextEntry(new ZipEntry(entry.getValue()));
               Lib.inputStreamToOutputStream(inputStream2, zout);
               zout.closeEntry();
            } catch (IOException ex) {
               OseeExceptions.wrapAndThrow(ex);
            } finally {
               Lib.close(inputStream2);
            }
         } catch (MalformedURLException ex1) {
            OseeExceptions.wrapAndThrow(ex1);
         }
      }

      for (String pcrId : pcrIds) {
         String pcrFileName = pcrId + ".xml";
         File file = new File(serverData + CHANGE_REPORTS_PATH + pcrFileName);
         if (file.exists()) {
            FileInputStream fis = null;
            try {
               fis = new FileInputStream(file);
               zout.putNextEntry(new ZipEntry("changeReports/" + pcrFileName));
               Lib.inputStreamToOutputStream(fis, zout);
               zout.closeEntry();
            } catch (IOException ex) {
               OseeExceptions.wrapAndThrow(ex);
            } finally {
               Lib.close(fis);
            }
         }
      }
   }

}