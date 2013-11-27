package org.eclipse.osee.ote.ui.builder;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import org.eclipse.core.resources.IResource;
import org.eclipse.osee.framework.jdk.core.util.ChecksumUtil;

/**
 * Creates a jar file from a single package folder.
 * 
 * @author Andrew M. Finkbeiner
 *
 */
class JarPackager {
   
   private JarOutputStream jos;
   private String path;
   private File archive;
   
   
   JarPackager(String path){
      this.path = path;
      if(!this.path.endsWith("/")){
         this.path += "/";
      }
      if(this.path.startsWith("/")){
         this.path = this.path.substring(1);
      }
   }
   
   /**
    * Open the archive so that we can add resources to it.
    * 
    * @param archive
    * @throws FileNotFoundException
    * @throws IOException
    */
   public void open(File archive) throws FileNotFoundException, IOException {
      this.archive = archive;
      Manifest manifest = new Manifest();
      manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
      jos = new JarOutputStream(new FileOutputStream(archive), manifest);
      JarEntry entry = new JarEntry(path);
      jos.putNextEntry(entry);
      jos.closeEntry();
   }

   public void close() throws Exception {
      jos.close();
      InputStream in = null;
      PrintStream ps = null;
      try{
         in = new FileInputStream(archive);
         String diskMd5Digest = ChecksumUtil.createChecksumAsString(in, "MD5");
         ps = new PrintStream(new FileOutputStream(new File(archive.getParentFile(), archive.getName() + ".md5")));
         ps.print(diskMd5Digest);
      } finally {
         if(in != null){
            in.close();
         }
         if(ps != null){
            ps.close();
         }
      }
   }

   public void add(IResource resource) throws IOException {
      File theFile = resource.getLocation().toFile();
      if(theFile.exists()){
         BufferedInputStream in = null;
         try
         {
            String name = path + theFile.getName();

            JarEntry entry = new JarEntry(name);
            jos.putNextEntry(entry);
            in = new BufferedInputStream(new FileInputStream(theFile));

            byte[] buffer = new byte[1024];
            while (true)
            {
               int count = in.read(buffer);
               if (count == -1)
                  break;
               jos.write(buffer, 0, count);
            }
            jos.closeEntry();
         }
         finally
         {
            if (in != null)
               in.close();
         }
      }
   }
   
}
