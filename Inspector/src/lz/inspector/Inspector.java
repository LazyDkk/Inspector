package lz.inspector;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

public class Inspector extends Group {

	protected static PApplet applet;
	public static boolean debug; // Remove when done

	protected static PVector elementSize;
	protected static PVector size;

	protected static Element selectedElement;
	private boolean isPressingInspector;

	public Inspector(PApplet applet) {
		super("", new PVector(250, 0));

		Inspector.applet = applet;

		Inspector.size = super.size;
		super.elementSize = new PVector(size.x, 20);
		Inspector.elementSize = super.elementSize;

		applet.registerMethod("draw", this);
		applet.registerMethod("mouseEvent", this);
		applet.registerMethod("keyEvent", this);

		try {
			if (new File(applet.sketchPath() + "/settings.json").exists())
				data = applet.loadJSONObject("settings.json");
		} catch (Exception e) {
			System.out.println(e.getClass().getSimpleName() + ": " + e.getMessage());
		}

		if (data.hasKey("IsCollapsed"))
			setCollapsed(data.getBoolean("IsCollapsed"));
	}

	public void draw() {
		if (isCollapsed)
			return;

		applet.push();
		applet.resetMatrix();
		applet.fill(20);
		applet.stroke(255);
		applet.rectMode(PConstants.CORNER);
		applet.strokeWeight(size.x * .01f);
		applet.rect(size.x * .005f, size.x * .005f, size.x + size.x * .03f, size.y + size.x * .034f, 0, 5, 5, 5);

		applet.translate(size.x * .02f, size.x * .02f);

		update();
		applet.pop();

		// rect(0, 0, size.x+size.x*.04, size.y+size.x*.04);
	}

	public void mouseEvent(MouseEvent event) {
		if (!(!isCollapsed && (event.getAction() == MouseEvent.PRESS || event.getAction() == MouseEvent.RELEASE)))
			return;

		isPressingInspector = (event.getAction() == MouseEvent.PRESS && isPointInInspector(event.getX(), event.getY()));

		if (selectedElement != null)
			selectedElement.mouseEvent(event, event.getX() - size.x * .02f, event.getY() - size.x * .02f);
		else
			mouseEvent(event, event.getX() - size.x * .02f, event.getY() - size.x * .02f);
	}

	public void keyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.PRESS && event.getKeyCode() == 72) // H - Hide
			setCollapsed(!isCollapsed);
	}

	public boolean isPressingInspector() {
		return isPressingInspector;
	}

	public boolean isPointInInspector(float x, float y) {
		return !isCollapsed && isPointInRect(x, y, size.x + size.x * .04f, size.y + size.x * .04f);
	}

	protected static String capitalizeFirst(String string) {
		if (string.isEmpty())
			return string;

		ArrayList<String> list = new ArrayList<String>(Arrays.asList(string.split(" |(?=\\p{Lu})")));
		list.removeAll(Arrays.asList("", null));
		String result = "";

		for (String str : list) {
			result += str.substring(0, 1).toUpperCase() + str.substring(1, str.length()).toLowerCase() + " ";
		}

		return result.substring(0, result.length() - 1);
	}

}
