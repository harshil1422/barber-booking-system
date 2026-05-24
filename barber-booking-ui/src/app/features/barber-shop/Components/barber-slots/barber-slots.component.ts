import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { Slot } from '../../model/slot.model';
import { SlotService } from '../../services/SlotService.service';
import { AppointmentService } from '../../services/AppointmentService.service';



@Component({
  selector: 'app-barber-slots',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './barber-slots.component.html',
  styleUrls: ['./barber-slots.component.scss']
})
export class BarberSlotsComponent implements OnInit {

  barberId!: number;
  slots: Slot[] = [];


  
  constructor(private route: ActivatedRoute, 
           private slotService: SlotService,
           private appointmentService: AppointmentService
           
  ) {}

  ngOnInit(): void {
    this.barberId = Number(this.route.snapshot.paramMap.get('id'));
     this.slotService.getSlotsByBarber(this.barberId)
    .subscribe((slots :Slot[]) => this.slots = slots);
  }

  onSlotSelect(slot: Slot): void {
  if (slot.status !== 'AVAILABLE') {
    return;
  }

  this.slotService.lockSlot(slot.id).subscribe({
    next: () => {
      slot.status = 'LOCKED';
      alert('Slot locked. Proceed to booking.');
    },
    error: () => {
      alert('Slot already booked by someone else.');
    }
  });
}

}
