/*********************************************************************
 * Copyright (c) 2018 Boeing
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
