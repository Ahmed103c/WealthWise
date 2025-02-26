import { TestBed } from '@angular/core/testing';

import { AllcategoryService } from './allcategory.service';

describe('AllcategoryService', () => {
  let service: AllcategoryService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AllcategoryService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
