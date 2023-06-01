/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.orcs.db.internal.search.handlers;

/**
 * @author Roberto E. Escobar
 */
public enum SqlHandlerPriority {
   BRANCH_ANCESTOR_OF,
   BRANCH_CHILD_OF,
   BRANCH_ID,
   BRANCH_CATEGORY,
   MERGE_BRANCH_FOR,
   BRANCH_TYPE,
   BRANCH_STATE,
   BRANCH_ARCHIVED,
   BRANCH_ASSOCIATED_ART_ID,
   BRANCH_NAME,
   ALL_BRANCHES,
   BRANCH_TX_DATA_XTRA,
   BRANCH_MAP_ASSOC_ART_TO_REL_ATTR,
   TX_ID,
   TX_BRANCH_ID,
   TX_COMMIT_ART_ID,
   TX_AUTHOR,
   TX_TYPE,
   TX_DATE,
   TX_COMMENT,
   TX_LAST,
   ARTIFACT_ID,
   ARTIFACT_GUID,
   ATTRIBUTE_VALUE,
   ATTRIBUTE_TOKENIZED_VALUE,
   ATTRIBUTE_TYPE_EXISTS,
   ATTRIBUTE_TYPE_NOT_EXISTS,
   ATTRIBUTE_DATA_XTRA,
   ATTRIBUTE_TX_DATA_XTRA,
   ARTIFACT_TYPE,
   ARTIFACT_ID_QUERY,
   ARTIFACT_TOKEN_QUERY,
   ARTIFACT_TX_DATA_XTRA,
   RELATION_DATA_XTRA,
   RELATION_TX_DATA_XTRA,
   ATTRIBUTE_SORT,
   PAGINATION,
   RELATED_TO_ART_IDS,
   FOLLOW_RELATION_TYPES,
   RELATION_TYPE_EXISTS,
   FOLLOW_SEARCH,
   LAST;
}