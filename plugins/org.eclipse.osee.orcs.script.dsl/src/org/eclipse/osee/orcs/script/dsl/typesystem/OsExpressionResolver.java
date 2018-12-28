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
package org.eclipse.osee.orcs.script.dsl.typesystem;

import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.eclipse.osee.orcs.script.dsl.IExpressionResolver;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsExpression;

/**
 * @author Roberto E. Escobar
 */
public class OsExpressionResolver implements IExpressionResolver {

   @Inject
   private TimestampConverter tsConverter;

   @Inject
   private NumberConverter numberConverter;

   private boolean isCycleProtected() {
      return false;
   }

   @Override
   public <T> List<T> resolve(Class<T> clazz, OsExpression expression) {
      ExpressionValueResolver<T> resolver =
         new ExpressionValueResolver<>(tsConverter, numberConverter, clazz, isCycleProtected());
      List<T> resolved = resolver.resolve(expression);
      return resolved != null ? resolved : Collections.<T> emptyList();
   }

   @Override
   public <T> T resolveSingle(Class<T> clazz, OsExpression expression) {
      List<T> resolved = resolve(clazz, expression);
      return resolved != null && !resolved.isEmpty() ? resolved.iterator().next() : null;
   }

   @Override
   public <T> List<T> resolve(Class<T> clazz, List<OsExpression> expressions) {
      List<T> toReturn = new ArrayList<>();
      for (OsExpression expression : expressions) {
         List<T> resolved = resolve(clazz, expression);
         toReturn.addAll(resolved);
      }
      return toReturn;
   }

   @Override
   public List<Class<?>> resolveType(OsExpression expression) {
      ExpressionTypeResolver<Class<?>> resolver = new ExpressionTypeResolver<>(tsConverter, isCycleProtected());
      List<Class<?>> resolved = resolver.resolve(expression);
      return resolved != null ? resolved : Collections.<Class<?>> emptyList();
   }

   @Override
   public Class<?> resolveTypeSingle(OsExpression expression) {
      List<Class<?>> resolved = resolveType(expression);
      return resolved != null && !resolved.isEmpty() ? resolved.iterator().next() : Void.class;
   }

   @Override
   public List<Class<?>> resolveTypes(List<OsExpression> expressions) {
      List<Class<?>> toReturn = new ArrayList<>();
      for (OsExpression expression : expressions) {
         List<Class<?>> resolved = resolveType(expression);
         toReturn.addAll(resolved);
      }
      return toReturn;
   }

}