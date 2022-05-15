package game.region;

public class Regions {
	
	public Region debug, test_house;
	
	/**create all regions**/
	public Regions() {
		debug = new Region("Debug", "/tile/debug.xml");
		test_house = new Region("Test House", "/tile/test_house.xml");
	}
	
	public class Region {
		
		//map name and file path
		private String name, path;
		
		//map dimension in the tile map. -1 indicates the entire tile map
		private int x = -1, y = -1, w = -1, h = -1;
		
		/**regions that take up the entire tile map (usually towns)**/
		public Region(String name, String path) {
			this.name = name;
			this.path = path;
		}
		
		/**regions that only take up a portion of the tile map (usually multi-room homes or dungeons)**/
		public Region(String name, String path, int x, int y, int w, int h) {
			this.name = name;
			this.path = path;
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
		}
		
		//name and path getters
		public String getName() { return name; }
		public String getPath() { return path; }
		
		//map dimension (in tiles) getters
		public int getX() { return x; }
		public int getY() { return y; }
		public int getW() { return w; }
		public int getH() { return h; }
	}
}
