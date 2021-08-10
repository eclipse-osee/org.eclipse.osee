import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { apiURL } from 'src/environments/environment';
import { PlatformType } from '../types/platformtype';

@Injectable({
  providedIn: 'root'
})
export class PlatformTypeService {

  constructor (private http: HttpClient) { }
  
  /**
   * Gets a list of Platform Types based on a filter condition using the platform types filter GET API
   * @param branchId @type {string} branch to fetch from
   * @returns @type {Observable<PlatformType[]>} Observable of array of platform types matching filter conditions (see @type {PlatformType} and @type {Observable})
   */
   getTypes(branchId: string): Observable<PlatformType[]> {
    return this.http.get<PlatformType[]>(apiURL + "/mim/branch/" + branchId + "/types");
   }
  
  getType(branchId: string, typeId: string) {
    return this.http.get<PlatformType>(apiURL + "/mim/branch/" + branchId + "/types/"+typeId)
  }
}
