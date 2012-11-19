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

package org.eclipse.osee.client.integration.tests.integration.ui.skynet;

import static org.eclipse.osee.framework.core.enums.DeletionFlag.EXCLUDE_DELETED;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.PurgeArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.filter.BranchGuidEventFilter;
import org.eclipse.osee.framework.skynet.core.event.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.event.listener.IArtifactEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.EventBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event.model.EventModType;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.ui.skynet.render.FileSystemRenderer;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.skynet.render.RenderingUtil;
import org.eclipse.osee.framework.ui.skynet.render.WordTemplateRenderer;
import org.eclipse.osee.support.test.util.DemoSawBuilds;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Paul K. Waldfogel
 * @author Megumi Telles
 */
public class WordEditTest {

   private static final String TEST_WORD_EDIT_FILE_NAME = "support/WordEditTest.xml";

   private static final IOseeBranch branch = DemoSawBuilds.SAW_Bld_1;
   private static final String ARTIFACT_NAME_1 = WordEditTest.class.getSimpleName() + ".Edit1";
   private static final String ARTIFACT_NAME_2 = WordEditTest.class.getSimpleName() + ".Edit2";

   /**
    * This test Word Edit's are being saved.
    * 
    * @throws OseeCoreException
    */
   @Before
   public void setUp() throws Exception {
      Assert.assertFalse("Not to be run on production database.", TestUtil.isProductionDb());
      RenderingUtil.setPopupsAllowed(false);
      tearDown();
   }

   @After
   public void tearDown() throws Exception {
      cleanupSimpleTest(branch, ARTIFACT_NAME_1, ARTIFACT_NAME_2);
   }

   private static void cleanupSimpleTest(IOseeBranch branch, String... titles) throws Exception {
      List<Artifact> artifacts = new ArrayList<Artifact>();
      for (String title : titles) {
         artifacts.addAll(ArtifactQuery.getArtifactListFromName(title + "%", branch, EXCLUDE_DELETED));
      }
      if (artifacts.size() > 0) {
         Operations.executeWorkAndCheckStatus(new PurgeArtifacts(artifacts));
      }
   }

   @Test
   public void testEditUsingWordTemplateRender() throws Exception {
      SevereLoggingMonitor monitorLog = TestUtil.severeLoggingStart();
      Artifact artifact = createArtifact(branch, ARTIFACT_NAME_1);
      artifact.persist(getClass().getSimpleName());

      String testData = Lib.fileToString(getClass(), TEST_WORD_EDIT_FILE_NAME);
      Assert.assertNotNull(testData);
      String expected = replaceGuidMarkers(testData, artifact.getGuid());

      FileSystemRenderer renderer = new WordTemplateRenderer();

      IFile editFile = openArtifactForEdit(renderer, artifact);

      writeNewContentAndWaitForSave(artifact, editFile, expected);

      String actual = getRenderedStoredContent(renderer, artifact);
      Assert.assertEquals(expected, actual);
      TestUtil.severeLoggingEnd(monitorLog);
   }

   private static String replaceGuidMarkers(String data, String guid) {
      String expected = data.replaceAll("###ART_GUID###", guid);
      expected = expected.replaceAll("###TAG_GUID_START###", guid + "_START.jpg");
      expected = expected.replaceAll("###TAG_GUID_END###", guid + "_END.jpg");
      return expected;
   }

   private static IFile openArtifactForEdit(FileSystemRenderer renderer, Artifact artifact) throws OseeCoreException {
      IFile editFile = renderer.renderToFile(artifact, artifact.getBranch(), PresentationType.SPECIALIZED_EDIT);
      Assert.assertNotNull(editFile);
      return editFile;
   }

   private static void writeNewContentAndWaitForSave(Artifact artifact, IFile editFile, String content) throws UnsupportedEncodingException, CoreException, InterruptedException {
      boolean eventBoolean = OseeEventManager.isDisableEvents();
      UpdateArtifactListener listener = new UpdateArtifactListener(EventModType.Modified, artifact);
      OseeEventManager.addListener(listener);
      OseeEventManager.setDisableEvents(false);
      try {
         synchronized (listener) {
            editFile.setContents(new ByteArrayInputStream(content.getBytes("UTF-8")), IResource.FORCE,
               new NullProgressMonitor());
            listener.wait(60000);
         }
      } finally {
         OseeEventManager.setDisableEvents(eventBoolean);
         OseeEventManager.removeListener(listener);
      }
   }

   private static String getRenderedStoredContent(FileSystemRenderer renderer, Artifact artifact) throws CoreException, IOException {
      Assert.assertNotNull(renderer);
      Assert.assertNotNull(artifact);

      IFile renderedFileFromModifiedStorage =
         renderer.renderToFile(artifact, artifact.getBranch(), PresentationType.SPECIALIZED_EDIT);
      Assert.assertNotNull(renderedFileFromModifiedStorage);
      InputStream inputStream = null;
      try {
         inputStream = renderedFileFromModifiedStorage.getContents();
         return Lib.inputStreamToString(inputStream);
      } finally {
         Lib.close(inputStream);
      }
   }

   private static Artifact createArtifact(IOseeBranch branch, String artifactName) throws OseeCoreException {
      Assert.assertNotNull(branch);
      Assert.assertNotNull(artifactName);
      Artifact artifact = ArtifactTypeManager.addArtifact(CoreArtifactTypes.SoftwareRequirement, branch, artifactName);
      Assert.assertNotNull(artifact);
      return artifact;
   }

   private static final class UpdateArtifactListener implements IArtifactEventListener {
      private final EventBasicGuidArtifact artToLookFor;
      private volatile boolean wasUpdateReceived;

      public UpdateArtifactListener(EventModType modType, Artifact artifact) {
         this.artToLookFor = new EventBasicGuidArtifact(modType, artifact);
      }

      @Override
      public void handleArtifactEvent(ArtifactEvent artifactEvent, Sender sender) {
         List<EventBasicGuidArtifact> changes = artifactEvent.getArtifacts();

         if (changes.contains(artToLookFor)) {
            synchronized (this) {
               wasUpdateReceived = true;
               notify();
            }
         }
      }

      public synchronized boolean wasUpdateReceived() {
         return wasUpdateReceived;
      }

      @Override
      public List<? extends IEventFilter> getEventFilters() {
         return Collections.singletonList(new BranchGuidEventFilter(branch));
      }
   };

   private static class DoneListeningMonitor implements IProgressMonitor {

      private boolean finished;

      public DoneListeningMonitor() {
         finished = false;
      }

      public boolean finished() {
         return finished;
      }

      @Override
      public void beginTask(String name, int totalWork) {
         // TODO Auto-generated method stub

      }

      @Override
      public void done() {
         finished = true;
      }

      @Override
      public void internalWorked(double work) {
         // TODO Auto-generated method stub

      }

      @Override
      public boolean isCanceled() {
         // TODO Auto-generated method stub
         return false;
      }

      @Override
      public void setCanceled(boolean value) {
         // TODO Auto-generated method stub

      }

      @Override
      public void setTaskName(String name) {
         // TODO Auto-generated method stub

      }

      @Override
      public void subTask(String name) {
         // TODO Auto-generated method stub

      }

      @Override
      public void worked(int work) {
         // TODO Auto-generated method stub

      }

   }
}
