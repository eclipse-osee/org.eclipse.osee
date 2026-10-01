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
// Second local web instance for multi-server testing. Must match the dev-server port used by the
// `demo_local_debug_server2` serve configuration (4201). Requests hit this origin and are proxied
// to the second application server (8090) by `proxy.conf.server2.json`.
export const apiURL = 'http://localhost:4201';
