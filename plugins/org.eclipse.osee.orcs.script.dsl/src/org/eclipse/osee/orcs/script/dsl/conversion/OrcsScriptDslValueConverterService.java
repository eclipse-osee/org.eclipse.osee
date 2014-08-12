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
package org.eclipse.osee.orcs.script.dsl.conversion;

import org.eclipse.xtext.conversion.IValueConverter;
import org.eclipse.xtext.conversion.ValueConverter;
import org.eclipse.xtext.conversion.impl.AbstractDeclarativeValueConverterService;
import com.google.inject.Singleton;

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
