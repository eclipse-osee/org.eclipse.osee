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
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule } from '@angular/material/dialog';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { RouteStateService } from '../../services/route-state-service.service';
import { MockGraphComponent } from '../../testing/graph.component.mock';
import { HarnessLoader } from '@angular/cdk/testing';
import { TestbedHarnessEnvironment } from '@angular/cdk/testing/testbed';

import { ConnectionsComponent } from './connections.component';
import { CurrentGraphService } from '../../services/current-graph.service';
import { graphServiceMock } from '../../testing/current-graph.service.mock';
import { MatSidenavModule } from '@angular/material/sidenav';
import { RouterTestingModule } from '@angular/router/testing';
import { MatIconModule } from '@angular/material/icon';
import { NgIf, AsyncPipe } from '@angular/common';
import { EditAuthService } from '@osee/messaging/shared/services';
import {
	editAuthServiceMock,
	MessagingControlsMockComponent,
	ViewSelectorMockComponent,
} from '@osee/messaging/shared/testing';
import { MockSingleDiffComponent } from '@osee/shared/testing';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MockConnectionsTableComponent } from '@osee/messaging/connection-view/testing';

describe('HostComponent', () => {
	let component: ConnectionsComponent;
	let routeState: RouteStateService;
	let loader: HarnessLoader;
	let fixture: ComponentFixture<ConnectionsComponent>;

	beforeEach(async () => {
		await TestBed.overrideComponent(ConnectionsComponent, {
			set: {
				imports: [
					MatDialogModule,
					MatIconModule,
					MatButtonModule,
					MatButtonToggleModule,
					MatSidenavModule,
					RouterTestingModule,
					NgIf,
					AsyncPipe,
					MockSingleDiffComponent,
					MockGraphComponent,
					MockConnectionsTableComponent,
					ViewSelectorMockComponent,
					MessagingControlsMockComponent,
				],
			},
		})
			.configureTestingModule({
				imports: [
					MatDialogModule,
					MatIconModule,
					MatButtonModule,
					MatButtonToggleModule,
					MatSidenavModule,
					RouterTestingModule,
					NoopAnimationsModule,
					MockSingleDiffComponent,
					ConnectionsComponent,
					MockGraphComponent,
					MockConnectionsTableComponent,
					ViewSelectorMockComponent,
					MessagingControlsMockComponent,
				],
				providers: [
					{ provide: EditAuthService, useValue: editAuthServiceMock },
					{
						provide: CurrentGraphService,
						useValue: graphServiceMock,
					},
				],
				declarations: [],
			})
			.compileComponents();
		routeState = TestBed.inject(RouteStateService);
	});

	beforeEach(() => {
		routeState.branchId = '10';
		fixture = TestBed.createComponent(ConnectionsComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
		loader = TestbedHarnessEnvironment.loader(fixture);
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
