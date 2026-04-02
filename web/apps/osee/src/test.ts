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
// This file is the global test setup file.

import 'zone.js/testing';
vi.mock('dagre', () => ({
	layout: vi.fn(),
}));
vi.spyOn(window, 'open').mockReturnValue(null);
