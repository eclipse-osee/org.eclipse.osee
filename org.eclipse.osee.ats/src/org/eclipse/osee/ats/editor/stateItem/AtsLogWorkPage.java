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

import org.eclipse.osee.ats.workflow.AtsWorkPage;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkFlowDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;

/**
 * @author Donald G. Dunne
 */
public class AtsLogWorkPage extends AtsWorkPage {

   public static String PAGE_ID = "ats.Log";

   public static class EmptyWorkFlowDefinition extends WorkFlowDefinition {

      /**
       * @param name
       * @param id
       * @param parentId
       */
      public EmptyWorkFlowDefinition(String name, String id) {
         super(name, id, null);
      }

   }

   /**
    * @param title
    */
   public AtsLogWorkPage(String title) {
      super(new EmptyWorkFlowDefinition(title, PAGE_ID), new WorkPageDefinition(title, PAGE_ID, null), null, null);
   }

}
