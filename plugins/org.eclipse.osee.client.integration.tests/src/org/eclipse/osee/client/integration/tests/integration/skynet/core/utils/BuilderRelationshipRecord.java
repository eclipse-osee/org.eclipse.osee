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

package org.eclipse.osee.client.integration.tests.integration.skynet.core.utils;

import java.util.List;
import org.eclipse.osee.framework.core.data.RelationTypeToken;

/**
 * Instances of the interface are used to specify relationships between test artifacts specified with an implementations
 * of {@link BuilderRecord}.
 *
 * @author Loren K. Ashley
 */

public interface BuilderRelationshipRecord {

   /**
    * Gets the relationship type as a {@link RelationTypeToken}.
    *
    * @return the relationship type.
    */

   RelationTypeToken getRelationTypeToken();

   /**
    * Gets a {@link List} of {@link BuilderRecord} identifiers for the relationship target artifacts.
    *
    * @return {@link List} of {@link Integer} {@link BuilderRecord} identifiers.
    */

   List<Integer> getTargetBuilderRecords();
}

/* EOF */