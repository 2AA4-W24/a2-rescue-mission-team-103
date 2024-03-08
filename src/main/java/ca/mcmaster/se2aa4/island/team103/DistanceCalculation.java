package ca.mcmaster.se2aa4.island.team103;

import java.util.List;

public class DistanceCalculation {
	
	// Note: params must be modified for input coordinates to handle new object's from Matt's side of things (e.g. Inlet, Site objects)
	public PointOfIntrest returnClosestInlet(List<PointOfIntrest> inlets, PointOfIntrest site){
		double min_dist = Integer.MAX_VALUE;
		PointOfIntrest closest_inlet = new Inlet("00000000-1111-2222-3333-44444444444", new Coordinate(0,0));
		for(int i=0; i<inlets.size(); i++){
			double current_distance = distance(inlets.get(i).coord(),site.coord());
			if(current_distance < min_dist){
				min_dist = current_distance;
				closest_inlet = inlets.get(i);
			}
		}
		return closest_inlet;
	}

	public double distance(Coordinate c1, Coordinate c2){
		int x_component = c2.x()-c1.x();
		int y_component = c2.y()-c1.y();
		double dist = Math.sqrt(Math.pow(x_component,2) + Math.pow(y_component,2));
		dist = Math.abs(dist);
		return dist;
	}
}
