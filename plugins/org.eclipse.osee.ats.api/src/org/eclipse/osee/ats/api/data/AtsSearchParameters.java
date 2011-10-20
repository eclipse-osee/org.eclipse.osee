/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.data;

import org.eclipse.osee.display.api.data.ViewId;
import org.eclipse.osee.display.api.data.ViewSearchParameters;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author John Misinco
 */
public class AtsSearchParameters extends ViewSearchParameters {

   private final ViewId build, program;

   public AtsSearchParameters(String searchString, Boolean nameOnly, Boolean verboseResults, ViewId build, ViewId program) {
      super(searchString, nameOnly, verboseResults);
      this.build = build;
      this.program = program;
   }

   public ViewId getBuild() {
      return build;
   }

   public ViewId getProgram() {
      return program;
   }

   public boolean isValid() {
      return (build != null && program != null && Strings.isValid(getSearchString()));
   }

}
