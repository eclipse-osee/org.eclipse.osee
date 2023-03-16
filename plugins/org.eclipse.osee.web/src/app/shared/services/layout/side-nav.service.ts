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
import { Injectable } from '@angular/core';
import { BehaviorSubject, ReplaySubject } from 'rxjs';
import { map } from 'rxjs/operators';
import { transactionToken } from '@osee/shared/types';
import { applic } from '@osee/shared/types/applicability';

@Injectable({
	providedIn: 'root',
})
export class SideNavService {
	private _rightSideNavContent = new ReplaySubject<{
		opened: boolean;
		field: string;
		currentValue: string | number | applic | boolean;
		previousValue?: string | number | applic | boolean;
		transaction?: transactionToken;
		user?: string;
		date?: string;
	}>();
	private _leftSideNavContent = new BehaviorSubject<{
		opened: boolean;
		icon: 'close' | 'menu';
	}>({
		opened: false,
		icon: 'menu',
	});
	private _leftSideNavContent$ = this._leftSideNavContent.asObservable();
	constructor() {}

	get rightSideNavContent() {
		return this._rightSideNavContent;
	}
	set rightSideNav(value: {
		opened: boolean;
		field: string;
		currentValue: string | number | applic | boolean;
		previousValue?: string | number | applic | boolean;
		transaction?: transactionToken;
		user?: string;
		date?: string;
	}) {
		this._rightSideNavContent.next(value);
	}

	get rightSideNavOpened() {
		return this.rightSideNavContent.pipe(map((val) => val.opened));
	}

	set toggleLeftSideNav(value: unknown) {
		const currentState = this._leftSideNavContent.getValue();
		this.leftSideNavState = { opened: !currentState.opened };
	}

	set leftSideNavState(value: { opened: boolean }) {
		this._leftSideNavContent.next({
			opened: value.opened,
			icon: value.opened ? 'close' : 'menu',
		});
	}

	set openLeftSideNav(value: unknown) {
		this.leftSideNavState = { opened: true };
	}

	set closeLeftSideNav(value: unknown) {
		this.leftSideNavState = { opened: false };
	}

	get leftSideNav() {
		return this._leftSideNavContent$;
	}
}
