/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.integration.tests.ats.actions;

import org.eclipse.osee.ats.ide.actions.TaskAddAction;
import org.eclipse.osee.ats.ide.actions.TaskAddAction.ITaskAddActionHandler;

/**
 * @author Donald G. Dunne
 */
public class TaskAddActionTest extends AbstractAtsActionRunTest {

   @Override
   public TaskAddAction createAction() {
      return new TaskAddAction(new ITaskAddActionHandler() {

         @Override
         public void taskAddActionHandler() {
            System.out.println(" ");
         }
      });
   }

}
