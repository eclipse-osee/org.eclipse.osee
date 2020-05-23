/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.core.enums;

/**
 * @author Roberto E. Escobar
 */
public enum EditState {

   NO_CHANGE,
   ARTIFACT_TYPE_MODIFIED,
   ARTIFACT_TYPE_HISTORICAL_CHANGE;

   public boolean isArtifactTypeChange() {
      return this == ARTIFACT_TYPE_MODIFIED || this == ARTIFACT_TYPE_HISTORICAL_CHANGE;
   }

   public boolean isHistoricalArtifactTypeChange() {
      return this == ARTIFACT_TYPE_HISTORICAL_CHANGE;
   }

   public boolean isCurrentVersionArtifactTypeChange() {
      return this == ARTIFACT_TYPE_MODIFIED;
   }
}
