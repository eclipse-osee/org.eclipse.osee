/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.demo.api;

/**
 * @author Donald G. Dunne
 */
public class DemoWorkflowTitles {

   private DemoWorkflowTitles() {
      // utility class
   }

   /**
    * Do NOT use these as ways to load Demo workflows by Name.  Use DemoUtil instead.
    */
   public static final String SAW_COMMITTED_REQT_CHANGES_FOR_DIAGRAM_VIEW =
      "SAW (committed) Reqt Changes for Diagram View";
   public static final String SAW_UNCOMMITTED_REQT_CHANGES_FOR_DIAGRAM_VIEW =
      "SAW (uncommitted) More Reqt Changes for Diagram View";
   public static final String SAW_UNCOMMITTED_CONFLICTED_REQT_CHANGES_FOR_DIAGRAM_VIEW =
      "SAW (uncommitted-conflicted) More Requirement Changes for Diagram View";
   public static final String SAW_NO_BRANCH_REQT_CHANGES_FOR_DIAGRAM_VIEW =
      "SAW (no-branch) Even More Requirement Changes for Diagram View";

}
