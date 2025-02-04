/*********************************************************************
 * Copyright (c) 2025 Boeing
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
import { TeamsTableComponent } from './teams-table.component';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
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
import { MatIcon } from '@angular/material/icon';
import { MatIconButton, MatMiniFabButton } from '@angular/material/button';
import { MatFormField } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MockPersistedStringAttributeInputComponent } from '@osee/attributes/persisted-string-attribute-input/testing';
import { FormsModule } from '@angular/forms';
import { dashboardHttpServiceMock } from '../../../services/dashboard-http.service.mock';
import { DashboardHttpService } from '../../../services/dashboard-http.service';
import { TransactionService } from '@osee/transactions/services';
import { transactionServiceMock } from '@osee/transactions/services/testing';
import { MatPaginator } from '@angular/material/paginator';

describe('TeamsTableComponent', () => {
	let component: TeamsTableComponent;
	let fixture: ComponentFixture<TeamsTableComponent>;

	beforeEach(async () => {
		await TestBed.overrideComponent(TeamsTableComponent, {
			set: {
				imports: [
					FormsModule,
					MatTable,
					MatColumnDef,
					MatCell,
					MatCellDef,
					MatRow,
					MatRowDef,
					MatHeaderCell,
					MatHeaderCellDef,
					MatHeaderRow,
					MatHeaderRowDef,
					MatTooltip,
					MatIcon,
					MatIconButton,
					MatMiniFabButton,
					MatFormField,
					MatInput,
					MatPaginator,
					MockPersistedStringAttributeInputComponent,
				],
			},
		})
			.configureTestingModule({
				imports: [TeamsTableComponent],
				providers: [
					provideNoopAnimations(),
					{
						provide: DashboardHttpService,
						useValue: dashboardHttpServiceMock,
					},
					{
						provide: TransactionService,
						useValue: transactionServiceMock,
					},
				],
			})
			.compileComponents();

		fixture = TestBed.createComponent(TeamsTableComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
