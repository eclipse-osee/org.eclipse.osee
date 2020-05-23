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

package org.eclipse.osee.define.ide.traceability;

import java.nio.CharBuffer;
import java.util.Collection;
import org.eclipse.osee.define.ide.traceability.data.TraceMark;

/**
 * @author Roberto E. Escobar
 */
public interface ITraceParser {

   public boolean addIfEmpty();

   public Collection<TraceMark> getTraceMarks(CharBuffer fileBuffer);

   public CharBuffer removeTraceMarks(CharBuffer fileBuffer);

   public boolean isTraceRemovalAllowed();

   default void setupTraceMatcher(boolean includeImpd) { //
   }

   default void setupCommentTraceMatcher(boolean inludeImpd) { //
   }
}
