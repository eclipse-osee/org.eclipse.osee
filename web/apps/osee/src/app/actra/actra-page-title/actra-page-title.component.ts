/*********************************************************************
 * Copyright (c) 2025 Boeing
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
import { Component, input } from '@angular/core';
import { MatIcon } from '@angular/material/icon';

@Component({
	selector: 'osee-actra-page-title',
	imports: [MatIcon],
	templateUrl: './actra-page-title.component.html',
})
export class ActraPageTitleComponent {
	icon = input.required<string>();
	title = input.required<string>();
}
export default ActraPageTitleComponent;
