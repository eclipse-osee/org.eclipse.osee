/*
 * Created on Apr 9, 2020
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.api.task.related;

import org.eclipse.osee.ats.api.config.WorkType;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.framework.jdk.core.util.Strings;

public interface IAutoGenTaskData {

   WorkType getWorkType();

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