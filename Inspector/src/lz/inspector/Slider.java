package lz.inspector;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;
import processing.event.MouseEvent;

public class Slider extends Element {
	private Settings settings;
	private boolean floored;

	private float internalValue;
	private float value;

	Slider(String name, Field field, Object instance, float value, Settings settings, PVector size) {
		super(name, field, instance, size);

		floored = field != null && (field.getType() == int.class || field.getType() == Integer.class);
		this.settings = settings != null ? settings : new Settings();

		setValue(oldValue = internalValue = value);

		// println(name, value, floored, this.settings.min(), this.settings.max(),
		// this.settings.tick(), this.settings.precision());
		// PApplet.println(name, value, floored, settings.getMin(), settings.getMax(),
		// settings.getTick(), settings.getPrecision());
	}

	protected void update() {
		updateValue();
		show();
	}

	protected void updateValue() {
		if (field != null && Modifier.isFinal(field.getModifiers()))
			return;

		if (isSelected) { // Write value
			internalValue += (Inspector.applet.mouseX - Inspector.applet.pmouseX) * .14f
					* PApplet.pow(settings.getTick(), .88f) * settings.getPrecision() * 2;
			// internalValue += mouseX-pmouseX;

			value = PApplet.round(internalValue / settings.getTick()) * settings.getTick();
		} else { // Read value
			internalValue = value = getFieldValue();
		}

		setValue(value);
	}

	protected void show() {
		Inspector.applet.fill(80);
		Inspector.applet.stroke(0);
		Inspector.applet.strokeWeight(1);
		if (Inspector.debug)
			Inspector.applet.rect(pos.x, pos.y, size.x, size.y);

		Inspector.applet.fill(255);
		Inspector.applet.textAlign(PConstants.LEFT, PConstants.CENTER);
		Inspector.applet.text(name, pos.x + Inspector.elementSize.x * .025f, pos.y + Inspector.elementSize.y * .39f);

		Inspector.applet.textAlign(PConstants.RIGHT, PConstants.CENTER);
		Inspector.applet.text(PApplet.nf(value, 0, 2).replace(",", "."),
				pos.x + size.x - Inspector.elementSize.x * .026f, pos.y + Inspector.elementSize.y * .39f);
	}

	private float oldValue;

	protected void setValue(float value) {
		if (field == null || !Modifier.isFinal(field.getModifiers())) {
			if (!isSelected)
				internalValue = value;

			if (internalValue <= settings.getMin() || internalValue >= settings.getMax())
				internalValue = value = PApplet.constrain(internalValue, settings.getMin(), settings.getMax());
		}

		/*
		 * if ((internalValue <= settings.getMin() || internalValue >=
		 * settings.getMax()) && (field == null ||
		 * !Modifier.isFinal(field.getModifiers()))) internalValue = value =
		 * PApplet.constrain(internalValue, settings.getMin(), settings.getMax());
		 */

		this.value = floored ? PApplet.round(value) : value;

		try {
			if (field != null && !Modifier.isFinal(field.getModifiers())) {
				if (floored)
					field.set(instance, (int) this.value);
				else
					field.set(instance, this.value);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (oldValue != this.value) {
			oldValue = this.value;
			onValueChanged();
		}
	}

	private float getFieldValue() {
		try {
			if (field != null && field.get(instance) != null) {
				if (floored)
					return Modifier.isFinal(field.getModifiers()) ? (int) field.get(instance)
							: PApplet.round(
									PApplet.constrain((int) field.get(instance), settings.getMin(), settings.getMax()));
				else
					return Modifier.isFinal(field.getModifiers()) ? (float) field.get(instance)
							: PApplet.constrain((float) field.get(instance), settings.getMin(), settings.getMax());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0;
	}

	public float getValue() {
		return value;
	}

	protected void mouseEvent(MouseEvent event, float x, float y) {
		if (event.getAction() == MouseEvent.PRESS) {
			Inspector.selectedElement = this;
			isSelected = true;
		} else if (event.getAction() == MouseEvent.RELEASE) {
			Inspector.selectedElement = null;
			isSelected = false;
		}
	}

	public static class Settings {
		protected static final float MIN = -99999;
		protected static final float MAX = 99999;
		protected static final float TICK = 1;
		protected static final float PRECISION = 1;

		private float min;
		private float max;
		private float tick;
		private float precision;

		public Settings(float min, float max, float tick, float precision) {
			setMin(min);
			setMax(max);
			setTick(tick);
			setPrecision(precision);
		}

		protected Settings(SliderSettings settings) {
			this(settings.min(), settings.max(), settings.tick(), settings.precision());
		}

		public Settings() {
			this(MIN, MAX, TICK, PRECISION);
		}

		public void setMin(float min) {
			this.min = PApplet.constrain(min, MIN, MAX);
		}

		public float getMin() {
			return min;
		}

		public void setMax(float max) {
			this.max = PApplet.constrain(max, MIN, MAX);
		}

		public float getMax() {
			return max;
		}

		public void setTick(float tick) {
			this.tick = tick;
		}

		public float getTick() {
			return tick;
		}

		public void setPrecision(float precision) {
			this.precision = precision;
		}

		public float getPrecision() {
			return precision;
		}
	}
}
