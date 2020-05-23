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

import java.util.List;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsExpression;

/**
 * @author Roberto E. Escobar
 */
public interface IExpressionResolver {

   <T> List<T> resolve(Class<T> clazz, List<OsExpression> expressions);

   <T> List<T> resolve(Class<T> clazz, OsExpression expression);

   <T> T resolveSingle(Class<T> clazz, OsExpression expression);

   List<Class<?>> resolveTypes(List<OsExpression> expression);

   List<Class<?>> resolveType(OsExpression expression);

   Class<?> resolveTypeSingle(OsExpression expression);

}
