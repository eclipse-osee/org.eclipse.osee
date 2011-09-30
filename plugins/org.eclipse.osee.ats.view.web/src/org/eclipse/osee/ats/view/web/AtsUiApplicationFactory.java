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
package org.eclipse.osee.ats.view.web;

import org.eclipse.osee.vaadin.ApplicationFactory;
import com.vaadin.Application;

/**
 * @author Shawn F. Cook
 */
public class AtsUiApplicationFactory implements ApplicationFactory {

   @Override
   public Application createInstance() {
      return new AtsUiApplication();
   }

   @Override
   public Class<? extends Application> getApplicationClass() {
      return AtsUiApplication.class;
   }

}
