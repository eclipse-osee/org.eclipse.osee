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
package org.eclipse.osee.framework.skynet.core.test.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchControlled;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.support.test.util.DemoSubsystems;
import org.eclipse.osee.support.test.util.TestUtil;

/**
 * @author Donald G. Dunne
 */
public class FrameworkTestUtil {
   private static final String TEST_PATH_NAME = "../org.eclipse.osee.framework.skynet.core.test/";

   /**
    * Creates a simple artifact and adds it to the root artifact default hierarchical relation
    * 
    * @param artifactTypeName
    * @param name
    * @param branch
    * @throws OseeCoreException
    * @throws Exception
    */
   public static Artifact createSimpleArtifact(String artifactTypeName, String name, Branch branch) throws OseeCoreException {
      Artifact softArt = ArtifactTypeManager.addArtifact(artifactTypeName, branch);
      softArt.setName(name);
      softArt.addAttribute("Subsystem", DemoSubsystems.Electrical.name());
      Artifact rootArtifact = OseeSystemArtifacts.getDefaultHierarchyRootArtifact(branch);
      rootArtifact.addRelation(CoreRelationTypes.Default_Hierarchical__Child, softArt);
      return softArt;
   }

   public static Collection<Artifact> createSimpleArtifacts(String artifactTypeName, int numArts, String name, Branch branch) throws Exception {
      List<Artifact> arts = new ArrayList<Artifact>();
      for (int x = 1; x < numArts + 1; x++) {
         arts.add(createSimpleArtifact(artifactTypeName, name + " " + x, branch));
      }
      return arts;
   }

   public static void purgeBranch(Branch branch) throws Exception {
      try {
         BranchManager.purgeBranch(branch);
         TestUtil.sleep(2000);
      } catch (BranchDoesNotExist ex) {
         // do nothing
      }
   }

   public static void purgeWorkingBranches(Collection<String> branchNamesContain) throws Exception {
      try {
         // delete working branches
         for (Branch workingBranch : BranchManager.getBranches(BranchArchivedState.ALL, BranchControlled.ALL,
               BranchType.WORKING)) {
            for (String branchName : branchNamesContain) {
               if (workingBranch.getName().contains(branchName)) {
                  BranchManager.purgeBranch(workingBranch);
                  TestUtil.sleep(2000);
               }
            }
         }
      } catch (BranchDoesNotExist ex) {
         // do nothing
      }
   }

   public static void purgeArtifacts(Collection<Artifact> artifacts) throws Exception {
      for (Artifact art : artifacts) {
         art.purgeFromBranch();
      }
   }

   /**
    * Deletes all artifacts with names that start with any title given
    * 
    * @param titles
    * @throws Exception
    */
   public static void cleanupSimpleTest(Branch branch, Collection<String> titles) throws Exception {
      List<Artifact> artifacts = new ArrayList<Artifact>();
      for (String title : titles) {
         artifacts.addAll(ArtifactQuery.getArtifactListFromName(title + "%", branch, false));
      }
      purgeArtifacts(artifacts);
      //      ArtifactPersistenceManager.purgeArtifacts(artifacts);
      TestUtil.sleep(4000);
   }

   /**
    * Deletes any artifact with name that starts with title
    * 
    * @param title
    * @throws Exception
    */
   public static void cleanupSimpleTest(Branch branch, String title) throws Exception {
      cleanupSimpleTest(branch, Arrays.asList(title));
   }

   private static List<String> executeCommand(List<String> commands) throws IOException, InterruptedException {
      List<String> resultStrings = new ArrayList<String>();
      ProcessBuilder myProcessBuilder = new ProcessBuilder();
      myProcessBuilder.command(commands);
      Process myProcess = myProcessBuilder.start();
      myProcess.waitFor();
      InputStream myInputStream = myProcess.getInputStream();
      BufferedReader myBufferedReader = new BufferedReader(new InputStreamReader(myInputStream));
      String line;
      while ((line = myBufferedReader.readLine()) != null) {
         resultStrings.add(line);
      }
      return resultStrings;
   }

   public static final List<String> killAllOpenWinword() throws IOException, InterruptedException {
      List<String> commands = new ArrayList<String>();
      commands.add("TASKKILL");
      commands.add("/F");
      commands.add("/IM");
      commands.add("wscript.exe");
      commands.add("/IM");
      commands.add("WINWORD.EXE");
      return executeCommand(commands);
   }

   public static final List<String> findAllWinWordRunning() throws IOException, InterruptedException {
      List<String> commands = new ArrayList<String>();
      commands.add("TASKLIST");
      commands.add("/FI");
      commands.add("Imagename eq WINWORD.EXE");
      return executeCommand(commands);
   }

   public static boolean areWinWordsRunning() throws IOException, InterruptedException {
      return findAllWinWordRunning().size() > 0 ? true : false;
   }

   public File getFileFromPlugin(String filename) throws Exception {
      return new File(TEST_PATH_NAME + "/" + filename);
   }
}
