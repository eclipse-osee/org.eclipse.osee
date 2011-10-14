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

import org.eclipse.osee.display.api.data.WebId;
import org.eclipse.osee.display.api.data.WebSearchParameters;

/**
 * @author John Misinco
 */
public class AtsSearchParameters extends WebSearchParameters {

   private final WebId build, program;

   public AtsSearchParameters(String searchString, boolean nameOnly, boolean verboseResults, WebId build, WebId program) {
      super(searchString, nameOnly, verboseResults);
      this.build = build;
      this.program = program;
   }

   public WebId getBuild() {
      return build;
   }

   public WebId getProgram() {
      return program;
   }

}
