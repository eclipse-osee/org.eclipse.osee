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
import { FeatureMenuComponent } from './feature-menu.component';

describe('FeatureMenuComponent', () => {
	let component: FeatureMenuComponent;
	let fixture: ComponentFixture<FeatureMenuComponent>;
	let loader: HarnessLoader;

	beforeEach(async () => {
		await TestBed.overrideComponent(FeatureMenuComponent, {
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
				imports: [MatMenuModule, MatIconModule, FeatureMenuComponent],
				declarations: [],
				providers: [
					provideRouter([
						{
							path: '',
							component: FeatureMenuComponent,
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
														FeatureMenuComponent,
												},
											],
										},
									],
								},
							],
						},
						{
							path: 'diffOpen',
							component: FeatureMenuComponent,
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
		fixture = TestBed.createComponent(FeatureMenuComponent);
		component = fixture.componentInstance;
		fixture.componentRef.setInput('feature', {
			id: '1',
			name: 'abcd',
			changes: {
				name: {
					currentValue: 'abcd',
					previousValue: '',
					transactionToken: { id: '1234', branchId: '8' },
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
	it('should open the feature dialog', async () => {
		const spy = spyOn(component, 'displayFeatureMenu').and.callThrough();
		const menu = await loader.getHarness(
			MatMenuItemHarness.with({ text: new RegExp('Open Feature Dialog') })
		);
		await menu.click();
		expect(spy).toHaveBeenCalled();
	});
});
