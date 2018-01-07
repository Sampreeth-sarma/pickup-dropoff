public class World {

	boolean east,west,north,south,pickup,dropoff;
	String actions[] = { " ", "east", "west", "north", "south", "pickup", "dropoff" };

	public World(){
		east =true;
		west = true;
		north = true;
		south = true;
		pickup = false;
		dropoff = false;
	}
}