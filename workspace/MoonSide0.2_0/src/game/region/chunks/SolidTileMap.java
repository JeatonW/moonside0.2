package game.region.chunks;

import game.graphics.Graphics;
import game.region.Position;

/**a map containing coordinates of all solid blocks for collision detection**/
public class SolidTileMap {

	//a list of all solid tiles
	public TileList list;
	
	/**create a tilelist of every solid tile position on the map**/
	public SolidTileMap(String data, int tilesX, int tilesY, int posx, int posy, int width, int height) {

		//create tile list
		list = new TileList();
		
		//format tile data
		String[] tileObj = data.split(",");
		
		//check every tile
		for(int y=posy; y<posy+height; y++) {
			for(int x=posx; x<posx+width; x++) {
				
				int i = y*tilesX+x;
				
				//get coordinates of current tile
				int temp = Integer.parseInt(tileObj[i].replaceAll("\\s+",""));
				
				//if the tile is solid, insert
				if(temp != 0) { list.insert((int) (i % tilesX) - posx, (int) (Math.floor(i / tilesX)) - posy, temp); }
			}
		}
	}
	
	/**see if a tile is in the tile list**/
	public int search(int x, int y) { return list.search(x, y); }
	
	/**render every solid tile hitbox thats close to the player**/
	public void render(Position playerPos) {
		
		//get player pos in tiles
		int posX = (int) (playerPos.x / ChunkManager.tileWidth);
		int posY = (int) (playerPos.y / ChunkManager.tileHeight);
		
		//left and top most tile hitboxes being rendered
		posX -= 5;
		posY -= 4;
		posX *= ChunkManager.tileWidth;
		posY *= ChunkManager.tileHeight;
		
		//render all solid tile hitboxes around the player
		for(int y=posY; y<posY+(15*ChunkManager.tileHeight); y+=8) {
			for(int x=posX; x<posX+(15*ChunkManager.tileWidth); x+=8) {
				if(list.search((x/8), (y/8)) != 0) {
					Graphics.setColor("red");
					Graphics.drawRect(x-Position.screenX, y-Position.screenY, ChunkManager.tileWidth, ChunkManager.tileHeight);
					Graphics.resetColor();
				}
			}
		}
	}
}

/**a data structure meant to reduce the time it takes to search for a coordinate in the solid tile map**/
class TileList {
	
	private Node head = null;
	
	/**insert coordinates of a solid tile**/
	public void insert(int x, int y, int type) {
		insertX(insertY(y, type), x, type);
	}

	/**insertX helper (find y node)**/
	private void insertX(int y, int data, int type) {
		
		//find y node
		Node yNode = head;
		for(int i=0; i<y; i++) { yNode = yNode.getNext(); }
		
		//if y node has no side yet, create side and return
		if(yNode.getSide() == null) {
			yNode.setSide(new Node(data, type));
			return;
		}
		
		//get first side node
		Node curNode = yNode.getSide();
		
		//if data is less than first side node, replace first side node with new node
		if(curNode.getData() > data) {
			Node temp = new Node(data, type);
			temp.setSide(curNode);
			yNode.setSide(temp);
			return;
		}
		
		//recursively insert x node
		insertX(curNode, data, type);
	}
	
	/**traverse down x branch and insert at correct position**/
	private void insertX(Node curNode, int data, int type) {
		
		//if at the end of the list, create new node at end
		if(curNode.getSide() == null) {
			curNode.setSide(new Node(data, type));
			return;
		}
		
		//if side data equals insert data, dont need to create new node. just return
		if(curNode.getSide().getData() == data) {
			return;
		}
		
		//if data is greater than side, go next
		else if(curNode.getSide().getData() < data) {
			insertX(curNode.getSide(), data, type);
		}
		
		//if data is less than side, create new node at side position
		else {
			Node prev = curNode;
			Node next = curNode.getSide();
			Node insertNode = new Node(data, type);
			prev.setSide(insertNode);
			insertNode.setSide(next);
			return;
		}
	}
	
	/**insert y helper**/
	private int insertY(int data, int type) {
		
		//create a new head if head is null
		if(head == null) {
			head = new Node(data, type);
			return 0;
		}
		
		//if y node is already created, dont need to create new node. just return
		if(head.getData() == data) { return 0; }
		
		//if data is less than head, create new head
		if(head.getData() > data) {
			Node temp = new Node(data, type);
			temp.setNext(head);
			head = temp;
			return 0;
		}
		
		//recursively insert y node
		return insertY(head, data, type, 1);
	}
	
	/**traverse down y branch and insert at correct position**/
	private int insertY(Node curNode, int data, int type, int index) {
		
		//if at the end of the list, create new node at end
		if(curNode.getNext() == null) {
			curNode.setNext(new Node(data, type));
			return index;
		}
		
		//if next data equals insert data, dont need to create new node. just return
		if(curNode.getNext().getData() == data) { return index; }
		
		//if data is greater than next, go to next
		else if(curNode.getNext().getData() < data) { return insertY(curNode.getNext(), data, type, index+1); }
		
		//if data is less than next, create new node at next position
		else {
			Node prev = curNode;
			Node next = curNode.getNext();
			Node insertNode = new Node(data, type);
			prev.setNext(insertNode);
			insertNode.setNext(next);
			return index;
		}
	}
	
	/**search for a coordinate; returns true if found**/
	public int search(int x, int y) {
		
		//start at y head
		Node yNode = head;
		Node xNode;
		
		if(yNode.getData() > y) { return 0; }
		
		//traverse down y list
		while(yNode != null && yNode.getData() <= y) {
			
			//if y coordinate was found, traverse down x list
			if(yNode.getData() == y) {
				xNode = yNode.getSide();
				while(xNode != null && xNode.getData() <= x) {
					
					//if x coordinate was found, return true;
					if(xNode.getData() == x) { return xNode.getType(); }
					
					xNode = xNode.getSide();
				}
			}
			yNode = yNode.getNext();
		}
		
		//if never found, return false
		return 0;
	}
	
	/**print out all the coordinates**/
	public void printAll() {
		
		//start at y head
		Node yNode = head;
		Node xNode;
		
		//do every y node
		while(yNode != null) {
			System.out.print(yNode.getData() + ":  ");
			
			//do every x node
			xNode = yNode.getSide();
			while(xNode != null) {
				
				//print the x and y coordinates together
				System.out.print(xNode.getData());
				if(xNode.getSide() != null) { System.out.print(", "); }
				else { System.out.println(); }
				
				xNode = xNode.getSide();
			}
			yNode = yNode.getNext();
		}
	}
	
	/**print out all the types**/
	public void printAllTypes() {
		
		//start at y head
		Node yNode = head;
		Node xNode;
		
		//do every y node
		while(yNode != null) {
			
			//do every x node
			xNode = yNode.getSide();
			while(xNode != null) {
				
				//print the x and y coordinates together
				System.out.print(xNode.getType());
				if(xNode.getSide() != null) { System.out.print(", "); }
				else { System.out.println(); }
				
				xNode = xNode.getSide();
			}
			yNode = yNode.getNext();
		}
	}
}

/** a node can either have a pointer to the next (down) node, or to the side (right) node.
    y coordinates represent the "next" trail, and x coordinates represent the "side" trail. **/
class Node {
	
	private Node next = null;
	private Node side = null;
	private int data;
	private int type;
	
	public Node(int data, int type) { this.data = data; this.type = type; }
	
	public Node getNext() { return next; }
	public Node getSide() { return side; }
	public int getData() { return data; }
	public int getType() { return type; }
	
	public void setNext(Node next) { this.next = next; }
	public void setSide(Node side) { this.side = side; }
}
