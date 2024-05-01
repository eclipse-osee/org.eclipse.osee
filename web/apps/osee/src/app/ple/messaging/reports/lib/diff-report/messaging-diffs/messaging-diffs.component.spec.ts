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
import { DiffReportService } from '@osee/messaging/shared/services';
import {
	DiffReportServiceMock,
	mimChangeSummaryMock,
} from '@osee/messaging/shared/testing';
import { DiffReportTableComponent } from '../diff-report-table/diff-report-table.component';
import { MessagingDiffsComponent } from './messaging-diffs.component';

describe('MessagingDiffsComponent', () => {
	let component: MessagingDiffsComponent;
	let fixture: ComponentFixture<MessagingDiffsComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			providers: [
				{ provide: DiffReportService, useValue: DiffReportServiceMock },
			],
			imports: [
				MatIconModule,
				MatTableModule,
				DiffReportTableComponent,
				MessagingDiffsComponent,
			],
		}).compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(MessagingDiffsComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create node diffs', () => {
		component.allItems.next(Object.values(mimChangeSummaryMock.nodes));
		expect(component).toBeTruthy();
	});
	it('should create connection diffs', () => {
		component.allItems.next(
			Object.values(mimChangeSummaryMock.connections)
		);
		expect(component).toBeTruthy();
	});
	it('should create message diffs', () => {
		component.allItems.next(Object.values(mimChangeSummaryMock.messages));
		expect(component).toBeTruthy();
	});
	it('should create submessage diffs', () => {
		component.allItems.next(
			Object.values(mimChangeSummaryMock.subMessages)
		);
		expect(component).toBeTruthy();
	});
});
