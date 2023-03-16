/*********************************************************************
 * Copyright (c) 2022 Boeing
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
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';

import { TestScheduler } from 'rxjs/testing';
import { connectionDiffsMock } from '@osee/messaging/shared/testing';

import { DiffReportTableComponent } from './diff-report-table.component';
import { connectionDiffHeaderDetails } from '../../table-headers/connection-diff-table-headers';
import type { connectionDiffItem } from '@osee/messaging/shared/types';

describe('DiffReportTableComponent', () => {
	let component: DiffReportTableComponent<connectionDiffItem>;
	let fixture: ComponentFixture<DiffReportTableComponent<connectionDiffItem>>;
	let scheduler: TestScheduler;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [MatIconModule, MatTableModule, DiffReportTableComponent],
		}).compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(
			DiffReportTableComponent<connectionDiffItem>
		);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	beforeEach(
		() =>
			(scheduler = new TestScheduler((actual, expected) => {
				expect(actual).toEqual(expected);
			}))
	);

	it('should create', () => {
		component.items = connectionDiffsMock;
		component.title = 'Connections Added';
		component.headers = ['name'];
		component.headerDetails = connectionDiffHeaderDetails;
		expect(component).toBeTruthy();
	});
});
