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
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Params } from '@angular/router';
import {
	BranchPickerComponent,
	ActionDropDownComponent,
} from '@osee/shared/components';
import { map } from 'rxjs/operators';
import { RouterStateService } from './lib/services/router-state.service';
import { ElementTableComponent } from './lib/tables/element-table/element-table.component';

@Component({
	selector: 'osee-typesearch-type-element-search',
	templateUrl: './type-element-search.component.html',
	styleUrls: ['./type-element-search.component.sass'],
	standalone: true,
	imports: [
		BranchPickerComponent,
		ActionDropDownComponent,
		ElementTableComponent,
	],
})
export class TypeElementSearchComponent implements OnInit {
	constructor(
		private route: ActivatedRoute,
		private routerState: RouterStateService
	) {}

	ngOnInit(): void {
		this.route.params
			.pipe(
				map((params: Params) => {
					if (Number(params['branchId']) > 0) {
						this.routerState.id = params['branchId'];
					}
					if (
						params['branchType'] === 'baseline' ||
						params['branchType'] === 'working'
					) {
						this.routerState.type = params['branchType'];
					}
				})
			)
			.subscribe();
	}
}
export default TypeElementSearchComponent;
