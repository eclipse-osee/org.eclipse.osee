/*********************************************************************
 * Copyright (c) 2022 Boeing
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

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;

/**
 * A Shared Publishing Folder is an artifact with children that are selected by an attribute with a specified value and
 * possibly also by artifact type. Implementations of this abstract class encapsulate the search for the applicable
 * children artifacts under the Shared Publishing Folder. Implementations of this abstract class are not intended to be
 * created until after a publish has been started because errors will be written to the {@link PublishingErrorLog} for
 * the publish.
 *
 * @author Loren K. Ashley
 */

public abstract class PublishingSharedArtifactsFolder {

   /**
    * Static factory method to create an instance of the {@link PublishingSharedArtifactsFolder} abstract class that
    * just returns:
    * <ul>
    * <li>an empty {@link List} for shared artifacts,</li>
    * <li>an empty {@link Optional} for the shared publishing folder, and
    * <li>
    * <li><code>false</code> for the predicate.</li>
    * </ul>
    * This method will also add an error to the provided {@link PublishingErrorLog} that indicates the shared folder
    * cannot be found.
    *
    * @param reason a description of why a non-error implementation was not created.
    * @param publishingUtils a reference to a {@link PublishingUtils} object used to perform database and token service
    * queries.
    * @param publishingErrorLog errors are added to the provided {@link PublishingErrorLog}.
    * @param publishingBranchId the branch and view to locate the shared folder on.
    * @param sharedFolderDescription a textual description of the shared folder's purpose for error messages.
    * @param sharedFolderArtifactToken the token (identifier) of the shared folder.
    * @return an implementation of the {@link PublishingSharedArtifactsFolder} abstract class.
    */

   //@formatter:off
   private static PublishingSharedArtifactsFolder
      createErrorImplementation
         (
            String             reason,
            PublishingUtils    publishingUtils,
            PublishingErrorLog publishingErrorLog,
            BranchId           publishingBranchId,
            String             sharedFolderDescription,
            ArtifactToken      sharedFolderArtifactToken
         ) {

      var message = new StringBuilder( 1024 )
                           .append( reason ).append( "\n" )
                           .append( "   Shared Folder Description: " ).append( sharedFolderDescription ).append( "\n" )
                           .append( "   Publishing Branch Id:      " ).append( publishingBranchId.getIdString() ).append( "\n" )
                           .append( "   Publishing View Id:        " ).append( publishingBranchId.getViewId().getIdString() ).append( "\n" )
                           .append( "   Shared Folder Id:          " ).append( sharedFolderArtifactToken.getIdString() ).append( "\n" )
                           .toString();

      publishingErrorLog.error( sharedFolderArtifactToken, message );

      return
         new PublishingSharedArtifactsFolder() {

            @Override
            public boolean sharedPublishingFolderFound() {
               return false;
            }

            @Override
            public Optional<ArtifactReadable> getSharedPublishingFolder() {
               return Optional.empty();
            }

            @Override
            public List<ArtifactReadable> getSharedArtifacts(String attributeValue) {
               return List.of();
            }
      };
   }
   //@formatter:on

   /**
    * Static factory method to create an instance of the {@link PublishingSharedArtifactsFolder} abstract class. When
    * the shared folder can be found or is provided, an implementation that will perform the database query is returned;
    * otherwise, an implementation that just returns an empty list is returned. The query will search for all artifacts
    * under the Shared Publishing Folder with an attribute of the specified type with a specified value. When the shared
    * folder cannot be found, an error is added to the provided {@link PublishingErrorLog}.
    *
    * @param publishingUtils a reference to a {@link PublishingUtils} object used to perform database and token service
    * queries.
    * @param publishingErrorLog errors are added to the provided {@link PublishingErrorLog}.
    * @param publishingBranchId the branch and view to locate the shared folder on.
    * @param sharedFolderDescription a textual description of the shared folder's purpose for error messages.
    * @param sharedFolderArtifactToken the token (identifier) of the shared folder.
    * @param childAttributeTypeId the identifier of the attribute of the child artifacts to be checked for the specified
    * value.
    * @return an implementation of the {@link PublishingSharedArtifactsFolder} abstract class.
    * @throws NullPointerException when any of the input parameters are <code>null</code>.
    */

   //@formatter:off
   public static PublishingSharedArtifactsFolder
      create
         (
            PublishingUtils    publishingUtils,
            PublishingErrorLog publishingErrorLog,
            BranchId           publishingBranchId,
            String             sharedFolderDescription,
            ArtifactToken      sharedFolderArtifactToken,
            AttributeTypeId    childAttributeTypeId
         ) {

      Objects.requireNonNull( publishingUtils,           "PublishingSharedArtifactsFolder::create, parameter \"publishingUtils\" is null."           );
      Objects.requireNonNull( publishingErrorLog,        "PublishingSharedArtifactsFolder::create, parameter \"publishingErrorLog\" is null."        );
      Objects.requireNonNull( publishingBranchId,        "PublishingSharedArtifactsFolder::create, parameter \"publishingBranchId\" is null."        );
      Objects.requireNonNull( sharedFolderDescription,   "PublishingSharedArtifactsFolder::create, parameter \"sharedFolderDescription\" is null."   );
      Objects.requireNonNull( sharedFolderArtifactToken, "PublishingSharedArtifactsFolder::create, parameter \"sharedFolderArtifactToken\" is null." );
      Objects.requireNonNull( childAttributeTypeId,      "PublishingSharedArtifactsFolder::create, parameter \"childAttributeTypeId\" is null."      );

      if( AttributeTypeId.SENTINEL.equals( childAttributeTypeId ) ) {
         return
            PublishingSharedArtifactsFolder.createErrorImplementation
               (
                  "Child Attribute Type Identifier is SENTINEL.",
                  publishingUtils,
                  publishingErrorLog,
                  publishingBranchId,
                  sharedFolderDescription,
                  sharedFolderArtifactToken
               );
      }

      var sharedFolderOptional =
         ( sharedFolderArtifactToken instanceof ArtifactReadable )
              ? Optional.of( (ArtifactReadable) sharedFolderArtifactToken )
              : publishingUtils.getArtifactReadableByIdentifierFilteredForView( publishingBranchId, publishingBranchId.getViewId(), sharedFolderArtifactToken );

      if( sharedFolderOptional.isEmpty() ) {
         return
            PublishingSharedArtifactsFolder.createErrorImplementation
               (
                  "Unable to locate the shared folder.",
                  publishingUtils,
                  publishingErrorLog,
                  publishingBranchId,
                  sharedFolderDescription,
                  sharedFolderArtifactToken
               );
      }

      return
         new PublishingSharedArtifactsFolder() {

            @Override
            public boolean sharedPublishingFolderFound() {
               return true;
            }

            @Override
            public Optional<ArtifactReadable> getSharedPublishingFolder() {
               return Optional.of( sharedFolderOptional.get() );
            }

            @Override
            public List<ArtifactReadable> getSharedArtifacts(String attributeValue) {

               return
                  publishingUtils.getRecursiveChildenArtifactReadablesByAttributeTypeAndAttributeValue
                     (
                        publishingBranchId,
                        publishingBranchId.getViewId(),
                        sharedFolderArtifactToken,
                        childAttributeTypeId,
                        attributeValue
                     )
                     .orElseGet
                        (
                           () ->
                           {
                              var message = new StringBuilder( 1024 )
                                                   .append( "Failed to get artifacts from the shared folder." ).append( "\n" )
                                                   .append( "   Shared Folder Description: " ).append( sharedFolderDescription                      ).append( "\n" )
                                                   .append( "   Publishing Branch Id:      " ).append( publishingBranchId.getIdString()             ).append( "\n" )
                                                   .append( "   Publishing View Id:        " ).append( publishingBranchId.getViewId().getIdString() ).append( "\n" )
                                                   .append( "   Shared Folder Id:          " ).append( sharedFolderArtifactToken.getIdString()      ).append( "\n" )
                                                   .append( "   Search Attribute Type Id:  " ).append( childAttributeTypeId.getIdString()           ).append( "\n" )
                                                   .append( "   Search Attribute Value:    " ).append( attributeValue                               ).append( "\n" );

                              publishingUtils.getLastError().ifPresent
                                 (
                                    ( reason ) -> message
                                                     .append( "   Reason Follows:" ).append( "\n" )
                                                     .append( reason.getMessage() )
                                 );

                              publishingErrorLog.error(sharedFolderArtifactToken,message.toString());
                              return List.of();
                           }
                        );
            }
         };
   }
   //@formatter:on

   /**
    * Static factory method to create an instance of the {@link PublishingSharedArtifactsFolder} abstract class. When
    * the shared folder can be found or is provided, an implementation that will perform the database query is returned;
    * otherwise, an implementation that just returns an empty list is returned. The query will search for all artifacts
    * of a specified type under the Shared Publishing Folder with an attribute of the specified type with a specified
    * value. When the shared folder cannot be found, an error is added to the provided {@link PublishingErrorLog}.
    *
    * @param publishingUtils a reference to a {@link PublishingUtils} object used to perform database and token service
    * queries.
    * @param publishingErrorLog errors are added to the provided {@link PublishingErrorLog}.
    * @param publishingBranchId the branch and view to locate the shared folder on.
    * @param sharedFolderDescription a textual description of the shared folder's purpose for error messages.
    * @param sharedFolderArtifactToken the token (identifier) of the shared folder.
    * @param childArtifactTypeToken only child artifacts of this type will be included in the returned {@link List}.
    * @param childAttributeTypeId the identifier of the attribute of the child artifacts to be checked for the specified
    * value.
    * @return an implementation of the {@link PublishingSharedArtifactsFolder} interface.
    * @throws NullPointerException when any of the input parameters are <code>null</code>.
    */

   //@formatter:off
   public static PublishingSharedArtifactsFolder
      create
         (
            PublishingUtils    publishingUtils,
            PublishingErrorLog publishingErrorLog,
            BranchId           publishingBranchId,
            String             sharedFolderDescription,
            ArtifactToken      sharedFolderArtifactToken,
            ArtifactTypeToken  childArtifactTypeToken,
            AttributeTypeId    childAttributeTypeId
         ) {

      Objects.requireNonNull( publishingUtils,           "PublishingSharedArtifactsFolder::create, parameter \"publishingUtils\" is null."           );
      Objects.requireNonNull( publishingErrorLog,        "PublishingSharedArtifactsFolder::create, parameter \"publishingErrorLog\" is null."        );
      Objects.requireNonNull( publishingBranchId,        "PublishingSharedArtifactsFolder::create, parameter \"publishingBranchId\" is null."        );
      Objects.requireNonNull( sharedFolderDescription,   "PublishingSharedArtifactsFolder::create, parameter \"sharedFolderDescription\" is null."   );
      Objects.requireNonNull( sharedFolderArtifactToken, "PublishingSharedArtifactsFolder::create, parameter \"sharedFolderArtifactToken\" is null." );
      Objects.requireNonNull( childArtifactTypeToken,    "PublishingSharedArtifactsFolder::create, parameter \"childArtifactTypeToken\" is null."    );
      Objects.requireNonNull( childAttributeTypeId,      "PublishingSharedArtifactsFolder::create, parameter \"childAttributeTypeId\" is null."      );

      if( AttributeTypeId.SENTINEL.equals( childAttributeTypeId ) ) {
         return
            PublishingSharedArtifactsFolder.createErrorImplementation
               (
                  "Child Attribute Type Identifier is SENTINEL.",
                  publishingUtils,
                  publishingErrorLog,
                  publishingBranchId,
                  sharedFolderDescription,
                  sharedFolderArtifactToken
               );
      }

      if( ArtifactTypeToken.SENTINEL.equals( childArtifactTypeToken ) ) {
         return
            PublishingSharedArtifactsFolder.createErrorImplementation
               (
                  "Child Artifact Type Token is SENTINEL.",
                  publishingUtils,
                  publishingErrorLog,
                  publishingBranchId,
                  sharedFolderDescription,
                  sharedFolderArtifactToken
               );
      }

      var sharedFolderOptional =
         ( sharedFolderArtifactToken instanceof ArtifactReadable )
              ? Optional.of( (ArtifactReadable) sharedFolderArtifactToken )
              : publishingUtils.getArtifactReadableByIdentifierFilteredForView( publishingBranchId, publishingBranchId.getViewId(), sharedFolderArtifactToken );

      if( sharedFolderOptional.isEmpty() ) {
         return
            PublishingSharedArtifactsFolder.createErrorImplementation
               (
                  "Unable to locate the shared folder.",
                  publishingUtils,
                  publishingErrorLog,
                  publishingBranchId,
                  sharedFolderDescription,
                  sharedFolderArtifactToken
               );
      }

      return
         new PublishingSharedArtifactsFolder() {

            @Override
            public boolean sharedPublishingFolderFound() {
               return true;
            }

            @Override
            public Optional<ArtifactReadable> getSharedPublishingFolder() {
               return Optional.of( sharedFolderOptional.get() );
            }

            @Override
            public List<ArtifactReadable> getSharedArtifacts(String attributeValue) {

               return
                  publishingUtils.getRecursiveChildenArtifactReadablesOfTypeByAttributeTypeAndAttributeValue
                     (
                       publishingBranchId,
                       publishingBranchId.getViewId(),
                       sharedFolderArtifactToken,
                       childArtifactTypeToken,
                       childAttributeTypeId,
                       attributeValue
                     )
                     .orElseGet
                        (
                           () ->
                           {
                              var message = new StringBuilder( 1024 )
                                                   .append( "Failed to get artifacts from the shared folder." ).append( "\n" )
                                                   .append( "   Shared Folder Description: " ).append( sharedFolderDescription                      ).append( "\n" )
                                                   .append( "   Publishing Branch Id:      " ).append( publishingBranchId.getIdString()             ).append( "\n" )
                                                   .append( "   Publishing View Id:        " ).append( publishingBranchId.getViewId().getIdString() ).append( "\n" )
                                                   .append( "   Shared Folder Id:          " ).append( sharedFolderArtifactToken.getIdString()      ).append( "\n" )
                                                   .append( "   Search Artifact Type:      " ).append( childArtifactTypeToken.getName()             ).append( "\n" )
                                                   .append( "   Search Attribute Type Id:  " ).append( childAttributeTypeId.getIdString()           ).append( "\n" )
                                                   .append( "   Search Attribute Value:    " ).append( attributeValue                               ).append( "\n" );

                              publishingUtils.getLastError().ifPresent
                                 (
                                    ( reason ) -> message
                                                     .append( "   Reason Follows:" ).append( "\n" )
                                                     .append( reason.getMessage() )
                                 );

                              publishingErrorLog.error(sharedFolderArtifactToken,message.toString());
                              return List.of();
                           }
                        );
            }
         };
   }
   //@formatter:on

   /**
    * Predicate to determine if the Shared Publishing Folder artifact was found or provided.
    *
    * @return <code>true</code>, when the Shared Publishing Folder artifact was found or provided; otherwise,
    * <code>false</code>.
    */

   public abstract boolean sharedPublishingFolderFound();

   /**
    * Gets the {@link ArtifactReadable} for the Shared Publishing Folder.
    *
    * @return when the shared publishing folder artifact was found or provided, an {@link Optional} containing the
    * {@link ArtifactReadable}; otherwise, an empty {@link Optional}.
    */

   public abstract Optional<ArtifactReadable> getSharedPublishingFolder();

   /**
    * Gets a list of the artifacts under the Shared Publishing Folder that have the specified
    * <code>attributeValue</code> set in the Child Attribute. The search may also be restricted to child artifacts of a
    * specified artifact type. If an error occurs during the search, an error will be added to the
    * {@link PublishingErrorLog} and an empty {@link List} will be returned.
    *
    * @param attributeValue the value to check for in the Child Attribute.
    * @return a list of the artifacts under the Shared Publishing Folder that have the specified
    * <code>attributeValue</code> set in the Child Attribute.
    */

   public abstract List<ArtifactReadable> getSharedArtifacts(String attributeValue);
}

/* EOF */
