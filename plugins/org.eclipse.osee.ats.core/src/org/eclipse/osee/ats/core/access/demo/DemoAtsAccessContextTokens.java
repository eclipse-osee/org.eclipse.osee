/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.access.demo;

import org.eclipse.osee.framework.core.data.AccessContextToken;

/**
 * @author Donald G. Dunne
 */
public final class DemoAtsAccessContextTokens {

   public static final AccessContextToken DEMO_DEFAULT = //
      AccessContextToken.valueOf(3968164675694830728L, "demo.default");
   public static final AccessContextToken DEMO_REQUIREMENT_CONTEXT = //
      AccessContextToken.valueOf(4322687303783459709L, "demo.requirement.context");
   public static final AccessContextToken DEMO_TEST_CONTEXT =
      AccessContextToken.valueOf(7389890235281156495L, "demo.test.context");
   public static final AccessContextToken DEMO_CODE_CONTEXT =
      AccessContextToken.valueOf(7242095630145694897L, "demo.code.context");
   public static final AccessContextToken DEMO_SW_DESIGN_CONTEXT =
      AccessContextToken.valueOf(4142714559854156433L, "demo.swdesign.context");
   public static final AccessContextToken DEMO_SUBSYSTEMS_CONTEXT =
      AccessContextToken.valueOf(6298053803615469854L, "demo.subsystems.context");
   public static final AccessContextToken DEMO_SYSTEMS_CONTEXT =
      AccessContextToken.valueOf(8756102828588705445L, "demo.systems.context");

   private DemoAtsAccessContextTokens() {
      //
   }

}
