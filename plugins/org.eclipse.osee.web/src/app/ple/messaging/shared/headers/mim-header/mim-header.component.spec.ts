/*********************************************************************
 * Copyright (c) 2022 Boeing
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
import { RouterTestingModule } from '@angular/router/testing';
import {
	MimRouteService,
	SharedConnectionService,
	StructuresService,
} from '@osee/messaging/shared/services';
import {
	sharedConnectionServiceMock,
	structureServiceMock,
} from '@osee/messaging/shared/testing';
import { BranchInfoService } from '@osee/shared/services';
import { BranchInfoServiceMock } from '@osee/shared/testing';

import { MimHeaderComponent } from './mim-header.component';
const headerUnderTest = [
	{
		title: 'Full Test',
		type: true,
		id: true,
		connection: true,
		message: true,
		submessage: true,
		breadcrumb: true,
		structure: true,
	},
	{
		title: 'BreadCrumb',
		type: true,
		id: true,
		connection: true,
		message: true,
		submessage: true,
		breadcrumb: true,
		structure: false,
	},
	{
		title: 'Submsg',
		type: true,
		id: true,
		connection: true,
		message: true,
		submessage: true,
		breadcrumb: false,
		structure: false,
	},
	{
		title: 'Msg',
		type: true,
		id: true,
		connection: true,
		message: true,
		submessage: false,
		breadcrumb: false,
		structure: false,
	},
	{
		title: 'Connection',
		type: true,
		id: true,
		connection: true,
		message: false,
		submessage: false,
		breadcrumb: false,
		structure: false,
	},
	{
		title: 'Branch Id',
		type: true,
		id: true,
		connection: false,
		message: false,
		submessage: false,
		breadcrumb: false,
		structure: false,
	},
	{
		title: 'Branch Type',
		type: true,
		id: false,
		connection: false,
		message: false,
		submessage: false,
		breadcrumb: false,
		structure: false,
	},
	{
		title: 'None',
		type: false,
		id: false,
		connection: false,
		message: false,
		submessage: false,
		breadcrumb: false,
		structure: false,
	},
];
describe('MimHeaderComponent', () => {
	let component: MimHeaderComponent;
	let fixture: ComponentFixture<MimHeaderComponent>;
	let uiService: MimRouteService;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [RouterTestingModule],
			providers: [
				{ provide: BranchInfoService, useValue: BranchInfoServiceMock },
				{
					provide: SharedConnectionService,
					useValue: sharedConnectionServiceMock,
				},
				{ provide: StructuresService, useValue: structureServiceMock },
			],
		}).compileComponents();
		uiService = TestBed.inject(MimRouteService);
	});

	headerUnderTest.forEach((header) => {
		describe(`${header.title}`, () => {
			beforeEach(() => {
				uiService.typeValue = header.type ? 'working' : '';
				uiService.idValue = header.id ? '10' : '';
				uiService.connectionIdString = header.connection ? '20' : '';
				uiService.messageIdString = header.message ? '30' : '';
				uiService.submessageIdString = header.submessage ? '40' : '';
				uiService.submessageToStructureBreadCrumbsString ? '30>40' : '';
				uiService.singleStructureIdValue ? '50' : '';
				fixture = TestBed.createComponent(MimHeaderComponent);
				component = fixture.componentInstance;
				fixture.detectChanges();
			});

			it('should create', () => {
				expect(component).toBeTruthy();
			});
		});
	});
});
