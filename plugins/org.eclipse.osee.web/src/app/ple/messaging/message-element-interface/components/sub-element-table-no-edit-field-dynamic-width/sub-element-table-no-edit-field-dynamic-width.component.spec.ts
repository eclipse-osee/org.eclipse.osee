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
import { HighlightFilteredTextDirective } from 'src/app/osee-utils/osee-string-utils/osee-string-utils-directives/highlight-filtered-text.directive';

import { SubElementTableNoEditFieldDynamicWidthComponent } from './sub-element-table-no-edit-field-dynamic-width.component';

describe('SubElementTableNoEditFieldDynamicWidthComponent', () => {
  let component: SubElementTableNoEditFieldDynamicWidthComponent;
  let fixture: ComponentFixture<SubElementTableNoEditFieldDynamicWidthComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SubElementTableNoEditFieldDynamicWidthComponent,HighlightFilteredTextDirective ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SubElementTableNoEditFieldDynamicWidthComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
