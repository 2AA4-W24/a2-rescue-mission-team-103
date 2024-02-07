package ca.mcmaster.se2aa4.island.team103.Storage;

enum Biome{
	NONE,
	OCEAN,
	DESERT,
	EMERGENCYSITE
}


public class Tile {
	private int x;
	private int y;
	private Biome biome;

	public Tile(int xCoord, int yCoord, Biome tileBiome){
		x = xCoord;
		y = yCoord;
		biome = tileBiome;
	}

	public int getX(){
		return this.x;
	}

	public int getY(){
		return this.y;
	}

	public Biome getBiome(){
		return this.biome;
	}
}
