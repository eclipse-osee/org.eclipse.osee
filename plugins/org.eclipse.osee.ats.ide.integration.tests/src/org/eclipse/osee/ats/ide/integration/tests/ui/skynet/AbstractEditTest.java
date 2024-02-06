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
import java.util.Objects;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.ats.ide.integration.tests.synchronization.TestUserRules;
import org.eclipse.osee.client.test.framework.ExitDatabaseInitializationRule;
import org.eclipse.osee.client.test.framework.NotProductionDataStoreRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.client.test.framework.TestInfo;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.enums.EventTopicTransferType;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.jdk.core.type.CharSequenceWindow;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Message;
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
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;

/**
 * @author Paul K. Waldfogel
 * @author Megumi Telles
 */
public abstract class AbstractEditTest {

   /**
    * Class level testing rules are applied before the {@link #testSetup} method is invoked. These rules are used for
    * the following:
    * <dl>
    * <dt>Not Production Data Store Rule</dt>
    * <dd>This rule is used to prevent modification of a production database.</dd>
    * <dt>ExitDatabaseInitializationRule</dt>
    * <dd>This rule will exit database initialization mode and re-authenticate as the test user when necessary.</dd>
    * <dt>In Publishing Group Test Rule</dt>
    * <dd>This rule is used to ensure the test user has been added to the OSEE publishing group and the server
    * {@Link UserToken} cache has been flushed.</dd></dt>
    */

   //@formatter:off
   @ClassRule
   public static TestRule classRuleChain =
      RuleChain
         .outerRule( new NotProductionDataStoreRule() )
         .around( new ExitDatabaseInitializationRule() )
         .around( TestUserRules.createInPublishingGroupTestRule() )
         ;
   //@formatter:on

   /**
    * Wrap the test methods with a check to prevent execution on a production database.
    */

   @Rule
   public NotProductionDataStoreRule notProduction = new NotProductionDataStoreRule();

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   /**
    * A rule to get the method name of the currently running test.
    */

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

   private List<Artifact> toList(Artifact artifact) {
      return Objects.nonNull(artifact) ? Collections.singletonList(artifact) : Collections.emptyList();
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
      this.compare(expected, actual);
   }

   /**
    * Asserts that two {@link String}s are equal. The comparison is done in chunks to make isolating where the
    * difference is in large Word ML strings.
    *
    * @param a string to be compared.
    * @param b string to be compared.
    * @throws AssertionError when the {@link String}s <code>a</code> and <code>b</code> are different.
    */

   private void compare(String a, String b) {
      var aSize = a.length();
      var bSize = b.length();
      var size = Math.min(aSize, bSize);
      var chunkSize = 128;
      var chunkCount = size / chunkSize;
      var chunkRemainder = size % chunkSize;

      for (int c = 0; c < chunkCount; c++) {

         var s = c * chunkSize;
         var e = s + chunkSize;

         var chunkA = new CharSequenceWindow(a, s, e);
         var chunkB = new CharSequenceWindow(b, s, e);

         this.compareChunk(c, s, e, chunkA, chunkB);

      }

      if (chunkRemainder > 0) {
         var s = chunkCount * chunkSize;
         var e = s + chunkRemainder;

         var chunkA = new CharSequenceWindow(a, s, e);
         var chunkB = new CharSequenceWindow(b, s, e);

         this.compareChunk(chunkCount + 1, s, e, chunkA, chunkB);
      }

      Assert.assertEquals("Comparison Strings Not Same Size", aSize, bSize);
   }

   /**
    * Compares two {@link CharSequence}s.
    *
    * @param c the chunk index of the chunks being compared.
    * @param s the inclusive starting character index of the chunks.
    * @param e the exclusive ending character index of the chunks.
    * @param a a {@link CharSequence} containing chunk <code>a</code>.
    * @param b a {@link CharSequence} containing chunk <code>b</code>.
    * @throws AssertionError when the {@link CharSequence}s <code>a</code> and <code>b</code> are different.
    */

   private void compareChunk(int c, int s, int e, CharSequence a, CharSequence b) {

      var l = CharSequence.compare(a, b);

      if (l != 0) {
         //@formatter:off
         var message =
            new Message()
                   .title( "Segments are not equal." )
                   .indentInc()
                   .segment( "Chunk", c )
                   .segment( "Start", s )
                   .segment( "End",   e )
                   .segment( "A",     a )
                   .segment( "B",     b )
                   .toString();
         //@formatter:on

         throw new AssertionError(message);
      }

   }

   private IFile openArtifactForEdit(FileSystemRenderer renderer, Artifact artifact) {
      IFile editFile =
         renderer.renderToFile(this.toList(artifact), PresentationType.SPECIALIZED_EDIT, this.method.getTestName());
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
         renderer.renderToFile(this.toList(artifact), PresentationType.SPECIALIZED_EDIT, this.method.getTestName());
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
