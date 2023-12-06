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

import { ServerHealthStatusComponent } from './server-health-status.component';
import { ServerHealthHttpService } from '../shared/services/server-health-http.service';
import { ServerHealthHttpServiceMock } from '../shared/testing/server-health-http.service.mock';

describe('ServerHealthStatusComponent', () => {
	let component: ServerHealthStatusComponent;
	let fixture: ComponentFixture<ServerHealthStatusComponent>;

	beforeEach(() => {
		TestBed.configureTestingModule({
			imports: [ServerHealthStatusComponent],
			providers: [
				{
					provide: ServerHealthHttpService,
					useValue: ServerHealthHttpServiceMock,
				},
			],
		});
		fixture = TestBed.createComponent(ServerHealthStatusComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
