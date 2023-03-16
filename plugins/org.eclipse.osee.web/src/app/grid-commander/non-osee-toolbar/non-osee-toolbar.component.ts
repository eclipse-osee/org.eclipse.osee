/*********************************************************************
 * Copyright (c) 2023 Boeing
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
import { Component } from '@angular/core';
import { ToolbarComponent } from '@osee/toolbar';

@Component({
	selector: 'osee-non-osee-toolbar',
	standalone: true,
	template: `<osee-toolbar [oseeToolbar]="false"></osee-toolbar>`,
	imports: [ToolbarComponent],
})
export class NonOseeToolbarComponent {
	oseeToolbar = false;
	constructor() {}
}
export default NonOseeToolbarComponent;
