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

import { ServerHealthJavaComponent } from './server-health-java.component';
import { ServerHealthHttpService } from 'src/app/server-health/shared/services/server-health-http.service';
import { ServerHealthHttpServiceMock } from 'src/app/server-health/shared/testing/server-health-http.service.mock';

describe('ServerHealthJavaComponent', () => {
	let component: ServerHealthJavaComponent;
	let fixture: ComponentFixture<ServerHealthJavaComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [ServerHealthJavaComponent],
			providers: [
				{
					provide: ServerHealthHttpService,
					useValue: ServerHealthHttpServiceMock,
				},
			],
		}).compileComponents();

		fixture = TestBed.createComponent(ServerHealthJavaComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
