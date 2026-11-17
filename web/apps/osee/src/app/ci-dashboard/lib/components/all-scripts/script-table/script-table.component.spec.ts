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
import { WritableSignal } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router, UrlTree } from '@angular/router';
import { ScriptTableComponent } from './script-table.component';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { CiDetailsTableService } from '../../../services/ci-details-table.service';
import { UserDataAccountService } from '../../../../../auth/user-data-account.service';
import { userDataAccountServiceMock } from '../../../../../auth/user-data-account.service.mock';

import {
	SubsystemSelectorMockComponent,
	TeamSelectorMockComponent,
	ciDetailsServiceMock,
} from '@osee/ci-dashboard/testing';
import { AsyncPipe } from '@angular/common';
import {
	MatCell,
	MatCellDef,
	MatColumnDef,
	MatHeaderCell,
	MatHeaderCellDef,
	MatHeaderRow,
	MatHeaderRowDef,
	MatRow,
	MatRowDef,
	MatTable,
} from '@angular/material/table';
import { MatTooltip } from '@angular/material/tooltip';
import {
	MatFormField,
	MatLabel,
	MatPrefix,
} from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatIcon } from '@angular/material/icon';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort, MatSortHeader } from '@angular/material/sort';
import { FormsModule } from '@angular/forms';

describe('ScriptTableComponent', () => {
	let component: ScriptTableComponent;
	let fixture: ComponentFixture<ScriptTableComponent>;

	beforeEach(() => {
		TestBed.overrideComponent(ScriptTableComponent, {
			set: {
				imports: [
					SubsystemSelectorMockComponent,
					TeamSelectorMockComponent,
					AsyncPipe,
					FormsModule,
					MatTable,
					MatColumnDef,
					MatHeaderCell,
					MatHeaderCellDef,
					MatTooltip,
					MatCell,
					MatCellDef,
					MatHeaderRow,
					MatHeaderRowDef,
					MatRow,
					MatRowDef,
					MatFormField,
					MatLabel,
					MatInput,
					MatIcon,
					MatPrefix,
					MatPaginator,
					MatSort,
					MatSortHeader,
				],
			},
		}).configureTestingModule({
			imports: [ScriptTableComponent],
			providers: [
				provideNoopAnimations(),
				{
					provide: CiDetailsTableService,
					useValue: ciDetailsServiceMock,
				},
				{
					provide: UserDataAccountService,
					useValue: userDataAccountServiceMock,
				},
			],
		});
		fixture = TestBed.createComponent(ScriptTableComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});

	describe('filter navigation', () => {
		let router: Router;
		let navigate: ReturnType<typeof vi.spyOn>;
		const filter =
			ciDetailsServiceMock.currentDefFilter as WritableSignal<string>;

		beforeEach(() => {
			router = TestBed.inject(Router);
			navigate = vi.spyOn(router, 'navigateByUrl');
		});

		afterEach(() => {
			filter.set('');
		});

		it('should not navigate when the URL filter already matches', () => {
			vi.spyOn(router, 'url', 'get').mockReturnValue('/ci/allScripts');
			filter.set('');
			TestBed.tick();

			expect(navigate).not.toHaveBeenCalled();
		});

		it('should replace the URL when the filter changes', () => {
			vi.spyOn(router, 'url', 'get').mockReturnValue('/ci/allScripts');
			filter.set('abc');
			TestBed.tick();

			expect(navigate).toHaveBeenCalledOnce();
			const [tree, extras] = navigate.mock.calls[0] as [
				UrlTree,
				{ replaceUrl: boolean },
			];
			expect(tree.queryParams['filter']).toBe('abc');
			expect(extras).toEqual({ replaceUrl: true });
		});

		it('should drop the filter param when the filter is cleared', () => {
			vi.spyOn(router, 'url', 'get').mockReturnValue(
				'/ci/allScripts?filter=abc'
			);
			filter.set('abc');
			TestBed.tick();
			filter.set('');
			TestBed.tick();

			const [tree] = navigate.mock.calls.at(-1) as [UrlTree];
			expect(tree.queryParams['filter']).toBeUndefined();
		});
	});
});
