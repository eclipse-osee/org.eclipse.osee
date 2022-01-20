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
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { concatMap, take } from 'rxjs/operators';
import { apiURL } from 'src/environments/environment';
import { ConfigurationGroupDefinition } from '../types/pl-config-cfggroups';
import { configuration } from '../types/pl-config-configurations';
import { modifyFeature, writeFeature } from '../types/pl-config-features';

import { PlConfigBranchService } from './pl-config-branch-service.service';

describe('PlConfigBranchService', () => {
  let service: PlConfigBranchService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports:[HttpClientTestingModule]
    }
    );
    service = TestBed.inject(PlConfigBranchService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('get branches should return results from /ats/ple/branches/', () => {
    service.getBranches('working').subscribe();
    const req = httpTestingController.expectOne(apiURL+'/ats/ple/branches/'+'working');
    expect(req.request.method).toEqual('GET');
    req.flush({});
    httpTestingController.verify();
  })
  it('get branch applicability should return results from /orcs/applicui/branch', () => {
    service.getBranchApplicability(10).subscribe();
    const req = httpTestingController.expectOne(apiURL+'/orcs/applicui/branch/'+10);
    expect(req.request.method).toEqual('GET');
    req.flush({});
    httpTestingController.verify();
  })
  it('get branch state should return results from /orcs/branches/', () => {
    service.getBranchState(10).subscribe();
    const req = httpTestingController.expectOne(apiURL+'/orcs/branches/'+10);
    expect(req.request.method).toEqual('GET');
    req.flush({});
    httpTestingController.verify();
  })

  it('post addConfiguration should return results from /orcs/branch/ + branchId + /applic/view/', () => {
    service.addConfiguration('10',{} as configuration).subscribe();
    const req = httpTestingController.expectOne(apiURL+'/orcs/branch/' + 10 + '/applic/view/');
    expect(req.request.method).toEqual('POST');
    req.flush({});
    httpTestingController.verify();
  })

  it('delete deleteConfiguration should return results from /orcs/branch/ + branchId + /applic/view/', () => {
    service.deleteConfiguration('20','10').subscribe();
    const req = httpTestingController.expectOne(apiURL+'/orcs/branch/' + 10 + '/applic/view/'+20);
    expect(req.request.method).toEqual('DELETE');
    req.flush({});
    httpTestingController.verify();
  })

  it('put editConfiguration should return results from /orcs/branch/ + branchId + /applic/view/', () => {
    service.editConfiguration('10',{} as configuration).subscribe();
    const req = httpTestingController.expectOne(apiURL+'/orcs/branch/' + 10 + '/applic/view');
    expect(req.request.method).toEqual('PUT');
    req.flush({});
    httpTestingController.verify();
  })

  it('post addFeature should return results from /orcs/branch/ + branchId + /applic/feature/', () => {
    service.addFeature('10',{name:'hello'} as writeFeature).subscribe();
    const req = httpTestingController.expectOne(apiURL+'/orcs/branch/' + 10 + '/applic/feature');
    expect(req.request.method).toEqual('POST');
    req.flush({});
    httpTestingController.verify();
  })

  it('put modifyFeature should return results from /orcs/branch/ + branchId + /applic/feature/', () => {
    service.modifyFeature('10',{name:'hello'} as modifyFeature).subscribe();
    const req = httpTestingController.expectOne(apiURL+'/orcs/branch/' + 10 + '/applic/feature');
    expect(req.request.method).toEqual('PUT');
    req.flush({});
    httpTestingController.verify();
  })

  it('delete deleteFeature should return results from /orcs/branch/ + branchId + /applic/feature/', () => {
    service.deleteFeature('10','20').subscribe();
    const req = httpTestingController.expectOne(apiURL+'/orcs/branch/' + 10 + '/applic/feature/' +20);
    expect(req.request.method).toEqual('DELETE');
    req.flush({});
    httpTestingController.verify();
  })

  it('put modifyConfiguration should return results from /orcs/branch/ + branchId + /applic/view/', () => {
    service.modifyConfiguration('10','20','string').subscribe();
    const req = httpTestingController.expectOne(apiURL+'/orcs/branch/' + 10 + '/applic/view/'+20+"/applic");
    expect(req.request.method).toEqual('PUT');
    req.flush({});
    httpTestingController.verify();
  })

  it('post synchronizeGroup should return results from /orcs/branch/ + branchId + /applic/cfggroup/sync/ +configId', () => {
    service.synchronizeGroup('10','20').subscribe();
    const req = httpTestingController.expectOne(apiURL+'/orcs/branch/' + 10 + '/applic/cfggroup/sync/' +20);
    expect(req.request.method).toEqual('POST');
    req.flush({});
    httpTestingController.verify();
  })

  it('post commitBranch should return results from /orcs/branches/ + branchId + /commit/ + parentBranchId', () => {
    service.commitBranch('10','20',{committer:'Joe Smith',archive:'false'}).subscribe();
    const req = httpTestingController.expectOne(apiURL+'/orcs/branches/' + 10 + '/commit/' + 20);
    expect(req.request.method).toEqual('POST');
    req.flush({});
    httpTestingController.verify();
  })

  it('get getCfgGroups should return results from /orcs/branch/ + branchId +/applic/cfggroup/', () => {
    service.getCfgGroups('10').subscribe();
    const req = httpTestingController.expectOne(apiURL+'/orcs/branch/' + 10 + '/applic/cfggroup/');
    expect(req.request.method).toEqual('GET');
    req.flush({});
    httpTestingController.verify();
  })

  it('get getCfgGroupDetail should return results from /orcs/branch/ + branchId +/applic/cfggroup/def/', () => {
    service.getCfgGroupDetail('10','20').subscribe();
    const req = httpTestingController.expectOne(apiURL+'/orcs/branch/' + 10 + '/applic/cfggroup/def/'+20);
    expect(req.request.method).toEqual('GET');
    req.flush({});
    httpTestingController.verify();
  })

  it('post addConfigurationGroup should return results from /orcs/branch/ + branchId +/applic/cfggroup/', () => {
    service.addConfigurationGroup('10',{} as ConfigurationGroupDefinition).subscribe();
    const req = httpTestingController.expectOne(apiURL+'/orcs/branch/' + 10 + '/applic/cfggroup/');
    expect(req.request.method).toEqual('POST');
    req.flush({});
    httpTestingController.verify();
  })

  it('delete deleteConfigurationGroup should return results from /orcs/branch/ + branchId +/applic/cfggroup/', () => {
    service.deleteConfigurationGroup('10','20').subscribe();
    const req = httpTestingController.expectOne(apiURL+'/orcs/branch/' + 10 + '/applic/cfggroup/'+20);
    expect(req.request.method).toEqual('DELETE');
    req.flush({});
    httpTestingController.verify();
  })

  it('put updateConfigurationGroup should return results from /orcs/branch/ + branchId +/applic/cfggroup/', () => {
    service.updateConfigurationGroup('10', {} as ConfigurationGroupDefinition).subscribe();
    const req = httpTestingController.expectOne(apiURL+'/orcs/branch/' + 10 + '/applic/cfggroup/');
    expect(req.request.method).toEqual('PUT');
    req.flush({});
    httpTestingController.verify();
  })

  it('get getApplicabilityToekn should fetch from cache', () => {
    service.getApplicabilityToken('10', '20').pipe(
      take(1),
      concatMap((app) => service.getApplicabilityToken('10', '20'))
    ).subscribe();
    const req = httpTestingController.expectOne(apiURL+'/orcs/branch/' + 10 + '/applic/applicabilityToken/'+20);
    expect(req.request.method).toEqual('GET');
    req.flush({});
    httpTestingController.verify();
  })
});
