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
import { ActivatedRoute, Params } from '@angular/router';
import { of, Subject } from 'rxjs';
import { BranchIdService } from './services/router/branch-id.service';
import { BranchTypeService } from './services/router/branch-type.service';
import { BranchSelectorDummy } from './testing/MockComponents/BranchSelector';
import { BranchTypeSelectorDummy } from './testing/MockComponents/BranchTypeSelectorMock';
import { ElementTableDummy } from './testing/MockComponents/ElementTable';
import { ElementTableSearchDummy } from './testing/MockComponents/ElementTableSearch';

import { TypeElementSearchComponent } from './type-element-search.component';

describe('TypeElementSearchComponent', () => {
  let component: TypeElementSearchComponent;
  let fixture: ComponentFixture<TypeElementSearchComponent>;
  let params: Subject<Params>;
  let idService: BranchIdService;
  let typeService: BranchTypeService;

  beforeEach(async () => {
    params = new Subject<Params>();
    await TestBed.configureTestingModule({
      providers:[{provide:ActivatedRoute,useValue:{params:params}}],
      declarations: [ TypeElementSearchComponent,ElementTableDummy,ElementTableSearchDummy,BranchSelectorDummy,BranchTypeSelectorDummy]
    })
    .compileComponents();
  });

  beforeEach(() => {
    idService = TestBed.inject(BranchIdService);
    typeService = TestBed.inject(BranchTypeService);
    fixture = TestBed.createComponent(TypeElementSearchComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should create with branchId 8 and type product line', () => {
    params.next({ 'branchId': '8', 'branchType': 'product line' });
    expect(component).toBeTruthy();
    expect(idService.id).toEqual('8');
    expect(typeService.type).toEqual('baseline')
  });

  it('should create with branchId 8 and type working', () => {
    params.next({ 'branchId': '8', 'branchType': 'working' });
    expect(component).toBeTruthy();
    expect(idService.id).toEqual('8');
    expect(typeService.type).toEqual('working')
  });

  it('should create with branchId(when set to -1) "" and type working', () => {
    params.next({ 'branchId': '-1', 'branchType': 'working' });
    expect(component).toBeTruthy();
    expect(idService.id).toEqual('');
    expect(typeService.type).toEqual('working')
  });

  it('should create with branchId(when set to asdf) "" and type working', () => {
    params.next({ 'branchId': 'asdf', 'branchType': 'working' });
    expect(component).toBeTruthy();
    expect(idService.id).toEqual('');
    expect(typeService.type).toEqual('working')
  });

  it('should create with branchId 8 and type "" (when set to asdf)', () => {
    params.next({ 'branchId': '8', 'branchType': 'asdf' });
    expect(component).toBeTruthy();
    expect(idService.id).toEqual('8');
    expect(typeService.type).toEqual('')
  });
});
