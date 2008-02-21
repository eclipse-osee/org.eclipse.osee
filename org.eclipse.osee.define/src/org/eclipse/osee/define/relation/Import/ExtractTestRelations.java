/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.relation.Import;

import java.io.File;
import java.io.IOException;
import java.nio.CharBuffer;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.osee.framework.jdk.core.text.FindResults;
import org.eclipse.osee.framework.jdk.core.text.tool.Find;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.attribute.ConfigurationPersistenceManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkspace;

public class ExtractTestRelations {
   private static final ArtifactPersistenceManager artifactManager = ArtifactPersistenceManager.getInstance();
   private static final ConfigurationPersistenceManager configurationPersistenceManager =
         ConfigurationPersistenceManager.getInstance();
   //   private static final BranchPersistenceManager branchManager = BranchPersistenceManager.getInstance();

   private String scriptsDir;
   private String fileNamePattern;
   private Branch branch;
   private static final Matcher testScriptMatcher = Pattern.compile("doTestCase").matcher("");
   private static final Matcher traceabilityMatcher = Pattern.compile("RequirementId\\(\\\"([^\\\"]+)\\\"").matcher("");

   public ExtractTestRelations(String scriptsDir, String fileNamePattern, Branch branch) throws SQLException {
      super();
      this.scriptsDir = scriptsDir;
      this.fileNamePattern = fileNamePattern;
      this.branch = branch;
   }

   public void run() {
      ArrayList<String> patterns = new ArrayList<String>();
      patterns.add(scriptsDir);
      Find app = new Find("RequirementId\\(\\\"([^\\\"]+)\\\"", new File(scriptsDir), fileNamePattern);
      app.find(999999, true);
      FindResults results = app.getResults();

      for (FindResults.FindResultsIterator i = results.iterator(); i.hasNext();) {
         try {
            addRelationToDatabaseIfNotAlreadyThere(AWorkspace.fileToIFile(i.currentFile), i.currentRegion);
         } catch (SQLException ex) {
            ex.printStackTrace();
         }
      }

   }

   public static void traceabilityReport() throws IOException {
      Pattern javaFilePattern = Pattern.compile(".*\\.java");
      for (IProject project : AWorkspace.getProjects()) {
         File projectLocation = project.getLocation().toFile();
         for (File javaFile : (List<File>) Lib.recursivelyListFiles(projectLocation, javaFilePattern)) {
            CharBuffer buf = Lib.fileToCharBuffer(javaFile);
            testScriptMatcher.reset(buf);
            if (testScriptMatcher.find()) {
               traceabilityMatcher.reset(buf);
               while (traceabilityMatcher.find()) {
                  System.out.println(javaFile.getName() + ", " + traceabilityMatcher.group(1));
               }
            } else {
               System.out.println(javaFile + ": no traceability");
            }
         }
      }
   }

   private void addRelationToDatabaseIfNotAlreadyThere(IFile testArtifactFile, String reqArtifactName) throws SQLException {

      // Make sure that the runtime relation type is available
      Artifact reqArtifact = artifactManager.getArtifactFromTypeName("Software Requirement", reqArtifactName, branch);

      // Make sure we have the reqArtifact
      if (reqArtifact == null) {
         System.out.println("Unable to locate the requirement named:\"" + reqArtifactName + "\"");
         return;
      }

      // TODO replace the null here with the Test_TestSide relationSide enumeration
      reqArtifact.relate(null, getTestArtifact(testArtifactFile, reqArtifact.getBranch()));
      // Get a new test relation with the same tag as the requirement artifact
      //      UserRelation testRelation = relationManager.getUserRelationDescriptor("Test").makeNewUserRelation(reqArtifact.getTag());
      //      IRelationLink link = relationManager.getIRelationLinkDescriptor("Test").makeNewLink();

      // Add the items to the relation
      //      link.setArtifact("Requirement", reqArtifact);
      //      link.setArtifact("Test", getTestArtifact(testArtifactName, reqArtifact.getTag()));
      //      reqArtifact.getLinkManager().addLink(link);
      //      
      //      link.setArtifactA(reqArtifact);
      //      link.setArtifactB(getTestArtifact(testArtifactName, reqArtifact.getTag()));
      //      link.persist();

      //      testRelation.addArtifact("test", getTestArtifact(testArtifactName, reqArtifact.getTag()));
      //      testRelation.addArtifact("requirement", reqArtifact);

      // Save the relation
      //      link.persist();
   }

   private Artifact getTestArtifact(IFile testArtifactFile, Branch branch) throws SQLException {
      Artifact testArtifact =
            artifactManager.getArtifactFromTypeName("Test Script", testArtifactFile.getName(), branch);

      if (testArtifact == null) {
         testArtifact =
               configurationPersistenceManager.getArtifactSubtypeDescriptor("Test Script").makeNewArtifact(branch);
         testArtifact.setSoleAttributeValue("Name", testArtifactFile.getName());
         testArtifact.setSoleAttributeValue("Content URL", testArtifactFile.getFullPath().toString());
         testArtifact.persistAttributes();
      }
      return testArtifact;

   }
}
