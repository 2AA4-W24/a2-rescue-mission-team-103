package ca.mcmaster.se2aa4.island.team103;

import org.junit.jupiter.api.Test;

import ca.mcmaster.se2aa4.island.team103.siteTracking.*;
import ca.mcmaster.se2aa4.island.team103.history.Coordinate;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

// Testing of distance calculations
class DistanceTest {
	
	@Test
	void distanceCalc() {
		double expected = 10.0;
		DistanceCalculation calculator = new DistanceCalculation();
		Coordinate c1 = new Coordinate(0,0);
		Coordinate c2 = new Coordinate(6,8);
		double result = calculator.distance(c1,c2);
		assertEquals(result,expected);
	}

	@Test
	void inletCalc() {
		PointOfInterest i1 = new Inlet("12345",new Coordinate(0,0));
		PointOfInterest i2 = new Inlet("23456",new Coordinate(10,10));
		List<PointOfInterest> inlets = new ArrayList<PointOfInterest>();
		inlets.add(i1);
		inlets.add(i2);
		PointOfInterest s1 = new Site("54321",new Coordinate(1,1));
		DistanceCalculation calculator = new DistanceCalculation();
		PointOfInterest result = calculator.returnClosestInlet(inlets,s1);
		String s = s1.id();
		assertEquals(i1.id(),result.id());
	}

}
