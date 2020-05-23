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

package org.eclipse.osee.framework.skynet.core.relation.order;

import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.RelationSorter;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;

/**
 * @author Andrew M. Finkbeiner
 */
public interface IRelationSorter {

   RelationSorter getSorterId();

   void sort(List<? extends ArtifactToken> relatives, List<String> relativeSequence);

   void sortRelations(List<? extends RelationLink> listToOrder, List<String> relativeOrder);
}
