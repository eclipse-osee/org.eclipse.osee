/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.script.dsl;

import java.util.Set;
import org.eclipse.emf.ecore.EObject;

/**
 * @author Roberto E. Escobar
 */
public interface IFieldResolver {

   public static interface OsField {

      String getFieldName();

   }

   Set<OsCollectType> getAllowedCollectTypes(EObject object);

   OsCollectType getCollectType(EObject object);

   Set<? extends OsField> getAllowedFields(EObject object);

   Set<? extends OsField> getDeclaredFields(EObject object);

   Set<? extends OsField> getRemainingAllowedFields(EObject object);

   Set<? extends OsField> getNotAllowedDeclaredFields(EObject object);

}
