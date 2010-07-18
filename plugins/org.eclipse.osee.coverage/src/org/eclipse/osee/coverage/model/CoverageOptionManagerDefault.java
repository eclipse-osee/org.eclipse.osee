/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.coverage.model;

import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;

/**
 * @author Donald G. Dunne
 */
public class CoverageOptionManagerDefault extends CoverageOptionManager {

   public static List<CoverageOption> defaultOptions =
         Arrays.asList(Deactivated_Code, Dead_Code, Exception_Handling, Test_Unit, Not_Covered);
   private static CoverageOptionManagerDefault instance = new CoverageOptionManagerDefault();

   private CoverageOptionManagerDefault() {
      super(defaultOptions);
   }

   public static CoverageOptionManagerDefault instance() {
      return instance;
   }

   @Override
   public void add(CoverageOption coverageOption) throws OseeArgumentException {
      throw new OseeArgumentException("Not supported for CoverageOptionManagerDefault");
   }

}
