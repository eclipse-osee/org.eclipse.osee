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
import { DiffHeaderType } from '@osee/messaging/shared';
import { TestScheduler } from 'rxjs/testing';
import { connectionDiffsMock } from 'src/app/ple-services/http/difference-report-connections.mock';

import { DiffReportTableComponent } from './diff-report-table.component';

describe('DiffReportTableComponent', () => {
	let component: DiffReportTableComponent;
	let fixture: ComponentFixture<DiffReportTableComponent>;
	let scheduler: TestScheduler;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [MatIconModule, MatTableModule, DiffReportTableComponent],
			declarations: [],
		}).compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(DiffReportTableComponent);
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
		component.headerKey = DiffHeaderType.CONNECTION;
		expect(component).toBeTruthy();
	});

	it('should get the header name', () => {
		scheduler.run(() => {
			component.headerKey = DiffHeaderType.ELEMENT;
			let expectedObservable = {
				a: {
					header: 'name',
					description: 'Name of element',
					humanReadable: 'Name',
				},
			};
			let expectedMarble = '(a)';
			scheduler
				.expectObservable(component.getHeaderByName('name'))
				.toBe(expectedMarble, expectedObservable);
		});
	});
});
