package com.ur91k.jdiep.ecs.components.physics;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.joml.Vector2f;

public class CollisionComponent implements Component {
    private Entity entity;
    private Body body;
    private Shape shape;
    private short categoryBits;
    private short maskBits;
    private float density = 1.0f;
    private float friction = 0.3f;
    private float restitution = 0.5f;
    private float linearDamping = 0.0f;
    private float angularDamping = 0.0f;
    private BodyType bodyType = BodyType.DYNAMIC;
    private boolean isBullet = false;
    
    public CollisionComponent() {
        // Default constructor for Ashley's pooling
    }
    
    public void init(Entity entity, float radius, short categoryBits, short maskBits) {
        this.entity = entity;
        this.categoryBits = categoryBits;
        this.maskBits = maskBits;
        
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(radius);
        this.shape = circleShape;
    }
    
    public void init(Entity entity, Vector2f[] vertices, short categoryBits, short maskBits) {
        this.entity = entity;
        this.categoryBits = categoryBits;
        this.maskBits = maskBits;
        
        PolygonShape polygonShape = new PolygonShape();
        Vec2[] jboxVertices = new Vec2[vertices.length];
        for (int i = 0; i < vertices.length; i++) {
            jboxVertices[i] = new Vec2(vertices[i].x, vertices[i].y);
        }
        polygonShape.set(jboxVertices, vertices.length);
        this.shape = polygonShape;
    }
    
    public void createBody(World world, Vector2f position, float angle) {
        if (body != null) {
            return;  // Body already exists
        }
        
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = bodyType;
        bodyDef.position.set(position.x, position.y);
        bodyDef.angle = angle;
        bodyDef.linearDamping = linearDamping;
        bodyDef.angularDamping = angularDamping;
        bodyDef.bullet = isBullet;
        
        body = world.createBody(bodyDef);
        body.setUserData(this);
        
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = density;
        fixtureDef.friction = friction;
        fixtureDef.restitution = restitution;
        fixtureDef.filter.categoryBits = categoryBits;
        fixtureDef.filter.maskBits = maskBits;
        
        body.createFixture(fixtureDef);
    }
    
    public void destroyBody(World world) {
        if (body != null) {
            world.destroyBody(body);
            body = null;
        }
    }
    
    // Getters
    public Entity getEntity() { return entity; }
    public Body getBody() { return body; }
    public short getCategoryBits() { return categoryBits; }
    public short getMaskBits() { return maskBits; }
    
    // Setters
    public void setDensity(float density) { this.density = density; }
    public void setFriction(float friction) { this.friction = friction; }
    public void setRestitution(float restitution) { this.restitution = restitution; }
    public void setLinearDamping(float damping) { this.linearDamping = damping; }
    public void setAngularDamping(float damping) { this.angularDamping = damping; }
    public void setBodyType(BodyType type) { this.bodyType = type; }
    public void setBullet(boolean bullet) { this.isBullet = bullet; }
} 