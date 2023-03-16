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
import { HarnessLoader } from '@angular/cdk/testing';
import { TestbedHarnessEnvironment } from '@angular/cdk/testing/testbed';

import { MultiStructureTableComponent } from './multi-structure-table.component';
import { RouterTestingModule } from '@angular/router/testing';
import { AsyncPipe } from '@angular/common';
import { CurrentStateServiceMock } from '@osee/messaging/shared/testing';
import { StructureTableComponentMock } from '@osee/messaging/structure-tables/testing';
import { CurrentStructureMultiService } from '@osee/messaging/shared/services';
import {
	STRUCTURE_SERVICE_TOKEN,
	MULTI_STRUCTURE_SERVICE,
} from '@osee/messaging/shared/tokens';

let loader: HarnessLoader;

describe('MessageElementInterfaceComponent', () => {
	let component: MultiStructureTableComponent;
	let fixture: ComponentFixture<MultiStructureTableComponent>;

	beforeEach(async () => {
		await TestBed.overrideComponent(MultiStructureTableComponent, {
			set: {
				imports: [
					AsyncPipe,
					StructureTableComponentMock,
					RouterTestingModule,
				],
				providers: [
					{
						provide: CurrentStructureMultiService,
						useValue: CurrentStateServiceMock,
					},
					{
						provide: STRUCTURE_SERVICE_TOKEN,
						useValue: MULTI_STRUCTURE_SERVICE,
					},
				],
			},
		}).compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(MultiStructureTableComponent);
		component = fixture.componentInstance;
		loader = TestbedHarnessEnvironment.loader(fixture);
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
