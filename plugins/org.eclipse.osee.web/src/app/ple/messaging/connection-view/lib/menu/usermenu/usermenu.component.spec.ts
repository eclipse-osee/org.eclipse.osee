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
import { HarnessLoader } from '@angular/cdk/testing';
import { TestbedHarnessEnvironment } from '@angular/cdk/testing/testbed';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatMenuItemHarness } from '@angular/material/menu/testing';
import { of } from 'rxjs';
import { EditAuthService } from '../../../../shared/services/public/edit-auth-service.service';
import { preferencesUiServiceMock } from '../../../../shared/services/ui/preferences-ui-service.mock';
import { PreferencesUIService } from '../../../../shared/services/ui/preferences-ui.service';
import { graphServiceMock } from '../../testing/current-graph.service.mock';
import { editAuthServiceMock } from '../../../../shared/testing/edit-auth.service.mock';
import { CurrentGraphService } from '../../services/current-graph.service';
import { RouteStateService } from '../../services/route-state-service.service';

import { UsermenuComponent } from './usermenu.component';
import { MatTooltipModule } from '@angular/material/tooltip';

describe('UsermenuComponent', () => {
	let component: UsermenuComponent;
	let fixture: ComponentFixture<UsermenuComponent>;
	let loader: HarnessLoader;
	let routeState: RouteStateService;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [
				MatMenuModule,
				MatIconModule,
				MatDialogModule,
				MatTooltipModule,
				UsermenuComponent,
			],
			providers: [
				{ provide: EditAuthService, useValue: editAuthServiceMock },
				{ provide: CurrentGraphService, useValue: graphServiceMock },
				{
					provide: PreferencesUIService,
					useValue: preferencesUiServiceMock,
				},
			],
			declarations: [],
		}).compileComponents();
		routeState = TestBed.inject(RouteStateService);
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(UsermenuComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
		loader = TestbedHarnessEnvironment.loader(fixture);
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});

	it('should open settings dialog', async () => {
		routeState.branchId = '10';
		let dialogRefSpy = jasmine.createSpyObj({
			afterClosed: of({
				branchId: '10',
				allowedHeaders1: [],
				allowedHeaders2: [],
				allHeaders1: [],
				allHeaders2: [],
				editable: true,
				headers1Label: '',
				headers2Label: '',
				headersTableActive: false,
			}),
			close: null,
		});
		let dialogSpy = spyOn(
			TestBed.inject(MatDialog),
			'open'
		).and.returnValue(dialogRefSpy);
		let spy = spyOn(component, 'openSettingsDialog').and.callThrough();
		await (
			await loader.getHarness(
				MatMenuItemHarness.with({ text: new RegExp('Settings') })
			)
		).click();
		expect(spy).toHaveBeenCalled();
	});
});
