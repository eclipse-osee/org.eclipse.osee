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
export const issuer = 'https://${yourOktaDomain}/oauth2/default';
export const clientId = '${yourClientID}';
export const redirectUri = window.location.origin + '/login/callback';
export const postLogoutRedirectUri = '';
export const pkce = true;
export const responseType = 'code';
