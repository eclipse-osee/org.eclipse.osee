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

package org.eclipse.osee.define.operations.publisher.dataaccess;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchSpecification;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.publishing.Cause;
import org.eclipse.osee.framework.core.publishing.DataAccessException;
import org.eclipse.osee.framework.core.publishing.DataAccessOperations;
import org.eclipse.osee.framework.core.publishing.IncludeDeleted;
import org.eclipse.osee.framework.jdk.core.type.Result;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.search.QueryFactory;

/**
 * An implementation of the {@link DataAccessOperations} interface for client side publishing.
 * <p>
 * The {@link ArtifactReadable} implementations returned by this class are implemented with
 * {@link ArtifactReadOnlyImpl}.
 *
 * @author Loren K. Ashley
 * @implNote This class is intended to only be used by the Define bundle and specialized publishing bundles that inherit
 * from publishing classes in the Define bundle.
 */

public class DataAccessOperationsImplArtifactReadOnlyImpl extends DataAccessOperationsBase {

   /**
    * Saves the single instance of the {@link DataAccessOperationsImplArtifactReadOnlyImpl} class.
    */

   private static DataAccessOperationsImplArtifactReadOnlyImpl dataAccessOperationsImplArtifactReadOnlyImpl = null;

   /**
    * Gets or creates the single instance of the {@link DataAccessOperationsImplArtifactReadOnlyImpl} class.
    *
    * @param orcsApi the {@link OrcsApi} handle.
    * @return the single {@link DataAccessOperationsImplArtifactReadOnlyImpl} class.
    * @throws NullPointerException when <code>orcsApi</code> is <code>null</code>.
    */

   public synchronized static DataAccessOperationsImplArtifactReadOnlyImpl create(OrcsApi orcsApi) {
      //@formatter:off
      return
         Objects.isNull( DataAccessOperationsImplArtifactReadOnlyImpl.dataAccessOperationsImplArtifactReadOnlyImpl )
            ? DataAccessOperationsImplArtifactReadOnlyImpl.dataAccessOperationsImplArtifactReadOnlyImpl =  new DataAccessOperationsImplArtifactReadOnlyImpl( Objects.requireNonNull( orcsApi ) )
            : DataAccessOperationsImplArtifactReadOnlyImpl.dataAccessOperationsImplArtifactReadOnlyImpl;
      //@formatter:on
   }

   /**
    * Nulls the static reference to the single instance of the {@link DataAccessOperationsImplArtifactReadOnlyImpl}
    * class.
    */

   public synchronized static void free() {
      DataAccessOperationsImplArtifactReadOnlyImpl.dataAccessOperationsImplArtifactReadOnlyImpl = null;
   }

   /**
    * Creates a new {@link DataAccessOperationsImplArtifactReadOnlyImpl} object associated with the provided
    * {@link OrcsApi}.
    *
    * @param orcsApi a non-<code>null</code> reference to the ORCS API.
    * @throws NullPointerException when the provided {@link OrcsApi} is <code>null</code>; or the {@link QueryFactory}
    * or the {@link OrcsTokenService} obtained from the {@link OrcsApi} is <code>null</code>.
    */

   private DataAccessOperationsImplArtifactReadOnlyImpl(OrcsApi orcsApi) {
      super(orcsApi);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Result<List<ArtifactReadable>, DataAccessException> getArtifactReadables(
      BranchSpecification branchSpecification, Collection<ArtifactId> artifactIdentifiers, Collection<String> guids,
      String artifactName, ArtifactTypeToken artifactTypeToken, TransactionId transactionId,
      IncludeDeleted includeDeleted) {

      //@formatter:off
      try {

         var query = this.getBranchQuery( branchSpecification );

         if( Objects.nonNull( artifactIdentifiers ) && !artifactIdentifiers.isEmpty() ) {
            query = query.andIds( artifactIdentifiers );
         }

         if( Objects.nonNull( guids ) && !guids.isEmpty() ) {
            query = query.andGuids( guids );
         }

         if( includeDeleted.yes() ) {
            query =
               query
                  .includeDeletedArtifacts()
                  .includeDeletedAttributes();
         }

         if( Strings.isValidAndNonBlank( artifactName ) ) {
            query = query.andNameEquals( artifactName );
         }

         if( artifactTypeToken.isValid() ) {
            query = query.andIsOfType( artifactTypeToken );
         }

         if( transactionId.isValid() ) {
            query =
               query
                  .fromTransaction(transactionId);
         }

         var result =
            Result.<List<ArtifactReadable>, DataAccessException>ofValue
               (
                  query.getResults().getList()
               );

         return result;
      } catch (Exception e) {
         return
            Result.ofError
               (
                  new DataAccessException
                         (
                            new Object() {}.getClass().getEnclosingMethod().getName(),
                            Cause.ERROR,
                            e
                         )
               );
      }
      //@formatter:on
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Result<List<ArtifactReadable>, DataAccessException> getArtifactReadablesListOfParents(
      BranchSpecification branchSpecification, ArtifactId childArtifactId) {

      //@formatter:off
      try {
         var result =
            Result.<List<ArtifactReadable>, DataAccessException>ofValue
               (
                  this
                     .getBranchQuery( branchSpecification )
                     .andRelatedTo(CoreRelationTypes.DefaultHierarchical_Child, childArtifactId)
                     .getResults().getList()
               );
         return result;
      } catch (Exception e) {
         return
            Result.ofError
               (
                  new DataAccessException
                         (
                            new Object() {}.getClass().getEnclosingMethod().getName(),
                            Cause.ERROR,
                            e
                         )
               );
      }
      //@formatter:on
   }

}

/* EOF */
