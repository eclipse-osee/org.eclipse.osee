package org.eclipse.osee.ote.rest.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZIPGenerator {

   private File folderToZip;

   public ZIPGenerator(File folderToZip) {
      this.folderToZip = folderToZip;
   }

   public void generateZip(OutputStream output) {
      ZipOutputStream zipOut = new ZipOutputStream(output);
      zipOut.setLevel(Deflater.DEFAULT_COMPRESSION);

      for (File file : folderToZip.listFiles()) {
         if (!file.isDirectory()){
            try {
               zipFile(zipOut, file);
            } catch (IOException e) {
               e.printStackTrace();
            }
         }
      }
      try {
         zipOut.flush();
         zipOut.close();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
   
   private void zipFile(ZipOutputStream zos, File file) throws IOException {
      if (!file.canRead()) {
         return;
      }

      zos.putNextEntry(new ZipEntry(file.getName()));
      FileInputStream fis = new FileInputStream(file);
      byte[] buffer = new byte[4092];
      int byteCount = 0;
      while ((byteCount = fis.read(buffer)) != -1)
      {
         zos.write(buffer, 0, byteCount);
      }
      fis.close();
      zos.closeEntry();
   }

}
