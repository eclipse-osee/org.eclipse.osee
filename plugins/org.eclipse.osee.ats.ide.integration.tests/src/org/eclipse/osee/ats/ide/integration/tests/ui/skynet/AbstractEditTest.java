/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide.integration.tests.ui.skynet;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.client.test.framework.NotProductionDataStoreRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.client.test.framework.TestInfo;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.enums.EventTopicTransferType;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.event.EventSystemPreferences;
import org.eclipse.osee.framework.skynet.core.event.FrameworkEventUtil;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.filter.BranchIdEventFilter;
import org.eclipse.osee.framework.skynet.core.event.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.event.listener.IArtifactEventListener;
import org.eclipse.osee.framework.skynet.core.event.listener.IArtifactTopicEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactTopicEvent;
import org.eclipse.osee.framework.skynet.core.event.model.EventBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event.model.EventModType;
import org.eclipse.osee.framework.skynet.core.event.model.EventTopicArtifactTransfer;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.skynet.core.topic.event.filter.BranchIdTopicEventFilter;
import org.eclipse.osee.framework.skynet.core.topic.event.filter.ITopicEventFilter;
import org.eclipse.osee.framework.ui.skynet.render.FileSystemRenderer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Paul K. Waldfogel
 * @author Megumi Telles
 */
public abstract class AbstractEditTest {

   @Rule
   public NotProductionDataStoreRule notProduction = new NotProductionDataStoreRule();

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   @Rule
   public TestInfo method = new TestInfo();

   private final String editFile;
   private final BranchToken branch;
   private final ArtifactTypeToken artType;
   private final FileSystemRenderer renderer;

   private Artifact artifact;

   protected AbstractEditTest(BranchToken branch, String editFile, ArtifactTypeToken artType, FileSystemRenderer renderer) {
      this.branch = branch;
      this.editFile = editFile;
      this.artType = artType;
      this.renderer = renderer;
   }

   @Before
   public void setUp() throws Exception {
      String artifactName = method.getQualifiedTestName() + ".Edit1";

      artifact = createArtifact(branch, artType, artifactName);
      artifact.persist(method.getQualifiedTestName());
   }

   @After
   public void tearDown() throws Exception {
      if (artifact != null) {
         artifact.purgeFromBranch();
      }
   }

   private Artifact createArtifact(BranchToken branch, ArtifactTypeToken artType, String artifactName) {
      Assert.assertNotNull(branch);
      Assert.assertNotNull(artifactName);
      Artifact artifact = ArtifactTypeManager.addArtifact(artType, branch, artifactName);
      Assert.assertNotNull(artifact);
      return artifact;
   }

   protected Artifact getArtifact() {
      return artifact;
   }

   protected abstract String updateDataForTest(String testData);

   @Test
   public void testEditUsingWordTemplateRender() throws Exception {
      String testData = Lib.fileToString(getClass(), editFile);
      Assert.assertNotNull(testData);

      String expected = updateDataForTest(testData);

      IFile renderFile = openArtifactForEdit(renderer, artifact);
      writeNewContentAndWaitForSave(artifact, renderFile, expected);
      String actual = getRenderedStoredContent(renderer, artifact);
      Assert.assertEquals(expected, actual);
   }

   private IFile openArtifactForEdit(FileSystemRenderer renderer, Artifact artifact) {
      IFile editFile = renderer.renderToFile(artifact, artifact.getBranchToken(), PresentationType.SPECIALIZED_EDIT);
      Assert.assertNotNull(editFile);
      return editFile;
   }

   private void writeNewContentAndWaitForSave(Artifact artifact, IFile editFile, String content) throws UnsupportedEncodingException, CoreException, InterruptedException {
      EventSystemPreferences preferences = OseeEventManager.getPreferences();

      boolean eventBoolean = preferences.isDisableEvents();
      UpdateArtifactListener listener = new UpdateArtifactListener(EventModType.Modified, artifact);

      try {
         preferences.setDisableEvents(false);
         Thread.sleep(6000);
         OseeEventManager.addListener(listener);

         NullProgressMonitor monitor = new NullProgressMonitor();
         editFile.setContents(new ByteArrayInputStream(content.getBytes("UTF-8")), IResource.FORCE, monitor);
         editFile.refreshLocal(IResource.DEPTH_ZERO, monitor);
         if (!listener.wasUpdateReceived()) {
            synchronized (listener) {
               listener.wait(60000);
            }
         }
      } finally {
         preferences.setDisableEvents(eventBoolean);
         OseeEventManager.removeListener(listener);
      }
      Assert.assertTrue(listener.wasUpdateReceived());
   }

   private String getRenderedStoredContent(FileSystemRenderer renderer, Artifact artifact) throws Exception {
      Assert.assertNotNull(renderer);
      Assert.assertNotNull(artifact);

      IFile renderedFileFromModifiedStorage =
         renderer.renderToFile(artifact, artifact.getBranchToken(), PresentationType.SPECIALIZED_EDIT);
      Assert.assertNotNull(renderedFileFromModifiedStorage);
      InputStream inputStream = null;
      try {
         inputStream = renderedFileFromModifiedStorage.getContents();
         return Lib.inputStreamToString(inputStream);
      } finally {
         Lib.close(inputStream);
      }
   }

   private static final class UpdateArtifactListener implements IArtifactEventListener, IArtifactTopicEventListener {
      private final EventBasicGuidArtifact artToLookFor;
      private final EventTopicArtifactTransfer topicArtToLookFor;
      private final BranchIdEventFilter branchFilter;
      private final BranchIdTopicEventFilter branchTopicFilter;
      private boolean wasUpdateReceived;

      public UpdateArtifactListener(EventModType modType, Artifact artifact) {
         branchFilter = new BranchIdEventFilter(artifact.getBranch());
         branchTopicFilter = new BranchIdTopicEventFilter(artifact.getBranch());
         artToLookFor = new EventBasicGuidArtifact(modType, artifact);
         topicArtToLookFor = FrameworkEventUtil.artifactTransferFactory(artifact.getBranch(), artifact.getToken(),
            artifact.getArtifactType(), modType, null, null, EventTopicTransferType.BASE);
         wasUpdateReceived = false;
      }

      @Override
      public void handleArtifactEvent(ArtifactEvent artifactEvent, Sender sender) {
         List<EventBasicGuidArtifact> changes = artifactEvent.getArtifacts();
         if (changes.contains(artToLookFor)) {
            wasUpdateReceived = true;
            synchronized (this) {
               notify();
            }
         }
      }

      @Override
      public void handleArtifactTopicEvent(ArtifactTopicEvent artifactTopicEvent, Sender sender) {
         List<EventTopicArtifactTransfer> changes = artifactTopicEvent.getArtifacts();
         for (EventTopicArtifactTransfer transferArt : changes) {
            // just make sure the artifact was sent, by checking for an artifactId match
            if (transferArt.getArtifactToken().equals(topicArtToLookFor.getArtifactToken())) {
               wasUpdateReceived = true;
               synchronized (this) {
                  notify();
               }
            }
         }
      }

      public boolean wasUpdateReceived() {
         return wasUpdateReceived;
      }

      @Override
      public List<? extends IEventFilter> getEventFilters() {
         return Collections.singletonList(branchFilter);
      }

      @Override
      public List<? extends ITopicEventFilter> getTopicEventFilters() {
         return Collections.singletonList(branchTopicFilter);
      }
   };
}
