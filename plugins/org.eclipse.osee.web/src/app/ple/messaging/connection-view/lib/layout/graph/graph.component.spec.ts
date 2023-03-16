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
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatMenuModule } from '@angular/material/menu';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { NgxGraphModule, Node } from '@swimlane/ngx-graph';
import { graphServiceMock } from '../../testing/current-graph.service.mock';
import { CurrentGraphService } from '../../services/current-graph.service';
import { HarnessLoader } from '@angular/cdk/testing';
import { TestbedHarnessEnvironment } from '@angular/cdk/testing/testbed';

import { GraphComponent } from './graph.component';
import {
	MatMenuHarness,
	MatMenuItemHarness,
} from '@angular/material/menu/testing';
import { MatFormFieldModule } from '@angular/material/form-field';
import { FormsModule } from '@angular/forms';
import { MatSelectModule } from '@angular/material/select';
import { MatInputModule } from '@angular/material/input';
import { CommonModule } from '@angular/common';
import { ConfirmRemovalDialogComponent } from '../../dialogs/confirm-removal-dialog/confirm-removal-dialog.component';
import { CreateConnectionDialogComponent } from '../../dialogs/create-connection-dialog/create-connection-dialog.component';
import { CreateNewNodeDialogComponent } from '../../dialogs/create-new-node-dialog/create-new-node-dialog.component';
import { EditConnectionDialogComponent } from '../../dialogs/edit-connection-dialog/edit-connection-dialog.component';
import { EditNodeDialogComponent } from '../../dialogs/edit-node-dialog/edit-node-dialog.component';
import { of } from 'rxjs';
import { MockGraphLinkMenuComponent } from '../../testing/graph-link-menu.component.mock';
import { MockGraphNodeMenuComponent } from '../../testing/graph-node-menu.component.mock';
import { MatIconModule } from '@angular/material/icon';
import type {
	connectionWithChanges,
	OseeEdge,
	node,
} from '@osee/messaging/shared/types';
import { EnumsService } from '@osee/messaging/shared/services';
import { RouteStateService } from '../../services/route-state-service.service';
import { enumsServiceMock } from '@osee/messaging/shared/testing';

describe('GraphComponent', () => {
	let component: GraphComponent;
	let fixture: ComponentFixture<GraphComponent>;
	let loader: HarnessLoader;
	let routerService: RouteStateService;
	let menuLinkHarness: MatMenuHarness;
	let menuNodeHarness: MatMenuHarness;
	let menuGraphHarness: MatMenuHarness;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [
				MatDialogModule,
				MatIconModule,
				NgxGraphModule,
				NoopAnimationsModule,
				RouterTestingModule,
				MatMenuModule,
				MatFormFieldModule,
				FormsModule,
				MatSelectModule,
				MatInputModule,
				CommonModule,
				ConfirmRemovalDialogComponent,
				CreateConnectionDialogComponent,
				CreateNewNodeDialogComponent,
				EditConnectionDialogComponent,
				EditNodeDialogComponent,
				MockGraphLinkMenuComponent,
				MockGraphNodeMenuComponent,
				GraphComponent,
			],
			providers: [
				{ provide: CurrentGraphService, useValue: graphServiceMock },
				{ provide: EnumsService, useValue: enumsServiceMock },
			],
			teardown: { destroyAfterEach: false },
		}).compileComponents();
		routerService = TestBed.inject(RouteStateService);
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(GraphComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
		loader = TestbedHarnessEnvironment.loader(fixture);
	});
	beforeEach(() => {
		component.linkMenuTrigger.closeMenu();
		component.nodeMenuTrigger.closeMenu();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});

	describe('Core Functionality', () => {
		describe('View Functionality', () => {
			beforeEach(() => {
				component.editMode = false;
				fixture.detectChanges();
			});
		});
		describe('Editing Menus', () => {
			beforeEach(() => {
				component.editMode = true;
				fixture.detectChanges();
			});
			describe('Link Menu', () => {
				beforeEach(async () => {
					menuLinkHarness = await loader.getHarness(
						MatMenuHarness.with({ triggerText: 'LinkMenu' })
					);
					component.linkMenuTrigger.menuData = {
						data: {
							id: '1',
							name: 'edge',
							applicability: {
								id: '1',
								name: 'Base',
							},
							dashed: false,
							description: '',
							transportType: 'ETHERNET',
						},
						source: {
							id: '2',
							label: 'Node 1',
							data: {
								id: '2',
								name: 'Node 1',
								applicability: {
									id: '1',
									name: 'Base',
								},
								interfaceNodeBgColor: '',
								interfaceNodeAddress: '',
								description: '',
							},
						},
						target: {
							id: '3',
							label: 'Node 2',
							data: {
								id: '3',
								name: 'Node 2',
								applicability: {
									id: '1',
									name: 'Base',
								},
								interfaceNodeBgColor: '',
								interfaceNodeAddress: '',
								description: '',
							},
						},
					};
				});
				it('menu harness should be defined', () => {
					expect(menuLinkHarness).toBeDefined();
				});
				describe('Testing open function', () => {
					it('should open the menu with proper initialization and close/reset', async () => {
						const testMenuData: connectionWithChanges = {
							id: '1',
							name: 'edge',
							description: '',
							applicability: {
								id: '1',
								name: 'Base',
							},
							transportType: {
								name: 'ETHERNET',
								byteAlignValidation: false,
								byteAlignValidationSize: 0,
								messageGeneration: false,
								messageGenerationPosition: '',
								messageGenerationType: '',
							},
							deleted: false,
							added: false,
							changes: {
								name: {
									previousValue: 'abcdef',
									currentValue: 'jkl',
									transactionToken: {
										id: '-1',
										branchId: '-1',
									},
								},
							},
						};
						component.openLinkDialog(
							new MouseEvent('contextmenu', {
								clientX: 100,
								clientY: 100,
							}),
							{
								id: '1',
								source: '10',
								target: '15',
								data: testMenuData,
							},
							[
								{
									id: '10',
									label: '10',
									data: { id: '10', name: '10' },
								},
								{
									id: '15',
									label: '15',
									data: { id: '15', name: '15' },
								},
							]
						);
						expect(await menuLinkHarness.isOpen()).toBeTrue();
						component.linkMenuTrigger.menuData = {
							data: testMenuData,
							source: {
								id: '2',
								label: 'Node 1',
								data: {
									id: '2',
									name: 'Node 1',
									applicability: {
										id: '1',
										name: 'Base',
									},
									interfaceNodeBgColor: '',
									interfaceNodeAddress: '',
									description: '',
								},
							},
							target: {
								id: '3',
								label: 'Node 2',
								data: {
									id: '3',
									name: 'Node 2',
									applicability: {
										id: '1',
										name: 'Base',
									},
									interfaceNodeBgColor: '',
									interfaceNodeAddress: '',
									description: '',
								},
							},
						};
						menuLinkHarness.close();
					});
				});
				afterEach(async () => {
					if (await menuLinkHarness.isOpen()) {
						await menuLinkHarness.close();
					} else {
						await 0;
					}
				});
			});
			describe('Node Menu', () => {
				let node: Node = {
					id: '2',
					label: 'Node 1',
					data: {
						id: '2',
						name: 'Node 1',
						applicability: {
							id: '1',
							name: 'Base',
						},
						interfaceNodeBgColor: '',
						interfaceNodeAddress: '',
						description: '',
					},
				};
				let connections: OseeEdge<connectionWithChanges>[] = [
					{
						source: '2',
						target: '5',
						data: {
							name: '',
							description: '',
							transportType: {
								name: 'ETHERNET',
								byteAlignValidation: false,
								byteAlignValidationSize: 0,
								messageGeneration: false,
								messageGenerationPosition: '',
								messageGenerationType: '',
							},
							deleted: false,
							added: false,
							changes: {},
						},
					},
					{
						target: '2',
						source: '7',
						data: {
							name: '',
							description: '',
							transportType: {
								name: 'ETHERNET',
								byteAlignValidation: false,
								byteAlignValidationSize: 0,
								messageGeneration: false,
								messageGenerationPosition: '',
								messageGenerationType: '',
							},
							deleted: false,
							added: false,
							changes: {},
						},
					},
				];
				beforeEach(async () => {
					menuNodeHarness = await loader.getHarness(
						MatMenuHarness.with({ triggerText: 'NodeMenu' })
					);
					component.nodeMenuTrigger.menuData = {
						data: node.data,
						sources: [connections[0]],
						targets: [connections[1]],
					};
				});
				it('menu harness should be defined', () => {
					expect(menuNodeHarness).toBeDefined();
				});
				describe('Testing open function', () => {
					it('should open the menu with proper initialization and close/reset', async () => {
						component.openNodeDialog(
							new MouseEvent('contextmenu', {
								clientX: 200,
								clientY: 200,
							}),
							{ id: '1', data: { name: 'first' } },
							[
								{
									id: 'a3',
									source: '1',
									target: '2',
									data: {
										name: 'a',
										description: '',
										transportType: {
											name: 'ETHERNET',
											byteAlignValidation: false,
											byteAlignValidationSize: 0,
											messageGeneration: false,
											messageGenerationPosition: '',
											messageGenerationType: '',
										},
									},
								},
								{
									id: 'a4',
									source: '2',
									target: '1',
									data: {
										name: 'b',
										description: '',
										transportType: {
											name: 'ETHERNET',
											byteAlignValidation: false,
											byteAlignValidationSize: 0,
											messageGeneration: false,
											messageGenerationPosition: '',
											messageGenerationType: '',
										},
									},
								},
							]
						);
						expect(await menuNodeHarness.isOpen()).toBeTrue();
						component.nodeMenuTrigger.menuData = {
							data: node.data,
							sources: [connections[0]],
							targets: [connections[1]],
						};
						menuNodeHarness.close();
					});
				});
				afterEach(async () => {
					if (await menuNodeHarness.isOpen()) {
						await menuNodeHarness.close();
					} else {
						await 0;
					}
				});
			});

			describe('Graph Menu', () => {
				beforeEach(async () => {
					menuGraphHarness = await loader.getHarness(
						MatMenuHarness.with({ triggerText: 'GraphMenu' })
					);
				});
				it('menu harness should be defined', () => {
					expect(menuGraphHarness).toBeDefined();
				});
				describe('Testing open function', () => {
					it('should open the menu with proper initialization and close/reset', async () => {
						let event = new MouseEvent('contextmenu', {
							clientX: 300,
							clientY: 300,
						});
						let el = document.createElement('button');
						el.classList.add('panning-rect');
						Object.defineProperty(event, 'target', {
							value: el,
							enumerable: true,
						});
						component.openGraphDialog(event);
						expect(await menuGraphHarness.isOpen()).toBeTrue();
						menuGraphHarness.close();
					});
				});
				describe('Open Menu Tests', () => {
					let items: MatMenuItemHarness[] = [];
					beforeEach(async () => {
						await menuGraphHarness.open();
						items = await menuGraphHarness.getItems();
					});
					it('should have the correct amount of items(1)', () => {
						expect(items.length).toEqual(1);
					});
					it('should open create new node dialog', async () => {
						let response: node = {
							name: 'abcdef',
							description: 'jkl',
							applicability: { id: '1', name: 'Base' },
						};
						let dialogRefSpy = jasmine.createSpyObj({
							afterClosed: of(response),
							close: null,
						});
						let dialogSpy = spyOn(
							TestBed.inject(MatDialog),
							'open'
						).and.returnValue(dialogRefSpy);
						let spy = spyOn(
							component,
							'createNewNode'
						).and.callThrough();
						await menuGraphHarness.clickItem({
							text: new RegExp('Create New Node'),
						});
						expect(spy).toHaveBeenCalled();
					});
				});
				afterEach(async () => {
					if (await menuGraphHarness.isOpen()) {
						menuGraphHarness.close();
					} else {
						await 0;
					}
				});
			});
		});
	});
});
