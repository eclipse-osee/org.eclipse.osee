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
import { CiDetailsService } from '../../../services/ci-details.service';
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
				],
			},
		}).configureTestingModule({
			imports: [ScriptTableComponent],
			providers: [
				provideNoopAnimations(),
				{ provide: CiDetailsService, useValue: ciDetailsServiceMock },
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
