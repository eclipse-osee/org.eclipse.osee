/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.ats;

import java.util.ArrayList;

/**
 * @author Donald G. Dunne
 */
public enum NoteType {
   Comment, Question, Error, Other;

   public static NoteType getType(String type) {
      for (NoteType e : NoteType.values()) {
         if (e.name().equals(type)) {
            return e;
         }
      }
      throw new IllegalArgumentException("Unhandled NoteType");
   }

   public static ArrayList<String> getNames() {
      ArrayList<String> names = new ArrayList<String>();
      for (NoteType e : NoteType.values()) {
         names.add(e.name());
      }
      return names;
   }

};
