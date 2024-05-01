/*********************************************************************
 * Copyright (c) 2021 Boeing
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

import { ConnectionViewComponent } from './connection-view.component';
import { MatDialogModule } from '@angular/material/dialog';
import { CurrentGraphService } from '@osee/messaging/connection-view';
import {
	MockConnectionsComponent,
	graphServiceMock,
} from '@osee/messaging/connection-view/testing';

describe('ConnectionViewComponent', () => {
	let component: ConnectionViewComponent;
	let fixture: ComponentFixture<ConnectionViewComponent>;

	beforeEach(async () => {
		await TestBed.overrideComponent(ConnectionViewComponent, {
			set: {
				imports: [MockConnectionsComponent],
			},
		})
			.configureTestingModule({
				imports: [
					RouterTestingModule,
					MatDialogModule,
					MockConnectionsComponent,
				],
				providers: [
					{
						provide: CurrentGraphService,
						useValue: graphServiceMock,
					},
				],
				declarations: [],
			})
			.compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(ConnectionViewComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
