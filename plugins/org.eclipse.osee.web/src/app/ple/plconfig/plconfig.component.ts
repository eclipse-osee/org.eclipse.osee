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
import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { combineLatest, from, iif, Observable, of, Subject } from 'rxjs';
import {
	concatMap,
	filter,
	map,
	switchMap,
	takeUntil,
	tap,
	reduce,
} from 'rxjs/operators';
import { ActionStateButtonService } from '@osee/shared/components';
import { PlConfigUIStateService } from './lib/services/pl-config-uistate.service';

@Component({
	selector: 'osee-plconfig',
	templateUrl: './plconfig.component.html',
	styleUrls: ['./plconfig.component.sass'],
})
export class PlconfigComponent implements OnInit, OnDestroy {
	_updateRequired: Observable<boolean> = this.uiStateService.updateReq;
	_branchType: string = '';
	branchType = this.uiStateService.viewBranchType;
	branchId = this.uiStateService.branchId;
	private _done = new Subject();
	isTeamLead = this.actionStateService.isTeamLead;
	isAllowedToDiff = combineLatest([
		this.uiStateService.viewBranchType,
		this.uiStateService.branchId,
		this.uiStateService.isInDiff,
	]).pipe(
		//invalid conditions equals false
		switchMap(([branchType, branchId, inDiff]) =>
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
	constructor(
		private uiStateService: PlConfigUIStateService,
		private route: ActivatedRoute,
		private actionStateService: ActionStateButtonService //kinda hacky, but best place for it to live for now
	) {
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
				tap(([paramMap, data]) => {
					this.uiStateService.viewBranchTypeString =
						paramMap.get('branchType') || '';
					this.uiStateService.branchIdNum =
						paramMap.get('branchId') || '';
				}),
				switchMap(([paramMap, data]) =>
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
