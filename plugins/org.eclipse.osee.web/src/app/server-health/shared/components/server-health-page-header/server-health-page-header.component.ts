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
import { Component, Input } from '@angular/core';
import { MatIcon } from '@angular/material/icon';

@Component({
	selector: 'osee-server-health-page-header',
	standalone: true,
	imports: [MatIcon],
	templateUrl: './server-health-page-header.component.html',
})
export class ServerHealthPageHeaderComponent {
	@Input() icon: string = '';
	@Input() name: string = '';
	@Input() description: string = '';
}
