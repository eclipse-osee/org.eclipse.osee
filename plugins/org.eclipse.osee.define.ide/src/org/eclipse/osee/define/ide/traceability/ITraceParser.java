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
