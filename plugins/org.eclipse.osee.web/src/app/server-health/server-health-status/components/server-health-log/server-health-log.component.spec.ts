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

import { ServerHealthLogComponent } from './server-health-log.component';
import { ServerHealthHttpService } from '../../../../server-health/shared/services/server-health-http.service';
import { ServerHealthHttpServiceMock } from '../../../../server-health/shared/testing/server-health-http.service.mock';
import { ScrollingModule } from '@angular/cdk/scrolling';

describe('ServerHealthLogComponent', () => {
	let component: ServerHealthLogComponent;
	let fixture: ComponentFixture<ServerHealthLogComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [ServerHealthLogComponent, ScrollingModule],
			providers: [
				{
					provide: ServerHealthHttpService,
					useValue: ServerHealthHttpServiceMock,
				},
			],
		}).compileComponents();

		fixture = TestBed.createComponent(ServerHealthLogComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		fixture.detectChanges();
		expect(component).toBeTruthy();
	});
});
