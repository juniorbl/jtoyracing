package net.juniorbl.jtoyracing.entity.vehicle;

import java.util.ArrayList;
import java.util.List;

import net.juniorbl.jtoyracing.audio.AudioConfig;
import net.juniorbl.jtoyracing.core.Health;
import net.juniorbl.jtoyracing.core.HealthObserver;
import net.juniorbl.jtoyracing.util.ModelUtil;
import net.juniorbl.jtoyracing.util.ResourcesPath;

import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jmex.audio.AudioTrack;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.PhysicsSpace;
import com.jmex.physics.material.Material;

/**
 * A vehicle of the game. May be a superclass of various kids of vehicles in the future.
 *
 * @version 1.0 Aug 11, 2007
 * @author Carlos Luz Junior
 */
public class Vehicle extends Node implements Health {

	/**
	 * Maximum value of health.
	 */
	public static final int MAX_VALUE_HEALTH = 500;

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 2049305191562715341L;

	/**
	 * Amount of health that is decreased.
	 */
	private static final int DECREASE_HEALTH_VALUE = 25;

	/**
	 * Scale of the chassis.
	 */
	private static final float CHASSIS_SCALE = .5f;

	/**
	 * Mass of the chassis.
	 */
	private static final float CHASSIS_MASS = 40;

	/**
	 * Location of the front suspension.
	 */
	private static final Vector3f FRONT_SUSPENSION_LOCATION = new Vector3f(-.2f, -1.7f, 0);

	/**
	 * Location of the rear suspension.
	 */
	private static final Vector3f REAR_SUSPENSION_LOCATION = new Vector3f(3.2f, -1.6f, 0);

	/**
	 * Minimum value of health.
	 */
	private static final int MIN_VALUE_HEALTH = 0;

	/**
	 * Health observers.
	 */
	private List <HealthObserver> healthObservers = new ArrayList<HealthObserver>();

	/**
	 * Health of a vehicle. The health decreases with time and is recharged when a checkpoint is reached.
	 */
	private int health;

	/**
	 * Chassis of a vehicle.
	 */
	private DynamicPhysicsNode chassis;

	/**
	 * Rear suspension.
	 */
	private Suspension rearSuspension;

	/**
	 * Front suspension.
	 */
	private Suspension frontSuspension;

	/**
	 * Sound of the engine.
	 */
	private AudioTrack engineSound;

	/**
	 * Constructs a vehicle.
	 */
	public Vehicle(PhysicsSpace physicsSpace, Vector3f chassisLocation) {
		health = MAX_VALUE_HEALTH;
		createChassis(physicsSpace, chassisLocation);
		createSuspension(physicsSpace);
		loadEngineSound();
	}

	/**
	 * Creates the chassis.
	 */
	private void createChassis(PhysicsSpace physicsSpace, Vector3f chassisLocation) {
		chassis = physicsSpace.createDynamicNode();
		chassis.setLocalTranslation(chassisLocation);
		chassis.generatePhysicsGeometry();
		chassis.attachChild(
				ModelUtil.convertMultipleModelToJME(ResourcesPath.MODELS_PATH + "obj/vehicle.obj"));
//		chassis.generatePhysicsGeometry();
		chassis.setMaterial(Material.IRON);
		chassis.setLocalScale(CHASSIS_SCALE);
		chassis.setMass(CHASSIS_MASS);
		this.attachChild(chassis);
	}

	/**
	 * Creates the suspension.
	 */
	private void createSuspension(PhysicsSpace physicsSpace) {
		frontSuspension = new Suspension(physicsSpace, chassis, FRONT_SUSPENSION_LOCATION);
		this.attachChild(frontSuspension);
		rearSuspension = new Suspension(physicsSpace, chassis, REAR_SUSPENSION_LOCATION);
		this.attachChild(rearSuspension);
	}

	/**
	 * Accelerates the vehicle.
	 */
	public final void accelerate(float desiredVelocity) {
		if (hasHealth()) {
			rearSuspension.accelerate(desiredVelocity);
			frontSuspension.accelerate(desiredVelocity);
			if (!engineSound.isPlaying()) {
				engineSound.play();
			}
		} else {
			engineSound.stop();
		}
	}

	/**
	 * Stops the car.
	 */
	public final void stop() {
		if (hasHealth()) {
			rearSuspension.stop();
			frontSuspension.stop();
			if (!engineSound.isPlaying()) {
				engineSound.play();
			}
		} else {
			engineSound.stop();
		}
	}

	/**
	 * Verifies whether there is health available or not.
	 */
	private boolean hasHealth() {
		return health > MIN_VALUE_HEALTH;
	}

	/**
	 * Update the engine sound depending on the speed.
	 *
	 * FIXME turn up the volume is not correct
	 */
	public final void updateEngineSound() {
		engineSound.setVolume((getSpeed() / 40) + .5f);
	}

	/**
	 * Returns the current speed.
	 */
	private float getSpeed() {
		return chassis.getLinearVelocity(Vector3f.ZERO).length();
	}

	/**
	 * Steers the vehicle.
	 */
	public final void steer(float direction) {
		frontSuspension.steer(direction);
	}

	/**
	 * Unsteer the vehicle.
	 */
	public final void unsteer() {
		frontSuspension.unsteer();
	}

	/**
	 * Returns the chassis.
	 */
	public final DynamicPhysicsNode getChassis() {
		return this.chassis;
	}

//	/**
//	 * Changes the location of the vehicle.
//	 *
//	 * @param location the new location.
//	 */
//	public void changeLocation(Vector3f location) {
//		//TODO http://www.jmonkeyengine.com/jmeforum/index.php?topic=5837.msg47229#msg47229
//	}

	/**
	 * Returns the current health.
	 */
	public final int getHealth() {
		return health;
	}

	/**
	 * Decreases the health by a value that depends on the vehicle.
	 */
	public final int decreaseHealth() {
		health -= DECREASE_HEALTH_VALUE;
		if (!hasHealth()) {
			rearSuspension.stop();
			frontSuspension.stop();
			engineSound.stop();
			notifyObserversHealthEnded();
		}
		return health;
	}

	/**
	 * Recharge the health by the given value.
	 */
	public final int rechargeHealth(int healthAmount) {
		health = healthAmount;
		if (!engineSound.isPlaying()) {
			engineSound.play();
		}
		return health;
	}

	/**
	 * Loads the engine sound.
	 */
	private void loadEngineSound() {
		engineSound = AudioConfig.loadSoundEffect(ResourcesPath.AUDIO_PATH + "engine.ogg");
		engineSound.play();
	}

	/**
	 * {@inheritDoc}
	 */
	public final void addObserver(final HealthObserver observadorEnergia) {
		this.healthObservers.add(observadorEnergia);
	}

	/**
	 * {@inheritDoc}
	 */
	public final void removeObserver(HealthObserver observadorEnergia) {
		healthObservers.remove(observadorEnergia);
	}

	/**
	 * {@inheritDoc}
	 */
	public final void notifyObserversHealthEnded() {
		for (HealthObserver healthObserver : this.healthObservers) {
			healthObserver.healthEnded();
		}
	}
}