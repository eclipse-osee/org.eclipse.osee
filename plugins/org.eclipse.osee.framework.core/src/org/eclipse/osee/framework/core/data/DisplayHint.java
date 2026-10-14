/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.data;

import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;

/**
 * @author Donald G. Dunne
 */
public class DisplayHint extends NamedIdBase {

   public static final DisplayHint SingleLine = new DisplayHint(1L, "Single Line");
   public static final DisplayHint MultiLine = new DisplayHint(2L, "Multiline");
   public static final DisplayHint NoGeneralEdit = new DisplayHint(3L, "No General Edit");
   public static final DisplayHint NoGeneralRender = new DisplayHint(4L, "No General Render");
   // Stores true/false for boolean but shows Yes/No in display and edit; Not valid for anything but boolean
   public static final DisplayHint YesNoBoolean = new DisplayHint(5L, "Show Yes/No for Boolean Value");
   // Allows for true/false/clear and yes/no/clear in display and edit; Not valid for anything but boolean
   public static final DisplayHint TriStateBoolean = new DisplayHint(6L, "Allow for Clear in Boolean");
   public static final DisplayHint IndexBased = new DisplayHint(7L, "IndexBased Search");
   public static final DisplayHint DisplaySizeLimited = new DisplayHint(8L, "Display Size Limited");
   // Sort by code-configured order vs alphabetical
   public static final DisplayHint InOrder = new DisplayHint(9L, "In-Order");

   public static final DisplayHint Common = new DisplayHint(77L, "Common Branch"); // Reference is on Common Branch

   public static final DisplayHint Edit = new DisplayHint(81L, "Edit"); // Attribute can be edited (workflow editor / mass edit) by user
   public static final DisplayHint Config = new DisplayHint(83L, "Config"); // Attributes used in ATS Config objects (not workflows)

   public static final DisplayHint Read = new DisplayHint(82L, "Read-Only", true); // Can NOT be edited without Admin
   public static final DisplayHint ReadConfig = new DisplayHint(84L, "Read-Only and Config", true); // Corner case where attr used in workflows and config
   public static final DisplayHint UserArtId = new DisplayHint(85L, "User ArtId Attr", true); // Attribute where store value is a user artifact id
   public static final DisplayHint UserUserId = new DisplayHint(86L, "User UserId Attr", true); // Attribute where store value is a user UserId
   public static final DisplayHint SignByDate = new DisplayHint(87L, "Sign-By Date", true); // Date Attribute used for approval
   public static final DisplayHint SignByUser = new DisplayHint(88L, "Sign-By User", true); // User Attribute used for approval

   public static final DisplayHint NoSpell = new DisplayHint(91L, "No_Spell"); // Disable spell checking values; eg: User Name

   // XWidgets
   public static final DisplayHint XTextFlat = new DisplayHint(10L, "XFlatDam", WidgetId.XXFlatAttrsWidget);
   public static final DisplayHint XBranchSel = new DisplayHint(11L, "XBranchSel", WidgetId.XXBranchSelWidget);
   public static final DisplayHint XArtRef = new DisplayHint(12L, "XArtRef", WidgetId.XXArtifactSelWidget);

   private WidgetId widgetId = WidgetId.SENTINEL;
   /**
    * Read-Only for most generic editors. <br/>
    * <br/>
    * eg: Don't want user to directly edit ATS Current Assignees through Artifact Editor
    */
   private boolean readonly = false;

   protected DisplayHint(Long id, String name) {
      this(id, name, false);
   }

   protected DisplayHint(Long id, String name, boolean readonly) {
      super(id, name);
      this.readonly = readonly;
   }

   protected DisplayHint(Long id, String name, WidgetId widgetId) {
      super(id, name);
      this.widgetId = widgetId;
   }

   public WidgetId getWidgetId() {
      return widgetId;
   }

   public boolean isReadonly() {
      return readonly;
   }
}
