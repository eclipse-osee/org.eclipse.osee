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
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import {
	connectionServiceMock,
	MessagingControlsMockComponent,
	ReportsServiceMock,
	validationServiceMock,
} from '@osee/messaging/shared/testing';

import { ReportsComponent } from './reports.component';
import {
	ConnectionService,
	ReportsService,
	ValidationService,
} from '@osee/messaging/shared/services';
import { MessagingControlsComponent } from '@osee/messaging/shared/main-content';
import { provideRouter } from '@angular/router';

describe('ReportsComponent', () => {
	let component: ReportsComponent;
	let fixture: ComponentFixture<ReportsComponent>;

	beforeEach(async () => {
		await TestBed.overrideComponent(ReportsComponent, {
			add: {
				imports: [MessagingControlsMockComponent],
			},
			remove: {
				imports: [MessagingControlsComponent],
			},
		})
			.configureTestingModule({
				imports: [ReportsComponent],
				providers: [
					provideNoopAnimations(),
					provideRouter([]),
					{
						provide: ValidationService,
						useValue: validationServiceMock,
					},
					{
						provide: ReportsService,
						useValue: ReportsServiceMock,
					},
					{
						provide: ConnectionService,
						useValue: connectionServiceMock,
					},
				],
			})
			.compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(ReportsComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
