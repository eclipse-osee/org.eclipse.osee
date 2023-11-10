/*********************************************************************
 * Copyright (c) 2023 Boeing
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
package org.eclipse.osee.ats.api.config;

import org.eclipse.osee.framework.core.data.DisplayHint;

/**
 * @author Donald G. Dunne
 */
public class AtsDisplayHint extends DisplayHint {

   // Every ATS Attribute Type MUST specify whether attribute is Edit/Read by the user
   public static final AtsDisplayHint Edit = new AtsDisplayHint(81L, "ATS Edit"); // Attribute can be edited (workflow editor / mass edit) by user
   public static final AtsDisplayHint Read = new AtsDisplayHint(82L, "ATS Read-Only"); // Can NOT be edited without Admin
   public static final AtsDisplayHint Config = new AtsDisplayHint(83L, "ATS Config"); // Attributes used in ATS Config objects (not workflows)
   public static final AtsDisplayHint ReadConfig = new AtsDisplayHint(84L, "ATS Read-Only and Config"); // Corner case where attr used in workflows and config
   public static final AtsDisplayHint UserArtId = new AtsDisplayHint(85L, "ATS User ArtId Attr"); // Attribute where store value is a user artifact id
   public static final AtsDisplayHint UserUserId = new AtsDisplayHint(86L, "ATS User UserId Attr"); // Attribute where store value is a user UserId

   protected AtsDisplayHint(Long id, String name) {
      super(id, name);
   }

}
