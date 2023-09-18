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
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import {
	MessagingControlsMockComponent,
	connectionValidationResponseMock,
	validationServiceMock,
} from '@osee/messaging/shared/testing';

import { ReportsComponent } from './reports.component';
import { connectionSentinel } from '@osee/messaging/shared/types';
import { TestScheduler } from 'rxjs/testing';
import { ValidationService } from '@osee/messaging/shared/services';

describe('ReportsComponent', () => {
	let component: ReportsComponent;
	let fixture: ComponentFixture<ReportsComponent>;
	let scheduler: TestScheduler;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [
				RouterTestingModule,
				NoopAnimationsModule,
				MessagingControlsMockComponent,
				HttpClientTestingModule,
				ReportsComponent,
			],
			providers: [
				{ provide: ValidationService, useValue: validationServiceMock },
			],
		}).compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(ReportsComponent);
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

	it('should run connection validation', () => {
		scheduler.run(({ expectObservable, cold }) => {
			component.BranchId = '10';
			component.selectedConnection = { ...connectionSentinel, id: '1' };
			component.selectedApplic = { id: '1', name: 'Applic' };
			cold('-a').subscribe(() =>
				component.startConnectionValidation.next(true)
			);
			expectObservable(component.connectionValidationResults).toBe('-a', {
				a: connectionValidationResponseMock,
			});
		});
	});
});
