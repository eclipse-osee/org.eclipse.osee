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
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;

/**
 * @author Roberto E. Escobar
 */
public class ExpressionValueResolver<R> extends OrcsScriptDslSwitch<Object> {

   private final TimestampConverter tsConverter;
   private final NumberConverter numberConverter;

   private final Class<R> type;
   private final boolean isCycleProtected;
   private Set<EObject> visited;

   public ExpressionValueResolver(TimestampConverter tsConverter, NumberConverter numberConverter, Class<R> type, boolean isCycleProtected) {
      this.tsConverter = tsConverter;
      this.numberConverter = numberConverter;
      this.type = type;
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

   protected Class<R> getType() {
      return type;
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
   public Boolean caseOsBooleanLiteral(OsBooleanLiteral object) {
      return object.isIsTrue();
   }

   @Override
   public Number caseOsNumberLiteral(OsNumberLiteral object) {
      return numberConverter.numberValue(object.getValue(), getType());
   }

   @Override
   public Object caseOsStringLiteral(OsStringLiteral object) {
      Object toReturn;
      Class<?> type = getType();
      if (tsConverter.isTimestampType(type)) {
         toReturn = tsConverter.toValue(object);
      } else {
         toReturn = object.getValue();
      }
      return toReturn;
   }

   @Override
   public Object caseOsTemplateLiteral(OsTemplateLiteral object) {
      return resolveTemplate(getType(), object);
   }

   protected Map<String, Object> getTemplateBindings(OsTemplateLiteral object) {
      return OrcsScriptUtil.getBinding(object);
   }

   @SuppressWarnings("unchecked")
   protected <T> T resolveTemplate(Class<T> type, OsTemplateLiteral object) {
      Map<String, Object> data = getTemplateBindings(object);
      String templateId = object.getValue();
      Object value = data.get(templateId);
      T toReturn = null;
      if (value == null) {
         // do nothing;
      } else if (tsConverter.isTimestampType(type) && value instanceof String) {
         ICompositeNode node = NodeModelUtils.findActualNodeFor(object);
         toReturn = (T) tsConverter.toValue((String) value, node);
      } else if (Number.class.isAssignableFrom(type) && value instanceof String) {
         toReturn = (T) numberConverter.numberValue((String) value, type);
      } else if (Boolean.class.isAssignableFrom(type) && value instanceof String) {
         toReturn = (T) Boolean.valueOf((String) value);
      } else {
         toReturn = type.cast(value);
      }
      return toReturn;
   }
}