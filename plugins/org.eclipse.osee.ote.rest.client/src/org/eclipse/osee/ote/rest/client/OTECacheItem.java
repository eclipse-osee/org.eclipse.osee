package org.eclipse.osee.ote.rest.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.eclipse.osee.framework.jdk.core.util.ChecksumUtil;

public class OTECacheItem {
   private File file;
   private String md5;
   
   public OTECacheItem(File file, String md5){
      this.file = file;
      this.md5 = md5;
   }
   
   public OTECacheItem(File file) throws FileNotFoundException, Exception{
      this.file = file;
      md5 = ChecksumUtil.createChecksumAsString(new FileInputStream(file), "MD5");
   }
   
   public File getFile() {
      return file;
   }
   
   public String getMd5() {
      return md5;
   }
   
}
