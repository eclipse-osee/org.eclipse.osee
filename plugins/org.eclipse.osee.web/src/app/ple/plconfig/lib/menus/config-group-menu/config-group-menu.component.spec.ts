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
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatMenuItemHarness } from '@angular/material/menu/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { DialogService } from '../../services/dialog.service';
import { PlConfigCurrentBranchService } from '../../services/pl-config-current-branch.service';
import { DialogServiceMock } from '../../testing/mockDialogService.mock';
import { plCurrentBranchServiceMock } from '../../testing/mockPlCurrentBranchService.mock';

import { ConfigGroupMenuComponent } from './config-group-menu.component';

describe('ConfigGroupMenuComponent', () => {
	let component: ConfigGroupMenuComponent;
	let fixture: ComponentFixture<ConfigGroupMenuComponent>;
	let loader: HarnessLoader;

	beforeEach(async () => {
		await TestBed.overrideComponent(ConfigGroupMenuComponent, {
			set: {
				providers: [
					{ provide: DialogService, useValue: DialogServiceMock },
					{
						provide: PlConfigCurrentBranchService,
						useValue: plCurrentBranchServiceMock,
					},
				],
			},
		})
			.configureTestingModule({
				imports: [
					MatMenuModule,
					MatIconModule,
					ConfigGroupMenuComponent,
					NoopAnimationsModule,
					RouterTestingModule.withRoutes([
						{
							path: '',
							component: ConfigGroupMenuComponent,
							children: [
								{
									path: ':branchType',
									children: [
										{
											path: ':branchId',
											children: [
												{
													path: 'diff',
													component:
														ConfigGroupMenuComponent,
												},
											],
										},
									],
								},
							],
						},
						{
							path: 'diffOpen',
							component: ConfigGroupMenuComponent,
							outlet: 'rightSideNav',
						},
					]),
				],
				declarations: [],
				providers: [
					{ provide: DialogService, useValue: DialogServiceMock },
					{
						provide: PlConfigCurrentBranchService,
						useValue: plCurrentBranchServiceMock,
					},
				],
			})
			.compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(ConfigGroupMenuComponent);
		component = fixture.componentInstance;
		component.group = {
			id: '1',
			name: 'abcd',
			description: '',
			configurations: [],
			changes: {
				name: {
					previousValue: '123',
					currentValue: 'abcd',
					transactionToken: { id: '12', branchId: '12345' },
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
			MatMenuItemHarness.with({
				text: new RegExp('Open Config Group Menu'),
			})
		);
		await menu.click();
		expect(spy).toHaveBeenCalled();
	});
});
