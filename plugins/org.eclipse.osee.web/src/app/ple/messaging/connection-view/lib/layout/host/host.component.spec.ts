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

import { HostComponent } from './host.component';
import { CurrentGraphService } from '../../services/current-graph.service';
import { graphServiceMock } from '../../testing/current-graph.service.mock';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MockSingleDiffComponent } from '../../../../../diff-views/single-diff/single-diff.component.mock';
import { RouterTestingModule } from '@angular/router/testing';
import { MatIconModule } from '@angular/material/icon';
import { NgIf, AsyncPipe } from '@angular/common';
import { EditAuthService } from '@osee/messaging/shared';
import {
	UndoButtonBranchMockComponent,
	ActionDropdownStub,
	BranchPickerStub,
} from '@osee/shared/components/testing';
import { editAuthServiceMock } from '@osee/messaging/shared/testing';
import { ViewSelectorMockComponent } from 'src/app/ple/messaging/shared/main-content/view-selector/view-selector.component.mock';

describe('HostComponent', () => {
	let component: HostComponent;
	let routeState: RouteStateService;
	let loader: HarnessLoader;
	let fixture: ComponentFixture<HostComponent>;

	beforeEach(async () => {
		await TestBed.overrideComponent(HostComponent, {
			set: {
				imports: [
					MatDialogModule,
					MatIconModule,
					MatButtonModule,
					MatSidenavModule,
					RouterTestingModule,
					NgIf,
					AsyncPipe,
					UndoButtonBranchMockComponent,
					MockSingleDiffComponent,
					ActionDropdownStub,
					MockGraphComponent,
					BranchPickerStub,
					ViewSelectorMockComponent,
				],
			},
		})
			.configureTestingModule({
				imports: [
					MatDialogModule,
					MatIconModule,
					MatButtonModule,
					MatSidenavModule,
					RouterTestingModule,
					UndoButtonBranchMockComponent,
					NoopAnimationsModule,
					MockSingleDiffComponent,
					ActionDropdownStub,
					HostComponent,
					MockGraphComponent,
					BranchPickerStub,
					ViewSelectorMockComponent,
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
		fixture = TestBed.createComponent(HostComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
		loader = TestbedHarnessEnvironment.loader(fixture);
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
