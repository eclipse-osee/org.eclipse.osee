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
import { AsyncPipe } from '@angular/common';
import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { MatButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { CurrentActionDropDownComponent } from '@osee/configuration-management/components';
import {
	ApplicabilityTableComponent,
	EditDefinitionsDropdownComponent,
	PlConfigUIStateService,
} from '@osee/plconfig';
import {
	BranchPickerComponent,
	CurrentViewSelectorComponent,
} from '@osee/shared/components';
import { Observable, Subject, combineLatest, iif, of } from 'rxjs';
import { filter, map, switchMap, takeUntil, tap } from 'rxjs/operators';

@Component({
	selector: 'osee-plconfig',
	templateUrl: './plconfig.component.html',
	imports: [
		AsyncPipe,
		RouterLink,
		MatButton,
		MatIcon,
		BranchPickerComponent,
		CurrentActionDropDownComponent,
		EditDefinitionsDropdownComponent,
		ApplicabilityTableComponent,
		CurrentViewSelectorComponent,
	],
	styles: [
		':host{ height: 94vh; min-height: calc(94vh - 10%); max-height: 94vh; width: 100vw; min-width: calc(100vw - 10%); display: inline-block;}',
	],
})
export class PlconfigComponent implements OnInit, OnDestroy {
	private uiStateService = inject(PlConfigUIStateService);
	private route = inject(ActivatedRoute);

	_updateRequired: Observable<boolean> = this.uiStateService.updateReq;
	_branchType = '';
	branchType = this.uiStateService.viewBranchType;
	branchId = this.uiStateService.branchId;
	private _done = new Subject();
	isAllowedToDiff = combineLatest([
		this.uiStateService.viewBranchType,
		this.uiStateService.branchId,
		this.uiStateService.isInDiff,
	]).pipe(
		//invalid conditions equals false
		switchMap(([_branchType, branchId, inDiff]) =>
			iif(
				() =>
					inDiff === false &&
					branchId.length !== 0 &&
					branchId !== '-1' &&
					branchId !== undefined,
				of('true'),
				of('false')
			)
		)
	);
	diff = './diff';
	currentRoute = this.route;

	/** Inserted by Angular inject() migration for backwards compatibility */
	constructor(...args: unknown[]);
	constructor() {
		this.uiStateService.branchIdNum = '';
		this.uiStateService.viewBranchTypeString = '';
		this.uiStateService.viewBranchType.subscribe((id) => {
			this._branchType = id;
		});
	}
	ngOnDestroy(): void {
		this._done.next('');
		this._done.complete();
	}

	ngOnInit(): void {
		combineLatest([
			this.route.paramMap,
			of(this.route).pipe(
				switchMap((route) => {
					while (route.firstChild) {
						route = route.firstChild;
					}
					return of(route);
				}),
				filter((activatedRoute) => activatedRoute.outlet === 'primary'),
				switchMap((route) => route.data)
			),
		])
			.pipe(
				tap(([paramMap, _data]) => {
					this.uiStateService.viewBranchTypeString =
						(paramMap.get('branchType') as
							| 'working'
							| 'baseline'
							| '') || '';
					this.uiStateService.branchIdNum =
						paramMap.get('branchId') || '';
				}),
				switchMap(([_paramMap, data]) =>
					iif(
						() => data.diff !== undefined,
						of(data).pipe(
							map((data) => {
								this.uiStateService.difference = data.diff;
								this.uiStateService.updateReqConfig = true;
								return data.diff;
							})
						),
						of(data).pipe(
							map((data) => {
								this.uiStateService.diffMode = false;
								this.uiStateService.difference = [];
								this.uiStateService.updateReqConfig = true;
								return data;
							})
						)
					)
				),
				takeUntil(this._done)
			)
			.subscribe();
	}
}
export default PlconfigComponent;
