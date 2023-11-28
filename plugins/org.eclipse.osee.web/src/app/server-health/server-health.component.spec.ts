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

import { ServerHealthComponent } from './server-health.component';
import { RouterTestingModule } from '@angular/router/testing';
import { ServerHealthHttpService } from './shared/services/server-health-http.service';
import { ServerHealthHttpServiceMock } from './shared/testing/server-health-http.service.mock';

describe('ServerHealthComponent', () => {
	let component: ServerHealthComponent;
	let fixture: ComponentFixture<ServerHealthComponent>;

	beforeEach(() => {
		TestBed.configureTestingModule({
			imports: [ServerHealthComponent, RouterTestingModule],
			providers: [
				{
					provide: ServerHealthHttpService,
					useValue: ServerHealthHttpServiceMock,
				},
			],
		});
		fixture = TestBed.createComponent(ServerHealthComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
