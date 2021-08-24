/*******************************************************************************
 * Copyright (c) 2021 Boeing.
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
package org.eclipse.osee.framework.ui.plugin.xnavigate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.enums.OseeEnum;

/**
 * @author Donald G. Dunne
 */
public class XNavItemCat extends OseeEnum {

   private static final Long ENUM_ID = 23443289235L;
   private static List<XNavItemCat> values = new ArrayList<XNavItemCat>();

   public static final XNavItemCat TOP = new XNavItemCat("TOP");
   public static final XNavItemCat TOP_NEW = new XNavItemCat("TOP_NEW");
   public static final XNavItemCat TOP_MID = new XNavItemCat("TOP_MID");
   public static final XNavItemCat MID_TOP = new XNavItemCat("MID_TOP");
   public static final XNavItemCat PROG = new XNavItemCat("PROG");
   public static final XNavItemCat MID = new XNavItemCat("MID");
   public static final XNavItemCat MID_BOT = new XNavItemCat("MID_BOT");
   public static final XNavItemCat BOT_MID = new XNavItemCat("BOT_MID");
   public static final XNavItemCat BOT = new XNavItemCat("BOT");

   // Child of one of above category items
   public static final XNavItemCat SUBCAT = new XNavItemCat("SUBCAT");

   // Will only display if OseeAdmin
   public static final XNavItemCat OSEE_ADMIN = new XNavItemCat("ADMIN");

   public XNavItemCat(String name) {
      super(ENUM_ID, name);
   }

   public synchronized static Collection<XNavItemCat> orderedValues() {
      if (values.isEmpty()) {
         values.addAll(Arrays.asList(TOP, TOP_NEW, TOP_MID, MID_TOP, MID, PROG, MID_BOT, BOT_MID, BOT));
      }
      return values;
   }

   @JsonIgnore
   @Override
   public Long getTypeId() {
      return ENUM_ID;
   }

   @JsonIgnore
   @Override
   public OseeEnum getDefault() {
      return BOT;
   }

}
