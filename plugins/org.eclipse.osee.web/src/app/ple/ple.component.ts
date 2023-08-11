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
import { Component } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
	selector: 'osee-ple-main',
	templateUrl: './ple.component.html',
	standalone: true,
	imports: [MatButtonModule],
})
export class PleComponent {
	constructor(
		private route: ActivatedRoute,
		private router: Router
	) {}

	navigateTo(location: string) {
		this.router.navigate([location], {
			relativeTo: this.route.parent,
			queryParamsHandling: 'merge',
		});
	}
}
export default PleComponent;
