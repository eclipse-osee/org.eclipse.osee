/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.framework.core.model.change;

/**
 * @author Roberto E. Escobar
 */
public enum ChangeType {
   ARTIFACT_CHANGE,
   ATTRIBUTE_CHANGE,
   RELATION_CHANGE,
   TUPLE_CHANGE,
   UNKNOWN_CHANGE;

   public boolean isArtifactChange() {
      return this == ARTIFACT_CHANGE;
   }

   public boolean isAttributeChange() {
      return this == ATTRIBUTE_CHANGE;
   }

   public boolean isRelationChange() {
      return this == RELATION_CHANGE;
   }

   public boolean isTupleChange() {
      return this == TUPLE_CHANGE;
   }
}