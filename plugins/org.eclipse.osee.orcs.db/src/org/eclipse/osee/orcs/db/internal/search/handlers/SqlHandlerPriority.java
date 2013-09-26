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
   ARTIFACT_ID,
   ARTIFACT_GUID,
   ATTRIBUTE_VALUE,
   ATTRIBUTE_TOKENIZED_VALUE,
   ARTIFACT_TYPE,
   ATTRIBUTE_TYPE_EXISTS,
   RELATION_TYPE_EXISTS,
   RELATED_TO_ART_IDS,
   ALL_ARTIFACTS;
}