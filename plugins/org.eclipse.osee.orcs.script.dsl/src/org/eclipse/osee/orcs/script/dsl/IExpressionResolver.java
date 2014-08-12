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
