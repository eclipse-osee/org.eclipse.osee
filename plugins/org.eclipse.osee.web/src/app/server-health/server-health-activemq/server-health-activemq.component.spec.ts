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

import { ServerHealthActivemqComponent } from './server-health-activemq.component';
import { ServerHealthHttpService } from '../shared/services/server-health-http.service';
import { ServerHealthHttpServiceMock } from '../shared/testing/server-health-http.service.mock';

describe('ServerHealthActivemqComponent', () => {
	let component: ServerHealthActivemqComponent;
	let fixture: ComponentFixture<ServerHealthActivemqComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [ServerHealthActivemqComponent],
			providers: [
				{
					provide: ServerHealthHttpService,
					useValue: ServerHealthHttpServiceMock,
				},
			],
		}).compileComponents();

		fixture = TestBed.createComponent(ServerHealthActivemqComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
