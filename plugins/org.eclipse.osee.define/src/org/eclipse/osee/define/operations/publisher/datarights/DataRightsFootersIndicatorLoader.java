/*********************************************************************
 * Copyright (c) 2026 Boeing
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

package org.eclipse.osee.define.operations.publisher.datarights;

import java.util.Set;
import org.eclipse.osee.orcs.search.QueryBuilder;

/**
 * Reads the data rights classification names defined by the {@code DataRightsFooters} artifact on
 * the common branch. The classification-name parsing rule is shared with
 * {@link DataRightClassificationMap} via
 * {@code org.eclipse.osee.framework.core.publishing.DataRightsClassificationNameParser} so the
 * loader and the footer map cannot drift.
 *
 * @author David W. Miller
 */

public interface DataRightsFootersIndicatorLoader {

   /**
    * Reads the classification names from the {@code DataRightsFooters} artifact on the common
    * branch. The returned set is never {@code null}: when the artifact is missing or unreadable an
    * empty set is returned so callers fall back to the compiled seed set instead of failing. The
    * read performs no database writes.
    *
    * @param commonBranchQuery a query builder for the common branch.
    * @return the distinct trimmed classification names contributed by the footer values; empty when
    * the artifact is missing, unreadable, or contributes no names.
    */

   Set<String> loadClassificationNames(QueryBuilder commonBranchQuery);

}
