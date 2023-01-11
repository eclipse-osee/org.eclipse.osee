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
import { CommonModule } from '@angular/common';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatButtonHarness } from '@angular/material/button/testing';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatMenuItem, MatMenuModule } from '@angular/material/menu';
import {
	MatMenuHarness,
	MatMenuItemHarness,
} from '@angular/material/menu/testing';
import { MatSelectModule } from '@angular/material/select';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { NgxGraphModule } from '@swimlane/ngx-graph';
import { of } from 'rxjs';
import { enumsServiceMock } from '../../../../shared/testing/enums.service.mock';
import { EnumsService } from '../../../../shared/services/http/enums.service';
import { connectionWithChanges } from '../../../../shared/types/connection';
import { difference } from '../../../../../../types/change-report/change-report';
import { graphServiceMock } from '../../testing/current-graph.service.mock';
import { CurrentGraphService } from '../../services/current-graph.service';

import { GraphLinkMenuComponent } from './graph-link-menu.component';

describe('GraphLinkMenuComponent', () => {
	let component: GraphLinkMenuComponent;
	let fixture: ComponentFixture<GraphLinkMenuComponent>;
	let loader: HarnessLoader;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [
				MatButtonModule,
				MatIconModule,
				MatDialogModule,
				NoopAnimationsModule,
				RouterTestingModule,
				MatMenuModule,
				MatFormFieldModule,
				FormsModule,
				MatSelectModule,
				MatInputModule,
				CommonModule,
				GraphLinkMenuComponent,
			],
			providers: [
				{ provide: CurrentGraphService, useValue: graphServiceMock },
				{ provide: EnumsService, useValue: enumsServiceMock },
			],
			declarations: [],
		}).compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(GraphLinkMenuComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
		loader = TestbedHarnessEnvironment.loader(fixture);
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});

	describe('With Editing Enabled & no changes', () => {
		beforeEach(() => {
			component.editMode = true;
			component.data = {
				id: '3',
				name: '3',
				description: '',
				transportType: {
					name: 'ETHERNET',
					byteAlignValidation: false,
					byteAlignValidationSize: 0,
					messageGeneration: false,
					messageGenerationPosition: '',
					messageGenerationType: '',
				},
			};
			component.source = {
				id: '1',
				data: {
					id: '1',
					name: '1',
					interfaceNodeAddress: '',
					interfaceNodeBgColor: '',
				},
			};
			component.target = {
				id: '2',
				data: {
					id: '2',
					name: '2',
					interfaceNodeAddress: '',
					interfaceNodeBgColor: '',
				},
			};
			fixture.detectChanges();
			expect(component).toBeTruthy();
		});
		it('Should have the correct amount of items(4)', async () => {
			const buttons = await loader.getAllHarnesses(MatMenuItemHarness);
			expect(buttons.length).toEqual(4);
		});

		it('should open the connection edit dialog', async () => {
			const connection = {
				id: '1',
				name: 'edge',
				applicability: {
					id: '1',
					name: 'Base',
				},
				dashed: false,
				description: '',
				transportType: 'ETHERNET',
			};
			let dialogRefSpy = jasmine.createSpyObj({
				afterClosed: of(component.data),
				close: null,
			});
			let dialogSpy = spyOn(
				TestBed.inject(MatDialog),
				'open'
			).and.returnValue(dialogRefSpy);
			let spy = spyOn(
				component,
				'openConnectionEditDialog'
			).and.callThrough();
			await (
				await loader.getHarness(
					MatMenuItemHarness.with({ text: new RegExp('Edit 3') })
				)
			).click();
			expect(spy).toHaveBeenCalledWith(component.data);
		});

		it('should open the remove connection dialog', async () => {
			let connection = {
				id: '1',
				name: 'edge',
				applicability: {
					id: '1',
					name: 'Base',
				},
				dashed: false,
				description: '',
				transportType: 'ETHERNET',
			};
			let source = {
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
			let target = {
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
			};
			let dialogRefSpy = jasmine.createSpyObj({
				afterClosed: of({
					id: component.data.id,
					name: component.data.name,
					extraNames: [
						component.source.label,
						component.target.label,
					],
					type: 'connection',
				}),
				close: null,
			});
			let dialogSpy = spyOn(
				TestBed.inject(MatDialog),
				'open'
			).and.returnValue(dialogRefSpy);
			let spy = spyOn(
				component,
				'openRemoveConnectionDialog'
			).and.callThrough();
			await (
				await loader.getHarness(
					MatMenuItemHarness.with({
						text: new RegExp('Remove connection 3'),
					})
				)
			).click();
			expect(spy).toHaveBeenCalledWith(
				component.data,
				component.source,
				component.target
			);
		});
	});

	describe('With Editing Enabled & changes', () => {
		beforeEach(() => {
			component.editMode = true;
			component.data = {
				id: '3',
				name: '3',
				description: '',
				applicability: { id: '1', name: 'Base' },
				transportType: {
					name: 'ETHERNET',
					byteAlignValidation: false,
					byteAlignValidationSize: 0,
					messageGeneration: false,
					messageGenerationPosition: '',
					messageGenerationType: '',
				},
				changes: {
					name: {
						previousValue: 'a',
						currentValue: '3',
						transactionToken: {
							id: '-1',
							branchId: '-1',
						},
					},
					description: {
						previousValue: 'abcdef',
						currentValue: '',
						transactionToken: {
							id: '-1',
							branchId: '-1',
						},
					},
					applicability: {
						previousValue: {
							id: '2',
							name: 'Random Applicability',
						},
						currentValue: { id: '1', name: 'Base' },
						transactionToken: {
							id: '-1',
							branchId: '-1',
						},
					},
					transportType: {
						previousValue: 'MILSTD1553_B',
						currentValue: 'ETHERNET',
						transactionToken: {
							id: '-1',
							branchId: '-1',
						},
					},
				},
			};
			component.source = {
				id: '1',
				data: {
					id: '1',
					name: '1',
					interfaceNodeAddress: '',
					interfaceNodeBgColor: '',
				},
			};
			component.target = {
				id: '2',
				data: {
					id: '2',
					name: '2',
					interfaceNodeAddress: '',
					interfaceNodeBgColor: '',
				},
			};
			fixture.detectChanges();
			expect(component).toBeTruthy();
		});

		it('Should have the correct amount of items(5)', async () => {
			const buttons = await loader.getAllHarnesses(MatMenuItemHarness);
			expect(buttons.length).toEqual(5);
		});

		it('should open the connection edit dialog', async () => {
			const connection = {
				id: '1',
				name: 'edge',
				applicability: {
					id: '1',
					name: 'Base',
				},
				dashed: false,
				description: '',
				transportType: 'ETHERNET',
			};
			let dialogRefSpy = jasmine.createSpyObj({
				afterClosed: of(component.data),
				close: null,
			});
			let dialogSpy = spyOn(
				TestBed.inject(MatDialog),
				'open'
			).and.returnValue(dialogRefSpy);
			let spy = spyOn(
				component,
				'openConnectionEditDialog'
			).and.callThrough();
			await (
				await loader.getHarness(
					MatMenuItemHarness.with({ text: new RegExp('Edit 3') })
				)
			).click();
			expect(spy).toHaveBeenCalledWith(component.data);
		});

		it('should open the remove connection dialog', async () => {
			let connection = {
				id: '1',
				name: 'edge',
				applicability: {
					id: '1',
					name: 'Base',
				},
				dashed: false,
				description: '',
				transportType: 'ETHERNET',
			};
			let source = {
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
			let target = {
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
			};
			let dialogRefSpy = jasmine.createSpyObj({
				afterClosed: of({
					id: component.data.id,
					name: component.data.name,
					extraNames: [
						component.source.label,
						component.target.label,
					],
					type: 'connection',
				}),
				close: null,
			});
			let dialogSpy = spyOn(
				TestBed.inject(MatDialog),
				'open'
			).and.returnValue(dialogRefSpy);
			let spy = spyOn(
				component,
				'openRemoveConnectionDialog'
			).and.callThrough();
			await (
				await loader.getHarness(
					MatMenuItemHarness.with({
						text: new RegExp('Remove connection 3'),
					})
				)
			).click();
			expect(spy).toHaveBeenCalledWith(
				component.data,
				component.source,
				component.target
			);
		});
		describe('opening the diff sidenav', () => {
			let spy: jasmine.Spy<
				(open: boolean, value: difference, header: string) => void
			>;
			beforeEach(() => {
				spy = spyOn(component, 'viewDiff').and.callThrough();
			});
			it('should open the name sidenav', async () => {
				const topItem = await loader.getHarness(
					MatMenuItemHarness.with({
						text: new RegExp('View Diff for'),
					})
				);
				if (await topItem.hasSubmenu()) {
					const subMenu = await topItem.getSubmenu();
					await subMenu?.clickItem({ text: 'Name' });
				}
				expect(spy).toHaveBeenCalledWith(
					true,
					(component.data as connectionWithChanges).changes.name || {
						previousValue: '',
						currentValue: '',
						transactionToken: { id: '-1', branchId: '-1' },
					},
					'Name'
				);
			});

			it('should open the description sidenav', async () => {
				const topItem = await loader.getHarness(
					MatMenuItemHarness.with({
						text: new RegExp('View Diff for'),
					})
				);
				if (await topItem.hasSubmenu()) {
					const subMenu = await topItem.getSubmenu();
					await subMenu?.clickItem({ text: 'Description' });
				}
				expect(spy).toHaveBeenCalledWith(
					true,
					(component.data as connectionWithChanges).changes
						.description || {
						previousValue: '',
						currentValue: '',
						transactionToken: { id: '-1', branchId: '-1' },
					},
					'Description'
				);
			});

			it('should open the transport type sidenav', async () => {
				MatMenuItemHarness.with({});
				const topItem = await loader.getHarness(
					MatMenuItemHarness.with({
						text: new RegExp('View Diff for'),
					})
				);
				if (await topItem.hasSubmenu()) {
					const subMenu = await topItem.getSubmenu();
					await subMenu?.clickItem({ text: 'Transport Type' });
				}
				expect(spy).toHaveBeenCalledWith(
					true,
					(component.data as connectionWithChanges).changes
						.transportType || {
						previousValue: '',
						currentValue: '',
						transactionToken: { id: '-1', branchId: '-1' },
					},
					'Transport Type'
				);
			});

			it('should open the applicability sidenav', async () => {
				const topItem = await loader.getHarness(
					MatMenuItemHarness.with({
						text: new RegExp('View Diff for'),
					})
				);
				if (await topItem.hasSubmenu()) {
					const subMenu = await topItem.getSubmenu();
					await subMenu?.clickItem({ text: 'Applicability' });
				}
				expect(spy).toHaveBeenCalledWith(
					true,
					(component.data as connectionWithChanges).changes
						.applicability || {
						previousValue: '',
						currentValue: '',
						transactionToken: { id: '-1', branchId: '-1' },
					},
					'Applicability'
				);
			});
		});
	});

	describe('With Editing Disabled & no changes', () => {
		beforeEach(() => {
			component.editMode = false;
			component.data = {
				id: '3',
				name: '3',
				description: '',
				transportType: {
					name: 'ETHERNET',
					byteAlignValidation: false,
					byteAlignValidationSize: 0,
					messageGeneration: false,
					messageGenerationPosition: '',
					messageGenerationType: '',
				},
			};
			component.source = {
				id: '1',
				data: {
					id: '1',
					name: '1',
					interfaceNodeAddress: '',
					interfaceNodeBgColor: '',
				},
			};
			component.target = {
				id: '2',
				data: {
					id: '2',
					name: '2',
					interfaceNodeAddress: '',
					interfaceNodeBgColor: '',
				},
			};
			fixture.detectChanges();
			expect(component).toBeTruthy();
		});
		it('Should have the correct amount of items(2)', async () => {
			const buttons = await loader.getAllHarnesses(MatMenuItemHarness);
			expect(buttons.length).toEqual(2);
		});
	});

	describe('With Editing Disabled & changes', () => {
		beforeEach(() => {
			component.editMode = false;
			component.data = {
				id: '3',
				name: '3',
				description: '',
				applicability: { id: '1', name: 'Base' },
				transportType: {
					name: 'ETHERNET',
					byteAlignValidation: false,
					byteAlignValidationSize: 0,
					messageGeneration: false,
					messageGenerationPosition: '',
					messageGenerationType: '',
				},
				changes: {
					name: {
						previousValue: 'a',
						currentValue: '3',
						transactionToken: {
							id: '-1',
							branchId: '-1',
						},
					},
					description: {
						previousValue: 'abcdef',
						currentValue: '',
						transactionToken: {
							id: '-1',
							branchId: '-1',
						},
					},
					applicability: {
						previousValue: {
							id: '2',
							name: 'Random Applicability',
						},
						currentValue: { id: '1', name: 'Base' },
						transactionToken: {
							id: '-1',
							branchId: '-1',
						},
					},
					transportType: {
						previousValue: 'MILSTD1553_B',
						currentValue: 'ETHERNET',
						transactionToken: {
							id: '-1',
							branchId: '-1',
						},
					},
				},
			};
			component.source = {
				id: '1',
				data: {
					id: '1',
					name: '1',
					interfaceNodeAddress: '',
					interfaceNodeBgColor: '',
				},
			};
			component.target = {
				id: '2',
				data: {
					id: '2',
					name: '2',
					interfaceNodeAddress: '',
					interfaceNodeBgColor: '',
				},
			};
			fixture.detectChanges();
			expect(component).toBeTruthy();
		});

		it('Should have the correct amount of items(3)', async () => {
			const buttons = await loader.getAllHarnesses(MatMenuItemHarness);
			expect(buttons.length).toEqual(3);
		});

		describe('opening the diff sidenav', () => {
			let spy: jasmine.Spy<
				(open: boolean, value: difference, header: string) => void
			>;
			beforeEach(() => {
				spy = spyOn(component, 'viewDiff').and.callThrough();
			});
			it('should open the name sidenav', async () => {
				const topItem = await loader.getHarness(
					MatMenuItemHarness.with({
						text: new RegExp('View Diff for'),
					})
				);
				if (await topItem.hasSubmenu()) {
					const subMenu = await topItem.getSubmenu();
					await subMenu?.clickItem({ text: 'Name' });
				}
				expect(spy).toHaveBeenCalledWith(
					true,
					(component.data as connectionWithChanges).changes.name || {
						previousValue: '',
						currentValue: '',
						transactionToken: { id: '-1', branchId: '-1' },
					},
					'Name'
				);
			});

			it('should open the description sidenav', async () => {
				const topItem = await loader.getHarness(
					MatMenuItemHarness.with({
						text: new RegExp('View Diff for'),
					})
				);
				if (await topItem.hasSubmenu()) {
					const subMenu = await topItem.getSubmenu();
					await subMenu?.clickItem({ text: 'Description' });
				}
				expect(spy).toHaveBeenCalledWith(
					true,
					(component.data as connectionWithChanges).changes
						.description || {
						previousValue: '',
						currentValue: '',
						transactionToken: { id: '-1', branchId: '-1' },
					},
					'Description'
				);
			});

			it('should open the transport type sidenav', async () => {
				const topItem = await loader.getHarness(
					MatMenuItemHarness.with({
						text: new RegExp('View Diff for'),
					})
				);
				if (await topItem.hasSubmenu()) {
					const subMenu = await topItem.getSubmenu();
					await subMenu?.clickItem({ text: 'Transport Type' });
				}
				expect(spy).toHaveBeenCalledWith(
					true,
					(component.data as connectionWithChanges).changes
						.transportType || {
						previousValue: '',
						currentValue: '',
						transactionToken: { id: '-1', branchId: '-1' },
					},
					'Transport Type'
				);
			});

			it('should open the applicability sidenav', async () => {
				const topItem = await loader.getHarness(
					MatMenuItemHarness.with({
						text: new RegExp('View Diff for'),
					})
				);
				if (await topItem.hasSubmenu()) {
					const subMenu = await topItem.getSubmenu();
					await subMenu?.clickItem({ text: 'Applicability' });
				}
				expect(spy).toHaveBeenCalledWith(
					true,
					(component.data as connectionWithChanges).changes
						.applicability || {
						previousValue: '',
						currentValue: '',
						transactionToken: { id: '-1', branchId: '-1' },
					},
					'Applicability'
				);
			});
		});
	});
});
