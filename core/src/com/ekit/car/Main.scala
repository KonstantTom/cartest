package com.ekit.car

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.PolygonShape
import java.util.ArrayList
import com.badlogic.gdx.physics.box2d.CircleShape
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable
import com.badlogic.gdx.scenes.scene2d.ui.Image
import box2dLight.RayHandler
import box2dLight.PointLight
import com.badlogic.gdx.graphics.Color

/**
 * Car in whole Box2D world.
 * W -- move forward
 * S -- move back
 * @author Konstant
 */
class Main extends ApplicationAdapter {

  /**Box 2D renderer*/
  var renderer: Box2DDebugRenderer = null

  /**Box 2D world*/
  var world: World = null

  /**Game camera*/
  var camera: OrthographicCamera = null

  /**Sprite batch for drawing*/
  var batch: SpriteBatch = null

  /**Car construction*/
  var carbody, wheel1, wheel2: Body = null

  /**Road body*/
  var road: Body = null

  /**Drawables*/
  var dcar, dwheel, droad: Drawable = null

  /**Images*/
  var icar, iwheel1, iwheel2, iroad: Image = null

  /**Light handler*/
  var light: RayHandler = null

  /**
   * Init application
   */
  override def create = {

    //Init renderer
    renderer = new Box2DDebugRenderer

    //Init camera
    camera = new OrthographicCamera
    camera setToOrtho (false, 40, 30)

    //Init batch
    batch = new SpriteBatch

    //Load textures
    dcar = new TextureRegionDrawable(
      new TextureRegion(new Texture("car.png")))
    dwheel = new TextureRegionDrawable(
      new TextureRegion(new Texture("wheel.png")))
    droad = new TextureRegionDrawable(
      new TextureRegion(new Texture("road.jpg")))

    //Init images
    icar = new Image(dcar)
    iwheel1 = new Image(dwheel)
    iwheel2 = new Image(dwheel)
    iroad = new Image(droad)

    icar setWidth 4
    icar setHeight 2
    icar setOrigin (2, 1)

    iroad setWidth 80
    iroad setHeight 10
    iroad setX -20
    iroad setY 0

    iwheel1 setWidth 1.2f
    iwheel1 setHeight 1.2f
    iwheel1 setOrigin (0.6f, 0.6f)

    iwheel2 setWidth 1.2f
    iwheel2 setHeight 1.2f
    iwheel2 setOrigin (0.6f, 0.6f)

    //Init world
    world = new World(new Vector2(0, -9.8f), true)

    //Init light
    light = new RayHandler(world)
    light setShadows true

    //Create road body
    var bodyDef = new BodyDef
    bodyDef.`type` = BodyType.StaticBody
    bodyDef.position.set(20, 5)
    road = world createBody bodyDef
    var fixDef = new FixtureDef
    fixDef.restitution = 0.3f
    fixDef.friction = 0.4f
    fixDef.density = 0f
    var shape = new PolygonShape
    shape setAsBox (40, 5)
    fixDef.shape = shape
    road createFixture fixDef

    //Create main car body
    bodyDef.`type` = BodyType.DynamicBody
    bodyDef.position.set(15, 15)
    carbody = world createBody bodyDef
    fixDef.density = 100
    var car_shape_verticles = new ArrayList[Vector2]

    car_shape_verticles add new Vector2(-2, -1)
    car_shape_verticles add new Vector2(-2, 0)
    car_shape_verticles add new Vector2(-1.5f, 0)
    car_shape_verticles add new Vector2(-1, 1)
    car_shape_verticles add new Vector2(1, 1)
    car_shape_verticles add new Vector2(1.5f, 0)
    car_shape_verticles add new Vector2(2, 0)
    car_shape_verticles add new Vector2(2, -1)

    shape.set(car_shape_verticles.toArray(new Array[Vector2](1)))
    fixDef.shape = shape
    carbody createFixture fixDef

    //Create wheels
    bodyDef.position.set(14, 14.25f)
    wheel1 = world createBody bodyDef

    bodyDef.position.set(16, 14.25f)
    wheel2 = world createBody bodyDef

    fixDef.density = 10
    var wheel_shape = new CircleShape
    wheel_shape setRadius 0.6f
    fixDef.shape = wheel_shape

    wheel1 createFixture fixDef
    wheel2 createFixture fixDef

    //Create joint
    var jointDef = new DistanceJointDef
    jointDef.initialize(carbody, wheel1,
      new Vector2(14, 14.25f), new Vector2(14, 14.25f))
    world createJoint jointDef
    jointDef.initialize(carbody, wheel2,
      new Vector2(16, 14.25f), new Vector2(16, 14.25f))
    world createJoint jointDef

    //Dispose shapes
    shape dispose ()
    wheel_shape dispose ()
    
    new PointLight(light, 128, new Color(1,1,1,1), 16, 20, 15);

  }

  /**
   * Render screen
   */
  override def render = {

    //Clear screen
    Gdx.gl.glClearColor(0, 0, 0, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    //Handle users input
    HandleInput

    //Update camera
    camera update ()

    //Connect batch and camera
    batch setProjectionMatrix camera.combined

    //Update world
    world step (Math.min(Gdx.graphics.getDeltaTime, 0.25f), 6, 2)

    //Update images
    icar setX carbody.getPosition.x - 2
    icar setY carbody.getPosition.y - 1
    icar setRotation Math.toDegrees(carbody.getAngle.
      asInstanceOf[Double]).asInstanceOf[Float]

    iwheel1 setX wheel1.getPosition.x - 0.6f
    iwheel1 setY wheel1.getPosition.y - 0.6f
    iwheel1 setRotation Math.toDegrees(wheel1.getAngle.
      asInstanceOf[Double]).asInstanceOf[Float]

    iwheel2 setX wheel2.getPosition.x - 0.6f
    iwheel2 setY wheel2.getPosition.y - 0.6f
    iwheel2 setRotation Math.toDegrees(wheel2.getAngle.
      asInstanceOf[Double]).asInstanceOf[Float]

    //Draw world
    /*batch begin ()

    iroad draw (batch, 1)
    icar draw (batch, 1)
    iwheel1 draw (batch, 1)
    iwheel2 draw (batch, 1)

    batch end ()*/

    //Draw world with debug renderer
    renderer render (world, camera.combined)
    //Configure and draw light
    
    light setCombinedMatrix camera.combined
    light update;
    light render;

  }

  /**Handle keyboard input*/
  private def HandleInput = {
    if (Gdx.input.isKeyPressed(Keys.W)) wheel2 applyTorque (-360, true)
    if (Gdx.input.isKeyPressed(Keys.S)) wheel2 applyTorque (360, true)
  }

  /**Dispose resources*/
  override def dispose = {
    batch dispose;
    world dispose;
    light dispose;
  }

}