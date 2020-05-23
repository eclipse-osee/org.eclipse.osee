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

package org.eclipse.osee.framework.ui.skynet.action;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;

/**
 * @author Donald G. Dunne
 */
public class OpenPropertiesHandler extends AbstractHandler {

   public OpenPropertiesHandler() {
   }

   @Override
   public Object execute(ExecutionEvent event) {
      new OpenOseePropertiesAction().run();
      return null;
   }
}
