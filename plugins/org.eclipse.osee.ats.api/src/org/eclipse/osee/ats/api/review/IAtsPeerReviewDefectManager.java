/*******************************************************************************
 * Copyright (c) 2022 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.review;

import java.util.Set;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;

/**
 * @author Donald G. Dunne
 */
public interface IAtsPeerReviewDefectManager {

   String getHtml();

   Set<ReviewDefectItem> getDefectItems();

   int getNumMajor(AtsUser user);

   int getNumMinor(AtsUser user);

   int getNumIssues(AtsUser user);

   int getNumMajor();

   int getNumMinor();

   int getNumIssues();

   void saveToArtifact(IAtsPeerToPeerReview peerRev, IAtsChangeSet changes);

   void addOrUpdateDefectItem(ReviewDefectItem defectItem);

   void removeDefectItem(ReviewDefectItem defectItem);

   void addDefectItem(String description);

   String getTable();

}