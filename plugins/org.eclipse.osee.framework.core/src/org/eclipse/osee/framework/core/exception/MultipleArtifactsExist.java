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

package org.eclipse.osee.framework.core.exception;

import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public class MultipleArtifactsExist extends OseeCoreException {

   private static final long serialVersionUID = 1L;

   public MultipleArtifactsExist(String message, Object... args) {
      super(message, args);
   }

   public MultipleArtifactsExist(String message, Throwable cause) {
      super(message, cause);
   }

   public MultipleArtifactsExist(Throwable cause, String message, Object... args) {
      super(cause, message, args);
   }

   public MultipleArtifactsExist(Throwable cause) {
      super(cause);
   }
}
