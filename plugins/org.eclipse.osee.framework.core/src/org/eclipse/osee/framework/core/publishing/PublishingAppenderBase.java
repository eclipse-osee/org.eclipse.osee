/*********************************************************************
 * Copyright (c) 2024 Boeing
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

package org.eclipse.osee.framework.core.publishing;

import java.io.IOException;
import java.util.Objects;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * A base class that implements the {@link PublishingAppender} interface methods that are common to all publishing
 * formats.
 *
 * @author fi390f
 */

public abstract class PublishingAppenderBase implements PublishingAppender {

   /**
    * Generated Word ML is appended to this {@link Appendable}.
    */

   protected final Appendable appendable;

   protected PublishingAppenderBase(Appendable appendable) {
      this.appendable = Objects.requireNonNull(appendable);
   }

   /**
    * Appends the provided text as it is to the {@link #appendable}.
    *
    * @param value the text to be appended.
    */

   @Override
   public PublishingAppender append(CharSequence value) {
      try {
         this.appendable.append(value);
      } catch (IOException ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
      return this;
   }

}
