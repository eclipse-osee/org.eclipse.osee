/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.core.enums;

import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * @author Megumi Telles
 */
public enum PresentationType {
   GENERALIZED_EDIT, // open using general editor (i.e. artifact editor)
   SPECIALIZED_EDIT, // open using application specific editor
   DIFF,
   DIFF_NO_ATTRIBUTES,
   F5_DIFF,

   /**
    * Open artifact read-only using an application specific editor with rendering done on the client.
    */

   PREVIEW,

   /**
    * Open artifact read-only using an application specific editor with rendering done on the server.
    */

   PREVIEW_SERVER,

   MERGE,
   RENDER_AS_HUMAN_READABLE_TEXT, // Used to pre and post process text based attributes.
   DEFAULT_OPEN, // up to the renderer to determine what is used for default
   GENERAL_REQUESTED, // this is the case where default open is selected and the preference "Default Presentation opens in Artifact Editor if applicable" is true
   PRODUCE_ATTRIBUTE, // used in conjunction with renderAttribute()
   WEB_PREVIEW;

   public boolean matches(PresentationType... presentationTypes) {
      Conditions.checkExpressionFailOnTrue(presentationTypes.length == 0, "presentationTypes to match cannot be empty");
      boolean result = false;
      for (PresentationType presentationType : presentationTypes) {
         if (this == presentationType) {
            result = true;
            break;
         }
      }
      return result;
   }
}