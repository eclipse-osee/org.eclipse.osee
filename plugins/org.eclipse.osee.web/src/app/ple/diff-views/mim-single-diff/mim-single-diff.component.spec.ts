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
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { TransactionService } from 'src/app/transactions/transaction.service';
import { transactionServiceMock } from 'src/app/transactions/transaction.service.mock';

import { MimSingleDiffComponent } from './mim-single-diff.component';

describe('MimSingleDiffComponent', () => {
  let component: MimSingleDiffComponent;
  let fixture: ComponentFixture<MimSingleDiffComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MatFormFieldModule, FormsModule, NoopAnimationsModule],
      providers:[{provide:TransactionService,useValue:transactionServiceMock}],
      declarations: [ MimSingleDiffComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MimSingleDiffComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
