/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.data;

import org.eclipse.osee.framework.core.enums.ModificationType;

/**
 * @author Donald G. Dunne
 */
public interface IRelationLink extends RelationId {

   long getGammaId();

   ModificationType getModificationType();

   RelationTypeId getRelationType();

   boolean isOfType(IRelationType relationType);

   @Override
   String toString();

   int getArtIdA();

   int getArtIdB();

}