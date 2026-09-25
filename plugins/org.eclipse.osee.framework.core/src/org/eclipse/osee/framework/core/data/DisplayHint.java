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
   public static final DisplayHint InOrder = new DisplayHint(9L, "In-Order");
   // Attribute whose stored value is a user's artifact id. Domain-neutral so any bundle
   // (ATS, MIM, core, future) can mark a user-valued attribute and have generic code
   // (e.g. change-event enrichment) recognize it without a domain dependency.
   public static final DisplayHint UserArtId = new DisplayHint(85L, "User ArtId Attr");
   // Attribute whose stored value is a user's UserId string.
   public static final DisplayHint UserId = new DisplayHint(86L, "User UserId Attr");

   protected DisplayHint(Long id, String name) {
      super(id, name);
   }
}
