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
const auth_types = ['NONE', 'DEV', 'FORCED_SSO', 'DEMO', 'OKTA'];

export type AUTH_TYPE = (typeof auth_types)[keyof typeof auth_types];
