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
