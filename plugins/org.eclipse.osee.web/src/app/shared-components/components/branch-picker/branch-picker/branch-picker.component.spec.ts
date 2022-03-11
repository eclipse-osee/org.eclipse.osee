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
import { BranchDummySelector } from '../branch-selector/BranchSelector.mock';
import { BranchTypeDummySelector } from '../branch-type-selector/BranchTypeSelector.mock';

import { BranchPickerComponent } from './branch-picker.component';

describe('BranchPickerComponent', () => {
  let component: BranchPickerComponent;
  let fixture: ComponentFixture<BranchPickerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BranchPickerComponent, BranchDummySelector,BranchTypeDummySelector ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BranchPickerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
