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
import { ConnectionDropdownComponent } from './connection-dropdown.component';
import { ConnectionService } from '@osee/messaging/shared/services';
import { connectionServiceMock } from '@osee/messaging/shared/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

describe('ConnectionDropdownComponent', () => {
	let component: ConnectionDropdownComponent;
	let fixture: ComponentFixture<ConnectionDropdownComponent>;

	beforeEach(() => {
		TestBed.overrideComponent(ConnectionDropdownComponent, {
			set: {
				providers: [
					{
						provide: ConnectionService,
						useValue: connectionServiceMock,
					},
				],
			},
		}).configureTestingModule({
			imports: [ConnectionDropdownComponent, NoopAnimationsModule],
			providers: [
				{
					provide: ConnectionService,
					useValue: connectionServiceMock,
				},
			],
		});
		fixture = TestBed.createComponent(ConnectionDropdownComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
