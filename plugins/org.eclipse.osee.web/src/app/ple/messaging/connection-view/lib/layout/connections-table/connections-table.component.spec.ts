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
import { CommonModule } from '@angular/common';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { graphServiceMock } from '@osee/messaging/connection-view/testing';
import { ConnectionService } from '@osee/messaging/shared/services';
import { connectionServiceMock } from '@osee/messaging/shared/testing';
import { CurrentGraphService } from 'src/app/ple/messaging/connection-view/lib/public-api';
import { ConnectionsTableComponent } from './connections-table.component';

describe('ConnectionsTableComponent', () => {
	let component: ConnectionsTableComponent;
	let fixture: ComponentFixture<ConnectionsTableComponent>;

	beforeEach(async () => {
		await TestBed.overrideComponent(ConnectionsTableComponent, {
			set: {
				imports: [
					CommonModule,
					ConnectionsTableComponent,
					MatTableModule,
					MatFormFieldModule,
					MatInputModule,
					MatIconModule,
					MatTooltipModule,
				],
			},
		})
			.configureTestingModule({
				imports: [
					CommonModule,
					ConnectionsTableComponent,
					MatTableModule,
					MatFormFieldModule,
					MatInputModule,
					MatIconModule,
					MatTooltipModule,
					NoopAnimationsModule,
				],
				providers: [
					{
						provide: CurrentGraphService,
						useValue: graphServiceMock,
					},
					{
						provide: ConnectionService,
						useValue: connectionServiceMock,
					},
				],
			})
			.compileComponents();

		fixture = TestBed.createComponent(ConnectionsTableComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
