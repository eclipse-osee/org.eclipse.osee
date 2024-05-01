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
import { CommonModule } from '@angular/common';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { TestScheduler } from 'rxjs/testing';
import { DiffReportTableComponent } from './diff-report-table/diff-report-table.component';
import { DiffReportComponent } from './diff-report.component';
import { DiffReportService } from '@osee/messaging/shared/services';
import { DiffReportServiceMock } from '@osee/messaging/shared/testing';
import { MessagingDiffsComponent } from './messaging-diffs/messaging-diffs.component';

describe('DiffReportComponent', () => {
	let component: DiffReportComponent;
	let fixture: ComponentFixture<DiffReportComponent>;
	let scheduler: TestScheduler;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			providers: [
				{ provide: DiffReportService, useValue: DiffReportServiceMock },
			],
			imports: [
				CommonModule,
				MatIconModule,
				MatTableModule,
				DiffReportTableComponent,
				MessagingDiffsComponent,
				DiffReportComponent,
			],
		}).compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(DiffReportComponent);
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
		expect(component).toBeTruthy();
	});

	it('should get the header name', () => {
		scheduler.run(() => {
			let expectedObservable = {
				a: {
					header: 'description',
					description: 'Description of the branch',
					humanReadable: 'Description',
				},
			};
			let expectedMarble = '(a)';
			scheduler
				.expectObservable(
					component.getHeaderByName('description', 'branchSummary')
				)
				.toBe(expectedMarble, expectedObservable);
		});
	});
});
