import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BarberSlotsComponent } from './barber-slots.component';

describe('BarberSlotsComponent', () => {
  let component: BarberSlotsComponent;
  let fixture: ComponentFixture<BarberSlotsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BarberSlotsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BarberSlotsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
