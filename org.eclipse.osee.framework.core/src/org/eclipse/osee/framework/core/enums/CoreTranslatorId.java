/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.enums;

import org.eclipse.osee.framework.core.services.ITranslatorId;

/**
 * @author Megumi Telles
 * @author Roberto E. Escobar
 */
public enum CoreTranslatorId implements ITranslatorId {
   BRANCH_COMMIT_REQUEST,
   BRANCH_COMMIT_RESPONSE,
   PURGE_BRANCH_REQUEST,

   CHANGE_VERSION,
   CHANGE_ITEM,
   CHANGE_REPORT_REQUEST,
   CHANGE_REPORT_RESPONSE,

   OSEE_IMPORT_MODEL_REQUEST,
   OSEE_IMPORT_MODEL_RESPONSE,

   OSEE_CACHE_UPDATE_REQUEST,
   ARTIFACT_TYPE_CACHE_UPDATE_RESPONSE,
   ATTRIBUTE_TYPE_CACHE_UPDATE_RESPONSE,
   RELATION_TYPE_CACHE_UPDATE_RESPONSE,
   OSEE_ENUM_TYPE_CACHE_UPDATE_RESPONSE,
   TX_CACHE_UPDATE_RESPONSE,
   BRANCH_CACHE_UPDATE_RESPONSE,
   BRANCH_CACHE_STORE_REQUEST,

   TRANSACTION_RECORD,
   BRANCH_CREATION_REQUEST,
   BRANCH_CREATION_RESPONSE,
   TABLE_DATA,

   STATUS_RESPONSE;

   @Override
   public String getKey() {
      return this.name();
   }

}
