/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.orcs.script.dsl;

import java.util.Set;
import org.eclipse.emf.ecore.EObject;

/**
 * @author Roberto E. Escobar
 */
public interface IFieldResolver {

   public static interface OsField {

      String getId();

      String getLiteral();

      boolean hasChildren();

      Set<? extends OsField> getChildren();

   }

   Set<OsCollectType> getAllowedCollectTypes(EObject object);

   OsCollectType getCollectType(EObject object);

   Set<? extends OsField> getAllowedFields(EObject object);

   Set<? extends OsField> getDeclaredFields(EObject object);

   Set<? extends OsField> getRemainingAllowedFields(EObject object);

   Set<? extends OsField> getNotAllowedDeclaredFields(EObject object);

}
