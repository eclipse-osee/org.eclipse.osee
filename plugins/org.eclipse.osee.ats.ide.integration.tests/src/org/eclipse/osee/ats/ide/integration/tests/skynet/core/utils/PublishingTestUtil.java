/*********************************************************************
 * Copyright (c) 2024 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils;

import java.io.File;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.osee.framework.core.client.OseeClient;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.publishing.RendererOption;
import org.eclipse.osee.framework.core.util.OsgiUtil;
import org.eclipse.osee.framework.core.xml.publishing.PublishingXmlUtils;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.ui.skynet.render.IRenderer;
import org.junit.Assert;
import org.w3c.dom.Document;

/**
 * Class of static utility methods for publishing tests.
 *
 * @author Loren K. Ashley
 */

public class PublishingTestUtil {

   /**
    * Build a new {@link AssertionError} with error message for a {@link PublishingXmlUtils} method failure.
    *
    * @param publishingXmlUtils reference to the {@link PublishingXmlUtils} object that contains the failure.
    * @param errorStatement description of the error
    * @param documentString {@link String} representation of the XML document being processed with the error occured.
    * @return the new {@link AssertionError} object.
    */

   public static AssertionError buildAssertionError(PublishingXmlUtils publishingXmlUtils, String errorStatement,
      String documentString) {

      var error = publishingXmlUtils.getLastError();

      var cause = publishingXmlUtils.getLastCause();

      //@formatter:off
      var message =
         new Message()
                .title( errorStatement )
                .indentInc()
                .segment( "Cause", cause )
                .reasonFollowsIfPresent( error )
                .follows( "XML Follows", documentString );
      //@formatter:on

      //@formatter:off
      return
         error.isPresent()
            ? new AssertionError( message.toString(), error.get() )
            : new AssertionError( message.toString() );
      //@formatter:on
   }

   /**
    * Purges the branch specified by {@link BranchId}.
    *
    * @param branchId the identifier of the branch to purge.
    */

   public static void cleanUpBranch(@Nullable BranchId branchId) {

      if ((branchId == null) || !BranchManager.branchExists(branchId)) {
         return;
      }

      BranchManager.purgeBranch(branchId);
   }

   /**
    * Purges all branches with the specified <code>branchName</code>.
    *
    * @param branchName all branches with this name will be purged.
    * @throws AssertionError when unable to obtain the {@link OseeClient}.
    */

   public static void cleanUpBranches(@Nullable String branchName) {

      if (Strings.isInvalidOrBlank(branchName)) {
         return;
      }

      final var oseeClient = OsgiUtil.getService(PublishingTestUtil.class, OseeClient.class);

      Assert.assertNotNull("PublishingTestUtil::cleanUpBranches, Failed to get OSEE Client.", oseeClient);

      final var branchEndpoint = oseeClient.getBranchEndpoint();

      //@formatter:off
      for (var branch  = TestUtil.getBranchByName(branchEndpoint, branchName).orElse(null);
               branch != null;
               branch  = TestUtil.getBranchByName(branchEndpoint, branchName).orElse(null) ) {

         BranchManager.purgeBranch(branch);
      }
      //@formatter:on

   }

   /**
    * Clears the template manager cache.
    *
    * @throws AssertionError when unable to obtain the {@link OseeClient}.
    */

   public static void clearTemplateManagerCache() {

      final var oseeClient = OsgiUtil.getService(PublishingTestUtil.class, OseeClient.class);

      Assert.assertNotNull("PublishingTestUtil::clearTemplateManagerCache, Failed to get OSEE Client.", oseeClient);

      final var templateManagerEndpoint = Conditions.requireNonNull(oseeClient.getTemplateManagerEndpoint());

      templateManagerEndpoint.deleteCache();

   }

   /**
    * Reads the XML content from the {@link Attachment} and parses it to a {@link Document}.
    *
    * @param attachment the {@link Attachment} to read the XML content from.
    * @param testName the name of the test being run. Used for error messages.
    * @return a {@link Document} representing the parsed XML.
    * @throws NullPointerException when <code>attachment</code> is <code>null</code>.
    * @throws AssertionError when unable to read the {@link Attachment} or the XML fails to parse.
    */

   public static @NonNull Document loadContent(@NonNull Attachment attachment, @Nullable String testName) {

      Conditions.requireNonNull(attachment);

      String content;

      try (final var inputStream = attachment.getDataHandler().getInputStream()) {

         content = new String(inputStream.readAllBytes());

      } catch (Exception e) {
         //@formatter:off
         throw
            new AssertionError
                   (
                      new Message()
                             .title( "PublishingTestUtil::loadContent, Failed to get input stream from attachment." )
                             .indentInc()
                             .segment( "Test Name", testName )
                             .reasonFollows( e )
                             .toString()
                   );
         //@formatter:on
      }

      //@formatter:off
      final var document =
         PublishingXmlUtils
            .parse( content )
            .orElseThrow
            (
              ( throwable ) ->
              {
                 var message =
                    new Message()
                           .title( "PublishingTestUtil::loadContent, Failed to parse results XML content." )
                           .indentInc()
                           .segment( "Test Name", testName )
                           .reasonFollows( throwable )
                           .follows( "Document", content )
                           .toString();

                 return new AssertionError( message.toString() );
              }
            );
      //@formatter:on

      return document;

   }

   /**
    * Reads and parses the XML file contained in the {@link IRenderer} result file.
    *
    * @param renderer the {@link IRenderer} implementation to get the result file path from.
    * @param testName the name of the test being run. Used for error messages.
    * @return a {@link Document} representing the XML parsed from the <code>renderer</code> result file.
    * @throws NullPointerException when <code>renderer</code> is <code>null</code>.
    * @throws AssertionError when the renderer did not supply a results path or the XML fails to parse.
    */

   public static @NonNull Document loadContent(@NonNull IRenderer renderer, @Nullable String testName) {

      Conditions.requireNonNull(renderer, "renderer");

      if (!renderer.isRendererOptionSet(RendererOption.RESULT_PATH_RETURN)) {

         //@formatter:off
         var message =
            new Message()
                   .title( "Renderer did not supply a results path." )
                   .indentInc()
                   .segment( "Test Name", testName )
                   .toString();
         //@formatter:on

         Assert.assertTrue(message, false);
      }

      final var contentPath = (String) renderer.getRendererOptionValue(RendererOption.RESULT_PATH_RETURN);

      final var document = PublishingTestUtil.loadContent(contentPath, testName);

      return document;
   }

   /**
    * Reads and parses the XML contained in the file.
    *
    * @param contentPath the path to the file to be parsed.
    * @param testName the name of the test being run. Used for error messages.
    * @return a {@link Document} representing the XML parsed from the file.
    * @throws NullPointerException when <code>contentPath</code> is <code>null</code>.
    * @throws AssertionError when the XML fails to parse.
    */

   public static Document loadContent(String contentPath, String testName) {

      Conditions.requireNonNull(contentPath, "contentPath");

      final var file = new File(contentPath);

      //@formatter:off
      final var document =
         PublishingXmlUtils
            .parse( file )
            .orElseThrow
               (
                 ( throwable ) ->
                 {
                    var message =
                       new Message()
                              .title( "PublishingTestUtil::loadContent, Failed to parse results XML content." )
                              .indentInc()
                              .segment( "File", contentPath )
                              .reasonFollows( throwable )
                              .toString();

                    return new AssertionError( message.toString() );
                 }
               );
      //@formatter:on

      return document;
   }

   /**
    * Pretty prints the <code>document</code> as an XML string.
    *
    * @param document the {@link Document} to be printed.
    * @param testName the name of the test being run. Used for error messages.
    * @param printDocuments when <code>true</code> the generated XML string will be written to {@link System#out}.
    * @return the XML string.
    */
   public static String prettyPrint(Document document, String testName, boolean printDocuments) {

      final var publishingXmlUtils = new PublishingXmlUtils();

      //@formatter:off
      final var documentString =
         publishingXmlUtils.prettyPrint( document )
            .orElseThrow
               (
                  () ->
                  {
                     final var error = publishingXmlUtils.getLastError();

                     var message =
                        new Message()
                               .title( "PublishingTestUtil::prettyPrint, Failed to pretty print XML content." )
                               .indentInc()
                               .segment( "Cause", publishingXmlUtils.getLastCause() )
                               .reasonFollowsIfPresent( error )
                               .toString();

                     return
                        error.isPresent()
                           ? new AssertionError( message.toString(), error.get() )
                           : new AssertionError( message.toString() );
                  }
               );
      //@formatter:on

      if (printDocuments) {
         System.out.println("=============================================================");
         System.out.println("Test Name: " + testName);
         System.out.println("-------------------------------------------------------------");
         System.out.println(documentString);
         System.out.println("=============================================================");
      }

      return documentString;

   }

   /**
    * Reads the content from the {@link Attachment} and parses it to a {@link String}.
    *
    * @param attachment the {@link Attachment} to read the content from.
    * @param testName the name of the test being run. Used for error messages.
    * @return a {@link String} representing the document.
    * @throws NullPointerException when <code>attachment</code> is <code>null</code>.
    * @throws AssertionError when unable to read the {@link Attachment}.
    */

   public static @NonNull String loadContentFromMarkdown(@NonNull Attachment attachment, @Nullable String testName) {

      Conditions.requireNonNull(attachment);

      String content;

      try (final var inputStream = attachment.getDataHandler().getInputStream()) {

         content = new String(inputStream.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);

      } catch (Exception e) {
         //@formatter:off
         throw
            new AssertionError
                   (
                      new Message()
                             .title( "PublishingTestUtil::loadContentFromMarkdown, Failed to get input stream from attachment." )
                             .indentInc()
                             .segment( "Test Name", testName )
                             .reasonFollows( e )
                             .toString()
                   );
         //@formatter:on
      }

      return content;

   }

}

/* EOF */
