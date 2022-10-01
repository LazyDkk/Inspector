package lz.inspector;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import lz.inspector.Slider.Settings;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;
import processing.data.JSONObject;
import processing.event.MouseEvent;

public class Group extends Element {
	private HashMap<Class<? extends Element>, HashMap<String, Element>> elementsMap = new HashMap<Class<? extends Element>, HashMap<String, Element>>();
	private ArrayList<Element> elements = new ArrayList<Element>();

	protected JSONObject data = new JSONObject();

	protected boolean isCollapsed;
	private int layer;

	protected PVector elementSize;
	private Group parent;

	protected Group(String name, PVector size) {
		super(name, size);
	}

	protected void update() {
		// fill(map(size.x, 0, inspector.size.x, 255, 100));
		Inspector.applet.stroke(0);
		Inspector.applet.strokeWeight(1);
		Inspector.applet.fill(60);
		// rect(pos.x, pos.y, size.x, size.y);

		Inspector.applet.fill(255);
		Inspector.applet.textSize(14);
		Inspector.applet.textAlign(PConstants.LEFT, PConstants.CENTER);

		if (!name.isEmpty())
			Inspector.applet.text(name, pos.x + Inspector.elementSize.x * .025f,
					pos.y + Inspector.elementSize.y * .39f);

		float dx = !name.isEmpty() ? Inspector.size.x * .04f : 0;
		float dy = size.y;

		if (!isCollapsed) {
			for (int i = elements.size() - 1; i >= 0; i--) {
				Element element = elements.get(i);

				dy -= element.size.y;
				element.pos.set(pos.x + dx, pos.y + dy);
				element.update();
			}
		}
	}

	public Group addGroup(String name) {
		if (layer >= 3) {
			System.out.println("Error: Can only stack up to 3 groups at a time.");
			return null;
		}

		if (getGroup(name) != null) {
			System.out.format("Error: A Group with the name '%s' already exist in the group '%s'.%n",
					Inspector.capitalizeFirst(name), this instanceof Inspector ? "Main" : this.name);
			return null;
		}

		Group group = new Group(name, new PVector(elementSize.x, !name.isEmpty() ? Inspector.elementSize.y : 0));
		group.elementSize = new PVector(group.size.x - (!group.name.isEmpty() ? Inspector.size.x * .04f : 0),
				Inspector.elementSize.y);
		group.layer = layer + 1;
		group.parent = this;

		addElement(group);

		if (data.hasKey(group.name))
			group.data = data.getJSONObject(group.name);

		if (group.data.hasKey("IsCollapsed"))
			group.setCollapsed(group.data.getBoolean("IsCollapsed"));

		return group;
	}

	private Slider addSlider(String name, Field field, Object instance, float value, float min, float max, float tick,
			float precision) {
		if (getSlider(name) != null) {
			System.out.format("Error: A Slider with the name '%s' already exist in the group '%s'.%n",
					Inspector.capitalizeFirst(name), this instanceof Inspector ? "Main" : this.name);
			return null;
		}

		Settings settings = null;

		if (field != null && field.isAnnotationPresent(SliderSettings.class))
			settings = new Slider.Settings(field.getAnnotation(SliderSettings.class));
		else
			settings = new Settings(min, max, tick, precision);

		Slider slider = new Slider(name, field, instance, value, settings, elementSize);

		if (data.hasKey(slider.getName()) && (field == null || !Modifier.isFinal(field.getModifiers())))
			slider.setValue(data.getFloat(slider.getName()));

		addElement(slider);

		return slider;
	}

	public Slider addSlider(String name, float value, float min, float max, float tick, float precision) {
		return addSlider(name, null, null, value, min, max, tick, precision);
	}

	public Slider addSlider(String name, float value, float min, float max) {
		return addSlider(name, value, min, max, Slider.Settings.TICK, Slider.Settings.PRECISION);
	}

	public Slider addSlider(String name, float min, float max) {
		return addSlider(name, 0, min, max);
	}

	public Slider addSlider(String name, float value) {
		return addSlider(name, value, Slider.Settings.MIN, Slider.Settings.MAX);
	}

	public Slider addSlider(String name) {
		return addSlider(name, 0);
	}

	private Slider addSlider(Field field, Object instance) throws IllegalAccessException {
		boolean floored = field.getType() == int.class || field.getType() == Integer.class;
		float value = field.get(instance) != null ? (floored ? (int) field.get(instance) : (float) field.get(instance))
				: 0;
		return addSlider(field.getName(), field, instance, value, Slider.Settings.MIN, Slider.Settings.MAX,
				Slider.Settings.TICK, Slider.Settings.PRECISION);
	}

	private VectorSlider addVectorSlider(String name, Field field, Object instance, PVector value, float min, float max,
			float tick, float precision) {
		if (getSlider(name) != null) {
			System.out.format("Error: A VectorSlider with the name '%s' already exist in the group '%s'.%n",
					Inspector.capitalizeFirst(name), this instanceof Inspector ? "Main" : this.name);
			return null;
		}

		Settings settings = null;

		if (field != null && field.isAnnotationPresent(SliderSettings.class))
			settings = new Slider.Settings(field.getAnnotation(SliderSettings.class));
		else
			settings = new Settings(min, max, tick, precision);

		VectorSlider vectorSlider = new VectorSlider(name, field, instance, value, settings,
				new PVector(elementSize.x, elementSize.y * 2));

		if (data.hasKey(vectorSlider.getName()))
			vectorSlider.data = data.getJSONObject(vectorSlider.getName());

		for (Slider slider : vectorSlider.sliders)
			if (vectorSlider.data.hasKey(slider.getName()))
				slider.setValue(vectorSlider.data.getFloat(slider.getName()));

		addElement(vectorSlider);

		return vectorSlider;
	}

	public VectorSlider addVectorSlider(String name, PVector value, float min, float max, float tick, float precision) {
		return addVectorSlider(name, null, null, value, min, max, tick, precision);
	}

	public VectorSlider addVectorSlider(String name, PVector value, float min, float max) {
		return addVectorSlider(name, value, min, max, Slider.Settings.TICK, Slider.Settings.PRECISION);
	}

	public VectorSlider addVectorSlider(String name, float min, float max) {
		return addVectorSlider(name, new PVector(), min, max);
	}

	public VectorSlider addVectorSlider(String name, PVector value) {
		return addVectorSlider(name, value, Slider.Settings.MIN, Slider.Settings.MAX);
	}

	public VectorSlider addVectorSlider(String name) {
		return addVectorSlider(name, new PVector());
	}

	private VectorSlider addVectorSlider(Field field, Object instance) throws IllegalAccessException {
		return addVectorSlider(field.getName(), field, instance,
				field.get(instance) != null ? (PVector) field.get(instance) : new PVector(), Slider.Settings.MIN,
				Slider.Settings.MAX, Slider.Settings.TICK, Slider.Settings.PRECISION);
	}

	private CheckBox addCheckBox(String name, Field field, Object instance, boolean value) {
		if (getSlider(name) != null) {
			System.out.format("Error: A CheckBox with the name '%s' already exist in the group '%s'.%n",
					Inspector.capitalizeFirst(name), this instanceof Inspector ? "Main" : this.name);
			return null;
		}

		CheckBox checkbox = new CheckBox(name, field, instance, value, elementSize);

		if (data.hasKey(checkbox.getName()) && (field == null || !Modifier.isFinal(field.getModifiers())))
			checkbox.setValue(data.getBoolean(checkbox.getName()));

		addElement(checkbox);

		return checkbox;
	}

	public CheckBox addCheckBox(String name, boolean value) {
		return addCheckBox(name, null, null, value);
	}

	public CheckBox addCheckBox(String name) {
		return addCheckBox(name, false);
	}

	private CheckBox addCheckBox(Field field, Object instance) throws IllegalAccessException {
		return addCheckBox(field.getName(), field, instance,
				field.get(instance) != null ? (boolean) field.get(instance) : false);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private DropDown addDropDown(Field field, Object instance) throws IllegalAccessException {
		if (getSlider(name) != null) {
			System.out.format("Error: A DropDown with the name '%s' already exist in the group '%s'.%n",
					Inspector.capitalizeFirst(name), this instanceof Inspector ? "Main" : this.name);
			return null;
		}

		DropDown dropdown = new DropDown(field.getName(), field, instance, field.get(instance), elementSize);

		if (data.hasKey(dropdown.getName()) && !Modifier.isFinal(field.getModifiers()))
			dropdown.setValue(Enum.valueOf((Class<Enum>) field.getType(), data.getString(dropdown.getName())));

		addElement(dropdown);

		return dropdown;
	}

	private Button addButton(String buttonLabel, Method method, Object instance) {
		Button button = new Button(buttonLabel, method, instance, elementSize);
		addElement(button);

		return button;
	}

	public Button addButton(String buttonLabel, String methodName, Object instance) {
		Method method = null;

		try {
			method = instance instanceof PApplet ? instance.getClass().getDeclaredMethod(methodName)
					: instance.getClass().getMethod(methodName);
		} catch (Exception e) {
			System.out.format("Warning: Could not find a method with the name %s in class %s.%n", methodName,
					instance.getClass().getSimpleName());
		}

		return addButton(buttonLabel, method, instance);
	}

	public Button addButton(String methodName, Object instance) {
		return addButton(methodName, methodName, instance);
	}

	private Element addElement(Element element) {
		if (!(element instanceof Button)) {
			HashMap<String, Element> map = elementsMap.get(element.getClass());

			if (map == null)
				elementsMap.put(element.getClass(), map = new HashMap<String, Element>());
			map.put(element.name.toUpperCase(), element);
		}

		updateHeight(element.size.y);
		element.addListener(this);
		elements.add(element);

		return element;
	}

	private void updateHeight(float dy) {
		if (isCollapsed)
			return;

		size.y += dy;

		if (parent != null && !parent.isCollapsed)
			parent.updateHeight(dy);
	}

	public void setCollapsed(boolean collapsed) {
		if (collapsed == isCollapsed)
			return;

		if (collapsed) {
			updateHeight(Inspector.elementSize.y - size.y);
			size.y = !name.isEmpty() ? Inspector.elementSize.y : 0;
			isCollapsed = true;
		} else {
			float dy = 0;

			for (Element element : elements)
				dy += element.size.y;

			isCollapsed = false;
			updateHeight(dy);
		}

		if (Inspector.selectedElement != null) { // Potential problem with Color Picker staying open or other elements
													// not deselecting correctly
			Inspector.selectedElement.isSelected = false;
			Inspector.selectedElement = null;
		}

		data.setBoolean("IsCollapsed", isCollapsed);
		saveData();
	}

	public boolean isCollapsed() {
		return isCollapsed;
	}

	protected void mouseEvent(MouseEvent event, float x, float y) {
		if (!name.isEmpty() && isPointInRect(x, y, size.x, Inspector.elementSize.y)
				&& event.getAction() == MouseEvent.PRESS) {
			setCollapsed(!isCollapsed);
			Inspector.selectedElement = this;
			isSelected = true;
			return;
		} else if (!name.isEmpty() && event.getAction() == MouseEvent.RELEASE) {
			Inspector.selectedElement = null;
			isSelected = false;
			return;
		}

		for (Element element : elements) {
			if (element.isPointInRect(x, y)) {
				element.mouseEvent(event, x, y);
				break;
			}
		}
	}

	public Group load(String name, Object instance) {
		Group group = instance instanceof PApplet ? this : addGroup(name);

		if (group == null) {
			System.out.format("Error: Could not load object '%s'.", instance.getClass().getSimpleName());
			return null;
		}

		try {
			for (Field field : instance instanceof PApplet ? instance.getClass().getDeclaredFields()
					: instance.getClass().getFields()) {
				if (!Modifier.isPublic(field.getModifiers()))
					continue;

				Class<?> type = field.getType();
				field.setAccessible(true);

				if (type == float.class || type == Float.class || type == int.class || type == Integer.class) {
					group.addSlider(field, instance);
				} else if (type == PVector.class) {
					group.addVectorSlider(field, instance);
				} else if (type == boolean.class || type == Boolean.class) {
					group.addCheckBox(field, instance);
				} else if (type.isEnum()) {
					group.addDropDown(field, instance);
				} else if (type.isMemberClass() && field.get(instance) != null) {
					group.load(field.get(instance));
				} /*
					 * else {
					 * 
					 * // Color Picker - Check annotation on int variable
					 * 
					 * //println(field); }
					 */
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return group;
	}

	public Group load(Object instance) {
		return load(instance.getClass().getSimpleName(), instance);
	}

	private void saveData() {
		if (parent != null) {
			parent.data.setJSONObject(name, data);
			parent.saveData();
		} else {
			Inspector.applet.saveJSONObject(data, "settings.json");
		}
	}

	@EventHandler
	private void onSliderChanged(Slider slider) {
		data.setFloat(slider.getName(), slider.getValue());
		saveData();
	}

	@EventHandler
	private void onVectorSliderChanged(VectorSlider vectorSlider) {
		data.setJSONObject(vectorSlider.getName(), vectorSlider.data);
		saveData();
	}

	@EventHandler
	private void onCheckBoxChanged(CheckBox checkbox) {
		data.setBoolean(checkbox.getName(), checkbox.getValue());
		saveData();
	}

	@EventHandler
	private void onDropDown(DropDown dropdown) {
		data.setString(dropdown.getName(), String.valueOf(dropdown.getValue()));
		saveData();
	}

	public Element getElement(String name) {
		for (Entry<Class<? extends Element>, HashMap<String, Element>> entry : elementsMap.entrySet()) {
			if (entry.getValue().containsKey(name.toUpperCase()))
				return entry.getValue().get(name.toUpperCase());
		}

		return null;
	}

	public Group getGroup(String name) {
		HashMap<String, Element> map = elementsMap.get(Group.class);
		return map != null ? (Group) map.get(name.toUpperCase()) : null;
	}

	public Slider getSlider(String name) {
		HashMap<String, Element> map = elementsMap.get(Slider.class);
		return map != null ? (Slider) map.get(name.toUpperCase()) : null;
	}

	public VectorSlider getVectorSlider(String name) {
		HashMap<String, Element> map = elementsMap.get(VectorSlider.class);
		return map != null ? (VectorSlider) map.get(name.toUpperCase()) : null;
	}

	public CheckBox getCheckBox(String name) {
		HashMap<String, Element> map = elementsMap.get(CheckBox.class);
		return map != null ? (CheckBox) map.get(name.toUpperCase()) : null;
	}

	public DropDown getDropDown(String name) {
		HashMap<String, Element> map = elementsMap.get(DropDown.class);
		return map != null ? (DropDown) map.get(name.toUpperCase()) : null;
	}

	public Button getButton(String name) {
		HashMap<String, Element> map = elementsMap.get(Button.class);
		return map != null ? (Button) map.get(name.toUpperCase()) : null;
	}

	public Button getButtonByMethod(String methodName) {
		if (!elementsMap.containsKey(Button.class))
			return null;

		for (Map.Entry<String, Element> entry : elementsMap.get(Button.class).entrySet()) {
			Button button = (Button) entry.getValue();

			if (button.getMethod() != null
					&& button.getMethod().getName().toUpperCase().equals(methodName.toUpperCase())) {
				return button;
			}
		}

		return null;
	}
}
