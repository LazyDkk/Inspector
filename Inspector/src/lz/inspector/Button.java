package lz.inspector;

import java.lang.reflect.Method;

import processing.core.PConstants;
import processing.core.PVector;
import processing.event.MouseEvent;

public class Button extends Element {
	private Method method;

	protected Button(String label, Method method, Object instance, PVector size) {
		super(label, null, instance, size);

		this.method = method;
	}

	protected void update() {
		Inspector.applet.fill(isSelected ? 40 : 50);
		// rect(inspector.size.x*.5-inspector.size.x*.9*.5,
		// inspector.size.x*.5-inspector.size.x*.9*.5, inspector.size.x*.9, 25, 10);

		Inspector.applet.stroke(20);
		Inspector.applet.strokeWeight(2);
		Inspector.applet.rectMode(PConstants.CENTER);
		// rect(size.x*.502, size.y*.5, size.x*.968, size.y*.95, 5);
		Inspector.applet.rect(pos.x + size.x * .5f, pos.y + size.y * .5f, size.x * .98f, size.y * .95f, 5);
		Inspector.applet.rectMode(PConstants.CORNER);

		// rect(0, 0, size.x, size.y);

		Inspector.applet.fill(230);
		Inspector.applet.textAlign(PConstants.CENTER, PConstants.CENTER);
		Inspector.applet.text(name, pos.x + size.x * .5f, pos.y + size.y * .39f);
	}

	public void call() {
		try {
			method.invoke(instance);
		} catch (Exception e) {
			System.out.println("Warning: Could not call the method - " + e.getCause());
			//System.out.println(e.getCause());
		}
	}

	protected void mouseEvent(MouseEvent event, float x, float y) {
		if (event.getAction() == MouseEvent.PRESS) {
			Inspector.selectedElement = this;
			isSelected = true;
			call();
		} else if (event.getAction() == MouseEvent.RELEASE) {
			Inspector.selectedElement = null;
			isSelected = false;
		}
	}

	public Method getMethod() {
		return method;
	}

	public String getLabel() {
		return name;
	}
}
