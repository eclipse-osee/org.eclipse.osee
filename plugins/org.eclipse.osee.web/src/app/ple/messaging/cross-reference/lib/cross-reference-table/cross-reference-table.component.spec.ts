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
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { CrossReferenceService } from '@osee/messaging/shared';
import { CrossReferenceServiceMock } from '@osee/messaging/shared/testing';

import { CrossReferenceTableComponent } from './cross-reference-table.component';

describe('CrossReferenceTableComponent', () => {
	let component: CrossReferenceTableComponent;
	let fixture: ComponentFixture<CrossReferenceTableComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [CrossReferenceTableComponent, NoopAnimationsModule],
			providers: [
				{
					provide: CrossReferenceService,
					useValue: CrossReferenceServiceMock,
				},
			],
		}).compileComponents();

		fixture = TestBed.createComponent(CrossReferenceTableComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
