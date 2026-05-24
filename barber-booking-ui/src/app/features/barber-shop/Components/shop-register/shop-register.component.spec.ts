import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ShopRegisterComponent } from './shop-register.component';

describe('ShopRegisterComponent', () => {
  let component: ShopRegisterComponent;
  let fixture: ComponentFixture<ShopRegisterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ShopRegisterComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ShopRegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
