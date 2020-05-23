/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.disposition.model;

import java.util.Date;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.jdk.core.type.Identifiable;

/**
 * @author Angel Avila
 */

public interface DispoItem extends Identifiable<String> {

   String getAssignee();

   Date getCreationDate();

   Date getLastUpdate();

   String getStatus();

   String getVersion();

   String getTotalPoints();

   Boolean getNeedsRerun();

   Map<String, Discrepancy> getDiscrepanciesList();

   List<DispoAnnotationData> getAnnotationsList();

   String getMachine();

   String getCategory();

   String getElapsedTime();

   Boolean getAborted();

   String getItemNotes();

   String getMethodNumber();

   String getFileNumber();

   Boolean getNeedsReview();

   String getTeam();

   Boolean getIsIncludeDetails();
}
