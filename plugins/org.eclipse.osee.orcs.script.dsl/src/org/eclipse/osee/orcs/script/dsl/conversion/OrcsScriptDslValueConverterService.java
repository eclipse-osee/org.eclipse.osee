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

package org.eclipse.osee.orcs.script.dsl.conversion;

import com.google.inject.Singleton;
import org.eclipse.xtext.conversion.IValueConverter;
import org.eclipse.xtext.conversion.ValueConverter;
import org.eclipse.xtext.conversion.impl.AbstractDeclarativeValueConverterService;

/**
 * @author Roberto E. Escobar
 */
@Singleton
public class OrcsScriptDslValueConverterService extends AbstractDeclarativeValueConverterService {

   @ValueConverter(rule = "STRING")
   public IValueConverter<String> STRING() {
      return new StringValueConverter();
   }

}
