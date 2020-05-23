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

package org.eclipse.osee.orcs.script.dsl.validation;

import static org.eclipse.osee.orcs.script.dsl.OrcsScriptDslConstants.CONVERSION_ERROR__BAD_TIMESTAMP_FORMAT__CODE;
import com.google.inject.Singleton;
import org.eclipse.osee.orcs.script.dsl.typesystem.TimestampConverter.TimestampFormatException;
import org.eclipse.xtext.conversion.ValueConverterException;
import org.eclipse.xtext.nodemodel.SyntaxErrorMessage;
import org.eclipse.xtext.parser.antlr.SyntaxErrorMessageProvider;

/**
 * @author Roberto E. Escobar
 */
@Singleton
public class OrcsScriptSyntaxErrorMessageProvider extends SyntaxErrorMessageProvider {

   @Override
   public SyntaxErrorMessage getSyntaxErrorMessage(IValueConverterErrorContext context) {
      String errorMsg = context.getDefaultMessage();
      SyntaxErrorMessage toReturn;
      ValueConverterException ex = context.getValueConverterException();
      if (ex != null) {
         String message = ex.getMessage();
         if (message != null) {
            errorMsg = message;
         }
         Throwable cause = ex.getCause();
         if (cause instanceof TimestampFormatException) {
            toReturn = new SyntaxErrorMessage(errorMsg, CONVERSION_ERROR__BAD_TIMESTAMP_FORMAT__CODE);
         } else {
            toReturn = super.getSyntaxErrorMessage(context);
         }
      } else {
         toReturn = super.getSyntaxErrorMessage(context);
      }
      return toReturn;
   }
}
