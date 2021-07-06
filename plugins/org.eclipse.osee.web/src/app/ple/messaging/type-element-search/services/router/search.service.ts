import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SearchService {

  private _searchTerm: BehaviorSubject<string> = new BehaviorSubject<string>("");
  constructor () { }
  
  get searchTerm() {
    return this._searchTerm;
  }

  set search(value: string) {
    this._searchTerm.next(value)
  }

  get search() {
    return this._searchTerm.getValue();
  }
}
