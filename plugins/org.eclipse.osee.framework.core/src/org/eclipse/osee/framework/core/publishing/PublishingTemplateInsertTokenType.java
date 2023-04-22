/*********************************************************************
 * Copyright (c) 2022 Boeing
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

package org.eclipse.osee.framework.core.publishing;

/**
 * Enumeration of publishing template "insert here" token types.
 *
 * @author Loren K. Ashley
 */

public enum PublishingTemplateInsertTokenType {

   /**
    * The publishing template "insert here token" is for artifacts.
    */

   ARTIFACT,

   /**
    * The publishing template "insert here token" is for links.
    */

   LINK,

   /**
    * The publishing template did not have an "insert here token".
    */

   NONE;

   /**
    * Converts the string in a case-insensitive manner into an enumeration member value. The result defaults to
    * {@link #NONE} when the provided string does not match any of the members.
    *
    * @param string the {@link String} to be parsed.
    * @return When the parameter <code>string</code> in any case is:
    * <dl>
    * <dt>"artifact"</dt>
    * <dd>{@link #ARTIFACT}</dd>
    * <dt>"link"</dt>
    * <dd>{@link #LINK}</dd>
    * </dl>
    * Otherwise, {@link #NONE} is returned.
    */

   public static PublishingTemplateInsertTokenType parse(String string) {
      PublishingTemplateInsertTokenType tokenType;

      try {
         tokenType = PublishingTemplateInsertTokenType.valueOf(string.toUpperCase());
      } catch (Exception e) {
         tokenType = NONE;
      }

      return tokenType;
   }

}

/* EOF */
