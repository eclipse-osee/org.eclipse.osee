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

import org.eclipse.osee.ats.workdef.StateDefinition;
import org.eclipse.osee.ats.workdef.StateXWidgetPage;
import org.eclipse.osee.ats.workdef.WorkDefinition;

/**
 * @author Donald G. Dunne
 */
public class AtsLogWorkPage extends StateXWidgetPage {

   public final static String PAGE_ID = "ats.Log";

   public static class EmptyWorkFlowDefinition extends WorkDefinition {

      public EmptyWorkFlowDefinition(String name) {
         super(name);
      }
   }

   public AtsLogWorkPage(String title) {
      super(new EmptyWorkFlowDefinition(PAGE_ID), new StateDefinition(PAGE_ID), null, null);
   }

}
