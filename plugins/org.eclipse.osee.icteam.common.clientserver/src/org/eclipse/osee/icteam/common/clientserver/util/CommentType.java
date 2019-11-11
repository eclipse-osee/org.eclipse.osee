/*********************************************************************
 * Copyright (c) 2020 Robert Bosch Engineering and Business Solutions Ltd India
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Robert Bosch Engineering and Business Solutions Ltd India - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.icteam.common.clientserver.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;

/**
 * Type of the Comment
 * 
 * @author Ajay Chandrahasan
 */
public enum CommentType {
  Comment,
  Question,
  Error,
  Other;

  /**
 * @param type 
 * @return Common Type corresponds to given type
 * @throws OseeArgumentException
 */
public static CommentType getType(final String type) throws OseeArgumentException {
    for (CommentType e : CommentType.values()) {
      if (e.name().equals(type)) {
        return e;
      }
    }
    throw new OseeArgumentException("Unhandled NoteType");
  }

  /**
 * @return all Comment types
 */
public static List<String> getNames() {
    List<String> names = new ArrayList<String>();
    for (CommentType e : CommentType.values()) {
      names.add(e.name());
    }
    return names;
  }

};
