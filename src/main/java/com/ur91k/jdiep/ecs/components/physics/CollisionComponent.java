package com.ur91k.jdiep.ecs.components.physics;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.joml.Vector2f;

public class CollisionComponent implements Component {
    private Body body;
    private Shape shape;
    private float radius;  // For circle colliders
    private Vector2f[] vertices;  // For polygon colliders
    private boolean isDynamic;
    private float density;
    private float friction;
    private float restitution;
    private short categoryBits;  // Collision category
    private short maskBits;      // What this body can collide with
    
    public CollisionComponent() {
        // Default constructor for Ashley's pooling
        this.radius = 1.0f;
        this.isDynamic = true;
        this.density = 1.0f;
        this.friction = 0.3f;
        this.restitution = 0.5f;
        this.categoryBits = 0x0001;  // Default category
        this.maskBits = -1;  // Collide with everything by default
    }
    
    public void init(float radius) {
        this.radius = radius;
        this.vertices = null;
        this.isDynamic = true;
        this.density = 1.0f;
        this.friction = 0.3f;
        this.restitution = 0.5f;
    }
    
    public void init(Vector2f[] vertices) {
        this.vertices = vertices.clone();
        this.isDynamic = true;
        this.density = 1.0f;
        this.friction = 0.3f;
        this.restitution = 0.5f;
    }
    
    public void createBody(World world, Vector2f position, float angle) {
        // Create body definition
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = isDynamic ? BodyType.DYNAMIC : BodyType.STATIC;
        bodyDef.position.set(position.x, position.y);
        bodyDef.angle = angle;
        
        // Create the body
        body = world.createBody(bodyDef);
        
        // Create fixture definition
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = density;
        fixtureDef.friction = friction;
        fixtureDef.restitution = restitution;
        fixtureDef.filter.categoryBits = categoryBits;
        fixtureDef.filter.maskBits = maskBits;
        
        // Create and set the shape
        if (vertices == null) {
            // Circle shape
            CircleShape circleShape = new CircleShape();
            circleShape.setRadius(radius);
            fixtureDef.shape = circleShape;
            shape = circleShape;
        } else {
            // Polygon shape
            PolygonShape polygonShape = new PolygonShape();
            org.jbox2d.common.Vec2[] box2dVertices = new org.jbox2d.common.Vec2[vertices.length];
            for (int i = 0; i < vertices.length; i++) {
                box2dVertices[i] = new org.jbox2d.common.Vec2(vertices[i].x, vertices[i].y);
            }
            polygonShape.set(box2dVertices, vertices.length);
            fixtureDef.shape = polygonShape;
            shape = polygonShape;
        }
        
        // Create the fixture
        body.createFixture(fixtureDef);
        
        // Store reference to owner entity
        body.setUserData(this);
    }
    
    public void destroyBody(World world) {
        if (body != null) {
            world.destroyBody(body);
            body = null;
        }
        if (shape != null) {
            shape = null;
        }
    }
    
    // Getters and setters
    public Body getBody() { return body; }
    public Shape getShape() { return shape; }
    public float getRadius() { return radius; }
    public Vector2f[] getVertices() { return vertices != null ? vertices.clone() : null; }
    public boolean isDynamic() { return isDynamic; }
    public void setDynamic(boolean dynamic) { this.isDynamic = dynamic; }
    public float getDensity() { return density; }
    public void setDensity(float density) { this.density = density; }
    public float getFriction() { return friction; }
    public void setFriction(float friction) { this.friction = friction; }
    public float getRestitution() { return restitution; }
    public void setRestitution(float restitution) { this.restitution = restitution; }
    
    // Collision filtering
    public void setCollisionCategory(short category) { this.categoryBits = category; }
    public void setCollisionMask(short mask) { this.maskBits = mask; }
    public short getCollisionCategory() { return categoryBits; }
    public short getCollisionMask() { return maskBits; }
} 