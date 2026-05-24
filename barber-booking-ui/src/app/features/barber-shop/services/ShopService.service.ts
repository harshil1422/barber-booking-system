import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ShopService {

  private baseUrl = 'http://localhost:9090/api/shop';

  constructor(private http: HttpClient) {}

  registerShop(formData: FormData) {
    return this.http.post(`${this.baseUrl}/register`, formData);
  }
}