import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { apiURL } from 'src/environments/environment';
import { TypesApiResponse } from '../types/ApiResponse';
import { logicalType, logicalTypeFormDetail } from '../types/logicaltype';
import { PlatformType } from '../types/platformType';

@Injectable({
  providedIn: 'root'
})
export class TypesService {
  
  constructor(private http: HttpClient) { }

  /**
   * Gets a list of Platform Types based on a filter condition using the platform types filter GET API
   * @param filter @type {string} filter conditions for finding the correct platform types
   * @param branchId @type {string} branch to fetch from
   * @returns @type {Observable<PlatformType[]>} Observable of array of platform types matching filter conditions (see @type {PlatformType} and @type {Observable})
   */
  getFilteredTypes(filter: string, branchId: string): Observable<PlatformType[]> {
    return this.http.get<PlatformType[]>(apiURL + "/mim/branch/" + branchId + "/types/filter/" + filter);
  }

  /**
   * Updates the attributes of a platform type using the platform types PATCH API, id is required
   * @param body @type {Partial<PlatformType>} attributes to update + id of platform type
   * @param branchId @type {string} branch to fetch from
   * @returns @type {Observable<TypesApiResponse>} observable containing results (see @type {TypesApiResponse} and @type {Observable})
   */
  partialUpdateTypes(body: Partial<PlatformType>, branchId: string) :Observable<TypesApiResponse> {
    return this.http.patch<TypesApiResponse>(apiURL + "/mim/branch/" + branchId + "/types", body);
  }

  /**
   * Creates a new PlatformType using the platform types POST API
   * @param body @type {PlatformType} platform type to create
   * @param branchId @type {string} branch to fetch from
   * @returns @type {Observable<TypesApiResponse>} observable containing results (see @type {TypesApiResponse} and @type {Observable})
   */
  createType(body: PlatformType|Partial<PlatformType>, branchId: string): Observable<TypesApiResponse> {
    return this.http.post<TypesApiResponse>(apiURL + "/mim/branch/" + branchId + "/types", body);
  }

  get logicalTypes() {
    return this.http.get<logicalType[]>(apiURL + "/mim/logicalType");
  }
  getLogicalTypeFormDetail(id:string) {
    return this.http.get<logicalTypeFormDetail>(apiURL + "/mim/logicalType/" + id);
  }
}
