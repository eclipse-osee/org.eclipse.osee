/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.workdef;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;

/**
 * @author Donald G. Dunne
 */
public interface IRelationResolver {

   Collection<Object> getRelated(Object object, IRelationTypeSide relationType);

   boolean areRelated(Object object1, IRelationTypeSide relationType, Object object2);

   Object getRelatedOrNull(Object object, IRelationTypeSide relationType);

}
