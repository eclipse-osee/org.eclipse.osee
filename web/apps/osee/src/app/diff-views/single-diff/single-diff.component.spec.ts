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
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { TransactionHistoryService } from '@osee/transaction-history/services';
import { transactionInfoServiceMock } from '@osee/transaction-history/services/testing';
import { SideNavService } from '@osee/shared/services/layout';

import { SingleDiffComponent } from './single-diff.component';

describe('MimSingleDiffComponent', () => {
	let component: SingleDiffComponent;
	let fixture: ComponentFixture<SingleDiffComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [
				MatFormFieldModule,
				FormsModule,
				MatIconModule,
				NoopAnimationsModule,
			],
			providers: [
				{ provide: SideNavService },
				{
					provide: TransactionHistoryService,
					useValue: transactionInfoServiceMock,
				},
			],
		}).compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(SingleDiffComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
