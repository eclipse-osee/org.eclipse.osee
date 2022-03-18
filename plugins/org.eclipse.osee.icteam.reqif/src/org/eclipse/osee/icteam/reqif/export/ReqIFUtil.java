/*********************************************************************
 * Copyright (c) 2021 Robert Bosch Engineering and Business Solutions Ltd India
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Robert Bosch Engineering and Business Solutions Ltd India - initial API and implementation
 *     Boeing - update for RMF 0.13
 **********************************************************************/
package org.eclipse.osee.icteam.reqif.export;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeConfigurationException;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.framework.core.enums.RelationSorter;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;

/**
 * This is a Util Class with utilities used by Reqif Export and Import
 *
 * @author Manjunath Sangappa
 */
public class ReqIFUtil {

   /**
    * Gets the current Data
    *
    * @return : Data
    * @throws DatatypeConfigurationException :
    */
   public static GregorianCalendar getGregorianCalendarNow() throws DatatypeConfigurationException {
      GregorianCalendar calendar = new GregorianCalendar();
      calendar.setTime(new Date());
      return calendar;
   }

   /**
    * @param projectBranch :
    * @param parent :
    * @param moduleName :
    * @return :
    * @throws OseeCoreException :
    */
   public static Artifact createModuleReqFolder(final Branch projectBranch, final Artifact parent, final String moduleName) throws OseeCoreException {

      SkynetTransaction transaction1 = TransactionManager.createTransaction(projectBranch,
         String.format("Created new %s \"%s\" in artifact explorer", "Project Module", moduleName));
      Artifact newChildArt = parent.addNewChild(RelationSorter.PREEXISTING, AtsArtifactTypes.ProjectModule, moduleName);

      parent.persist(transaction1);
      transaction1.execute();

      return newChildArt;
   }

   /**
    * @param fileName
    * @throws IOException
    */
   public static File changeExtension(final String fileName) throws IOException {
      String newFileName = fileName.replace(".xml", ".reqif");
      File reqifFile = new File(newFileName);
      File oldFile = new File(fileName);

      InputStream inStream = new FileInputStream(oldFile);
      OutputStream outStream = new FileOutputStream(reqifFile);

      byte[] buffer = new byte[1024];

      int length;
      // copy the file content in bytes
      while ((length = inStream.read(buffer)) > 0) {

         outStream.write(buffer, 0, length);

      }

      inStream.close();
      outStream.close();

      return reqifFile;
   }
}