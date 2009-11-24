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

   CHANGE_VERSION,
   CHANGE_ITEM,
   CHANGE_REPORT_REQUEST,
   CHANGE_REPORT_RESPONSE,

   OSEE_CACHE_UPDATE_REQUEST,
   OSEE_CACHE_UPDATE_RESPONSE__ARTIFACT_TYPE,
   OSEE_CACHE_UPDATE_RESPONSE__ATTRIBUTE_TYPE,
   OSEE_CACHE_UPDATE_RESPONSE__RELATION_TYPE,
   OSEE_CACHE_UPDATE_RESPONSE__OSEE_ENUM_TYPE,
   OSEE_CACHE_UPDATE_RESPONSE__TRANSACTION_RECORD,
   OSEE_CACHE_UPDATE_RESPONSE__BRANCH,

   ARTIFACT_TYPE,
   ATTRIBUTE_TYPE,
   RELATION_TYPE,
   OSEE_ENUM_TYPE,
   OSEE_ENUM_ENTRY,
   TRANSACTION_RECORD,
   BRANCH,
   ARTIFACT_METADATA;

   @Override
   public String getKey() {
      return this.name();
   }

}
