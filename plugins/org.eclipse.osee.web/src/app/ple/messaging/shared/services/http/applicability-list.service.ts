import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { apiURL } from 'src/environments/environment';
import { applic } from '../../types/NamedId.applic';

@Injectable({
  providedIn: 'root'
})
export class ApplicabilityListService {

  constructor (private http: HttpClient) { }
  
  getApplicabilities(branchId: string | number) {
    return this.http.get<applic[]>(apiURL+'/orcs/branch/'+branchId+'/applic')
  }
}
