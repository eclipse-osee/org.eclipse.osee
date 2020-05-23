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

package org.eclipse.osee.orcs.script.dsl.typesystem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.script.dsl.OrcsScriptUtil;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsAssignment;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBooleanLiteral;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsExpression;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsListLiteral;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsNullLiteral;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsNumberLiteral;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsStringLiteral;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTemplateLiteral;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsVariable;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsVariableDeclaration;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsVariableReference;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.util.OrcsScriptDslSwitch;

/**
 * @author Roberto E. Escobar
 */
public class ExpressionTypeResolver<R> extends OrcsScriptDslSwitch<Object> {

   private final TimestampConverter tsConverter;
   private final boolean isCycleProtected;
   private Set<EObject> visited;

   public ExpressionTypeResolver(TimestampConverter tsConverter, boolean isCycleProtected) {
      this.tsConverter = tsConverter;
      this.isCycleProtected = isCycleProtected;
   }

   @SuppressWarnings("unchecked")
   public List<R> resolve(EObject expression) {
      List<R> toReturn = null;
      Object result = doSwitch(expression);
      if (result instanceof Collection) {
         toReturn = (List<R>) result;
      } else {
         toReturn = Collections.singletonList((R) result);
      }
      return toReturn;
   }

   @Override
   public Object doSwitch(EObject object) {
      Object result = null;
      if (object != null && !wasVisited(object)) {
         result = super.doSwitch(object);
      }
      return result;
   }

   private boolean wasVisited(EObject object) {
      boolean result = false;
      if (isCycleProtected) {
         if (visited == null) {
            visited = new HashSet<>();
         }
         result = !visited.add(object);
      }
      return result;
   }

   @Override
   public Object caseOsVariableReference(OsVariableReference object) {
      return doSwitch(object.getRef());
   }

   @Override
   public Object caseOsAssignment(OsAssignment object) {
      return doSwitch(object.getRight());
   }

   @Override
   public Object caseOsVariable(OsVariable object) {
      OsExpression assignment = object.getRight();
      return doSwitch(assignment);
   }

   @Override
   public Object caseOsNullLiteral(OsNullLiteral object) {
      return null;
   }

   @Override
   public Object caseOsVariableDeclaration(OsVariableDeclaration object) {
      List<Object> values = new ArrayList<>();
      List<OsExpression> elements = object.getElements();
      for (OsExpression variable : elements) {
         values.add(doSwitch(variable));
      }
      return values;
   }

   @Override
   public Object caseOsListLiteral(OsListLiteral object) {
      List<Object> values = new ArrayList<>();
      List<OsExpression> elements = object.getElements();
      for (OsExpression expression : elements) {
         values.add(doSwitch(expression));
      }
      return values;
   }

   @Override
   public Object caseOsBooleanLiteral(OsBooleanLiteral object) {
      return Boolean.class;
   }

   @Override
   public Object caseOsNumberLiteral(OsNumberLiteral object) {
      return Number.class;
   }

   @Override
   public Object caseOsStringLiteral(OsStringLiteral object) {
      Class<?> toReturn;
      if (tsConverter.isTimestampType(object)) {
         toReturn = tsConverter.getType();
      } else {
         toReturn = String.class;
      }
      return toReturn;
   }

   @Override
   public Object caseOsTemplateLiteral(OsTemplateLiteral object) {
      Map<String, Object> data = getTemplateBindings(object);
      String templateId = object.getValue();
      Object value = data.get(templateId);
      Class<?> toReturn;
      if (value instanceof String) {
         if (Strings.isNumeric((String) value)) {
            toReturn = Number.class;
         } else {
            toReturn = String.class;
         }
      } else {
         toReturn = value != null ? value.getClass() : Void.class;
      }
      return toReturn;
   }

   protected Map<String, Object> getTemplateBindings(OsTemplateLiteral object) {
      return OrcsScriptUtil.getBinding(object);
   }

}