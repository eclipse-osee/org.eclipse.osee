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
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

import { TwoLayerAddButtonComponent } from './two-layer-add-button.component';

describe('TwoLayerAddButtonComponent', () => {
  let component: TwoLayerAddButtonComponent;
  let fixture: ComponentFixture<TwoLayerAddButtonComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports:[NoopAnimationsModule,MatButtonModule,MatIconModule],
      declarations: [ TwoLayerAddButtonComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TwoLayerAddButtonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
