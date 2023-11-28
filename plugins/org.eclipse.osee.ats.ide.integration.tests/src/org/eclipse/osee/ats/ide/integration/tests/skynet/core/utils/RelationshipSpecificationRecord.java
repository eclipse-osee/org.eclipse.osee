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

package org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils;

import java.util.List;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.osee.framework.core.data.RelationTypeToken;

/**
 * Instances of the interface are used to specify relationships between test artifacts specified with an implementation
 * of {@link ArtifactSpecificationRecord}.
 *
 * @author Loren K. Ashley
 */

public interface RelationshipSpecificationRecord {

   /**
    * Gets the relationship type as a {@link RelationTypeToken}.
    *
    * @return the relationship type.
    * @implSpec Implementations shall not return <code>null</code>.
    */

   public @NonNull RelationTypeToken getRelationTypeToken();

   /**
    * Gets a {@link List} of {@link ArtifactSpecificationRecord} identifiers for the relationship target artifacts.
    *
    * @return {@link List} of {@link Integer} {@link ArtifactSpecificationRecord} identifiers.
    * @implSpec Implementations shall not return <code>null</code>.
    * @implSpec Implementations shall not return a {@link List} with <code>null</code> elements.
    */

   public @NonNull List<@NonNull Integer> getRelationshipTargetArtifactSpecificationRecordIdentifiers();
}

/* EOF */