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

package org.eclipse.osee.ats.api.workflow.note;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;

/**
 * @author Donald G. Dunne
 */
public enum AtsStateNoteXmlType {
   Comment,
   Question,
   Error,
   Other;

   public static AtsStateNoteXmlType getType(String type) {
      for (AtsStateNoteXmlType e : AtsStateNoteXmlType.values()) {
         if (e.name().equals(type)) {
            return e;
         }
      }
      throw new OseeArgumentException("Unhandled NoteType");
   }

   public static List<String> getNames() {
      List<String> names = new ArrayList<>();
      for (AtsStateNoteXmlType e : AtsStateNoteXmlType.values()) {
         names.add(e.name());
      }
      return names;
   }

};
