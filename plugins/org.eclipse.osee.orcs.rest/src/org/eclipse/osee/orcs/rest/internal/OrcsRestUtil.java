/*********************************************************************
 * Copyright (c) 2015 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.orcs.rest.internal;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.zip.ZipEntry;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.orcs.data.TransactionReadable;
import org.eclipse.osee.orcs.rest.model.Transaction;

/**
 * @author Roberto E. Escobar
 */
public final class OrcsRestUtil {

   private OrcsRestUtil() {
      // Utility class
   }

   public static <T> T executeCallable(Callable<T> callable) {
      try {
         return callable.call();
      } catch (Exception ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   public static Response asResponse(boolean modified) {
      ResponseBuilder builder;
      if (modified) {
         builder = Response.ok();
      } else {
         builder = Response.notModified();
      }
      return builder.build();
   }

   public static List<Transaction> asTransactions(ResultSet<? extends TransactionReadable> results) {
      List<Transaction> toReturn = new ArrayList<>(results.size());
      for (TransactionReadable data : results) {
         toReturn.add(asTransaction(data));
      }
      return toReturn;
   }

   public static Transaction asTransaction(TransactionReadable tx) {
      Transaction data = new Transaction();
      data.setTxId(tx);
      data.setAuthor(tx.getAuthor());
      data.setBranchUuid(tx.getBranch().getId());
      data.setComment(tx.getComment());
      data.setCommitArt(tx.getCommitArt());
      data.setTimeStamp(tx.getDate());
      data.setTxType(tx.getTxType());
      return data;
   }
   
   private static boolean isSearchedFile(String filename, String subname, String ext) {
      if(filename.length() + 1 < subname.length() + ext.length())
         return false;
      
      boolean getname = false;
      boolean getext = false;
      
      if(subname.isEmpty())
         getname = true;
      else if(filename.contains(subname))
         getname = true;        
         
      if(ext.isEmpty())
         getext = true;
      else if(filename.substring(filename.length() - ext.length()-1, filename.length()).equals("." + ext))
         getext = true;
      
      if(getname && getext)
         return true;
      return false;      
   }
   
   public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
      File destFile = new File(destinationDir, zipEntry.getName());
      String destDirPath = destinationDir.getCanonicalPath();
      String destFilePath = destFile.getCanonicalPath();

      if (!destFilePath.startsWith(destDirPath + File.separator)) {
         throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
      }
      return destFile;
   }
   
   /*
    * get the latest file name from folder. Search for all name if subname is empty
    * Search for all files if ext is empty
    */
   public static  String getLatestFile(String folder, String subname, String ext) {

      File dir = new File(folder);
      File[] files = dir.listFiles();
      if (files == null || files.length == 0) {
          return null;
      }

      File lastModifiedFile = files[0];
      int index = -1;
      for (int i = 0; i < files.length; i++) {
         if(isSearchedFile(files[i].getName(), subname, ext)) {
            index = i;
            lastModifiedFile = files[i];
            break;
         }
      }
      
      for (int i = index + 1; i < files.length; i++) {
          if (isSearchedFile(files[i].getName(), subname, ext) && lastModifiedFile.lastModified() < files[i].lastModified()) {
              lastModifiedFile = files[i];
          }
      }
      String k = lastModifiedFile.toString();
      String name = lastModifiedFile.getName();

      //Path p = Paths.get(k);
      //String file = p.getFileName().toString();
      
      if(index > -1)
         return name;
      return null;
  }
}