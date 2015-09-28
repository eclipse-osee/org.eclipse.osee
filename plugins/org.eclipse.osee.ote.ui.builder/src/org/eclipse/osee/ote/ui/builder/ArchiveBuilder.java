package org.eclipse.osee.ote.ui.builder;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

/**
 * This class writes class files to a jar file.  This is used as part of the OTE Builder that will package up 
 * java test classes and then ship them to a server to be executed. 
 * 
 * @author Andrew M. Finkbeiner
 *
 */
class ArchiveBuilder {

   private List<IPath> outputLocations;
   private Set<IContainer> packages;
   private File workspaceArchiveHome;

   public ArchiveBuilder(File workspaceArchiveHome, List<IPath> outputLocations) {
      this.workspaceArchiveHome = workspaceArchiveHome;
      this.outputLocations = outputLocations;
      this.packages = new HashSet<>();
   }

   /**
    * Add a resources parent to be archived as a java package.
    * 
    * @param resource
    */
   public void addFile(IResource resource) {
      packages.add(resource.getParent());
   }
   
   /**
    * Builds a jar file with the currently added packages.
    */
   public void archive() {
      for(IContainer path:packages){
         try {
            IResource[] resources = path.members();
            if(resources.length > 0){
               String fileName = getFileName(path);
               File archive = new File(workspaceArchiveHome, fileName + ".jar");
               if(archive.exists()){
                  archive.delete();
               }
               JarPackager jarPackager = new JarPackager(getPath(path));
               jarPackager.open(archive);
               try{
                  for(IResource resource:resources){
                     if(resource.getFullPath().toString().endsWith(".class")){
                        jarPackager.add(resource);
                     }
                  }
               } finally {
                  jarPackager.close();
               }
            }
         } catch (CoreException e) {
            e.printStackTrace();
         } catch (FileNotFoundException e) {
            e.printStackTrace();
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
   }

   private String getFileName(IContainer path) {
      String name = path.getFullPath().toString().replace(".", "").replace('/', '.');
      return name.substring(1);
   }

   private String getPath(IContainer path){
      String strPath = path.getFullPath().toString();
      for(IPath sourcePath:outputLocations){
         String source = sourcePath.toString();
         if(strPath.startsWith(sourcePath.toString())){
            return strPath.substring(source.length());
         }
      }
      return "";
   }

}
