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

import { ServerHealthTopComponent } from './server-health-top.component';
import { ServerHealthHttpService } from 'src/app/server-health/shared/services/server-health-http.service';
import { ServerHealthHttpServiceMock } from 'src/app/server-health/shared/testing/server-health-http.service.mock';

describe('ServerHealthTopComponent', () => {
	let component: ServerHealthTopComponent;
	let fixture: ComponentFixture<ServerHealthTopComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [ServerHealthTopComponent],
			providers: [
				{
					provide: ServerHealthHttpService,
					useValue: ServerHealthHttpServiceMock,
				},
			],
		}).compileComponents();

		fixture = TestBed.createComponent(ServerHealthTopComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
