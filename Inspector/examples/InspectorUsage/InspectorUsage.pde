import lz.inspector.*;

/*  Features
 Slider - float, int, vector - unlimited, constrained - step size
 check box
 color picker
 enum drop down - Done
 
 groups - collaps - hide - Done
 save and load values - values config file - Done
 check if cursor is inside inspector
 */

Inspector inspector;
Shape shape;

void setup() {
  size(600, 400);

  inspector = new Inspector(this);
  //textSize(24);
  //println(g.textSize);

  //println(Type.Sphere.name());
  shape = new Shape(Type.Sphere, width/2, height/2, width*0.2, width*0.2);

  inspector.load(shape);
  
  //inspector.load(shape);
  //inspector.load("Cube", shape);

  //inspector.setCollapsed(true);
}

void draw() {
  background(0);

  shape.update();
}

class Shape {
  public Transform transform;
  public Type type = Type.Rectangle;
  @SliderSettings(min=0, max=255, precision=1.5)
    public float clr = -80;

  Shape(Type type, float x, float y, float w, float h) {
    transform = new Transform(x, y, w, h);
    transform.pos.z = 100;
    this.type = type;
  }

  void update() {
    //println(transform.pos);
    rectMode(CENTER);
    colorMode(HSB);
    fill(clr, 255, 255);
    if (type == Type.Sphere)
      circle(transform.pos.x, transform.pos.y, transform.size.x);
    else if (type == Type.Rectangle)
      rect(transform.pos.x, transform.pos.y, transform.size.x, transform.size.y);
  }
}

class Transform {
  @SliderSettings(min=0, max=500, precision=1)
    public PVector pos;
  @SliderSettings(precision=5)
    public PVector size;

  Transform(float x, float y, float w, float h) {
    pos = new PVector(x, y);
    size = new PVector(w, h);
  }
}

enum Type {
  Sphere, Rectangle;
}
