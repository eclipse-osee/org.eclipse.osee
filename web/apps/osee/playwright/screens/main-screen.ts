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
import { ApplicationScreen } from './screen.js';

export class MainScreen extends ApplicationScreen {
	// The path to this screen, relative to the base URL of the
	// application
	static path = '/';

	title = this.locatorFor('h1, h2, h3, h4, h5, h6');
}
