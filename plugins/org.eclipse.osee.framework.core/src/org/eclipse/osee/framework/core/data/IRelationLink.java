/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.framework.core.data;

import org.eclipse.osee.framework.core.enums.ModificationType;

/**
 * @author Donald G. Dunne
 */
public interface IRelationLink extends RelationId {

   GammaId getGammaId();

   ModificationType getModificationType();

   RelationTypeToken getRelationType();

   boolean isOfType(RelationTypeToken relationType);

   @Override
   String toString();

   int getArtIdA();

   int getArtIdB();

   boolean isDeleted();

}