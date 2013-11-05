/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.dsl.validation;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.osee.ats.dsl.AttributeResolverService;
import org.eclipse.osee.ats.dsl.atsDsl.AtsDsl;
import org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage;
import org.eclipse.osee.ats.dsl.atsDsl.AttrWidget;
import org.eclipse.osee.ats.dsl.atsDsl.Composite;
import org.eclipse.osee.ats.dsl.atsDsl.LayoutDef;
import org.eclipse.osee.ats.dsl.atsDsl.LayoutItem;
import org.eclipse.osee.ats.dsl.atsDsl.LayoutType;
import org.eclipse.osee.ats.dsl.atsDsl.StateDef;
import org.eclipse.osee.ats.dsl.atsDsl.ToState;
import org.eclipse.osee.ats.dsl.atsDsl.WidgetDef;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.ComposedChecks;

// Override the checks in AbstractAtsDslJavaValidator to provide own Name validator
/**
 * @author Donald G. Dunne
 */
@ComposedChecks(validators = {org.eclipse.xtext.validation.ImportUriValidator.class, AtsNamesAreUniqueValidator.class})
public class AtsDslJavaValidator extends AbstractAtsDslJavaValidator {

   @Check
   public void checkPercentWeights(AtsDsl atsDsl) {
      if (atsDsl.getWorkDef() == null) {
         return;
      }
      int weight = 0;
      for (StateDef state : atsDsl.getWorkDef().getStates()) {
         weight += state.getPercentWeight();
      }
      if (weight != 0 && weight != 100) {
         for (StateDef state : atsDsl.getWorkDef().getStates()) {
            String message = String.format("State Percent Weights must add to 0 or 100; currently [%s].", weight);
            error(message, state, AtsDslPackage.Literals.STATE_DEF__PERCENT_WEIGHT,
               AtsDslPackage.STATE_DEF__PERCENT_WEIGHT, "percent_weights");
         }
      }
   }

   @Check
   public void checkOrdinalUnique(AtsDsl atsDsl) {
      if (atsDsl.getWorkDef() == null) {
         return;
      }
      Set<Integer> ordinals = new HashSet<Integer>();
      for (StateDef state : atsDsl.getWorkDef().getStates()) {
         if (ordinals.contains(state.getOrdinal())) {
            String message = String.format("Ordinals must be unique [%s].", state.getOrdinal());
            error(message, state, AtsDslPackage.Literals.STATE_DEF__ORDINAL, AtsDslPackage.STATE_DEF__ORDINAL,
               "unique_ordinals");
         } else {
            ordinals.add(state.getOrdinal());
         }
      }
   }

   @Check
   public void checkDefaultToState(AtsDsl atsDsl) {
      if (atsDsl.getWorkDef() == null) {
         return;
      }
      for (StateDef state : atsDsl.getWorkDef().getStates()) {
         ToState asDefaultToState = null;
         for (ToState toState : state.getTransitionStates()) {
            if (toState.getOptions().contains("AsDefault")) {
               if (asDefaultToState != null) {
                  String message =
                     String.format("Only One AsDefault state allowed [%s].", toState.getState().getName());
                  error(message, toState, AtsDslPackage.Literals.STATE_DEF__TRANSITION_STATES,
                     AtsDslPackage.STATE_DEF__TRANSITION_STATES, "single_as_default");
               } else {
                  asDefaultToState = toState;
               }
            }
            if (toState.getState() != null && toState.getState().getName() != null && toState.getState().getName().equals(
               state.getName())) {
               String message =
                  String.format("State should not transition to itself [%s].", toState.getState().getName());
               error(message, toState, AtsDslPackage.Literals.STATE_DEF__TRANSITION_STATES,
                  AtsDslPackage.STATE_DEF__TRANSITION_STATES, "no_transition_to_self");
            }
         }
      }
   }

   @Check
   public void checkAttributeNameValidity(AtsDsl atsDsl) {
      if (atsDsl.getWorkDef() == null) {
         return;
      }
      for (WidgetDef widget : atsDsl.getWorkDef().getWidgetDefs()) {
         String attributeName = widget.getAttributeName();
         validateAttributeName(attributeName, widget, AtsDslPackage.Literals.WIDGET_DEF__ATTRIBUTE_NAME,
            AtsDslPackage.WIDGET_DEF__ATTRIBUTE_NAME);
      }
      for (StateDef state : atsDsl.getWorkDef().getStates()) {
         LayoutType layout = state.getLayout();
         if (layout instanceof LayoutDef) {
            validateAttributeNames(((LayoutDef) layout).getLayoutItems());
         }
      }
   }

   private void validateAttributeNames(EList<LayoutItem> layoutItems) {
      for (LayoutItem item : layoutItems) {
         if (item instanceof AttrWidget) {
            validateAttributeName(((AttrWidget) item).getAttributeName(), item,
               AtsDslPackage.Literals.ATTR_WIDGET__ATTRIBUTE_NAME, AtsDslPackage.ATTR_WIDGET__ATTRIBUTE_NAME);
         } else if (item instanceof Composite) {
            validateAttributeNames(((Composite) item).getLayoutItems());
         }
      }
   }

   private void validateAttributeName(String attributeName, EObject source, EStructuralFeature eFeature, int feature) {
      if (Strings.isValid(attributeName)) {
         try {
            boolean valid = AttributeResolverService.get().getAttributeResolver().isAttributeNamed(attributeName);
            if (!valid) {
               String message = String.format("Attribute type [%s] not defined in database .", attributeName);
               warning(message, source, eFeature, feature, "attribute_type_undefined");
            }
         } catch (OseeCoreException ex) {
            String message =
               String.format("Exception [%s] accessing attribute type [%s].", ex.getLocalizedMessage(), attributeName);
            error(message, source, eFeature, feature, "attribute_type_exception");
            return;
         }
      }

   }
}
