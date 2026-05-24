import { Component, Inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ShopService } from '../../services/ShopService.service';

@Component({
  selector: 'app-shop-register',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './shop-register.component.html',
  styleUrl: './shop-register.component.scss'
})
export class ShopRegisterComponent {
  shopForm: FormGroup;
  selectedFiles: File[] = [];

  constructor(private fb: FormBuilder, private shopService: ShopService) {
    this.shopForm = this.fb.group({
      shopName: ['', Validators.required],
      ownerName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      phoneNumber: ['', Validators.required],
      address: ['', Validators.required],
      city: ['', Validators.required],
      state: ['', Validators.required],
      pinCode: ['', Validators.required],
      description: ['']
    });
  }

  onFileSelect(event: any) {
    this.selectedFiles = Array.from(event.target.files);
  }

  submit() {
    if (this.shopForm.invalid) {
      return;
    }

    const formData = new FormData();

    // 🔴 IMPORTANT: JSON part
    formData.append(
      'shop',
      new Blob([JSON.stringify(this.shopForm.value)], {
        type: 'application/json'
      })
    );

    // 🔴 Append images
    this.selectedFiles.forEach(file => {
      formData.append('images', file);
    });

    // call API
     this.shopService.registerShop(formData).subscribe({
      next:()=> alert('Shop registered successfully!'),
      error:()=> alert('Failed to register shop. Please try again.'),
      complete:()=> this.shopForm.reset()
     })
    }
}
