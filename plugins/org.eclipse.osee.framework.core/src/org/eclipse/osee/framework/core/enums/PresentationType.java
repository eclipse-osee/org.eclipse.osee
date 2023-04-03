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

import java.util.Objects;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.ToMessage;

/**
 * @author Megumi Telles
 * @author Loren K. Ashley
 */

public enum PresentationType implements ToMessage {

   /**
    * open using general editor (i.e. artifact editor)
    */

   GENERALIZED_EDIT(null),

   /**
    * Open the artifact using an application specific editor. The temporary files for editing are created in the
    * ".working" sub-directory of the workspace.
    */

   SPECIALIZED_EDIT(PresentationType.WORKING_FOLDER), // open using application specific editor

   /**
    * Temporary files for comparison are created in the ".compare" sub-directory of the workspace.
    */

   DIFF(PresentationType.COMPARE_FOLDER),

   /**
    * Temporary files for comparison are created in the ".compare" sub-directory of the workspace.
    */

   DIFF_NO_ATTRIBUTES(PresentationType.COMPARE_FOLDER),

   F5_DIFF(null),

   /**
    * Open artifact read-only using an application specific editor with rendering done on the client. Temporary files
    * are created in the ".compare" sub-directory of the workspace.
    */

   PREVIEW(PresentationType.PREVIEW_FOLDER),

   /**
    * Open artifact read-only using an application specific editor with rendering done on the server. Temporary files
    * are created in the ".compare" sub-directory of the workspace.
    */

   PREVIEW_SERVER(PresentationType.PREVIEW_FOLDER),

   /**
    * Temporary files for comparison are created in the ".compare" sub-directory of the workspace.
    */

   MERGE(PresentationType.COMPARE_FOLDER),

   /**
    * Used to pre and post process text based attributes.
    */

   RENDER_AS_HUMAN_READABLE_TEXT(null),

   /**
    * up to the renderer to determine what is used for default
    */

   DEFAULT_OPEN(null),

   /**
    * this is the case where default open is selected and the preference "Default Presentation opens in Artifact Editor
    * if applicable" is true
    */

   GENERAL_REQUESTED(null),

   /**
    * used in conjunction with renderAttribute()
    */

   PRODUCE_ATTRIBUTE(null),

   WEB_PREVIEW(null);

   /**
    * Name of the sub-directory in the OSEE client workspace for comparing files.
    */

   private static final String COMPARE_FOLDER = ".compare";

   /**
    * Name of the sub-directory in the OSEE client workspace for previewing files.
    */

   private static final String PREVIEW_FOLDER = ".preview";

   /**
    * Name of the sub-directory in the OSEE client workspace for editing files.
    */

   private static final String WORKING_FOLDER = ".working";

   /**
    * Saves the workspace sub-directory for the {@link PresentationType}.
    */

   private String subFolder;

   /**
    * Creates a new enumeration member and saves the workspace sub-directory.
    *
    * @param subFolder the name of the workspace sub-directory.
    */

   private PresentationType(String subFolder) {
      this.subFolder = subFolder;
   }

   /**
    * Gets the workspace sub-directory for working with files of the {@link PresentationType}.
    *
    * @return the workspace sub-directory name.
    */

   public String getSubFolder() {
      return this.subFolder;
   }

   /**
    * Predicate to determine if a workspace sub-directory is defined for the member.
    *
    * @return <code>true</code>, when a workspace sub-directory is defined for the member; otherwise,
    * <code>false</code>.
    */

   public boolean isSubFolderDefined() {
      return Objects.nonNull(this.subFolder);
   }

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

   /**
    * {@inheritDoc}
    */

   @Override
   public Message toMessage(int indent, Message message) {
      var outMessage = Objects.nonNull(message) ? message : new Message();

      //@formatter:off
      outMessage
         .indent( indent )
         .title( "PresentationType" )
         .indentInc()
         .segment( "Name",       this.name()    )
         .segment( "Sub-Folder", this.subFolder )
         .indentDec()
         ;
      //@formatter:off

      return outMessage;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String toString() {
      return this.toMessage(0, null).toString();
   }
}

/* EOF */
