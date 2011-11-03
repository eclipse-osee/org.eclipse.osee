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
package org.eclipse.osee.display.mvp.view;

import org.eclipse.osee.logger.Log;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractViewFactory implements ViewFactory {

   private Log logger;

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public Log getLogger() {
      return logger;
   }

   @Override
   public boolean canCreate(Class<? extends View> viewType) {
      return true;
   }

   protected boolean implementsType(Class<? extends View> concreteType, Class<? extends View> interfaceType) {
      return interfaceType.isAssignableFrom(concreteType);
   }
}
