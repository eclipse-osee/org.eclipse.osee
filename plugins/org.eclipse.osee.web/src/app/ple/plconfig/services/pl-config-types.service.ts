import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { apiURL } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class PlConfigTypesService {

  constructor(private http: HttpClient) { }

  get productApplicabilityTypes() {
    return this.http.get<string[]>(apiURL + '/orcs/types/productApplicability');
  }
}
