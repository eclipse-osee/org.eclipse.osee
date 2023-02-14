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
import { HttpInterceptorFn } from '@angular/common/http';
import { HttpTimeoutExtensionInterceptor } from './http-timeout-extension.interceptor';
import { LoadingIndicatorInterceptor } from './loading-indicator.interceptor';
import { OseeAuthInterceptor } from './osee-auth-header.interceptor';

export const GlobalHttpInterceptors: HttpInterceptorFn[] = [
	LoadingIndicatorInterceptor,
	HttpTimeoutExtensionInterceptor,
	OseeAuthInterceptor,
];
