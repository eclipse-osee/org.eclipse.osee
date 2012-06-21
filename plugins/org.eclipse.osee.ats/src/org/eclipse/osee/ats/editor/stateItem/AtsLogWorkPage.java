/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.editor.stateItem;

import java.util.Collections;
import java.util.List;
import org.eclipse.osee.ats.core.workdef.AtsWorkDefinitionService;
import org.eclipse.osee.ats.workdef.StateXWidgetPage;
import org.eclipse.osee.ats.workdef.api.IAtsStateDefinition;
import org.eclipse.osee.ats.workdef.api.IAtsWorkDefinition;

/**
 * @author Donald G. Dunne
 */
public class AtsLogWorkPage extends StateXWidgetPage {

   public final static String PAGE_ID = "ats.Log";

   public static class EmptyWorkFlowDefinition implements IAtsWorkDefinition {

      private String name;
      private String id;

      public EmptyWorkFlowDefinition(String name) {
         this.name = name;
      }

      @Override
      public String getName() {
         return name;
      }

      @Override
      public IAtsStateDefinition getStateByName(String name) {
         return null;
      }

      @Override
      public boolean hasRule(String name) {
         return false;
      }

      @Override
      public List<String> getRules() {
         return Collections.emptyList();
      }

      @Override
      public IAtsStateDefinition getStartState() {
         return null;
      }

      @Override
      public void setStartState(IAtsStateDefinition startState) {
      }

      @Override
      public String getId() {
         return "";
      }

      @Override
      public void setName(String name) {
         this.name = name;
      }

      @Override
      public void setDescription(String description) {
      }

      @Override
      public void setId(String id) {
         this.id = id;
      }

      @Override
      public String getDescription() {
         return this.name;
      }

      @Override
      public List<IAtsStateDefinition> getStates() {
         return null;
      }

      @Override
      public void addRule(String rule) {
      }

      @Override
      public void removeRule(String rule) {
      }

      @Override
      public IAtsStateDefinition addState(IAtsStateDefinition state) {
         return null;
      }
   }

   public AtsLogWorkPage(String title) {
      super(new EmptyWorkFlowDefinition(PAGE_ID), AtsWorkDefinitionService.getService().createStateDefinition(PAGE_ID), null, null);
   }

}
