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

package org.eclipse.osee.orcs.core.ds;

import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.RelationId;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.enums.RelationSide;

/**
 * @author Andrew M. Finkbeiner
 */
public interface RelationData extends OrcsData<RelationTypeToken>, RelationId {

   ArtifactId getArtifactIdA();

   ArtifactId getArtifactIdB();

   void setArtIdA(ArtifactId artIdA);

   void setArtIdB(ArtifactId artIdB);

   ArtifactId getArtIdOn(RelationSide side);

   String getRationale();

   void setRationale(String rationale);

   ArtifactId getRelationArtifact();

   void setRelationArtifact(ArtifactId relationArtifact);

   int getRelOrder();

   void setRelOrder(int relOrder);
}