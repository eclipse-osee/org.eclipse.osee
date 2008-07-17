/*
 * Created on Jul 15, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.connection.service;

import java.io.File;

/**
 * @author b1529404
 */
class LocalFileKey implements IFileKey {

   private final File file;

   /**
    * @param file
    */
   LocalFileKey(File file) {
      this.file = file;
   }

   File getFile() {
      return file;
   }
}
