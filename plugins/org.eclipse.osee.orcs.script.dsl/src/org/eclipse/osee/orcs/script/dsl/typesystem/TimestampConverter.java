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

package org.eclipse.osee.orcs.script.dsl.typesystem;

import static org.eclipse.osee.orcs.script.dsl.OrcsScriptDslConstants.COVERSION_ERROR__BAD_FORMAT_TEMPLATE__MSG;
import static org.eclipse.osee.orcs.script.dsl.OrcsScriptDslConstants.COVERSION_ERROR__DEFAULT_LOCALE_FORMAT__MSG;
import static org.eclipse.osee.orcs.script.dsl.OrcsScriptDslConstants.TIMESTAMP_FORMAT;
import com.google.inject.Singleton;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.eclipse.osee.orcs.script.dsl.OrcsScriptUtil;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsStringLiteral;
import org.eclipse.xtext.conversion.ValueConverterException;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;

/**
 * @author Roberto E. Escobar
 */
@Singleton
public class TimestampConverter {

   public boolean isTimestampType(Class<?> type) {
      return getType().isAssignableFrom(type);
   }

   public boolean isTimestampType(OsStringLiteral object) {
      boolean canParse = false;
      if (object != null) {
         String rawValue = object.getValue();
         if (rawValue != null) {
            try {
               OrcsScriptUtil.parseDate(rawValue);
               canParse = true;
            } catch (ParseException ex) {
               canParse = false;
            }
         }
      }
      return canParse;
   }

   public Class<?> getType() {
      return Date.class;
   }

   public Date toValue(OsStringLiteral literal) {
      ICompositeNode node = NodeModelUtils.findActualNodeFor(literal);
      return toValue(literal.getValue(), node);
   }

   public Date toValue(String rawValue, INode node) throws ValueConverterException {
      Date toReturn = null;
      if (rawValue != null) {
         try {
            toReturn = OrcsScriptUtil.parseDate(rawValue);
         } catch (ParseException ex) {
            String errorMsg = newBadTimestampFormatErrorMsg();
            throw new ValueConverterException(errorMsg, node, new TimestampFormatException(ex));
         }
      }
      return toReturn;
   };

   private String newBadTimestampFormatErrorMsg() {
      DateFormat fmt = DateFormat.getDateTimeInstance();
      String defaultFormat;
      if (fmt instanceof SimpleDateFormat) {
         SimpleDateFormat sdfmt = (SimpleDateFormat) fmt;
         defaultFormat = sdfmt.toLocalizedPattern();
      } else {
         defaultFormat = COVERSION_ERROR__DEFAULT_LOCALE_FORMAT__MSG;
      }
      return String.format(COVERSION_ERROR__BAD_FORMAT_TEMPLATE__MSG, TIMESTAMP_FORMAT, defaultFormat);
   }

   public static class TimestampFormatException extends Exception {

      private static final long serialVersionUID = -1056825990595244386L;

      public TimestampFormatException(Throwable cause) {
         super(cause);
      }

   }

}
