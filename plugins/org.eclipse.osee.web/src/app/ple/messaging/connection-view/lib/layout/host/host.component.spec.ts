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
import { EditAuthService } from '../../../../shared/services/public/edit-auth-service.service';
import { editAuthServiceMock } from '../../../../shared/testing/edit-auth.service.mock';
import { CurrentGraphService } from '../../services/current-graph.service';
import { graphServiceMock } from '../../testing/current-graph.service.mock';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MimSingleDiffDummy } from '../../../../../diff-views/mocks/mim-single-diff.mock';
import { RouterTestingModule } from '@angular/router/testing';
import { MatIconModule } from '@angular/material/icon';
import { BranchPickerStub } from '../../../../../../shared-components/components/branch-picker/branch-picker/branch-picker.mock.component';
import { ActionDropdownStub } from '../../../../../../shared-components/components/action-state-button/action-drop-down/action-drop-down.mock.component';
import { NgIf, AsyncPipe } from '@angular/common';
import { UndoButtonBranchMockComponent } from '../../../../../../shared-components/components/branch-undo-button/branch-undo-button.component.mock';

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
					MimSingleDiffDummy,
					ActionDropdownStub,
					MockGraphComponent,
					BranchPickerStub,
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
					MimSingleDiffDummy,
					ActionDropdownStub,
					HostComponent,
					MockGraphComponent,
					BranchPickerStub,
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
