/*********************************************************************
 * Copyright (c) 2021 Boeing
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
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { HttpTimeoutExtensionInterceptor } from './http-timeout-extension.interceptor';
import { OSEEAuthHeaderInterceptor } from './oseeauth-header.interceptor';
import { PlConfigSetLoadingIndicatorInterceptor } from './plconfig-set-loading-indicator-interceptor';


/** Http interceptor providers in outside-in order */
export const httpInterceptorProviders = [
  { provide: HTTP_INTERCEPTORS, useClass: PlConfigSetLoadingIndicatorInterceptor, multi: true },
  { provide: HTTP_INTERCEPTORS, useClass: HttpTimeoutExtensionInterceptor, multi: true },
  { provide: HTTP_INTERCEPTORS, useClass: OSEEAuthHeaderInterceptor, multi:true}
];