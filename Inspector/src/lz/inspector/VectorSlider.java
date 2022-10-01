package lz.inspector;

import java.lang.reflect.Field;

import lz.inspector.Slider.Settings;
import processing.core.PConstants;
import processing.core.PVector;
import processing.data.JSONObject;
import processing.event.MouseEvent;

public class VectorSlider extends Element {
	protected JSONObject data = new JSONObject();

	protected Slider[] sliders;
	private PVector value;

	protected VectorSlider(String name, Field field, Object instance, PVector value, Settings settings, PVector size) {
		super(name, field, instance, size);

		try {
			if (field != null) {
				sliders = new Slider[3];
				int i = 0;

				for (Field componentField : value.getClass().getFields()) {
					// sliders[i] = new Slider(componentField.getName(), componentField, value,
					// (float) componentField.get(value), settings, new
					// PVector(inspector.size.x*.24, inspector.elementSize.y));
					sliders[i] = new Slider(componentField.getName(), componentField, value,
							(float) componentField.get(value), settings,
							new PVector((size.x - Inspector.elementSize.x * .04f) / 3, Inspector.elementSize.y));
					sliders[i++].addListener(this);
				}
			} else {
				sliders = new Slider[0];
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		setValue(oldValue = value);
	}

	protected void update() {
		setValue(getFieldValue());

		Inspector.applet.fill(80);
		if (Inspector.debug)
			Inspector.applet.rect(pos.x, pos.y, size.x, size.y);

		Inspector.applet.fill(255);
		Inspector.applet.textAlign(PConstants.LEFT, PConstants.CENTER);
		Inspector.applet.text(name, pos.x + Inspector.elementSize.x * .025f, pos.y + Inspector.elementSize.y * .39f);

		for (int i = 0; i < sliders.length; i++) {
			// sliders[i].pos.set(pos.y+size.x-sliders[0].size.x*(sliders.length-i), pos.y);
			sliders[i].pos.set(pos.x + Inspector.elementSize.x * .04f + sliders[i].size.x * i,
					pos.y + Inspector.elementSize.y);
			sliders[i].update();
		}
	}

	protected void mouseEvent(MouseEvent event, float x, float y) {
		for (Slider slider : sliders) {
			if (slider.isPointInRect(x, y)) {
				slider.mouseEvent(event, x, y);
				break;
			}
		}
	}

	private PVector oldValue;

	protected void setValue(PVector value) {
		this.value = value;

		try {
			if (field != null)
				field.set(instance, value);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (oldValue != value) {
			oldValue = value;

			for (Slider slider : sliders) {
				slider.instance = value;
				slider.updateValue();
			}
		}
	}

	private PVector getFieldValue() {
		try {
			if (field == null)
				return getValue();
			else if (field.get(instance) != null)
				return (PVector) field.get(instance);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new PVector();
	}

	public PVector getValue() {
		return value;
	}

	@EventHandler
	private void onSliderChanged(Slider slider) {
		data.setFloat(slider.getName(), slider.getValue());
		onValueChanged();
	}
}
