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

import org.eclipse.osee.orcs.search.QueryBuilder;

/**
 * Merges the data rights classification names defined by the {@code DataRightsFooters} artifact on
 * the common branch into the valid enum set of the {@code DataRightsClassification} attribute type.
 * The classification names are read with a {@link DataRightsFootersIndicatorLoader} and promoted
 * into the attribute type's valid selectable set so they appear in pick lists and pass validation.
 *
 * @author David W. Miller
 */

public interface RequiredIndicatorRefresher {

   /**
    * Merges the footer-derived classification names into the {@code DataRightsClassification}
    * attribute type's valid enum set. Only names that are not already valid are added; the compiled
    * seed values and any previously added values are retained. The operation performs no database
    * writes and is safe to call repeatedly: a second call with unchanged footer content adds no
    * values and returns zero.
    *
    * @param commonBranchQuery a query builder for the common branch.
    * @return the count of classification names newly added to the valid enum set; never negative.
    */

   int refresh(QueryBuilder commonBranchQuery);

}
