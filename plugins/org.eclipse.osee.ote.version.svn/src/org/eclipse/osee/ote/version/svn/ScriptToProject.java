/*
 * Created on Feb 27, 2013
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ote.version.svn;

import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.resources.IProject;
import org.eclipse.team.svn.core.utility.SVNUtility;

public class ScriptToProject {

   private final Map<String, Set<String>> projectsScriptsMap = new HashMap<>();
   private final Map<String, File> scriptNameToScriptFileMap = new HashMap<>();
   private final IProject[] workspaceProjects;

   public ScriptToProject(IProject[] workspaceProjects) {
      this.workspaceProjects = workspaceProjects;
   }

   public void add(File scriptFile) {
      String scriptName = scriptFile.getName();
      IProject scriptProject = null;

      for(IProject project:workspaceProjects){
         String projectName = project.getName();
         if(scriptFile.toString().contains(projectName)){
            scriptProject = project;
         }
      }

      URI scriptProjectLocationUri = scriptProject.getLocationURI();
      File scriptProjectFile = new File(scriptProjectLocationUri);

      if (isSvn(scriptProjectFile)) {
         String scriptProjectLocation = scriptProjectFile.getAbsolutePath();
         addScriptListValue(scriptProjectLocation, scriptName);
         scriptNameToScriptFileMap.put(scriptName, scriptFile);
      } 
   }

   public Set<String> getProjectsSet() {
      return projectsScriptsMap.keySet();
   }

   public void addScriptListValue(String key, String value) {
      Set<String> values = projectsScriptsMap.get(key);
      if (values == null) {
         values = new HashSet<>();
         projectsScriptsMap.put(key, values);
      }
      values.add(value);
   }

   public File getScriptFileMatch(String project, String itemToMatch) {
      Set<String> scriptsForProject = projectsScriptsMap.get(project);
      if(scriptsForProject.contains(itemToMatch)){
         return scriptNameToScriptFileMap.get(itemToMatch);
      } else {
         return null;
      }
   }

   protected boolean isSvn(File file) {
      File svn = new File(file, SVNUtility.getSVNFolderName());
      return svn.exists();
   }
}
