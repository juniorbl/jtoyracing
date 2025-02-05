package net.juniorbl.jtoyracing.entity.environment;

import javax.swing.ImageIcon;

import net.juniorbl.jtoyracing.enums.GridPosition;
import net.juniorbl.jtoyracing.enums.ResourcesPath;
import net.juniorbl.jtoyracing.util.ModelUtil;

import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingVolume;
import com.jme.image.Texture;
import com.jme.math.Vector3f;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;
import com.jmex.physics.PhysicsSpace;
import com.jmex.physics.StaticPhysicsNode;
import com.jmex.physics.material.Material;
import com.jmex.terrain.TerrainBlock;
import com.jmex.terrain.util.MidPointHeightMap;

/**
 * A room, first level of the game.
 *
 * @version 1.0 Aug 11, 2007
 * @author Carlos Luz Junior
 */
public class KidsRoom extends Node {
	private static final int ROOM_WIDTH = 162;
	private static final int ROOM_HEIGHT = 100;
	private static final int ROOM_LENGTH = 279;
	private static final long serialVersionUID = -4018480325870153949L;
	private static final Vector3f FLOOR_TEXTURE_SCALE = new Vector3f(2f, 2f, 2f);
	private static final Vector3f FLOOR_SIZE = new Vector3f(9, 0, 5.2f);
	private static final Vector3f LOCATION = new Vector3f(-66, -30, 28);
	private TerrainBlock floorTerrainBlock;
	private RaceTrack raceTrack;
	private Renderer renderer;
	private PhysicsSpace physicsSpace;

	public KidsRoom(PhysicsSpace physicsSpace, Renderer renderer) {
		this.physicsSpace = physicsSpace;
		this.renderer = renderer;
		this.setLocalTranslation(LOCATION);
		createFloor();
		createRaceTrack();
		createWalls();
		createRoomObjects();
	}

	private void createFloor() {
		createTerrainBlock();
		createFloorNode();
	}

	private void createTerrainBlock() {
		MidPointHeightMap mapHeight = new MidPointHeightMap(32, 5f);
		floorTerrainBlock = new TerrainBlock("terrain", mapHeight.getSize(),
				FLOOR_SIZE, mapHeight.getHeightMap(),
				new Vector3f(0, 0, 0), false);
		floorTerrainBlock.setModelBound(new BoundingBox());
		floorTerrainBlock.updateModelBound();
	}

	private void createFloorNode() {
		StaticPhysicsNode floorNode = physicsSpace.createStaticNode();
		floorNode.attachChild(floorTerrainBlock);
		floorNode.generatePhysicsGeometry(true);
		floorNode.setMaterial(Material.WOOD);
		this.attachChild(floorNode);
		loadFloorTexture();
	}

	private void loadFloorTexture() {
		TextureState floorTextureState = renderer.createTextureState();
		ImageIcon floorImage = new ImageIcon(
				KidsRoom.class.getClassLoader().getResource(ResourcesPath.TEXTURE_PATH + "room-floor.jpg"));
		Texture floorTexture = TextureManager.loadTexture(
				floorImage.getImage(), Texture.MM_LINEAR_LINEAR, Texture.FM_LINEAR, true);
		floorTexture.setWrap(Texture.WM_WRAP_S_WRAP_T);
		floorTexture.setScale(FLOOR_TEXTURE_SCALE);
		floorTextureState.setTexture(floorTexture);
		floorTerrainBlock.setRenderState(floorTextureState);
	}

	private void createRaceTrack() {
		raceTrack = new RaceTrack(physicsSpace, getFloorHeight());
		this.attachChild(raceTrack);
	}

	private void createWalls() {
		createLeftWall();
		createRightWall();
		createBackWall();
		createFrontWall();
	}

	private void createFrontWall() {
		Quad frontWall = new Quad("frontWall", ROOM_WIDTH, ROOM_HEIGHT);
		frontWall.setLocalTranslation(new Vector3f(278, getFloorHeight() + 50, 80));
		frontWall.setLocalRotation(ModelUtil.calculateRotation(270));
		String frontWallTexture = "front-wall-texture";
		loadWallTexture(frontWall, frontWallTexture);
		this.attachChild(frontWall);
	}

	private void createBackWall() {
		Quad backWall = new Quad("backWall", ROOM_WIDTH, ROOM_HEIGHT);
		backWall.setLocalTranslation(new Vector3f(0, getFloorHeight() + 50, 81));
		backWall.setLocalRotation(ModelUtil.calculateRotation(90));
		String backWallTexture = "back-wall-texture";
		loadWallTexture(backWall, backWallTexture);
		this.attachChild(backWall);
	}

	private void createRightWall() {
		Quad rightWall = new Quad("rightWall", ROOM_LENGTH, ROOM_HEIGHT);
		rightWall.setLocalTranslation(new Vector3f(139, getFloorHeight() + 50, 161));
		rightWall.setLocalRotation(ModelUtil.calculateRotation(180));
		String rightWallTexture = "right-wall-texture";
		loadWallTexture(rightWall, rightWallTexture);
		this.attachChild(rightWall);
	}

	private void createLeftWall() {
		Quad leftWall = new Quad("leftWall", ROOM_LENGTH, ROOM_HEIGHT);
		leftWall.setLocalTranslation(new Vector3f(139, getFloorHeight() + 50, 0));
		String leftWallTexture = "left-wall-texture";
		loadWallTexture(leftWall, leftWallTexture);
		this.attachChild(leftWall);
	}

	private void loadWallTexture(Quad wall, String textureName) {
		TextureState wallTextureState = renderer.createTextureState();
		ImageIcon wallImage = new ImageIcon(
				KidsRoom.class.getClassLoader().getResource(ResourcesPath.TEXTURE_PATH + textureName + ".jpg"));
		Texture wallTexture = TextureManager.loadTexture(
				wallImage.getImage(), Texture.MM_NONE, Texture.MM_NONE, true);
		wallTextureState.setTexture(wallTexture);
		wall.setRenderState(wallTextureState);
	}

	private void createRoomObjects() {
		createBed();
		createDesk();
		createComputer();
		createChair();
		createLegoDoll();
		createTV();
		createTable();
		createShelf();
	}

	public final boolean isVehicleReachedCheckpoint(BoundingVolume vehicleBoundingVolume) {
		return raceTrack.isVehicleReachedCheckpoint(vehicleBoundingVolume);
	}

	public final float getFloorHeight() {
		return floorTerrainBlock.getHeight(new Vector3f());
	}

	private void createLegoDoll() {
		final float legoDollScale = 2.5f;
		StaticPhysicsNode legoDoll = loadObjectModel(
				new Vector3f(200, getFloorHeight() + 3.9f, 30), "obj/legoDoll.obj", legoDollScale);
		this.attachChild(legoDoll);
	}

	private void createShelf() {
		final float shelfScale = 7;
		StaticPhysicsNode shelf = loadObjectModel(
				new Vector3f(5, getFloorHeight() + 35, 80), "obj/shelf.obj", shelfScale);
		this.attachChild(shelf);
	}

	private void createBed() {
		final float bedScale = 0.4f;
		StaticPhysicsNode bed = loadObjectModel(
				new Vector3f(235, getFloorHeight(), 130), "obj/bed.obj", bedScale);
		this.attachChild(bed);
	}

	private void createDesk() {
		final float deskScale = 19;
		StaticPhysicsNode desk = loadObjectModel(
				new Vector3f(80, getFloorHeight(), 20.5f), "obj/desk.obj", deskScale);
		this.attachChild(desk);
	}

	private void createChair() {
		final float chairScale = 17;
		StaticPhysicsNode chair = loadObjectModel(
				new Vector3f(80, getFloorHeight(), 50), "obj/chair.obj", chairScale);
		chair.setLocalRotation(ModelUtil.calculateRotation(90));
		this.attachChild(chair);
	}

	private void createComputer() {
		final float computerScale = 1.5f;
		StaticPhysicsNode computer = loadObjectModel(
				new Vector3f(80, getFloorHeight() + 24, 40), "obj/computer.obj", computerScale);
		this.attachChild(computer);
	}

	private void createTV() {
		final float tvScale = 5;
		StaticPhysicsNode tv = loadObjectModel(
				new Vector3f(230, getFloorHeight() + 7, 10), "obj/TV.obj", tvScale);
		this.attachChild(tv);
	}

	private void createTable() {
		final float tableScale = 10;
		StaticPhysicsNode table = loadObjectModel(
				new Vector3f(230, getFloorHeight(), 15), "obj/table.obj", tableScale);
		this.attachChild(table);
	}

	private StaticPhysicsNode loadObjectModel(Vector3f objectTranslation, String modelPath, Float objectScale) {
		StaticPhysicsNode object = physicsSpace.createStaticNode();
		object.setLocalTranslation(objectTranslation);
		object.setLocalScale(objectScale);
		object.attachChild(ModelUtil.convertOBJToStatial(ResourcesPath.MODELS_PATH + modelPath));
		return object;
	}

	public final Vector3f getGridPosition(GridPosition position) {
		return raceTrack.getGridPosition(position, getFloorHeight());
	}
}
