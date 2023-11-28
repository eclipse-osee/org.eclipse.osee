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

package org.eclipse.osee.define.operations.api.publisher;

import org.eclipse.osee.define.operations.api.publisher.dataaccess.DataAccessOperations;
import org.eclipse.osee.define.operations.api.publisher.datarights.DataRightsOperations;
import org.eclipse.osee.define.operations.api.publisher.publishing.PublishingOperations;
import org.eclipse.osee.define.operations.api.publisher.templatemanager.TemplateManagerOperations;

/**
 * Interface used to obtain the define operations interface implementations used for publishing.
 *
 * @author Loren K. Ashley
 */

public interface PublisherOperations {

   /**
    * Gets an implementation of the {@link DataAccessOperations} interface.
    *
    * @return an implementation of the {@link DataAccessOperations} interface.
    * @implNote This {@link DataAccessOperations} is intended to only be used by the Define bundle and specialized
    * publishing bundles that inherit from publishing classes in the Define bundle.
    */

   DataAccessOperations getDataAccessOperations();

   /**
    * Gets the operations interface used to determine the data rights for artifacts in a publish and generate the
    * headers and footers.
    *
    * @return an implementation of the {@link DataRightsOperations} interface.
    */

   DataRightsOperations getDataRightsOperations();

   /**
    * Gets the operations interface used to publish a document or render an artifact and it's attributes.
    *
    * @return an implementation of the {@link PublishingOperations} interface.
    */

   PublishingOperations getPublishingOperations();

   /**
    * Gets the operations interface used to obtain the publishing template for a document or preview publish.
    *
    * @return an implementation of the {@link TemplateManagerOperations} interface.
    */

   TemplateManagerOperations getTemplateManagerOperations();
}

/* EOF */
