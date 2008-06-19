/*
 * Created on Jun 18, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.utility;

import java.io.File;

/**
 * @author Ken J. Aguilar
 */
public class FileChangeEvent {
   private final File file;
   private final FileChangeType changeType;

   /**
    * @param file
    * @param changeType
    */
   public FileChangeEvent(File file, FileChangeType changeType) {
      this.file = file;
      this.changeType = changeType;
   }

   /**
    * @return the file
    */
   public File getFile() {
      return file;
   }

   /**
    * @return the changeType
    */
   public FileChangeType getChangeType() {
      return changeType;
   }

}
