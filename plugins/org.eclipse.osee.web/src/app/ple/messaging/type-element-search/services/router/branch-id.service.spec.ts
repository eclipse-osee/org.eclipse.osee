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
import { TestBed } from '@angular/core/testing';

import { BranchIdService } from './branch-id.service';

describe('BranchIdService', () => {
  let service: BranchIdService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BranchIdService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('Core Functionality',()=> {
    describe('Branch Id', () => {
      describe('Valid States', () => {
        it('should set id to 8', () => {
          service.id='8'
          expect(service.id).toEqual('8')
        });    
      })
      describe('Invalid States', () => {
        it('should throw an error when NaN is passed', () => {
          expect(() => { service.id = 'asdf' }).toThrow(new Error('Id is not a valid value. Invalid Value:'+'asdf'+' Valid values: ID>0'));
        });
        it('should throw an error when an 0 is passed', () => {
          expect(() => { service.id = '0' }).toThrow(new Error('Id is not a valid value. Invalid Value:'+'0'+' Valid values: ID>0'));
        });
        it('should throw an error when an -1 is passed', () => {
          expect(() => { service.id = '-1' }).toThrow(new Error('Id is not a valid value. Invalid Value:'+'-1'+' Valid values: ID>0'));
        });
        it('should throw an error when an -123456 is passed', () => {
          expect(() => { service.id = '-123456' }).toThrow(new Error('Id is not a valid value. Invalid Value:'+'-123456'+' Valid values: ID>0'));
        });
      })
    })
  })
});
