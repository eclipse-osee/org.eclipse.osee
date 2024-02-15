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

package org.eclipse.osee.define.operations.publisher.publishing;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchSpecification;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.publishing.DataAccessException;
import org.eclipse.osee.framework.core.publishing.FilterForView;
import org.eclipse.osee.framework.core.publishing.IncludeDeleted;
import org.eclipse.osee.framework.core.publishing.ProcessRecursively;
import org.eclipse.osee.framework.core.publishing.PublishingArtifact;
import org.eclipse.osee.framework.core.publishing.PublishingArtifactLoader;
import org.eclipse.osee.framework.core.publishing.PublishingArtifactLoader.BranchIndicator;
import org.eclipse.osee.framework.core.publishing.PublishingArtifactLoader.WhenNotFound;
import org.eclipse.osee.framework.core.publishing.PublishingErrorLog;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Message;

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
    * @param reason a description of why a error implementation was created.
    * @param publishingErrorLog errors are added to the provided {@link PublishingErrorLog}.
    * @param publishingBranchSpecification the branch and view to locate the shared folder on.
    * @param sharedFolderDescription a textual description of the shared folder's purpose for error messages.
    * @param sharedFolderArtifactToken the token (identifier) of the shared folder.
    * @return an implementation of the {@link PublishingSharedArtifactsFolder} abstract class.
    */

   //@formatter:off
   private static PublishingSharedArtifactsFolder
      createErrorImplementation
         (
            String                   reason,
            PublishingErrorLog       publishingErrorLog,
            BranchSpecification      publishingBranchSpecification,
            String                   sharedFolderDescription,
            ArtifactToken            sharedFolderArtifactToken
         ) {

      var message = new Message( )
                           .title( reason )
                           .indentInc()
                           .segment( "Shared Folder Description",       sharedFolderDescription )
                           .segment( "Shared Folder Id",                sharedFolderArtifactToken.getIdString() )
                           .segment( "Publishing Branch Specification", publishingBranchSpecification )
                           .toString();

      publishingErrorLog.error( sharedFolderArtifactToken, message );

      return
         new PublishingSharedArtifactsFolder() {

            @Override
            public boolean sharedPublishingFolderFound() {
               return false;
            }

            @Override
            public Optional<PublishingArtifact> getSharedPublishingFolder() {
               return Optional.empty();
            }

            @Override
            public List<PublishingArtifact> getSharedArtifacts(String attributeValue) {
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
    * @param publishingArtifactLoader a reference to a {@link PublishingArtifactLoader} object used to perform database
    * and token service queries.
    * @param publishingErrorLog errors are added to the provided {@link PublishingErrorLog}.
    * @param publishingBranchSpecification the branch and view to locate the shared folder on.
    * @param sharedFolderDescription a textual description of the shared folder's purpose for error messages.
    * @param sharedFolderArtifactToken the token (identifier) of the shared folder.
    * @param childAttributeTypeId the identifier of the attribute of the child artifacts to be checked for the specified
    * value.
    * @return an implementation of the {@link PublishingSharedArtifactsFolder} abstract class.
    * @throws NullPointerException when any of the input parameters are <code>null</code>.
    * @throws DataAccessException when an error occurs loading the shared folder artifact.
    */

   //@formatter:off
   public static PublishingSharedArtifactsFolder
      create
         (
            PublishingArtifactLoader publishingArtifactLoader,
            PublishingErrorLog       publishingErrorLog,
            BranchSpecification      publishingBranchSpecification,
            String                   sharedFolderDescription,
            ArtifactToken            sharedFolderArtifactToken,
            AttributeTypeToken       childAttributeTypeId,
            ProcessRecursively       processRecursively
         ) {

      Objects.requireNonNull( publishingArtifactLoader,      "PublishingSharedArtifactsFolder::create, parameter \"publishingArtifactLoader\" is null."      );
      Objects.requireNonNull( publishingErrorLog,            "PublishingSharedArtifactsFolder::create, parameter \"publishingErrorLog\" is null."            );
      Objects.requireNonNull( publishingBranchSpecification, "PublishingSharedArtifactsFolder::create, parameter \"publishingBranchSpecification\" is null." );
      Objects.requireNonNull( sharedFolderDescription,       "PublishingSharedArtifactsFolder::create, parameter \"sharedFolderDescription\" is null."       );
      Objects.requireNonNull( sharedFolderArtifactToken,     "PublishingSharedArtifactsFolder::create, parameter \"sharedFolderArtifactToken\" is null."     );
      Objects.requireNonNull( childAttributeTypeId,          "PublishingSharedArtifactsFolder::create, parameter \"childAttributeTypeId\" is null."          );

      if( AttributeTypeId.SENTINEL.equals( childAttributeTypeId ) ) {
         return
            PublishingSharedArtifactsFolder.createErrorImplementation
               (
                  "Child Attribute Type Identifier is SENTINEL.",
                  publishingErrorLog,
                  publishingBranchSpecification,
                  sharedFolderDescription,
                  sharedFolderArtifactToken
               );
      }

      var sharedFolder =
         publishingArtifactLoader
            .getPublishingArtifactByArtifactIdentifier
               (
                  BranchIndicator.PUBLISHING_BRANCH,
                  sharedFolderArtifactToken,
                  FilterForView.YES,
                  WhenNotFound.SENTINEL,
                  TransactionId.SENTINEL,
                  IncludeDeleted.NO
               )
            .orElseThrow
               (
                  ( dataAccessException ) -> new OseeCoreException
                                                    (
                                                       new Message()
                                                              .title( "PublishingSharedArtifactsFolder::create, failed to load shared folder artifact." )
                                                              .indentInc()
                                                              .segment( "Shared Folder Artifact", sharedFolderArtifactToken )
                                                              .reasonFollows( dataAccessException )
                                                              .toString(),
                                                        dataAccessException
                                                    )
               );

      if( sharedFolder.isInvalid() ) {
         return
            PublishingSharedArtifactsFolder.createErrorImplementation
               (
                  "Unable to locate the shared folder.",
                  publishingErrorLog,
                  publishingBranchSpecification,
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
            public Optional<PublishingArtifact> getSharedPublishingFolder() {
               return Optional.of( sharedFolder );
            }

            @Override
            public List<PublishingArtifact> getSharedArtifacts(String attributeValue) {

               return
                  publishingArtifactLoader
                     .getChildrenPublishingArtifacts
                        (
                           sharedFolderArtifactToken,
                           ArtifactTypeToken.SENTINEL,
                           childAttributeTypeId,
                           attributeValue,
                           publishingBranchSpecification.hasView() ? FilterForView.YES : FilterForView.NO,
                           processRecursively
                        )
                     .peekError
                        (
                           ( exception ) -> publishingErrorLog.error
                                               (
                                                  sharedFolderArtifactToken,
                                                  new Message()
                                                         .title( "Failed to get artifacts from the shared folder." )
                                                         .indentInc()
                                                         .segment( "Shared Folder Description",       sharedFolderDescription                 )
                                                         .segment( "Shared Folder Id",                sharedFolderArtifactToken.getIdString() )
                                                         .segment( "Publishing Branch Specification", publishingBranchSpecification           )
                                                         .segment( "Search Attribute Type Id",        childAttributeTypeId.getIdString()      )
                                                         .segment( "Search Attribute Value",          attributeValue                          )
                                                         .reasonFollows( exception )
                                                         .toString()
                                               )
                        )
                     .orElseGet( new LinkedList<PublishingArtifact>() ); /* List must be mutable */
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
    * @param publishingArtifactLoader a reference to a {@link PublishingArtifactLoader} object used to perform database
    * and token service queries.
    * @param publishingErrorLog errors are added to the provided {@link PublishingErrorLog}.
    * @param publishingBranchSpecification the branch and view to locate the shared folder on.
    * @param sharedFolderDescription a textual description of the shared folder's purpose for error messages.
    * @param sharedFolderArtifactToken the token (identifier) of the shared folder.
    * @param childArtifactTypeToken only child artifacts of this type will be included in the returned {@link List}.
    * @param childAttributeTypeId the identifier of the attribute of the child artifacts to be checked for the specified
    * value.
    * @return an implementation of the {@link PublishingSharedArtifactsFolder} interface.
    * @throws NullPointerException when any of the input parameters are <code>null</code>.
    * @throws DataAccessException when an error occurs loading the shared folder artifact.
    */

   //@formatter:off
   public static PublishingSharedArtifactsFolder
      create
         (
            PublishingArtifactLoader publishingArtifactLoader,
            PublishingErrorLog       publishingErrorLog,
            BranchSpecification      publishingBranchSpecification,
            String                   sharedFolderDescription,
            ArtifactToken            sharedFolderArtifactToken,
            ArtifactTypeToken        childArtifactTypeToken,
            AttributeTypeToken       childAttributeTypeId,
            ProcessRecursively       processRecursively
         ) {

      Objects.requireNonNull( publishingArtifactLoader,      "PublishingSharedArtifactsFolder::create, parameter \"publishingArtifactLoader\" is null."      );
      Objects.requireNonNull( publishingErrorLog,            "PublishingSharedArtifactsFolder::create, parameter \"publishingErrorLog\" is null."            );
      Objects.requireNonNull( publishingBranchSpecification, "PublishingSharedArtifactsFolder::create, parameter \"publishingBranchSpecification\" is null." );
      Objects.requireNonNull( sharedFolderDescription,       "PublishingSharedArtifactsFolder::create, parameter \"sharedFolderDescription\" is null."       );
      Objects.requireNonNull( sharedFolderArtifactToken,     "PublishingSharedArtifactsFolder::create, parameter \"sharedFolderArtifactToken\" is null."     );
      Objects.requireNonNull( childArtifactTypeToken,        "PublishingSharedArtifactsFolder::create, parameter \"childArtifactTypeToken\" is null."        );
      Objects.requireNonNull( childAttributeTypeId,          "PublishingSharedArtifactsFolder::create, parameter \"childAttributeTypeId\" is null."          );

      if( AttributeTypeId.SENTINEL.equals( childAttributeTypeId ) ) {
         return
            PublishingSharedArtifactsFolder.createErrorImplementation
               (
                  "Child Attribute Type Identifier is SENTINEL.",
                  publishingErrorLog,
                  publishingBranchSpecification,
                  sharedFolderDescription,
                  sharedFolderArtifactToken
               );
      }

      if( ArtifactTypeToken.SENTINEL.equals( childArtifactTypeToken ) ) {
         return
            PublishingSharedArtifactsFolder.createErrorImplementation
               (
                  "Child Artifact Type Token is SENTINEL.",
                  publishingErrorLog,
                  publishingBranchSpecification,
                  sharedFolderDescription,
                  sharedFolderArtifactToken
               );
      }

      var sharedFolder =
         publishingArtifactLoader
            .getPublishingArtifactByArtifactIdentifier
               (
                  BranchIndicator.PUBLISHING_BRANCH,
                  sharedFolderArtifactToken,
                  FilterForView.YES,
                  WhenNotFound.SENTINEL,
                  TransactionId.SENTINEL,
                  IncludeDeleted.NO
               )
            .orElseThrow
               (
                  ( dataAccessException ) -> new OseeCoreException
                                                    (
                                                       new Message()
                                                              .title( "PublishingSharedArtifactsFolder::create, failed to load shared folder artifact." )
                                                              .indentInc()
                                                              .segment( "Shared Folder Artifact", sharedFolderArtifactToken )
                                                              .reasonFollows( dataAccessException )
                                                              .toString(),
                                                        dataAccessException
                                                    )
               );

      if( sharedFolder.isInvalid() ) {
         return
            PublishingSharedArtifactsFolder.createErrorImplementation
               (
                  "Unable to locate the shared folder.",
                  publishingErrorLog,
                  publishingBranchSpecification,
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
            public Optional<PublishingArtifact> getSharedPublishingFolder() {
               return Optional.of( sharedFolder );
            }

            @Override
            public List<PublishingArtifact> getSharedArtifacts(String attributeValue) {

               return
                  publishingArtifactLoader
                     .getChildrenPublishingArtifacts
                        (
                           sharedFolderArtifactToken,
                           childArtifactTypeToken,
                           childAttributeTypeId,
                           attributeValue,
                           publishingBranchSpecification.hasView() ? FilterForView.YES : FilterForView.NO,
                           processRecursively
                        )
                     .peekError
                        (
                           ( exception ) -> publishingErrorLog.error
                                               (
                                                  sharedFolderArtifactToken,
                                                  new Message()
                                                         .title( "Failed to get artifacts from the shared folder." )
                                                         .segment( "Shared Folder Description",       sharedFolderDescription                 )
                                                         .segment( "Shared Folder Id",                sharedFolderArtifactToken.getIdString() )
                                                         .segment( "Publishing Branch Specification", publishingBranchSpecification           )
                                                         .segment( "Search Artifact Type",            childArtifactTypeToken.getName()        )
                                                         .segment( "Search Attribute Type Id",        childAttributeTypeId.getIdString()      )
                                                         .segment( "Search Attribute Value",          attributeValue                          )
                                                         .reasonFollows( exception )
                                                         .toString()
                                               )
                        )
                     .orElseGet( new LinkedList<PublishingArtifact>() ); /* List must be mutable */
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

   public abstract Optional<PublishingArtifact> getSharedPublishingFolder();

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

   public abstract List<PublishingArtifact> getSharedArtifacts(String attributeValue);
}

/* EOF */
