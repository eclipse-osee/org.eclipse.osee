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
import { ComponentHarness } from '@angular/cdk/testing';

export class ApplicationScreen extends ComponentHarness {
	// Selector to the application's root element
	static hostSelector = 'osee-root';

	// This would be a great place for some application-wide utilities,
	// for example access to modals or a log-out button
}
