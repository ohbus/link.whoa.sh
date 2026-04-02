import { Component, input, signal, effect, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-animated-counter',
  standalone: true,
  imports: [CommonModule],
  template: `
    <span class="tabular-nums transition-all duration-500">{{ displayValue() | number }}</span>
  `,
  styles: [`
    :host {
      display: inline-block;
    }
  `]
})
export class AnimatedCounterComponent implements OnDestroy {
  /**
   * The authoritative value to animate towards.
   */
  value = input.required<number>();
  
  /**
   * Animation speed in milliseconds per step.
   */
  speed = input<number>(30);

  displayValue = signal<number>(0);
  private animationInterval: any;

  constructor() {
    effect(() => {
      const target = this.value();
      this.animateToValue(target);
    });
  }

  private animateToValue(target: number) {
    if (this.animationInterval) {
      clearInterval(this.animationInterval);
    }

    const startValue = this.displayValue();
    if (startValue === target) return;

    // Use a variable step size for "dopamine" effect: faster for large gaps
    const difference = Math.abs(target - startValue);
    const stepSize = Math.max(1, Math.ceil(difference / 20));

    this.animationInterval = setInterval(() => {
      const current = this.displayValue();
      if (current < target) {
        this.displayValue.set(Math.min(current + stepSize, target));
      } else if (current > target) {
        this.displayValue.set(Math.max(current - stepSize, target));
      }

      if (this.displayValue() === target) {
        clearInterval(this.animationInterval);
      }
    }, this.speed());
  }

  ngOnDestroy() {
    if (this.animationInterval) {
      clearInterval(this.animationInterval);
    }
  }
}
