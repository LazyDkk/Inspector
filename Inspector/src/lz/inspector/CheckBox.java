package lz.inspector;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import processing.core.PConstants;
import processing.core.PVector;
import processing.event.MouseEvent;

public class CheckBox extends Element {
	private boolean value;

	CheckBox(String name, Field field, Object instance, boolean value, PVector size) {
		super(name, field, instance, size);

		setValue(value);
	}

	protected void update() {
		if (isSelected && (field == null || !Modifier.isFinal(field.getModifiers()))) {
			isSelected = false;
			setValue(!value);
		} else {
			setValue(getFieldValue());
		}

		Inspector.applet.fill(80);
		if (Inspector.debug)
			Inspector.applet.rect(pos.x, pos.y, size.x, size.y);

		Inspector.applet.fill(255);
		Inspector.applet.textAlign(PConstants.LEFT, PConstants.CENTER);
		Inspector.applet.text(name, pos.x + Inspector.elementSize.x * .025f, pos.y + Inspector.elementSize.y * .39f);

		if (value)
			Inspector.applet.fill(0, 0, 255);
		else
			Inspector.applet.fill(60);
		
		Inspector.applet.stroke(255);
		Inspector.applet.strokeWeight(Inspector.size.x * .01f * .6f);
		Inspector.applet.rectMode(PConstants.CENTER);
		
		Inspector.applet.rect(pos.x + size.x - size.y * .6f * .5f - Inspector.elementSize.x * .025f, pos.y + size.y * .5f, size.y * .6f,
				size.y * .6f, 4);
		Inspector.applet.rectMode(PConstants.CORNER);
	}

	protected void mouseEvent(MouseEvent event, float x, float y) {
		if (event.getAction() == MouseEvent.PRESS)
			isSelected = true;
	}

	private boolean oldValue;

	protected void setValue(boolean value) {
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

	private boolean getFieldValue() {
		try {
			return field != null && field.get(instance) != null ? (boolean) field.get(instance) : value;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}
	
	public boolean getValue() {
		return value;
	}
}
