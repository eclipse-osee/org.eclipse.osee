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
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { DialogService } from '../../services/dialog.service';
import { PlConfigCurrentBranchService } from '../../services/pl-config-current-branch.service';
import { DialogServiceMock } from '../../testing/mockDialogService.mock';
import { plCurrentBranchServiceMock } from '../../testing/mockPlCurrentBranchService.mock';

import { provideRouter } from '@angular/router';
import { ConfigGroupMenuComponent } from './config-group-menu.component';
import { CurrentBranchInfoService } from '@osee/shared/services';
import { testBranchInfo } from '@osee/shared/testing';
import { of } from 'rxjs';

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
					{
						provide: CurrentBranchInfoService,
						useValue: { currentBranch: of(testBranchInfo) },
					},
				],
			},
		})
			.configureTestingModule({
				imports: [
					MatMenuModule,
					MatIconModule,
					ConfigGroupMenuComponent,
				],
				declarations: [],
				providers: [
					provideRouter([
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
					provideNoopAnimations(),
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
		fixture.componentRef.setInput('group', {
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
		});
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
