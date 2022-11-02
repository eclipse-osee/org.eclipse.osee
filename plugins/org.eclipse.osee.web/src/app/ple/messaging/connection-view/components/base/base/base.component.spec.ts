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
import { RouteStateService } from '../../../services/route-state-service.service';
import { GraphDummy } from '../../../testing/MockComponents/Graph.mock';
import { HarnessLoader } from '@angular/cdk/testing';
import { TestbedHarnessEnvironment } from '@angular/cdk/testing/testbed';

import { BaseComponent } from './base.component';
import { EditAuthService } from 'src/app/ple/messaging/shared/services/edit-auth-service.service';
import { editAuthServiceMock } from '../../../mocks/EditAuthService.mock';
import { CurrentGraphService } from '../../../services/current-graph.service';
import { graphServiceMock } from '../../../mocks/CurrentGraphService.mock';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MimSingleDiffDummy } from 'src/app/ple/diff-views/mocks/mim-single-diff.mock';
import { RouterTestingModule } from '@angular/router/testing';
import { MatIconModule } from '@angular/material/icon';
import { BranchPickerStub } from '../../../../../../shared-components/components/branch-picker/branch-picker/branch-picker.mock.component';
import { ActionDropdownStub } from '../../../../../../shared-components/components/action-state-button/action-drop-down/action-drop-down.mock.component';
import { BranchUndoButtonTestingModule } from '../../../../../../shared-components/components/branch-undo-button/branch.undo-button.testing.module';

describe('BaseComponent', () => {
	let component: BaseComponent;
	let routeState: RouteStateService;
	let loader: HarnessLoader;
	let fixture: ComponentFixture<BaseComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [
				MatDialogModule,
				MatIconModule,
				MatButtonModule,
				MatSidenavModule,
				RouterTestingModule,
				BranchUndoButtonTestingModule,
				NoopAnimationsModule,
			],
			providers: [
				{ provide: EditAuthService, useValue: editAuthServiceMock },
				{ provide: CurrentGraphService, useValue: graphServiceMock },
			],
			declarations: [
				BaseComponent,
				GraphDummy,
				BranchPickerStub,
				MimSingleDiffDummy,
				ActionDropdownStub,
			],
		}).compileComponents();
		routeState = TestBed.inject(RouteStateService);
	});

	beforeEach(() => {
		routeState.branchId = '10';
		fixture = TestBed.createComponent(BaseComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
		loader = TestbedHarnessEnvironment.loader(fixture);
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
