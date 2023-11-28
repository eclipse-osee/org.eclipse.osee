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

package org.eclipse.osee.define.operations.publisher;

import java.util.Objects;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.define.operations.api.publisher.PublisherOperations;
import org.eclipse.osee.define.operations.api.publisher.dataaccess.DataAccessOperations;
import org.eclipse.osee.define.operations.api.publisher.datarights.DataRightsOperations;
import org.eclipse.osee.define.operations.api.publisher.publishing.PublishingOperations;
import org.eclipse.osee.define.operations.api.publisher.templatemanager.TemplateManagerOperations;
import org.eclipse.osee.define.operations.publisher.dataaccess.DataAccessOperationsImpl;
import org.eclipse.osee.define.operations.publisher.datarights.DataRightsOperationsImpl;
import org.eclipse.osee.define.operations.publisher.publishing.PublishingOperationsImpl;
import org.eclipse.osee.define.operations.publisher.templatemanager.TemplateManagerOperationsImpl;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.osgi.service.event.EventAdmin;

/**
 * An implementation of the {@link PublisherOperations} interface that provides a set of the define operations interface
 * implementations used for publishing. This class instantiates two "singleton" implementations. The
 * {@link PublisherOperationsImpl#publisherOperationsImplArtifactReadable} instance uses {@link QueryBuilder} interface
 * calls that return {@link ArtifactReadableImpl} objects for the {@link ArtifactReadable} interface. The
 * {@link PublisherOperationsImpl#publisherOperationsImplArtifactReadOnly} instance uses {@link QueryBuilder} interface
 * calls the return {@link ArtifactReadOnlyImpl} objects for the {@link ArtifactReadable} interface.
 *
 * @author Loren K. Ashley
 */

public class PublisherOperationsImpl implements PublisherOperations {

   /**
    * Saves the {@link PublisherOperationsImpl#publisherOperationsImplArtifactReadable} instance that uses
    * {@link QueryBuilder} interface calls that return {@link ArtifactReadableImpl} objects for the
    * {@link ArtifactReadable} interface.
    */

   private static volatile PublisherOperationsImpl publisherOperationsImpl = null;

   /**
    * Saves the {@link DataAccessOperations} implementation.
    */

   private final DataAccessOperations dataAccessOperations;

   /**
    * Saves the {@link DataRightsOperations} implementation.
    */

   private final DataRightsOperations dataRightsOperations;

   /**
    * Saves the {@link TemplateManagerOperations} implementation.
    */

   private final TemplateManagerOperations templateManagerOperations;

   /**
    * Saves the {@link PublishingOperations} implementation.
    */

   private final PublishingOperations publishingOperations;

   /**
    * Creates or gets the two "singleton" implementations of the {@link PublisherOperations}. One implementation uses
    * {@link ArtifactReadableImpl} objects and the other uses {@link ArtifactReadOnlyImpl} objects as the implementation
    * for the {@link ArtifactReadable} interface.
    *
    * @param orcsApi a handle to the {@link OrcsApi}.
    * @param atsApi a handle to the {@link AtsApi}.
    * @param logger a handle to the {@link Log}.
    * @param eventAdmin a handle to the {@link EventAdmin}.
    * @return a {@link Pair} with the following:
    * <dl>
    * <dt>First:</dt>
    * <dd>An implementation of the {@link PublisherOperations} that uses the {@link QueryBuilder} interface that returns
    * {@link ArtifactReadableImpl} implementations of the {@link ArtifactReadable} interface.</dd>
    * <dt>Second:</dt>
    * <dd>An implementation of the {@link PublisherOperations} that uses the {@link QueryBuilder} interface that returns
    * {@link ArtifactReadOnlyImpl} implementations of the {@link ArtifactReadable} interface.</dd></dt>
    * </dl>
    * @throws NullPointerException when any of the parameters are <code>null</code>.
    */

   public synchronized static PublisherOperations create(OrcsApi orcsApi, AtsApi atsApi, Log logger, EventAdmin eventAdmin) {

      if (Objects.nonNull(PublisherOperationsImpl.publisherOperationsImpl)) {
         return PublisherOperationsImpl.publisherOperationsImpl;
      }

      Objects.requireNonNull(orcsApi);
      Objects.requireNonNull(atsApi);
      Objects.requireNonNull(logger);
      Objects.requireNonNull(eventAdmin);

      var jdbcService = Objects.requireNonNull(orcsApi.getJdbcService());

      var dataAccessOperations = new DataAccessOperationsImpl(orcsApi);

      var dataRightsOperations = DataRightsOperationsImpl.create(/* dataAccessOperations */orcsApi);

      /*
       * The template manager can run with the "faster" QueryBuilder API that returns ArtifactReadableImpl objects. The
       * template manager is not concerned with hierarchical ordering of artifact or with obtaining delete artifacts.
       */

      var templateManagerOperations = TemplateManagerOperationsImpl.create(jdbcService, logger, dataAccessOperations);

      var publishingOperations = PublishingOperationsImpl.create(orcsApi, atsApi, logger, eventAdmin,
         dataAccessOperations, dataRightsOperations, templateManagerOperations);

      var publisherOperations = new PublisherOperationsImpl(dataAccessOperations, dataRightsOperations,
         publishingOperations, templateManagerOperations);

      return publisherOperations;
   }

   /**
    * Creates a new instance of the {@link PublisherOperationsImpl}.
    *
    * @param dataAccessOperations a handle to the {@link DataAccessOperations} implementation.
    * @param dataRightsOperations a handle to the {@link DataRightsOperations} implementation that uses the
    * <code>dataAccessOperations</code> implementation.
    * @param publishingOperations a handle to the {@link PublishingOperations} implementation that uses the
    * <code>dataAccessOperations</code> implementation.
    * @param templateManagerOperations a handle to the {@link TemplateManagerOperations} implementation that uses the
    * {@link DataAccessOperations} implementation using {@link ArtifactReadableImpl} objects.
    */

   private PublisherOperationsImpl(DataAccessOperations dataAccessOperations, DataRightsOperations dataRightsOperations, PublishingOperations publishingOperations, TemplateManagerOperations templateManagerOperations) {
      this.dataAccessOperations = dataAccessOperations;
      this.dataRightsOperations = dataRightsOperations;
      this.publishingOperations = publishingOperations;
      this.templateManagerOperations = templateManagerOperations;
   }

   /**
    * {@inheritDoc}
    *
    * @implNote This {@link DataAccessOperations} is intended to only be used by the Define bundle and specialized
    * publishing bundles that inherit from publishing classes in the Define bundle.
    */

   @Override
   public DataAccessOperations getDataAccessOperations() {
      return this.dataAccessOperations;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public DataRightsOperations getDataRightsOperations() {
      return this.dataRightsOperations;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public TemplateManagerOperations getTemplateManagerOperations() {
      return this.templateManagerOperations;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public PublishingOperations getPublishingOperations() {
      return this.publishingOperations;
   }

   /**
    * Invokes the <code>free</code> methods for the {@link DataAccessOperations}, {@link DataRightsOperations},
    * {@link PublishingOperations}, and {@link TemplateManagerOperations}. The static references to the two
    * {@link PublisherOperations} objects are nulled so they can be garbage collected.
    */

   public synchronized static void free() {
      //DataAccessOperationsImplArtifactReadOnlyImpl.free();
      DataRightsOperationsImpl.free();
      PublishingOperationsImpl.free();
      TemplateManagerOperationsImpl.free();
      PublisherOperationsImpl.publisherOperationsImpl = null;
   }

}

/* EOF */
