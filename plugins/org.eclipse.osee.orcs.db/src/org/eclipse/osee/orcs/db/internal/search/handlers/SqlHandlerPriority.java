/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.search.handlers;

/**
 * @author Roberto E. Escobar
 */
public enum SqlHandlerPriority {
   BRANCH_ANCESTOR_OF,
   BRANCH_CHILD_OF,
   BRANCH_ID,
   MERGE_BRANCH_FOR,
   BRANCH_TYPE,
   BRANCH_STATE,
   BRANCH_ARCHIVED,
   BRANCH_ASSOCIATED_ART_ID,
   BRANCH_NAME,
   ALL_BRANCHES,
   BRANCH_TX_DATA_XTRA,
   //
   TX_ID,
   TX_BRANCH_ID,
   TX_COMMIT_ART_ID,
   TX_AUTHOR,
   TX_TYPE,
   TX_DATE,
   TX_COMMENT,
   TX_LAST,
   //
   ARTIFACT_ID,
   ARTIFACT_GUID,
   RELATED_TO_ART_IDS,
   ATTRIBUTE_VALUE,
   ATTRIBUTE_TOKENIZED_VALUE,

   ATTRIBUTE_TYPE_EXISTS,
   ATTRIBUTE_TYPE_NOT_EXISTS,
   ATTRIBUTE_DATA_XTRA,
   ATTRIBUTE_TX_DATA_XTRA,

   RELATION_TYPE_EXISTS,
   ARTIFACT_TYPE,
   ARTIFACT_ID_QUERY,
   ARTIFACT_TOKEN_QUERY,
   ARTIFACT_TX_DATA_XTRA,

   FOLLOW_RELATION_TYPES,
   RELATION_DATA_XTRA,
   RELATION_TX_DATA_XTRA,
   LAST;
}