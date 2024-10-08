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

@Component({
	selector: 'osee-ci-dashboard-controls',
	template: '<p>Dummy</p>',
	standalone: true,
})
export class CiDashboardControlsMockComponent {
	@Input() branchPicker: boolean = true;
	@Input() ciSetSelector: boolean = true;
	@Input() actionButton: boolean = false;
}
