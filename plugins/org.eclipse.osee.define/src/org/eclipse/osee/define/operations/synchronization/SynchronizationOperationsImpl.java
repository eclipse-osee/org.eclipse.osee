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

package org.eclipse.osee.define.operations.synchronization;

import java.io.InputStream;
import java.util.Map;
import java.util.Objects;
import org.eclipse.osee.define.api.synchronization.ExportRequest;
import org.eclipse.osee.define.api.synchronization.ImportRequest;
import org.eclipse.osee.define.api.synchronization.SynchronizationOperations;
import org.eclipse.osee.define.util.OsgiUtils;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.Validation;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * Implementation of the {@link SynchronizationOperationsImpl} interface contains the methods that are invoked when a
 * REST API call has been made for a synchronization artifact.
 *
 * @author Loren K. Ashley
 */

public class SynchronizationOperationsImpl implements SynchronizationOperations {

   /**
    * Saves the single instance of the {@link SynchronizationOperationsImpl}.
    */

   private static SynchronizationOperationsImpl synchronizationOperationsImpl = null;

   /**
    * Saves the orcsApi handle.
    */

   private final OrcsApi orcsApi;

   /**
    * A {@link Map} of the supported {@link SynchronizationArtifactBuilder} classes by their artifact type
    * {@link String} identifiers.
    */

   private Map<String, Class<SynchronizationArtifactBuilder>> synchronizationArtifactBuilderClassMap;

   /**
    * Creates an object to process synchronization REST calls.
    *
    * @param orcsApi the {@link OrcsApi} handle.
    */

   private SynchronizationOperationsImpl(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
      this.synchronizationArtifactBuilderClassMap = null;
   }

   /**
    * Gets or creates the single instance of the {@link SynchronizationOperationsImpl} class.
    *
    * @param orcsApi A reference to the {@link OrcsApi}.
    * @return the single {@link SynchronizationOperationsImpl} object.
    * @throws NullPointerException when the parameter <code>orcsApi</code> is <code>null</code> and the single instance
    * of the {@link SynchronizationOperationsImpl} has not yet been created.
    */

   public synchronized static SynchronizationOperationsImpl create(OrcsApi orcsApi) {

      //@formatter:off
      return
         Objects.isNull( SynchronizationOperationsImpl.synchronizationOperationsImpl )
            ? ( SynchronizationOperationsImpl.synchronizationOperationsImpl = new SynchronizationOperationsImpl( Objects.requireNonNull( orcsApi ) ) )
            : SynchronizationOperationsImpl.synchronizationOperationsImpl;
      //@formatter:on
   }

   /*
    * Find the available {@link SynchronizationArtifactBuilder} classes.
    */

   private void findSynchronizationArtifactBuilders() {

      //@formatter:off
      this.synchronizationArtifactBuilderClassMap =
         OsgiUtils.findImplementations
            (
               "org/eclipse/osee/define/operations/synchronization", /* Package path to search for classes. */
               IsSynchronizationArtifactBuilder.class,               /* Classes must have this Annotation class to be found. */
               "artifactType",                                       /* Annotation parameter (class method) to get the Publishing Template key from. */
               SynchronizationArtifactBuilder.class                  /* Classes must implement this interface to be found. */
            );
      //@formatter:on
   }

   /**
    * Gets the {@link SynchronizationArtifactBuilder} for the type of Synchronization Artifact to be built.
    *
    * @param artifactType the type of Synchronization Artifact to be built.
    * @return the {@link SynchronizationArtifactBuilder} to build the Synchronization Artifact with.
    * @throws UnknownSynchronizationArtifactTypeException when a {@link SynchronizationArtifactBuilder} could not be
    * created for the artifact type.
    */

   public SynchronizationArtifactBuilder getSynchronizationArtifactBuilder(String artifactType) throws UnknownSynchronizationArtifactTypeException {

      if (Objects.isNull(this.synchronizationArtifactBuilderClassMap)) {
         this.findSynchronizationArtifactBuilders();
      }

      try {
         //@formatter:off
         return
            this.synchronizationArtifactBuilderClassMap
            .get( artifactType )
            .getConstructor( (Class<?>[]) null )
            .newInstance( (Object[]) null );
         //@formatter:on
      } catch (Exception e) {
         throw new UnknownSynchronizationArtifactTypeException(artifactType, e);
      }
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public InputStream exporter(ExportRequest exportRequest) {

      //@formatter:off
      Validation.require
         (
            exportRequest,
            Validation.ValueType.PARAMETER,
            "SynchronizationOperationsImpl",
            "exporter",
            "exportRequest",
            "cannot be null",
            Objects::isNull,
            NullPointerException::new,
            "cannot be sentinel",
            (p) -> !p.isValid(),
            IllegalArgumentException::new
         );
      //@formatter:on

      RootList rootList;

      var synchronizationArtifactType = exportRequest.getSynchronizationArtifactType();

      var synchronizationArtifactBuilder = this.getSynchronizationArtifactBuilder(synchronizationArtifactType);

      rootList = RootList.create(this.orcsApi, Direction.EXPORT, synchronizationArtifactBuilder);

      var rootsArray = exportRequest.getRoots();
      for (var root : rootsArray) {
         rootList.add(root);
      }

      rootList.validate();

      try (var synchronizationArtifact = SynchronizationArtifact.create(rootList)) {

         synchronizationArtifact.build();
         return synchronizationArtifact.serialize();
      }
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public void importer(ImportRequest importRequest, InputStream inputStream) {

      var complete = false;

      try {

         Message message = null;

         //@formatter:off
         message =
            Validation.require
               (
                  message,
                  importRequest,
                  Validation.ValueType.PARAMETER,
                  "SynchronizationOperationsImpl",
                  "importer",
                  "importRequest",
                  "cannont be null",
                  Objects::isNull,
                  "is invalid",
                  (p) -> !p.isValid()
               );

         message =
            Validation.requireNonNull
               (
                  message,
                  inputStream,
                  "SynchronizationOperationsImpl",
                  "importer",
                  "inputStream"
               );

         if( Objects.nonNull( message ) ) {
            throw
               new IllegalArgumentException
                      (
                         Validation.buildIllegalArgumentExceptionMessage
                            (
                               this.getClass().getSimpleName(),
                               "importer",
                               message
                            )
                      );
         }
         //@formatter:on

         RootList rootList;

         var synchronizationArtifactType = importRequest.getSynchronizationArtifactType();

         var synchronizationArtifactBuilder = this.getSynchronizationArtifactBuilder(synchronizationArtifactType);

         var importMappingsArray = importRequest.getImportMappings();
         rootList = RootList.create(this.orcsApi, Direction.IMPORT, synchronizationArtifactBuilder);

         for (var importMapping : importMappingsArray) {
            rootList.add(importMapping.getRoot());
         }

         rootList.validate();

         try (var synchronizationArtifact = SynchronizationArtifact.create(rootList)) {

            synchronizationArtifact.deserialize(inputStream);
            synchronizationArtifact.buildForeign();
         }

         complete = true;

      } finally {

         if (Objects.nonNull(inputStream)) {
            try {
               inputStream.close();
            } catch (Exception e) {
               /*
                * If the primary try block didn't complete, eat the exception so the primary exception is not masked by
                * the close exception.
                */
               if (complete) {
                  throw new OseeCoreException(
                     "SynchronizationOperationsImpl::importer, failed to close \"inputStream\".", e);
               }
            }
         }
      }
   }
}

/* EOF */