import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GrowthWidgetComponent } from './growth-widget.component';

describe('GrowthWidgetComponent', () => {
  let component: GrowthWidgetComponent;
  let fixture: ComponentFixture<GrowthWidgetComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GrowthWidgetComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GrowthWidgetComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
