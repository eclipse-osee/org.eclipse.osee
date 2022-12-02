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
import { MatDialog } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatMenuItemHarness } from '@angular/material/menu/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { DialogService } from '../../../services/dialog.service';
import { PlConfigCurrentBranchService } from '../../../services/pl-config-current-branch.service';
import { DialogServiceMock } from '../../../testing/mockDialogService.mock';
import { plCurrentBranchServiceMock } from '../../../testing/mockPlCurrentBranchService';

import { ConfigMenuComponent } from './config-menu.component';

describe('ConfigMenuComponent', () => {
	let component: ConfigMenuComponent;
	let fixture: ComponentFixture<ConfigMenuComponent>;
	let loader: HarnessLoader;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [
				MatMenuModule,
				MatIconModule,
				NoopAnimationsModule,
				RouterTestingModule.withRoutes([
					{
						path: '',
						component: ConfigMenuComponent,
						children: [
							{
								path: ':branchType',
								children: [
									{
										path: ':branchId',
										children: [
											{
												path: 'diff',
												component: ConfigMenuComponent,
											},
										],
									},
								],
							},
						],
					},
					{
						path: 'diffOpen',
						component: ConfigMenuComponent,
						outlet: 'rightSideNav',
					},
				]),
			],
			declarations: [ConfigMenuComponent],
			providers: [
				{ provide: DialogService, useValue: DialogServiceMock },
				{
					provide: PlConfigCurrentBranchService,
					useValue: plCurrentBranchServiceMock,
				},
			],
		}).compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(ConfigMenuComponent);
		component = fixture.componentInstance;
		component.config = {
			id: '1',
			name: 'abcd',
			description: '',
			hasFeatureApplicabilities: false,
			changes: {
				name: {
					currentValue: 'abcd',
					previousValue: '',
					transactionToken: { id: '12', branchId: '8' },
				},
			},
		};
		fixture.detectChanges();
		loader = TestbedHarnessEnvironment.loader(fixture);
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});

	it('should open a diff sidenav', async () => {
		const spy = spyOn(component, 'viewDiff').and.callThrough();
		const menu = await loader.getHarness(
			MatMenuItemHarness.with({ text: new RegExp('View Diff for abcd') })
		);
		expect(menu).toBeDefined();
		await menu.focus();
		expect(await menu.getSubmenu()).toBeDefined();
		await (await menu.getSubmenu())?.clickItem({ text: 'Name' });
		expect(spy).toHaveBeenCalled();
	});
	it('should open the config group dialog', async () => {
		const spy = spyOn(component, 'openConfigMenu').and.callThrough();
		const menu = await loader.getHarness(
			MatMenuItemHarness.with({ text: new RegExp('Open Config Menu') })
		);
		await menu.click();
		expect(spy).toHaveBeenCalled();
	});
});
