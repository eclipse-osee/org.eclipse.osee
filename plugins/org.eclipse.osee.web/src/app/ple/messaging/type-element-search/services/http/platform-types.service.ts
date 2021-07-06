import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { apiURL } from 'src/environments/environment';
import { PlatformType } from '../../types/PlatformType';

@Injectable({
  providedIn: 'root'
})
export class PlatformTypesService {

  constructor (private http: HttpClient) { }
  
  getFilteredTypes(filter: string, branchId: string): Observable<PlatformType[]> {
    return this.http.get<PlatformType[]>(apiURL + "/mim/branch/" + branchId + "/types/filter/" + filter);
  }
}
