import 'zone.js';
import 'zone.js/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AnimatedCounterComponent } from './animated-counter';
import { describe, it, expect, beforeEach, vi } from 'vitest';

describe('AnimatedCounterComponent', () => {
  let component: AnimatedCounterComponent;
  let fixture: ComponentFixture<AnimatedCounterComponent>;

  beforeEach(async () => {
    vi.useFakeTimers();
    await TestBed.configureTestingModule({
      imports: [AnimatedCounterComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(AnimatedCounterComponent);
    component = fixture.componentInstance;
  });

  it('should animate from 0 to target value', async () => {
    fixture.componentRef.setInput('value', 100);
    fixture.detectChanges();

    // Initially it should be 0
    expect(component.displayValue()).toBe(0);

    vi.advanceTimersByTime(300);
    fixture.detectChanges();
    expect(component.displayValue()).toBeGreaterThan(0);
    expect(component.displayValue()).toBeLessThan(100);

    vi.advanceTimersByTime(1000);
    fixture.detectChanges();
    expect(component.displayValue()).toBe(100);
  });

  it('should handle decreasing values', async () => {
    // Set initial
    fixture.componentRef.setInput('value', 100);
    fixture.detectChanges();
    vi.advanceTimersByTime(2000);
    expect(component.displayValue()).toBe(100);

    // Decrease
    fixture.componentRef.setInput('value', 50);
    fixture.detectChanges();

    vi.advanceTimersByTime(2000);
    fixture.detectChanges();
    expect(component.displayValue()).toBe(50);
  });

  it('should return early if target is same as current', () => {
    fixture.componentRef.setInput('value', 0);
    fixture.detectChanges();
    
    const spy = vi.spyOn(window, 'setInterval');
    
    // Set same value again
    fixture.componentRef.setInput('value', 0);
    fixture.detectChanges();
    
    expect(spy).not.toHaveBeenCalled();
  });

  it('should cleanup interval on destroy', () => {
    fixture.componentRef.setInput('value', 100);
    fixture.detectChanges();
    
    const clearIntervalSpy = vi.spyOn(window, 'clearInterval');
    component.ngOnDestroy();
    
    expect(clearIntervalSpy).toHaveBeenCalled();
  });
});
