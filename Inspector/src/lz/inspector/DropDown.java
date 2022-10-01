package lz.inspector;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;
import processing.event.MouseEvent;

public class DropDown extends Element {
	Object[] values;
	Object value;

	float valueWidth;

	DropDown(String name, Field field, Object instance, Object value, PVector size) {
		super(name, field, instance, size);
		this.value = value;

		values = field.getType().getEnumConstants();

		// Use push/pop so text
		Inspector.applet.textSize(14);

		for (Object constant : values)
			valueWidth = PApplet.max(valueWidth, Inspector.applet.textWidth(String.valueOf(constant)));
	}

	protected void update() {
		if (!isSelected && field != null && !Modifier.isFinal(field.getModifiers()))
			setValue(getFieldValue());

		Inspector.applet.fill(80);
		if (Inspector.debug)
			Inspector.applet.rect(pos.x, pos.y, size.x, size.y);

		Inspector.applet.fill(255);
		Inspector.applet.textAlign(PConstants.LEFT, PConstants.CENTER);
		Inspector.applet.text(name, pos.x + Inspector.elementSize.x * .025f, pos.y + Inspector.elementSize.y * .39f);

		Inspector.applet.textAlign(PConstants.RIGHT, PConstants.CENTER);
		Inspector.applet.text(String.valueOf(value), pos.x + size.x - Inspector.elementSize.x * .025f,
				pos.y + Inspector.elementSize.y * .39f);

		Inspector.applet.fill(40);
		if (isSelected) {
			Inspector.applet.rect(pos.x + size.x - valueWidth - Inspector.elementSize.x * .025f, pos.y + size.y,
					valueWidth, size.y * 2);
			Inspector.applet.textAlign(PConstants.LEFT, PConstants.CENTER);
			Inspector.applet.fill(255);

			for (int i = 0; i < values.length; i++) {
				// rect(pos.x+size.x-valueWidth-inspector.elementSize.x*.025,
				// pos.y+size.y*(i+1), valueWidth, size.y);
				Inspector.applet.text(String.valueOf(values[i]),
						pos.x + size.x - valueWidth - Inspector.elementSize.x * .025f,
						pos.y + Inspector.elementSize.y * .36f + Inspector.elementSize.y * (i + 1));
			}
		}

		// circle(m.x, m.y, 5);
		// circle(m2.x, m2.y, 5);
		// circle(m3.x, m3.y, 5);
	}

	PVector m = new PVector(), m2 = new PVector(), m3 = new PVector();

	public void mouseEvent(MouseEvent event, float x, float y) {
		m.set(x, y);

		if (!isSelected && event.getAction() == MouseEvent.PRESS) {
			Inspector.selectedElement = this;
			isSelected = true;
			// println(values);
		} else if (isSelected && event.getAction() == MouseEvent.PRESS) {
			// inspector.selectedElement = null;
			// isSelected = false;

			m2.set(pos.x + size.x - valueWidth - Inspector.elementSize.x * .025f, pos.y + size.y);
			m3.set(pos.x + size.x - valueWidth - Inspector.elementSize.x * .025f + valueWidth,
					pos.y + size.y * values.length + size.y);

			for (int i = 0; i < values.length; i++) {
				if (isPointInRect(x, y, pos.x + size.x - valueWidth - Inspector.elementSize.x * .025f,
						pos.y + size.y * (i + 1), valueWidth, size.y)) {
					// text(String.valueOf(values[i]), pos.x+size.x-inspector.elementSize.x*.025,
					// pos.y+inspector.elementSize.y*.36+inspector.elementSize.y*(i+1));
					// println(values[i], frameCount);
					// value = values[i];
					setValue(values[i]);
					break;
				}
			}

			Inspector.selectedElement = null;
			isSelected = false;
		}
	}

	Object oldValue;

	void setValue(Object value) {
		this.value = value;

		try {
			if (field != null && !Modifier.isFinal(field.getModifiers()))
				field.set(instance, value);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (oldValue != this.value) {
			oldValue = this.value;
			onValueChanged();
		}
	}

	Object getFieldValue() {
		try {
			return field.get(instance);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public Object getValue() {
		return value;
	}
}
