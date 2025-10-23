/*********************************************************************
 * Copyright (c) 2025 Boeing
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

import { RouterTestingModule } from '@angular/router/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import ActraWorldComponent from './actra-world.component';
import { ActraWorldHttpService } from '../services/actra-world-http.service';
import { ActraWorldHttpServiceMock } from '../services/actra-world-http.service.mock';
import { CreateActionService } from '@osee/configuration-management/services';
import { createActionServiceMock } from '@osee/configuration-management/testing';

describe('ActraWorldComponent', () => {
	let component: ActraWorldComponent;
	let fixture: ComponentFixture<ActraWorldComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [
				ActraWorldComponent,
				RouterTestingModule,
				NoopAnimationsModule,
			],
			providers: [
				{
					provide: ActraWorldHttpService,
					useValue: ActraWorldHttpServiceMock,
				},
				{
					provide: CreateActionService,
					useValue: createActionServiceMock,
				},
			],
		}).compileComponents();

		fixture = TestBed.createComponent(ActraWorldComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
