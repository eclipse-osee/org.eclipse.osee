/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.integration.tests;

import org.eclipse.osee.framework.core.OseeApi;

/**
 * @author Donald G. Dunne
 */
public class OseeApiService {

   public static OseeApi oseeApi;

   public OseeApiService() {
      // for jax-rs
   }

   public OseeApi getOseeApi() {
      return oseeApi;
   }

   public void setOseeApi(OseeApi oseeApi) {
      OseeApiService.oseeApi = oseeApi;
   }

   public static OseeApi get() {
      return oseeApi;
   }

}
