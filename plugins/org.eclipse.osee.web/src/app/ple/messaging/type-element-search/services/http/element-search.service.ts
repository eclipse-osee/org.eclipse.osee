import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { apiURL } from 'src/environments/environment';
import { element } from '../../types/element';

@Injectable({
  providedIn: 'root'
})
export class ElementSearchService {

  constructor (private http: HttpClient) { }
  
  getFilteredElements(branchId:string, typeId:string ) {
    return this.http.get<element[]>(apiURL + '/mim/branch/' + branchId + '/elements/getType/' + typeId);
  }
}
