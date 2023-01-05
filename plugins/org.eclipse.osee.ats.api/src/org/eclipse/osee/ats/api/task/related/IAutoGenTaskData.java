/*********************************************************************
 * Copyright (c) 2023 Boeing
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
package org.eclipse.osee.ats.api.task.related;

import org.eclipse.osee.ats.api.config.WorkType;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.util.Strings;

public interface IAutoGenTaskData {

   WorkType getWorkType();

   ArtifactId getRelatedArtId();

   String getRelatedArtName();

   String getAddDetails();

   boolean isDeleted();

   IAtsTask getTask();

   AutoGenVersion getAutoGenVer();

   default boolean hasRelatedArt() {
      return Strings.isValid(getRelatedArtName());
   }

   default boolean isNoChangedArtifact() {
      return Strings.isInValid(getRelatedArtName());
   }

}