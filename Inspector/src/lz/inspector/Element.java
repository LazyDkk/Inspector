package lz.inspector;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import processing.core.PApplet;
import processing.core.PVector;
import processing.event.MouseEvent;

public abstract class Element {
	private HashMap<Object, Method> listeners = new HashMap<Object, Method>();

	protected Object instance;
	protected Field field;

	protected boolean isSelected;
	protected String name;
	protected PVector pos;
	protected PVector size;

	protected Element(String name, Field field, Object instance, PVector size) {
		this.name = Inspector.capitalizeFirst(name);
		this.pos = new PVector();
		this.instance = instance;
		this.field = field;
		this.size = size;
	}

	protected Element(String name, PVector size) {
		this(name, null, null, size);
	}

	protected abstract void update();

	protected abstract void mouseEvent(MouseEvent event, float x, float y);

	public void addListener(Object listener) {
		ArrayList<Method> methods = new ArrayList<Method>(Arrays.asList(listener.getClass().getDeclaredMethods()));

		if (!(listener instanceof PApplet))
			methods.addAll(Arrays.asList(listener.getClass().getMethods()));

		for (Method method : methods) {
			Class<?>[] types = method.getParameterTypes();

			if (method.isAnnotationPresent(EventHandler.class) && types.length == 1 && types[0] == getClass()) {
				listeners.put(listener, method);
				method.setAccessible(true);
				return;
			}
		}
	}

	public void removeListener(Object listener) {
		listeners.remove(listener);
	}

	protected void onValueChanged() {
		for (Map.Entry<Object, Method> entry : listeners.entrySet()) {
			try {
				entry.getValue().invoke(entry.getKey(), this);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public String getName() {
		return name;
	}

	protected boolean isPointInRect(float px, float py, float x, float y, float w, float h) {
		return px >= x && px <= x + w && py >= y && py <= y + h;
	}

	protected boolean isPointInRect(float x, float y, float w, float h) {
		return isPointInRect(x, y, pos.x, pos.y, w, h);
	}

	protected boolean isPointInRect(float x, float y) {
		return isPointInRect(x, y, size.x, size.y);
	}
}
