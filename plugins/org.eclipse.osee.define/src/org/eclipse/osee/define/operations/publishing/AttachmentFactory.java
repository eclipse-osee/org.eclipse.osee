/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.define.operations.publishing;

import java.io.InputStream;
import java.util.Objects;
import java.util.Optional;
import javax.activation.DataHandler;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.AttachmentBuilder;
import org.apache.cxf.jaxrs.ext.multipart.ContentDisposition;
import org.apache.cxf.jaxrs.ext.multipart.InputStreamDataSource;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.publishing.FilenameFactory;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * A factory class for creating {@link Attachment} objects used to return published documents from the OSEE server to
 * the OSEE client or a Web client.
 *
 * @author Loren K. Ashley
 */

public class AttachmentFactory {

   /**
    * The {@link ContentDisposition} prefix for the attachment filename.
    */

   private static final String ATTACHMENT_TEMPLATE = "attachment; filename=";

   /**
    * A custom header for the rendered branch.
    */

   private static final String BRANCH_HEADER = "OSEE-Branch";

   /**
    * Initial {@link StringBuilder} size.
    */

   private static final int BUFFER_SIZE = 1024;

   /**
    * A default name to use for the attachment when none was specified.
    */

   private static final String FINAL_DEFAULT_NAME = "UNNAMED";

   /**
    * A default filename suffix to used for the attachment when none was specified.
    */

   private static final String FINAL_DEFAULT_SUFFIX = "";

   /**
    * Used to sanitize names for HTTP header values.
    *
    * @param name the name to clean.
    * @return a new {@link String} containing the safe name.
    */

   private static String cleanName(String name) {
      return name.replaceAll("[^a-zA-Z0-9_+-]", "-");
   }

   /**
    * Saves the default attachment name provided by the factory.
    */

   private String defaultName;

   /**
    * Saves a utility class for making database queries with the {@link OrcsApi}.
    */

   private final PublishingUtils publishingUtils;

   /**
    * Saves the file suffix to use for the attachments.
    */

   private String suffix;

   /**
    * Creates a new {@link AttachmentFactory} without the {@link PublishingUtils} and no defaults. Calling the method
    * {@link #create(BranchId, ArtifactId, String, InputStream)} will result in an
    * {@link UnsupportedOperationException}.
    */

   public AttachmentFactory() {
      this.publishingUtils = null;
      this.setDefaultName(null);
      this.setSuffix(null);
   }

   /**
    * Creates a new {@link AttachmentFactory} without the {@link PublishingUtils} and the specified
    * <code>defaultName</code> and <code>defaultSuffix</code>. Calling the method
    * {@link #create(BranchId, ArtifactId, String, InputStream)} will result in an
    * {@link UnsupportedOperationException}.
    *
    * @param defaultName a defaultName to use for the attachment when a filename cannot be generated.
    * @param suffix the filename suffix to append to the generated attachment name.
    */

   public AttachmentFactory(String defaultName, String suffix) {
      this.publishingUtils = null;
      this.setDefaultName(defaultName);
      this.setSuffix(suffix);
   }

   /**
    * Creates a new {@link AttachmentFactory} with the {@link PublishingUtils} and the specified
    * <code>defaultName</code> and <code>defaultSuffix</code>. Calling the method
    * {@link #create(BranchId, ArtifactId, String, InputStream)} will perform the database queries to obtain the
    * specified artifact to generate the attachment name from it.
    *
    * @param defaultName a defaultName to use for the attachment when a filename cannot be generated.
    * @param suffix the filename suffix to append to the generated attachment name.
    */

   public AttachmentFactory(String defaultName, String suffix, OrcsApi orcsApi) {
      this.publishingUtils = new PublishingUtils(
         Objects.requireNonNull(orcsApi, "AttachmentFactory::new, The parameter \"orcsApi\" cannot be null."));
      this.setDefaultName(defaultName);
      this.setSuffix(suffix);
   }

   /**
    * Creates a new {@link Attachment} with the following filename:
    *
    * <pre>
    * &lt;artifact-name&gt; "-" { &lt;segmentN&gt; }{0,N-1} &lt;date-segment&gt; "-" &lt;random-segment&gt; "." &lt;suffix&gt;
    * </pre>
    *
    * The {@link Attachment} identifier is set to <code>id</code>. The custom header {@link #BRANCH_HEADER} is set to
    * the branch name.
    *
    * @param branchToken the {@link BranchToken} for the OSEE Artifact the {@link InputStream} was rendered from.
    * @param artifactToken the {@Link ArtifactToken} for the OSEE Artifact the {@link InputStream} was rendered from.
    * @param id an identifier to assign to the {@link Attachment}.
    * @param inputStream the rendered {@link InputStream} to be sent in the {@link Attachment}.
    * @return the newly created {@link Attachment}.
    */

   public Attachment create(InputStream inputStream, CharSequence id, BranchToken branchToken, ArtifactToken artifactToken, CharSequence... segments) {
      var branchName = Objects.nonNull(branchToken) ? branchToken.getName() : null;
      var artifactName = Objects.nonNull(artifactToken) ? artifactToken.getName() : null;
      var attachment = this.create(inputStream, id, branchName, artifactName, segments);
      return attachment;
   }

   /**
    * Creates a new {@link Attachment} with the following filename:
    *
    * <pre>
    * &lt;artifact-name&gt; "-" { &lt;segmentN&gt; }{0,N-1} &lt;date-segment&gt; "-" &lt;random-segment&gt; "." &lt;suffix&gt;
    * </pre>
    *
    * The {@link Attachment} identifier is set to <code>id</code>. The custom header {@link #BRANCH_HEADER} is set to
    * the branch name.
    *
    * @param branchToken the {@link BranchToken} for the OSEE Artifact the {@link InputStream} was rendered from.
    * @param artifactToken the {@Link ArtifactToken} for the OSEE Artifact the {@link InputStream} was rendered from.
    * @param id an identifier to assign to the {@link Attachment}.
    * @param inputStream the rendered {@link InputStream} to be sent in the {@link Attachment}.
    * @return the newly created {@link Attachment}.
    */

   public Attachment create(InputStream inputStream, CharSequence id, BranchId branchId, ArtifactId artifactId, CharSequence... segments) {

      //@formatter:off
      if(    Objects.isNull( this.publishingUtils )
          && (    ( Objects.nonNull( artifactId ) && artifactId.isValid() )
               || ( Objects.nonNull( branchId   ) && branchId.isValid()   ) ) ) {
         throw
            new UnsupportedOperationException
                   (
                      new Message()
                             .title( "AttachementFactory:create, Attempt to create and \"Attachment\" using a \"branchId\" and/or an \"artifactId\" when the factory was not created with the \"OrcsApi\" service." )
                             .indentInc()
                             .segment( "Branch Identifier",      branchId   )
                             .segment( "Artifact Identifier",    artifactId )
                             .segment( "Attachement Identifier", id         )
                             .toString()
                   );
      }
      //@formatter:on

      String branchName;
      String artifactName;

      //@formatter:off
      synchronized( this.publishingUtils ) {

         branchName =
            Objects.nonNull( branchId ) && branchId.isValid()
               ? this.publishingUtils.getBranchByIdentifier( branchId )
                    .map( Branch::getShortName )
                    .orElse( null )
               : null;

         artifactName =
            Objects.nonNull( artifactId ) && artifactId.isValid()
               ? this.publishingUtils.getArtifactReadableByIdentifierFilteredForView( branchId, branchId.getViewId(), artifactId )
                    .map( ArtifactReadable::getName )
                    .orElse( this.defaultName )
               : this.defaultName;
      }
      //@formatter:on

      var attachment = this.create(inputStream, id, branchName, artifactName, segments);

      return attachment;
   }

   /**
    * Creates a new {@link Attachment} with the following filename:
    *
    * <pre>
    * &lt;artifact-name&gt; "-" { &lt;segmentN&gt; }{0,N-1} &lt;date-segment&gt; "-" &lt;random-segment&gt; "." &lt;suffix&gt;
    * </pre>
    *
    * The {@link Attachment} identifier is set to <code>id</code>. The custom header {@link #BRANCH_HEADER} is set to
    * the branch name.
    *
    * @param branchToken the {@link BranchToken} for the OSEE Artifact the {@link InputStream} was rendered from.
    * @param artifactToken the {@Link ArtifactToken} for the OSEE Artifact the {@link InputStream} was rendered from.
    * @param id an identifier to assign to the {@link Attachment}.
    * @param inputStream the rendered {@link InputStream} to be sent in the {@link Attachment}.
    * @return the newly created {@link Attachment}.
    */

   public Attachment create(InputStream inputStream, CharSequence id, CharSequence branchName, CharSequence filename, CharSequence... segments) {

      var attachmentBuilder = new AttachmentBuilder();

      if (Strings.isValid(id)) {
         attachmentBuilder.id(id.toString());
      }

      if (Objects.nonNull(inputStream)) {
         var dataHandler = new DataHandler(new InputStreamDataSource(inputStream, "application/octet-stream"));
         attachmentBuilder.dataHandler(dataHandler);
      }

      //@formatter:off
      var newSegments =
         ( Strings.isValid( filename ) && Objects.nonNull( segments ) )
            ? this.newSegments( filename,  segments )
            : Strings.isValid( filename )
                 ? new CharSequence[] { filename }
                 : segments;
      //@formatter:on

      this.getContentDisposition(newSegments).ifPresent(attachmentBuilder::contentDisposition);

      this.getBranchHeaderValue(branchName).ifPresent(
         (branchHeaderValue) -> attachmentBuilder.header(AttachmentFactory.BRANCH_HEADER, branchHeaderValue));
      //@formatter:on

      var attachment = attachmentBuilder.build();

      return attachment;
   }

   /**
    * Creates a safe version of the branch name for use in the header content of the {@link Attachment}.
    *
    * @param branchName the name of the branch.
    * @return a safe version of the branch name as a {@link String}.
    */

   private Optional<String> getBranchHeaderValue(CharSequence branchName) {
      //@formatter:off
      return
         Strings.isValid( branchName )
            ? Optional.ofNullable( FilenameFactory.makeNameSafer( branchName ) )
            : Optional.empty();
      //@formatter:on
   }

   /**
    * Creates the prefix and filename string for a {@link ContentDisposition} header for the {@link Attachment}.
    *
    * @param segments the segments to build the filename from.
    * @return when a name is successfully created, an {@link Optional} with the content disposition header string;
    * otherwise, an empty {@link Optional}.
    */

   private Optional<ContentDisposition> getContentDisposition(CharSequence... segments) {

      var cleanFileName = FilenameFactory.create(this.suffix, segments);

      if (Objects.isNull(cleanFileName)) {
         return Optional.empty();
      }

      //@formatter:off
      var fileAttachmentString =
         new StringBuilder( AttachmentFactory.BUFFER_SIZE )
               .append( AttachmentFactory.ATTACHMENT_TEMPLATE )
               .append( cleanFileName )
               .toString()
               ;
      //@formatter:on

      var contentDisposition = new ContentDisposition(fileAttachmentString);

      return Optional.of(contentDisposition);
   }

   /**
    * Creates a new array of segments with the segment specified by <code>first</code> in the first array element, and
    * the segments from <code>rest</code> copied into the remainder of the array.
    *
    * @param first the {@link CharSequence} for the start of the new array.
    * @param rest the remaining {@link CharSequence} objects for the remainder of the new array.
    * @return the new array of segments.
    */

   private CharSequence[] newSegments(CharSequence first, CharSequence... rest) {
      var newSegments = new CharSequence[rest.length + 1];
      newSegments[0] = first;
      System.arraycopy(rest, 0, newSegments, 1, rest.length);
      return newSegments;
   }

   /**
    * Sets a safe version of the <code>defaultName</code> if it is valid; otherwise, the {@link #FINAL_DEFAULT_NAME} is
    * set.
    *
    * @param defaultName the default name for the {@link AttachmentFactory} to use.
    */

   private void setDefaultName(String defaultName) {
      //@formatter:off
      this.defaultName =
         Objects.nonNull( defaultName ) && !defaultName.isBlank()
            ? AttachmentFactory.cleanName( defaultName )
            : AttachmentFactory.FINAL_DEFAULT_NAME;
      //@formatter:on
   }

   /**
    * Sets a safe version of the <code>suffix</code> if it is valid; otherwise, the {@link #FINAL_DEFAULT_SUFFIX} is
    * set.
    *
    * @param suffix the filename suffix for the {@link AttachmentFactory} to use.
    */

   private void setSuffix(String suffix) {
      //@formatter:off
      this.suffix =
         Objects.nonNull( suffix ) && !suffix.isBlank()
            ? AttachmentFactory.cleanName( suffix )
            : AttachmentFactory.FINAL_DEFAULT_SUFFIX;
      //@formatter:on
   }

}

/* EOF */