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

import { ServerHealthBalancersComponent } from './server-health-balancers.component';
import { ServerHealthHttpService } from '../shared/services/server-health-http.service';
import { ServerHealthHttpServiceMock } from '../shared/testing/server-health-http.service.mock';

describe('ServerHealthBalancersComponent', () => {
	let component: ServerHealthBalancersComponent;
	let fixture: ComponentFixture<ServerHealthBalancersComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [ServerHealthBalancersComponent],
			providers: [
				{
					provide: ServerHealthHttpService,
					useValue: ServerHealthHttpServiceMock,
				},
			],
		}).compileComponents();

		fixture = TestBed.createComponent(ServerHealthBalancersComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
