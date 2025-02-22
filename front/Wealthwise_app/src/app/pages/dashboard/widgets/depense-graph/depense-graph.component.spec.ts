import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DepenseGraphComponent } from './depense-graph.component';

describe('DepenseGraphComponent', () => {
  let component: DepenseGraphComponent;
  let fixture: ComponentFixture<DepenseGraphComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DepenseGraphComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DepenseGraphComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

