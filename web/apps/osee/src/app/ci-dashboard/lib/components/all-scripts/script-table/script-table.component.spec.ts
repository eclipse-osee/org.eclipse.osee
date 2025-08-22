/*********************************************************************
 * Copyright (c) 2023 Boeing
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
import { ScriptTableComponent } from './script-table.component';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { CiDetailsTableService } from '../../../services/ci-details-table.service';
import {
	SubsystemSelectorMockComponent,
	TeamSelectorMockComponent,
	ciDetailsServiceMock,
} from '@osee/ci-dashboard/testing';
import { AsyncPipe } from '@angular/common';
import {
	MatCell,
	MatCellDef,
	MatColumnDef,
	MatHeaderCell,
	MatHeaderCellDef,
	MatHeaderRow,
	MatHeaderRowDef,
	MatRow,
	MatRowDef,
	MatTable,
} from '@angular/material/table';
import { MatTooltip } from '@angular/material/tooltip';
import {
	MatFormField,
	MatLabel,
	MatPrefix,
} from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatIcon } from '@angular/material/icon';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort, MatSortHeader } from '@angular/material/sort';
import { FormsModule } from '@angular/forms';

describe('ScriptTableComponent', () => {
	let component: ScriptTableComponent;
	let fixture: ComponentFixture<ScriptTableComponent>;

	beforeEach(() => {
		TestBed.overrideComponent(ScriptTableComponent, {
			set: {
				imports: [
					SubsystemSelectorMockComponent,
					TeamSelectorMockComponent,
					AsyncPipe,
					FormsModule,
					MatTable,
					MatColumnDef,
					MatHeaderCell,
					MatHeaderCellDef,
					MatTooltip,
					MatCell,
					MatCellDef,
					MatHeaderRow,
					MatHeaderRowDef,
					MatRow,
					MatRowDef,
					MatFormField,
					MatLabel,
					MatInput,
					MatIcon,
					MatPrefix,
					MatPaginator,
					MatSort,
					MatSortHeader,
				],
			},
		}).configureTestingModule({
			imports: [ScriptTableComponent],
			providers: [
				provideNoopAnimations(),
				{
					provide: CiDetailsTableService,
					useValue: ciDetailsServiceMock,
				},
			],
		});
		fixture = TestBed.createComponent(ScriptTableComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
