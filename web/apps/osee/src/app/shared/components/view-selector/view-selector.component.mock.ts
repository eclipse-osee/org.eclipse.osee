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

import { Component, model } from '@angular/core';
import { applic } from '@osee/applicability/types';

/**
 * Component utilized strictly for stubbing out functionality in tests
 */
@Component({
	selector: 'osee-view-selector',
	template: `<div></div>`,
	standalone: true,
	imports: [],
})
export class MockViewSelectorComponent {
	public view = model.required<applic>();
}
