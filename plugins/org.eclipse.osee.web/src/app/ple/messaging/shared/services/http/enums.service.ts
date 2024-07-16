import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { share, shareReplay } from 'rxjs/operators';
import { apiURL } from 'src/environments/environment';
import { transportType } from '../../types/connection';

@Injectable({
  providedIn: 'root'
})
export class EnumsService {

  constructor (private http: HttpClient) { }

  private _baseURL = apiURL + '/mim/enums/'
  private _periodicities = this.http.get<string[]>(this.baseURL + 'MessagePeriodicities').pipe(share());
  private _rates = this.http.get<string[]>(this.baseURL + 'MessageRates').pipe(share());
  private _types = this.http.get<string[]>(this.baseURL + 'MessageTypes').pipe(share());
  private _categories = this.http.get<string[]>(this.baseURL + 'StructureCategories').pipe(share(), shareReplay(1));
  private _connectionTypes = this.http.get<transportType[]>(this.baseURL + 'ConnectionTypes').pipe(share(),shareReplay(1));
  
  get baseURL() {
    return this._baseURL;
  }
  
  get periodicities() {
    return this._periodicities;
  }

  get rates() {
    return this._rates;
  }

  get types() {
    return this._types;
  }

  get categories() {
    return this._categories;
  }
  get connectionTypes() {
    return this._connectionTypes;
  }
}
