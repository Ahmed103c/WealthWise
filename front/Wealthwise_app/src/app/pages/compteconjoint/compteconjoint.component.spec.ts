import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CompteconjointComponent } from './compteconjoint.component';

describe('CompteconjointComponent', () => {
  let component: CompteconjointComponent;
  let fixture: ComponentFixture<CompteconjointComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CompteconjointComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CompteconjointComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
