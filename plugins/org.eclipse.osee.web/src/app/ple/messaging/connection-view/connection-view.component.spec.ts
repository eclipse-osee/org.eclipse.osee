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
import { graphServiceMock } from './lib/testing/current-graph.service.mock';
import { CurrentGraphService } from './lib/services/current-graph.service';
import { MockHostComponent } from './lib/testing/host.component.mock';
import { MatDialogModule } from '@angular/material/dialog';

describe('ConnectionViewComponent', () => {
	let component: ConnectionViewComponent;
	let fixture: ComponentFixture<ConnectionViewComponent>;

	beforeEach(async () => {
		await TestBed.overrideComponent(ConnectionViewComponent, {
			set: {
				imports: [MockHostComponent],
			},
		})
			.configureTestingModule({
				imports: [
					RouterTestingModule,
					MatDialogModule,
					MockHostComponent,
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
