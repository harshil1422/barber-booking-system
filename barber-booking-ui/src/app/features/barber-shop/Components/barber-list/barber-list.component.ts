import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Barber } from '../../model/barber.model';
import { RouterModule } from '@angular/router';


@Component({
  selector: 'app-barber-list',
  standalone: true, // 🚨 MOST IMPORTANT
  imports: [CommonModule, RouterModule], // needed for *ngIf, *ngFor later
  templateUrl: './barber-list.component.html',
  styleUrls: ['./barber-list.component.scss'] // ✅ FIXED
})
export class BarberListComponent {
   barbers: Barber[] = [
    {
      id: 1,
      name: 'Royal Cuts',
      location: 'Surat',
      rating: 4.5,
      imageUrl: 'https://via.placeholder.com/300'
    },
    {
      id: 2,
      name: 'Urban Barber',
      location: 'Valsad',
      rating: 4.2,
      imageUrl: 'https://via.placeholder.com/300'
    }
  ];
}
