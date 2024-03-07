package ca.mcmaster.se2aa4.island.team103;

import java.util.List;

public class DistanceCalculation {
	
	// Note: params must be modified for input coordinates to handle new object's from Matt's side of things (e.g. Inlet, Site objects)
	public String returnClosestInlet(List<Coordinate> inputCoordinates, Coordinate siteCoordinates){
		double min_dist = Integer.MAX_VALUE;
		for(int i=0; i<inputCoordinates.size(); i++){
			
		}
		return "";
	}

	public double distance(Coordinate c1, Coordinate c2){
		int x_component = c2.x()-c1.x();
		int y_component = c2.y()-c1.y();
		double dist = Math.sqrt(Math.pow(x_component,2) + Math.pow(y_component,2));
		dist = Math.abs(dist);
		return dist;
	}
}
