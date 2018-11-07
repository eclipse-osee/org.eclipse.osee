/*******************************************************************************
 * Copyright (c) 2018 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.dsl;

import org.eclipse.osee.framework.core.dsl.conversion.OseeStringValueConverter;
import org.eclipse.xtext.conversion.IValueConverterService;

/**
 * Use this class to register components to be used at runtime / without the Equinox extension registry.
 * 
 * @author Roberto E. Escobar
 */
public class OseeDslRuntimeModule extends org.eclipse.osee.framework.core.dsl.AbstractOseeDslRuntimeModule {

   @Override
   public Class<? extends IValueConverterService> bindIValueConverterService() {
      return OseeStringValueConverter.class;
   }
}
